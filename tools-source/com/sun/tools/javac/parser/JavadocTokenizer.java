package com.sun.tools.javac.parser;

import com.sun.tools.javac.util.Position;
import java.nio.CharBuffer;

public class JavadocTokenizer extends JavaTokenizer {
   protected JavadocTokenizer(ScannerFactory var1, CharBuffer var2) {
      super(var1, var2);
   }

   protected JavadocTokenizer(ScannerFactory var1, char[] var2, int var3) {
      super(var1, var2, var3);
   }

   protected Tokens.Comment processComment(int var1, int var2, Tokens.Comment.CommentStyle var3) {
      char[] var4 = this.reader.getRawCharacters(var1, var2);
      return new JavadocComment(new DocReader(this.fac, var4, var4.length, var1), var3);
   }

   public Position.LineMap getLineMap() {
      char[] var1 = this.reader.getRawCharacters();
      return Position.makeLineMap(var1, var1.length, true);
   }

   protected static class JavadocComment extends JavaTokenizer.BasicComment {
      private String docComment = null;
      private int[] docPosns = null;

      JavadocComment(DocReader var1, Tokens.Comment.CommentStyle var2) {
         super(var1, var2);
      }

      public String getText() {
         if (!this.scanned && this.cs == Tokens.Comment.CommentStyle.JAVADOC) {
            this.scanDocComment();
         }

         return this.docComment;
      }

