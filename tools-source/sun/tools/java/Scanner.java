package sun.tools.java;

import java.io.IOException;
import java.io.InputStream;

public class Scanner implements Constants {
   public static final long OFFSETINC = 1L;
   public static final long LINEINC = 4294967296L;
   public static final int EOF = -1;
   public Environment env;
   protected ScannerInputReader in;
   public boolean scanComments = false;
   public int token;
   public long pos;
   public long prevPos;
   protected int ch;
   public char charValue;
   public int intValue;
   public long longValue;
   public float floatValue;
   public double doubleValue;
   public String stringValue;
   public Identifier idValue;
   public int radix;
   public String docComment;
   private int count;
   private char[] buffer = new char[1024];

   private void growBuffer() {
      char[] var1 = new char[this.buffer.length * 2];
      System.arraycopy(this.buffer, 0, var1, 0, this.buffer.length);
      this.buffer = var1;
   }

   private void putc(int var1) {
      if (this.count == this.buffer.length) {
         this.growBuffer();
      }

      this.buffer[this.count++] = (char)var1;
   }

   private String bufferString() {
      return new String(this.buffer, 0, this.count);
   }

   public Scanner(Environment var1, InputStream var2) throws IOException {
      this.env = var1;
      this.useInputStream(var2);
   }

   protected void useInputStream(InputStream var1) throws IOException {
      try {
         this.in = new ScannerInputReader(this.env, var1);
      } catch (Exception var3) {
         this.env.setCharacterEncoding((String)null);
         this.in = new ScannerInputReader(this.env, var1);
      }

      this.ch = this.in.read();
      this.prevPos = this.in.pos;
      this.scan();
   }

   protected Scanner(Environment var1) {
      this.env = var1;
   }

   private static void defineKeyword(int var0) {
      Identifier.lookup(opNames[var0]).setType(var0);
   }

   private void skipComment() throws IOException {
      while(true) {
         switch (this.ch) {
            case -1:
               this.env.error(this.pos, "eof.in.comment");
               return;
            case 42:
               if ((this.ch = this.in.read()) != 47) {
                  break;
               }

               this.ch = this.in.read();
               return;
            default:
               this.ch = this.in.read();
         }
      }
   }

   private String scanDocComment() throws IOException {
      ScannerInputReader var2 = this.in;
      char[] var3 = this.buffer;
      int var4 = 0;

      int var1;
      while((var1 = var2.read()) == 42) {
      }

      if (var1 == 47) {
         this.ch = var2.read();
         return "";
      } else {
         if (var1 == 10) {
            var1 = var2.read();
         }

         label78:
         while(true) {
            label63:
            while(true) {
               switch (var1) {
                  case 9:
                  case 32:
                     var1 = var2.read();
                     break;
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  default:
                     if (var1 == 42) {
                        do {
                           var1 = var2.read();
                        } while(var1 == 42);

                        if (var1 == 47) {
                           this.ch = var2.read();
                           break label78;
                        }
                     }

                     while(true) {
                        switch (var1) {
                           case -1:
                              this.env.error(this.pos, "eof.in.comment");
                              this.ch = -1;
                              break label78;
                           case 0:
                           case 1:
                           case 2:
                           case 3:
                           case 4:
                           case 5:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           case 11:
                           case 12:
                           case 13:
                           case 14:
                           case 15:
                           case 16:
                           case 17:
                           case 18:
                           case 19:
                           case 20:
                           case 21:
                           case 22:
                           case 23:
                           case 24:
                           case 25:
                           case 26:
                           case 27:
                           case 28:
                           case 29:
                           case 30:
                           case 31:
                           case 32:
                           case 33:
                           case 34:
                           case 35:
                           case 36:
                           case 37:
                           case 38:
                           case 39:
                           case 40:
                           case 41:
                           default:
                              if (var4 == var3.length) {
                                 this.growBuffer();
                                 var3 = this.buffer;
                              }

                              var3[var4++] = (char)var1;
                              var1 = var2.read();
                              break;
                           case 10:
                              if (var4 == var3.length) {
                                 this.growBuffer();
                                 var3 = this.buffer;
                              }

                              var3[var4++] = '\n';
                              var1 = var2.read();
                              continue label63;
                           case 42:
                              var1 = var2.read();
                              if (var1 == 47) {
                                 this.ch = var2.read();
                                 break label78;
                              }

                              if (var4 == var3.length) {
                                 this.growBuffer();
                                 var3 = this.buffer;
                              }

                              var3[var4++] = '*';
                        }
                     }
               }
            }
         }

         if (var4 <= 0) {
            return "";
         } else {
            int var5 = var4 - 1;

            label56:
            while(var5 > -1) {
               switch (var3[var5]) {
                  case '\u0000':
                  case '\u0001':
                  case '\u0002':
                  case '\u0003':
                  case '\u0004':
                  case '\u0005':
                  case '\u0006':
                  case '\u0007':
                  case '\b':
                  case '\n':
                  case '\u000b':
                  case '\f':
                  case '\r':
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
                  case '"':
                  case '#':
                  case '$':
                  case '%':
                  case '&':
                  case '\'':
                  case '(':
                  case ')':
                  default:
                     break label56;
                  case '\t':
                  case ' ':
                  case '*':
                     --var5;
               }
            }

            var4 = var5 + 1;
            return new String(var3, 0, var4);
         }
      }
   }

