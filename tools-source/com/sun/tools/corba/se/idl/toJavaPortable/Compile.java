package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.IncludeEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.InvalidArgument;
import com.sun.tools.corba.se.idl.ModuleEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.SymtabFactory;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionBranch;
import com.sun.tools.corba.se.idl.UnionEntry;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Compile extends com.sun.tools.corba.se.idl.Compile {
   public Factories _factories = new Factories();
   ModuleEntry org;
   ModuleEntry omg;
   ModuleEntry corba;
   InterfaceEntry object;
   public Vector importTypes = new Vector();
   public SymtabFactory factory = this.factories().symtabFactory();
   public static int typedefInfo;
   public Hashtable list = new Hashtable();
   public static Compile compiler = null;

   public static void main(String[] var0) {
      compiler = new Compile();
      compiler.start(var0);
   }

   public void start(String[] var1) {
      try {
         Util.registerMessageFile("com/sun/tools/corba/se/idl/toJavaPortable/toJavaPortable.prp");
         this.init(var1);
         if (this.arguments.versionRequest) {
            this.displayVersion();
         } else {
            this.preParse();
            Enumeration var2 = this.parse();
            if (var2 != null) {
               this.preEmit(var2);
               this.generate();
            }
         }
      } catch (InvalidArgument var3) {
         System.err.println(var3);
      } catch (IOException var4) {
         System.err.println(var4);
      }

   }

   protected Compile() {
   }

   protected com.sun.tools.corba.se.idl.Factories factories() {
      return this._factories;
   }

   protected void preParse() {
      Util.setSymbolTable(this.symbolTable);
      Util.setPackageTranslation(((Arguments)this.arguments).packageTranslation);
      this.org = this.factory.moduleEntry();
      this.org.emit(false);
      this.org.name("org");
      this.org.container((SymtabEntry)null);
      this.omg = this.factory.moduleEntry();
      this.omg.emit(false);
      this.omg.name("omg");
      this.omg.module("org");
      this.omg.container(this.org);
      this.org.addContained(this.omg);
      this.corba = this.factory.moduleEntry();
      this.corba.emit(false);
      this.corba.name("CORBA");
      this.corba.module("org/omg");
      this.corba.container(this.omg);
      this.omg.addContained(this.corba);
      this.symbolTable.put("org", this.org);
      this.symbolTable.put("org/omg", this.omg);
      this.symbolTable.put("org/omg/CORBA", this.corba);
      this.object = (InterfaceEntry)this.symbolTable.get("Object");
      this.object.module("org/omg/CORBA");
      this.object.container(this.corba);
      this.symbolTable.put("org/omg/CORBA/Object", this.object);
      PrimitiveEntry var1 = this.factory.primitiveEntry();
      var1.name("TypeCode");
      var1.module("org/omg/CORBA");
      var1.container(this.corba);
      this.symbolTable.put("org/omg/CORBA/TypeCode", var1);
      this.symbolTable.put("CORBA/TypeCode", var1);
      this.overrideNames.put("CORBA/TypeCode", "org/omg/CORBA/TypeCode");
      this.overrideNames.put("org/omg/CORBA/TypeCode", "CORBA/TypeCode");
      var1 = this.factory.primitiveEntry();
      var1.name("Principal");
      var1.module("org/omg/CORBA");
      var1.container(this.corba);
      this.symbolTable.put("org/omg/CORBA/Principle", var1);
      this.symbolTable.put("CORBA/Principal", var1);
      this.overrideNames.put("CORBA/Principal", "org/omg/CORBA/Principal");
      this.overrideNames.put("org/omg/CORBA/Principal", "CORBA/Principal");
      this.overrideNames.put("TRUE", "true");
      this.overrideNames.put("FALSE", "false");
      this.symbolTable.put("CORBA", this.corba);
      this.overrideNames.put("CORBA", "org/omg/CORBA");
      this.overrideNames.put("org/omg/CORBA", "CORBA");
   }

   protected void preEmit(Enumeration var1) {
      typedefInfo = SymtabEntry.getVariableKey();
      Hashtable var2 = (Hashtable)this.symbolTable.clone();
      Enumeration var3 = var2.elements();

      SymtabEntry var4;
      while(var3.hasMoreElements()) {
         var4 = (SymtabEntry)var3.nextElement();
         this.preEmitSTElement(var4);
      }

      var3 = this.symbolTable.elements();

      while(var3.hasMoreElements()) {
         var4 = (SymtabEntry)var3.nextElement();
         if (!(var4 instanceof TypedefEntry) && !(var4 instanceof SequenceEntry)) {
            Enumeration var5;
            if (var4 instanceof StructEntry) {
               var5 = ((StructEntry)var4).members().elements();

               while(var5.hasMoreElements()) {
                  Util.fillInfo((SymtabEntry)var5.nextElement());
               }
            } else if (var4 instanceof InterfaceEntry && ((InterfaceEntry)var4).state() != null) {
               var5 = ((InterfaceEntry)var4).state().elements();

               while(var5.hasMoreElements()) {
                  Util.fillInfo(((InterfaceState)var5.nextElement()).entry);
               }
            } else if (var4 instanceof UnionEntry) {
               var5 = ((UnionEntry)var4).branches().elements();

               while(var5.hasMoreElements()) {
                  Util.fillInfo(((UnionBranch)var5.nextElement()).typedef);
               }
            }
         } else {
            Util.fillInfo(var4);
         }

         if (var4.module().equals("") && !(var4 instanceof ModuleEntry) && !(var4 instanceof IncludeEntry) && !(var4 instanceof PrimitiveEntry)) {
            this.importTypes.addElement(var4);
         }
      }

      while(var1.hasMoreElements()) {
         var4 = (SymtabEntry)var1.nextElement();
         this.preEmitELElement(var4);
      }

   }

   protected void preEmitSTElement(SymtabEntry var1) {
      Hashtable var2 = ((Arguments)this.arguments).packages;
      if (var2.size() > 0) {
         String var3 = (String)var2.get(var1.fullName());
         if (var3 != null) {
            String var4 = null;
            ModuleEntry var5 = null;

            for(ModuleEntry var6 = null; var3 != null; var6 = var5) {
               int var7 = var3.indexOf(46);
               if (var7 < 0) {
                  var4 = var3;
                  var3 = null;
               } else {
                  var4 = var3.substring(0, var7);
                  var3 = var3.substring(var7 + 1);
               }

               String var8 = var6 == null ? var4 : var6.fullName() + '/' + var4;
               var5 = (ModuleEntry)this.symbolTable.get(var8);
               if (var5 == null) {
                  var5 = this.factory.moduleEntry();
                  var5.name(var4);
                  var5.container(var6);
                  if (var6 != null) {
                     var5.module(var6.fullName());
                  }

                  this.symbolTable.put(var4, var5);
               }
            }

            var1.module(var5.fullName());
            var1.container(var5);
         }
      }

   }

   protected void preEmitELElement(SymtabEntry var1) {
   }
}
