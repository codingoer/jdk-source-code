package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class MethodEntry extends SymtabEntry {
   private Vector _exceptionNames = new Vector();
   private Vector _exceptions = new Vector();
   private Vector _contexts = new Vector();
   private Vector _parameters = new Vector();
   private boolean _oneway = false;
   private boolean _valueMethod = false;
   static MethodGen methodGen;

   protected MethodEntry() {
   }

   protected MethodEntry(MethodEntry var1) {
      super(var1);
      this._exceptionNames = (Vector)var1._exceptionNames.clone();
      this._exceptions = (Vector)var1._exceptions.clone();
      this._contexts = (Vector)var1._contexts.clone();
      this._parameters = (Vector)var1._parameters.clone();
      this._oneway = var1._oneway;
   }

   protected MethodEntry(InterfaceEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new MethodEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      methodGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return methodGen;
   }

   public void type(SymtabEntry var1) {
      super.type(var1);
      if (var1 == null) {
         this.typeName("void");
      }

   }

   public void addException(ExceptionEntry var1) {
      this._exceptions.addElement(var1);
   }

   public Vector exceptions() {
      return this._exceptions;
   }

   public void addExceptionName(String var1) {
      this._exceptionNames.addElement(var1);
   }

   public Vector exceptionNames() {
      return this._exceptionNames;
   }

   public void addContext(String var1) {
      this._contexts.addElement(var1);
   }

   public Vector contexts() {
      return this._contexts;
   }

   public void addParameter(ParameterEntry var1) {
      this._parameters.addElement(var1);
   }

   public Vector parameters() {
      return this._parameters;
   }

   public void oneway(boolean var1) {
      this._oneway = var1;
   }

   public boolean oneway() {
      return this._oneway;
   }

   public void valueMethod(boolean var1) {
      this._valueMethod = var1;
   }

   public boolean valueMethod() {
      return this._valueMethod;
   }

   void exceptionsAddElement(ExceptionEntry var1) {
      this.addException(var1);
      this.addExceptionName(var1.fullName());
   }
}
