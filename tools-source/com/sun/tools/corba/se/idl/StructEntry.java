package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class StructEntry extends SymtabEntry {
   private Vector _members = new Vector();
   private Vector _contained = new Vector();
   static StructGen structGen;

   protected StructEntry() {
   }

   protected StructEntry(StructEntry var1) {
      super(var1);
      if (!this.name().equals("")) {
         this.module(this.module() + this.name());
         this.name("");
      }

      this._members = (Vector)var1._members.clone();
      this._contained = (Vector)var1._contained.clone();
   }

   protected StructEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new StructEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      structGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return structGen;
   }

   public void addMember(TypedefEntry var1) {
      this._members.addElement(var1);
   }

   public Vector members() {
      return this._members;
   }

   public void addContained(SymtabEntry var1) {
      this._contained.addElement(var1);
   }

   public Vector contained() {
      return this._contained;
   }
}
