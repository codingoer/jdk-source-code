package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Hashtable;

public class ConstEntry extends SymtabEntry {
   static ConstGen constGen;
   private Expression _value = null;

   protected ConstEntry() {
   }

   protected ConstEntry(ConstEntry var1) {
      super(var1);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

      this._value = var1._value;
   }

   protected ConstEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new ConstEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      constGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return constGen;
   }

   public Expression value() {
      return this._value;
   }

   public void value(Expression var1) {
      this._value = var1;
   }
}
