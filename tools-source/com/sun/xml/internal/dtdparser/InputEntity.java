package com.sun.xml.internal.dtdparser;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class InputEntity {
   private int start;
   private int finish;
   private char[] buf;
   private int lineNumber = 1;
   private boolean returnedFirstHalf = false;
   private boolean maybeInCRLF = false;
   private String name;
   private InputEntity next;
   private InputSource input;
   private Reader reader;
   private boolean isClosed;
   private DTDEventListener errHandler;
   private Locale locale;
   private StringBuffer rememberedText;
   private int startRemember;
   private boolean isPE;
   private static final int BUFSIZ = 8193;
   private static final char[] newline = new char[]{'\n'};

   public static InputEntity getInputEntity(DTDEventListener h, Locale l) {
      InputEntity retval = new InputEntity();
      retval.errHandler = h;
      retval.locale = l;
      return retval;
   }

   private InputEntity() {
   }

   public boolean isInternal() {
      return this.reader == null;
   }

   public boolean isDocument() {
      return this.next == null;
   }

   public boolean isParameterEntity() {
      return this.isPE;
   }

   public String getName() {
      return this.name;
   }

   public void init(InputSource in, String name, InputEntity stack, boolean isPE) throws IOException, SAXException {
      this.input = in;
      this.isPE = isPE;
      this.reader = in.getCharacterStream();
      if (this.reader == null) {
         InputStream bytes = in.getByteStream();
         if (bytes == null) {
            this.reader = XmlReader.createReader((new URL(in.getSystemId())).openStream());
         } else if (in.getEncoding() != null) {
            this.reader = XmlReader.createReader(in.getByteStream(), in.getEncoding());
         } else {
            this.reader = XmlReader.createReader(in.getByteStream());
         }
      }

      this.next = stack;
      this.buf = new char[8193];
      this.name = name;
      this.checkRecursion(stack);
   }

   public void init(char[] b, String name, InputEntity stack, boolean isPE) throws SAXException {
      this.next = stack;
      this.buf = b;
      this.finish = b.length;
      this.name = name;
      this.isPE = isPE;
      this.checkRecursion(stack);
   }

   private void checkRecursion(InputEntity stack) throws SAXException {
      if (stack != null) {
         for(stack = stack.next; stack != null; stack = stack.next) {
            if (stack.name != null && stack.name.equals(this.name)) {
               this.fatal("P-069", new Object[]{this.name});
            }
         }

      }
   }

   public InputEntity pop() throws IOException {
      this.close();
      return this.next;
   }

   public boolean isEOF() throws IOException, SAXException {
      if (this.start >= this.finish) {
         this.fillbuf();
         return this.start >= this.finish;
      } else {
         return false;
      }
   }

   public String getEncoding() {
      if (this.reader == null) {
         return null;
      } else if (this.reader instanceof XmlReader) {
         return ((XmlReader)this.reader).getEncoding();
      } else {
         return this.reader instanceof InputStreamReader ? ((InputStreamReader)this.reader).getEncoding() : null;
      }
   }

   public char getNameChar() throws IOException, SAXException {
      if (this.finish <= this.start) {
         this.fillbuf();
      }

      if (this.finish > this.start) {
         char c = this.buf[this.start++];
         if (XmlChars.isNameChar(c)) {
            return c;
         }

         --this.start;
      }

      return '\u0000';
   }

   public char getc() throws IOException, SAXException {
      if (this.finish <= this.start) {
         this.fillbuf();
      }

      if (this.finish > this.start) {
         char c = this.buf[this.start++];
         if (this.returnedFirstHalf) {
            if (c >= '\udc00' && c <= '\udfff') {
               this.returnedFirstHalf = false;
               return c;
            }

            this.fatal("P-070", new Object[]{Integer.toHexString(c)});
         }

         if (c >= ' ' && c <= '\ud7ff' || c == '\t' || c >= '\ue000' && c <= '�') {
            return c;
         }

         if (c == '\r' && !this.isInternal()) {
            this.maybeInCRLF = true;
            c = this.getc();
            if (c != '\n') {
               this.ungetc();
            }

            this.maybeInCRLF = false;
            ++this.lineNumber;
            return '\n';
         }

         if (c == '\n' || c == '\r') {
            if (!this.isInternal() && !this.maybeInCRLF) {
               ++this.lineNumber;
            }

            return c;
         }

         if (c >= '\ud800' && c < '\udc00') {
            this.returnedFirstHalf = true;
            return c;
         }

         this.fatal("P-071", new Object[]{Integer.toHexString(c)});
      }

      throw new EndOfInputException();
   }

   public boolean peekc(char c) throws IOException, SAXException {
      if (this.finish <= this.start) {
         this.fillbuf();
      }

      if (this.finish > this.start) {
         if (this.buf[this.start] == c) {
            ++this.start;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void ungetc() {
      if (this.start == 0) {
         throw new InternalError("ungetc");
      } else {
         --this.start;
         if (this.buf[this.start] != '\n' && this.buf[this.start] != '\r') {
            if (this.returnedFirstHalf) {
               this.returnedFirstHalf = false;
            }
         } else if (!this.isInternal()) {
            --this.lineNumber;
         }

      }
   }

   public boolean maybeWhitespace() throws IOException, SAXException {
      boolean isSpace = false;
      boolean sawCR = false;

      while(true) {
         char c;
         do {
            do {
               if (this.finish <= this.start) {
                  this.fillbuf();
               }

               if (this.finish <= this.start) {
                  return isSpace;
               }

               c = this.buf[this.start++];
               if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                  --this.start;
                  return isSpace;
               }

               isSpace = true;
            } while(c != '\n' && c != '\r');
         } while(this.isInternal());

         if (c != '\n' || !sawCR) {
            ++this.lineNumber;
            sawCR = false;
         }

         if (c == '\r') {
            sawCR = true;
         }
      }
   }

   public boolean parsedContent(DTDEventListener docHandler) throws IOException, SAXException {
      int last;
      int first = last = this.start;
      boolean sawContent = false;

      while(true) {
         if (last >= this.finish) {
            if (last > first) {
               docHandler.characters(this.buf, first, last - first);
               sawContent = true;
               this.start = last;
            }

            if (this.isEOF()) {
               return sawContent;
            }

            first = this.start;
            last = first - 1;
         } else {
            char c = this.buf[last];
            if ((c <= ']' || c > '\ud7ff') && (c >= '&' || c < ' ') && (c <= '<' || c >= ']') && (c <= '&' || c >= '<') && c != '\t' && (c < '\ue000' || c > '�')) {
               if (c == '<' || c == '&') {
                  break;
               }

               if (c == '\n') {
                  if (!this.isInternal()) {
                     ++this.lineNumber;
                  }
               } else if (c == '\r') {
                  if (!this.isInternal()) {
                     docHandler.characters(this.buf, first, last - first);
                     docHandler.characters(newline, 0, 1);
                     sawContent = true;
                     ++this.lineNumber;
                     if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                        ++last;
                     }

                     first = this.start = last + 1;
                  }
               } else if (c == ']') {
                  switch (this.finish - last) {
                     case 2:
                        if (this.buf[last + 1] != ']') {
                           break;
                        }
                     case 1:
                        if (this.reader != null && !this.isClosed) {
                           if (last == first) {
                              throw new InternalError("fillbuf");
                           }

                           --last;
                           if (last > first) {
                              docHandler.characters(this.buf, first, last - first);
                              sawContent = true;
                              this.start = last;
                           }

                           this.fillbuf();
                           first = last = this.start;
                        }
                        break;
                     default:
                        if (this.buf[last + 1] == ']' && this.buf[last + 2] == '>') {
                           this.fatal("P-072", (Object[])null);
                        }
                  }
               } else if (c >= '\ud800' && c <= '\udfff') {
                  if (last + 1 >= this.finish) {
                     if (last > first) {
                        docHandler.characters(this.buf, first, last - first);
                        sawContent = true;
                        this.start = last + 1;
                     }

                     if (this.isEOF()) {
                        this.fatal("P-081", new Object[]{Integer.toHexString(c)});
                     }

                     first = this.start;
                     last = first;
                  } else {
                     if (!this.checkSurrogatePair(last)) {
                        --last;
                        break;
                     }

                     ++last;
                  }
               } else {
                  this.fatal("P-071", new Object[]{Integer.toHexString(c)});
               }
            }
         }

         ++last;
      }

      if (last == first) {
         return sawContent;
      } else {
         docHandler.characters(this.buf, first, last - first);
         this.start = last;
         return true;
      }
   }

   public boolean unparsedContent(DTDEventListener docHandler, boolean ignorableWhitespace, String whitespaceInvalidMessage) throws IOException, SAXException {
      if (!this.peek("![CDATA[", (char[])null)) {
         return false;
      } else {
         docHandler.startCDATA();

         while(true) {
            boolean done = false;
            boolean white = ignorableWhitespace;

            int last;
            for(last = this.start; last < this.finish; ++last) {
               char c = this.buf[last];
               if (!XmlChars.isChar(c)) {
                  white = false;
                  if (c >= '\ud800' && c <= '\udfff') {
                     if (!this.checkSurrogatePair(last)) {
                        --last;
                        break;
                     }

                     ++last;
                     continue;
                  }

                  this.fatal("P-071", new Object[]{Integer.toHexString(this.buf[last])});
               }

               if (c == '\n') {
                  if (!this.isInternal()) {
                     ++this.lineNumber;
                  }
               } else if (c == '\r') {
                  if (!this.isInternal()) {
                     if (white) {
                        if (whitespaceInvalidMessage != null) {
                           this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), (Locator)null));
                        }

                        docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
                        docHandler.ignorableWhitespace(newline, 0, 1);
                     } else {
                        docHandler.characters(this.buf, this.start, last - this.start);
                        docHandler.characters(newline, 0, 1);
                     }

                     ++this.lineNumber;
                     if (this.finish > last + 1 && this.buf[last + 1] == '\n') {
                        ++last;
                     }

                     this.start = last + 1;
                  }
               } else if (c != ']') {
                  if (c != ' ' && c != '\t') {
                     white = false;
                  }
               } else {
                  if (last + 2 >= this.finish) {
                     break;
                  }

                  if (this.buf[last + 1] == ']' && this.buf[last + 2] == '>') {
                     done = true;
                     break;
                  }

                  white = false;
               }
            }

            if (white) {
               if (whitespaceInvalidMessage != null) {
                  this.errHandler.error(new SAXParseException(DTDParser.messages.getMessage(this.locale, whitespaceInvalidMessage), (Locator)null));
               }

               docHandler.ignorableWhitespace(this.buf, this.start, last - this.start);
            } else {
               docHandler.characters(this.buf, this.start, last - this.start);
            }

            if (done) {
               this.start = last + 3;
               docHandler.endCDATA();
               return true;
            }

            this.start = last;
            if (this.isEOF()) {
               this.fatal("P-073", (Object[])null);
            }
         }
      }
   }

   private boolean checkSurrogatePair(int offset) throws SAXException {
      if (offset + 1 >= this.finish) {
         return false;
      } else {
         char c1 = this.buf[offset++];
         char c2 = this.buf[offset];
         if (c1 >= '\ud800' && c1 < '\udc00' && c2 >= '\udc00' && c2 <= '\udfff') {
            return true;
         } else {
            this.fatal("P-074", new Object[]{Integer.toHexString(c1 & '\uffff'), Integer.toHexString(c2 & '\uffff')});
            return false;
         }
      }
   }

   public boolean ignorableWhitespace(DTDEventListener handler) throws IOException, SAXException {
      boolean isSpace = false;
      int first = this.start;

      while(true) {
         if (this.finish <= this.start) {
            if (isSpace) {
               handler.ignorableWhitespace(this.buf, first, this.start - first);
            }

            this.fillbuf();
            first = this.start;
         }

         if (this.finish <= this.start) {
            return isSpace;
         }

         char c = this.buf[this.start++];
         switch (c) {
            case '\n':
               if (!this.isInternal()) {
                  ++this.lineNumber;
               }
            case '\t':
            case ' ':
               isSpace = true;
               break;
            case '\r':
               isSpace = true;
               if (!this.isInternal()) {
                  ++this.lineNumber;
               }

               handler.ignorableWhitespace(this.buf, first, this.start - 1 - first);
               handler.ignorableWhitespace(newline, 0, 1);
               if (this.start < this.finish && this.buf[this.start] == '\n') {
                  ++this.start;
               }

               first = this.start;
               break;
            default:
               this.ungetc();
               if (isSpace) {
                  handler.ignorableWhitespace(this.buf, first, this.start - first);
               }

               return isSpace;
         }
      }
   }

   public boolean peek(String next, char[] chars) throws IOException, SAXException {
      int len;
      if (chars != null) {
         len = chars.length;
      } else {
         len = next.length();
      }

      if (this.finish <= this.start || this.finish - this.start < len) {
         this.fillbuf();
      }

      if (this.finish <= this.start) {
         return false;
      } else {
         int i;
         if (chars != null) {
            for(i = 0; i < len && this.start + i < this.finish; ++i) {
               if (this.buf[this.start + i] != chars[i]) {
                  return false;
               }
            }
         } else {
            for(i = 0; i < len && this.start + i < this.finish; ++i) {
               if (this.buf[this.start + i] != next.charAt(i)) {
                  return false;
               }
            }
         }

         if (i < len) {
            if (this.reader != null && !this.isClosed) {
               if (len > this.buf.length) {
                  this.fatal("P-077", new Object[]{new Integer(this.buf.length)});
               }

               this.fillbuf();
               return this.peek(next, chars);
            } else {
               return false;
            }
         } else {
            this.start += len;
            return true;
         }
      }
   }

   public void startRemembering() {
      if (this.startRemember != 0) {
         throw new InternalError();
      } else {
         this.startRemember = this.start;
      }
   }

   public String rememberText() {
      String retval;
      if (this.rememberedText != null) {
         this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
         retval = this.rememberedText.toString();
      } else {
         retval = new String(this.buf, this.startRemember, this.start - this.startRemember);
      }

      this.startRemember = 0;
      this.rememberedText = null;
      return retval;
   }

   private InputEntity getTopEntity() {
      InputEntity current;
      for(current = this; current != null && current.input == null; current = current.next) {
      }

      return current == null ? this : current;
   }

   public String getPublicId() {
      InputEntity where = this.getTopEntity();
      return where == this ? this.input.getPublicId() : where.getPublicId();
   }

   public String getSystemId() {
      InputEntity where = this.getTopEntity();
      return where == this ? this.input.getSystemId() : where.getSystemId();
   }

   public int getLineNumber() {
      InputEntity where = this.getTopEntity();
      return where == this ? this.lineNumber : where.getLineNumber();
   }

   public int getColumnNumber() {
      return -1;
   }

   private void fillbuf() throws IOException, SAXException {
      if (this.reader != null && !this.isClosed) {
         if (this.startRemember != 0) {
            if (this.rememberedText == null) {
               this.rememberedText = new StringBuffer(this.buf.length);
            }

            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
         }

         boolean extra = this.finish > 0 && this.start > 0;
         if (extra) {
            --this.start;
         }

         int len = this.finish - this.start;
         System.arraycopy(this.buf, this.start, this.buf, 0, len);
         this.start = 0;
         this.finish = len;

         try {
            len = this.buf.length - len;
            len = this.reader.read(this.buf, this.finish, len);
         } catch (UnsupportedEncodingException var4) {
            this.fatal("P-075", new Object[]{var4.getMessage()});
         } catch (CharConversionException var5) {
            this.fatal("P-076", new Object[]{var5.getMessage()});
         }

         if (len >= 0) {
            this.finish += len;
         } else {
            this.close();
         }

         if (extra) {
            ++this.start;
         }

         if (this.startRemember != 0) {
            this.startRemember = 1;
         }

      }
   }

   public void close() {
      try {
         if (this.reader != null && !this.isClosed) {
            this.reader.close();
         }

         this.isClosed = true;
      } catch (IOException var2) {
      }

   }

   private void fatal(String messageId, Object[] params) throws SAXException {
      SAXParseException x = new SAXParseException(DTDParser.messages.getMessage(this.locale, messageId, params), (Locator)null);
      this.close();
      this.errHandler.fatalError(x);
      throw x;
   }
}
