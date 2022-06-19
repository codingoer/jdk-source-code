package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

public class JNIWriter {
   protected static final Context.Key jniWriterKey = new Context.Key();
   private final JavaFileManager fileManager;
   JavacElements elements;
   JavacTypes types;
   private final Log log;
   private boolean verbose;
   private boolean checkAll;
   private Mangle mangler;
   private Context context;
   private Symtab syms;
   private String lineSep;
   private final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

   public static JNIWriter instance(Context var0) {
      JNIWriter var1 = (JNIWriter)var0.get(jniWriterKey);
      if (var1 == null) {
         var1 = new JNIWriter(var0);
      }

      return var1;
   }

   private JNIWriter(Context var1) {
      var1.put((Context.Key)jniWriterKey, (Object)this);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      this.log = Log.instance(var1);
      Options var2 = Options.instance(var1);
      this.verbose = var2.isSet(Option.VERBOSE);
      this.checkAll = var2.isSet("javah:full");
      this.context = var1;
      this.syms = Symtab.instance(var1);
      this.lineSep = System.getProperty("line.separator");
   }

   private void lazyInit() {
      if (this.mangler == null) {
         this.elements = JavacElements.instance(this.context);
         this.types = JavacTypes.instance(this.context);
         this.mangler = new Mangle(this.elements, this.types);
      }

   }

   public boolean needsHeader(Symbol.ClassSymbol var1) {
      if (!var1.isLocal() && (var1.flags() & 4096L) == 0L) {
         return this.checkAll ? this.needsHeader(var1.outermostClass(), true) : this.needsHeader(var1, false);
      } else {
         return false;
      }
   }

