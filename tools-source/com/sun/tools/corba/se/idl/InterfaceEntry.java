package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class InterfaceEntry extends SymtabEntry implements InterfaceType {
   private Vector _derivedFromNames = new Vector();
   private Vector _derivedFrom = new Vector();
   private Vector _methods = new Vector();
   Vector _allMethods = new Vector();
   Vector forwardedDerivers = new Vector();
   private Vector _contained = new Vector();
   private Vector _state = null;
   private int _interfaceType = 0;
   static InterfaceGen interfaceGen;

   protected InterfaceEntry() {
   }

   protected InterfaceEntry(InterfaceEntry var1) {
      super(var1);
      this._derivedFromNames = (Vector)var1._derivedFromNames.clone();
      this._derivedFrom = (Vector)var1._derivedFrom.clone();
      this._methods = (Vector)var1._methods.clone();
      this._allMethods = (Vector)var1._allMethods.clone();
      this.forwardedDerivers = (Vector)var1.forwardedDerivers.clone();
      this._contained = (Vector)var1._contained.clone();
      this._interfaceType = var1._interfaceType;
   }

   protected InterfaceEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public boolean isAbstract() {
      return this._interfaceType == 1;
   }

   public boolean isLocal() {
      return this._interfaceType == 2;
   }

   public boolean isLocalServant() {
      return this._interfaceType == 3;
   }

   public boolean isLocalSignature() {
      return this._interfaceType == 4;
   }

   public Object clone() {
      return new InterfaceEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      interfaceGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return interfaceGen;
   }

   public void addDerivedFrom(SymtabEntry var1) {
      this._derivedFrom.addElement(var1);
   }

   public Vector derivedFrom() {
      return this._derivedFrom;
   }

   public void addDerivedFromName(String var1) {
      this._derivedFromNames.addElement(var1);
   }

   public Vector derivedFromNames() {
      return this._derivedFromNames;
   }

   public void addMethod(MethodEntry var1) {
      this._methods.addElement(var1);
   }

   public Vector methods() {
      return this._methods;
   }

   public void addContained(SymtabEntry var1) {
      this._contained.addElement(var1);
   }

   public Vector contained() {
      return this._contained;
   }

   void methodsAddElement(MethodEntry var1, Scanner var2) {
      if (this.verifyMethod(var1, var2, false)) {
         this.addMethod(var1);
         this._allMethods.addElement(var1);
         this.addToForwardedAllMethods(var1, var2);
      }

   }

   void addToForwardedAllMethods(MethodEntry var1, Scanner var2) {
      Enumeration var3 = this.forwardedDerivers.elements();

      while(var3.hasMoreElements()) {
         InterfaceEntry var4 = (InterfaceEntry)var3.nextElement();
         if (var4.verifyMethod(var1, var2, true)) {
            var4._allMethods.addElement(var1);
         }
      }

   }

   private boolean verifyMethod(MethodEntry var1, Scanner var2, boolean var3) {
      boolean var4 = true;
      String var5 = var1.name().toLowerCase();
      Enumeration var6 = this._allMethods.elements();

      while(var6.hasMoreElements()) {
         MethodEntry var7 = (MethodEntry)var6.nextElement();
         String var8 = var7.name().toLowerCase();
         if (var1 != var7 && var5.equals(var8)) {
            if (var3) {
               ParseException.methodClash(var2, this.fullName(), var1.name());
            } else {
               ParseException.alreadyDeclared(var2, var1.name());
            }

            var4 = false;
            break;
         }
      }

      return var4;
   }

   void derivedFromAddElement(SymtabEntry var1, Scanner var2) {
      this.addDerivedFrom(var1);
      this.addDerivedFromName(var1.fullName());
      this.addParentType(var1, var2);
   }

   void addParentType(SymtabEntry var1, Scanner var2) {
      if (var1 instanceof ForwardEntry) {
         this.addToDerivers((ForwardEntry)var1);
      } else {
         InterfaceEntry var3 = (InterfaceEntry)var1;

         MethodEntry var5;
         for(Enumeration var4 = var3._allMethods.elements(); var4.hasMoreElements(); this.addToForwardedAllMethods(var5, var2)) {
            var5 = (MethodEntry)var4.nextElement();
            if (this.verifyMethod(var5, var2, true)) {
               this._allMethods.addElement(var5);
            }
         }

         this.lookForForwardEntrys(var2, var3);
      }

   }

   private void lookForForwardEntrys(Scanner var1, InterfaceEntry var2) {
      Enumeration var3 = var2.derivedFrom().elements();

      while(var3.hasMoreElements()) {
         SymtabEntry var4 = (SymtabEntry)var3.nextElement();
         if (var4 instanceof ForwardEntry) {
            this.addToDerivers((ForwardEntry)var4);
         } else if (var4 == var2) {
            ParseException.selfInherit(var1, var2.fullName());
         } else {
            this.lookForForwardEntrys(var1, (InterfaceEntry)var4);
         }
      }

   }

   public boolean replaceForwardDecl(ForwardEntry var1, InterfaceEntry var2) {
      int var3 = this._derivedFrom.indexOf(var1);
      if (var3 >= 0) {
         this._derivedFrom.setElementAt(var2, var3);
      }

      return var3 >= 0;
   }

   private void addToDerivers(ForwardEntry var1) {
      var1.derivers.addElement(this);
      Enumeration var2 = this.forwardedDerivers.elements();

      while(var2.hasMoreElements()) {
         var1.derivers.addElement((InterfaceEntry)var2.nextElement());
      }

   }

   public Vector state() {
      return this._state;
   }

   public void initState() {
      this._state = new Vector();
   }

   public void addStateElement(InterfaceState var1, Scanner var2) {
      if (this._state == null) {
         this._state = new Vector();
      }

      String var3 = var1.entry.name();
      Enumeration var4 = this._state.elements();

      while(var4.hasMoreElements()) {
         if (var3.equals(((InterfaceState)var4.nextElement()).entry.name())) {
            ParseException.duplicateState(var2, var3);
         }
      }

      this._state.addElement(var1);
   }

   public int getInterfaceType() {
      return this._interfaceType;
   }

   public void setInterfaceType(int var1) {
      this._interfaceType = var1;
   }

   public Vector allMethods() {
      return this._allMethods;
   }
}
