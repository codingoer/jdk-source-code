package com.sun.codemodel.internal.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class JavadocEscapeWriter extends FilterWriter {
   public JavadocEscapeWriter(Writer next) {
      super(next);
   }

   public void write(int ch) throws IOException {
      if (ch == 60) {
         this.out.write("&lt;");
      } else if (ch == 38) {
         this.out.write("&amp;");
      } else {
         this.out.write(ch);
      }

   }

   public void write(char[] buf, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         this.write(buf[off + i]);
      }

   }

   public void write(char[] buf) throws IOException {
      this.write((char[])buf, 0, buf.length);
   }

   public void write(String buf, int off, int len) throws IOException {
      this.write(buf.toCharArray(), off, len);
   }

   public void write(String buf) throws IOException {
      this.write((char[])buf.toCharArray(), 0, buf.length());
   }
}
