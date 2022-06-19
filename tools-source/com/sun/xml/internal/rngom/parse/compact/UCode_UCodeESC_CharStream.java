package com.sun.xml.internal.rngom.parse.compact;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.util.Utf16;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class UCode_UCodeESC_CharStream {
   public static final boolean staticFlag = false;
   public int bufpos;
   int bufsize;
   int available;
   int tokenBegin;
   private int[] bufline;
   private int[] bufcolumn;
   private int column;
   private int line;
   private Reader inputStream;
   private boolean closed;
   private boolean prevCharIsLF;
   private char[] nextCharBuf;
   private char[] buffer;
   private int maxNextCharInd;
   private int nextCharInd;
   private int inBuf;
   private static final char NEWLINE_MARKER = '\u0000';
   private static final char BOM = '\ufeff';

   static final int hexval(char c) {
      switch (c) {
         case '0':
            return 0;
         case '1':
            return 1;
         case '2':
            return 2;
         case '3':
            return 3;
         case '4':
            return 4;
         case '5':
            return 5;
         case '6':
            return 6;
         case '7':
            return 7;
         case '8':
            return 8;
         case '9':
            return 9;
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         default:
            return -1;
         case 'A':
         case 'a':
            return 10;
         case 'B':
         case 'b':
            return 11;
         case 'C':
         case 'c':
            return 12;
         case 'D':
         case 'd':
            return 13;
         case 'E':
         case 'e':
            return 14;
         case 'F':
         case 'f':
            return 15;
      }
   }

   private final void ExpandBuff(boolean wrapAround) {
      char[] newbuffer = new char[this.bufsize + 2048];
      int[] newbufline = new int[this.bufsize + 2048];
      int[] newbufcolumn = new int[this.bufsize + 2048];
      if (wrapAround) {
         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
         System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
         this.buffer = newbuffer;
         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
         System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
         this.bufline = newbufline;
         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
         System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
         this.bufcolumn = newbufcolumn;
         this.bufpos += this.bufsize - this.tokenBegin;
      } else {
         System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
         this.buffer = newbuffer;
         System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
         this.bufline = newbufline;
         System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
         this.bufcolumn = newbufcolumn;
         this.bufpos -= this.tokenBegin;
      }

      this.available = this.bufsize += 2048;
      this.tokenBegin = 0;
   }

   private final void FillBuff() throws EOFException {
      if (this.maxNextCharInd == 4096) {
         this.maxNextCharInd = this.nextCharInd = 0;
      }

      if (this.closed) {
         throw new EOFException();
      } else {
         try {
            int i;
            if ((i = this.inputStream.read(this.nextCharBuf, this.maxNextCharInd, 4096 - this.maxNextCharInd)) == -1) {
               this.closed = true;
               this.inputStream.close();
               throw new EOFException();
            } else {
               this.maxNextCharInd += i;
            }
         } catch (IOException var3) {
            throw new BuildException(var3);
         }
      }
   }

   private final char ReadChar() throws EOFException {
      if (++this.nextCharInd >= this.maxNextCharInd) {
         this.FillBuff();
      }

      return this.nextCharBuf[this.nextCharInd];
   }

   private final char PeekChar() throws EOFException {
      char c = this.ReadChar();
      --this.nextCharInd;
      return c;
   }

   public final char BeginToken() throws EOFException {
      if (this.inBuf > 0) {
         --this.inBuf;
         return this.buffer[this.tokenBegin = this.bufpos == this.bufsize - 1 ? (this.bufpos = 0) : ++this.bufpos];
      } else {
         this.tokenBegin = 0;
         this.bufpos = -1;
         return this.readChar();
      }
   }

   private final void AdjustBuffSize() {
      if (this.available == this.bufsize) {
         if (this.tokenBegin > 2048) {
            this.bufpos = 0;
            this.available = this.tokenBegin;
         } else {
            this.ExpandBuff(false);
         }
      } else if (this.available > this.tokenBegin) {
         this.available = this.bufsize;
      } else if (this.tokenBegin - this.available < 2048) {
         this.ExpandBuff(true);
      } else {
         this.available = this.tokenBegin;
      }

   }

   private final void UpdateLineColumn(char c) {
      ++this.column;
      if (this.prevCharIsLF) {
         this.prevCharIsLF = false;
         this.line += this.column = 1;
      }

      switch (c) {
         case '\u0000':
            this.prevCharIsLF = true;
            break;
         case '\t':
            --this.column;
            this.column += 8 - (this.column & 7);
      }

      this.bufline[this.bufpos] = this.line;
      this.bufcolumn[this.bufpos] = this.column;
   }

   public final char readChar() throws EOFException {
      if (this.inBuf > 0) {
         --this.inBuf;
         return this.buffer[this.bufpos == this.bufsize - 1 ? (this.bufpos = 0) : ++this.bufpos];
      } else {
         char c;
         try {
            c = this.ReadChar();
            switch (c) {
               case '\t':
                  break;
               case '\n':
                  c = 0;
                  break;
               case '\r':
                  c = 0;

                  try {
                     if (this.PeekChar() == '\n') {
                        this.ReadChar();
                     }
                  } catch (EOFException var7) {
                  }
                  break;
               default:
                  if (c >= ' ') {
                     if (Utf16.isSurrogate(c)) {
                        if (Utf16.isSurrogate2(c)) {
                           throw new EscapeSyntaxException("illegal_surrogate_pair", this.line, this.column + 1);
                        }

                        if (++this.bufpos == this.available) {
                           this.AdjustBuffSize();
                        }

                        this.buffer[this.bufpos] = c;

                        try {
                           c = this.ReadChar();
                        } catch (EOFException var6) {
                           throw new EscapeSyntaxException("illegal_surrogate_pair", this.line, this.column + 1);
                        }

                        if (!Utf16.isSurrogate2(c)) {
                           throw new EscapeSyntaxException("illegal_surrogate_pair", this.line, this.column + 2);
                        }
                     }
                     break;
                  }
               case '\ufffe':
               case '\uffff':
                  throw new EscapeSyntaxException("illegal_char_code", this.line, this.column + 1);
            }
         } catch (EOFException var10) {
            if (this.bufpos == -1) {
               if (++this.bufpos == this.available) {
                  this.AdjustBuffSize();
               }

               this.bufline[this.bufpos] = this.line;
               this.bufcolumn[this.bufpos] = this.column;
            }

            throw var10;
         }

         if (++this.bufpos == this.available) {
            this.AdjustBuffSize();
         }

         this.buffer[this.bufpos] = c;
         this.UpdateLineColumn(c);

         try {
            if (c != '\\' || this.PeekChar() != 'x') {
               return c;
            }
         } catch (EOFException var9) {
            return c;
         }

         int xCnt = 1;

         while(true) {
            this.ReadChar();
            if (++this.bufpos == this.available) {
               this.AdjustBuffSize();
            }

            this.buffer[this.bufpos] = 'x';
            this.UpdateLineColumn('x');

            try {
               c = this.PeekChar();
            } catch (EOFException var5) {
               this.backup(xCnt);
               return '\\';
            }

            if (c == '{') {
               this.ReadChar();
               ++this.column;
               this.bufpos -= xCnt;
               if (this.bufpos < 0) {
                  this.bufpos += this.bufsize;
               }

               try {
                  int scalarValue = hexval(this.ReadChar());
                  ++this.column;
                  if (scalarValue < 0) {
                     throw new EscapeSyntaxException("illegal_hex_digit", this.line, this.column);
                  }

                  int bufpos1;
                  while((c = this.ReadChar()) != '}') {
                     ++this.column;
                     bufpos1 = hexval(c);
                     if (bufpos1 < 0) {
                        throw new EscapeSyntaxException("illegal_hex_digit", this.line, this.column);
                     }

                     scalarValue <<= 4;
                     scalarValue |= bufpos1;
                     if (scalarValue >= 1114112) {
                        throw new EscapeSyntaxException("char_code_too_big", this.line, this.column);
                     }
                  }

                  ++this.column;
                  if (scalarValue > 65535) {
                     c = Utf16.surrogate1(scalarValue);
                     this.buffer[this.bufpos] = c;
                     bufpos1 = this.bufpos;
                     if (++this.bufpos == this.bufsize) {
                        this.bufpos = 0;
                     }

                     this.buffer[this.bufpos] = Utf16.surrogate2(scalarValue);
                     this.bufline[this.bufpos] = this.bufline[bufpos1];
                     this.bufcolumn[this.bufpos] = this.bufcolumn[bufpos1];
                     this.backup(1);
                     return c;
                  }

                  c = (char)scalarValue;
                  switch (c) {
                     case '\ufffe':
                     case '\uffff':
                        break;
                     default:
                        if (c < ' ' || Utf16.isSurrogate(c)) {
                           break;
                        }
                     case '\t':
                     case '\n':
                     case '\r':
                        this.buffer[this.bufpos] = c;
                        return c;
                  }

                  throw new EscapeSyntaxException("illegal_char_code_ref", this.line, this.column);
               } catch (EOFException var8) {
                  throw new EscapeSyntaxException("incomplete_escape", this.line, this.column);
               }
            }

            if (c != 'x') {
               this.backup(xCnt);
               return '\\';
            }

            ++xCnt;
         }
      }
   }

   /** @deprecated */
   public final int getColumn() {
      return this.bufcolumn[this.bufpos];
   }

   /** @deprecated */
   public final int getLine() {
      return this.bufline[this.bufpos];
   }

   public final int getEndColumn() {
      return this.bufcolumn[this.bufpos];
   }

   public final int getEndLine() {
      return this.bufline[this.bufpos];
   }

   public final int getBeginColumn() {
      return this.bufcolumn[this.tokenBegin];
   }

   public final int getBeginLine() {
      return this.bufline[this.tokenBegin];
   }

   public final void backup(int amount) {
      this.inBuf += amount;
      if ((this.bufpos -= amount) < 0) {
         this.bufpos += this.bufsize;
      }

   }

   public UCode_UCodeESC_CharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
      this.bufpos = -1;
      this.column = 0;
      this.line = 1;
      this.closed = false;
      this.prevCharIsLF = false;
      this.maxNextCharInd = 0;
      this.nextCharInd = -1;
      this.inBuf = 0;
      this.inputStream = dstream;
      this.line = startline;
      this.column = startcolumn - 1;
      this.available = this.bufsize = buffersize;
      this.buffer = new char[buffersize];
      this.bufline = new int[buffersize];
      this.bufcolumn = new int[buffersize];
      this.nextCharBuf = new char[4096];
      this.skipBOM();
   }

   public UCode_UCodeESC_CharStream(Reader dstream, int startline, int startcolumn) {
      this((Reader)dstream, startline, startcolumn, 4096);
   }

   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
      this.inputStream = dstream;
      this.closed = false;
      this.line = startline;
      this.column = startcolumn - 1;
      if (this.buffer == null || buffersize != this.buffer.length) {
         this.available = this.bufsize = buffersize;
         this.buffer = new char[buffersize];
         this.bufline = new int[buffersize];
         this.bufcolumn = new int[buffersize];
         this.nextCharBuf = new char[4096];
      }

      this.prevCharIsLF = false;
      this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
      this.nextCharInd = this.bufpos = -1;
      this.skipBOM();
   }

   public void ReInit(Reader dstream, int startline, int startcolumn) {
      this.ReInit((Reader)dstream, startline, startcolumn, 4096);
   }

   public UCode_UCodeESC_CharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
      this((Reader)(new InputStreamReader(dstream)), startline, startcolumn, 4096);
   }

   public UCode_UCodeESC_CharStream(InputStream dstream, int startline, int startcolumn) {
      this((InputStream)dstream, startline, startcolumn, 4096);
   }

   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
      this.ReInit((Reader)(new InputStreamReader(dstream)), startline, startcolumn, 4096);
   }

   public void ReInit(InputStream dstream, int startline, int startcolumn) {
      this.ReInit((InputStream)dstream, startline, startcolumn, 4096);
   }

   private void skipBOM() {
      try {
         if (this.PeekChar() == '\ufeff') {
            this.ReadChar();
         }
      } catch (EOFException var2) {
      }

   }

   public final String GetImage() {
      return this.bufpos >= this.tokenBegin ? new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1) : new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
   }

   public final char[] GetSuffix(int len) {
      char[] ret = new char[len];
      if (this.bufpos + 1 >= len) {
         System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
      } else {
         System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, len - this.bufpos - 1);
         System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
      }

      return ret;
   }

   public void Done() {
      this.nextCharBuf = null;
      this.buffer = null;
      this.bufline = null;
      this.bufcolumn = null;
   }

   public void adjustBeginLineColumn(int newLine, int newCol) {
      int start = this.tokenBegin;
      int len;
      if (this.bufpos >= this.tokenBegin) {
         len = this.bufpos - this.tokenBegin + this.inBuf + 1;
      } else {
         len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
      }

      int i = 0;
      int j = 0;
      int k = false;

      int var10000;
      int columnDiff;
      for(columnDiff = 0; i < len; ++i) {
         var10000 = this.bufline[j = start % this.bufsize];
         ++start;
         int k;
         if (var10000 != this.bufline[k = start % this.bufsize]) {
            break;
         }

         this.bufline[j] = newLine;
         int nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
         this.bufcolumn[j] = newCol + columnDiff;
         columnDiff = nextColDiff;
      }

      if (i < len) {
         this.bufline[j] = newLine++;
         this.bufcolumn[j] = newCol + columnDiff;

         while(i++ < len) {
            var10000 = this.bufline[j = start % this.bufsize];
            ++start;
            if (var10000 != this.bufline[start % this.bufsize]) {
               this.bufline[j] = newLine++;
            } else {
               this.bufline[j] = newLine;
            }
         }
      }

      this.line = this.bufline[j];
      this.column = this.bufcolumn[j];
   }
}
