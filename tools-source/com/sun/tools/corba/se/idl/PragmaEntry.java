package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class PragmaEntry extends SymtabEntry {
   static PragmaGen pragmaGen;
   private String _data = null;

   protected PragmaEntry() {
      this.repositoryID(Util.emptyID);
   }

   protected PragmaEntry(SymtabEntry var1) {
      super(var1, new IDLID());
      this.module(var1.name());
      this.name("");
   }

   protected PragmaEntry(PragmaEntry var1) {
      super(var1);
   }

   public Object clone() {
      return new PragmaEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      pragmaGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return pragmaGen;
   }

   public String data() {
      return this._data;
   }

   public void data(String var1) {
      this._data = var1;
   }
}
