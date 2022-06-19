package com.sun.tools.javah;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor8;

public class LLNI extends Gen {
   protected final char innerDelim = '$';
   protected Set doneHandleTypes;
   List fields;
   List methods;
   private boolean doubleAlign;
   private int padFieldNum = 0;
   private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

   LLNI(boolean var1, Util var2) {
      super(var2);
      this.doubleAlign = var1;
   }

   protected String getIncludes() {
      return "";
   }

   protected void write(OutputStream var1, TypeElement var2) throws Util.Exit {
      try {
         String var3 = this.mangleClassName(var2.getQualifiedName().toString());
         PrintWriter var4 = this.wrapWriter(var1);
         this.fields = ElementFilter.fieldsIn(var2.getEnclosedElements());
         this.methods = ElementFilter.methodsIn(var2.getEnclosedElements());
         this.generateDeclsForClass(var4, var2, var3);
      } catch (TypeSignature.SignatureException var5) {
         this.util.error("llni.sigerror", var5.getMessage());
      }

   }

   protected void generateDeclsForClass(PrintWriter var1, TypeElement var2, String var3) throws TypeSignature.SignatureException, Util.Exit {
      this.doneHandleTypes = new HashSet();
      this.genHandleType((PrintWriter)null, "java.lang.Class");
      this.genHandleType((PrintWriter)null, "java.lang.ClassLoader");
      this.genHandleType((PrintWriter)null, "java.lang.Object");
      this.genHandleType((PrintWriter)null, "java.lang.String");
      this.genHandleType((PrintWriter)null, "java.lang.Thread");
      this.genHandleType((PrintWriter)null, "java.lang.ThreadGroup");
      this.genHandleType((PrintWriter)null, "java.lang.Throwable");
      var1.println("/* LLNI Header for class " + var2.getQualifiedName() + " */" + this.lineSep);
      var1.println("#ifndef _Included_" + var3);
      var1.println("#define _Included_" + var3);
      var1.println("#include \"typedefs.h\"");
      var1.println("#include \"llni.h\"");
      var1.println("#include \"jni.h\"" + this.lineSep);
      this.forwardDecls(var1, var2);
      this.structSectionForClass(var1, var2, var3);
      this.methodSectionForClass(var1, var2, var3);
      var1.println("#endif");
   }

   protected void genHandleType(PrintWriter var1, String var2) {
      String var3 = this.mangleClassName(var2);
      if (!this.doneHandleTypes.contains(var3)) {
         this.doneHandleTypes.add(var3);
         if (var1 != null) {
            var1.println("#ifndef DEFINED_" + var3);
            var1.println("    #define DEFINED_" + var3);
            var1.println("    GEN_HANDLE_TYPES(" + var3 + ");");
            var1.println("#endif" + this.lineSep);
         }
      }

   }

   protected String mangleClassName(String var1) {
      return var1.replace('.', '_').replace('/', '_').replace('$', '_');
   }

   protected void forwardDecls(PrintWriter var1, TypeElement var2) throws TypeSignature.SignatureException {
      TypeElement var3 = this.elems.getTypeElement("java.lang.Object");
      if (!var2.equals(var3)) {
         this.genHandleType(var1, var2.getQualifiedName().toString());
         TypeElement var4 = (TypeElement)((TypeElement)this.types.asElement(var2.getSuperclass()));
         if (var4 != null) {
            String var5 = var4.getQualifiedName().toString();
            this.forwardDecls(var1, var4);
         }

         Iterator var11 = this.fields.iterator();

         TypeMirror var7;
         String var10;
         while(var11.hasNext()) {
            VariableElement var6 = (VariableElement)var11.next();
            if (!var6.getModifiers().contains(Modifier.STATIC)) {
               var7 = this.types.erasure(var6.asType());
               TypeSignature var8 = new TypeSignature(this.elems);
               String var9 = var8.qualifiedTypeName(var7);
               var10 = var8.getTypeSignature(var9);
               if (var10.charAt(0) != '[') {
                  this.forwardDeclsFromSig(var1, var10);
               }
            }
         }

         var11 = this.methods.iterator();

         while(var11.hasNext()) {
            ExecutableElement var12 = (ExecutableElement)var11.next();
            if (var12.getModifiers().contains(Modifier.NATIVE)) {
               var7 = this.types.erasure(var12.getReturnType());
               String var13 = this.signature(var12);
               TypeSignature var14 = new TypeSignature(this.elems);
               var10 = var14.getTypeSignature(var13, var7);
               if (var10.charAt(0) != '[') {
                  this.forwardDeclsFromSig(var1, var10);
               }
            }
         }

      }
   }

