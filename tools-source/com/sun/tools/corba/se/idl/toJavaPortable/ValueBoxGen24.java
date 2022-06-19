package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.io.PrintWriter;
import java.util.Vector;

public class ValueBoxGen24 extends ValueBoxGen {
   protected void writeTruncatable() {
      this.stream.print("  private static String[] _truncatable_ids = {");
      this.stream.println(Util.helperName(this.v, true) + ".id ()};");
      this.stream.println();
      this.stream.println("  public String[] _truncatable_ids() {");
      this.stream.println("    return _truncatable_ids;");
      this.stream.println("  }");
      this.stream.println();
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    if (!(istream instanceof org.omg.CORBA_2_3.portable.InputStream)) {");
      var3.println("      throw new org.omg.CORBA.BAD_PARAM(); }");
      var3.println("    return (" + var1 + ") ((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (_instance);");
      var3.println("  }");
      var3.println();
      var3.println("  public java.io.Serializable read_value (org.omg.CORBA.portable.InputStream istream)");
      var3.println("  {");
      String var4 = "    ";
      Vector var5 = ((ValueBoxEntry)var2).state();
      TypedefEntry var6 = ((InterfaceState)var5.elementAt(0)).entry;
      SymtabEntry var7 = var6.type();
      if (!(var7 instanceof PrimitiveEntry) && !(var7 instanceof SequenceEntry) && !(var7 instanceof TypedefEntry) && !(var7 instanceof StringEntry) && var6.arrayInfo().isEmpty()) {
         var3.println(var4 + Util.javaName(var7) + " tmp = " + Util.helperName(var7, true) + ".read (istream);");
      } else {
         var3.println(var4 + Util.javaName(var7) + " tmp;");
         ((JavaGenerator)var6.generator()).read(0, var4, "tmp", var6, var3);
      }

      if (var7 instanceof PrimitiveEntry) {
         var3.println(var4 + "return new " + var1 + " (tmp);");
      } else {
         var3.println(var4 + "return (java.io.Serializable) tmp;");
      }

   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      var2.println("    if (!(ostream instanceof org.omg.CORBA_2_3.portable.OutputStream)) {");
      var2.println("      throw new org.omg.CORBA.BAD_PARAM(); }");
      var2.println("    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, _instance);");
      var2.println("  }");
      var2.println();
      var2.println("  public void write_value (org.omg.CORBA.portable.OutputStream ostream, java.io.Serializable value)");
      var2.println("  {");
      String var3 = Util.javaName(var1);
      var2.println("    if (!(value instanceof " + var3 + ")) {");
      var2.println("      throw new org.omg.CORBA.MARSHAL(); }");
      var2.println("    " + var3 + " valueType = (" + var3 + ") value;");
      this.write(0, "    ", "valueType", var1, var2);
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).state();
      TypedefEntry var7 = ((InterfaceState)var6.elementAt(0)).entry;
      SymtabEntry var8 = var7.type();
      if (!(var8 instanceof PrimitiveEntry) && var7.arrayInfo().isEmpty()) {
         if (!(var8 instanceof SequenceEntry) && !(var8 instanceof StringEntry) && !(var8 instanceof TypedefEntry) && var7.arrayInfo().isEmpty()) {
            var5.println(var2 + Util.helperName(var8, true) + ".write (ostream, " + var3 + ");");
         } else {
            var1 = ((JavaGenerator)var7.generator()).write(var1, var2, var3, var7, var5);
         }
      } else {
         var1 = ((JavaGenerator)var7.generator()).write(var1, var2, var3 + ".value", var7, var5);
      }

      return var1;
   }
}
