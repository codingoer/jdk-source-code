package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class ValueBoxEntry extends ValueEntry {
   static ValueBoxGen valueBoxGen;

   protected ValueBoxEntry() {
   }

   protected ValueBoxEntry(ValueBoxEntry var1) {
      super(var1);
   }

   protected ValueBoxEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
   }

   public Object clone() {
      return new ValueBoxEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      valueBoxGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return valueBoxGen;
   }
}
