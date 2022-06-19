package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class ParameterEntry extends SymtabEntry {
   public static final int In = 0;
   public static final int Inout = 1;
   public static final int Out = 2;
   private int _passType = 0;
   static ParameterGen parameterGen;

   protected ParameterEntry() {
   }

   protected ParameterEntry(ParameterEntry var1) {
      super(var1);
      this._passType = var1._passType;
   }

   protected ParameterEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new ParameterEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      parameterGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return parameterGen;
   }

   public void passType(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         this._passType = var1;
      }

   }

   public int passType() {
      return this._passType;
   }
}
