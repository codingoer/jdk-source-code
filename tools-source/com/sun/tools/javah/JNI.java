package com.sun.tools.javah;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

public class JNI extends Gen {
   JNI(Util var1) {
      super(var1);
   }

   public String getIncludes() {
      return "#include <jni.h>";
   }

   public void write(OutputStream var1, TypeElement var2) throws Util.Exit {
      try {
         String var3 = this.mangler.mangle(var2.getQualifiedName(), 1);
         PrintWriter var4 = this.wrapWriter(var1);
         var4.println(this.guardBegin(var3));
         var4.println(this.cppGuardBegin());
         List var5 = this.getAllFields(var2);
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            VariableElement var7 = (VariableElement)var6.next();
            if (var7.getModifiers().contains(Modifier.STATIC)) {
               String var8 = null;
               var8 = this.defineForStatic(var2, var7);
               if (var8 != null) {
                  var4.println(var8);
               }
            }
         }

         List var19 = ElementFilter.methodsIn(var2.getEnclosedElements());
         Iterator var20 = var19.iterator();

         while(true) {
            ExecutableElement var21;
            do {
               if (!var20.hasNext()) {
                  var4.println(this.cppGuardEnd());
                  var4.println(this.guardEnd(var3));
                  return;
               }

               var21 = (ExecutableElement)var20.next();
            } while(!var21.getModifiers().contains(Modifier.NATIVE));

            TypeMirror var9 = this.types.erasure(var21.getReturnType());
            String var10 = this.signature(var21);
            TypeSignature var11 = new TypeSignature(this.elems);
            Name var12 = var21.getSimpleName();
            boolean var13 = false;
            Iterator var14 = var19.iterator();

            while(var14.hasNext()) {
               ExecutableElement var15 = (ExecutableElement)var14.next();
               if (var15 != var21 && var12.equals(var15.getSimpleName()) && var15.getModifiers().contains(Modifier.NATIVE)) {
                  var13 = true;
               }
            }

            var4.println("/*");
            var4.println(" * Class:     " + var3);
            var4.println(" * Method:    " + this.mangler.mangle(var12, 2));
            var4.println(" * Signature: " + var11.getTypeSignature(var10, var9));
            var4.println(" */");
            var4.println("JNIEXPORT " + this.jniType(var9) + " JNICALL " + this.mangler.mangleMethod(var21, var2, var13 ? 8 : 7));
            var4.print("  (JNIEnv *, ");
            List var22 = var21.getParameters();
            ArrayList var23 = new ArrayList();
            Iterator var16 = var22.iterator();

            while(var16.hasNext()) {
               VariableElement var17 = (VariableElement)var16.next();
               var23.add(this.types.erasure(var17.asType()));
            }

            if (var21.getModifiers().contains(Modifier.STATIC)) {
               var4.print("jclass");
            } else {
               var4.print("jobject");
            }

            var16 = var23.iterator();

            while(var16.hasNext()) {
               TypeMirror var24 = (TypeMirror)var16.next();
               var4.print(", ");
               var4.print(this.jniType(var24));
            }

            var4.println(");" + this.lineSep);
         }
      } catch (TypeSignature.SignatureException var18) {
         this.util.error("jni.sigerror", var18.getMessage());
      }
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
}
