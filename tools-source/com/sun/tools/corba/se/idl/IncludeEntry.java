package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class IncludeEntry extends SymtabEntry {
   static IncludeGen includeGen;
   private Vector includeList = new Vector();
   private String _absFilename = null;

   protected IncludeEntry() {
      this.repositoryID(Util.emptyID);
   }

   protected IncludeEntry(SymtabEntry var1) {
      super(var1, new IDLID());
      this.module(var1.name());
      this.name("");
   }

   protected IncludeEntry(IncludeEntry var1) {
      super(var1);
   }

   public Object clone() {
      return new IncludeEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      includeGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return includeGen;
   }

   public void absFilename(String var1) {
      this._absFilename = var1;
   }

   public String absFilename() {
      return this._absFilename;
   }

   public void addInclude(IncludeEntry var1) {
      this.includeList.addElement(var1);
   }

   public Vector includes() {
      return this.includeList;
   }
}
