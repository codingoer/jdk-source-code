package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ConstEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class InterfaceGen implements com.sun.tools.corba.se.idl.InterfaceGen, JavaGenerator {
   protected int emit = 0;
   protected Factories factories = null;
   protected Hashtable symbolTable = null;
   protected InterfaceEntry i = null;
   protected PrintWriter stream = null;
   protected static final int SIGNATURE = 1;
   protected static final int OPERATIONS = 2;
   protected int intfType = 0;

   public void generate(Hashtable var1, InterfaceEntry var2, PrintWriter var3) {
      if (!this.isPseudo(var2)) {
         this.symbolTable = var1;
         this.i = var2;
         this.init();
         if (!var2.isLocalSignature()) {
            if (!var2.isLocal()) {
               this.generateSkeleton();
               Arguments var4 = (Arguments)Compile.compiler.arguments;
               if (var4.TIEServer && var4.emit == 3) {
                  var4.TIEServer = false;
                  this.generateSkeleton();
                  var4.TIEServer = true;
               }

               this.generateStub();
            }

            this.generateHolder();
            this.generateHelper();
         }

         this.intfType = 1;
         this.generateInterface();
         this.intfType = 2;
         this.generateInterface();
         this.intfType = 0;
      }

   }

   protected void init() {
      this.emit = ((Arguments)Compile.compiler.arguments).emit;
      this.factories = (Factories)Compile.compiler.factories();
   }

   protected void generateSkeleton() {
      if (this.emit != 1) {
         this.factories.skeleton().generate(this.symbolTable, this.i);
      }

   }

   protected void generateStub() {
      if (this.emit != 2) {
         this.factories.stub().generate(this.symbolTable, this.i);
      }

   }

   protected void generateHelper() {
      if (this.emit != 2) {
         this.factories.helper().generate(this.symbolTable, this.i);
      }

   }

   protected void generateHolder() {
      if (this.emit != 2) {
         this.factories.holder().generate(this.symbolTable, this.i);
      }

   }

   protected void generateInterface() {
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.writeHeading();
         if (this.intfType == 2) {
            this.writeOperationsBody();
         }

         if (this.intfType == 1) {
            this.writeSignatureBody();
         }

         this.writeClosing();
         this.closeStream();
      }
   }

   protected void openStream() {
      if (!this.i.isAbstract() && this.intfType != 1) {
         if (this.intfType == 2) {
            this.stream = Util.stream(this.i, "Operations.java");
         }
      } else {
         this.stream = Util.stream(this.i, ".java");
      }

   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.i, (short)0);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.i.comment() != null) {
         this.i.comment().generate("", this.stream);
      }

      String var1 = this.i.name();
      if (this.intfType == 1) {
         this.writeSignatureHeading();
      } else if (this.intfType == 2) {
         this.writeOperationsHeading();
      }

      this.stream.println();
      this.stream.println('{');
   }

   protected void writeSignatureHeading() {
      String var1 = this.i.name();
      this.stream.print("public interface " + var1 + " extends " + var1 + "Operations, ");
      boolean var2 = true;
      boolean var3 = false;

      for(int var4 = 0; var4 < this.i.derivedFrom().size(); ++var4) {
         if (var2) {
            var2 = false;
         } else {
            this.stream.print(", ");
         }

         InterfaceEntry var5 = (InterfaceEntry)this.i.derivedFrom().elementAt(var4);
         this.stream.print(Util.javaName(var5));
         if (!var5.isAbstract()) {
            var3 = true;
         }
      }

      if (!var3) {
         this.stream.print(", org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity ");
      } else if (this.i.derivedFrom().size() == 1) {
         this.stream.print(", org.omg.CORBA.portable.IDLEntity ");
      }

   }

   protected void writeOperationsHeading() {
      this.stream.print("public interface " + this.i.name());
      if (!this.i.isAbstract()) {
         this.stream.print("Operations ");
      } else if (this.i.derivedFrom().size() == 0) {
         this.stream.print(" extends org.omg.CORBA.portable.IDLEntity");
      }

      boolean var1 = true;

      for(int var2 = 0; var2 < this.i.derivedFrom().size(); ++var2) {
         InterfaceEntry var3 = (InterfaceEntry)this.i.derivedFrom().elementAt(var2);
         String var4 = Util.javaName(var3);
         if (!var4.equals("org.omg.CORBA.Object")) {
            if (var1) {
               var1 = false;
               this.stream.print(" extends ");
            } else {
               this.stream.print(", ");
            }

            if (!var3.isAbstract() && !this.i.isAbstract()) {
               this.stream.print(var4 + "Operations");
            } else {
               this.stream.print(var4);
            }
         }
      }

   }

   protected void writeOperationsBody() {
      Enumeration var1 = this.i.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (var2 instanceof MethodEntry) {
            MethodEntry var3 = (MethodEntry)var2;
            ((MethodGen)var3.generator()).interfaceMethod(this.symbolTable, var3, this.stream);
         } else if (!(var2 instanceof ConstEntry)) {
            var2.generate(this.symbolTable, this.stream);
         }
      }

   }

   protected void writeSignatureBody() {
      Enumeration var1 = this.i.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (var2 instanceof ConstEntry) {
            var2.generate(this.symbolTable, this.stream);
         }
      }

   }

   protected void writeClosing() {
      String var1 = this.i.name();
      if (!this.i.isAbstract() && this.intfType == 2) {
         var1 = var1 + "Operations";
      }

      this.stream.println("} // interface " + var1);
   }

   protected void closeStream() {
      this.stream.close();
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      InterfaceEntry var7 = (InterfaceEntry)var5;
      var3.set(var5);
      if (var5.fullName().equals("org/omg/CORBA/Object")) {
         var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_objref);");
      } else {
         var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_interface_tc (" + Util.helperName(var7, true) + ".id (), " + '"' + Util.stripLeadingUnderscores(var5.name()) + "\");");
      }

      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      InterfaceEntry var4 = (InterfaceEntry)var2;
      if (var4.isAbstract()) {
         var3.println("    return narrow (((org.omg.CORBA_2_3.portable.InputStream)istream).read_abstract_interface (_" + var4.name() + "Stub.class));");
      } else {
         var3.println("    return narrow (istream.read_Object (_" + var4.name() + "Stub.class));");
      }

   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      this.write(0, "    ", "value", var1, var2);
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      InterfaceEntry var6 = (InterfaceEntry)var4;
      if (var4.fullName().equals("org/omg/CORBA/Object")) {
         var5.println(var2 + var3 + " = istream.read_Object (_" + var6.name() + "Stub.class);");
      } else {
         var5.println(var2 + var3 + " = " + Util.helperName(var4, false) + ".narrow (istream.read_Object (_" + var6.name() + "Stub.class));");
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      InterfaceEntry var6 = (InterfaceEntry)var4;
      if (var6.isAbstract()) {
         var5.println(var2 + "((org.omg.CORBA_2_3.portable.OutputStream)ostream).write_abstract_interface ((java.lang.Object) " + var3 + ");");
      } else {
         var5.println(var2 + "ostream.write_Object ((org.omg.CORBA.Object) " + var3 + ");");
      }

      return var1;
   }

   private boolean isPseudo(InterfaceEntry var1) {
      String var2 = var1.fullName();
      if (var2.equalsIgnoreCase("CORBA/TypeCode")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/Principal")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/ORB")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/Any")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/Context")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/ContextList")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/DynamicImplementation")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/Environment")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/ExceptionList")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/NVList")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/NamedValue")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/Request")) {
         return true;
      } else if (var2.equalsIgnoreCase("CORBA/ServerRequest")) {
         return true;
      } else {
         return var2.equalsIgnoreCase("CORBA/UserException");
      }
   }
}
