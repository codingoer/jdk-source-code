package com.sun.tools.javac.parser;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Position;
import java.nio.CharBuffer;

public class JavaTokenizer {
   private static final boolean scannerDebug = false;
   private boolean allowHexFloats;
   private boolean allowBinaryLiterals;
   private boolean allowUnderscoresInLiterals;
   private Source source;
   private final Log log;
   private final Tokens tokens;
   protected Tokens.TokenKind tk;
   protected int radix;
   protected Name name;
   protected int errPos;
   protected UnicodeReader reader;
   protected ScannerFactory fac;
   private static final boolean hexFloatsWork = hexFloatsWork();

   private static boolean hexFloatsWork() {
      try {
         Float.valueOf("0x1.0p1");
         return true;
      } catch (NumberFormatException var1) {
         return false;
      }
   }

   protected JavaTokenizer(ScannerFactory var1, CharBuffer var2) {
      this(var1, new UnicodeReader(var1, var2));
   }

   protected JavaTokenizer(ScannerFactory var1, char[] var2, int var3) {
      this(var1, new UnicodeReader(var1, var2, var3));
   }

   protected JavaTokenizer(ScannerFactory var1, UnicodeReader var2) {
      this.errPos = -1;
      this.fac = var1;
      this.log = var1.log;
      this.tokens = var1.tokens;
      this.source = var1.source;
      this.reader = var2;
      this.allowBinaryLiterals = this.source.allowBinaryLiterals();
      this.allowHexFloats = this.source.allowHexFloats();
      this.allowUnderscoresInLiterals = this.source.allowUnderscoresInLiterals();
   }

   protected void lexError(int var1, String var2, Object... var3) {
      this.log.error(var1, var2, var3);
      this.tk = Tokens.TokenKind.ERROR;
      this.errPos = var1;
   }

   private void scanLitChar(int var1) {
      if (this.reader.ch == '\\') {
         if (this.reader.peekChar() == '\\' && !this.reader.isUnicode()) {
            this.reader.skipChar();
            this.reader.putChar('\\', true);
         } else {
            this.reader.scanChar();
            switch (this.reader.ch) {
               case '"':
                  this.reader.putChar('"', true);
                  break;
               case '\'':
                  this.reader.putChar('\'', true);
                  break;
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
                  char var2 = this.reader.ch;
                  int var3 = this.reader.digit(var1, 8);
                  this.reader.scanChar();
                  if ('0' <= this.reader.ch && this.reader.ch <= '7') {
                     var3 = var3 * 8 + this.reader.digit(var1, 8);
                     this.reader.scanChar();
                     if (var2 <= '3' && '0' <= this.reader.ch && this.reader.ch <= '7') {
                        var3 = var3 * 8 + this.reader.digit(var1, 8);
                        this.reader.scanChar();
                     }
                  }

                  this.reader.putChar((char)var3);
                  break;
               case '\\':
                  this.reader.putChar('\\', true);
                  break;
               case 'b':
                  this.reader.putChar('\b', true);
                  break;
               case 'f':
                  this.reader.putChar('\f', true);
                  break;
               case 'n':
                  this.reader.putChar('\n', true);
                  break;
               case 'r':
                  this.reader.putChar('\r', true);
                  break;
               case 't':
                  this.reader.putChar('\t', true);
                  break;
               default:
                  this.lexError(this.reader.bp, "illegal.esc.char");
            }
         }
      } else if (this.reader.bp != this.reader.buflen) {
         this.reader.putChar(true);
      }

   }

   private void scanDigits(int var1, int var2) {
      char var3;
      int var4;
      do {
         if (this.reader.ch != '_') {
            this.reader.putChar(false);
         } else if (!this.allowUnderscoresInLiterals) {
            this.lexError(var1, "unsupported.underscore.lit", this.source.name);
            this.allowUnderscoresInLiterals = true;
         }

         var3 = this.reader.ch;
         var4 = this.reader.bp;
         this.reader.scanChar();
      } while(this.reader.digit(var1, var2) >= 0 || this.reader.ch == '_');

      if (var3 == '_') {
         this.lexError(var4, "illegal.underscore");
      }

   }

