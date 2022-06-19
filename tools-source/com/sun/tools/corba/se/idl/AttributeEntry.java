package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class AttributeEntry extends MethodEntry {
   static AttributeGen attributeGen;
   public boolean _readOnly = false;

   protected AttributeEntry() {
   }

   protected AttributeEntry(AttributeEntry var1) {
      super(var1);
      this._readOnly = var1._readOnly;
   }

   protected AttributeEntry(InterfaceEntry var1, IDLID var2) {
      super(var1, var2);
   }

   public Object clone() {
      return new AttributeEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      attributeGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return attributeGen;
   }

   public boolean readOnly() {
      return this._readOnly;
   }

   public void readOnly(boolean var1) {
      this._readOnly = var1;
   }
}
