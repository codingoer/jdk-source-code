package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.MethodEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class MethodGenClone24 extends AttributeGen {
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
