package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ForwardEntry extends SymtabEntry implements InterfaceType {
   static ForwardGen forwardGen;
   Vector derivers = new Vector();
   Vector types = new Vector();
   private int _type = 0;

   protected ForwardEntry() {
   }

   protected ForwardEntry(ForwardEntry var1) {
      super(var1);
   }

   protected ForwardEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (this.module().equals("")) {
         this.module(this.name());
      } else if (!this.name().equals("")) {
         this.module(this.module() + "/" + this.name());
      }

   }

   public Object clone() {
      return new ForwardEntry(this);
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      forwardGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return forwardGen;
   }

   static boolean replaceForwardDecl(InterfaceEntry var0) {
      boolean var1 = true;

      try {
         ForwardEntry var2 = (ForwardEntry)Parser.symbolTable.get(var0.fullName());
         if (var2 != null) {
            var1 = var0.getInterfaceType() == var2.getInterfaceType();
            var2.type(var0);
            var0.forwardedDerivers = var2.derivers;
            Enumeration var3 = var2.derivers.elements();

            while(var3.hasMoreElements()) {
               ((InterfaceEntry)var3.nextElement()).replaceForwardDecl(var2, var0);
            }

            var3 = var2.types.elements();

            while(var3.hasMoreElements()) {
               ((SymtabEntry)var3.nextElement()).type(var0);
            }
         }
      } catch (Exception var4) {
      }

      return var1;
   }

   public int getInterfaceType() {
      return this._type;
   }

   public void setInterfaceType(int var1) {
      this._type = var1;
   }
}