   private void scanHexExponentAndSuffix(int var1) {
      if (this.reader.ch != 'p' && this.reader.ch != 'P') {
         this.lexError(var1, "malformed.fp.lit");
      } else {
         this.reader.putChar(true);
         this.skipIllegalUnderscores();
         if (this.reader.ch == '+' || this.reader.ch == '-') {
            this.reader.putChar(true);
         }

         this.skipIllegalUnderscores();
         if ('0' <= this.reader.ch && this.reader.ch <= '9') {
            this.scanDigits(var1, 10);
            if (!this.allowHexFloats) {
               this.lexError(var1, "unsupported.fp.lit", this.source.name);
               this.allowHexFloats = true;
            } else if (!hexFloatsWork) {
               this.lexError(var1, "unsupported.cross.fp.lit");
            }
         } else {
            this.lexError(var1, "malformed.fp.lit");
         }
      }

      if (this.reader.ch != 'f' && this.reader.ch != 'F') {
         if (this.reader.ch == 'd' || this.reader.ch == 'D') {
            this.reader.putChar(true);
         }

         this.tk = Tokens.TokenKind.DOUBLELITERAL;
         this.radix = 16;
      } else {
         this.reader.putChar(true);
         this.tk = Tokens.TokenKind.FLOATLITERAL;
         this.radix = 16;
      }

   }

   private void scanFraction(int var1) {
      this.skipIllegalUnderscores();
      if ('0' <= this.reader.ch && this.reader.ch <= '9') {
         this.scanDigits(var1, 10);
      }

      int var2 = this.reader.sp;
      if (this.reader.ch == 'e' || this.reader.ch == 'E') {
         this.reader.putChar(true);
         this.skipIllegalUnderscores();
         if (this.reader.ch == '+' || this.reader.ch == '-') {
            this.reader.putChar(true);
         }

         this.skipIllegalUnderscores();
         if ('0' <= this.reader.ch && this.reader.ch <= '9') {
            this.scanDigits(var1, 10);
            return;
         }

         this.lexError(var1, "malformed.fp.lit");
         this.reader.sp = var2;
      }

   }

   private void scanFractionAndSuffix(int var1) {
      this.radix = 10;
      this.scanFraction(var1);
      if (this.reader.ch != 'f' && this.reader.ch != 'F') {
         if (this.reader.ch == 'd' || this.reader.ch == 'D') {
            this.reader.putChar(true);
         }

         this.tk = Tokens.TokenKind.DOUBLELITERAL;
      } else {
         this.reader.putChar(true);
         this.tk = Tokens.TokenKind.FLOATLITERAL;
      }

   }

   private void scanHexFractionAndSuffix(int var1, boolean var2) {
      this.radix = 16;
      Assert.check(this.reader.ch == '.');
      this.reader.putChar(true);
      this.skipIllegalUnderscores();
      if (this.reader.digit(var1, 16) >= 0) {
         var2 = true;
         this.scanDigits(var1, 16);
      }

      if (!var2) {
         this.lexError(var1, "invalid.hex.number");
      } else {
         this.scanHexExponentAndSuffix(var1);
      }

   }

   private void skipIllegalUnderscores() {
      if (this.reader.ch == '_') {
         this.lexError(this.reader.bp, "illegal.underscore");

         while(this.reader.ch == '_') {
            this.reader.scanChar();
         }
      }

   }

   private void scanNumber(int var1, int var2) {
      this.radix = var2;
      int var3 = var2 == 8 ? 10 : var2;
      boolean var4 = false;
      if (this.reader.digit(var1, var3) >= 0) {
         var4 = true;
         this.scanDigits(var1, var3);
      }

      if (var2 == 16 && this.reader.ch == '.') {
         this.scanHexFractionAndSuffix(var1, var4);
      } else if (var4 && var2 == 16 && (this.reader.ch == 'p' || this.reader.ch == 'P')) {
         this.scanHexExponentAndSuffix(var1);
      } else if (var3 == 10 && this.reader.ch == '.') {
         this.reader.putChar(true);
         this.scanFractionAndSuffix(var1);
      } else if (var3 == 10 && (this.reader.ch == 'e' || this.reader.ch == 'E' || this.reader.ch == 'f' || this.reader.ch == 'F' || this.reader.ch == 'd' || this.reader.ch == 'D')) {
         this.scanFractionAndSuffix(var1);
      } else if (this.reader.ch != 'l' && this.reader.ch != 'L') {
         this.tk = Tokens.TokenKind.INTLITERAL;
      } else {
         this.reader.scanChar();
         this.tk = Tokens.TokenKind.LONGLITERAL;
      }

   }