      public int getSourcePos(int var1) {
         if (var1 == -1) {
            return -1;
         } else if (var1 >= 0 && var1 <= this.docComment.length()) {
            if (this.docPosns == null) {
               return -1;
            } else {
               int var2 = 0;
               int var3 = this.docPosns.length;

               while(var2 < var3 - 2) {
                  int var4 = (var2 + var3) / 4 * 2;
                  if (this.docPosns[var4] < var1) {
                     var2 = var4;
                  } else {
                     if (this.docPosns[var4] == var1) {
                        return this.docPosns[var4 + 1];
                     }

                     var3 = var4;
                  }
               }

               return this.docPosns[var2 + 1] + (var1 - this.docPosns[var2]);
            }
         } else {
            throw new StringIndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      protected void scanDocComment() {
         try {
            boolean var1 = true;
            ((DocReader)this.comment_reader).scanCommentChar();
            ((DocReader)this.comment_reader).scanCommentChar();

            while(((DocReader)this.comment_reader).bp < ((DocReader)this.comment_reader).buflen && ((DocReader)this.comment_reader).ch == '*') {
               ((DocReader)this.comment_reader).scanCommentChar();
            }

            if (((DocReader)this.comment_reader).bp >= ((DocReader)this.comment_reader).buflen || ((DocReader)this.comment_reader).ch != '/') {
               if (((DocReader)this.comment_reader).bp < ((DocReader)this.comment_reader).buflen) {
                  if (((DocReader)this.comment_reader).ch == '\n') {
                     ((DocReader)this.comment_reader).scanCommentChar();
                     var1 = false;
                  } else if (((DocReader)this.comment_reader).ch == '\r') {
                     ((DocReader)this.comment_reader).scanCommentChar();
                     if (((DocReader)this.comment_reader).ch == '\n') {
                        ((DocReader)this.comment_reader).scanCommentChar();
                        var1 = false;
                     }
                  }
               }

               while(true) {
                  label283: {
                     int var2;
                     if (((DocReader)this.comment_reader).bp < ((DocReader)this.comment_reader).buflen) {
                        label274: {
                           var2 = ((DocReader)this.comment_reader).bp;
                           char var3 = ((DocReader)this.comment_reader).ch;

                           label247:
                           while(((DocReader)this.comment_reader).bp < ((DocReader)this.comment_reader).buflen) {
                              switch (((DocReader)this.comment_reader).ch) {
                                 case '\t':
                                    ((DocReader)this.comment_reader).col = (((DocReader)this.comment_reader).col - 1) / 8 * 8 + 8;
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break;
                                 case '\f':
                                    ((DocReader)this.comment_reader).col = 0;
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break;
                                 case ' ':
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break;
                                 default:
                                    break label247;
                              }
                           }

                           if (((DocReader)this.comment_reader).ch == '*') {
                              do {
                                 ((DocReader)this.comment_reader).scanCommentChar();
                              } while(((DocReader)this.comment_reader).ch == '*');

                              if (((DocReader)this.comment_reader).ch == '/') {
                                 break label274;
                              }
                           } else if (!var1) {
                              ((DocReader)this.comment_reader).bp = var2;
                              ((DocReader)this.comment_reader).ch = var3;
                           }

                           label230:
                           while(true) {
                              if (((DocReader)this.comment_reader).bp >= ((DocReader)this.comment_reader).buflen) {
                                 break label283;
                              }

                              switch (((DocReader)this.comment_reader).ch) {
                                 case '\t':
                                 case ' ':
                                    ((DocReader)this.comment_reader).putChar(((DocReader)this.comment_reader).ch, false);
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break;
                                 case '\f':
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break label283;
                                 case '\r':
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    if (((DocReader)this.comment_reader).ch != '\n') {
                                       ((DocReader)this.comment_reader).putChar('\n', false);
                                       break label283;
                                    }
                                 case '\n':
                                    ((DocReader)this.comment_reader).putChar(((DocReader)this.comment_reader).ch, false);
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    break label283;
                                 case '*':
                                    ((DocReader)this.comment_reader).scanCommentChar();
                                    if (((DocReader)this.comment_reader).ch == '/') {
                                       break label230;
                                    }

                                    ((DocReader)this.comment_reader).putChar('*', false);
                                    break;
                                 default:
                                    ((DocReader)this.comment_reader).putChar(((DocReader)this.comment_reader).ch, false);
                                    ((DocReader)this.comment_reader).scanCommentChar();
                              }
                           }
                        }
                     }

                     if (((DocReader)this.comment_reader).sp > 0) {
                        var2 = ((DocReader)this.comment_reader).sp - 1;

                        label217:
                        while(var2 > -1) {
                           switch (((DocReader)this.comment_reader).sbuf[var2]) {
                              case '*':
                                 --var2;
                                 break;
                              default:
                                 break label217;
                           }
                        }

                        ((DocReader)this.comment_reader).sp = var2 + 1;
                        this.docComment = ((DocReader)this.comment_reader).chars();
                        this.docPosns = new int[((DocReader)this.comment_reader).pp];
                        System.arraycopy(((DocReader)this.comment_reader).pbuf, 0, this.docPosns, 0, this.docPosns.length);
                     } else {
                        this.docComment = "";
                     }

                     return;
                  }

                  var1 = false;
               }
            }

            this.docComment = "";
         } finally {
            this.scanned = true;
            this.comment_reader = null;
            if (this.docComment != null && this.docComment.matches("(?sm).*^\\s*@deprecated( |$).*")) {
               this.deprecatedFlag = true;
            }

         }

      }
   }

   static class DocReader extends UnicodeReader {
      int col;
      int startPos;
      int[] pbuf = new int[128];
      int pp = 0;

      DocReader(ScannerFactory var1, char[] var2, int var3, int var4) {
         super(var1, var2, var3);
         this.startPos = var4;
      }

      protected void convertUnicode() {
         if (this.ch == '\\' && this.unicodeConversionBp != this.bp) {
            ++this.bp;
            this.ch = this.buf[this.bp];
            ++this.col;
            if (this.ch == 'u') {
               do {
                  ++this.bp;
                  this.ch = this.buf[this.bp];
                  ++this.col;
               } while(this.ch == 'u');

               int var1 = this.bp + 3;
               if (var1 < this.buflen) {
                  int var2 = this.digit(this.bp, 16);

                  int var3;
                  for(var3 = var2; this.bp < var1 && var2 >= 0; var3 = (var3 << 4) + var2) {
                     ++this.bp;
                     this.ch = this.buf[this.bp];
                     ++this.col;
                     var2 = this.digit(this.bp, 16);
                  }

                  if (var2 >= 0) {
                     this.ch = (char)var3;
                     this.unicodeConversionBp = this.bp;
                     return;
                  }
               }
            } else {
               --this.bp;
               this.ch = '\\';
               --this.col;
            }
         }

      }

      protected void scanCommentChar() {
         this.scanChar();
         if (this.ch == '\\') {
            if (this.peekChar() == '\\' && !this.isUnicode()) {
               this.putChar(this.ch, false);
               ++this.bp;
               ++this.col;
            } else {
               this.convertUnicode();
            }
         }

      }

      protected void scanChar() {
         ++this.bp;
         this.ch = this.buf[this.bp];
         switch (this.ch) {
            case '\t':
               this.col = this.col / 8 * 8 + 8;
               break;
            case '\n':
               if (this.bp == 0 || this.buf[this.bp - 1] != '\r') {
                  this.col = 0;
               }
               break;
            case '\r':
               this.col = 0;
               break;
            case '\\':
               ++this.col;
               this.convertUnicode();
               break;
            default:
               ++this.col;
         }

      }

      public void putChar(char var1, boolean var2) {
         if (this.pp == 0 || this.sp - this.pbuf[this.pp - 2] != this.startPos + this.bp - this.pbuf[this.pp - 1]) {
            if (this.pp + 1 >= this.pbuf.length) {
               int[] var3 = new int[this.pbuf.length * 2];
               System.arraycopy(this.pbuf, 0, var3, 0, this.pbuf.length);
               this.pbuf = var3;
            }

            this.pbuf[this.pp] = this.sp;
            this.pbuf[this.pp + 1] = this.startPos + this.bp;
            this.pp += 2;
         }

         super.putChar(var1, var2);
      }
   }
}
