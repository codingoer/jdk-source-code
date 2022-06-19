package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class PrimitiveEntry extends SymtabEntry {
   static PrimitiveGen primitiveGen;

   protected PrimitiveEntry() {
      this.repositoryID(Util.emptyID);
   }

   protected PrimitiveEntry(String var1) {
      this.name(var1);
      this.module("");
      this.repositoryID(Util.emptyID);
   }

   protected PrimitiveEntry(PrimitiveEntry var1) {
      super(var1);
   }

   public Object clone() {
      return new PrimitiveEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      primitiveGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return primitiveGen;
   }
}