   private void scanIdent() {
      this.reader.putChar(true);

      while(true) {
         while(true) {
            switch (this.reader.ch) {
               case '\u0000':
               case '\u0001':
               case '\u0002':
               case '\u0003':
               case '\u0004':
               case '\u0005':
               case '\u0006':
               case '\u0007':
               case '\b':
               case '\u000e':
               case '\u000f':
               case '\u0010':
               case '\u0011':
               case '\u0012':
               case '\u0013':
               case '\u0014':
               case '\u0015':
               case '\u0016':
               case '\u0017':
               case '\u0018':
               case '\u0019':
               case '\u001b':
               case '\u007f':
                  this.reader.scanChar();
                  break;
               case '\t':
               case '\n':
               case '\u000b':
               case '\f':
               case '\r':
               case '\u001c':
               case '\u001d':
               case '\u001e':
               case '\u001f':
               case ' ':
               case '!':
               case '"':
               case '#':
               case '%':
               case '&':
               case '\'':
               case '(':
               case ')':
               case '*':
               case '+':
               case ',':
               case '-':
               case '.':
               case '/':
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '`':
               case '{':
               case '|':
               case '}':
               case '~':
               default:
                  boolean var1;
                  if (this.reader.ch < 128) {
                     var1 = false;
                  } else {
                     if (Character.isIdentifierIgnorable(this.reader.ch)) {
                        this.reader.scanChar();
                        break;
                     }

                     char var2 = this.reader.scanSurrogates();
                     if (var2 != 0) {
                        this.reader.putChar(var2);
                        var1 = Character.isJavaIdentifierPart(Character.toCodePoint(var2, this.reader.ch));
                     } else {
                        var1 = Character.isJavaIdentifierPart(this.reader.ch);
                     }
                  }

                  if (!var1) {
                     this.name = this.reader.name();
                     this.tk = this.tokens.lookupKind(this.name);
                     return;
                  }
               case '$':
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case 'A':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
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
               case '_':
               case 'a':
               case 'b':
               case 'c':
               case 'd':
               case 'e':
               case 'f':
               case 'g':
               case 'h':
               case 'i':
               case 'j':
               case 'k':
               case 'l':
               case 'm':
               case 'n':
               case 'o':
               case 'p':
               case 'q':
               case 'r':
               case 's':
               case 't':
               case 'u':
               case 'v':
               case 'w':
               case 'x':
               case 'y':
               case 'z':
                  this.reader.putChar(true);
                  break;
               case '\u001a':
                  if (this.reader.bp >= this.reader.buflen) {
                     this.name = this.reader.name();
                     this.tk = this.tokens.lookupKind(this.name);
                     return;
                  }

                  this.reader.scanChar();
            }
         }
      }
   }

   private boolean isSpecial(char var1) {
      switch (var1) {
         case '!':
         case '%':
         case '&':
         case '*':
         case '+':
         case '-':
         case ':':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case '^':
         case '|':
         case '~':
            return true;
         default:
            return false;
      }
   }

   private void scanOperator() {
      while(true) {
         this.reader.putChar(false);
         Name var1 = this.reader.name();
         Tokens.TokenKind var2 = this.tokens.lookupKind(var1);
         if (var2 == Tokens.TokenKind.IDENTIFIER) {
            --this.reader.sp;
         } else {
            this.tk = var2;
            this.reader.scanChar();
            if (this.isSpecial(this.reader.ch)) {
               continue;
            }
         }

         return;
      }
   }