   protected void forwardDeclsFromSig(PrintWriter var1, String var2) {
      int var3 = var2.length();
      int var4 = var2.charAt(0) == '(' ? 1 : 0;

      while(true) {
         while(var4 < var3) {
            if (var2.charAt(var4) == 'L') {
               int var5;
               for(var5 = var4 + 1; var2.charAt(var5) != ';'; ++var5) {
               }

               this.genHandleType(var1, var2.substring(var4 + 1, var5));
               var4 = var5 + 1;
            } else {
               ++var4;
            }
         }

         return;
      }
   }

   protected void structSectionForClass(PrintWriter var1, TypeElement var2, String var3) {
      String var4 = var2.getQualifiedName().toString();
      if (var3.equals("java_lang_Object")) {
         var1.println("/* struct java_lang_Object is defined in typedefs.h. */");
         var1.println();
      } else {
         var1.println("#if !defined(__i386)");
         var1.println("#pragma pack(4)");
         var1.println("#endif");
         var1.println();
         var1.println("struct " + var3 + " {");
         var1.println("    ObjHeader h;");
         var1.print(this.fieldDefs(var2, var3));
         if (var4.equals("java.lang.Class")) {
            var1.println("    Class *LLNI_mask(cClass);  /* Fake field; don't access (see oobj.h) */");
         }

         var1.println("};" + this.lineSep + this.lineSep + "#pragma pack()");
         var1.println();
      }
   }

   private boolean doField(FieldDefsRes var1, VariableElement var2, String var3, boolean var4) {
      String var5 = this.addStructMember(var2, var3, var4);
      if (var5 != null) {
         if (!var1.printedOne) {
            if (var1.bottomMost) {
               if (var1.s.length() != 0) {
                  var1.s = var1.s + "    /* local members: */" + this.lineSep;
               }
            } else {
               var1.s = var1.s + "    /* inherited members from " + var1.className + ": */" + this.lineSep;
            }

            var1.printedOne = true;
         }

         var1.s = var1.s + var5;
         return true;
      } else {
         return false;
      }
   }

   private int doTwoWordFields(FieldDefsRes var1, TypeElement var2, int var3, String var4, boolean var5) {
      boolean var6 = true;
      List var7 = ElementFilter.fieldsIn(var2.getEnclosedElements());
      Iterator var8 = var7.iterator();

      while(true) {
         VariableElement var9;
         boolean var11;
         do {
            if (!var8.hasNext()) {
               return var3;
            }

            var9 = (VariableElement)var8.next();
            TypeKind var10 = var9.asType().getKind();
            var11 = var10 == TypeKind.LONG || var10 == TypeKind.DOUBLE;
         } while(!var11);

         if (this.doField(var1, var9, var4, var6 && var5)) {
            var3 += 8;
            var6 = false;
         }
      }
   }

   String fieldDefs(TypeElement var1, String var2) {
      FieldDefsRes var3 = this.fieldDefs(var1, var2, true);
      return var3.s;
   }

   FieldDefsRes fieldDefs(TypeElement var1, String var2, boolean var3) {
      boolean var6 = false;
      TypeElement var7 = (TypeElement)this.types.asElement(var1.getSuperclass());
      FieldDefsRes var4;
      int var5;
      if (var7 != null) {
         String var8 = var7.getQualifiedName().toString();
         var4 = new FieldDefsRes(var1, this.fieldDefs(var7, var2, false), var3);
         var5 = var4.parent.byteSize;
      } else {
         var4 = new FieldDefsRes(var1, (FieldDefsRes)null, var3);
         var5 = 0;
      }

      List var13 = ElementFilter.fieldsIn(var1.getEnclosedElements());
      Iterator var9 = var13.iterator();

      while(true) {
         VariableElement var10;
         boolean var12;
         do {
            if (!var9.hasNext()) {
               if (this.doubleAlign && !var6) {
                  if (var5 % 8 != 0) {
                     var5 += 4;
                  }

                  var5 = this.doTwoWordFields(var4, var1, var5, var2, true);
               }

               var4.byteSize = var5;
               return var4;
            }

            var10 = (VariableElement)var9.next();
            if (this.doubleAlign && !var6 && var5 % 8 == 0) {
               var5 = this.doTwoWordFields(var4, var1, var5, var2, false);
               var6 = true;
            }

            TypeKind var11 = var10.asType().getKind();
            var12 = var11 == TypeKind.LONG || var11 == TypeKind.DOUBLE;
         } while(this.doubleAlign && var12);

         if (this.doField(var4, var10, var2, false)) {
            var5 += 4;
         }
      }
   }

