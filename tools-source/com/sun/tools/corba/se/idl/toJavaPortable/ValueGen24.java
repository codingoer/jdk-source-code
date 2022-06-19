package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ValueGen24 extends ValueGen {
   protected void writeConstructor() {
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      var2.println("    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, id ());");
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    return (" + var1 + ")((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (id ());");
   }

   protected void writeInitializers() {
   }

   protected void writeTruncatable() {
      if (!this.v.isAbstract()) {
         this.stream.println("  private static String[] _truncatable_ids = {");
         this.stream.print("    " + Util.helperName(this.v, true) + ".id ()");

         ValueEntry var2;
         for(ValueEntry var1 = this.v; var1.isSafe(); var1 = var2) {
            this.stream.println(",");
            var2 = (ValueEntry)var1.derivedFrom().elementAt(0);
            this.stream.print("    \"" + Util.stripLeadingUnderscoresFromID(var2.repositoryID().ID()) + "\"");
         }

         this.stream.println();
         this.stream.println("  };");
         this.stream.println();
         this.stream.println("  public String[] _truncatable_ids() {");
         this.stream.println("    return _truncatable_ids;");
         this.stream.println("  }");
         this.stream.println();
      }

   }

   protected void writeHeading() {
      ImplStreamWriter var1 = new ImplStreamWriter();
      Util.writePackage(this.stream, this.v);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.v.comment() != null) {
         this.v.comment().generate("", this.stream);
      }

      if (this.v.isAbstract()) {
         this.writeAbstract();
      } else {
         this.stream.print("public abstract class " + this.v.name());
         SymtabEntry var2 = (SymtabEntry)this.v.derivedFrom().elementAt(0);
         String var3 = Util.javaName(var2);
         boolean var4 = false;
         if (var3.equals("java.io.Serializable")) {
            if (this.v.isCustom()) {
               var1.writeClassName("org.omg.CORBA.portable.CustomValue");
               var4 = true;
            } else {
               var1.writeClassName("org.omg.CORBA.portable.StreamableValue");
            }
         } else if (!((ValueEntry)var2).isAbstract()) {
            this.stream.print(" extends " + var3);
         }

         for(int var5 = 0; var5 < this.v.derivedFrom().size(); ++var5) {
            var2 = (SymtabEntry)this.v.derivedFrom().elementAt(var5);
            if (((ValueEntry)var2).isAbstract()) {
               var1.writeClassName(Util.javaName(var2));
            }
         }

         String var7;
         for(Enumeration var8 = this.v.supports().elements(); var8.hasMoreElements(); var1.writeClassName(var7)) {
            InterfaceEntry var6 = (InterfaceEntry)((InterfaceEntry)var8.nextElement());
            var7 = Util.javaName(var6);
            if (!var6.isAbstract()) {
               var7 = var7 + "Operations";
            }
         }

         if (this.v.isCustom() && !var4) {
            var1.writeClassName("org.omg.CORBA.portable.CustomValue");
         }

         this.stream.println();
         this.stream.println("{");
      }
   }

   protected void writeMembers() {
      if (this.v.state() != null) {
         for(int var1 = 0; var1 < this.v.state().size(); ++var1) {
            InterfaceState var2 = (InterfaceState)this.v.state().elementAt(var1);
            TypedefEntry var3 = var2.entry;
            Util.fillInfo(var3);
            if (var3.comment() != null) {
               var3.comment().generate(" ", this.stream);
            }

            String var4 = "  ";
            if (var2.modifier == 2) {
               var4 = "  public ";
            } else {
               var4 = "  protected ";
            }

            Util.writeInitializer(var4, var3.name(), "", var3, this.stream);
         }

         this.stream.println();
      }
   }

   protected void writeMethods() {
      Enumeration var1 = this.v.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (var2 instanceof AttributeEntry) {
            AttributeEntry var3 = (AttributeEntry)var2;
            ((AttributeGen24)var3.generator()).abstractMethod(this.symbolTable, var3, this.stream);
         } else if (var2 instanceof MethodEntry) {
            MethodEntry var4 = (MethodEntry)var2;
            ((MethodGen24)var4.generator()).abstractMethod(this.symbolTable, var4, this.stream);
         } else {
            if (var2 instanceof TypedefEntry) {
               var2.type().generate(this.symbolTable, this.stream);
            }

            var2.generate(this.symbolTable, this.stream);
         }
      }

      if (!this.v.isAbstract()) {
         if (!this.v.isCustom() && !this.v.isAbstract()) {
            this.writeStreamableMethods();
         }

      }
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).derivedFrom();
      if (var6 != null && var6.size() != 0) {
         ValueEntry var7 = (ValueEntry)var6.elementAt(0);
         if (var7 == null) {
            return var1;
         }

         if (!var7.isAbstract() && !Util.javaQualifiedName(var7).equals("java.io.Serializable")) {
            var5.println(var2 + "super._read (istream);");
         }
      }

      Vector var13 = ((ValueEntry)var4).state();
      int var8 = var13 == null ? 0 : var13.size();

      for(int var9 = 0; var9 < var8; ++var9) {
         TypedefEntry var10 = ((InterfaceState)var13.elementAt(var9)).entry;
         String var11 = var10.name();
         SymtabEntry var12 = var10.type();
         if (!(var12 instanceof PrimitiveEntry) && !(var12 instanceof TypedefEntry) && !(var12 instanceof SequenceEntry) && !(var12 instanceof StringEntry) && var10.arrayInfo().isEmpty()) {
            var5.println(var2 + var3 + '.' + var11 + " = " + Util.helperName(var12, true) + ".read (istream);");
         } else {
            var1 = ((JavaGenerator)var10.generator()).read(var1, var2, var3 + '.' + var11, var10, var5);
         }
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).derivedFrom();
      if (var6 != null && var6.size() != 0) {
         ValueEntry var7 = (ValueEntry)var6.elementAt(0);
         if (var7 == null) {
            return var1;
         }

         if (!var7.isAbstract() && !Util.javaQualifiedName(var7).equals("java.io.Serializable")) {
            var5.println(var2 + "super._write (ostream);");
         }
      }

      Vector var13 = ((ValueEntry)var4).state();
      int var8 = var13 == null ? 0 : var13.size();

      for(int var9 = 0; var9 < var8; ++var9) {
         TypedefEntry var10 = ((InterfaceState)var13.elementAt(var9)).entry;
         String var11 = var10.name();
         SymtabEntry var12 = var10.type();
         if (!(var12 instanceof PrimitiveEntry) && !(var12 instanceof TypedefEntry) && !(var12 instanceof SequenceEntry) && !(var12 instanceof StringEntry) && var10.arrayInfo().isEmpty()) {
            var5.println(var2 + Util.helperName(var12, true) + ".write (ostream, " + var3 + '.' + var11 + ");");
         } else {
            var1 = ((JavaGenerator)var10.generator()).write(var1, var2, var3 + '.' + var11, var10, var5);
         }
      }

      return var1;
   }

   public void generate(Hashtable var1, ValueEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.v = var2;
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.generateTie();
         this.generateHelper();
         this.generateHolder();
         if (!var2.isAbstract()) {
            this.generateValueFactory();
            this.generateDefaultFactory();
         }

         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void generateValueFactory() {
      ((Factories)Compile.compiler.factories()).valueFactory().generate(this.symbolTable, this.v);
   }

   protected void generateDefaultFactory() {
      ((Factories)Compile.compiler.factories()).defaultFactory().generate(this.symbolTable, this.v);
   }

   class ImplStreamWriter {
      private boolean isImplementsWritten = false;

      public void writeClassName(String var1) {
         if (!this.isImplementsWritten) {
            ValueGen24.this.stream.print(" implements ");
            this.isImplementsWritten = true;
         } else {
            ValueGen24.this.stream.print(", ");
         }

         ValueGen24.this.stream.print(var1);
      }
   }
}
