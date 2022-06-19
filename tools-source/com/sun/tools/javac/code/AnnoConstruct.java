package com.sun.tools.javac.code;

import com.sun.tools.javac.model.AnnotationProxyMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import javax.lang.model.AnnotatedConstruct;

public abstract class AnnoConstruct implements AnnotatedConstruct {
   private static final Class REPEATABLE_CLASS = initRepeatable();
   private static final Method VALUE_ELEMENT_METHOD = initValueElementMethod();

   public abstract List getAnnotationMirrors();

   protected Attribute.Compound getAttribute(Class var1) {
      String var2 = var1.getName();
      Iterator var3 = this.getAnnotationMirrors().iterator();

      Attribute.Compound var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (Attribute.Compound)var3.next();
      } while(!var2.equals(var4.type.tsym.flatName().toString()));

      return var4;
   }

   protected Annotation[] getInheritedAnnotations(Class var1) {
      return (Annotation[])((Annotation[])Array.newInstance(var1, 0));
   }

   public Annotation[] getAnnotationsByType(Class var1) {
      if (!var1.isAnnotation()) {
         throw new IllegalArgumentException("Not an annotation type: " + var1);
      } else {
         Class var2 = getContainer(var1);
         if (var2 == null) {
            Annotation var16 = this.getAnnotation(var1);
            int var17 = var16 == null ? 0 : 1;
            Annotation[] var18 = (Annotation[])((Annotation[])Array.newInstance(var1, var17));
            if (var16 != null) {
               var18[0] = var16;
            }

            return var18;
         } else {
            String var3 = var1.getName();
            String var4 = var2.getName();
            int var5 = -1;
            int var6 = -1;
            Attribute.Compound var7 = null;
            Attribute.Compound var8 = null;
            int var9 = -1;
            Iterator var10 = this.getAnnotationMirrors().iterator();

            while(var10.hasNext()) {
               Attribute.Compound var11 = (Attribute.Compound)var10.next();
               ++var9;
               if (var11.type.tsym.flatName().contentEquals(var3)) {
                  var5 = var9;
                  var7 = var11;
               } else if (var4 != null && var11.type.tsym.flatName().contentEquals(var4)) {
                  var6 = var9;
                  var8 = var11;
               }
            }

            if (var7 == null && var8 == null && var1.isAnnotationPresent(Inherited.class)) {
               return this.getInheritedAnnotations(var1);
            } else {
               Attribute.Compound[] var19 = this.unpackContained(var8);
               if (var7 == null && var19.length == 0 && var1.isAnnotationPresent(Inherited.class)) {
                  return this.getInheritedAnnotations(var1);
               } else {
                  int var20 = (var7 == null ? 0 : 1) + var19.length;
                  Annotation[] var12 = (Annotation[])((Annotation[])Array.newInstance(var1, var20));
                  boolean var13 = true;
                  int var14 = var12.length;
                  byte var21;
                  if (var5 >= 0 && var6 >= 0) {
                     if (var5 < var6) {
                        var12[0] = AnnotationProxyMaker.generateAnnotation(var7, var1);
                        var21 = 1;
                     } else {
                        var12[var12.length - 1] = AnnotationProxyMaker.generateAnnotation(var7, var1);
                        var21 = 0;
                        --var14;
                     }
                  } else {
                     if (var5 >= 0) {
                        var12[0] = AnnotationProxyMaker.generateAnnotation(var7, var1);
                        return var12;
                     }

                     var21 = 0;
                  }

                  for(int var15 = 0; var15 + var21 < var14; ++var15) {
                     var12[var21 + var15] = AnnotationProxyMaker.generateAnnotation(var19[var15], var1);
                  }

                  return var12;
               }
            }
         }
      }
   }

   private Attribute.Compound[] unpackContained(Attribute.Compound var1) {
      Attribute[] var2 = null;
      if (var1 != null) {
         var2 = unpackAttributes(var1);
      }

      ListBuffer var3 = new ListBuffer();
      if (var2 != null) {
         Attribute[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Attribute var7 = var4[var6];
            if (var7 instanceof Attribute.Compound) {
               var3 = var3.append((Attribute.Compound)var7);
            }
         }
      }

      return (Attribute.Compound[])var3.toArray(new Attribute.Compound[var3.size()]);
   }

   public Annotation getAnnotation(Class var1) {
      if (!var1.isAnnotation()) {
         throw new IllegalArgumentException("Not an annotation type: " + var1);
      } else {
         Attribute.Compound var2 = this.getAttribute(var1);
         return var2 == null ? null : AnnotationProxyMaker.generateAnnotation(var2, var1);
      }
   }

   private static Class initRepeatable() {
      try {
         return Class.forName("java.lang.annotation.Repeatable").asSubclass(Annotation.class);
      } catch (SecurityException | ClassNotFoundException var1) {
         return null;
      }
   }

   private static Method initValueElementMethod() {
      if (REPEATABLE_CLASS == null) {
         return null;
      } else {
         Method var0 = null;

         try {
            var0 = REPEATABLE_CLASS.getMethod("value");
            if (var0 != null) {
               var0.setAccessible(true);
            }

            return var0;
         } catch (NoSuchMethodException var2) {
            return null;
         }
      }
   }

   private static Class getContainer(Class var0) {
      if (REPEATABLE_CLASS != null && VALUE_ELEMENT_METHOD != null) {
         Annotation var1 = var0.getAnnotation(REPEATABLE_CLASS);
         if (var1 != null) {
            try {
               Class var2 = (Class)VALUE_ELEMENT_METHOD.invoke(var1);
               if (var2 == null) {
                  return null;
               }

               return var2;
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException var3) {
               return null;
            }
         }
      }

      return null;
   }

   private static Attribute[] unpackAttributes(Attribute.Compound var0) {
      return ((Attribute.Array)var0.member(var0.type.tsym.name.table.names.value)).values;
   }
}
