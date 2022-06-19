package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class EnumEntry extends SymtabEntry {
   static EnumGen enumGen;
   private Vector _elements = new Vector();

   protected EnumEntry() {
   }

   protected EnumEntry(EnumEntry var1) {
      super(var1);
      this._elements = (Vector)var1._elements.clone();
   }

   protected EnumEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new EnumEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      enumGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return enumGen;
   }

   public void addElement(String var1) {
      this._elements.addElement(var1);
   }

   public Vector elements() {
      return this._elements;
   }
}