   private void scanNumber() throws IOException {
      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;
      this.radix = this.ch == 48 ? 8 : 10;
      long var4 = (long)(this.ch - 48);
      this.count = 0;
      this.putc(this.ch);

      label162:
      while(true) {
         switch (this.ch = this.in.read()) {
            case 46:
               if (this.radix != 16) {
                  this.scanReal();
                  return;
               }
               break label162;
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            default:
               this.intValue = (int)var4;
               this.token = 65;
               break label162;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
               break;
            case 56:
            case 57:
               var1 = true;
               break;
            case 68:
            case 69:
            case 70:
            case 100:
            case 101:
            case 102:
               if (this.radix != 16) {
                  this.scanReal();
                  return;
               }
            case 65:
            case 66:
            case 67:
            case 97:
            case 98:
            case 99:
               var3 = true;
               this.putc(this.ch);
               if (this.radix != 16) {
                  break label162;
               }

               var2 = var2 || var4 >>> 60 != 0L;
               var4 = (var4 << 4) + 10L + (long)Character.toLowerCase((char)this.ch) - 97L;
               continue;
            case 76:
            case 108:
               this.ch = this.in.read();
               this.longValue = var4;
               this.token = 66;
               break label162;
            case 88:
            case 120:
               if (this.count != 1 || this.radix != 8) {
                  break label162;
               }

               this.radix = 16;
               var3 = false;
               continue;
         }

         var3 = true;
         this.putc(this.ch);
         if (this.radix == 10) {
            var2 = var2 || var4 * 10L / 10L != var4;
            var4 = var4 * 10L + (long)(this.ch - 48);
            var2 = var2 || var4 - 1L < -1L;
         } else if (this.radix == 8) {
            var2 = var2 || var4 >>> 61 != 0L;
            var4 = (var4 << 3) + (long)(this.ch - 48);
         } else {
            var2 = var2 || var4 >>> 60 != 0L;
            var4 = (var4 << 4) + (long)(this.ch - 48);
         }
      }

      if (!Character.isJavaLetterOrDigit((char)this.ch) && this.ch != 46) {
         if (this.radix == 8 && var1) {
            this.intValue = 0;
            this.token = 65;
            this.env.error(this.pos, "invalid.octal.number");
         } else if (this.radix == 16 && !var3) {
            this.intValue = 0;
            this.token = 65;
            this.env.error(this.pos, "invalid.hex.number");
         } else if (this.token == 65) {
            var2 = var2 || (var4 & -4294967296L) != 0L || this.radix == 10 && var4 > 2147483648L;
            if (var2) {
               this.intValue = 0;
               switch (this.radix) {
                  case 8:
                     this.env.error(this.pos, "overflow.int.oct");
                     break;
                  case 10:
                     this.env.error(this.pos, "overflow.int.dec");
                     break;
                  case 16:
                     this.env.error(this.pos, "overflow.int.hex");
                     break;
                  default:
                     throw new CompilerError("invalid radix");
               }
            }
         } else if (var2) {
            this.longValue = 0L;
            switch (this.radix) {
               case 8:
                  this.env.error(this.pos, "overflow.long.oct");
                  break;
               case 10:
                  this.env.error(this.pos, "overflow.long.dec");
                  break;
               case 16:
                  this.env.error(this.pos, "overflow.long.hex");
                  break;
               default:
                  throw new CompilerError("invalid radix");
            }
         }
      } else {
         this.env.error(this.in.pos, "invalid.number");

         do {
            do {
               this.ch = this.in.read();
            } while(Character.isJavaLetterOrDigit((char)this.ch));
         } while(this.ch == 46);

         this.intValue = 0;
         this.token = 65;
      }

   }

