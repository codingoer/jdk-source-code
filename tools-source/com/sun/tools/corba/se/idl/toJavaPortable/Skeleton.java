package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Skeleton implements AuxGen {
   private NameModifier skeletonNameModifier;
   private NameModifier tieNameModifier;
   protected Hashtable symbolTable = null;
   protected InterfaceEntry i = null;
   protected PrintWriter stream = null;
   protected String tieClassName = null;
   protected String skeletonClassName = null;
   protected boolean tie = false;
   protected boolean poa = false;
   protected Vector methodList = null;
   protected String intfName = "";

   public void generate(Hashtable var1, SymtabEntry var2) {
      if (var2 instanceof ValueEntry) {
         ValueEntry var3 = (ValueEntry)var2;
         if (var3.supports().size() == 0 || ((InterfaceEntry)var3.supports().elementAt(0)).isAbstract()) {
            return;
         }
      }

      if (!((InterfaceEntry)var2).isAbstract()) {
         this.symbolTable = var1;
         this.i = (InterfaceEntry)var2;
         this.init();
         this.openStream();
         if (this.stream != null) {
            this.writeHeading();
            this.writeBody();
            this.writeClosing();
            this.closeStream();
         }
      }
   }

   protected void init() {
      this.tie = ((Arguments)Compile.compiler.arguments).TIEServer;
      this.poa = ((Arguments)Compile.compiler.arguments).POAServer;
      this.skeletonNameModifier = ((Arguments)Compile.compiler.arguments).skeletonNameModifier;
      this.tieNameModifier = ((Arguments)Compile.compiler.arguments).tieNameModifier;
      this.tieClassName = this.tieNameModifier.makeName(this.i.name());
      this.skeletonClassName = this.skeletonNameModifier.makeName(this.i.name());
      this.intfName = Util.javaName(this.i);
      if (this.i instanceof ValueEntry) {
         ValueEntry var1 = (ValueEntry)this.i;
         InterfaceEntry var2 = (InterfaceEntry)var1.supports().elementAt(0);
         this.intfName = Util.javaName(var2);
      }

   }

   protected void openStream() {
      if (this.tie) {
         this.stream = Util.stream(this.i, this.tieNameModifier, ".java");
      } else {
         this.stream = Util.stream(this.i, this.skeletonNameModifier, ".java");
      }

   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.i, (short)1);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.i.comment() != null) {
         this.i.comment().generate("", this.stream);
      }

      this.writeClassDeclaration();
      this.stream.println('{');
      this.stream.println();
   }

   protected void writeClassDeclaration() {
      if (this.tie) {
         this.stream.println("public class " + this.tieClassName + " extends " + this.skeletonClassName);
      } else if (this.poa) {
         this.stream.println("public abstract class " + this.skeletonClassName + " extends org.omg.PortableServer.Servant");
         this.stream.print(" implements " + this.intfName + "Operations, ");
         this.stream.println("org.omg.CORBA.portable.InvokeHandler");
      } else {
         this.stream.println("public abstract class " + this.skeletonClassName + " extends org.omg.CORBA.portable.ObjectImpl");
         this.stream.print("                implements " + this.intfName + ", ");
         this.stream.println("org.omg.CORBA.portable.InvokeHandler");
      }

   }

   protected void writeBody() {
      this.writeCtors();
      if (this.i instanceof ValueEntry) {
         ValueEntry var1 = (ValueEntry)this.i;
         this.i = (InterfaceEntry)var1.supports().elementAt(0);
      }

      this.buildMethodList();
      if (this.tie) {
         if (this.poa) {
            this.writeMethods();
            this.stream.println("  private " + this.intfName + "Operations _impl;");
            this.stream.println("  private org.omg.PortableServer.POA _poa;");
         } else {
            this.writeMethods();
            this.stream.println("  private " + this.intfName + "Operations _impl;");
         }
      } else if (this.poa) {
         this.writeMethodTable();
         this.writeDispatchMethod();
         this.writeCORBAOperations();
      } else {
         this.writeMethodTable();
         this.writeDispatchMethod();
         this.writeCORBAOperations();
      }

      this.writeOperations();
   }

   protected void writeClosing() {
      this.stream.println();
      if (this.tie) {
         this.stream.println("} // class " + this.tieClassName);
      } else {
         this.stream.println("} // class " + this.skeletonClassName);
      }

   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeCtors() {
      this.stream.println("  // Constructors");
      if (!this.poa) {
         if (this.tie) {
            this.stream.println("  public " + this.tieClassName + " ()");
            this.stream.println("  {");
            this.stream.println("  }");
         } else {
            this.stream.println("  public " + this.skeletonClassName + " ()");
            this.stream.println("  {");
            this.stream.println("  }");
         }
      }

      this.stream.println();
      if (this.tie) {
         if (this.poa) {
            this.writePOATieCtors();
            this.writePOATieFieldAccessMethods();
         } else {
            this.stream.println("  public " + this.tieClassName + " (" + this.intfName + "Operations impl)");
            this.stream.println("  {");
            if (((InterfaceEntry)this.i.derivedFrom().firstElement()).state() != null) {
               this.stream.println("    super (impl);");
            } else {
               this.stream.println("    super ();");
            }

            this.stream.println("    _impl = impl;");
            this.stream.println("  }");
            this.stream.println();
         }
      } else if (this.poa) {
      }

   }

   private void writePOATieCtors() {
      this.stream.println("  public " + this.tieClassName + " ( " + this.intfName + "Operations delegate ) {");
      this.stream.println("      this._impl = delegate;");
      this.stream.println("  }");
      this.stream.println("  public " + this.tieClassName + " ( " + this.intfName + "Operations delegate , org.omg.PortableServer.POA poa ) {");
      this.stream.println("      this._impl = delegate;");
      this.stream.println("      this._poa      = poa;");
      this.stream.println("  }");
   }

   private void writePOATieFieldAccessMethods() {
      this.stream.println("  public " + this.intfName + "Operations _delegate() {");
      this.stream.println("      return this._impl;");
      this.stream.println("  }");
      this.stream.println("  public void _delegate (" + this.intfName + "Operations delegate ) {");
      this.stream.println("      this._impl = delegate;");
      this.stream.println("  }");
      this.stream.println("  public org.omg.PortableServer.POA _default_POA() {");
      this.stream.println("      if(_poa != null) {");
      this.stream.println("          return _poa;");
      this.stream.println("      }");
      this.stream.println("      else {");
      this.stream.println("          return super._default_POA();");
      this.stream.println("      }");
      this.stream.println("  }");
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

   protected void writeDispatchMethod() {
      String var1 = "                                ";
      this.stream.println("  public org.omg.CORBA.portable.OutputStream _invoke (String $method,");
      this.stream.println(var1 + "org.omg.CORBA.portable.InputStream in,");
      this.stream.println(var1 + "org.omg.CORBA.portable.ResponseHandler $rh)");
      this.stream.println("  {");
      boolean var2 = false;
      if (this.i instanceof InterfaceEntry) {
         var2 = this.i.isLocalServant();
      }

      if (!var2) {
         this.stream.println("    org.omg.CORBA.portable.OutputStream out = null;");
         this.stream.println("    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);");
         this.stream.println("    if (__method == null)");
         this.stream.println("      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
         this.stream.println();
         if (this.methodList.size() > 0) {
            this.stream.println("    switch (__method.intValue ())");
            this.stream.println("    {");
            int var3 = 0;
            int var4 = 0;

            while(true) {
               if (var4 >= this.methodList.size()) {
                  var1 = "       ";
                  this.stream.println(var1 + "default:");
                  this.stream.println(var1 + "  throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
                  this.stream.println("    }");
                  this.stream.println();
                  break;
               }

               MethodEntry var5 = (MethodEntry)this.methodList.elementAt(var4);
               ((MethodGen)var5.generator()).dispatchSkeleton(this.symbolTable, var5, this.stream, var3);
               if (var5 instanceof AttributeEntry && !((AttributeEntry)var5).readOnly()) {
                  var3 += 2;
               } else {
                  ++var3;
               }

               ++var4;
            }
         }

         this.stream.println("    return out;");
      } else {
         this.stream.println("    throw new org.omg.CORBA.BAD_OPERATION();");
      }

      this.stream.println("  } // _invoke");
      this.stream.println();
   }

   protected void writeMethodTable() {
      this.stream.println("  private static java.util.Hashtable _methods = new java.util.Hashtable ();");
      this.stream.println("  static");
      this.stream.println("  {");
      int var1 = -1;
      Enumeration var2 = this.methodList.elements();

      while(var2.hasMoreElements()) {
         MethodEntry var3 = (MethodEntry)var2.nextElement();
         PrintWriter var10000;
         StringBuilder var10001;
         if (var3 instanceof AttributeEntry) {
            var10000 = this.stream;
            var10001 = (new StringBuilder()).append("    _methods.put (\"_get_").append(Util.stripLeadingUnderscores(var3.name())).append("\", new java.lang.Integer (");
            ++var1;
            var10000.println(var10001.append(var1).append("));").toString());
            if (!((AttributeEntry)var3).readOnly()) {
               var10000 = this.stream;
               var10001 = (new StringBuilder()).append("    _methods.put (\"_set_").append(Util.stripLeadingUnderscores(var3.name())).append("\", new java.lang.Integer (");
               ++var1;
               var10000.println(var10001.append(var1).append("));").toString());
            }
         } else {
            var10000 = this.stream;
            var10001 = (new StringBuilder()).append("    _methods.put (\"").append(Util.stripLeadingUnderscores(var3.name())).append("\", new java.lang.Integer (");
            ++var1;
            var10000.println(var10001.append(var1).append("));").toString());
         }
      }

      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeMethods() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.methodList.size(); ++var2) {
         MethodEntry var3 = (MethodEntry)this.methodList.elementAt(var2);
         ((MethodGen)var3.generator()).skeleton(this.symbolTable, var3, this.stream, var1);
         if (var3 instanceof AttributeEntry && !((AttributeEntry)var3).readOnly()) {
            var1 += 2;
         } else {
            ++var1;
         }

         this.stream.println();
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

   protected void writeCORBAOperations() {
      this.stream.println("  // Type-specific CORBA::Object operations");
      this.stream.println("  private static String[] __ids = {");
      this.writeIDs();
      this.stream.println("};");
      this.stream.println();
      if (this.poa) {
         this.writePOACORBAOperations();
      } else {
         this.writeNonPOACORBAOperations();
      }

   }

   protected void writePOACORBAOperations() {
      this.stream.println("  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)");
      this.stream.println("  {");
      this.stream.println("    return (String[])__ids.clone ();");
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public " + this.i.name() + " _this() ");
      this.stream.println("  {");
      this.stream.println("    return " + this.i.name() + "Helper.narrow(");
      this.stream.println("    super._this_object());");
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public " + this.i.name() + " _this(org.omg.CORBA.ORB orb) ");
      this.stream.println("  {");
      this.stream.println("    return " + this.i.name() + "Helper.narrow(");
      this.stream.println("    super._this_object(orb));");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeNonPOACORBAOperations() {
      this.stream.println("  public String[] _ids ()");
      this.stream.println("  {");
      this.stream.println("    return (String[])__ids.clone ();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeOperations() {
   }
}
