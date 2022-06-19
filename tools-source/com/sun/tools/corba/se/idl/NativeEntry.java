package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class NativeEntry extends SymtabEntry {
   static NativeGen nativeGen;

   protected NativeEntry() {
      this.repositoryID(Util.emptyID);
   }

   protected NativeEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   protected NativeEntry(NativeEntry var1) {
      super(var1);
   }

   public Object clone() {
      return new NativeEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      nativeGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return nativeGen;
   }
}
