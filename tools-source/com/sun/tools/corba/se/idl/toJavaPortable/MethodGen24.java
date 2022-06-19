package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class MethodGen24 extends MethodGen {
   protected void writeParmList(MethodEntry var1, boolean var2, PrintWriter var3) {
      boolean var4 = true;

      ParameterEntry var6;
      for(Enumeration var5 = var1.parameters().elements(); var5.hasMoreElements(); var3.print(var6.name())) {
         if (var4) {
            var4 = false;
         } else {
            var3.print(", ");
         }

         var6 = (ParameterEntry)var5.nextElement();
         if (var2) {
            this.writeParmType(var6.type(), var6.passType());
            var3.print(' ');
         }
      }

   }

   protected void helperFactoryMethod(Hashtable var1, MethodEntry var2, SymtabEntry var3, PrintWriter var4) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var4;
      String var5 = var2.name();
      String var6 = Util.javaName(var3);
      String var7 = var6 + "ValueFactory";
      var4.print("  public static " + var6 + " " + var5 + " (org.omg.CORBA.ORB $orb");
      if (!var2.parameters().isEmpty()) {
         var4.print(", ");
      }

      this.writeParmList(var2, true, var4);
      var4.println(")");
      var4.println("  {");
      var4.println("    try {");
      var4.println("      " + var7 + " $factory = (" + var7 + ")");
      var4.println("          ((org.omg.CORBA_2_3.ORB) $orb).lookup_value_factory(id());");
      var4.print("      return $factory." + var5 + " (");
      this.writeParmList(var2, false, var4);
      var4.println(");");
      var4.println("    } catch (ClassCastException $ex) {");
      var4.println("      throw new org.omg.CORBA.BAD_PARAM ();");
      var4.println("    }");
      var4.println("  }");
      var4.println();
   }

   protected void abstractMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      var3.print("  ");
      var3.print("public abstract ");
      this.writeMethodSignature();
      var3.println(";");
      var3.println();
   }

   protected void defaultFactoryMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      String var4 = var2.container().name();
      var3.println();
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      var3.print("  public " + var4 + " " + var2.name() + " (");
      this.writeParmList(var2, true, var3);
      var3.println(")");
      var3.println("  {");
      var3.print("    return new " + var4 + "Impl (");
      this.writeParmList(var2, false, var3);
      var3.println(");");
      var3.println("  }");
   }

   protected void writeMethodSignature() {
      if (this.m.type() == null) {
         if (this.isValueInitializer()) {
            this.stream.print(this.m.container().name());
         } else {
            this.stream.print("void");
         }
      } else {
         this.stream.print(Util.javaName(this.m.type()));
      }

      this.stream.print(' ' + this.m.name() + " (");
      boolean var1 = true;
      Enumeration var2 = this.m.parameters().elements();

      while(var2.hasMoreElements()) {
         if (var1) {
            var1 = false;
         } else {
            this.stream.print(", ");
         }

         ParameterEntry var3 = (ParameterEntry)var2.nextElement();
         this.writeParmType(var3.type(), var3.passType());
         this.stream.print(' ' + var3.name());
      }

      if (this.m.contexts().size() > 0) {
         if (!var1) {
            this.stream.print(", ");
         }

         this.stream.print("org.omg.CORBA.Context $context");
      }

      if (this.m.exceptions().size() > 0) {
         this.stream.print(") throws ");
         var2 = this.m.exceptions().elements();

         for(var1 = true; var2.hasMoreElements(); this.stream.print(Util.javaName((SymtabEntry)var2.nextElement()))) {
            if (var1) {
               var1 = false;
            } else {
               this.stream.print(", ");
            }
         }
      } else {
         this.stream.print(')');
      }

   }

   protected void interfaceMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      var3.print("  ");
      this.writeMethodSignature();
      var3.println(";");
   }
}
