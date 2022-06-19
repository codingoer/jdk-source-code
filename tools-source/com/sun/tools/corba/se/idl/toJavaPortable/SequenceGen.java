package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Hashtable;

public class SequenceGen implements com.sun.tools.corba.se.idl.SequenceGen, JavaGenerator {
   public void generate(Hashtable var1, SequenceEntry var2, PrintWriter var3) {
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      int var7 = var3.offset(var5.type().fullName());
      Expression var8;
      if (var7 >= 0) {
         var3.set((SymtabEntry)null);
         var8 = ((SequenceEntry)var5).maxSize();
         if (var8 == null) {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_recursive_sequence_tc (0, " + (var7 - var3.currentOffset()) + ");");
         } else {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_recursive_sequence_tc (" + Util.parseExpression(var8) + ", " + (var7 - var3.currentOffset()) + ");");
         }

         var3.bumpCurrentOffset(4);
      } else {
         var3.set(var5);
         var1 = ((JavaGenerator)var5.type().generator()).helperType(var1 + 1, var2, var3, var4, var5.type(), var6);
         var8 = ((SequenceEntry)var5).maxSize();
         if (var8 == null) {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_sequence_tc (0, " + var4 + ");");
         } else {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_sequence_tc (" + Util.parseExpression(var8) + ", " + var4 + ");");
         }
      }

      var3.bumpCurrentOffset(4);
      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      int var7 = var3.offset(var5.type().fullName());
      if (var7 >= 0) {
         var3.set((SymtabEntry)null);
         var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_recursive_tc (\"\");");
         var3.bumpCurrentOffset(4);
      } else {
         var3.set(var5);
         var1 = ((JavaGenerator)var5.type().generator()).type(var1 + 1, var2, var3, var4, var5.type(), var6);
         Expression var8 = ((SequenceEntry)var5).maxSize();
         if (var8 == null) {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_sequence_tc (0, " + var4 + ");");
         } else {
            var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_sequence_tc (" + Util.parseExpression(var8) + ", " + var4 + ");");
         }
      }

      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      SequenceEntry var6 = (SequenceEntry)var4;
      String var7 = "_len" + var1++;
      var5.println(var2 + "int " + var7 + " = istream.read_long ();");
      if (var6.maxSize() != null) {
         var5.println(var2 + "if (" + var7 + " > (" + Util.parseExpression(var6.maxSize()) + "))");
         var5.println(var2 + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
      }

      String var8;
      try {
         var8 = Util.sansArrayInfo((String)var6.dynamicVariable(Compile.typedefInfo));
      } catch (NoSuchFieldException var15) {
         var8 = var6.name();
      }

      int var9 = var8.indexOf(91);
      String var10 = var8.substring(var9);
      var8 = var8.substring(0, var9);
      SymtabEntry var11 = (SymtabEntry)Util.symbolTable.get(var8.replace('.', '/'));
      if (var11 != null && var11 instanceof InterfaceEntry && ((InterfaceEntry)var11).state() != null) {
         var8 = Util.javaName((InterfaceEntry)var11);
      }

      var10 = var10.substring(2);
      var5.println(var2 + var3 + " = new " + var8 + '[' + var7 + ']' + var10 + ';');
      String var12;
      int var13;
      if (var6.type() instanceof PrimitiveEntry) {
         if (!var6.type().name().equals("any") && !var6.type().name().equals("TypeCode") && !var6.type().name().equals("Principal")) {
            var12 = var3;
            var13 = var3.indexOf(32);
            if (var13 != -1) {
               var12 = var3.substring(var13 + 1);
            }

            var5.println(var2 + "istream.read_" + Util.collapseName(var4.type().name()) + "_array (" + var12 + ", 0, " + var7 + ");");
         } else {
            var12 = "_o" + var1;
            var5.println(var2 + "for (int " + var12 + " = 0;" + var12 + " < " + var3 + ".length; ++" + var12 + ')');
            var5.println(var2 + "  " + var3 + '[' + var12 + "] = istream.read_" + var6.type().name() + " ();");
         }
      } else if (var4.type() instanceof StringEntry) {
         var12 = "_o" + var1;
         var5.println(var2 + "for (int " + var12 + " = 0;" + var12 + " < " + var3 + ".length; ++" + var12 + ')');
         var5.println(var2 + "  " + var3 + '[' + var12 + "] = istream.read_" + var6.type().name() + " ();");
      } else if (var4.type() instanceof SequenceEntry) {
         var12 = "_o" + var1;
         var5.println(var2 + "for (int " + var12 + " = 0;" + var12 + " < " + var3 + ".length; ++" + var12 + ')');
         var5.println(var2 + '{');
         var1 = ((JavaGenerator)var6.type().generator()).read(var1, var2 + "  ", var3 + '[' + var12 + ']', var6.type(), var5);
         var5.println(var2 + '}');
      } else {
         var12 = var3;
         var13 = var3.indexOf(32);
         if (var13 != -1) {
            var12 = var3.substring(var13 + 1);
         }

         String var14 = "_o" + var1;
         var5.println(var2 + "for (int " + var14 + " = 0;" + var14 + " < " + var12 + ".length; ++" + var14 + ')');
         var5.println(var2 + "  " + var12 + '[' + var14 + "] = " + Util.helperName(var6.type(), true) + ".read (istream);");
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      SequenceEntry var6 = (SequenceEntry)var4;
      if (var6.maxSize() != null) {
         var5.println(var2 + "if (" + var3 + ".length > (" + Util.parseExpression(var6.maxSize()) + "))");
         var5.println(var2 + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
      }

      var5.println(var2 + "ostream.write_long (" + var3 + ".length);");
      String var7;
      if (var4.type() instanceof PrimitiveEntry) {
         if (!var4.type().name().equals("any") && !var4.type().name().equals("TypeCode") && !var4.type().name().equals("Principal")) {
            var5.println(var2 + "ostream.write_" + Util.collapseName(var4.type().name()) + "_array (" + var3 + ", 0, " + var3 + ".length);");
         } else {
            var7 = "_i" + var1++;
            var5.println(var2 + "for (int " + var7 + " = 0;" + var7 + " < " + var3 + ".length; ++" + var7 + ')');
            var5.println(var2 + "  ostream.write_" + var6.type().name() + " (" + var3 + '[' + var7 + "]);");
         }
      } else if (var4.type() instanceof StringEntry) {
         var7 = "_i" + var1++;
         var5.println(var2 + "for (int " + var7 + " = 0;" + var7 + " < " + var3 + ".length; ++" + var7 + ')');
         var5.println(var2 + "  ostream.write_" + var6.type().name() + " (" + var3 + '[' + var7 + "]);");
      } else if (var4.type() instanceof SequenceEntry) {
         var7 = "_i" + var1++;
         var5.println(var2 + "for (int " + var7 + " = 0;" + var7 + " < " + var3 + ".length; ++" + var7 + ')');
         var5.println(var2 + '{');
         var1 = ((JavaGenerator)var6.type().generator()).write(var1, var2 + "  ", var3 + '[' + var7 + ']', var6.type(), var5);
         var5.println(var2 + '}');
      } else {
         var7 = "_i" + var1++;
         var5.println(var2 + "for (int " + var7 + " = 0;" + var7 + " < " + var3 + ".length; ++" + var7 + ')');
         var5.println(var2 + "  " + Util.helperName(var6.type(), true) + ".write (ostream, " + var3 + '[' + var7 + "]);");
      }

      return var1;
   }
}
