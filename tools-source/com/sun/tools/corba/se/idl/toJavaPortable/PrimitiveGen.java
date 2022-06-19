package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class PrimitiveGen implements com.sun.tools.corba.se.idl.PrimitiveGen, JavaGenerator {
   public void generate(Hashtable var1, PrimitiveEntry var2, PrintWriter var3) {
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      return this.type(var1, var2, var3, var4, var5, var6);
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var3.set(var5);
      String var7 = "tk_null";
      if (var5.name().equals("null")) {
         var7 = "tk_null";
      } else if (var5.name().equals("void")) {
         var7 = "tk_void";
      } else if (var5.name().equals("short")) {
         var7 = "tk_short";
      } else if (var5.name().equals("long")) {
         var7 = "tk_long";
      } else if (var5.name().equals("long long")) {
         var7 = "tk_longlong";
      } else if (var5.name().equals("unsigned short")) {
         var7 = "tk_ushort";
      } else if (var5.name().equals("unsigned long")) {
         var7 = "tk_ulong";
      } else if (var5.name().equals("unsigned long long")) {
         var7 = "tk_ulonglong";
      } else if (var5.name().equals("float")) {
         var7 = "tk_float";
      } else if (var5.name().equals("double")) {
         var7 = "tk_double";
      } else if (var5.name().equals("boolean")) {
         var7 = "tk_boolean";
      } else if (var5.name().equals("char")) {
         var7 = "tk_char";
      } else if (var5.name().equals("octet")) {
         var7 = "tk_octet";
      } else if (var5.name().equals("any")) {
         var7 = "tk_any";
      } else if (var5.name().equals("TypeCode")) {
         var7 = "tk_TypeCode";
      } else if (var5.name().equals("wchar")) {
         var7 = "tk_wchar";
      } else if (var5.name().equals("Principal")) {
         var7 = "tk_Principal";
      } else if (var5.name().equals("wchar")) {
         var7 = "tk_wchar";
      }

      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind." + var7 + ");");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      var5.println(var2 + var3 + " = istream.read_" + Util.collapseName(var4.name()) + " ();");
      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      var5.println(var2 + "ostream.write_" + Util.collapseName(var4.name()) + " (" + var3 + ");");
      return var1;
   }
}
