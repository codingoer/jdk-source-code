package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SymtabEntry {
   static Stack includeStack = new Stack();
   static boolean setEmit = true;
   static int maxKey = -1;
   private SymtabEntry _container = null;
   private String _module = "";
   private String _name = "";
   private String _typeName = "";
   private SymtabEntry _type = null;
   private IncludeEntry _sourceFile = null;
   private Object _info = null;
   private RepositoryID _repID = new IDLID("", "", "1.0");
   private boolean _emit;
   private Comment _comment;
   private Vector _dynamicVars;
   private boolean _isReferencable;

   public SymtabEntry() {
      this._emit = setEmit;
      this._comment = null;
      this._isReferencable = true;
      this.initDynamicVars();
   }

   SymtabEntry(SymtabEntry var1, IDLID var2) {
      this._emit = setEmit;
      this._comment = null;
      this._isReferencable = true;
      this._module = var1._module;
      this._name = var1._name;
      this._type = var1._type;
      this._typeName = var1._typeName;
      this._sourceFile = var1._sourceFile;
      this._info = var1._info;
      this._repID = (RepositoryID)var2.clone();
      ((IDLID)this._repID).appendToName(this._name);
      if (!(var1 instanceof InterfaceEntry) && !(var1 instanceof ModuleEntry) && !(var1 instanceof StructEntry) && !(var1 instanceof UnionEntry) && (!(var1 instanceof SequenceEntry) || !(this instanceof SequenceEntry))) {
         this._container = var1._container;
      } else {
         this._container = var1;
      }

      this.initDynamicVars();
      this._comment = var1._comment;
   }

   SymtabEntry(SymtabEntry var1) {
      this._emit = setEmit;
      this._comment = null;
      this._isReferencable = true;
      this._module = var1._module;
      this._name = var1._name;
      this._type = var1._type;
      this._typeName = var1._typeName;
      this._sourceFile = var1._sourceFile;
      this._info = var1._info;
      this._repID = (RepositoryID)var1._repID.clone();
      this._container = var1._container;
      if (this._type instanceof ForwardEntry) {
         ((ForwardEntry)this._type).types.addElement(this);
      }

      this.initDynamicVars();
      this._comment = var1._comment;
   }

   void initDynamicVars() {
      this._dynamicVars = new Vector(maxKey + 1);

      for(int var1 = 0; var1 <= maxKey; ++var1) {
         this._dynamicVars.addElement((Object)null);
      }

   }

   public Object clone() {
      return new SymtabEntry(this);
   }

   public final String fullName() {
      return this._module.equals("") ? this._name : this._module + '/' + this._name;
   }

   public String module() {
      return this._module;
   }

   public void module(String var1) {
      if (var1 == null) {
         this._module = "";
      } else {
         this._module = var1;
      }

   }

   public String name() {
      return this._name;
   }

   public void name(String var1) {
      if (var1 == null) {
         this._name = "";
      } else {
         this._name = var1;
      }

      if (this._repID instanceof IDLID) {
         ((IDLID)this._repID).replaceName(var1);
      }

   }

   public String typeName() {
      return this._typeName;
   }

   protected void typeName(String var1) {
      this._typeName = var1;
   }

   public SymtabEntry type() {
      return this._type;
   }

   public void type(SymtabEntry var1) {
      if (var1 == null) {
         this.typeName("");
      } else {
         this.typeName(var1.fullName());
      }

      this._type = var1;
      if (this._type instanceof ForwardEntry) {
         ((ForwardEntry)this._type).types.addElement(this);
      }

   }

   public IncludeEntry sourceFile() {
      return this._sourceFile;
   }

   public void sourceFile(IncludeEntry var1) {
      this._sourceFile = var1;
   }

   public SymtabEntry container() {
      return this._container;
   }

   public void container(SymtabEntry var1) {
      if (var1 instanceof InterfaceEntry || var1 instanceof ModuleEntry) {
         this._container = var1;
      }

   }

   public RepositoryID repositoryID() {
      return this._repID;
   }

   public void repositoryID(RepositoryID var1) {
      this._repID = var1;
   }

   public boolean emit() {
      return this._emit && this._isReferencable;
   }

   public void emit(boolean var1) {
      this._emit = var1;
   }

   public Comment comment() {
      return this._comment;
   }

   public void comment(Comment var1) {
      this._comment = var1;
   }

   public boolean isReferencable() {
      return this._isReferencable;
   }

   public void isReferencable(boolean var1) {
      this._isReferencable = var1;
   }

   static void enteringInclude() {
      includeStack.push(new Boolean(setEmit));
      setEmit = false;
   }

   static void exitingInclude() {
      setEmit = (Boolean)includeStack.pop();
   }

   public static int getVariableKey() {
      return ++maxKey;
   }

   public void dynamicVariable(int var1, Object var2) throws NoSuchFieldException {
      if (var1 > maxKey) {
         throw new NoSuchFieldException(Integer.toString(var1));
      } else {
         if (var1 >= this._dynamicVars.size()) {
            this.growVars();
         }

         this._dynamicVars.setElementAt(var2, var1);
      }
   }

   public Object dynamicVariable(int var1) throws NoSuchFieldException {
      if (var1 > maxKey) {
         throw new NoSuchFieldException(Integer.toString(var1));
      } else {
         if (var1 >= this._dynamicVars.size()) {
            this.growVars();
         }

         return this._dynamicVars.elementAt(var1);
      }
   }

   void growVars() {
      int var1 = maxKey - this._dynamicVars.size() + 1;

      for(int var2 = 0; var2 < var1; ++var2) {
         this._dynamicVars.addElement((Object)null);
      }

   }

   public void generate(Hashtable var1, PrintWriter var2) {
   }

   public Generator generator() {
      return null;
   }
}
