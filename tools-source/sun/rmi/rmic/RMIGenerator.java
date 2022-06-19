package sun.rmi.rmic;

import com.sun.corba.se.impl.util.Utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class RMIGenerator implements RMIConstants, Generator {
   private static final Hashtable versionOptions = new Hashtable();
   private BatchEnvironment env;
   private RemoteClass remoteClass;
   private int version;
   private RemoteClass.Method[] remoteMethods;
   private Identifier remoteClassName;
   private Identifier stubClassName;
   private Identifier skeletonClassName;
   private ClassDefinition cdef;
   private File destDir;
   private File stubFile;
   private File skeletonFile;
   private String[] methodFieldNames;
   private ClassDefinition defException;
   private ClassDefinition defRemoteException;
   private ClassDefinition defRuntimeException;

   public RMIGenerator() {
      this.version = 3;
   }

   public boolean parseArgs(String[] var1, Main var2) {
      String var3 = null;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var1[var4] != null) {
            String var5 = var1[var4].toLowerCase();
            if (versionOptions.containsKey(var5)) {
               if (var3 != null && !var3.equals(var5)) {
                  var2.error("rmic.cannot.use.both", var3, var5);
                  return false;
               }

               var3 = var5;
               this.version = (Integer)versionOptions.get(var5);
               var1[var4] = null;
            }
         }
      }

      return true;
   }

   public void generate(BatchEnvironment var1, ClassDefinition var2, File var3) {
      RemoteClass var4 = RemoteClass.forClass(var1, var2);
      if (var4 != null) {
         RMIGenerator var5;
         try {
            var5 = new RMIGenerator(var1, var2, var3, var4, this.version);
         } catch (ClassNotFound var7) {
            var1.error(0L, "rmic.class.not.found", var7.name);
            return;
         }

         var5.generate();
      }
   }

   private void generate() {
      this.env.addGeneratedFile(this.stubFile);

      IndentingWriter var1;
      try {
         var1 = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(this.stubFile)));
         this.writeStub(var1);
         var1.close();
         if (this.env.verbose()) {
            this.env.output(Main.getText("rmic.wrote", this.stubFile.getPath()));
         }

         this.env.parseFile(new ClassFile(this.stubFile));
      } catch (IOException var4) {
         this.env.error(0L, "cant.write", this.stubFile.toString());
         return;
      }

      if (this.version != 1 && this.version != 2) {
         File var5 = Util.getOutputDirectoryFor(this.remoteClassName, this.destDir, this.env);
         File var2 = new File(var5, this.skeletonClassName.getName().toString() + ".class");
         this.skeletonFile.delete();
         var2.delete();
      } else {
         this.env.addGeneratedFile(this.skeletonFile);

         try {
            var1 = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(this.skeletonFile)));
            this.writeSkeleton(var1);
            var1.close();
            if (this.env.verbose()) {
               this.env.output(Main.getText("rmic.wrote", this.skeletonFile.getPath()));
            }

            this.env.parseFile(new ClassFile(this.skeletonFile));
         } catch (IOException var3) {
            this.env.error(0L, "cant.write", this.stubFile.toString());
            return;
         }
      }

   }

   protected static File sourceFileForClass(Identifier var0, Identifier var1, File var2, BatchEnvironment var3) {
      File var4 = Util.getOutputDirectoryFor(var0, var2, var3);
      String var5 = Names.mangleClass(var1).getName().toString();
      String var6;
      if (var5.endsWith("_Skel")) {
         var6 = var0.getName().toString();
         File var7 = new File(var4, Utility.tieName(var6) + ".class");
         if (var7.exists() && !var3.getMain().iiopGeneration) {
            var3.error(0L, "warn.rmic.tie.found", var6, var7.getAbsolutePath());
         }
      }

      var6 = var5 + ".java";
      return new File(var4, var6);
   }

   private RMIGenerator(BatchEnvironment var1, ClassDefinition var2, File var3, RemoteClass var4, int var5) throws ClassNotFound {
      this.destDir = var3;
      this.cdef = var2;
      this.env = var1;
      this.remoteClass = var4;
      this.version = var5;
      this.remoteMethods = var4.getRemoteMethods();
      this.remoteClassName = var4.getName();
      this.stubClassName = Names.stubFor(this.remoteClassName);
      this.skeletonClassName = Names.skeletonFor(this.remoteClassName);
      this.methodFieldNames = nameMethodFields(this.remoteMethods);
      this.stubFile = sourceFileForClass(this.remoteClassName, this.stubClassName, var3, var1);
      this.skeletonFile = sourceFileForClass(this.remoteClassName, this.skeletonClassName, var3, var1);
      this.defException = var1.getClassDeclaration(idJavaLangException).getClassDefinition(var1);
      this.defRemoteException = var1.getClassDeclaration(idRemoteException).getClassDefinition(var1);
      this.defRuntimeException = var1.getClassDeclaration(idJavaLangRuntimeException).getClassDefinition(var1);
   }

   private void writeStub(IndentingWriter var1) throws IOException {
      var1.pln("// Stub class generated by rmic, do not edit.");
      var1.pln("// Contents subject to change without notice.");
      var1.pln();
      if (this.remoteClassName.isQualified()) {
         var1.pln("package " + this.remoteClassName.getQualifier() + ";");
         var1.pln();
      }

      var1.plnI("public final class " + Names.mangleClass(this.stubClassName.getName()));
      var1.pln("extends " + idRemoteStub);
      ClassDefinition[] var2 = this.remoteClass.getRemoteInterfaces();
      int var3;
      if (var2.length > 0) {
         var1.p("implements ");

         for(var3 = 0; var3 < var2.length; ++var3) {
            if (var3 > 0) {
               var1.p(", ");
            }

            var1.p(var2[var3].getName().toString());
         }

         var1.pln();
      }

      var1.pOlnI("{");
      if (this.version == 1 || this.version == 2) {
         this.writeOperationsArray(var1);
         var1.pln();
         this.writeInterfaceHash(var1);
         var1.pln();
      }

      if (this.version == 2 || this.version == 3) {
         var1.pln("private static final long serialVersionUID = 2;");
         var1.pln();
         if (this.methodFieldNames.length > 0) {
            if (this.version == 2) {
               var1.pln("private static boolean useNewInvoke;");
            }

            this.writeMethodFieldDeclarations(var1);
            var1.pln();
            var1.plnI("static {");
            var1.plnI("try {");
            if (this.version == 2) {
               var1.plnI(idRemoteRef + ".class.getMethod(\"invoke\",");
               var1.plnI("new java.lang.Class[] {");
               var1.pln(idRemote + ".class,");
               var1.pln("java.lang.reflect.Method.class,");
               var1.pln("java.lang.Object[].class,");
               var1.pln("long.class");
               var1.pOln("});");
               var1.pO();
               var1.pln("useNewInvoke = true;");
            }

            this.writeMethodFieldInitializers(var1);
            var1.pOlnI("} catch (java.lang.NoSuchMethodException e) {");
            if (this.version == 2) {
               var1.pln("useNewInvoke = false;");
            } else {
               var1.plnI("throw new java.lang.NoSuchMethodError(");
               var1.pln("\"stub class initialization failed\");");
               var1.pO();
            }

            var1.pOln("}");
            var1.pOln("}");
            var1.pln();
         }
      }

      this.writeStubConstructors(var1);
      var1.pln();
      if (this.remoteMethods.length > 0) {
         var1.pln("// methods from remote interfaces");

         for(var3 = 0; var3 < this.remoteMethods.length; ++var3) {
            var1.pln();
            this.writeStubMethod(var1, var3);
         }
      }

      var1.pOln("}");
   }

   private void writeStubConstructors(IndentingWriter var1) throws IOException {
      var1.pln("// constructors");
      if (this.version == 1 || this.version == 2) {
         var1.plnI("public " + Names.mangleClass(this.stubClassName.getName()) + "() {");
         var1.pln("super();");
         var1.pOln("}");
      }

      var1.plnI("public " + Names.mangleClass(this.stubClassName.getName()) + "(" + idRemoteRef + " ref) {");
      var1.pln("super(ref);");
      var1.pOln("}");
   }

   private void writeStubMethod(IndentingWriter var1, int var2) throws IOException {
      RemoteClass.Method var3 = this.remoteMethods[var2];
      Identifier var4 = var3.getName();
      Type var5 = var3.getType();
      Type[] var6 = var5.getArgumentTypes();
      String[] var7 = nameParameters(var6);
      Type var8 = var5.getReturnType();
      ClassDeclaration[] var9 = var3.getExceptions();
      var1.pln("// implementation of " + var5.typeString(var4.toString(), true, false));
      var1.p("public " + var8 + " " + var4 + "(");

      int var10;
      for(var10 = 0; var10 < var6.length; ++var10) {
         if (var10 > 0) {
            var1.p(", ");
         }

         var1.p(var6[var10] + " " + var7[var10]);
      }

      var1.plnI(")");
      if (var9.length > 0) {
         var1.p("throws ");

         for(var10 = 0; var10 < var9.length; ++var10) {
            if (var10 > 0) {
               var1.p(", ");
            }

            var1.p(var9[var10].getName().toString());
         }

         var1.pln();
      }

      var1.pOlnI("{");
      Vector var13 = this.computeUniqueCatchList(var9);
      if (var13.size() > 0) {
         var1.plnI("try {");
      }

      if (this.version == 2) {
         var1.plnI("if (useNewInvoke) {");
      }

      if (this.version == 2 || this.version == 3) {
         if (!var8.isType(11)) {
            var1.p("Object $result = ");
         }

         var1.p("ref.invoke(this, " + this.methodFieldNames[var2] + ", ");
         if (var6.length <= 0) {
            var1.p("null");
         } else {
            var1.p("new java.lang.Object[] {");

            for(int var11 = 0; var11 < var6.length; ++var11) {
               if (var11 > 0) {
                  var1.p(", ");
               }

               var1.p(wrapArgumentCode(var6[var11], var7[var11]));
            }

            var1.p("}");
         }

         var1.pln(", " + var3.getMethodHash() + "L);");
         if (!var8.isType(11)) {
            var1.pln("return " + unwrapArgumentCode(var8, "$result") + ";");
         }
      }

      if (this.version == 2) {
         var1.pOlnI("} else {");
      }

      if (this.version == 1 || this.version == 2) {
         var1.pln(idRemoteCall + " call = ref.newCall((" + idRemoteObject + ") this, operations, " + var2 + ", interfaceHash);");
         if (var6.length > 0) {
            var1.plnI("try {");
            var1.pln("java.io.ObjectOutput out = call.getOutputStream();");
            writeMarshalArguments(var1, "out", var6, var7);
            var1.pOlnI("} catch (java.io.IOException e) {");
            var1.pln("throw new " + idMarshalException + "(\"error marshalling arguments\", e);");
            var1.pOln("}");
         }

         var1.pln("ref.invoke(call);");
         if (var8.isType(11)) {
            var1.pln("ref.done(call);");
         } else {
            var1.pln(var8 + " $result;");
            var1.plnI("try {");
            var1.pln("java.io.ObjectInput in = call.getInputStream();");
            boolean var14 = writeUnmarshalArgument(var1, "in", var8, "$result");
            var1.pln(";");
            var1.pOlnI("} catch (java.io.IOException e) {");
            var1.pln("throw new " + idUnmarshalException + "(\"error unmarshalling return\", e);");
            if (var14) {
               var1.pOlnI("} catch (java.lang.ClassNotFoundException e) {");
               var1.pln("throw new " + idUnmarshalException + "(\"error unmarshalling return\", e);");
            }

            var1.pOlnI("} finally {");
            var1.pln("ref.done(call);");
            var1.pOln("}");
            var1.pln("return $result;");
         }
      }

      if (this.version == 2) {
         var1.pOln("}");
      }

      if (var13.size() > 0) {
         Enumeration var15 = var13.elements();

         while(var15.hasMoreElements()) {
            ClassDefinition var12 = (ClassDefinition)var15.nextElement();
            var1.pOlnI("} catch (" + var12.getName() + " e) {");
            var1.pln("throw e;");
         }

         var1.pOlnI("} catch (java.lang.Exception e) {");
         var1.pln("throw new " + idUnexpectedException + "(\"undeclared checked exception\", e);");
         var1.pOln("}");
      }

      var1.pOln("}");
   }

   private Vector computeUniqueCatchList(ClassDeclaration[] var1) {
      Vector var2 = new Vector();
      var2.addElement(this.defRuntimeException);
      var2.addElement(this.defRemoteException);

      label47:
      for(int var3 = 0; var3 < var1.length; ++var3) {
         ClassDeclaration var4 = var1[var3];

         try {
            if (this.defException.subClassOf(this.env, var4)) {
               var2.clear();
               break;
            }

            if (this.defException.superClassOf(this.env, var4)) {
               int var5 = 0;

               while(var5 < var2.size()) {
                  ClassDefinition var6 = (ClassDefinition)var2.elementAt(var5);
                  if (var6.superClassOf(this.env, var4)) {
                     continue label47;
                  }

                  if (var6.subClassOf(this.env, var4)) {
                     var2.removeElementAt(var5);
                  } else {
                     ++var5;
                  }
               }

               var2.addElement(var4.getClassDefinition(this.env));
            }
         } catch (ClassNotFound var7) {
            this.env.error(0L, "class.not.found", var7.name, var4.getName());
         }
      }

      return var2;
   }

   private void writeSkeleton(IndentingWriter var1) throws IOException {
      if (this.version == 3) {
         throw new Error("should not generate skeleton for version");
      } else {
         var1.pln("// Skeleton class generated by rmic, do not edit.");
         var1.pln("// Contents subject to change without notice.");
         var1.pln();
         if (this.remoteClassName.isQualified()) {
            var1.pln("package " + this.remoteClassName.getQualifier() + ";");
            var1.pln();
         }

         var1.plnI("public final class " + Names.mangleClass(this.skeletonClassName.getName()));
         var1.pln("implements " + idSkeleton);
         var1.pOlnI("{");
         this.writeOperationsArray(var1);
         var1.pln();
         this.writeInterfaceHash(var1);
         var1.pln();
         var1.plnI("public " + idOperation + "[] getOperations() {");
         var1.pln("return (" + idOperation + "[]) operations.clone();");
         var1.pOln("}");
         var1.pln();
         var1.plnI("public void dispatch(" + idRemote + " obj, " + idRemoteCall + " call, int opnum, long hash)");
         var1.pln("throws java.lang.Exception");
         var1.pOlnI("{");
         int var2;
         if (this.version == 2) {
            var1.plnI("if (opnum < 0) {");
            if (this.remoteMethods.length > 0) {
               for(var2 = 0; var2 < this.remoteMethods.length; ++var2) {
                  if (var2 > 0) {
                     var1.pO("} else ");
                  }

                  var1.plnI("if (hash == " + this.remoteMethods[var2].getMethodHash() + "L) {");
                  var1.pln("opnum = " + var2 + ";");
               }

               var1.pOlnI("} else {");
            }

            var1.pln("throw new " + idUnmarshalException + "(\"invalid method hash\");");
            if (this.remoteMethods.length > 0) {
               var1.pOln("}");
            }

            var1.pOlnI("} else {");
         }

         var1.plnI("if (hash != interfaceHash)");
         var1.pln("throw new " + idSkeletonMismatchException + "(\"interface hash mismatch\");");
         var1.pO();
         if (this.version == 2) {
            var1.pOln("}");
         }

         var1.pln();
         var1.pln(this.remoteClassName + " server = (" + this.remoteClassName + ") obj;");
         var1.plnI("switch (opnum) {");

         for(var2 = 0; var2 < this.remoteMethods.length; ++var2) {
            this.writeSkeletonDispatchCase(var1, var2);
         }

         var1.pOlnI("default:");
         var1.pln("throw new " + idUnmarshalException + "(\"invalid method number\");");
         var1.pOln("}");
         var1.pOln("}");
         var1.pOln("}");
      }
   }

   private void writeSkeletonDispatchCase(IndentingWriter var1, int var2) throws IOException {
      RemoteClass.Method var3 = this.remoteMethods[var2];
      Identifier var4 = var3.getName();
      Type var5 = var3.getType();
      Type[] var6 = var5.getArgumentTypes();
      String[] var7 = nameParameters(var6);
      Type var8 = var5.getReturnType();
      var1.pOlnI("case " + var2 + ": // " + var5.typeString(var4.toString(), true, false));
      var1.pOlnI("{");
      int var9;
      if (var6.length > 0) {
         for(var9 = 0; var9 < var6.length; ++var9) {
            var1.pln(var6[var9] + " " + var7[var9] + ";");
         }

         var1.plnI("try {");
         var1.pln("java.io.ObjectInput in = call.getInputStream();");
         boolean var10 = writeUnmarshalArguments(var1, "in", var6, var7);
         var1.pOlnI("} catch (java.io.IOException e) {");
         var1.pln("throw new " + idUnmarshalException + "(\"error unmarshalling arguments\", e);");
         if (var10) {
            var1.pOlnI("} catch (java.lang.ClassNotFoundException e) {");
            var1.pln("throw new " + idUnmarshalException + "(\"error unmarshalling arguments\", e);");
         }

         var1.pOlnI("} finally {");
         var1.pln("call.releaseInputStream();");
         var1.pOln("}");
      } else {
         var1.pln("call.releaseInputStream();");
      }

      if (!var8.isType(11)) {
         var1.p(var8 + " $result = ");
      }

      var1.p("server." + var4 + "(");

      for(var9 = 0; var9 < var7.length; ++var9) {
         if (var9 > 0) {
            var1.p(", ");
         }

         var1.p(var7[var9]);
      }

      var1.pln(");");
      var1.plnI("try {");
      if (!var8.isType(11)) {
         var1.p("java.io.ObjectOutput out = ");
      }

      var1.pln("call.getResultStream(true);");
      if (!var8.isType(11)) {
         writeMarshalArgument(var1, "out", var8, "$result");
         var1.pln(";");
      }

      var1.pOlnI("} catch (java.io.IOException e) {");
      var1.pln("throw new " + idMarshalException + "(\"error marshalling return\", e);");
      var1.pOln("}");
      var1.pln("break;");
      var1.pOlnI("}");
      var1.pln();
   }

   private void writeOperationsArray(IndentingWriter var1) throws IOException {
      var1.plnI("private static final " + idOperation + "[] operations = {");

      for(int var2 = 0; var2 < this.remoteMethods.length; ++var2) {
         if (var2 > 0) {
            var1.pln(",");
         }

         var1.p("new " + idOperation + "(\"" + this.remoteMethods[var2].getOperationString() + "\")");
      }

      var1.pln();
      var1.pOln("};");
   }

   private void writeInterfaceHash(IndentingWriter var1) throws IOException {
      var1.pln("private static final long interfaceHash = " + this.remoteClass.getInterfaceHash() + "L;");
   }

   private void writeMethodFieldDeclarations(IndentingWriter var1) throws IOException {
      for(int var2 = 0; var2 < this.methodFieldNames.length; ++var2) {
         var1.pln("private static java.lang.reflect.Method " + this.methodFieldNames[var2] + ";");
      }

   }

   private void writeMethodFieldInitializers(IndentingWriter var1) throws IOException {
      for(int var2 = 0; var2 < this.methodFieldNames.length; ++var2) {
         var1.p(this.methodFieldNames[var2] + " = ");
         RemoteClass.Method var3 = this.remoteMethods[var2];
         MemberDefinition var4 = var3.getMemberDefinition();
         Identifier var5 = var3.getName();
         Type var6 = var3.getType();
         Type[] var7 = var6.getArgumentTypes();
         var1.p(var4.getClassDefinition().getName() + ".class.getMethod(\"" + var5 + "\", new java.lang.Class[] {");

         for(int var8 = 0; var8 < var7.length; ++var8) {
            if (var8 > 0) {
               var1.p(", ");
            }

            var1.p(var7[var8] + ".class");
         }

         var1.pln("});");
      }

   }

   private static String[] nameMethodFields(RemoteClass.Method[] var0) {
      String[] var1 = new String[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = "$method_" + var0[var2].getName() + "_" + var2;
      }

      return var1;
   }

   private static String[] nameParameters(Type[] var0) {
      String[] var1 = new String[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = "$param_" + generateNameFromType(var0[var2]) + "_" + (var2 + 1);
      }

      return var1;
   }

   private static String generateNameFromType(Type var0) {
      int var1 = var0.getTypeCode();
      switch (var1) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            return var0.toString();
         case 8:
         default:
            throw new Error("unexpected type code: " + var1);
         case 9:
            return "arrayOf_" + generateNameFromType(var0.getElementType());
         case 10:
            return Names.mangleClass(var0.getClassName().getName()).toString();
      }
   }

   private static void writeMarshalArgument(IndentingWriter var0, String var1, Type var2, String var3) throws IOException {
      int var4 = var2.getTypeCode();
      switch (var4) {
         case 0:
            var0.p(var1 + ".writeBoolean(" + var3 + ")");
            break;
         case 1:
            var0.p(var1 + ".writeByte(" + var3 + ")");
            break;
         case 2:
            var0.p(var1 + ".writeChar(" + var3 + ")");
            break;
         case 3:
            var0.p(var1 + ".writeShort(" + var3 + ")");
            break;
         case 4:
            var0.p(var1 + ".writeInt(" + var3 + ")");
            break;
         case 5:
            var0.p(var1 + ".writeLong(" + var3 + ")");
            break;
         case 6:
            var0.p(var1 + ".writeFloat(" + var3 + ")");
            break;
         case 7:
            var0.p(var1 + ".writeDouble(" + var3 + ")");
            break;
         case 8:
         default:
            throw new Error("unexpected type code: " + var4);
         case 9:
         case 10:
            var0.p(var1 + ".writeObject(" + var3 + ")");
      }

   }

   private static void writeMarshalArguments(IndentingWriter var0, String var1, Type[] var2, String[] var3) throws IOException {
      if (var2.length != var3.length) {
         throw new Error("parameter type and name arrays different sizes");
      } else {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            writeMarshalArgument(var0, var1, var2[var4], var3[var4]);
            var0.pln(";");
         }

      }
   }

   private static boolean writeUnmarshalArgument(IndentingWriter var0, String var1, Type var2, String var3) throws IOException {
      boolean var4 = false;
      if (var3 != null) {
         var0.p(var3 + " = ");
      }

      int var5 = var2.getTypeCode();
      switch (var2.getTypeCode()) {
         case 0:
            var0.p(var1 + ".readBoolean()");
            break;
         case 1:
            var0.p(var1 + ".readByte()");
            break;
         case 2:
            var0.p(var1 + ".readChar()");
            break;
         case 3:
            var0.p(var1 + ".readShort()");
            break;
         case 4:
            var0.p(var1 + ".readInt()");
            break;
         case 5:
            var0.p(var1 + ".readLong()");
            break;
         case 6:
            var0.p(var1 + ".readFloat()");
            break;
         case 7:
            var0.p(var1 + ".readDouble()");
            break;
         case 8:
         default:
            throw new Error("unexpected type code: " + var5);
         case 9:
         case 10:
            var0.p("(" + var2 + ") " + var1 + ".readObject()");
            var4 = true;
      }

      return var4;
   }

   private static boolean writeUnmarshalArguments(IndentingWriter var0, String var1, Type[] var2, String[] var3) throws IOException {
      if (var2.length != var3.length) {
         throw new Error("parameter type and name arrays different sizes");
      } else {
         boolean var4 = false;

         for(int var5 = 0; var5 < var2.length; ++var5) {
            if (writeUnmarshalArgument(var0, var1, var2[var5], var3[var5])) {
               var4 = true;
            }

            var0.pln(";");
         }

         return var4;
      }
   }

   private static String wrapArgumentCode(Type var0, String var1) {
      int var2 = var0.getTypeCode();
      switch (var2) {
         case 0:
            return "(" + var1 + " ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE)";
         case 1:
            return "new java.lang.Byte(" + var1 + ")";
         case 2:
            return "new java.lang.Character(" + var1 + ")";
         case 3:
            return "new java.lang.Short(" + var1 + ")";
         case 4:
            return "new java.lang.Integer(" + var1 + ")";
         case 5:
            return "new java.lang.Long(" + var1 + ")";
         case 6:
            return "new java.lang.Float(" + var1 + ")";
         case 7:
            return "new java.lang.Double(" + var1 + ")";
         case 8:
         default:
            throw new Error("unexpected type code: " + var2);
         case 9:
         case 10:
            return var1;
      }
   }

   private static String unwrapArgumentCode(Type var0, String var1) {
      int var2 = var0.getTypeCode();
      switch (var2) {
         case 0:
            return "((java.lang.Boolean) " + var1 + ").booleanValue()";
         case 1:
            return "((java.lang.Byte) " + var1 + ").byteValue()";
         case 2:
            return "((java.lang.Character) " + var1 + ").charValue()";
         case 3:
            return "((java.lang.Short) " + var1 + ").shortValue()";
         case 4:
            return "((java.lang.Integer) " + var1 + ").intValue()";
         case 5:
            return "((java.lang.Long) " + var1 + ").longValue()";
         case 6:
            return "((java.lang.Float) " + var1 + ").floatValue()";
         case 7:
            return "((java.lang.Double) " + var1 + ").doubleValue()";
         case 8:
         default:
            throw new Error("unexpected type code: " + var2);
         case 9:
         case 10:
            return "((" + var0 + ") " + var1 + ")";
      }
   }

   static {
      versionOptions.put("-v1.1", new Integer(1));
      versionOptions.put("-vcompat", new Integer(2));
      versionOptions.put("-v1.2", new Integer(3));
   }
}