   public Tokens.Token readToken() {
      this.reader.sp = 0;
      this.name = null;
      this.radix = 0;
      boolean var1 = false;
      boolean var2 = false;
      List var3 = null;

      try {
         int var9;
         label480:
         while(true) {
            var9 = this.reader.bp;
            int var4;
            boolean var11;
            switch (this.reader.ch) {
               case '\t':
               case '\f':
               case ' ':
                  do {
                     do {
                        this.reader.scanChar();
                     } while(this.reader.ch == ' ');
                  } while(this.reader.ch == '\t' || this.reader.ch == '\f');

                  this.processWhiteSpace(var9, this.reader.bp);
                  break;
               case '\n':
                  this.reader.scanChar();
                  this.processLineTerminator(var9, this.reader.bp);
                  break;
               case '\u000b':
               case '\u000e':
               case '\u000f':
               case '\u0010':
               case '\u0011':
               case '\u0012':
               case '\u0013':
               case '\u0014':
               case '\u0015':
               case '\u0016':
               case '\u0017':
               case '\u0018':
               case '\u0019':
               case '\u001a':
               case '\u001b':
               case '\u001c':
               case '\u001d':
               case '\u001e':
               case '\u001f':
               case '!':
               case '#':
               case '%':
               case '&':
               case '*':
               case '+':
               case '-':
               case ':':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case '\\':
               case '^':
               case '`':
               case '|':
               default:
                  if (this.isSpecial(this.reader.ch)) {
                     this.scanOperator();
                  } else {
                     if (this.reader.ch < 128) {
                        var11 = false;
                     } else {
                        char var13 = this.reader.scanSurrogates();
                        if (var13 != 0) {
                           this.reader.putChar(var13);
                           var11 = Character.isJavaIdentifierStart(Character.toCodePoint(var13, this.reader.ch));
                        } else {
                           var11 = Character.isJavaIdentifierStart(this.reader.ch);
                        }
                     }

                     if (var11) {
                        this.scanIdent();
                     } else if (this.reader.bp != this.reader.buflen && (this.reader.ch != 26 || this.reader.bp + 1 != this.reader.buflen)) {
                        String var14 = ' ' < this.reader.ch && this.reader.ch < 127 ? String.format("%s", this.reader.ch) : String.format("\\u%04x", Integer.valueOf(this.reader.ch));
                        this.lexError(var9, "illegal.char", var14);
                        this.reader.scanChar();
                     } else {
                        this.tk = Tokens.TokenKind.EOF;
                        var9 = this.reader.buflen;
                     }
                  }
                  break label480;
               case '\r':
                  this.reader.scanChar();
                  if (this.reader.ch == '\n') {
                     this.reader.scanChar();
                  }

                  this.processLineTerminator(var9, this.reader.bp);
                  break;
               case '"':
                  this.reader.scanChar();

                  while(this.reader.ch != '"' && this.reader.ch != '\r' && this.reader.ch != '\n' && this.reader.bp < this.reader.buflen) {
                     this.scanLitChar(var9);
                  }

                  if (this.reader.ch == '"') {
                     this.tk = Tokens.TokenKind.STRINGLITERAL;
                     this.reader.scanChar();
                  } else {
                     this.lexError(var9, "unclosed.str.lit");
                  }
                  break label480;
               case '$':
               case 'A':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
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
               case '_':
               case 'a':
               case 'b':
               case 'c':
               case 'd':
               case 'e':
               case 'f':
               case 'g':
               case 'h':
               case 'i':
               case 'j':
               case 'k':
               case 'l':
               case 'm':
               case 'n':
               case 'o':
               case 'p':
               case 'q':
               case 'r':
               case 's':
               case 't':
               case 'u':
               case 'v':
               case 'w':
               case 'x':
               case 'y':
               case 'z':
                  this.scanIdent();
                  break label480;
               case '\'':
                  this.reader.scanChar();
                  if (this.reader.ch == '\'') {
                     this.lexError(var9, "empty.char.lit");
                  } else {
                     if (this.reader.ch == '\r' || this.reader.ch == '\n') {
                        this.lexError(var9, "illegal.line.end.in.char.lit");
                     }

                     this.scanLitChar(var9);
                     char var12 = this.reader.ch;
                     if (this.reader.ch == '\'') {
                        this.reader.scanChar();
                        this.tk = Tokens.TokenKind.CHARLITERAL;
                     } else {
                        this.lexError(var9, "unclosed.char.lit");
                     }
                  }
                  break label480;
               case '(':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.LPAREN;
                  break label480;
               case ')':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.RPAREN;
                  break label480;
               case ',':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.COMMA;
                  break label480;
               case '.':
                  this.reader.scanChar();
                  if ('0' <= this.reader.ch && this.reader.ch <= '9') {
                     this.reader.putChar('.');
                     this.scanFractionAndSuffix(var9);
                  } else if (this.reader.ch == '.') {
                     var4 = this.reader.bp;
                     this.reader.putChar('.');
                     this.reader.putChar('.', true);
                     if (this.reader.ch == '.') {
                        this.reader.scanChar();
                        this.reader.putChar('.');
                        this.tk = Tokens.TokenKind.ELLIPSIS;
                     } else {
                        this.lexError(var4, "illegal.dot");
                     }
                  } else {
                     this.tk = Tokens.TokenKind.DOT;
                  }
                  break label480;
               case '/':
                  this.reader.scanChar();
                  if (this.reader.ch == '/') {
                     do {
                        this.reader.scanCommentChar();
                     } while(this.reader.ch != '\r' && this.reader.ch != '\n' && this.reader.bp < this.reader.buflen);

                     if (this.reader.bp < this.reader.buflen) {
                        var3 = this.addComment(var3, this.processComment(var9, this.reader.bp, Tokens.Comment.CommentStyle.LINE));
                     }
                     break;
                  } else {
                     if (this.reader.ch != '*') {
                        if (this.reader.ch == '=') {
                           this.tk = Tokens.TokenKind.SLASHEQ;
                           this.reader.scanChar();
                        } else {
                           this.tk = Tokens.TokenKind.SLASH;
                        }
                        break label480;
                     }

                     var11 = false;
                     this.reader.scanChar();
                     Tokens.Comment.CommentStyle var5;
                     if (this.reader.ch == '*') {
                        var5 = Tokens.Comment.CommentStyle.JAVADOC;
                        this.reader.scanCommentChar();
                        if (this.reader.ch == '/') {
                           var11 = true;
                        }
                     } else {
                        var5 = Tokens.Comment.CommentStyle.BLOCK;
                     }

                     while(!var11 && this.reader.bp < this.reader.buflen) {
                        if (this.reader.ch == '*') {
                           this.reader.scanChar();
                           if (this.reader.ch == '/') {
                              break;
                           }
                        } else {
                           this.reader.scanCommentChar();
                        }
                     }

                     if (this.reader.ch == '/') {
                        this.reader.scanChar();
                        var3 = this.addComment(var3, this.processComment(var9, this.reader.bp, var5));
                        break;
                     }

                     this.lexError(var9, "unclosed.comment");
                     break label480;
                  }
               case '0':
                  this.reader.scanChar();
                  if (this.reader.ch != 'x' && this.reader.ch != 'X') {
                     if (this.reader.ch != 'b' && this.reader.ch != 'B') {
                        this.reader.putChar('0');
                        if (this.reader.ch == '_') {
                           var4 = this.reader.bp;

                           do {
                              this.reader.scanChar();
                           } while(this.reader.ch == '_');

                           if (this.reader.digit(var9, 10) < 0) {
                              this.lexError(var4, "illegal.underscore");
                           }
                        }

                        this.scanNumber(var9, 8);
                     } else {
                        if (!this.allowBinaryLiterals) {
                           this.lexError(var9, "unsupported.binary.lit", this.source.name);
                           this.allowBinaryLiterals = true;
                        }

                        this.reader.scanChar();
                        this.skipIllegalUnderscores();
                        if (this.reader.digit(var9, 2) < 0) {
                           this.lexError(var9, "invalid.binary.number");
                        } else {
                           this.scanNumber(var9, 2);
                        }
                     }
                  } else {
                     this.reader.scanChar();
                     this.skipIllegalUnderscores();
                     if (this.reader.ch == '.') {
                        this.scanHexFractionAndSuffix(var9, false);
                     } else if (this.reader.digit(var9, 16) < 0) {
                        this.lexError(var9, "invalid.hex.number");
                     } else {
                        this.scanNumber(var9, 16);
                     }
                  }
                  break label480;
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  this.scanNumber(var9, 10);
                  break label480;
               case ';':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.SEMI;
                  break label480;
               case '[':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.LBRACKET;
                  break label480;
               case ']':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.RBRACKET;
                  break label480;
               case '{':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.LBRACE;
                  break label480;
               case '}':
                  this.reader.scanChar();
                  this.tk = Tokens.TokenKind.RBRACE;
                  break label480;
            }
         }

         int var10 = this.reader.bp;
         switch (this.tk.tag) {
            case DEFAULT:
               Tokens.Token var18 = new Tokens.Token(this.tk, var9, var10, var3);
               return var18;
            case NAMED:
               Tokens.NamedToken var17 = new Tokens.NamedToken(this.tk, var9, var10, this.name, var3);
               return var17;
            case STRING:
               Tokens.StringToken var16 = new Tokens.StringToken(this.tk, var9, var10, this.reader.chars(), var3);
               return var16;
            case NUMERIC:
               Tokens.NumericToken var15 = new Tokens.NumericToken(this.tk, var9, var10, this.reader.chars(), this.radix, var3);
               return var15;
            default:
               throw new AssertionError();
         }
      } finally {
         ;
      }
   }

