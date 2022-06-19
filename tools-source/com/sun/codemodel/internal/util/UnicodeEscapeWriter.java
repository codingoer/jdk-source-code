package com.sun.codemodel.internal.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class UnicodeEscapeWriter extends FilterWriter {
   public UnicodeEscapeWriter(Writer next) {
      super(next);
   }

   public final void write(int ch) throws IOException {
      if (!this.requireEscaping(ch)) {
         this.out.write(ch);
      } else {
         this.out.write("\\u");
         String s = Integer.toHexString(ch);

         for(int i = s.length(); i < 4; ++i) {
            this.out.write(48);
         }

         this.out.write(s);
      }

   }

   protected boolean requireEscaping(int ch) {
      if (ch >= 128) {
         return true;
      } else {
         return ch < 32 && " \t\r\n".indexOf(ch) == -1;
      }
   }

   public final void write(char[] buf, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(buf[off + i]);
      }

   }

   public final void write(char[] buf) throws IOException {
      this.write((char[])buf, 0, buf.length);
   }

   public final void write(String buf, int off, int len) throws IOException {
      this.write(buf.toCharArray(), off, len);
   }

   public final void write(String buf) throws IOException {
      this.write((char[])buf.toCharArray(), 0, buf.length());
   }
}