   protected String addStructMember(VariableElement var1, String var2, boolean var3) {
      String var4 = null;
      if (var1.getModifiers().contains(Modifier.STATIC)) {
         var4 = this.addStaticStructMember(var1, var2);
      } else {
         TypeMirror var5 = this.types.erasure(var1.asType());
         if (var3) {
            (new StringBuilder()).append("    java_int padWord").append(this.padFieldNum++).append(";").append(this.lineSep).toString();
         }

         var4 = "    " + this.llniType(var5, false, false) + " " + this.llniFieldName(var1);
         if (this.isLongOrDouble(var5)) {
            var4 = var4 + "[2]";
         }

         var4 = var4 + ";" + this.lineSep;
      }

      return var4;
   }

   protected String addStaticStructMember(VariableElement var1, String var2) {
      String var3 = null;
      Object var4 = null;
      if (!var1.getModifiers().contains(Modifier.STATIC)) {
         return var3;
      } else if (!var1.getModifiers().contains(Modifier.FINAL)) {
         return var3;
      } else {
         var4 = var1.getConstantValue();
         if (var4 != null) {
            String var5 = var2 + "_" + var1.getSimpleName();
            String var6 = null;
            long var7 = 0L;
            if (!(var4 instanceof Byte) && !(var4 instanceof Short) && !(var4 instanceof Integer)) {
               if (var4 instanceof Long) {
                  var6 = isWindows ? "i64" : "LL";
                  var7 = (Long)var4;
               } else if (var4 instanceof Float) {
                  var6 = "f";
               } else if (var4 instanceof Double) {
                  var6 = "";
               } else if (var4 instanceof Character) {
                  var6 = "L";
                  Character var9 = (Character)var4;
                  var7 = (long)(var9 & '\uffff');
               }
            } else {
               var6 = "L";
               var7 = (long)((Number)var4).intValue();
            }

            if (var6 != null) {
               if ((!var6.equals("L") || var7 != -2147483648L) && (!var6.equals("LL") || var7 != Long.MIN_VALUE)) {
                  if (!var6.equals("L") && !var6.endsWith("LL")) {
                     var3 = "    #undef  " + var5 + this.lineSep + "    #define " + var5 + " " + var4 + var6 + this.lineSep;
                  } else {
                     var3 = "    #undef  " + var5 + this.lineSep + "    #define " + var5 + " " + var7 + var6 + this.lineSep;
                  }
               } else {
                  var3 = "    #undef  " + var5 + this.lineSep + "    #define " + var5 + " (" + (var7 + 1L) + var6 + "-1)" + this.lineSep;
               }
            }
         }

         return var3;
      }
   }

   protected void methodSectionForClass(PrintWriter var1, TypeElement var2, String var3) throws TypeSignature.SignatureException, Util.Exit {
      String var4 = this.methodDecls(var2, var3);
      if (var4.length() != 0) {
         var1.println("/* Native method declarations: */" + this.lineSep);
         var1.println("#ifdef __cplusplus");
         var1.println("extern \"C\" {");
         var1.println("#endif" + this.lineSep);
         var1.println(var4);
         var1.println("#ifdef __cplusplus");
         var1.println("}");
         var1.println("#endif");
      }

   }

   protected String methodDecls(TypeElement var1, String var2) throws TypeSignature.SignatureException, Util.Exit {
      String var3 = "";
      Iterator var4 = this.methods.iterator();

      while(var4.hasNext()) {
         ExecutableElement var5 = (ExecutableElement)var4.next();
         if (var5.getModifiers().contains(Modifier.NATIVE)) {
            var3 = var3 + this.methodDecl(var5, var1, var2);
         }
      }

      return var3;
   }

