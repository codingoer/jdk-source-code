package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.MethodEntry;
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

public class ValueGen implements com.sun.tools.corba.se.idl.ValueGen, JavaGenerator {
   protected int emit = 0;
   protected Factories factories = null;
   protected Hashtable symbolTable = null;
   protected ValueEntry v = null;
   protected PrintWriter stream = null;
   protected boolean explicitDefaultInit = false;

   public void generate(Hashtable var1, ValueEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.v = var2;
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.generateTie();
         this.generateHelper();
         this.generateHolder();
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void init() {
      this.emit = ((Arguments)Compile.compiler.arguments).emit;
      this.factories = (Factories)Compile.compiler.factories();
   }

   protected void openStream() {
      this.stream = Util.stream(this.v, ".java");
   }

   protected void generateTie() {
      boolean var1 = ((Arguments)Compile.compiler.arguments).TIEServer;
      if (this.v.supports().size() > 0 && var1) {
         Factories var2 = (Factories)Compile.compiler.factories();
         var2.skeleton().generate(this.symbolTable, this.v);
      }

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

      if (this.v.isAbstract()) {
         this.writeAbstract();
      } else {
         this.stream.print("public class " + this.v.name());
         SymtabEntry var1 = (SymtabEntry)this.v.derivedFrom().elementAt(0);
         String var2 = Util.javaName(var1);
         boolean var3 = false;
         if (var2.equals("java.io.Serializable")) {
            this.stream.print(" implements org.omg.CORBA.portable.ValueBase");
            var3 = true;
         } else if (!((ValueEntry)var1).isAbstract()) {
            this.stream.print(" extends " + var2);
         }

         for(int var4 = 0; var4 < this.v.derivedFrom().size(); ++var4) {
            var1 = (SymtabEntry)this.v.derivedFrom().elementAt(var4);
            if (((ValueEntry)var1).isAbstract()) {
               if (!var3) {
                  this.stream.print(" implements ");
                  var3 = true;
               } else {
                  this.stream.print(", ");
               }

               this.stream.print(Util.javaName(var1));
            }
         }

         if (this.v.supports().size() > 0) {
            if (!var3) {
               this.stream.print(" implements ");
               var3 = true;
            } else {
               this.stream.print(", ");
            }

            InterfaceEntry var5 = (InterfaceEntry)this.v.supports().elementAt(0);
            if (var5.isAbstract()) {
               this.stream.print(Util.javaName(var5));
            } else {
               this.stream.print(Util.javaName(var5) + "Operations");
            }
         }

         if (this.v.isCustom()) {
            if (!var3) {
               this.stream.print(" implements ");
               var3 = true;
            } else {
               this.stream.print(", ");
            }

            this.stream.print("org.omg.CORBA.CustomMarshal ");
         }

         this.stream.println();
         this.stream.println("{");
      }
   }

   protected void writeBody() {
      this.writeMembers();
      this.writeInitializers();
      this.writeConstructor();
      this.writeTruncatable();
      this.writeMethods();
   }

   protected void writeClosing() {
      if (this.v.isAbstract()) {
         this.stream.println("} // interface " + this.v.name());
      } else {
         this.stream.println("} // class " + this.v.name());
      }

   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeConstructor() {
      if (!this.v.isAbstract() && !this.explicitDefaultInit) {
         this.stream.println("  protected " + this.v.name() + " () {}");
         this.stream.println();
      }

   }

   protected void writeTruncatable() {
      if (!this.v.isAbstract()) {
         this.stream.println("  public String[] _truncatable_ids() {");
         this.stream.println("      return " + Util.helperName(this.v, true) + ".get_instance().get_truncatable_base_ids();");
         this.stream.println("  }");
         this.stream.println();
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
            }

            Util.writeInitializer(var4, var3.name(), "", var3, this.stream);
         }

      }
   }

