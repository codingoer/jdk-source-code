package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public class ForwardValueEntry extends ForwardEntry {
   static ForwardValueGen forwardValueGen;

   protected ForwardValueEntry() {
   }

   protected ForwardValueEntry(ForwardValueEntry var1) {
      super(var1);
   }

   protected ForwardValueEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
   }

   public Object clone() {
      return new ForwardValueEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      forwardValueGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return forwardValueGen;
   }
}
