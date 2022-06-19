package com.sun.tools.internal.ws.processor.util;

import com.sun.tools.internal.ws.processor.generator.GeneratorException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.MessageFormat;

public class IndentingWriter extends BufferedWriter {
   private boolean beginningOfLine;
   private int currentIndent;
   private int indentStep;

   public IndentingWriter(Writer out) {
      super(out);
      this.beginningOfLine = true;
      this.currentIndent = 0;
      this.indentStep = 4;
   }

   public IndentingWriter(Writer out, int step) {
      this(out);
      if (this.indentStep < 0) {
         throw new IllegalArgumentException("negative indent step");
      } else {
         this.indentStep = step;
      }
   }

   public void write(int c) throws IOException {
      this.checkWrite();
      super.write(c);
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      if (len > 0) {
         this.checkWrite();
      }

      super.write(cbuf, off, len);
   }

   public void write(String s, int off, int len) throws IOException {
      if (len > 0) {
         this.checkWrite();
      }

      super.write(s, off, len);
   }

   public void newLine() throws IOException {
      super.newLine();
      this.beginningOfLine = true;
   }

   protected void checkWrite() throws IOException {
      if (this.beginningOfLine) {
         this.beginningOfLine = false;

         for(int i = this.currentIndent; i > 0; --i) {
            super.write(32);
         }
      }

   }

   protected void indentIn() {
      this.currentIndent += this.indentStep;
   }

   protected void indentOut() {
      this.currentIndent -= this.indentStep;
      if (this.currentIndent < 0) {
         this.currentIndent = 0;
      }

   }

   public void pI() {
      this.indentIn();
   }

   public void pO() {
      this.indentOut();
   }

   public void pI(int levels) {
      for(int i = 0; i < levels; ++i) {
         this.indentIn();
      }

   }

   public void pO(int levels) {
      for(int i = 0; i < levels; ++i) {
         this.indentOut();
      }

   }

   public void p(String s) throws IOException {
      boolean canEncode = true;

      try {
         if (!this.canEncode(s)) {
            canEncode = false;
         }
      } catch (Throwable var4) {
      }

      if (!canEncode) {
         throw new GeneratorException("generator.indentingwriter.charset.cantencode", new Object[]{s});
      } else {
         this.write(s);
      }
   }

   protected boolean canEncode(String s) {
      CharsetEncoder encoder = Charset.forName(System.getProperty("file.encoding")).newEncoder();
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         if (!encoder.canEncode(chars[i])) {
            return false;
         }
      }

      return true;
   }

   public void p(String s1, String s2) throws IOException {
      this.p(s1);
      this.p(s2);
   }

   public void p(String s1, String s2, String s3) throws IOException {
      this.p(s1);
      this.p(s2);
      this.p(s3);
   }

   public void p(String s1, String s2, String s3, String s4) throws IOException {
      this.p(s1);
      this.p(s2);
      this.p(s3);
      this.p(s4);
   }

   public void p(String s1, String s2, String s3, String s4, String s5) throws IOException {
      this.p(s1);
      this.p(s2);
      this.p(s3);
      this.p(s4);
      this.p(s5);
   }

   public void pln() throws IOException {
      this.newLine();
   }

   public void pln(String s) throws IOException {
      this.p(s);
      this.pln();
   }

   public void pln(String s1, String s2) throws IOException {
      this.p(s1, s2);
      this.pln();
   }

   public void pln(String s1, String s2, String s3) throws IOException {
      this.p(s1, s2, s3);
      this.pln();
   }

   public void pln(String s1, String s2, String s3, String s4) throws IOException {
      this.p(s1, s2, s3, s4);
      this.pln();
   }

   public void pln(String s1, String s2, String s3, String s4, String s5) throws IOException {
      this.p(s1, s2, s3, s4, s5);
      this.pln();
   }

   public void plnI(String s) throws IOException {
      this.p(s);
      this.pln();
      this.pI();
   }

   public void pO(String s) throws IOException {
      this.pO();
      this.p(s);
   }

   public void pOln(String s) throws IOException {
      this.pO(s);
      this.pln();
   }

   public void pOlnI(String s) throws IOException {
      this.pO(s);
      this.pln();
      this.pI();
   }

   public void p(Object o) throws IOException {
      this.write(o.toString());
   }

   public void pln(Object o) throws IOException {
      this.p(o.toString());
      this.pln();
   }

   public void plnI(Object o) throws IOException {
      this.p(o.toString());
      this.pln();
      this.pI();
   }

   public void pO(Object o) throws IOException {
      this.pO();
      this.p(o.toString());
   }

   public void pOln(Object o) throws IOException {
      this.pO(o.toString());
      this.pln();
   }

   public void pOlnI(Object o) throws IOException {
      this.pO(o.toString());
      this.pln();
      this.pI();
   }

   public void pM(String s) throws IOException {
      int j;
      for(int i = 0; i < s.length(); i = j + 1) {
         j = s.indexOf(10, i);
         if (j == -1) {
            this.p(s.substring(i));
            break;
         }

         this.pln(s.substring(i, j));
      }

   }

   public void pMln(String s) throws IOException {
      this.pM(s);
      this.pln();
   }

   public void pMlnI(String s) throws IOException {
      this.pM(s);
      this.pln();
      this.pI();
   }

   public void pMO(String s) throws IOException {
      this.pO();
      this.pM(s);
   }

   public void pMOln(String s) throws IOException {
      this.pMO(s);
      this.pln();
   }

   public void pF(String pattern, Object[] arguments) throws IOException {
      this.pM(MessageFormat.format(pattern, arguments));
   }

   public void pFln(String pattern, Object[] arguments) throws IOException {
      this.pF(pattern, arguments);
      this.pln();
   }
}
