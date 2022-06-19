package com.sun.tools.javap;

import com.sun.tools.classfile.AttributeException;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.DescriptorException;
import java.io.PrintWriter;

public class BasicWriter {
   private String[] spaces = new String[80];
   private LineWriter lineWriter;
   private PrintWriter out;
   protected Messages messages;

   protected BasicWriter(Context var1) {
      this.lineWriter = BasicWriter.LineWriter.instance(var1);
      this.out = (PrintWriter)var1.get(PrintWriter.class);
      this.messages = (Messages)var1.get(Messages.class);
      if (this.messages == null) {
         throw new AssertionError();
      }
   }

   protected void print(String var1) {
      this.lineWriter.print(var1);
   }

   protected void print(Object var1) {
      this.lineWriter.print(var1 == null ? null : var1.toString());
   }

   protected void println() {
      this.lineWriter.println();
   }

   protected void println(String var1) {
      this.lineWriter.print(var1);
      this.lineWriter.println();
   }

   protected void println(Object var1) {
      this.lineWriter.print(var1 == null ? null : var1.toString());
      this.lineWriter.println();
   }

   protected void indent(int var1) {
      this.lineWriter.indent(var1);
   }

   protected void tab() {
      this.lineWriter.tab();
   }

   protected void setPendingNewline(boolean var1) {
      this.lineWriter.pendingNewline = var1;
   }

   protected String report(AttributeException var1) {
      this.out.println("Error: " + var1.getMessage());
      return "???";
   }

   protected String report(ConstantPoolException var1) {
      this.out.println("Error: " + var1.getMessage());
      return "???";
   }

   protected String report(DescriptorException var1) {
      this.out.println("Error: " + var1.getMessage());
      return "???";
   }

   protected String report(String var1) {
      this.out.println("Error: " + var1);
      return "???";
   }

   protected String space(int var1) {
      if (var1 < this.spaces.length && this.spaces[var1] != null) {
         return this.spaces[var1];
      } else {
         StringBuilder var2 = new StringBuilder();

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.append(" ");
         }

         String var4 = var2.toString();
         if (var1 < this.spaces.length) {
            this.spaces[var1] = var4;
         }

         return var4;
      }
   }

   private static class LineWriter {
      private final PrintWriter out;
      private final StringBuilder buffer;
      private int indentCount;
      private final int indentWidth;
      private final int tabColumn;
      private boolean pendingNewline;
      private int pendingSpaces;

      static LineWriter instance(Context var0) {
         LineWriter var1 = (LineWriter)var0.get(LineWriter.class);
         if (var1 == null) {
            var1 = new LineWriter(var0);
         }

         return var1;
      }

      protected LineWriter(Context var1) {
         var1.put(LineWriter.class, this);
         Options var2 = Options.instance(var1);
         this.indentWidth = var2.indentWidth;
         this.tabColumn = var2.tabColumn;
         this.out = (PrintWriter)var1.get(PrintWriter.class);
         this.buffer = new StringBuilder();
      }

      protected void print(String var1) {
         if (this.pendingNewline) {
            this.println();
            this.pendingNewline = false;
         }

         if (var1 == null) {
            var1 = "null";
         }

         for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            switch (var3) {
               case '\n':
                  this.println();
                  break;
               case ' ':
                  ++this.pendingSpaces;
                  break;
               default:
                  if (this.buffer.length() == 0) {
                     this.indent();
                  }

                  if (this.pendingSpaces > 0) {
                     for(int var4 = 0; var4 < this.pendingSpaces; ++var4) {
                        this.buffer.append(' ');
                     }

                     this.pendingSpaces = 0;
                  }

                  this.buffer.append(var3);
            }
         }

      }

      protected void println() {
         this.pendingSpaces = 0;
         this.out.println(this.buffer);
         this.buffer.setLength(0);
      }

      protected void indent(int var1) {
         this.indentCount += var1;
      }

      protected void tab() {
         int var1 = this.indentCount * this.indentWidth + this.tabColumn;
         this.pendingSpaces += var1 <= this.buffer.length() ? 1 : var1 - this.buffer.length();
      }

      private void indent() {
         this.pendingSpaces += this.indentCount * this.indentWidth;
      }
   }
}
