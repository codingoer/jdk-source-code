package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ValueBoxGen implements com.sun.tools.corba.se.idl.ValueBoxGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected ValueBoxEntry v = null;
   protected PrintWriter stream = null;

   public void generate(Hashtable var1, ValueBoxEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.v = var2;
      TypedefEntry var4 = ((InterfaceState)var2.state().elementAt(0)).entry;
      SymtabEntry var5 = var4.type();
      if (var5 instanceof PrimitiveEntry) {
         this.openStream();
         if (this.stream == null) {
            return;
         }

         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      } else {
         Enumeration var6 = var2.contained().elements();

         while(var6.hasMoreElements()) {
            SymtabEntry var7 = (SymtabEntry)var6.nextElement();
            if (var7.type() != null) {
               var7.type().generate(var1, this.stream);
            }
         }
      }

      this.generateHelper();
      this.generateHolder();
   }

   protected void openStream() {
      this.stream = Util.stream(this.v, ".java");
   }

   protected void generateHelper() {
      ((Factories)Compile.compiler.factories()).helper().generate(this.symbolTable, this.v);
   }

   protected void generateHolder() {
      ((Factories)Compile.compiler.factories()).holder().generate(this.symbolTable, this.v);
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.v);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.v.comment() != null) {
         this.v.comment().generate("", this.stream);
      }

      this.stream.println("public class " + this.v.name() + " implements org.omg.CORBA.portable.ValueBase");
      this.stream.println("{");
   }

   protected void writeBody() {
      InterfaceState var1 = (InterfaceState)this.v.state().elementAt(0);
      TypedefEntry var2 = var1.entry;
      Util.fillInfo(var2);
      if (var2.comment() != null) {
         var2.comment().generate(" ", this.stream);
      }

      this.stream.println("  public " + Util.javaName(var2) + " value;");
      this.stream.println("  public " + this.v.name() + " (" + Util.javaName(var2) + " initial)");
      this.stream.println("  {");
      this.stream.println("    value = initial;");
      this.stream.println("  }");
      this.stream.println();
      this.writeTruncatable();
   }

   protected void writeTruncatable() {
      this.stream.println("  public String[] _truncatable_ids() {");
      this.stream.println("      return " + Util.helperName(this.v, true) + ".get_instance().get_truncatable_base_ids();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeClosing() {
      this.stream.println("} // class " + this.v.name());
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeStreamableMethods() {
      this.stream.println("  public void _read (org.omg.CORBA.portable.InputStream istream)");
      this.stream.println("  {");
      this.streamableRead("this", this.v, this.stream);
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public void _write (org.omg.CORBA.portable.OutputStream ostream)");
      this.stream.println("  {");
      this.write(0, "    ", "this", this.v, this.stream);
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public org.omg.CORBA.TypeCode _type ()");
      this.stream.println("  {");
      this.stream.println("    return " + Util.helperName(this.v, false) + ".type ();");
      this.stream.println("  }");
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      ValueEntry var7 = (ValueEntry)var5;
      TypedefEntry var8 = ((InterfaceState)var7.state().elementAt(0)).entry;
      SymtabEntry var9 = Util.typeOf(var8);
      var1 = ((JavaGenerator)var9.generator()).type(var1, var2, var3, var4, var9, var6);
      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_value_box_tc (_id, " + '"' + var5.name() + "\", " + var4 + ");");
      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    return (" + var1 + ") ((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (get_instance());");
      var3.println("  }");
      var3.println();
      var3.println("  public java.io.Serializable read_value (org.omg.CORBA.portable.InputStream istream)");
      var3.println("  {");
      String var4 = "    ";
      Vector var5 = ((ValueBoxEntry)var2).state();
      TypedefEntry var6 = ((InterfaceState)var5.elementAt(0)).entry;
      SymtabEntry var7 = var6.type();
      if (!(var7 instanceof PrimitiveEntry) && !(var7 instanceof SequenceEntry) && !(var7 instanceof TypedefEntry) && !(var7 instanceof StringEntry) && var6.arrayInfo().isEmpty()) {
         if (!(var7 instanceof ValueEntry) && !(var7 instanceof ValueBoxEntry)) {
            var3.println(var4 + Util.javaName(var7) + " tmp = " + Util.helperName(var7, true) + ".read (istream);");
         } else {
            var3.println(var4 + Util.javaQualifiedName(var7) + " tmp = (" + Util.javaQualifiedName(var7) + ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + Util.helperName(var7, true) + ".get_instance ());");
         }
      } else {
         var3.println(var4 + Util.javaName(var7) + " tmp;");
         ((JavaGenerator)var6.generator()).read(0, var4, "tmp", var6, var3);
      }

      if (var7 instanceof PrimitiveEntry) {
         var3.println(var4 + "return new " + var1 + " (tmp);");
      } else {
         var3.println(var4 + "return tmp;");
      }

   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      var2.println("    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, get_instance());");
      var2.println("  }");
      var2.println();
      var2.println("  public void write_value (org.omg.CORBA.portable.OutputStream ostream, java.io.Serializable obj)");
      var2.println("  {");
      String var3 = Util.javaName(var1);
      var2.println("    " + var3 + " value  = (" + var3 + ") obj;");
      this.write(0, "    ", "value", var1, var2);
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).state();
      TypedefEntry var7 = ((InterfaceState)var6.elementAt(0)).entry;
      SymtabEntry var8 = var7.type();
      if (!(var8 instanceof PrimitiveEntry) && var7.arrayInfo().isEmpty()) {
         if (!(var8 instanceof SequenceEntry) && !(var8 instanceof StringEntry) && !(var8 instanceof TypedefEntry) && var7.arrayInfo().isEmpty()) {
            if (!(var8 instanceof ValueEntry) && !(var8 instanceof ValueBoxEntry)) {
               var5.println(var2 + Util.helperName(var8, true) + ".write (ostream, " + var3 + ");");
            } else {
               var5.println(var2 + "((org.omg.CORBA_2_3.portable.OutputStream)ostream).write_value ((java.io.Serializable) value, " + Util.helperName(var8, true) + ".get_instance ());");
            }
         } else {
            var1 = ((JavaGenerator)var7.generator()).write(var1, var2, var3, var7, var5);
         }
      } else {
         var1 = ((JavaGenerator)var7.generator()).write(var1, var2, var3 + ".value", var7, var5);
      }

      return var1;
   }

   protected void writeAbstract() {
   }

   protected void streamableRead(String var1, SymtabEntry var2, PrintWriter var3) {
      Vector var4 = ((ValueBoxEntry)var2).state();
      TypedefEntry var5 = ((InterfaceState)var4.elementAt(0)).entry;
      SymtabEntry var6 = var5.type();
      if (!(var6 instanceof PrimitiveEntry) && !(var6 instanceof SequenceEntry) && !(var6 instanceof TypedefEntry) && !(var6 instanceof StringEntry) && var5.arrayInfo().isEmpty()) {
         if (!(var6 instanceof ValueEntry) && !(var6 instanceof ValueBoxEntry)) {
            var3.println("    " + var1 + ".value = " + Util.helperName(var6, true) + ".read (istream);");
         } else {
            var3.println("    " + var1 + ".value = (" + Util.javaQualifiedName(var6) + ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + Util.helperName(var6, true) + ".get_instance ());");
         }
      } else {
         TypedefEntry var7 = ((InterfaceState)var4.elementAt(0)).entry;
         ((JavaGenerator)var5.generator()).read(0, "    ", var1 + ".value", var5, var3);
      }

   }
}
