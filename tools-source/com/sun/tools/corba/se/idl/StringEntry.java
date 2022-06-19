package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Hashtable;

public class StringEntry extends SymtabEntry {
   static StringGen stringGen;
   private Expression _maxSize = null;

   protected StringEntry() {
      String var1 = (String)Parser.overrideNames.get("string");
      if (var1 == null) {
         this.name("string");
      } else {
         this.name(var1);
      }

      this.repositoryID(Util.emptyID);
   }

   protected StringEntry(StringEntry var1) {
      super(var1);
      this._maxSize = var1._maxSize;
   }

   protected StringEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      this.module("");
      String var3 = (String)Parser.overrideNames.get("string");
      if (var3 == null) {
         this.name("string");
      } else {
         this.name(var3);
      }

      this.repositoryID(Util.emptyID);
   }

   public Object clone() {
      return new StringEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      stringGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return stringGen;
   }

   public void maxSize(Expression var1) {
      this._maxSize = var1;
   }

   public Expression maxSize() {
      return this._maxSize;
   }
}