   protected String methodDecl(ExecutableElement var1, TypeElement var2, String var3) throws TypeSignature.SignatureException, Util.Exit {
      String var4 = null;
      TypeMirror var5 = this.types.erasure(var1.getReturnType());
      String var6 = this.signature(var1);
      TypeSignature var7 = new TypeSignature(this.elems);
      String var8 = var7.getTypeSignature(var6, var5);
      boolean var9 = this.needLongName(var1, var2);
      if (var8.charAt(0) != '(') {
         this.util.error("invalid.method.signature", var8);
      }

      var4 = "JNIEXPORT " + this.jniType(var5) + " JNICALL" + this.lineSep + this.jniMethodName(var1, var3, var9) + "(JNIEnv *, " + this.cRcvrDecl(var1, var3);
      List var10 = var1.getParameters();
      ArrayList var11 = new ArrayList();
      Iterator var12 = var10.iterator();

      while(var12.hasNext()) {
         VariableElement var13 = (VariableElement)var12.next();
         var11.add(this.types.erasure(var13.asType()));
      }

      TypeMirror var14;
      for(var12 = var11.iterator(); var12.hasNext(); var4 = var4 + ", " + this.jniType(var14)) {
         var14 = (TypeMirror)var12.next();
      }

      var4 = var4 + ");" + this.lineSep;
      return var4;
   }

   protected final boolean needLongName(ExecutableElement var1, TypeElement var2) {
      Name var3 = var1.getSimpleName();
      Iterator var4 = this.methods.iterator();

      ExecutableElement var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (ExecutableElement)var4.next();
      } while(var5 == var1 || !var5.getModifiers().contains(Modifier.NATIVE) || !var3.equals(var5.getSimpleName()));

