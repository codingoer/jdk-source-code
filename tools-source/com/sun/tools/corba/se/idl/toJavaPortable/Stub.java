package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Stub implements AuxGen {
   protected Hashtable symbolTable = null;
   protected InterfaceEntry i = null;
   protected PrintWriter stream = null;
   protected Vector methodList = null;
   protected String classSuffix = "";
   protected boolean localStub = false;
   private boolean isAbstract = false;

   public void generate(Hashtable var1, SymtabEntry var2) {
      this.symbolTable = var1;
      this.i = (InterfaceEntry)var2;
      this.localStub = this.i.isLocalServant();
      this.isAbstract = this.i.isAbstract();
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void init() {
      this.classSuffix = "Stub";
   }

   protected void openStream() {
      String var1 = '_' + this.i.name() + this.classSuffix;
      String var2 = Util.containerFullName(this.i.container());
      if (var2 != null && !var2.equals("")) {
         Util.mkdir(var2);
         var1 = var2 + '/' + var1;
      }

      this.stream = Util.getStream(var1.replace('/', File.separatorChar) + ".java", this.i);
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.i, (short)1);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.i.comment() != null) {
         this.i.comment().generate("", this.stream);
      }

      this.writeClassDeclaration();
      this.stream.println('{');
   }

   protected void writeClassDeclaration() {
      this.stream.print("public class _" + this.i.name() + this.classSuffix + " extends org.omg.CORBA.portable.ObjectImpl");
      this.stream.println(" implements " + Util.javaName(this.i));
   }

   protected void writeBody() {
      this.writeCtors();
      this.buildMethodList();
      this.writeMethods();
      this.writeCORBAObjectMethods();
      this.writeSerializationMethods();
   }

   protected void writeClosing() {
      this.stream.println("} // class _" + this.i.name() + this.classSuffix);
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeCtors() {
      String var1 = this.i.name();
      if (this.localStub) {
         this.stream.println("  final public static java.lang.Class _opsClass = " + var1 + "Operations.class;");
         this.stream.println();
      }

      this.stream.println();
   }

   protected void buildMethodList() {
      this.methodList = new Vector();
      this.buildMethodList(this.i);
   }

   private void buildMethodList(InterfaceEntry var1) {
      Enumeration var2 = var1.methods().elements();

      while(var2.hasMoreElements()) {
         this.addMethod((MethodEntry)var2.nextElement());
      }

      Enumeration var3 = var1.derivedFrom().elements();

      while(var3.hasMoreElements()) {
         InterfaceEntry var4 = (InterfaceEntry)var3.nextElement();
         if (!var4.name().equals("Object")) {
            this.buildMethodList(var4);
         }
      }

   }

   private void addMethod(MethodEntry var1) {
      if (!this.methodList.contains(var1)) {
         this.methodList.addElement(var1);
      }

   }

   protected void writeMethods() {
      int var1 = this.methodList.size();
      Enumeration var2 = this.methodList.elements();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof AttributeEntry && !((AttributeEntry)var3).readOnly()) {
            ++var1;
         }
      }

      if (((Arguments)Compile.compiler.arguments).LocalOptimization && !this.isAbstract) {
         this.stream.println("    final public static java.lang.Class _opsClass =");
         this.stream.println("        " + this.i.name() + "Operations.class;");
      }

      int var6 = 0;

      for(int var4 = 0; var4 < this.methodList.size(); ++var4) {
         MethodEntry var5 = (MethodEntry)this.methodList.elementAt(var4);
         if (!this.localStub) {
            ((MethodGen)var5.generator()).stub(this.i.name(), this.isAbstract, this.symbolTable, var5, this.stream, var6);
         } else {
            ((MethodGen)var5.generator()).localstub(this.symbolTable, var5, this.stream, var6, this.i);
         }

         if (var5 instanceof AttributeEntry && !((AttributeEntry)var5).readOnly()) {
            var6 += 2;
         } else {
            ++var6;
         }
      }

   }

   private void buildIDList(InterfaceEntry var1, Vector var2) {
      if (!var1.fullName().equals("org/omg/CORBA/Object")) {
         String var3 = Util.stripLeadingUnderscoresFromID(var1.repositoryID().ID());
         if (!var2.contains(var3)) {
            var2.addElement(var3);
         }

         Enumeration var4 = var1.derivedFrom().elements();

         while(var4.hasMoreElements()) {
            this.buildIDList((InterfaceEntry)var4.nextElement(), var2);
         }
      }

   }

   private void writeIDs() {
      Vector var1 = new Vector();
      this.buildIDList(this.i, var1);
      Enumeration var2 = var1.elements();

      for(boolean var3 = true; var2.hasMoreElements(); this.stream.print("    \"" + (String)var2.nextElement() + '"')) {
         if (var3) {
            var3 = false;
         } else {
            this.stream.println(", ");
         }
      }

   }

   protected void writeCORBAObjectMethods() {
      this.stream.println("  // Type-specific CORBA::Object operations");
      this.stream.println("  private static String[] __ids = {");
      this.writeIDs();
      this.stream.println("};");
      this.stream.println();
      this.stream.println("  public String[] _ids ()");
      this.stream.println("  {");
      this.stream.println("    return (String[])__ids.clone ();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeSerializationMethods() {
      this.stream.println("  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException");
      this.stream.println("  {");
      this.stream.println("     String str = s.readUTF ();");
      this.stream.println("     String[] args = null;");
      this.stream.println("     java.util.Properties props = null;");
      this.stream.println("     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);");
      this.stream.println("   try {");
      this.stream.println("     org.omg.CORBA.Object obj = orb.string_to_object (str);");
      this.stream.println("     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();");
      this.stream.println("     _set_delegate (delegate);");
      this.stream.println("   } finally {");
      this.stream.println("     orb.destroy() ;");
      this.stream.println("   }");
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException");
      this.stream.println("  {");
      this.stream.println("     String[] args = null;");
      this.stream.println("     java.util.Properties props = null;");
      this.stream.println("     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);");
      this.stream.println("   try {");
      this.stream.println("     String str = orb.object_to_string (this);");
      this.stream.println("     s.writeUTF (str);");
      this.stream.println("   } finally {");
      this.stream.println("     orb.destroy() ;");
      this.stream.println("   }");
      this.stream.println("  }");
   }
}
