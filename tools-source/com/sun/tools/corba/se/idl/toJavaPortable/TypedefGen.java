package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionEntry;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class TypedefGen implements com.sun.tools.corba.se.idl.TypedefGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected TypedefEntry t = null;

   public void generate(Hashtable var1, TypedefEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.t = var2;
      if (var2.arrayInfo().size() > 0 || var2.type() instanceof SequenceEntry) {
         this.generateHolder();
      }

      this.generateHelper();
   }

   protected void generateHolder() {
      ((Factories)Compile.compiler.factories()).holder().generate(this.symbolTable, this.t);
   }

   protected void generateHelper() {
      ((Factories)Compile.compiler.factories()).helper().generate(this.symbolTable, this.t);
   }

   private boolean inStruct(TypedefEntry var1) {
      boolean var2 = false;
      if (!(var1.container() instanceof StructEntry) && !(var1.container() instanceof UnionEntry)) {
         if (var1.container() instanceof InterfaceEntry) {
            InterfaceEntry var3 = (InterfaceEntry)var1.container();
            if (var3.state() != null) {
               Enumeration var4 = var3.state().elements();

               while(var4.hasMoreElements()) {
                  if (((InterfaceState)var4.nextElement()).entry == var1) {
                     var2 = true;
                     break;
                  }
               }
            }
         }
      } else {
         var2 = true;
      }

      return var2;
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      TypedefEntry var7 = (TypedefEntry)var5;
      boolean var8 = this.inStruct(var7);
      if (var8) {
         var3.setMember(var5);
      } else {
         var3.set(var5);
      }

      var1 = ((JavaGenerator)var7.type().generator()).type(var1, var2, var3, var4, var7.type(), var6);
      if (var8 && var7.arrayInfo().size() != 0) {
         var3.bumpCurrentOffset(4);
      }

      int var9 = var7.arrayInfo().size();

      for(int var10 = 0; var10 < var9; ++var10) {
         String var11 = Util.parseExpression((Expression)var7.arrayInfo().elementAt(var10));
         var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_array_tc (" + var11 + ", " + var4 + " );");
      }

      if (!var8) {
         var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_alias_tc (" + Util.helperName(var7, true) + ".id (), \"" + Util.stripLeadingUnderscores(var7.name()) + "\", " + var4 + ");");
      }

      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      return this.helperType(var1, var2, var3, var4, var5, var6);
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      Util.writeInitializer("    ", "value", "", var2, var3);
      this.read(0, "    ", "value", var2, var3);
      var3.println("    return value;");
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      this.write(0, "    ", "value", var1, var2);
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      TypedefEntry var6 = (TypedefEntry)var4;
      String var7 = Util.arrayInfo(var6.arrayInfo());
      if (!var7.equals("")) {
         int var8 = 0;
         String var9 = "";

         String var10;
         try {
            var10 = (String)var6.dynamicVariable(Compile.typedefInfo);
         } catch (NoSuchFieldException var17) {
            var10 = var6.name();
         }

         int var11 = var10.indexOf(91);
         String var12 = Util.sansArrayInfo(var10.substring(var11)) + "[]";
         var10 = var10.substring(0, var11);
         SymtabEntry var13 = (SymtabEntry)Util.symbolTable.get(var10.replace('.', '/'));
         if (var13 instanceof InterfaceEntry && ((InterfaceEntry)var13).state() != null) {
            var10 = Util.javaName((InterfaceEntry)var13);
         }

         int var14;
         int var15;
         while(!var7.equals("")) {
            var15 = var7.indexOf(93);
            String var16 = var7.substring(1, var15);
            var14 = var12.indexOf(93);
            var12 = '[' + var16 + var12.substring(var14 + 2);
            var5.println(var2 + var3 + " = new " + var10 + var12 + ';');
            var9 = "_o" + var1++;
            var5.println(var2 + "for (int " + var9 + " = 0;" + var9 + " < (" + var16 + "); ++" + var9 + ')');
            var5.println(var2 + '{');
            ++var8;
            var7 = var7.substring(var15 + 1);
            var2 = var2 + "  ";
            var3 = var3 + '[' + var9 + ']';
         }

         var14 = var12.indexOf(93);
         if (!(var6.type() instanceof SequenceEntry) && !(var6.type() instanceof PrimitiveEntry) && !(var6.type() instanceof StringEntry)) {
            if (var6.type() instanceof InterfaceEntry && var6.type().fullName().equals("org/omg/CORBA/Object")) {
               var5.println(var2 + var3 + " = istream.read_Object ();");
            } else {
               var5.println(var2 + var3 + " = " + Util.helperName(var6.type(), true) + ".read (istream);");
            }
         } else {
            var1 = ((JavaGenerator)var6.type().generator()).read(var1, var2, var3, var6.type(), var5);
         }

         for(var15 = 0; var15 < var8; ++var15) {
            var2 = var2.substring(2);
            var5.println(var2 + '}');
         }
      } else {
         SymtabEntry var18 = Util.typeOf(var6.type());
         if (!(var18 instanceof SequenceEntry) && !(var18 instanceof PrimitiveEntry) && !(var18 instanceof StringEntry)) {
            if (var18 instanceof InterfaceEntry && var18.fullName().equals("org/omg/CORBA/Object")) {
               var5.println(var2 + var3 + " = istream.read_Object ();");
            } else {
               var5.println(var2 + var3 + " = " + Util.helperName(var18, true) + ".read (istream);");
            }
         } else {
            var1 = ((JavaGenerator)var18.generator()).read(var1, var2, var3, var18, var5);
         }
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      TypedefEntry var6 = (TypedefEntry)var4;
      String var7 = Util.arrayInfo(var6.arrayInfo());
      if (!var7.equals("")) {
         int var8 = 0;

         int var10;
         for(String var9 = ""; !var7.equals(""); var3 = var3 + '[' + var9 + ']') {
            var10 = var7.indexOf(93);
            String var11 = var7.substring(1, var10);
            var5.println(var2 + "if (" + var3 + ".length != (" + var11 + "))");
            var5.println(var2 + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
            var9 = "_i" + var1++;
            var5.println(var2 + "for (int " + var9 + " = 0;" + var9 + " < (" + var11 + "); ++" + var9 + ')');
            var5.println(var2 + '{');
            ++var8;
            var7 = var7.substring(var10 + 1);
            var2 = var2 + "  ";
         }

         if (!(var6.type() instanceof SequenceEntry) && !(var6.type() instanceof PrimitiveEntry) && !(var6.type() instanceof StringEntry)) {
            if (var6.type() instanceof InterfaceEntry && var6.type().fullName().equals("org/omg/CORBA/Object")) {
               var5.println(var2 + "ostream.write_Object (" + var3 + ");");
            } else {
               var5.println(var2 + Util.helperName(var6.type(), true) + ".write (ostream, " + var3 + ");");
            }
         } else {
            var1 = ((JavaGenerator)var6.type().generator()).write(var1, var2, var3, var6.type(), var5);
         }

         for(var10 = 0; var10 < var8; ++var10) {
            var2 = var2.substring(2);
            var5.println(var2 + '}');
         }
      } else {
         SymtabEntry var12 = Util.typeOf(var6.type());
         if (!(var12 instanceof SequenceEntry) && !(var12 instanceof PrimitiveEntry) && !(var12 instanceof StringEntry)) {
            if (var12 instanceof InterfaceEntry && var12.fullName().equals("org/omg/CORBA/Object")) {
               var5.println(var2 + "ostream.write_Object (" + var3 + ");");
            } else {
               var5.println(var2 + Util.helperName(var12, true) + ".write (ostream, " + var3 + ");");
            }
         } else {
            var1 = ((JavaGenerator)var12.generator()).write(var1, var2, var3, var12, var5);
         }
      }

      return var1;
   }
}