   List addComment(List var1, Tokens.Comment var2) {
      return var1 == null ? List.of(var2) : var1.prepend(var2);
   }

   public int errPos() {
      return this.errPos;
   }

   public void errPos(int var1) {
      this.errPos = var1;
   }

   protected Tokens.Comment processComment(int var1, int var2, Tokens.Comment.CommentStyle var3) {
      char[] var4 = this.reader.getRawCharacters(var1, var2);
      return new BasicComment(new UnicodeReader(this.fac, var4, var4.length), var3);
   }

   protected void processWhiteSpace(int var1, int var2) {
   }

   protected void processLineTerminator(int var1, int var2) {
   }

   public Position.LineMap getLineMap() {
      return Position.makeLineMap(this.reader.getRawCharacters(), this.reader.buflen, false);
   }

   protected static class BasicComment implements Tokens.Comment {
      Tokens.Comment.CommentStyle cs;
      UnicodeReader comment_reader;
      protected boolean deprecatedFlag = false;
      protected boolean scanned = false;

      protected BasicComment(UnicodeReader var1, Tokens.Comment.CommentStyle var2) {
         this.comment_reader = var1;
         this.cs = var2;
      }

      public String getText() {
         return null;
      }

      public int getSourcePos(int var1) {
         return -1;
      }