   private void scanReal() throws IOException {
      boolean var1 = false;
      boolean var2 = false;
      if (this.ch == 46) {
         this.putc(this.ch);
         this.ch = this.in.read();
      }

      char var3;
      label93:
      while(true) {
         switch (this.ch) {
            case 43:
            case 45:
               var3 = this.buffer[this.count - 1];
               if (var3 == 'e' || var3 == 'E') {
                  this.putc(this.ch);
                  break;
               }
            case 44:
            case 46:
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            default:
               break label93;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               this.putc(this.ch);
               break;
            case 68:
            case 100:
               this.ch = this.in.read();
               break label93;
            case 69:
            case 101:
               if (var1) {
                  break label93;
               }

               this.putc(this.ch);
               var1 = true;
               break;
            case 70:
            case 102:
               this.ch = this.in.read();
               var2 = true;
               break label93;
         }

         this.ch = this.in.read();
      }

      if (!Character.isJavaLetterOrDigit((char)this.ch) && this.ch != 46) {
         this.token = var2 ? 67 : 68;

         try {
            var3 = this.buffer[this.count - 1];
            if (var3 != 'e' && var3 != 'E' && var3 != '+' && var3 != '-') {
               String var4;
               if (var2) {
                  var4 = this.bufferString();
                  this.floatValue = Float.valueOf(var4);
                  if (Float.isInfinite(this.floatValue)) {
                     this.env.error(this.pos, "overflow.float");
                  } else if (this.floatValue == 0.0F && !looksLikeZero(var4)) {
                     this.env.error(this.pos, "underflow.float");
                  }
               } else {
                  var4 = this.bufferString();
                  this.doubleValue = Double.valueOf(var4);
                  if (Double.isInfinite(this.doubleValue)) {
                     this.env.error(this.pos, "overflow.double");
                  } else if (this.doubleValue == 0.0 && !looksLikeZero(var4)) {
                     this.env.error(this.pos, "underflow.double");
                  }
               }
            } else {
               this.env.error(this.in.pos - 1L, "float.format");
            }
         } catch (NumberFormatException var5) {
            this.env.error(this.pos, "float.format");
            this.doubleValue = 0.0;
            this.floatValue = 0.0F;
         }
      } else {
         this.env.error(this.in.pos, "invalid.number");

         do {
            do {
               this.ch = this.in.read();
            } while(Character.isJavaLetterOrDigit((char)this.ch));
         } while(this.ch == 46);

         this.doubleValue = 0.0;
         this.token = 68;
      }

   }

   private static boolean looksLikeZero(String var0) {
      int var1 = var0.length();
      int var2 = 0;

      while(var2 < var1) {
         switch (var0.charAt(var2)) {
            case '\u0000':
            case '.':
            default:
               ++var2;
               break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               return false;
            case 'E':
            case 'F':
            case 'e':
            case 'f':
               return true;
         }
      }

      return true;
   }

