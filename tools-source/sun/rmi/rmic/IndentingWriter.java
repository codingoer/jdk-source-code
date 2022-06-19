package sun.rmi.rmic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class IndentingWriter extends BufferedWriter {
   private boolean beginningOfLine;
   private int currentIndent;
   private int indentStep;
   private int tabSize;

   public IndentingWriter(Writer var1) {
      super(var1);
      this.beginningOfLine = true;
      this.currentIndent = 0;
      this.indentStep = 4;
      this.tabSize = 8;
   }

   public IndentingWriter(Writer var1, int var2) {
      this(var1);
      if (this.indentStep < 0) {
         throw new IllegalArgumentException("negative indent step");
      } else {
         this.indentStep = var2;
      }
   }

   public IndentingWriter(Writer var1, int var2, int var3) {
      this(var1);
      if (this.indentStep < 0) {
         throw new IllegalArgumentException("negative indent step");
      } else {
         this.indentStep = var2;
         this.tabSize = var3;
      }
   }

   public void write(int var1) throws IOException {
      this.checkWrite();
      super.write(var1);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      if (var3 > 0) {
         this.checkWrite();
      }

      super.write(var1, var2, var3);
   }

   public void write(String var1, int var2, int var3) throws IOException {
      if (var3 > 0) {
         this.checkWrite();
      }

      super.write(var1, var2, var3);
   }

   public void newLine() throws IOException {
      super.newLine();
      this.beginningOfLine = true;
   }

   protected void checkWrite() throws IOException {
      if (this.beginningOfLine) {
         this.beginningOfLine = false;

         int var1;
         for(var1 = this.currentIndent; var1 >= this.tabSize; var1 -= this.tabSize) {
            super.write(9);
         }

         while(var1 > 0) {
            super.write(32);
            --var1;
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

   public void p(String var1) throws IOException {
      this.write(var1);
   }

   public void pln() throws IOException {
      this.newLine();
   }

   public void pln(String var1) throws IOException {
      this.p(var1);
      this.pln();
   }

   public void plnI(String var1) throws IOException {
      this.p(var1);
      this.pln();
      this.pI();
   }

   public void pO(String var1) throws IOException {
      this.pO();
      this.p(var1);
   }

   public void pOln(String var1) throws IOException {
      this.pO(var1);
      this.pln();
   }

   public void pOlnI(String var1) throws IOException {
      this.pO(var1);
      this.pln();
      this.pI();
   }

   public void p(Object var1) throws IOException {
      this.write(var1.toString());
   }

   public void pln(Object var1) throws IOException {
      this.p(var1.toString());
      this.pln();
   }

   public void plnI(Object var1) throws IOException {
      this.p(var1.toString());
      this.pln();
      this.pI();
   }

   public void pO(Object var1) throws IOException {
      this.pO();
      this.p(var1.toString());
   }

   public void pOln(Object var1) throws IOException {
      this.pO(var1.toString());
      this.pln();
   }

   public void pOlnI(Object var1) throws IOException {
      this.pO(var1.toString());
      this.pln();
      this.pI();
   }
}
