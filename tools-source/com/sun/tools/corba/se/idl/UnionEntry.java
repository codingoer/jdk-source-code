package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class UnionEntry extends SymtabEntry {
   private Vector _branches = new Vector();
   private TypedefEntry _defaultBranch = null;
   private Vector _contained = new Vector();
   static UnionGen unionGen;

   protected UnionEntry() {
   }

   protected UnionEntry(UnionEntry var1) {
      super(var1);
      if (!this.name().equals("")) {
         this.module(this.module() + this.name());
         this.name("");
      }

      this._branches = (Vector)var1._branches.clone();
      this._defaultBranch = var1._defaultBranch;
      this._contained = var1._contained;
   }

   protected UnionEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new UnionEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      unionGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return unionGen;
   }

   public void addBranch(UnionBranch var1) {
      this._branches.addElement(var1);
   }

   public Vector branches() {
      return this._branches;
   }

   public void defaultBranch(TypedefEntry var1) {
      this._defaultBranch = var1;
   }

   public TypedefEntry defaultBranch() {
      return this._defaultBranch;
   }

   public void addContained(SymtabEntry var1) {
      this._contained.addElement(var1);
   }

   public Vector contained() {
      return this._contained;
   }

   boolean has(Expression var1) {
      Enumeration var2 = this._branches.elements();

      while(var2.hasMoreElements()) {
         Enumeration var3 = ((UnionBranch)var2.nextElement()).labels.elements();

         while(var3.hasMoreElements()) {
            Expression var4 = (Expression)var3.nextElement();
            if (var4.equals(var1) || var4.value().equals(var1.value())) {
               return true;
            }
         }
      }

      return false;
   }

   boolean has(TypedefEntry var1) {
      Enumeration var2 = this._branches.elements();

      UnionBranch var3;
      do {
         if (!var2.hasMoreElements()) {
            return false;
         }

         var3 = (UnionBranch)var2.nextElement();
      } while(var3.typedef.equals(var1) || !var3.typedef.name().equals(var1.name()));

      return true;
   }
}