   private int scanEscapeChar() throws IOException {
      long var1 = this.in.pos;
      switch (this.ch = this.in.read()) {
         case 34:
            this.ch = this.in.read();
            return 34;
         case 39:
            this.ch = this.in.read();
            return 39;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
            int var3 = this.ch - 48;
            int var4 = 2;

            while(var4 > 0) {
               switch (this.ch = this.in.read()) {
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                     var3 = (var3 << 3) + this.ch - 48;
                     --var4;
                     break;
                  default:
                     if (var3 > 255) {
                        this.env.error(var1, "invalid.escape.char");
                     }

                     return var3;
               }
            }

            this.ch = this.in.read();
            if (var3 > 255) {
               this.env.error(var1, "invalid.escape.char");
            }

            return var3;
         case 92:
            this.ch = this.in.read();
            return 92;
         case 98:
            this.ch = this.in.read();
            return 8;
         case 102:
            this.ch = this.in.read();
            return 12;
         case 110:
            this.ch = this.in.read();
            return 10;
         case 114:
            this.ch = this.in.read();
            return 13;
         case 116:
            this.ch = this.in.read();
            return 9;
         default:
            this.env.error(var1, "invalid.escape.char");
            this.ch = this.in.read();
            return -1;
      }
   }

   private void scanString() throws IOException {
      this.token = 69;
      this.count = 0;
      this.ch = this.in.read();

      while(true) {
         switch (this.ch) {
            case -1:
               this.env.error(this.pos, "eof.in.string");
               this.stringValue = this.bufferString();
               return;
            case 10:
            case 13:
               this.ch = this.in.read();
               this.env.error(this.pos, "newline.in.string");
               this.stringValue = this.bufferString();
               return;
            case 34:
               this.ch = this.in.read();
               this.stringValue = this.bufferString();
               return;
            case 92:
               int var1 = this.scanEscapeChar();
               if (var1 >= 0) {
                  this.putc((char)var1);
               }
               break;
            default:
               this.putc(this.ch);
               this.ch = this.in.read();
         }
      }
   }

   private void scanCharacter() throws IOException {
      this.token = 63;
      switch (this.ch = this.in.read()) {
         case 10:
         case 13:
            this.charValue = 0;
            this.env.error(this.pos, "invalid.char.constant");
            return;
         case 39:
            this.charValue = 0;
            this.env.error(this.pos, "invalid.char.constant");

            for(this.ch = this.in.read(); this.ch == 39; this.ch = this.in.read()) {
            }

            return;
         case 92:
            int var1 = this.scanEscapeChar();
            this.charValue = (char)(var1 >= 0 ? var1 : 0);
            break;
         default:
            this.charValue = (char)this.ch;
            this.ch = this.in.read();
      }

      if (this.ch == 39) {
         this.ch = this.in.read();
      } else {
         this.env.error(this.pos, "invalid.char.constant");

         while(true) {
            switch (this.ch) {
               case -1:
               case 10:
               case 59:
                  return;
               case 39:
                  this.ch = this.in.read();
                  return;
               default:
                  this.ch = this.in.read();
            }
         }
      }
   }

   private void scanIdentifier() throws IOException {
      this.count = 0;

      label18:
      do {
         while(true) {
            this.putc(this.ch);
            switch (this.ch = this.in.read()) {
               case 36:
               case 48:
               case 49:
               case 50:
               case 51:
               case 52:
               case 53:
               case 54:
               case 55:
               case 56:
               case 57:
               case 65:
               case 66:
               case 67:
               case 68:
               case 69:
               case 70:
               case 71:
               case 72:
               case 73:
               case 74:
               case 75:
               case 76:
               case 77:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 83:
               case 84:
               case 85:
               case 86:
               case 87:
               case 88:
               case 89:
               case 90:
               case 95:
               case 97:
               case 98:
               case 99:
               case 100:
               case 101:
               case 102:
               case 103:
               case 104:
               case 105:
               case 106:
               case 107:
               case 108:
               case 109:
               case 110:
               case 111:
               case 112:
               case 113:
               case 114:
               case 115:
               case 116:
               case 117:
               case 118:
               case 119:
               case 120:
               case 121:
               case 122:
                  break;
               case 37:
               case 38:
               case 39:
               case 40:
               case 41:
               case 42:
               case 43:
               case 44:
               case 45:
               case 46:
               case 47:
               case 58:
               case 59:
               case 60:
               case 61:
               case 62:
               case 63:
               case 64:
               case 91:
               case 92:
               case 93:
               case 94:
               case 96:
               default:
                  continue label18;
            }
         }
      } while(Character.isJavaLetterOrDigit((char)this.ch));

      this.idValue = Identifier.lookup(this.bufferString());
      this.token = this.idValue.getType();
   }

