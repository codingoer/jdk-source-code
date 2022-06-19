package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class ModuleEntry extends SymtabEntry {
   private Vector _contained = new Vector();
   static ModuleGen moduleGen;

   protected ModuleEntry() {
   }

   protected ModuleEntry(ModuleEntry var1) {
      super(var1);
      this._contained = (Vector)var1._contained.clone();
   }

   protected ModuleEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new ModuleEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      moduleGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return moduleGen;
   }

   public void addContained(SymtabEntry var1) {
      this._contained.addElement(var1);
   }

   public Vector contained() {
      return this._contained;
   }
}
