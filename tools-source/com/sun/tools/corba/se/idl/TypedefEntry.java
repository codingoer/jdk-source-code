package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class TypedefEntry extends SymtabEntry {
   private Vector _arrayInfo = new Vector();
   static TypedefGen typedefGen;

   protected TypedefEntry() {
   }

   protected TypedefEntry(TypedefEntry var1) {
      super(var1);
      this._arrayInfo = (Vector)var1._arrayInfo.clone();
   }

   protected TypedefEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Vector arrayInfo() {
      return this._arrayInfo;
   }

   public void addArrayInfo(Expression var1) {
      this._arrayInfo.addElement(var1);
   }

   public Object clone() {
      return new TypedefEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      typedefGen.generate(var1, this, var2);
   }

   public boolean isReferencable() {
      return this.type().isReferencable();
   }

   public void isReferencable(boolean var1) {
   }

   public Generator generator() {
      return typedefGen;
   }
}
