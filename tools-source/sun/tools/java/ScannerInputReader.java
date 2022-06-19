package sun.tools.java;

import java.io.CharConversionException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ScannerInputReader extends FilterReader implements Constants {
   Environment env;
   long pos;
   private long chpos;
   private int pushBack = -1;
   private static final int BUFFERLEN = 10240;
   private final char[] buffer = new char[10240];
   private int currentIndex = 0;
   private int numChars = 0;

   public ScannerInputReader(Environment var1, InputStream var2) throws UnsupportedEncodingException {
      super(var1.getCharacterEncoding() != null ? new InputStreamReader(var2, var1.getCharacterEncoding()) : new InputStreamReader(var2));
      this.env = var1;
      this.chpos = 4294967296L;
   }

   private int getNextChar() throws IOException {
      if (this.currentIndex >= this.numChars) {
         this.numChars = this.in.read(this.buffer);
         if (this.numChars == -1) {
            return -1;
         }

         this.currentIndex = 0;
      }

      return this.buffer[this.currentIndex++];
   }

   public int read(char[] var1, int var2, int var3) {
      throw new CompilerError("ScannerInputReader is not a fully implemented reader.");
   }

   public int read() throws IOException {
      this.pos = (long)(this.chpos++);
      int var1 = this.pushBack;
      if (var1 == -1) {
         try {
            label74: {
               if (this.currentIndex >= this.numChars) {
                  this.numChars = this.in.read(this.buffer);
                  if (this.numChars == -1) {
                     var1 = -1;
                     break label74;
                  }

                  this.currentIndex = 0;
               }

               var1 = this.buffer[this.currentIndex++];
            }
         } catch (CharConversionException var4) {
            this.env.error(this.pos, "invalid.encoding.char");
            return -1;
         }
      } else {
         this.pushBack = -1;
      }

      switch (var1) {
         case -2:
            return 92;
         case 10:
            this.chpos += 4294967296L;
            return 10;
         case 13:
            if ((var1 = this.getNextChar()) != 10) {
               this.pushBack = var1;
            } else {
               ++this.chpos;
            }

            this.chpos += 4294967296L;
            return 10;
         case 92:
            if ((var1 = this.getNextChar()) != 117) {
               this.pushBack = var1 == 92 ? -2 : var1;
               return 92;
            } else {
               ++this.chpos;

               while((var1 = this.getNextChar()) == 117) {
                  ++this.chpos;
               }

               int var2 = 0;

               for(int var3 = 0; var3 < 4; var1 = this.getNextChar()) {
                  switch (var1) {
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
                        var2 = (var2 << 4) + var1 - 48;
                        break;
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
                     default:
                        this.env.error(this.pos, "invalid.escape.char");
                        this.pushBack = var1;
                        return var2;
                     case 65:
                     case 66:
                     case 67:
                     case 68:
                     case 69:
                     case 70:
                        var2 = (var2 << 4) + 10 + var1 - 65;
                        break;
                     case 97:
                     case 98:
                     case 99:
                     case 100:
                     case 101:
                     case 102:
                        var2 = (var2 << 4) + 10 + var1 - 97;
                  }

                  ++var3;
                  ++this.chpos;
               }

               this.pushBack = var1;
               switch (var2) {
                  case 10:
                     this.chpos += 4294967296L;
                     return 10;
                  case 13:
                     if ((var1 = this.getNextChar()) != 10) {
                        this.pushBack = var1;
                     } else {
                        ++this.chpos;
                     }

                     this.chpos += 4294967296L;
                     return 10;
                  default:
                     return var2;
               }
            }
         default:
            return var1;
      }
   }
}