   protected void writeInitializers() {
      Vector var1 = this.v.initializers();
      if (var1 != null) {
         this.stream.println();

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            MethodEntry var3 = (MethodEntry)var1.elementAt(var2);
            var3.valueMethod(true);
            ((MethodGen)var3.generator()).interfaceMethod(this.symbolTable, var3, this.stream);
            if (var3.parameters().isEmpty()) {
               this.explicitDefaultInit = true;
            }
         }
      }

   }

   protected void writeMethods() {
      Enumeration var1 = this.v.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (var2 instanceof MethodEntry) {
            MethodEntry var3 = (MethodEntry)var2;
            ((MethodGen)var3.generator()).interfaceMethod(this.symbolTable, var3, this.stream);
         } else {
            if (var2 instanceof TypedefEntry) {
               var2.type().generate(this.symbolTable, this.stream);
            }

            var2.generate(this.symbolTable, this.stream);
         }
      }

      if (!this.v.isAbstract()) {
         MethodEntry var5;
         if (this.v.supports().size() > 0) {
            InterfaceEntry var7 = (InterfaceEntry)this.v.supports().elementAt(0);
            Enumeration var9 = var7.allMethods().elements();

            while(var9.hasMoreElements()) {
               MethodEntry var4 = (MethodEntry)var9.nextElement();
               var5 = (MethodEntry)var4.clone();
               var5.container(this.v);
               ((MethodGen)var5.generator()).interfaceMethod(this.symbolTable, var5, this.stream);
            }
         }

         for(int var8 = 0; var8 < this.v.derivedFrom().size(); ++var8) {
            ValueEntry var10 = (ValueEntry)this.v.derivedFrom().elementAt(var8);
            if (var10.isAbstract()) {
               Enumeration var11 = var10.allMethods().elements();

               while(var11.hasMoreElements()) {
                  var5 = (MethodEntry)var11.nextElement();
                  MethodEntry var6 = (MethodEntry)var5.clone();
                  var6.container(this.v);
                  ((MethodGen)var6.generator()).interfaceMethod(this.symbolTable, var6, this.stream);
               }
            }
         }

      }
   }

   protected void writeStreamableMethods() {
      this.stream.println("  public void _read (org.omg.CORBA.portable.InputStream istream)");
      this.stream.println("  {");
      this.read(0, "    ", "this", this.v, this.stream);
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
      Vector var8 = var7.state();
      int var9 = var8 == null ? 0 : var8.size();
      String var10 = "_members" + var1++;
      String var11 = "_tcOf" + var10;
      var6.println(var2 + "org.omg.CORBA.ValueMember[] " + var10 + " = new org.omg.CORBA.ValueMember[" + var9 + "];");
      var6.println(var2 + "org.omg.CORBA.TypeCode " + var11 + " = null;");
      String var12 = "_id";

      for(int var15 = 0; var15 < var9; ++var15) {
         InterfaceState var16 = (InterfaceState)var8.elementAt(var15);
         TypedefEntry var17 = var16.entry;
         SymtabEntry var18 = Util.typeOf(var17);
         String var13;
         String var14;
         if (hasRepId(var17)) {
            var13 = Util.helperName(var18, true) + ".id ()";
            if (!(var18 instanceof ValueEntry) && !(var18 instanceof ValueBoxEntry)) {
               String var19 = var18.repositoryID().ID();
               var14 = '"' + var19.substring(var19.lastIndexOf(58) + 1) + '"';
            } else {
               var14 = "\"\"";
            }
         } else {
            var13 = "\"\"";
            var14 = "\"\"";
         }

         var6.println(var2 + "// ValueMember instance for " + var17.name());
         var1 = ((JavaGenerator)var17.generator()).type(var1, var2, var3, var11, var17, var6);
         var6.println(var2 + var10 + "[" + var15 + "] = new org.omg.CORBA.ValueMember (" + '"' + var17.name() + "\", ");
         var6.println(var2 + "    " + var13 + ", ");
         var6.println(var2 + "    " + var12 + ", ");
         var6.println(var2 + "    " + var14 + ", ");
         var6.println(var2 + "    " + var11 + ", ");
         var6.println(var2 + "    null, ");
         var6.println(var2 + "    org.omg.CORBA." + (var16.modifier == 2 ? "PUBLIC_MEMBER" : "PRIVATE_MEMBER") + ".value);");
      }

      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_value_tc (_id, " + '"' + var5.name() + "\", " + getValueModifier(var7) + ", " + getConcreteBaseTypeCode(var7) + ", " + var10 + ");");
      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   private static boolean hasRepId(SymtabEntry var0) {
      SymtabEntry var1 = Util.typeOf(var0);
      return !(var1 instanceof PrimitiveEntry) && !(var1 instanceof StringEntry) && (!(var1 instanceof TypedefEntry) || ((TypedefEntry)var1).arrayInfo().isEmpty()) && (!(var1 instanceof TypedefEntry) || !(var0.type() instanceof SequenceEntry));
   }

   private static String getValueModifier(ValueEntry var0) {
      String var1 = "NONE";
      if (var0.isCustom()) {
         var1 = "CUSTOM";
      } else if (var0.isAbstract()) {
         var1 = "ABSTRACT";
      } else if (var0.isSafe()) {
         var1 = "TRUNCATABLE";
      }

      return "org.omg.CORBA.VM_" + var1 + ".value";
   }

   private static String getConcreteBaseTypeCode(ValueEntry var0) {
      Vector var1 = var0.derivedFrom();
      if (!var0.isAbstract()) {
         SymtabEntry var2 = (SymtabEntry)var0.derivedFrom().elementAt(0);
         if (!"ValueBase".equals(var2.name())) {
            return Util.helperName(var2, true) + ".type ()";
         }
      }

      return "null";
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      if (((ValueEntry)var2).isAbstract()) {
         var3.println("    throw new org.omg.CORBA.BAD_OPERATION (\"abstract value cannot be instantiated\");");
      } else {
         var3.println("    return (" + var1 + ") ((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (get_instance());");
      }

      var3.println("  }");
      var3.println();
      var3.println("  public java.io.Serializable read_value (org.omg.CORBA.portable.InputStream istream)");
      var3.println("  {");
      if (((ValueEntry)var2).isAbstract()) {
         var3.println("    throw new org.omg.CORBA.BAD_OPERATION (\"abstract value cannot be instantiated\");");
      } else if (((ValueEntry)var2).isCustom()) {
         var3.println("    throw new org.omg.CORBA.BAD_OPERATION (\"custom values should use unmarshal()\");");
      } else {
         var3.println("    " + var1 + " value = new " + var1 + " ();");
         this.read(0, "    ", "value", var2, var3);
         var3.println("    return value;");
      }

      var3.println("  }");
      var3.println();
      var3.println("  public static void read (org.omg.CORBA.portable.InputStream istream, " + var1 + " value)");
      var3.println("  {");
      this.read(0, "    ", "value", var2, var3);
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).derivedFrom();
      if (var6 != null && var6.size() != 0) {
         ValueEntry var7 = (ValueEntry)var6.elementAt(0);
         if (var7 == null) {
            return var1;
         }

         if (!Util.javaQualifiedName(var7).equals("java.io.Serializable")) {
            var5.println(var2 + Util.helperName(var7, true) + ".read (istream, value);");
         }
      }

      Vector var14 = ((ValueEntry)var4).state();
      int var8 = var14 == null ? 0 : var14.size();

      for(int var9 = 0; var9 < var8; ++var9) {
         TypedefEntry var10 = ((InterfaceState)var14.elementAt(var9)).entry;
         String var11 = var10.name();
         SymtabEntry var12 = var10.type();
         if (!(var12 instanceof PrimitiveEntry) && !(var12 instanceof TypedefEntry) && !(var12 instanceof SequenceEntry) && !(var12 instanceof StringEntry) && var10.arrayInfo().isEmpty()) {
            if (var12 instanceof ValueEntry) {
               String var13 = Util.javaQualifiedName(var12);
               if (var12 instanceof ValueBoxEntry) {
                  var13 = Util.javaName(var12);
               }

               var5.println("    " + var3 + '.' + var11 + " = (" + var13 + ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + Util.helperName(var12, true) + ".get_instance ());");
            } else {
               var5.println(var2 + var3 + '.' + var11 + " = " + Util.helperName(var12, true) + ".read (istream);");
            }
         } else {
            var1 = ((JavaGenerator)var10.generator()).read(var1, var2, var3 + '.' + var11, var10, var5);
         }
      }

      return var1;
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      var2.println("    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, get_instance());");
      var2.println("  }");
      var2.println();
      if (!((ValueEntry)var1).isCustom()) {
         var2.println("  public static void _write (org.omg.CORBA.portable.OutputStream ostream, " + Util.javaName(var1) + " value)");
         var2.println("  {");
         this.write(0, "    ", "value", var1, var2);
         var2.println("  }");
         var2.println();
      }

      var2.println("  public void write_value (org.omg.CORBA.portable.OutputStream ostream, java.io.Serializable obj)");
      var2.println("  {");
      if (((ValueEntry)var1).isCustom()) {
         var2.println("    throw new org.omg.CORBA.BAD_OPERATION (\"custom values should use marshal()\");");
      } else {
         String var3 = Util.javaName(var1);
         var2.println("    _write (ostream, (" + var3 + ") obj);");
      }

   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      Vector var6 = ((ValueEntry)var4).derivedFrom();
      if (var6 != null && var6.size() != 0) {
         ValueEntry var7 = (ValueEntry)var6.elementAt(0);
         if (var7 == null) {
            return var1;
         }

         if (!Util.javaQualifiedName(var7).equals("java.io.Serializable")) {
            var5.println(var2 + Util.helperName(var7, true) + "._write (ostream, value);");
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

   protected void writeAbstract() {
      this.stream.print("public interface " + this.v.name());
      SymtabEntry var1;
      if (this.v.derivedFrom().size() == 0) {
         this.stream.print(" extends org.omg.CORBA.portable.ValueBase");
      } else {
         for(int var2 = 0; var2 < this.v.derivedFrom().size(); ++var2) {
            if (var2 == 0) {
               this.stream.print(" extends ");
            } else {
               this.stream.print(", ");
            }

            var1 = (SymtabEntry)this.v.derivedFrom().elementAt(var2);
            this.stream.print(Util.javaName(var1));
         }
      }

      if (this.v.supports().size() > 0) {
         this.stream.print(", ");
         var1 = (SymtabEntry)this.v.supports().elementAt(0);
         this.stream.print(Util.javaName(var1));
      }

      this.stream.println();
      this.stream.println("{");
   }
}
