package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class ExceptionEntry extends StructEntry {
   static ExceptionGen exceptionGen;

   protected ExceptionEntry() {
   }

   protected ExceptionEntry(ExceptionEntry var1) {
      super(var1);
   }

   protected ExceptionEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
   }

   public Object clone() {
      return new ExceptionEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      exceptionGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return exceptionGen;
   }
}
