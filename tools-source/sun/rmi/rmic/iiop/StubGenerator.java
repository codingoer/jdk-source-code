package sun.rmi.rmic.iiop;

import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.rmic.IndentingWriter;
import sun.rmi.rmic.Main;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;

public class StubGenerator extends Generator {
   private static final String DEFAULT_STUB_CLASS = "javax.rmi.CORBA.Stub";
   private static final String DEFAULT_TIE_CLASS = "org.omg.CORBA_2_3.portable.ObjectImpl";
   private static final String DEFAULT_POA_TIE_CLASS = "org.omg.PortableServer.Servant";
   protected boolean reverseIDs = false;
   protected boolean localStubs = true;
   protected boolean standardPackage = false;
   protected boolean useHash = true;
   protected String stubBaseClass = "javax.rmi.CORBA.Stub";
   protected String tieBaseClass = "org.omg.CORBA_2_3.portable.ObjectImpl";
   protected HashSet namesInUse = new HashSet();
   protected Hashtable classesInUse = new Hashtable();
   protected Hashtable imports = new Hashtable();
   protected int importCount = 0;
   protected String currentPackage = null;
   protected String currentClass = null;
   protected boolean castArray = false;
   protected Hashtable transactionalObjects = new Hashtable();
   protected boolean POATie = false;
   protected boolean emitPermissionCheck = false;
   private static final String NO_IMPORT = new String();
   static final String SINGLE_SLASH = "\\";
   static final String DOUBLE_SLASH = "\\\\";

   public void generate(sun.rmi.rmic.BatchEnvironment var1, ClassDefinition var2, File var3) {
      ((BatchEnvironment)var1).setStandardPackage(this.standardPackage);
      super.generate(var1, var2, var3);
   }

   protected boolean requireNewInstance() {
      return false;
   }

   protected boolean parseNonConforming(ContextStack var1) {
      return var1.getEnv().getParseNonConforming();
   }

   protected CompoundType getTopType(ClassDefinition var1, ContextStack var2) {
      Object var3 = null;
      if (var1.isInterface()) {
         var3 = AbstractType.forAbstract(var1, var2, true);
         if (var3 == null) {
            var3 = RemoteType.forRemote(var1, var2, false);
         }
      } else {
         var3 = ImplementationType.forImplementation(var1, var2, false);
      }

      return (CompoundType)var3;
   }