   private boolean needsHeader(Symbol.ClassSymbol var1, boolean var2) {
      if (!var1.isLocal() && (var1.flags() & 4096L) == 0L) {
         Scope.Entry var3;
         for(var3 = var1.members_field.elems; var3 != null; var3 = var3.sibling) {
            if (var3.sym.kind == 16 && (var3.sym.flags() & 256L) != 0L) {
               return true;
            }

            Iterator var4 = var3.sym.getDeclarationAttributes().iterator();

            while(var4.hasNext()) {
               Attribute.Compound var5 = (Attribute.Compound)var4.next();
               if (var5.type.tsym == this.syms.nativeHeaderType.tsym) {
                  return true;
               }
            }
         }

         if (var2) {
            for(var3 = var1.members_field.elems; var3 != null; var3 = var3.sibling) {
               if (var3.sym.kind == 2 && this.needsHeader((Symbol.ClassSymbol)var3.sym, true)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public FileObject write(Symbol.ClassSymbol var1) throws IOException {
      String var2 = var1.flatName().toString();
      FileObject var3 = this.fileManager.getFileForOutput(StandardLocation.NATIVE_HEADER_OUTPUT, "", var2.replaceAll("[.$]", "_") + ".h", (FileObject)null);
      Writer var4 = var3.openWriter();

      try {
         this.write(var4, var1);
         if (this.verbose) {
            this.log.printVerbose("wrote.file", var3);
         }

         var4.close();
         var4 = null;
      } finally {
         if (var4 != null) {
            var4.close();
            var3.delete();
            var3 = null;
         }

      }

      return var3;
   }

   public void write(Writer var1, Symbol.ClassSymbol var2) throws IOException {
      this.lazyInit();

      try {
         String var3 = this.mangler.mangle(var2.fullname, 1);
         this.println(var1, this.fileTop());
         this.println(var1, this.includes());
         this.println(var1, this.guardBegin(var3));
         this.println(var1, this.cppGuardBegin());
         this.writeStatics(var1, var2);
         this.writeMethods(var1, var2, var3);
         this.println(var1, this.cppGuardEnd());
         this.println(var1, this.guardEnd(var3));
      } catch (TypeSignature.SignatureException var4) {
         throw new IOException(var4);
      }
   }

   protected void writeStatics(Writer var1, Symbol.ClassSymbol var2) throws IOException {
      List var3 = this.getAllFields(var2);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         VariableElement var5 = (VariableElement)var4.next();
         if (var5.getModifiers().contains(Modifier.STATIC)) {
            String var6 = null;
            var6 = this.defineForStatic(var2, var5);
            if (var6 != null) {
               this.println(var1, var6);
            }
         }
      }

   }

   List getAllFields(TypeElement var1) {
      ArrayList var2 = new ArrayList();
      TypeElement var3 = null;
      Stack var4 = new Stack();
      var3 = var1;

      while(true) {
         var4.push(var3);
         TypeElement var5 = (TypeElement)((TypeElement)this.types.asElement(var3.getSuperclass()));
         if (var5 == null) {
            while(!var4.empty()) {
               var3 = (TypeElement)var4.pop();
               var2.addAll(ElementFilter.fieldsIn(var3.getEnclosedElements()));
            }

            return var2;
         }

         var3 = var5;
      }
   }

   protected String defineForStatic(TypeElement var1, VariableElement var2) {
      Name var3 = var1.getQualifiedName();
      Name var4 = var2.getSimpleName();
      String var5 = this.mangler.mangle(var3, 1);
      String var6 = this.mangler.mangle(var4, 2);
      Assert.check(var2.getModifiers().contains(Modifier.STATIC));
      if (var2.getModifiers().contains(Modifier.FINAL)) {
         Object var7 = null;
         var7 = var2.getConstantValue();
         if (var7 != null) {
            String var8 = null;
            if (!(var7 instanceof Integer) && !(var7 instanceof Byte) && !(var7 instanceof Short)) {
               if (var7 instanceof Boolean) {
                  var8 = (Boolean)var7 ? "1L" : "0L";
               } else if (var7 instanceof Character) {
                  Character var9 = (Character)var7;
                  var8 = (var9 & '\uffff') + "L";
               } else if (var7 instanceof Long) {
                  if (this.isWindows) {
                     var8 = var7.toString() + "i64";
                  } else {
                     var8 = var7.toString() + "LL";
                  }
               } else if (var7 instanceof Float) {
                  float var11 = (Float)var7;
                  if (Float.isInfinite(var11)) {
                     var8 = (var11 < 0.0F ? "-" : "") + "Inff";
                  } else {
                     var8 = var7.toString() + "f";
                  }
               } else if (var7 instanceof Double) {
                  double var12 = (Double)var7;
                  if (Double.isInfinite(var12)) {
                     var8 = (var12 < 0.0 ? "-" : "") + "InfD";
                  } else {
                     var8 = var7.toString();
                  }
               }
            } else {
               var8 = var7.toString() + "L";
            }

            if (var8 != null) {
               StringBuilder var13 = new StringBuilder("#undef ");
               var13.append(var5);
               var13.append("_");
               var13.append(var6);
               var13.append(this.lineSep);
               var13.append("#define ");
               var13.append(var5);
               var13.append("_");
               var13.append(var6);
               var13.append(" ");
               var13.append(var8);
               return var13.toString();
            }
         }
      }

      return null;
   }

   protected void writeMethods(Writer var1, Symbol.ClassSymbol var2, String var3) throws IOException, TypeSignature.SignatureException {
      List var4 = ElementFilter.methodsIn(var2.getEnclosedElements());
      Iterator var5 = var4.iterator();

      while(true) {
         ExecutableElement var6;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (ExecutableElement)var5.next();
         } while(!var6.getModifiers().contains(Modifier.NATIVE));

         TypeMirror var7 = this.types.erasure(var6.getReturnType());
         String var8 = this.signature(var6);
         TypeSignature var9 = new TypeSignature(this.elements);
         Name var10 = var6.getSimpleName();
         boolean var11 = false;
         Iterator var12 = var4.iterator();

         while(var12.hasNext()) {
            ExecutableElement var13 = (ExecutableElement)var12.next();
            if (var13 != var6 && var10.equals(var13.getSimpleName()) && var13.getModifiers().contains(Modifier.NATIVE)) {
               var11 = true;
            }
         }

         this.println(var1, "/*");
         this.println(var1, " * Class:     " + var3);
         this.println(var1, " * Method:    " + this.mangler.mangle(var10, 2));
         this.println(var1, " * Signature: " + var9.getTypeSignature(var8, var7));
         this.println(var1, " */");
         this.println(var1, "JNIEXPORT " + this.jniType(var7) + " JNICALL " + this.mangler.mangleMethod(var6, var2, var11 ? 8 : 7));
         this.print(var1, "  (JNIEnv *, ");
         List var16 = var6.getParameters();
         ArrayList var17 = new ArrayList();
         Iterator var14 = var16.iterator();

         while(var14.hasNext()) {
            VariableElement var15 = (VariableElement)var14.next();
            var17.add(this.types.erasure(var15.asType()));
         }

         if (var6.getModifiers().contains(Modifier.STATIC)) {
            this.print(var1, "jclass");
         } else {
            this.print(var1, "jobject");
         }

         var14 = var17.iterator();

         while(var14.hasNext()) {
            TypeMirror var18 = (TypeMirror)var14.next();
            this.print(var1, ", ");
            this.print(var1, this.jniType(var18));
         }

         this.println(var1, ");" + this.lineSep);
      }
   }

   String signature(ExecutableElement var1) {
      StringBuilder var2 = new StringBuilder("(");
      String var3 = "";

      for(Iterator var4 = var1.getParameters().iterator(); var4.hasNext(); var3 = ",") {
         VariableElement var5 = (VariableElement)var4.next();
         var2.append(var3);
         var2.append(this.types.erasure(var5.asType()).toString());
      }

      var2.append(")");
      return var2.toString();
   }

   protected final String jniType(TypeMirror var1) {
      Symbol.ClassSymbol var2 = this.elements.getTypeElement("java.lang.Throwable");
      Symbol.ClassSymbol var3 = this.elements.getTypeElement("java.lang.Class");
      Symbol.ClassSymbol var4 = this.elements.getTypeElement("java.lang.String");
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
            Assert.check(false, "jni unknown type");
            return null;
      }
   }

   protected String fileTop() {
      return "/* DO NOT EDIT THIS FILE - it is machine generated */";
   }

   protected String includes() {
      return "#include <jni.h>";
   }

   protected String cppGuardBegin() {
      return "#ifdef __cplusplus" + this.lineSep + "extern \"C\" {" + this.lineSep + "#endif";
   }

   protected String cppGuardEnd() {
      return "#ifdef __cplusplus" + this.lineSep + "}" + this.lineSep + "#endif";
   }

   protected String guardBegin(String var1) {
      return "/* Header for class " + var1 + " */" + this.lineSep + this.lineSep + "#ifndef _Included_" + var1 + this.lineSep + "#define _Included_" + var1;
   }

   protected String guardEnd(String var1) {
      return "#endif";
   }

   protected void print(Writer var1, String var2) throws IOException {
      var1.write(var2);
   }

   protected void println(Writer var1, String var2) throws IOException {
      var1.write(var2);
      var1.write(this.lineSep);
   }

   private static class TypeSignature {
      Elements elems;
      private static final String SIG_VOID = "V";
      private static final String SIG_BOOLEAN = "Z";
      private static final String SIG_BYTE = "B";
      private static final String SIG_CHAR = "C";
      private static final String SIG_SHORT = "S";
      private static final String SIG_INT = "I";
      private static final String SIG_LONG = "J";
      private static final String SIG_FLOAT = "F";
      private static final String SIG_DOUBLE = "D";
      private static final String SIG_ARRAY = "[";
      private static final String SIG_CLASS = "L";

      public TypeSignature(Elements var1) {
         this.elems = var1;
      }

      public String getTypeSignature(String var1) throws SignatureException {
         return this.getParamJVMSignature(var1);
      }

      public String getTypeSignature(String var1, TypeMirror var2) throws SignatureException {
         String var3 = null;
         String var4 = null;
         ArrayList var5 = new ArrayList();
         String var6 = null;
         String var7 = null;
         String var8 = null;
         String var9 = null;
         int var10 = 0;
         int var11 = -1;
         int var12 = -1;
         StringTokenizer var13 = null;
         byte var14 = 0;
         if (var1 != null) {
            var11 = var1.indexOf("(");
            var12 = var1.indexOf(")");
         }

         if (var11 != -1 && var12 != -1 && var11 + 1 < var1.length() && var12 < var1.length()) {
            var3 = var1.substring(var11 + 1, var12);
         }

         if (var3 != null) {
            if (var3.indexOf(",") != -1) {
               var13 = new StringTokenizer(var3, ",");
               if (var13 != null) {
                  while(var13.hasMoreTokens()) {
                     var5.add(var13.nextToken());
                  }
               }
            } else {
               var5.add(var3);
            }
         }

         var4 = "(";

         while(!var5.isEmpty()) {
            var6 = ((String)var5.remove(var14)).trim();
            var7 = this.getParamJVMSignature(var6);
            if (var7 != null) {
               var4 = var4 + var7;
            }
         }

         var4 = var4 + ")";
         var9 = "";
         if (var2 != null) {
            var10 = this.dimensions(var2);
         }

         while(var10-- > 0) {
            var9 = var9 + "[";
         }

         if (var2 != null) {
            var8 = this.qualifiedTypeName(var2);
            var9 = var9 + this.getComponentType(var8);
         } else {
            System.out.println("Invalid return type.");
         }

         var4 = var4 + var9;
         return var4;
      }

      private String getParamJVMSignature(String var1) throws SignatureException {
         String var2 = "";
         String var3 = "";
         if (var1 != null) {
            if (var1.indexOf("[]") != -1) {
               int var4 = var1.indexOf("[]");
               var3 = var1.substring(0, var4);
               String var5 = var1.substring(var4);
               if (var5 != null) {
                  while(var5.indexOf("[]") != -1) {
                     var2 = var2 + "[";
                     int var6 = var5.indexOf("]") + 1;
                     if (var6 < var5.length()) {
                        var5 = var5.substring(var6);
                     } else {
                        var5 = "";
                     }
                  }
               }
            } else {
               var3 = var1;
            }

            var2 = var2 + this.getComponentType(var3);
         }

         return var2;
      }

      private String getComponentType(String var1) throws SignatureException {
         String var2 = "";
         if (var1 != null) {
            if (var1.equals("void")) {
               var2 = var2 + "V";
            } else if (var1.equals("boolean")) {
               var2 = var2 + "Z";
            } else if (var1.equals("byte")) {
               var2 = var2 + "B";
            } else if (var1.equals("char")) {
               var2 = var2 + "C";
            } else if (var1.equals("short")) {
               var2 = var2 + "S";
            } else if (var1.equals("int")) {
               var2 = var2 + "I";
            } else if (var1.equals("long")) {
               var2 = var2 + "J";
            } else if (var1.equals("float")) {
               var2 = var2 + "F";
            } else if (var1.equals("double")) {
               var2 = var2 + "D";
            } else if (!var1.equals("")) {
               TypeElement var3 = this.elems.getTypeElement(var1);
               if (var3 == null) {
                  throw new SignatureException(var1);
               }

               String var4 = var3.getQualifiedName().toString();
               String var5 = var4.replace('.', '/');
               var2 = var2 + "L";
               var2 = var2 + var5;
               var2 = var2 + ";";
            }
         }

         return var2;
      }

      int dimensions(TypeMirror var1) {
         return var1.getKind() != TypeKind.ARRAY ? 0 : 1 + this.dimensions(((ArrayType)var1).getComponentType());
      }

      String qualifiedTypeName(TypeMirror var1) {
         SimpleTypeVisitor8 var2 = new SimpleTypeVisitor8() {
            public Name visitArray(ArrayType var1, Void var2) {
               return (Name)var1.getComponentType().accept(this, var2);
            }

            public Name visitDeclared(DeclaredType var1, Void var2) {
               return ((TypeElement)var1.asElement()).getQualifiedName();
            }

            public Name visitPrimitive(PrimitiveType var1, Void var2) {
               return TypeSignature.this.elems.getName(var1.toString());
            }

            public Name visitNoType(NoType var1, Void var2) {
               return var1.getKind() == TypeKind.VOID ? TypeSignature.this.elems.getName("void") : (Name)this.defaultAction(var1, var2);
            }

            public Name visitTypeVariable(TypeVariable var1, Void var2) {
               return (Name)var1.getUpperBound().accept(this, var2);
            }
         };
         return ((Name)var2.visit(var1)).toString();
      }

      static class SignatureException extends Exception {
         private static final long serialVersionUID = 1L;

         SignatureException(String var1) {
            super(var1);
         }
      }
   }

   private static class Mangle {
      private Elements elems;
      private Types types;

      Mangle(Elements var1, Types var2) {
         this.elems = var1;
         this.types = var2;
      }

      public final String mangle(CharSequence var1, int var2) {
         StringBuilder var3 = new StringBuilder(100);
         int var4 = var1.length();

         for(int var5 = 0; var5 < var4; ++var5) {
            char var6 = var1.charAt(var5);
            if (isalnum(var6)) {
               var3.append(var6);
            } else if (var6 == '.' && var2 == 1) {
               var3.append('_');
            } else if (var6 == '$' && var2 == 1) {
               var3.append('_');
               var3.append('_');
            } else if (var6 == '_' && var2 == 2) {
               var3.append('_');
            } else if (var6 == '_' && var2 == 1) {
               var3.append('_');
            } else if (var2 == 4) {
               String var7 = null;
               if (var6 == '_') {
                  var7 = "_1";
               } else if (var6 == '.') {
                  var7 = "_";
               } else if (var6 == ';') {
                  var7 = "_2";
               } else if (var6 == '[') {
                  var7 = "_3";
               }

               if (var7 != null) {
                  var3.append(var7);
               } else {
                  var3.append(this.mangleChar(var6));
               }
            } else if (var2 == 5) {
               if (isprint(var6)) {
                  var3.append(var6);
               } else {
                  var3.append(this.mangleChar(var6));
               }
            } else {
               var3.append(this.mangleChar(var6));
            }
         }

         return var3.toString();
      }

      public String mangleMethod(ExecutableElement var1, TypeElement var2, int var3) throws TypeSignature.SignatureException {
         StringBuilder var4 = new StringBuilder(100);
         var4.append("Java_");
         if (var3 == 6) {
            var4.append(this.mangle(var2.getQualifiedName(), 1));
            var4.append('_');
            var4.append(this.mangle(var1.getSimpleName(), 3));
            var4.append("_stub");
            return var4.toString();
         } else {
            var4.append(this.mangle(this.getInnerQualifiedName(var2), 4));
            var4.append('_');
            var4.append(this.mangle(var1.getSimpleName(), 4));
            if (var3 == 8) {
               var4.append("__");
               String var5 = this.signature(var1);
               TypeSignature var6 = new TypeSignature(this.elems);
               String var7 = var6.getTypeSignature(var5, var1.getReturnType());
               var7 = var7.substring(1);
               var7 = var7.substring(0, var7.lastIndexOf(41));
               var7 = var7.replace('/', '.');
               var4.append(this.mangle(var7, 4));
            }

            return var4.toString();
         }
      }

      private String getInnerQualifiedName(TypeElement var1) {
         return this.elems.getBinaryName(var1).toString();
      }

      public final String mangleChar(char var1) {
         String var2 = Integer.toHexString(var1);
         int var3 = 5 - var2.length();
         char[] var4 = new char[6];
         var4[0] = '_';

         int var5;
         for(var5 = 1; var5 <= var3; ++var5) {
            var4[var5] = '0';
         }

         var5 = var3 + 1;

         for(int var6 = 0; var5 < 6; ++var6) {
            var4[var5] = var2.charAt(var6);
            ++var5;
         }

         return new String(var4);
      }

      private String signature(ExecutableElement var1) {
         StringBuilder var2 = new StringBuilder();
         String var3 = "(";

         for(Iterator var4 = var1.getParameters().iterator(); var4.hasNext(); var3 = ",") {
            VariableElement var5 = (VariableElement)var4.next();
            var2.append(var3);
            var2.append(this.types.erasure(var5.asType()).toString());
         }

         var2.append(")");
         return var2.toString();
      }

      private static boolean isalnum(char var0) {
         return var0 <= 127 && (var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9');
      }

      private static boolean isprint(char var0) {
         return var0 >= ' ' && var0 <= '~';
      }

      public static class Type {
         public static final int CLASS = 1;
         public static final int FIELDSTUB = 2;
         public static final int FIELD = 3;
         public static final int JNI = 4;
         public static final int SIGNATURE = 5;
         public static final int METHOD_JDK_1 = 6;
         public static final int METHOD_JNI_SHORT = 7;
         public static final int METHOD_JNI_LONG = 8;
      }
   }
}