      public Tokens.Comment.CommentStyle getStyle() {
         return this.cs;
      }

      public boolean isDeprecated() {
         if (!this.scanned && this.cs == Tokens.Comment.CommentStyle.JAVADOC) {
            this.scanDocComment();
         }

         return this.deprecatedFlag;
      }

      protected void scanDocComment() {
         try {
            boolean var1 = false;
            UnicodeReader var10000 = this.comment_reader;
            var10000.bp += 3;
            this.comment_reader.ch = this.comment_reader.buf[this.comment_reader.bp];

            label217:
            while(this.comment_reader.bp < this.comment_reader.buflen) {
               while(this.comment_reader.bp < this.comment_reader.buflen && (this.comment_reader.ch == ' ' || this.comment_reader.ch == '\t' || this.comment_reader.ch == '\f')) {
                  this.comment_reader.scanCommentChar();
               }

               while(this.comment_reader.bp < this.comment_reader.buflen && this.comment_reader.ch == '*') {
                  this.comment_reader.scanCommentChar();
                  if (this.comment_reader.ch == '/') {
                     return;
                  }
               }

               while(this.comment_reader.bp < this.comment_reader.buflen && (this.comment_reader.ch == ' ' || this.comment_reader.ch == '\t' || this.comment_reader.ch == '\f')) {
                  this.comment_reader.scanCommentChar();
               }

               var1 = false;
               if (!this.deprecatedFlag) {
                  String var2 = "@deprecated";
                  int var3 = 0;

                  while(this.comment_reader.bp < this.comment_reader.buflen && this.comment_reader.ch == var2.charAt(var3)) {
                     this.comment_reader.scanCommentChar();
                     ++var3;
                     if (var3 == var2.length()) {
                        var1 = true;
                        break;
                     }
                  }
               }

               if (var1 && this.comment_reader.bp < this.comment_reader.buflen) {
                  if (Character.isWhitespace(this.comment_reader.ch)) {
                     this.deprecatedFlag = true;
                  } else if (this.comment_reader.ch == '*') {
                     this.comment_reader.scanCommentChar();
                     if (this.comment_reader.ch == '/') {
                        this.deprecatedFlag = true;
                        return;
                     }
                  }
               }

               while(this.comment_reader.bp < this.comment_reader.buflen) {
                  switch (this.comment_reader.ch) {
                     case '\r':
                        this.comment_reader.scanCommentChar();
                        if (this.comment_reader.ch != '\n') {
                           continue label217;
                        }
                     case '\n':
                        this.comment_reader.scanCommentChar();
                        continue label217;
                     case '*':
                        this.comment_reader.scanCommentChar();
                        if (this.comment_reader.ch == '/') {
                           return;
                        }
                        break;
                     default:
                        this.comment_reader.scanCommentChar();
                  }
               }
            }

         } finally {
            this.scanned = true;
         }
      }
   }
}
