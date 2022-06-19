package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ForwardValueEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class ForwardValueGen implements com.sun.tools.corba.se.idl.ForwardValueGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected ForwardValueEntry v = null;
   protected PrintWriter stream = null;

   public void generate(Hashtable var1, ForwardValueEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.v = var2;
      this.openStream();
      if (this.stream != null) {
         this.generateHelper();
         this.generateHolder();
         this.generateStub();
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
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

   protected void generateStub() {
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.v);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.v.comment() != null) {
         this.v.comment().generate("", this.stream);
      }

      this.stream.print("public class " + this.v.name() + " implements org.omg.CORBA.portable.IDLEntity");
      this.stream.println("{");
   }

   protected void writeBody() {
   }

   protected void writeClosing() {
      this.stream.println("} // class " + this.v.name());
   }

   protected void closeStream() {
      this.stream.close();
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    " + var1 + " value = new " + var1 + " ();");
      this.read(0, "    ", "value", var2, var3);
      var3.println("    return value;");
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      return var1;
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      this.write(0, "    ", "value", var1, var2);
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      return var1;
   }

   protected void writeAbstract() {
   }
}
