package com.sun.tools.javac.parser;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.ArrayUtils;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.nio.CharBuffer;
import java.util.Arrays;

public class UnicodeReader {
   protected char[] buf;
   protected int bp;
   protected final int buflen;
   protected char ch;
   protected int unicodeConversionBp;
   protected Log log;
   protected Names names;
   protected char[] sbuf;
   protected int sp;
   static final boolean surrogatesSupported = surrogatesSupported();

   protected UnicodeReader(ScannerFactory var1, CharBuffer var2) {
      this(var1, JavacFileManager.toArray(var2), var2.limit());
   }

   protected UnicodeReader(ScannerFactory var1, char[] var2, int var3) {
      this.unicodeConversionBp = -1;
      this.sbuf = new char[128];
      this.log = var1.log;
      this.names = var1.names;
      if (var3 == var2.length) {
         if (var2.length > 0 && Character.isWhitespace(var2[var2.length - 1])) {
            --var3;
         } else {
            var2 = Arrays.copyOf(var2, var3 + 1);
         }
      }

      this.buf = var2;
      this.buflen = var3;
      this.buf[this.buflen] = 26;
      this.bp = -1;
      this.scanChar();
   }

   protected void scanChar() {
      if (this.bp < this.buflen) {
         this.ch = this.buf[++this.bp];
         if (this.ch == '\\') {
            this.convertUnicode();
         }
      }

   }

   protected void scanCommentChar() {
      this.scanChar();
      if (this.ch == '\\') {
         if (this.peekChar() == '\\' && !this.isUnicode()) {
            this.skipChar();
         } else {
            this.convertUnicode();
         }
      }

   }

   protected void putChar(char var1, boolean var2) {
      this.sbuf = ArrayUtils.ensureCapacity(this.sbuf, this.sp);
      this.sbuf[this.sp++] = var1;
      if (var2) {
         this.scanChar();
      }

   }

   protected void putChar(char var1) {
      this.putChar(var1, false);
   }

   protected void putChar(boolean var1) {
      this.putChar(this.ch, var1);
   }

   Name name() {
      return this.names.fromChars(this.sbuf, 0, this.sp);
   }

   String chars() {
      return new String(this.sbuf, 0, this.sp);
   }

   protected void convertUnicode() {
      if (this.ch == '\\' && this.unicodeConversionBp != this.bp) {
         ++this.bp;
         this.ch = this.buf[this.bp];
         if (this.ch == 'u') {
            do {
               ++this.bp;
               this.ch = this.buf[this.bp];
            } while(this.ch == 'u');

            int var1 = this.bp + 3;
            if (var1 < this.buflen) {
               int var2 = this.digit(this.bp, 16);

               int var3;
               for(var3 = var2; this.bp < var1 && var2 >= 0; var3 = (var3 << 4) + var2) {
                  ++this.bp;
                  this.ch = this.buf[this.bp];
                  var2 = this.digit(this.bp, 16);
               }

               if (var2 >= 0) {
                  this.ch = (char)var3;
                  this.unicodeConversionBp = this.bp;
                  return;
               }
            }

            this.log.error(this.bp, "illegal.unicode.esc", new Object[0]);
         } else {
            --this.bp;
            this.ch = '\\';
         }
      }

   }

   private static boolean surrogatesSupported() {
      try {
         Character.isHighSurrogate('a');
         return true;
      } catch (NoSuchMethodError var1) {
         return false;
      }
   }

   protected char scanSurrogates() {
      if (surrogatesSupported && Character.isHighSurrogate(this.ch)) {
         char var1 = this.ch;
         this.scanChar();
         if (Character.isLowSurrogate(this.ch)) {
            return var1;
         }

         this.ch = var1;
      }

      return '\u0000';
   }

   protected int digit(int var1, int var2) {
      char var3 = this.ch;
      int var4 = Character.digit(var3, var2);
      if (var4 >= 0 && var3 > 127) {
         this.log.error(var1 + 1, "illegal.nonascii.digit", new Object[0]);
         this.ch = "0123456789abcdef".charAt(var4);
      }

      return var4;
   }

   protected boolean isUnicode() {
      return this.unicodeConversionBp == this.bp;
   }

   protected void skipChar() {
      ++this.bp;
   }

   protected char peekChar() {
      return this.buf[this.bp + 1];
   }

   public char[] getRawCharacters() {
      char[] var1 = new char[this.buflen];
      System.arraycopy(this.buf, 0, var1, 0, this.buflen);
      return var1;
   }

   public char[] getRawCharacters(int var1, int var2) {
      int var3 = var2 - var1;
      char[] var4 = new char[var3];
      System.arraycopy(this.buf, var1, var4, 0, var3);
      return var4;
   }
}