   public boolean parseArgs(String[] var1, Main var2) {
      Object var3 = new Object();
      this.reverseIDs = false;
      this.localStubs = true;
      this.useHash = true;
      this.stubBaseClass = "javax.rmi.CORBA.Stub";
      this.transactionalObjects = new Hashtable();
      boolean var4 = super.parseArgs(var1, var2);
      if (var4) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            if (var1[var5] != null) {
               String var6 = var1[var5].toLowerCase();
               if (var6.equals("-iiop")) {
                  var1[var5] = null;
               } else if (var6.equals("-xreverseids")) {
                  this.reverseIDs = true;
                  var1[var5] = null;
               } else if (var6.equals("-nolocalstubs")) {
                  this.localStubs = false;
                  var1[var5] = null;
               } else if (var6.equals("-xnohash")) {
                  this.useHash = false;
                  var1[var5] = null;
               } else if (var1[var5].equals("-standardPackage")) {
                  this.standardPackage = true;
                  var1[var5] = null;
               } else if (var1[var5].equals("-emitPermissionCheck")) {
                  this.emitPermissionCheck = true;
                  var1[var5] = null;
               } else if (var6.equals("-xstubbase")) {
                  var1[var5] = null;
                  ++var5;
                  if (var5 < var1.length && var1[var5] != null && !var1[var5].startsWith("-")) {
                     this.stubBaseClass = var1[var5];
                     var1[var5] = null;
                  } else {
                     var2.error("rmic.option.requires.argument", "-Xstubbase");
                     var4 = false;
                  }
               } else if (var6.equals("-xtiebase")) {
                  var1[var5] = null;
                  ++var5;
                  if (var5 < var1.length && var1[var5] != null && !var1[var5].startsWith("-")) {
                     this.tieBaseClass = var1[var5];
                     var1[var5] = null;
                  } else {
                     var2.error("rmic.option.requires.argument", "-Xtiebase");
                     var4 = false;
                  }
               } else if (!var6.equals("-transactional")) {
                  if (var6.equals("-poa")) {
                     this.POATie = true;
                     var1[var5] = null;
                  }
               } else {
                  for(int var7 = var5 + 1; var7 < var1.length; ++var7) {
                     if (var1[var7].charAt(1) != '-') {
                        this.transactionalObjects.put(var1[var7], var3);
                        break;
                     }
                  }

                  var1[var5] = null;
               }
            }
         }
      }

      if (this.POATie) {
         this.tieBaseClass = "org.omg.PortableServer.Servant";
      } else {
         this.tieBaseClass = "org.omg.CORBA_2_3.portable.ObjectImpl";
      }

      return var4;
   }

   protected Generator.OutputType[] getOutputTypesFor(CompoundType var1, HashSet var2) {
      int var3 = 69632;
      Type[] var4 = var1.collectMatching(var3, var2);
      int var5 = var4.length;
      Vector var6 = new Vector(var5 + 5);
      BatchEnvironment var7 = var1.getEnv();

      for(int var8 = 0; var8 < var4.length; ++var8) {
         Type var9 = var4[var8];
         String var10 = var9.getName();
         boolean var11 = true;
         if (var9 instanceof ImplementationType) {
            var6.addElement(new Generator.OutputType(Utility.tieNameForCompiler(var10), var9));
            int var12 = 0;
            InterfaceType[] var13 = ((CompoundType)var9).getInterfaces();

            for(int var14 = 0; var14 < var13.length; ++var14) {
               if (var13[var14].isType(4096) && !var13[var14].isType(8192)) {
                  ++var12;
               }
            }

            if (var12 <= 1) {
               var11 = false;
            }
         }

         if (var9 instanceof AbstractType) {
            var11 = false;
         }

         if (var11) {
            var6.addElement(new Generator.OutputType(Utility.stubNameForCompiler(var10), var9));
         }
      }

      Generator.OutputType[] var15 = new Generator.OutputType[var6.size()];
      var6.copyInto(var15);
      return var15;
   }

   protected String getFileNameExtensionFor(Generator.OutputType var1) {
      return ".java";
   }

   protected void writeOutputFor(Generator.OutputType var1, HashSet var2, IndentingWriter var3) throws IOException {
      String var4 = var1.getName();
      CompoundType var5 = (CompoundType)var1.getType();
      if (var4.endsWith("_Stub")) {
         this.writeStub(var1, var3);
      } else {
         this.writeTie(var1, var3);
      }

   }

   protected void writeStub(Generator.OutputType var1, IndentingWriter var2) throws IOException {
      CompoundType var3 = (CompoundType)var1.getType();
      RemoteType[] var4 = this.getDirectRemoteInterfaces(var3);
      var2.pln("// Stub class generated by rmic, do not edit.");
      var2.pln("// Contents subject to change without notice.");
      var2.pln();
      this.setStandardClassesInUse(var3, true);
      this.addClassesInUse(var3, var4);
      this.writePackageAndImports(var2);
      if (this.emitPermissionCheck) {
         var2.pln("import java.security.AccessController;");
         var2.pln("import java.security.PrivilegedAction;");
         var2.pln("import java.io.SerializablePermission;");
         var2.pln();
         var2.pln();
      }

      var2.p("public class " + this.currentClass);
      var2.p(" extends " + this.getName(this.stubBaseClass));
      var2.p(" implements ");
      if (var4.length > 0) {
         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var5 > 0) {
               var2.pln(",");
            }

            String var6 = this.testUtil(this.getName((Type)var4[var5]), var3);
            var2.p(var6);
         }
      }

      if (!this.implementsRemote(var3)) {
         var2.pln(",");
         var2.p(this.getName("java.rmi.Remote"));
      }

      var2.plnI(" {");
      var2.pln();
      this.writeIds(var2, var3, false);
      var2.pln();
      if (this.emitPermissionCheck) {
         var2.pln();
         var2.plnI("private transient boolean _instantiated = false;");
         var2.pln();
         var2.pO();
         var2.plnI("private static Void checkPermission() {");
         var2.plnI("SecurityManager sm = System.getSecurityManager();");
         var2.pln("if (sm != null) {");
         var2.pI();
         var2.plnI("sm.checkPermission(new SerializablePermission(");
         var2.plnI("\"enableSubclassImplementation\"));");
         var2.pO();
         var2.pO();
         var2.pOln("}");
         var2.pln("return null;");
         var2.pO();
         var2.pOln("}");
         var2.pln();
         var2.pO();
         var2.pI();
         var2.plnI("private " + this.currentClass + "(Void ignore) {  }");
         var2.pln();
         var2.pO();
         var2.plnI("public " + this.currentClass + "() {");
         var2.pln("this(checkPermission());");
         var2.pln("_instantiated = true;");
         var2.pOln("}");
         var2.pln();
         var2.plnI("private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {");
         var2.plnI("checkPermission();");
         var2.pO();
         var2.pln("s.defaultReadObject();");
         var2.pln("_instantiated = true;");
         var2.pOln("}");
         var2.pln();
      }

      if (!this.emitPermissionCheck) {
         var2.pI();
      }

      var2.plnI("public String[] _ids() { ");
      var2.pln("return (String[]) _type_ids.clone();");
      var2.pOln("}");
      CompoundType.Method[] var9 = var3.getMethods();
      int var10 = var9.length;
      if (var10 > 0) {
         boolean var7 = true;

         for(int var8 = 0; var8 < var10; ++var8) {
            if (!var9[var8].isConstructor()) {
               if (var7) {
                  var7 = false;
               }

               var2.pln();
               this.writeStubMethod(var2, var9[var8], var3);
            }
         }
      }

      this.writeCastArray(var2);
      var2.pOln("}");
   }

   void addClassInUse(String var1) {
      String var2 = var1;
      String var3 = null;
      int var4 = var1.lastIndexOf(46);
      if (var4 > 0) {
         var2 = var1.substring(var4 + 1);
         var3 = var1.substring(0, var4);
      }

      this.addClassInUse(var2, var1, var3);
   }

   void addClassInUse(Type var1) {
      if (!var1.isPrimitive()) {
         Identifier var2 = var1.getIdentifier();
         String var3 = IDLNames.replace(var2.getName().toString(), ". ", ".");
         String var4 = var1.getPackageName();
         String var5;
         if (var4 != null) {
            var5 = var4 + "." + var3;
         } else {
            var5 = var3;
         }

         this.addClassInUse(var3, var5, var4);
      }

   }

   void addClassInUse(Type[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.addClassInUse(var1[var2]);
      }

   }

   void addStubInUse(Type var1) {
      if (var1.getIdentifier() != idCorbaObject && var1.isType(2048)) {
         String var2 = this.getStubNameFor(var1, false);
         String var3 = var1.getPackageName();
         String var4;
         if (var3 == null) {
            var4 = var2;
         } else {
            var4 = var3 + "." + var2;
         }

         this.addClassInUse(var2, var4, var3);
      }

      if (var1.isType(4096) || var1.isType(524288)) {
         this.addClassInUse("javax.rmi.PortableRemoteObject");
      }

   }

   String getStubNameFor(Type var1, boolean var2) {
      String var4;
      if (var2) {
         var4 = var1.getQualifiedName();
      } else {
         var4 = var1.getName();
      }

      String var3;
      if (((CompoundType)var1).isCORBAObject()) {
         var3 = Utility.idlStubName(var4);
      } else {
         var3 = Utility.stubNameForCompiler(var4);
      }

      return var3;
   }

   void addStubInUse(Type[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.addStubInUse(var1[var2]);
      }

   }

   void addClassInUse(String var1, String var2, String var3) {
      String var4 = (String)this.classesInUse.get(var2);
      if (var4 == null) {
         String var5 = (String)this.imports.get(var1);
         String var6 = null;
         if (var3 == null) {
            var6 = var1;
         } else if (var3.equals("java.lang")) {
            var6 = var1;
            if (var1.endsWith("_Stub")) {
               var6 = Util.packagePrefix() + var2;
            }
         } else if (this.currentPackage != null && var3.equals(this.currentPackage)) {
            var6 = var1;
            if (var5 != null) {
               var6 = var2;
            }
         } else if (var5 != null) {
            var6 = var2;
         } else if (var2.equals("org.omg.CORBA.Object")) {
            var6 = var2;
         } else if (var1.indexOf(46) != -1) {
            var6 = var2;
         } else {
            var6 = var1;
            this.imports.put(var1, var2);
            ++this.importCount;
         }

         this.classesInUse.put(var2, var6);
      }

   }

   String getName(Type var1) {
      if (var1.isPrimitive()) {
         return var1.getName() + var1.getArrayBrackets();
      } else {
         Identifier var2 = var1.getIdentifier();
         String var3 = IDLNames.replace(var2.toString(), ". ", ".");
         return this.getName(var3) + var1.getArrayBrackets();
      }
   }

   String getExceptionName(Type var1) {
      Identifier var2 = var1.getIdentifier();
      return IDLNames.replace(var2.toString(), ". ", ".");
   }

   String getName(String var1) {
      return (String)this.classesInUse.get(var1);
   }

   String getName(Identifier var1) {
      return this.getName(var1.toString());
   }

   String getStubName(Type var1) {
      String var2 = this.getStubNameFor(var1, true);
      return this.getName(var2);
   }

   void setStandardClassesInUse(CompoundType var1, boolean var2) throws IOException {
      this.currentPackage = var1.getPackageName();
      this.imports.clear();
      this.classesInUse.clear();
      this.namesInUse.clear();
      this.importCount = 0;
      this.castArray = false;
      this.addClassInUse((Type)var1);
      if (var2) {
         this.currentClass = Utility.stubNameForCompiler(var1.getName());
      } else {
         this.currentClass = Utility.tieNameForCompiler(var1.getName());
      }

      if (this.currentPackage == null) {
         this.addClassInUse(this.currentClass, this.currentClass, this.currentPackage);
      } else {
         this.addClassInUse(this.currentClass, this.currentPackage + "." + this.currentClass, this.currentPackage);
      }

      this.addClassInUse("javax.rmi.CORBA.Util");
      this.addClassInUse(idRemote.toString());
      this.addClassInUse(idRemoteException.toString());
      this.addClassInUse(idOutputStream.toString());
      this.addClassInUse(idInputStream.toString());
      this.addClassInUse(idSystemException.toString());
      this.addClassInUse(idJavaIoSerializable.toString());
      this.addClassInUse(idCorbaORB.toString());
      this.addClassInUse(idReplyHandler.toString());
      if (var2) {
         this.addClassInUse(this.stubBaseClass);
         this.addClassInUse("java.rmi.UnexpectedException");
         this.addClassInUse(idRemarshalException.toString());
         this.addClassInUse(idApplicationException.toString());
         if (this.localStubs) {
            this.addClassInUse("org.omg.CORBA.portable.ServantObject");
         }
      } else {
         this.addClassInUse((Type)var1);
         this.addClassInUse(this.tieBaseClass);
         this.addClassInUse(idTieInterface.toString());
         this.addClassInUse(idBadMethodException.toString());
         this.addClassInUse(idPortableUnknownException.toString());
         this.addClassInUse(idJavaLangThrowable.toString());
      }

   }

   void addClassesInUse(CompoundType var1, RemoteType[] var2) {
      CompoundType.Method[] var3 = var1.getMethods();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         this.addClassInUse(var3[var4].getReturnType());
         this.addStubInUse(var3[var4].getReturnType());
         this.addClassInUse(var3[var4].getArguments());
         this.addStubInUse(var3[var4].getArguments());
         this.addClassInUse((Type[])var3[var4].getExceptions());
         this.addClassInUse((Type[])var3[var4].getImplExceptions());
      }

      if (var2 != null) {
         this.addClassInUse((Type[])var2);
      }

   }

   void writePackageAndImports(IndentingWriter var1) throws IOException {
      if (this.currentPackage != null) {
         var1.pln("package " + Util.correctPackageName(this.currentPackage, false, this.standardPackage) + ";");
         var1.pln();
      }

      String[] var2 = new String[this.importCount];
      int var3 = 0;
      Enumeration var4 = this.imports.elements();

      while(var4.hasMoreElements()) {
         String var5 = (String)var4.nextElement();
         if (var5 != NO_IMPORT) {
            var2[var3++] = var5;
         }
      }

      Arrays.sort(var2, new StringComparator());

      for(int var6 = 0; var6 < this.importCount; ++var6) {
         if (Util.isOffendingPackage(var2[var6]) && var2[var6].endsWith("_Stub") && String.valueOf(var2[var6].charAt(var2[var6].lastIndexOf(".") + 1)).equals("_")) {
            var1.pln("import " + PackagePrefixChecker.packagePrefix() + var2[var6] + ";");
         } else {
            var1.pln("import " + var2[var6] + ";");
         }
      }

      var1.pln();
      if (this.currentPackage != null && Util.isOffendingPackage(this.currentPackage)) {
         var1.pln("import " + this.currentPackage + ".*  ;");
      }

      var1.pln();
   }

   boolean implementsRemote(CompoundType var1) {
      boolean var2 = var1.isType(4096) && !var1.isType(8192);
      if (!var2) {
         InterfaceType[] var3 = var1.getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var2 = this.implementsRemote(var3[var4]);
            if (var2) {
               break;
            }
         }
      }

      return var2;
   }

   void writeStubMethod(IndentingWriter var1, CompoundType.Method var2, CompoundType var3) throws IOException {
      String var4 = var2.getName();
      String var5 = var2.getIDLName();
      Type[] var6 = var2.getArguments();
      String[] var7 = var2.getArgumentNames();
      Type var8 = var2.getReturnType();
      ValueType[] var9 = this.getStubExceptions(var2, false);
      boolean var10 = false;
      this.addNamesInUse(var2);
      this.addNameInUse("_type_ids");
      String var11 = this.testUtil(this.getName(var8), var8);
      var1.p("public " + var11 + " " + var4 + "(");

      int var12;
      for(var12 = 0; var12 < var6.length; ++var12) {
         if (var12 > 0) {
            var1.p(", ");
         }

         var1.p(this.getName(var6[var12]) + " " + var7[var12]);
      }

      var1.p(")");
      if (var9.length > 0) {
         var1.p(" throws ");

         for(var12 = 0; var12 < var9.length; ++var12) {
            if (var12 > 0) {
               var1.p(", ");
            }

            var1.p(this.getExceptionName(var9[var12]));
         }
      }

      var1.plnI(" {");
      if (this.emitPermissionCheck) {
         var1.pln("if ((System.getSecurityManager() != null) && (!_instantiated)) {");
         var1.plnI("    throw new java.io.IOError(new java.io.IOException(\"InvalidObject \"));");
         var1.pOln("}");
         var1.pln();
      }

      if (this.localStubs) {
         this.writeLocalStubMethodBody(var1, var2, var3);
      } else {
         this.writeNonLocalStubMethodBody(var1, var2, var3);
      }

      var1.pOln("}");
   }

   void writeLocalStubMethodBody(IndentingWriter var1, CompoundType.Method var2, CompoundType var3) throws IOException {
      String[] var5 = var2.getArgumentNames();
      Type var6 = var2.getReturnType();
      ValueType[] var7 = this.getStubExceptions(var2, false);
      String var8 = var2.getName();
      String var9 = var2.getIDLName();
      var1.plnI("if (!Util.isLocal(this)) {");
      this.writeNonLocalStubMethodBody(var1, var2, var3);
      var1.pOlnI("} else {");
      String var10 = this.getVariableName("so");
      var1.pln("ServantObject " + var10 + " = _servant_preinvoke(\"" + var9 + "\"," + this.getName((Type)var3) + ".class);");
      var1.plnI("if (" + var10 + " == null) {");
      if (!var6.isType(1)) {
         var1.p("return ");
      }

      var1.p(var8 + "(");

      for(int var11 = 0; var11 < var5.length; ++var11) {
         if (var11 > 0) {
            var1.p(", ");
         }

         var1.p(var5[var11]);
      }

      var1.pln(");");
      if (var6.isType(1)) {
         var1.pln("return ;");
      }

      var1.pOln("}");
      var1.plnI("try {");
      String[] var17 = this.writeCopyArguments(var2, var1);
      boolean var12 = mustCopy(var6);
      String var13 = null;
      String var4;
      if (!var6.isType(1)) {
         if (var12) {
            var13 = this.getVariableName("result");
            var4 = this.testUtil(this.getName(var6), var6);
            var1.p(var4 + " " + var13 + " = ");
         } else {
            var1.p("return ");
         }
      }

      var4 = this.testUtil(this.getName((Type)var3), var3);
      var1.p("((" + var4 + ")" + var10 + ".servant)." + var8 + "(");

      for(int var14 = 0; var14 < var17.length; ++var14) {
         if (var14 > 0) {
            var1.p(", ");
         }

         var1.p(var17[var14]);
      }

      if (var12) {
         var1.pln(");");
         var4 = this.testUtil(this.getName(var6), var6);
         var1.pln("return (" + var4 + ")Util.copyObject(" + var13 + ",_orb());");
      } else {
         var1.pln(");");
      }

      String var18 = this.getVariableName("ex");
      String var15 = this.getVariableName("exCopy");
      var1.pOlnI("} catch (Throwable " + var18 + ") {");
      var1.pln("Throwable " + var15 + " = (Throwable)Util.copyObject(" + var18 + ",_orb());");

      for(int var16 = 0; var16 < var7.length; ++var16) {
         if (var7[var16].getIdentifier() != idRemoteException && var7[var16].isType(32768)) {
            var1.plnI("if (" + var15 + " instanceof " + this.getExceptionName(var7[var16]) + ") {");
            var1.pln("throw (" + this.getExceptionName(var7[var16]) + ")" + var15 + ";");
            var1.pOln("}");
         }
      }

      var1.pln("throw Util.wrapException(" + var15 + ");");
      var1.pOlnI("} finally {");
      var1.pln("_servant_postinvoke(" + var10 + ");");
      var1.pOln("}");
      var1.pOln("}");
   }

   void writeNonLocalStubMethodBody(IndentingWriter var1, CompoundType.Method var2, CompoundType var3) throws IOException {
      String var4 = var2.getName();
      String var5 = var2.getIDLName();
      Type[] var6 = var2.getArguments();
      String[] var7 = var2.getArgumentNames();
      Type var8 = var2.getReturnType();
      ValueType[] var9 = this.getStubExceptions(var2, true);
      String var10 = this.getVariableName("in");
      String var11 = this.getVariableName("out");
      String var12 = this.getVariableName("ex");
      boolean var13 = false;

      int var14;
      for(var14 = 0; var14 < var9.length; ++var14) {
         if (var9[var14].getIdentifier() != idRemoteException && var9[var14].isType(32768) && needNewReadStreamClass(var9[var14])) {
            var13 = true;
            break;
         }
      }

      if (!var13) {
         for(var14 = 0; var14 < var6.length; ++var14) {
            if (needNewReadStreamClass(var6[var14])) {
               var13 = true;
               break;
            }
         }
      }

      if (!var13) {
         var13 = needNewReadStreamClass(var8);
      }

      boolean var20 = false;

      for(int var15 = 0; var15 < var6.length; ++var15) {
         if (needNewWriteStreamClass(var6[var15])) {
            var20 = true;
            break;
         }
      }

      var1.plnI("try {");
      if (var13) {
         var1.pln(idExtInputStream + " " + var10 + " = null;");
      } else {
         var1.pln(idInputStream + " " + var10 + " = null;");
      }

      var1.plnI("try {");
      String var21 = "null";
      if (var20) {
         var1.plnI(idExtOutputStream + " " + var11 + " = ");
         var1.pln("(" + idExtOutputStream + ")");
         var1.pln("_request(\"" + var5 + "\", true);");
         var1.pO();
      } else {
         var1.pln("OutputStream " + var11 + " = _request(\"" + var5 + "\", true);");
      }

      if (var6.length > 0) {
         this.writeMarshalArguments(var1, var11, var6, var7);
         var1.pln();
      }

      if (var8.isType(1)) {
         var1.pln("_invoke(" + var11 + ");");
      } else {
         if (var13) {
            var1.plnI(var10 + " = (" + idExtInputStream + ")_invoke(" + var11 + ");");
            var1.pO();
         } else {
            var1.pln(var10 + " = _invoke(" + var11 + ");");
         }

         var1.p("return ");
         this.writeUnmarshalArgument(var1, var10, var8, (String)null);
         var1.pln();
      }

      var1.pOlnI("} catch (" + this.getName(idApplicationException) + " " + var12 + ") {");
      if (var13) {
         var1.pln(var10 + " = (" + idExtInputStream + ") " + var12 + ".getInputStream();");
      } else {
         var1.pln(var10 + " = " + var12 + ".getInputStream();");
      }

      boolean var16 = false;
      boolean var17 = false;

      int var18;
      for(var18 = 0; var18 < var9.length; ++var18) {
         if (var9[var18].getIdentifier() != idRemoteException) {
            if (var9[var18].isIDLEntityException() && !var9[var18].isCORBAUserException()) {
               if (!var17 && !var16) {
                  var1.pln("String $_id = " + var12 + ".getId();");
                  var17 = true;
               }

               String var19 = IDLNames.replace(var9[var18].getQualifiedIDLName(false), "::", ".");
               var19 = var19 + "Helper";
               var1.plnI("if ($_id.equals(" + var19 + ".id())) {");
               var1.pln("throw " + var19 + ".read(" + var10 + ");");
            } else {
               if (!var17 && !var16) {
                  var1.pln("String $_id = " + var10 + ".read_string();");
                  var17 = true;
                  var16 = true;
               } else if (var17 && !var16) {
                  var1.pln("$_id = " + var10 + ".read_string();");
                  var16 = true;
               }

               var1.plnI("if ($_id.equals(\"" + this.getExceptionRepositoryID(var9[var18]) + "\")) {");
               var1.pln("throw (" + this.getExceptionName(var9[var18]) + ") " + var10 + ".read_value(" + this.getExceptionName(var9[var18]) + ".class);");
            }

            var1.pOln("}");
         }
      }

      if (!var17 && !var16) {
         var1.pln("String $_id = " + var10 + ".read_string();");
         var17 = true;
         var16 = true;
      } else if (var17 && !var16) {
         var1.pln("$_id = " + var10 + ".read_string();");
         var16 = true;
      }

      var1.pln("throw new UnexpectedException($_id);");
      var1.pOlnI("} catch (" + this.getName(idRemarshalException) + " " + var12 + ") {");
      if (!var8.isType(1)) {
         var1.p("return ");
      }

      var1.p(var4 + "(");

      for(var18 = 0; var18 < var6.length; ++var18) {
         if (var18 > 0) {
            var1.p(",");
         }

         var1.p(var7[var18]);
      }

      var1.pln(");");
      var1.pOlnI("} finally {");
      var1.pln("_releaseReply(" + var10 + ");");
      var1.pOln("}");
      var1.pOlnI("} catch (SystemException " + var12 + ") {");
      var1.pln("throw Util.mapSystemException(" + var12 + ");");
      var1.pOln("}");
   }

   void allocateResult(IndentingWriter var1, Type var2) throws IOException {
      if (!var2.isType(1)) {
         String var3 = this.testUtil(this.getName(var2), var2);
         var1.p(var3 + " result = ");
      }

   }

   int getTypeCode(Type var1) {
      int var2 = var1.getTypeCode();
      if (var1 instanceof CompoundType && ((CompoundType)var1).isAbstractBase()) {
         var2 = 8192;
      }

      return var2;
   }

   void writeMarshalArgument(IndentingWriter var1, String var2, Type var3, String var4) throws IOException {
      int var5 = this.getTypeCode(var3);
      switch (var5) {
         case 2:
            var1.p(var2 + ".write_boolean(" + var4 + ");");
            break;
         case 4:
            var1.p(var2 + ".write_octet(" + var4 + ");");
            break;
         case 8:
            var1.p(var2 + ".write_wchar(" + var4 + ");");
            break;
         case 16:
            var1.p(var2 + ".write_short(" + var4 + ");");
            break;
         case 32:
            var1.p(var2 + ".write_long(" + var4 + ");");
            break;
         case 64:
            var1.p(var2 + ".write_longlong(" + var4 + ");");
            break;
         case 128:
            var1.p(var2 + ".write_float(" + var4 + ");");
            break;
         case 256:
            var1.p(var2 + ".write_double(" + var4 + ");");
            break;
         case 512:
            var1.p(var2 + ".write_value(" + var4 + "," + this.getName(var3) + ".class);");
            break;
         case 1024:
            var1.p("Util.writeAny(" + var2 + "," + var4 + ");");
            break;
         case 2048:
            var1.p(var2 + ".write_Object(" + var4 + ");");
            break;
         case 4096:
            var1.p("Util.writeRemoteObject(" + var2 + "," + var4 + ");");
            break;
         case 8192:
            var1.p("Util.writeAbstractObject(" + var2 + "," + var4 + ");");
            break;
         case 16384:
            var1.p(var2 + ".write_value((Serializable)" + var4 + "," + this.getName(var3) + ".class);");
            break;
         case 32768:
            var1.p(var2 + ".write_value(" + var4 + "," + this.getName(var3) + ".class);");
            break;
         case 65536:
            var1.p(var2 + ".write_value((Serializable)" + var4 + "," + this.getName(var3) + ".class);");
            break;
         case 131072:
            var1.p(var2 + ".write_value((Serializable)" + var4 + "," + this.getName(var3) + ".class);");
            break;
         case 262144:
            this.castArray = true;
            var1.p(var2 + ".write_value(cast_array(" + var4 + ")," + this.getName(var3) + ".class);");
            break;
         case 524288:
            var1.p("Util.writeRemoteObject(" + var2 + "," + var4 + ");");
            break;
         default:
            throw new Error("unexpected type code: " + var5);
      }

   }

   void writeUnmarshalArgument(IndentingWriter var1, String var2, Type var3, String var4) throws IOException {
      int var5 = this.getTypeCode(var3);
      if (var4 != null) {
         var1.p(var4 + " = ");
      }

      switch (var5) {
         case 2:
            var1.p(var2 + ".read_boolean();");
            break;
         case 4:
            var1.p(var2 + ".read_octet();");
            break;
         case 8:
            var1.p(var2 + ".read_wchar();");
            break;
         case 16:
            var1.p(var2 + ".read_short();");
            break;
         case 32:
            var1.p(var2 + ".read_long();");
            break;
         case 64:
            var1.p(var2 + ".read_longlong();");
            break;
         case 128:
            var1.p(var2 + ".read_float();");
            break;
         case 256:
            var1.p(var2 + ".read_double();");
            break;
         case 512:
            var1.p("(String) " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 1024:
            if (var3.getIdentifier() != idJavaLangObject) {
               var1.p("(" + this.getName(var3) + ") ");
            }

            var1.p("Util.readAny(" + var2 + ");");
            break;
         case 2048:
            if (var3.getIdentifier() == idCorbaObject) {
               var1.p("(" + this.getName(var3) + ") " + var2 + ".read_Object();");
            } else {
               var1.p("(" + this.getName(var3) + ") " + var2 + ".read_Object(" + this.getStubName(var3) + ".class);");
            }
            break;
         case 4096:
            String var6 = this.testUtil(this.getName(var3), var3);
            var1.p("(" + var6 + ") PortableRemoteObject.narrow(" + var2 + ".read_Object(), " + var6 + ".class);");
            break;
         case 8192:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_abstract_interface();");
            break;
         case 16384:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 32768:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 65536:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 131072:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 262144:
            var1.p("(" + this.getName(var3) + ") " + var2 + ".read_value(" + this.getName(var3) + ".class);");
            break;
         case 524288:
            var1.p("(" + this.getName(var3) + ") PortableRemoteObject.narrow(" + var2 + ".read_Object(), " + this.getName(var3) + ".class);");
            break;
         default:
            throw new Error("unexpected type code: " + var5);
      }

   }

   String[] getAllRemoteRepIDs(CompoundType var1) {
      Type[] var3 = this.collectAllRemoteInterfaces(var1);
      int var4 = var3.length;
      boolean var5 = var1 instanceof ImplementationType;
      InterfaceType[] var6 = var1.getInterfaces();
      int var7 = this.countRemote(var6, false);
      int var8 = 0;
      String[] var2;
      int var10;
      if (var5 && var7 > 1) {
         var2 = new String[var4 + 1];
         var2[0] = this.getRepositoryID(var1);
         var8 = 1;
      } else {
         var2 = new String[var4];
         if (var4 > 1) {
            String var9 = null;
            if (var5) {
               for(var10 = 0; var10 < var6.length; ++var10) {
                  if (var6[var10].isType(4096)) {
                     var9 = var6[var10].getRepositoryID();
                     break;
                  }
               }
            } else {
               var9 = var1.getRepositoryID();
            }

            for(var10 = 0; var10 < var4; ++var10) {
               if (var3[var10].getRepositoryID() == var9) {
                  if (var10 > 0) {
                     Type var11 = var3[0];
                     var3[0] = var3[var10];
                     var3[var10] = var11;
                  }
                  break;
               }
            }
         }
      }

      int var12;
      for(var12 = 0; var12 < var3.length; ++var12) {
         var2[var8++] = this.getRepositoryID(var3[var12]);
      }

      if (this.reverseIDs) {
         var12 = 0;

         String var13;
         for(var10 = var2.length - 1; var12 < var10; var2[var10--] = var13) {
            var13 = var2[var12];
            var2[var12++] = var2[var10];
         }
      }

      return var2;
   }

   Type[] collectAllRemoteInterfaces(CompoundType var1) {
      Vector var2 = new Vector();
      this.addRemoteInterfaces(var2, var1);
      Type[] var3 = new Type[var2.size()];
      var2.copyInto(var3);
      return var3;
   }

   void addRemoteInterfaces(Vector var1, CompoundType var2) {
      if (var2 != null) {
         if (var2.isInterface() && !var1.contains(var2)) {
            var1.addElement(var2);
         }

         InterfaceType[] var3 = var2.getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4].isType(4096)) {
               this.addRemoteInterfaces(var1, var3[var4]);
            }
         }

         this.addRemoteInterfaces(var1, var2.getSuperclass());
      }

   }

   RemoteType[] getDirectRemoteInterfaces(CompoundType var1) {
      InterfaceType[] var3 = var1.getInterfaces();
      InterfaceType[] var4;
      if (var1 instanceof ImplementationType) {
         var4 = var3;
      } else {
         var4 = new InterfaceType[]{(InterfaceType)var1};
      }

      int var5 = this.countRemote(var4, false);
      if (var5 == 0) {
         throw new CompilerError("iiop.StubGenerator: No remote interfaces!");
      } else {
         RemoteType[] var2 = new RemoteType[var5];
         int var6 = 0;

         for(int var7 = 0; var7 < var4.length; ++var7) {
            if (var4[var7].isType(4096)) {
               var2[var6++] = (RemoteType)var4[var7];
            }
         }

         return var2;
      }
   }

   int countRemote(Type[] var1, boolean var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var1[var4].isType(4096) && (var2 || !var1[var4].isType(8192))) {
            ++var3;
         }
      }

      return var3;
   }

   void writeCastArray(IndentingWriter var1) throws IOException {
      if (this.castArray) {
         var1.pln();
         var1.pln("// This method is required as a work-around for");
         var1.pln("// a bug in the JDK 1.1.6 verifier.");
         var1.pln();
         var1.plnI("private " + this.getName(idJavaIoSerializable) + " cast_array(Object obj) {");
         var1.pln("return (" + this.getName(idJavaIoSerializable) + ")obj;");
         var1.pOln("}");
      }

   }

   void writeIds(IndentingWriter var1, CompoundType var2, boolean var3) throws IOException {
      var1.plnI("private static final String[] _type_ids = {");
      String[] var4 = this.getAllRemoteRepIDs(var2);
      if (var4.length > 0) {
         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var5 > 0) {
               var1.pln(", ");
            }

            var1.p("\"" + var4[var5] + "\"");
         }
      } else {
         var1.pln("\"\"");
      }

      String var7 = var2.getQualifiedName();
      boolean var6 = var3 && this.transactionalObjects.containsKey(var7);
      if (var6) {
         var1.pln(", ");
         var1.pln("\"IDL:omg.org/CosTransactions/TransactionalObject:1.0\"");
      } else if (var4.length > 0) {
         var1.pln();
      }

      var1.pOln("};");
   }

   protected void writeTie(Generator.OutputType var1, IndentingWriter var2) throws IOException {
      CompoundType var3 = (CompoundType)var1.getType();
      Object var4 = null;
      var2.pln("// Tie class generated by rmic, do not edit.");
      var2.pln("// Contents subject to change without notice.");
      var2.pln();
      this.setStandardClassesInUse(var3, false);
      this.addClassesInUse(var3, (RemoteType[])var4);
      this.writePackageAndImports(var2);
      var2.p("public class " + this.currentClass + " extends " + this.getName(this.tieBaseClass) + " implements Tie");
      if (!this.implementsRemote(var3)) {
         var2.pln(",");
         var2.p(this.getName("java.rmi.Remote"));
      }

      var2.plnI(" {");
      var2.pln();
      var2.pln("volatile private " + this.getName((Type)var3) + " target = null;");
      var2.pln();
      this.writeIds(var2, var3, true);
      var2.pln();
      var2.plnI("public void setTarget(Remote target) {");
      var2.pln("this.target = (" + this.getName((Type)var3) + ") target;");
      var2.pOln("}");
      var2.pln();
      var2.plnI("public Remote getTarget() {");
      var2.pln("return target;");
      var2.pOln("}");
      var2.pln();
      this.write_tie_thisObject_method(var2, idCorbaObject);
      var2.pln();
      this.write_tie_deactivate_method(var2);
      var2.pln();
      var2.plnI("public ORB orb() {");
      var2.pln("return _orb();");
      var2.pOln("}");
      var2.pln();
      this.write_tie_orb_method(var2);
      var2.pln();
      this.write_tie__ids_method(var2);
      CompoundType.Method[] var5 = var3.getMethods();
      this.addNamesInUse(var5);
      this.addNameInUse("target");
      this.addNameInUse("_type_ids");
      var2.pln();
      String var6 = this.getVariableName("in");
      String var7 = this.getVariableName("_in");
      String var8 = this.getVariableName("ex");
      String var9 = this.getVariableName("method");
      String var10 = this.getVariableName("reply");
      var2.plnI("public OutputStream  _invoke(String " + var9 + ", InputStream " + var7 + ", ResponseHandler " + var10 + ") throws SystemException {");
      if (var5.length > 0) {
         var2.plnI("try {");
         var2.pln(this.getName((Type)var3) + " target = this.target;");
         var2.plnI("if (target == null) {");
         var2.pln("throw new java.io.IOException();");
         var2.pOln("}");
         var2.plnI(idExtInputStream + " " + var6 + " = ");
         var2.pln("(" + idExtInputStream + ") " + var7 + ";");
         var2.pO();
         StaticStringsHash var11 = this.getStringsHash(var5);
         int var12;
         if (var11 == null) {
            for(var12 = 0; var12 < var5.length; ++var12) {
               CompoundType.Method var15 = var5[var12];
               if (var12 > 0) {
                  var2.pO("} else ");
               }

               var2.plnI("if (" + var9 + ".equals(\"" + var15.getIDLName() + "\")) {");
               this.writeTieMethod(var2, var3, var15);
            }
         } else {
            var2.plnI("switch (" + var9 + "." + var11.method + ") {");

            for(var12 = 0; var12 < var11.buckets.length; ++var12) {
               var2.plnI("case " + var11.keys[var12] + ": ");

               for(int var13 = 0; var13 < var11.buckets[var12].length; ++var13) {
                  CompoundType.Method var14 = var5[var11.buckets[var12][var13]];
                  if (var13 > 0) {
                     var2.pO("} else ");
                  }

                  var2.plnI("if (" + var9 + ".equals(\"" + var14.getIDLName() + "\")) {");
                  this.writeTieMethod(var2, var3, var14);
               }

               var2.pOln("}");
               var2.pO();
            }
         }

         if (var11 != null) {
            var2.pI();
         }

         if (var11 != null) {
            var2.pO();
         }

         var2.pOln("}");
         var2.pln("throw new " + this.getName(idBadMethodException) + "();");
         var2.pOlnI("} catch (" + this.getName(idSystemException) + " " + var8 + ") {");
         var2.pln("throw " + var8 + ";");
         var2.pOlnI("} catch (" + this.getName(idJavaLangThrowable) + " " + var8 + ") {");
         var2.pln("throw new " + this.getName(idPortableUnknownException) + "(" + var8 + ");");
         var2.pOln("}");
      } else {
         var2.pln("throw new " + this.getName(idBadMethodException) + "();");
      }

      var2.pOln("}");
      this.writeCastArray(var2);
      var2.pOln("}");
   }

   public void catchWrongPolicy(IndentingWriter var1) throws IOException {
      var1.pln("");
   }

   public void catchServantNotActive(IndentingWriter var1) throws IOException {
      var1.pln("");
   }

   public void catchObjectNotActive(IndentingWriter var1) throws IOException {
      var1.pln("");
   }

   public void write_tie_thisObject_method(IndentingWriter var1, Identifier var2) throws IOException {
      if (this.POATie) {
         var1.plnI("public " + var2 + " thisObject() {");
         var1.pln("return _this_object();");
         var1.pOln("}");
      } else {
         var1.plnI("public " + var2 + " thisObject() {");
         var1.pln("return this;");
         var1.pOln("}");
      }

   }

   public void write_tie_deactivate_method(IndentingWriter var1) throws IOException {
      if (this.POATie) {
         var1.plnI("public void deactivate() {");
         var1.pln("try{");
         var1.pln("_poa().deactivate_object(_poa().servant_to_id(this));");
         var1.pln("}catch (org.omg.PortableServer.POAPackage.WrongPolicy exception){");
         this.catchWrongPolicy(var1);
         var1.pln("}catch (org.omg.PortableServer.POAPackage.ObjectNotActive exception){");
         this.catchObjectNotActive(var1);
         var1.pln("}catch (org.omg.PortableServer.POAPackage.ServantNotActive exception){");
         this.catchServantNotActive(var1);
         var1.pln("}");
         var1.pOln("}");
      } else {
         var1.plnI("public void deactivate() {");
         var1.pln("_orb().disconnect(this);");
         var1.pln("_set_delegate(null);");
         var1.pln("target = null;");
         var1.pOln("}");
      }

   }

   public void write_tie_orb_method(IndentingWriter var1) throws IOException {
      if (this.POATie) {
         var1.plnI("public void orb(ORB orb) {");
         var1.pln("try {");
         var1.pln("    ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);");
         var1.pln("}");
         var1.pln("catch(ClassCastException e) {");
         var1.pln("    throw new org.omg.CORBA.BAD_PARAM");
         var1.pln("        (\"POA Servant requires an instance of org.omg.CORBA_2_3.ORB\");");
         var1.pln("}");
         var1.pOln("}");
      } else {
         var1.plnI("public void orb(ORB orb) {");
         var1.pln("orb.connect(this);");
         var1.pOln("}");
      }

   }

   public void write_tie__ids_method(IndentingWriter var1) throws IOException {
      if (this.POATie) {
         var1.plnI("public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId){");
         var1.pln("return (String[]) _type_ids.clone();");
         var1.pOln("}");
      } else {
         var1.plnI("public String[] _ids() { ");
         var1.pln("return (String[]) _type_ids.clone();");
         var1.pOln("}");
      }

   }

   StaticStringsHash getStringsHash(CompoundType.Method[] var1) {
      if (this.useHash && var1.length > 1) {
         String[] var2 = new String[var1.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = var1[var3].getIDLName();
         }

         return new StaticStringsHash(var2);
      } else {
         return null;
      }
   }

   static boolean needNewReadStreamClass(Type var0) {
      if (var0.isType(8192)) {
         return true;
      } else {
         return var0 instanceof CompoundType && ((CompoundType)var0).isAbstractBase() ? true : needNewWriteStreamClass(var0);
      }
   }

   static boolean needNewWriteStreamClass(Type var0) {
      switch (var0.getTypeCode()) {
         case 1:
         case 2:
         case 4:
         case 8:
         case 16:
         case 32:
         case 64:
         case 128:
         case 256:
            return false;
         case 512:
            return true;
         case 1024:
            return false;
         case 2048:
            return false;
         case 4096:
            return false;
         case 8192:
            return false;
         case 16384:
            return true;
         case 32768:
            return true;
         case 65536:
            return true;
         case 131072:
            return true;
         case 262144:
            return true;
         case 524288:
            return false;
         default:
            throw new Error("unexpected type code: " + var0.getTypeCode());
      }
   }

   String[] writeCopyArguments(CompoundType.Method var1, IndentingWriter var2) throws IOException {
      Type[] var3 = var1.getArguments();
      String[] var4 = var1.getArgumentNames();
      String[] var5 = new String[var4.length];

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = var4[var6];
      }

      boolean var14 = false;
      boolean[] var7 = new boolean[var3.length];
      int var8 = 0;
      int var9 = 0;

      int var10;
      for(var10 = 0; var10 < var3.length; ++var10) {
         if (mustCopy(var3[var10])) {
            var7[var10] = true;
            ++var8;
            var9 = var10;
            if (var3[var10].getTypeCode() != 4096 && var3[var10].getTypeCode() != 65536) {
               var14 = true;
            }
         } else {
            var7[var10] = false;
         }
      }

      if (var8 > 0) {
         if (var14) {
            for(var10 = 0; var10 < var3.length; ++var10) {
               if (var3[var10].getTypeCode() == 512) {
                  var7[var10] = true;
                  ++var8;
               }
            }
         }

         if (var8 > 1) {
            String var15 = this.getVariableName("copies");
            var2.p("Object[] " + var15 + " = Util.copyObjects(new Object[]{");
            boolean var11 = true;

            int var12;
            for(var12 = 0; var12 < var3.length; ++var12) {
               if (var7[var12]) {
                  if (!var11) {
                     var2.p(",");
                  }

                  var11 = false;
                  var2.p(var4[var12]);
               }
            }

            var2.pln("},_orb());");
            var12 = 0;

            for(int var13 = 0; var13 < var3.length; ++var13) {
               if (var7[var13]) {
                  var5[var13] = this.getVariableName(var5[var13] + "Copy");
                  var2.pln(this.getName(var3[var13]) + " " + var5[var13] + " = (" + this.getName(var3[var13]) + ") " + var15 + "[" + var12++ + "];");
               }
            }
         } else {
            var5[var9] = this.getVariableName(var5[var9] + "Copy");
            var2.pln(this.getName(var3[var9]) + " " + var5[var9] + " = (" + this.getName(var3[var9]) + ") Util.copyObject(" + var4[var9] + ",_orb());");
         }
      }

      return var5;
   }

   String getRepositoryID(Type var1) {
      return IDLNames.replace(var1.getRepositoryID(), "\\", "\\\\");
   }

   String getExceptionRepositoryID(Type var1) {
      ClassType var2 = (ClassType)var1;
      return IDLNames.getIDLRepositoryID(var2.getQualifiedIDLExceptionName(false));
   }

   String getVariableName(String var1) {
      while(this.namesInUse.contains(var1)) {
         var1 = "$" + var1;
      }

      return var1;
   }

   void addNamesInUse(CompoundType.Method[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.addNamesInUse(var1[var2]);
      }

   }

   void addNamesInUse(CompoundType.Method var1) {
      String[] var2 = var1.getArgumentNames();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.addNameInUse(var2[var3]);
      }

   }

   void addNameInUse(String var1) {
      this.namesInUse.add(var1);
   }

   static boolean mustCopy(Type var0) {
      switch (var0.getTypeCode()) {
         case 1:
         case 2:
         case 4:
         case 8:
         case 16:
         case 32:
         case 64:
         case 128:
         case 256:
         case 512:
            return false;
         case 1024:
            return true;
         case 2048:
            return false;
         case 4096:
         case 8192:
         case 16384:
         case 32768:
         case 65536:
         case 131072:
         case 262144:
         case 524288:
            return true;
         default:
            throw new Error("unexpected type code: " + var0.getTypeCode());
      }
   }

   ValueType[] getStubExceptions(CompoundType.Method var1, boolean var2) {
      ValueType[] var3 = var1.getFilteredStubExceptions(var1.getExceptions());
      if (var2) {
         Arrays.sort(var3, new UserExceptionComparator());
      }

      return var3;
   }

   ValueType[] getTieExceptions(CompoundType.Method var1) {
      return var1.getUniqueCatchList(var1.getImplExceptions());
   }

   void writeTieMethod(IndentingWriter var1, CompoundType var2, CompoundType.Method var3) throws IOException {
      String var4 = var3.getName();
      Type[] var5 = var3.getArguments();
      String[] var6 = var3.getArgumentNames();
      Type var7 = var3.getReturnType();
      ValueType[] var8 = this.getTieExceptions(var3);
      String var9 = this.getVariableName("in");
      String var10 = this.getVariableName("ex");
      String var11 = this.getVariableName("out");
      String var12 = this.getVariableName("reply");

      for(int var13 = 0; var13 < var5.length; ++var13) {
         var1.p(this.getName(var5[var13]) + " " + var6[var13] + " = ");
         this.writeUnmarshalArgument(var1, var9, var5[var13], (String)null);
         var1.pln();
      }

      boolean var17 = var8 != null;
      boolean var14 = !var7.isType(1);
      if (var17 && var14) {
         String var15 = this.testUtil(this.getName(var7), var7);
         var1.pln(var15 + " result;");
      }

      if (var17) {
         var1.plnI("try {");
      }

      if (var14) {
         if (var17) {
            var1.p("result = ");
         } else {
            var1.p(this.getName(var7) + " result = ");
         }
      }

      var1.p("target." + var4 + "(");

      int var18;
      for(var18 = 0; var18 < var6.length; ++var18) {
         if (var18 > 0) {
            var1.p(", ");
         }

         var1.p(var6[var18]);
      }

      var1.pln(");");
      if (var17) {
         for(var18 = 0; var18 < var8.length; ++var18) {
            var1.pOlnI("} catch (" + this.getName((Type)var8[var18]) + " " + var10 + ") {");
            if (var8[var18].isIDLEntityException() && !var8[var18].isCORBAUserException()) {
               String var16 = IDLNames.replace(var8[var18].getQualifiedIDLName(false), "::", ".");
               var16 = var16 + "Helper";
               var1.pln(idOutputStream + " " + var11 + " = " + var12 + ".createExceptionReply();");
               var1.pln(var16 + ".write(" + var11 + "," + var10 + ");");
            } else {
               var1.pln("String id = \"" + this.getExceptionRepositoryID(var8[var18]) + "\";");
               var1.plnI(idExtOutputStream + " " + var11 + " = ");
               var1.pln("(" + idExtOutputStream + ") " + var12 + ".createExceptionReply();");
               var1.pOln(var11 + ".write_string(id);");
               var1.pln(var11 + ".write_value(" + var10 + "," + this.getName((Type)var8[var18]) + ".class);");
            }

            var1.pln("return " + var11 + ";");
         }

         var1.pOln("}");
      }

      if (needNewWriteStreamClass(var7)) {
         var1.plnI(idExtOutputStream + " " + var11 + " = ");
         var1.pln("(" + idExtOutputStream + ") " + var12 + ".createReply();");
         var1.pO();
      } else {
         var1.pln("OutputStream " + var11 + " = " + var12 + ".createReply();");
      }

      if (var14) {
         this.writeMarshalArgument(var1, var11, var7, "result");
         var1.pln();
      }

      var1.pln("return " + var11 + ";");
   }

   void writeMarshalArguments(IndentingWriter var1, String var2, Type[] var3, String[] var4) throws IOException {
      if (var3.length != var4.length) {
         throw new Error("paramter type and name arrays different sizes");
      } else {
         for(int var5 = 0; var5 < var3.length; ++var5) {
            this.writeMarshalArgument(var1, var2, var3[var5], var4[var5]);
            if (var5 != var3.length - 1) {
               var1.pln();
            }
         }

      }
   }

   String testUtil(String var1, Type var2) {
      if (var1.equals("Util")) {
         String var3 = var2.getPackageName() + "." + var1;
         return var3;
      } else {
         return var1;
      }
   }
}
