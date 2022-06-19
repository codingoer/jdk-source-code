package com.sun.tools.javac.model;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Pair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.EnumConstantNotPresentExceptionProxy;
import sun.reflect.annotation.ExceptionProxy;

public class AnnotationProxyMaker {
   private final Attribute.Compound anno;
   private final Class annoType;

   private AnnotationProxyMaker(Attribute.Compound var1, Class var2) {
      this.anno = var1;
      this.annoType = var2;
   }

   public static Annotation generateAnnotation(Attribute.Compound var0, Class var1) {
      AnnotationProxyMaker var2 = new AnnotationProxyMaker(var0, var1);
      return (Annotation)var1.cast(var2.generateAnnotation());
   }

   private Annotation generateAnnotation() {
      return AnnotationParser.annotationForMap(this.annoType, this.getAllReflectedValues());
   }

   private Map getAllReflectedValues() {
      LinkedHashMap var1 = new LinkedHashMap();
      Iterator var2 = this.getAllValues().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)var3.getKey();
         Object var5 = this.generateValue(var4, (Attribute)var3.getValue());
         if (var5 != null) {
            var1.put(var4.name.toString(), var5);
         }
      }

      return var1;
   }

   private Map getAllValues() {
      LinkedHashMap var1 = new LinkedHashMap();
      Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)this.anno.type.tsym;

      for(Scope.Entry var3 = var2.members().elems; var3 != null; var3 = var3.sibling) {
         if (var3.sym.kind == 16) {
            Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)var3.sym;
            Attribute var5 = var4.getDefaultValue();
            if (var5 != null) {
               var1.put(var4, var5);
            }
         }
      }

      Iterator var6 = this.anno.values.iterator();

      while(var6.hasNext()) {
         Pair var7 = (Pair)var6.next();
         var1.put(var7.fst, var7.snd);
      }

      return var1;
   }

   private Object generateValue(Symbol.MethodSymbol var1, Attribute var2) {
      ValueVisitor var3 = new ValueVisitor(var1);
      return var3.getValue(var2);
   }

   private static final class MirroredTypesExceptionProxy extends ExceptionProxy {
      static final long serialVersionUID = 269L;
      private transient List types;
      private final String typeStrings;

      MirroredTypesExceptionProxy(List var1) {
         this.types = var1;
         this.typeStrings = var1.toString();
      }

      public String toString() {
         return this.typeStrings;
      }

      public int hashCode() {
         return (this.types != null ? this.types : this.typeStrings).hashCode();
      }

      public boolean equals(Object var1) {
         return this.types != null && var1 instanceof MirroredTypesExceptionProxy && this.types.equals(((MirroredTypesExceptionProxy)var1).types);
      }

      protected RuntimeException generateException() {
         return new MirroredTypesException(this.types);
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.types = null;
      }
   }

   private static final class MirroredTypeExceptionProxy extends ExceptionProxy {
      static final long serialVersionUID = 269L;
      private transient TypeMirror type;
      private final String typeString;

      MirroredTypeExceptionProxy(TypeMirror var1) {
         this.type = var1;
         this.typeString = var1.toString();
      }

      public String toString() {
         return this.typeString;
      }

      public int hashCode() {
         return (this.type != null ? this.type : this.typeString).hashCode();
      }

      public boolean equals(Object var1) {
         return this.type != null && var1 instanceof MirroredTypeExceptionProxy && this.type.equals(((MirroredTypeExceptionProxy)var1).type);
      }

      protected RuntimeException generateException() {
         return new MirroredTypeException(this.type);
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.type = null;
      }
   }

   private class ValueVisitor implements Attribute.Visitor {
      private Symbol.MethodSymbol meth;
      private Class returnClass;
      private Object value;

      ValueVisitor(Symbol.MethodSymbol var2) {
         this.meth = var2;
      }

      Object getValue(Attribute var1) {
         Method var2;
         try {
            var2 = AnnotationProxyMaker.this.annoType.getMethod(this.meth.name.toString());
         } catch (NoSuchMethodException var4) {
            return null;
         }

         this.returnClass = var2.getReturnType();
         var1.accept(this);
         if (!(this.value instanceof ExceptionProxy) && !AnnotationType.invocationHandlerReturnType(this.returnClass).isInstance(this.value)) {
            this.typeMismatch(var2, var1);
         }

         return this.value;
      }

      public void visitConstant(Attribute.Constant var1) {
         this.value = var1.getValue();
      }

      public void visitClass(Attribute.Class var1) {
         this.value = new MirroredTypeExceptionProxy(var1.classType);
      }

      public void visitArray(Attribute.Array var1) {
         Name var2 = ((Type.ArrayType)var1.type).elemtype.tsym.getQualifiedName();
         int var6;
         if (var2.equals(var2.table.names.java_lang_Class)) {
            ListBuffer var14 = new ListBuffer();
            Attribute[] var15 = var1.values;
            int var16 = var15.length;

            for(var6 = 0; var6 < var16; ++var6) {
               Attribute var7 = var15[var6];
               Type var8 = ((Attribute.Class)var7).classType;
               var14.append(var8);
            }

            this.value = new MirroredTypesExceptionProxy(var14.toList());
         } else {
            int var3 = var1.values.length;
            Class var4 = this.returnClass;
            this.returnClass = this.returnClass.getComponentType();

            try {
               Object var5 = Array.newInstance(this.returnClass, var3);

               for(var6 = 0; var6 < var3; ++var6) {
                  var1.values[var6].accept(this);
                  if (this.value == null || this.value instanceof ExceptionProxy) {
                     return;
                  }

                  try {
                     Array.set(var5, var6, this.value);
                  } catch (IllegalArgumentException var12) {
                     this.value = null;
                     return;
                  }
               }

               this.value = var5;
            } finally {
               this.returnClass = var4;
            }
         }
      }

      public void visitEnum(Attribute.Enum var1) {
         if (this.returnClass.isEnum()) {
            String var2 = var1.value.toString();

            try {
               this.value = Enum.valueOf(this.returnClass, var2);
            } catch (IllegalArgumentException var4) {
               this.value = new EnumConstantNotPresentExceptionProxy(this.returnClass, var2);
            }
         } else {
            this.value = null;
         }

      }

      public void visitCompound(Attribute.Compound var1) {
         try {
            Class var2 = this.returnClass.asSubclass(Annotation.class);
            this.value = AnnotationProxyMaker.generateAnnotation(var1, var2);
         } catch (ClassCastException var3) {
            this.value = null;
         }

      }

      public void visitError(Attribute.Error var1) {
         if (var1 instanceof Attribute.UnresolvedClass) {
            this.value = new MirroredTypeExceptionProxy(((Attribute.UnresolvedClass)var1).classType);
         } else {
            this.value = null;
         }

      }

      private void typeMismatch(Method var1, final Attribute var2) {
         class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy {
            static final long serialVersionUID = 269L;
            final transient Method method;

            AnnotationTypeMismatchExceptionProxy(Method var2x) {
               this.method = var2x;
            }

            public String toString() {
               return "<error>";
            }

            protected RuntimeException generateException() {
               return new AnnotationTypeMismatchException(this.method, var2.type.toString());
            }
         }

         this.value = new AnnotationTypeMismatchExceptionProxy(var1);
      }
   }
}
