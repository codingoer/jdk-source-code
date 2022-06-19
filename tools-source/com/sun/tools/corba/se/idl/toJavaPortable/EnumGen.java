package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class EnumGen implements com.sun.tools.corba.se.idl.EnumGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected EnumEntry e = null;
   protected PrintWriter stream = null;
   String className = null;
   String fullClassName = null;

   public void generate(Hashtable var1, EnumEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.e = var2;
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.generateHolder();
         this.generateHelper();
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void init() {
      this.className = this.e.name();
      this.fullClassName = Util.javaName(this.e);
   }

   protected void openStream() {
      this.stream = Util.stream(this.e, ".java");
   }

   protected void generateHolder() {
      ((Factories)Compile.compiler.factories()).holder().generate(this.symbolTable, this.e);
   }

   protected void generateHelper() {
      ((Factories)Compile.compiler.factories()).helper().generate(this.symbolTable, this.e);
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.e);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.e.comment() != null) {
         this.e.comment().generate("", this.stream);
      }

      this.stream.println("public class " + this.className + " implements org.omg.CORBA.portable.IDLEntity");
      this.stream.println("{");
   }

   protected void writeBody() {
      this.stream.println("  private        int __value;");
      this.stream.println("  private static int __size = " + this.e.elements().size() + ';');
      this.stream.println("  private static " + this.fullClassName + "[] __array = new " + this.fullClassName + " [__size];");
      this.stream.println();

      for(int var1 = 0; var1 < this.e.elements().size(); ++var1) {
         String var2 = (String)this.e.elements().elementAt(var1);
         this.stream.println("  public static final int _" + var2 + " = " + var1 + ';');
         this.stream.println("  public static final " + this.fullClassName + ' ' + var2 + " = new " + this.fullClassName + "(_" + var2 + ");");
      }

      this.stream.println();
      this.writeValue();
      this.writeFromInt();
      this.writeCtors();
   }

   protected void writeValue() {
      this.stream.println("  public int value ()");
      this.stream.println("  {");
      this.stream.println("    return __value;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeFromInt() {
      this.stream.println("  public static " + this.fullClassName + " from_int (int value)");
      this.stream.println("  {");
      this.stream.println("    if (value >= 0 && value < __size)");
      this.stream.println("      return __array[value];");
      this.stream.println("    else");
      this.stream.println("      throw new org.omg.CORBA.BAD_PARAM ();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeCtors() {
      this.stream.println("  protected " + this.className + " (int value)");
      this.stream.println("  {");
      this.stream.println("    __value = value;");
      this.stream.println("    __array[__value] = this;");
      this.stream.println("  }");
   }

   protected void writeClosing() {
      this.stream.println("} // class " + this.className);
   }

   protected void closeStream() {
      this.stream.close();
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var3.set(var5);
      EnumEntry var7 = (EnumEntry)var5;
      StringBuffer var8 = new StringBuffer("new String[] { ");
      Enumeration var9 = var7.elements().elements();

      for(boolean var10 = true; var9.hasMoreElements(); var8.append('"' + Util.stripLeadingUnderscores((String)var9.nextElement()) + '"')) {
         if (var10) {
            var10 = false;
         } else {
            var8.append(", ");
         }
      }

      var8.append("} ");
      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_enum_tc (" + Util.helperName(var7, true) + ".id (), \"" + Util.stripLeadingUnderscores(var5.name()) + "\", " + new String(var8) + ");");
      return var1 + 1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    return " + Util.javaQualifiedName(var2) + ".from_int (istream.read_long ());");
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      var2.println("    ostream.write_long (value.value ());");
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      var5.println(var2 + var3 + " = " + Util.javaQualifiedName(var4) + ".from_int (istream.read_long ());");
      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      var5.println(var2 + "ostream.write_long (" + var3 + ".value ());");
      return var1;
   }
}