   public long getEndPos() {
      return this.in.pos;
   }

   public IdentifierToken getIdToken() {
      return this.token != 60 ? null : new IdentifierToken(this.pos, this.idValue);
   }

   public long scan() throws IOException {
      return this.xscan();
   }

   protected long xscan() throws IOException {
      ScannerInputReader var1 = this.in;
      long var2 = this.pos;
      this.prevPos = var1.pos;
      this.docComment = null;

      while(true) {
         this.pos = var1.pos;
         switch (this.ch) {
            case -1:
               this.token = -1;
               return var2;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 35:
            case 64:
            case 92:
            case 96:
            default:
               if (Character.isJavaLetter((char)this.ch)) {
                  this.scanIdentifier();
                  return var2;
               }

               this.env.error(this.pos, "funny.char");
               this.ch = var1.read();
               break;
            case 10:
               if (this.scanComments) {
                  this.ch = 32;
                  this.token = 146;
                  return var2;
               }
            case 9:
            case 12:
            case 32:
               this.ch = var1.read();
               break;
            case 26:
               if ((this.ch = var1.read()) == -1) {
                  this.token = -1;
                  return var2;
               }

               this.env.error(this.pos, "funny.char");
               this.ch = var1.read();
               break;
            case 33:
               if ((this.ch = var1.read()) == 61) {
                  this.ch = var1.read();
                  this.token = 19;
                  return var2;
               }

               this.token = 37;
               return var2;
            case 34:
               this.scanString();
               return var2;
            case 36:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 95:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
               this.scanIdentifier();
               return var2;
            case 37:
               if ((this.ch = var1.read()) == 61) {
                  this.ch = var1.read();
                  this.token = 4;
                  return var2;
               }

               this.token = 32;
               return var2;
            case 38:
               switch (this.ch = var1.read()) {
                  case 38:
                     this.ch = var1.read();
                     this.token = 15;
                     return var2;
                  case 61:
                     this.ch = var1.read();
                     this.token = 10;
                     return var2;
                  default:
                     this.token = 18;
                     return var2;
               }
            case 39:
               this.scanCharacter();
               return var2;
            case 40:
               this.ch = var1.read();
               this.token = 140;
               return var2;
            case 41:
               this.ch = var1.read();
               this.token = 141;
               return var2;
            case 42:
               if ((this.ch = var1.read()) == 61) {
                  this.ch = var1.read();
                  this.token = 2;
                  return var2;
               }

               this.token = 33;
               return var2;
            case 43:
               switch (this.ch = var1.read()) {
                  case 43:
                     this.ch = var1.read();
                     this.token = 50;
                     return var2;
                  case 61:
                     this.ch = var1.read();
                     this.token = 5;
                     return var2;
                  default:
                     this.token = 29;
                     return var2;
               }
            case 44:
               this.ch = var1.read();
               this.token = 0;
               return var2;
            case 45:
               switch (this.ch = var1.read()) {
                  case 45:
                     this.ch = var1.read();
                     this.token = 51;
                     return var2;
                  case 61:
                     this.ch = var1.read();
                     this.token = 6;
                     return var2;
                  default:
                     this.token = 30;
                     return var2;
               }
            case 46:
               switch (this.ch = var1.read()) {
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     this.count = 0;
                     this.putc(46);
                     this.scanReal();
                     break;
                  default:
                     this.token = 46;
               }

               return var2;
            case 47:
               switch (this.ch = var1.read()) {
                  case 42:
                     this.ch = var1.read();
                     if (this.ch == 42) {
                        this.docComment = this.scanDocComment();
                     } else {
                        this.skipComment();
                     }

                     if (this.scanComments) {
                        return var2;
                     }
                     continue;
                  case 47:
                     while((this.ch = var1.read()) != -1 && this.ch != 10) {
                     }

                     if (this.scanComments) {
                        this.token = 146;
                        return var2;
                     }
                     continue;
                  case 61:
                     this.ch = var1.read();
                     this.token = 3;
                     return var2;
                  default:
                     this.token = 31;
                     return var2;
               }
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               this.scanNumber();
               return var2;
            case 58:
               this.ch = var1.read();
               this.token = 136;
               return var2;
            case 59:
               this.ch = var1.read();
               this.token = 135;
               return var2;
            case 60:
               switch (this.ch = var1.read()) {
                  case 60:
                     if ((this.ch = var1.read()) == 61) {
                        this.ch = var1.read();
                        this.token = 7;
                        return var2;
                     }

                     this.token = 26;
                     return var2;
                  case 61:
                     this.ch = var1.read();
                     this.token = 23;
                     return var2;
                  default:
                     this.token = 24;
                     return var2;
               }
            case 61:
               if ((this.ch = var1.read()) == 61) {
                  this.ch = var1.read();
                  this.token = 20;
                  return var2;
               }

               this.token = 1;
               return var2;
            case 62:
               switch (this.ch = var1.read()) {
                  case 61:
                     this.ch = var1.read();
                     this.token = 21;
                     return var2;
                  case 62:
                     switch (this.ch = var1.read()) {
                        case 61:
                           this.ch = var1.read();
                           this.token = 8;
                           return var2;
                        case 62:
                           if ((this.ch = var1.read()) == 61) {
                              this.ch = var1.read();
                              this.token = 9;
                              return var2;
                           }

                           this.token = 28;
                           return var2;
                        default:
                           this.token = 27;
                           return var2;
                     }
                  default:
                     this.token = 22;
                     return var2;
               }
            case 63:
               this.ch = var1.read();
               this.token = 137;
               return var2;
            case 91:
               this.ch = var1.read();
               this.token = 142;
               return var2;
            case 93:
               this.ch = var1.read();
               this.token = 143;
               return var2;
            case 94:
               if ((this.ch = var1.read()) == 61) {
                  this.ch = var1.read();
                  this.token = 12;
                  return var2;
               }

               this.token = 17;
               return var2;
            case 123:
               this.ch = var1.read();
               this.token = 138;
               return var2;
            case 124:
               switch (this.ch = var1.read()) {
                  case 61:
                     this.ch = var1.read();
                     this.token = 11;
                     return var2;
                  case 124:
                     this.ch = var1.read();
                     this.token = 14;
                     return var2;
                  default:
                     this.token = 16;
                     return var2;
               }
            case 125:
               this.ch = var1.read();
               this.token = 139;
               return var2;
            case 126:
               this.ch = var1.read();
               this.token = 38;
               return var2;
         }
      }
   }

