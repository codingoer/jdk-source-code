package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ValueEntry extends InterfaceEntry {
   private Vector _supportsNames = new Vector();
   private Vector _supports = new Vector();
   private Vector _initializers = new Vector();
   private boolean _custom = false;
   private boolean _isSafe = false;
   static ValueGen valueGen;

   protected ValueEntry() {
   }

   protected ValueEntry(ValueEntry var1) {
      super(var1);
      this._supportsNames = (Vector)var1._supportsNames.clone();
      this._supports = (Vector)var1._supports.clone();
      this._initializers = (Vector)var1._initializers.clone();
      this._custom = var1._custom;
      this._isSafe = var1._isSafe;
   }

   protected ValueEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
   }

   public Object clone() {
      return new ValueEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      valueGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return valueGen;
   }

   public void addSupport(SymtabEntry var1) {
      this._supports.addElement(var1);
   }

   public Vector supports() {
      return this._supports;
   }

   public void addSupportName(String var1) {
      this._supportsNames.addElement(var1);
   }

   public Vector supportsNames() {
      return this._supportsNames;
   }

   void derivedFromAddElement(SymtabEntry var1, boolean var2, Scanner var3) {
      if (((InterfaceType)var1).getInterfaceType() != 1) {
         if (this.isAbstract()) {
            ParseException.nonAbstractParent2(var3, this.fullName(), var1.fullName());
         } else if (this.derivedFrom().size() > 0) {
            ParseException.nonAbstractParent3(var3, this.fullName(), var1.fullName());
         }
      }

      if (this.derivedFrom().contains(var1)) {
         ParseException.alreadyDerived(var3, var1.fullName(), this.fullName());
      }

      if (var2) {
         this._isSafe = true;
      }

      this.addDerivedFrom(var1);
      this.addDerivedFromName(var1.fullName());
      this.addParentType(var1, var3);
   }

   void derivedFromAddElement(SymtabEntry var1, Scanner var2) {
      this.addSupport(var1);
      this.addSupportName(var1.fullName());
      this.addParentType(var1, var2);
   }

   public boolean replaceForwardDecl(ForwardEntry var1, InterfaceEntry var2) {
      if (super.replaceForwardDecl(var1, var2)) {
         return true;
      } else {
         int var3 = this._supports.indexOf(var1);
         if (var3 >= 0) {
            this._supports.setElementAt(var2, var3);
         }

         return var3 >= 0;
      }
   }

   void initializersAddElement(MethodEntry var1, Scanner var2) {
      Vector var3 = var1.parameters();
      int var4 = var3.size();
      Enumeration var5 = this._initializers.elements();

      while(true) {
         Vector var6;
         do {
            if (!var5.hasMoreElements()) {
               this._initializers.addElement(var1);
               return;
            }

            var6 = ((MethodEntry)var5.nextElement()).parameters();
         } while(var4 != var6.size());

         int var7;
         for(var7 = 0; var7 < var4 && ((ParameterEntry)var3.elementAt(var7)).type().equals(((ParameterEntry)var6.elementAt(var7)).type()); ++var7) {
         }

         if (var7 >= var4) {
            ParseException.duplicateInit(var2);
         }
      }
   }

   public Vector initializers() {
      return this._initializers;
   }

   public void tagMethods() {
      Enumeration var1 = this.methods().elements();

      while(var1.hasMoreElements()) {
         ((MethodEntry)var1.nextElement()).valueMethod(true);
      }

   }

   public boolean isCustom() {
      return this._custom;
   }

   public void setCustom(boolean var1) {
      this._custom = var1;
   }

   public boolean isSafe() {
      return this._isSafe;
   }
}