      return true;
   }

   protected final String jniMethodName(ExecutableElement var1, String var2, boolean var3) throws TypeSignature.SignatureException {
      String var4 = "Java_" + var2 + "_" + var1.getSimpleName();
      if (var3) {
         TypeMirror var5 = this.types.erasure(var1.getReturnType());
         List var6 = var1.getParameters();
         ArrayList var7 = new ArrayList();
         Iterator var8 = var6.iterator();

         while(var8.hasNext()) {
            VariableElement var9 = (VariableElement)var8.next();
            var7.add(this.types.erasure(var9.asType()));
         }

         var4 = var4 + "__";

         String var12;
         for(var8 = var7.iterator(); var8.hasNext(); var4 = var4 + this.nameToIdentifier(var12)) {
            TypeMirror var13 = (TypeMirror)var8.next();
            String var10 = var13.toString();
            TypeSignature var11 = new TypeSignature(this.elems);
            var12 = var11.getTypeSignature(var10);
         }
      }

      return var4;
   }

   protected final String jniType(TypeMirror var1) throws Util.Exit {
      TypeElement var2 = this.elems.getTypeElement("java.lang.Throwable");
      TypeElement var3 = this.elems.getTypeElement("java.lang.Class");
      TypeElement var4 = this.elems.getTypeElement("java.lang.String");
      Element var5 = this.types.asElement(var1);
      switch (var1.getKind()) {
         case BOOLEAN:
            return "jboolean";
         case BYTE:
            return "jbyte";
         case CHAR:
            return "jchar";
         case SHORT:
            return "jshort";
         case INT:
            return "jint";
         case LONG:
            return "jlong";
         case FLOAT:
            return "jfloat";
         case DOUBLE:
            return "jdouble";
         case ARRAY:
            TypeMirror var6 = ((ArrayType)var1).getComponentType();
            switch (var6.getKind()) {
               case BOOLEAN:
                  return "jbooleanArray";
               case BYTE:
                  return "jbyteArray";
               case CHAR:
                  return "jcharArray";
               case SHORT:
                  return "jshortArray";
               case INT:
                  return "jintArray";
               case LONG:
                  return "jlongArray";
               case FLOAT:
                  return "jfloatArray";
               case DOUBLE:
                  return "jdoubleArray";
               case ARRAY:
               case DECLARED:
                  return "jobjectArray";
               default:
                  throw new Error(var6.toString());
            }
         case DECLARED:
            if (var5.equals(var4)) {
               return "jstring";
            } else if (this.types.isAssignable(var1, var2.asType())) {
               return "jthrowable";
            } else {
               if (this.types.isAssignable(var1, var3.asType())) {
                  return "jclass";
               }

               return "jobject";
            }
         case VOID:
            return "void";
         default:
            this.util.bug("jni.unknown.type");
            return null;
      }
   }

   protected String llniType(TypeMirror var1, boolean var2, boolean var3) {
      String var4 = null;
      switch (var1.getKind()) {
         case BOOLEAN:
         case BYTE:
         case CHAR:
         case SHORT:
         case INT:
            var4 = "java_int";
            break;
         case LONG:
            var4 = var3 ? "java_long" : "val32 /* java_long */";
            break;
         case FLOAT:
            var4 = "java_float";
            break;
         case DOUBLE:
            var4 = var3 ? "java_double" : "val32 /* java_double */";
            break;
         case ARRAY:
            TypeMirror var6 = ((ArrayType)var1).getComponentType();
            switch (var6.getKind()) {
               case BOOLEAN:
                  var4 = "IArrayOfBoolean";
                  break;
               case BYTE:
                  var4 = "IArrayOfByte";
                  break;
               case CHAR:
                  var4 = "IArrayOfChar";
                  break;
               case SHORT:
                  var4 = "IArrayOfShort";
                  break;
               case INT:
                  var4 = "IArrayOfInt";
                  break;
               case LONG:
                  var4 = "IArrayOfLong";
                  break;
               case FLOAT:
                  var4 = "IArrayOfFloat";
                  break;
               case DOUBLE:
                  var4 = "IArrayOfDouble";
                  break;
               case ARRAY:
               case DECLARED:
                  var4 = "IArrayOfRef";
                  break;
               default:
                  throw new Error(var6.getKind() + " " + var6);
            }

            if (!var2) {
               var4 = "DEREFERENCED_" + var4;
            }
            break;
         case DECLARED:
            TypeElement var5 = (TypeElement)this.types.asElement(var1);
            var4 = "I" + this.mangleClassName(var5.getQualifiedName().toString());
            if (!var2) {
               var4 = "DEREFERENCED_" + var4;
            }
            break;
         case VOID:
            var4 = "void";
            break;
         default:
            throw new Error(var1.getKind() + " " + var1);
      }

      return var4;
   }

   protected final String cRcvrDecl(Element var1, String var2) {
      return var1.getModifiers().contains(Modifier.STATIC) ? "jclass" : "jobject";
   }

   protected String maskName(String var1) {
      return "LLNI_mask(" + var1 + ")";
   }

   protected String llniFieldName(VariableElement var1) {
      return this.maskName(var1.getSimpleName().toString());
   }

   protected final boolean isLongOrDouble(TypeMirror var1) {
      SimpleTypeVisitor8 var2 = new SimpleTypeVisitor8() {
         public Boolean defaultAction(TypeMirror var1, Void var2) {
            return false;
         }

         public Boolean visitArray(ArrayType var1, Void var2) {
            return (Boolean)this.visit(var1.getComponentType(), var2);
         }

         public Boolean visitPrimitive(PrimitiveType var1, Void var2) {
            TypeKind var3 = var1.getKind();
            return var3 == TypeKind.LONG || var3 == TypeKind.DOUBLE;
         }
      };
      return (Boolean)var2.visit(var1, (Object)null);
   }

   protected final String nameToIdentifier(String var1) {
      int var2 = var1.length();
      StringBuilder var3 = new StringBuilder(var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var1.charAt(var4);
         if (this.isASCIILetterOrDigit(var5)) {
            var3.append(var5);
         } else if (var5 == '/') {
            var3.append('_');
         } else if (var5 == '.') {
            var3.append('_');
         } else if (var5 == '_') {
            var3.append("_1");
         } else if (var5 == ';') {
            var3.append("_2");
         } else if (var5 == '[') {
            var3.append("_3");
         } else {
            var3.append("_0" + var5);
         }
      }

      return new String(var3);
   }

   protected final boolean isASCIILetterOrDigit(char var1) {
      return var1 >= 'A' && var1 <= 'Z' || var1 >= 'a' && var1 <= 'z' || var1 >= '0' && var1 <= '9';
   }

   private static class FieldDefsRes {
      public String className;
      public FieldDefsRes parent;
      public String s;
      public int byteSize;
      public boolean bottomMost;
      public boolean printedOne = false;

      FieldDefsRes(TypeElement var1, FieldDefsRes var2, boolean var3) {
         this.className = var1.getQualifiedName().toString();
         this.parent = var2;
         this.bottomMost = var3;
         boolean var4 = false;
         if (var2 == null) {
            this.s = "";
         } else {
            this.s = var2.s;
         }

      }
   }
}