   public void match(int var1, int var2) throws IOException {
      int var3 = 1;

      while(true) {
         while(true) {
            this.scan();
            if (this.token == var1) {
               ++var3;
            } else if (this.token == var2) {
               --var3;
               if (var3 == 0) {
                  return;
               }
            } else if (this.token == -1) {
               this.env.error(this.pos, "unbalanced.paren");
               return;
            }
         }
      }
   }

   static {
      defineKeyword(92);
      defineKeyword(90);
      defineKeyword(91);
      defineKeyword(93);
      defineKeyword(94);
      defineKeyword(95);
      defineKeyword(96);
      defineKeyword(97);
      defineKeyword(98);
      defineKeyword(99);
      defineKeyword(100);
      defineKeyword(101);
      defineKeyword(102);
      defineKeyword(103);
      defineKeyword(104);
      defineKeyword(70);
      defineKeyword(71);
      defineKeyword(72);
      defineKeyword(73);
      defineKeyword(74);
      defineKeyword(75);
      defineKeyword(76);
      defineKeyword(77);
      defineKeyword(78);
      defineKeyword(25);
      defineKeyword(80);
      defineKeyword(81);
      defineKeyword(49);
      defineKeyword(82);
      defineKeyword(83);
      defineKeyword(84);
      defineKeyword(110);
      defineKeyword(111);
      defineKeyword(112);
      defineKeyword(113);
      defineKeyword(114);
      defineKeyword(115);
      defineKeyword(144);
      defineKeyword(120);
      defineKeyword(121);
      defineKeyword(122);
      defineKeyword(124);
      defineKeyword(125);
      defineKeyword(126);
      defineKeyword(127);
      defineKeyword(130);
      defineKeyword(129);
      defineKeyword(128);
      defineKeyword(131);
      defineKeyword(123);
      defineKeyword(58);
   }
}
