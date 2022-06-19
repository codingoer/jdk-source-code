package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class StringGen implements com.sun.tools.corba.se.idl.StringGen, JavaGenerator {
   public void generate(Hashtable var1, StringEntry var2, PrintWriter var3) {
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      return this.type(var1, var2, var3, var4, var5, var6);
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var3.set(var5);
      StringEntry var7 = (StringEntry)var5;
      String var8;
      if (var7.maxSize() == null) {
         var8 = "0";
      } else {
         var8 = Util.parseExpression(var7.maxSize());
      }

      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_" + var5.name() + "_tc (" + var8 + ");");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      StringEntry var6 = (StringEntry)var4;
      String var7 = var4.name();
      if (var7.equals("string")) {
         var5.println(var2 + var3 + " = istream.read_string ();");
      } else if (var7.equals("wstring")) {
         var5.println(var2 + var3 + " = istream.read_wstring ();");
      }

      if (var6.maxSize() != null) {
         var5.println(var2 + "if (" + var3 + ".length () > (" + Util.parseExpression(var6.maxSize()) + "))");
         var5.println(var2 + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      StringEntry var6 = (StringEntry)var4;
      if (var6.maxSize() != null) {
         var5.print(var2 + "if (" + var3 + ".length () > (" + Util.parseExpression(var6.maxSize()) + "))");
         var5.println(var2 + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
      }

      String var7 = var4.name();
      if (var7.equals("string")) {
         var5.println(var2 + "ostream.write_string (" + var3 + ");");
      } else if (var7.equals("wstring")) {
         var5.println(var2 + "ostream.write_wstring (" + var3 + ");");
      }

      return var1;
   }
}
