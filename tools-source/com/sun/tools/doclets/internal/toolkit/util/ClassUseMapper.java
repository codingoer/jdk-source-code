package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ClassUseMapper {
   private final ClassTree classtree;
   public Map classToPackage = new HashMap();
   public Map classToPackageAnnotations = new HashMap();
   public Map classToClass = new HashMap();
   public Map classToSubclass = new HashMap();
   public Map classToSubinterface = new HashMap();
   public Map classToImplementingClass = new HashMap();
   public Map classToField = new HashMap();
   public Map classToMethodReturn = new HashMap();
   public Map classToMethodArgs = new HashMap();
   public Map classToMethodThrows = new HashMap();
   public Map classToConstructorArgs = new HashMap();
   public Map classToConstructorThrows = new HashMap();
   public Map classToConstructorAnnotations = new HashMap();
   public Map classToConstructorParamAnnotation = new HashMap();
   public Map classToConstructorDocArgTypeParam = new HashMap();
   public Map classToClassTypeParam = new HashMap();
   public Map classToClassAnnotations = new HashMap();
   public Map classToExecMemberDocTypeParam = new HashMap();
   public Map classToExecMemberDocArgTypeParam = new HashMap();
   public Map classToExecMemberDocAnnotations = new HashMap();
   public Map classToExecMemberDocReturnTypeParam = new HashMap();
   public Map classToExecMemberDocParamAnnotation = new HashMap();
   public Map classToFieldDocTypeParam = new HashMap();
   public Map annotationToFieldDoc = new HashMap();

   public ClassUseMapper(RootDoc var1, ClassTree var2) {
      this.classtree = var2;
      Iterator var3 = var2.baseclasses().iterator();

      while(var3.hasNext()) {
         this.subclasses((ClassDoc)var3.next());
      }

      var3 = var2.baseinterfaces().iterator();

      while(var3.hasNext()) {
         this.implementingClasses((ClassDoc)var3.next());
      }

      ClassDoc[] var12 = var1.classes();

      for(int var4 = 0; var4 < var12.length; ++var4) {
         PackageDoc var5 = var12[var4].containingPackage();
         this.mapAnnotations(this.classToPackageAnnotations, var5, var5);
         ClassDoc var6 = var12[var4];
         this.mapTypeParameters(this.classToClassTypeParam, var6, var6);
         this.mapAnnotations(this.classToClassAnnotations, (Object)var6, (ProgramElementDoc)var6);
         FieldDoc[] var7 = var6.fields();

         for(int var8 = 0; var8 < var7.length; ++var8) {
            FieldDoc var9 = var7[var8];
            this.mapTypeParameters(this.classToFieldDocTypeParam, var9, var9);
            this.mapAnnotations(this.annotationToFieldDoc, (Object)var9, (ProgramElementDoc)var9);
            if (!var9.type().isPrimitive()) {
               this.add(this.classToField, var9.type().asClassDoc(), var9);
            }
         }

         ConstructorDoc[] var13 = var6.constructors();

         for(int var14 = 0; var14 < var13.length; ++var14) {
            this.mapAnnotations(this.classToConstructorAnnotations, (Object)var13[var14], (ProgramElementDoc)var13[var14]);
            this.mapExecutable(var13[var14]);
         }

         MethodDoc[] var15 = var6.methods();

         for(int var10 = 0; var10 < var15.length; ++var10) {
            MethodDoc var11 = var15[var10];
            this.mapExecutable(var11);
            this.mapTypeParameters(this.classToExecMemberDocTypeParam, var11, var11);
            this.mapAnnotations(this.classToExecMemberDocAnnotations, (Object)var11, (ProgramElementDoc)var11);
            if (!var11.returnType().isPrimitive() && !(var11.returnType() instanceof TypeVariable)) {
               this.mapTypeParameters(this.classToExecMemberDocReturnTypeParam, var11.returnType(), var11);
               this.add(this.classToMethodReturn, var11.returnType().asClassDoc(), var11);
            }
         }
      }

   }

   private Collection subclasses(ClassDoc var1) {
      Object var2 = (Collection)this.classToSubclass.get(var1.qualifiedName());
      if (var2 == null) {
         var2 = new TreeSet();
         List var3 = this.classtree.subclasses(var1);
         if (var3 != null) {
            ((Collection)var2).addAll(var3);
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               ((Collection)var2).addAll(this.subclasses((ClassDoc)var4.next()));
            }
         }

         this.addAll(this.classToSubclass, var1, (Collection)var2);
      }

      return (Collection)var2;
   }

   private Collection subinterfaces(ClassDoc var1) {
      Object var2 = (Collection)this.classToSubinterface.get(var1.qualifiedName());
      if (var2 == null) {
         var2 = new TreeSet();
         List var3 = this.classtree.subinterfaces(var1);
         if (var3 != null) {
            ((Collection)var2).addAll(var3);
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               ((Collection)var2).addAll(this.subinterfaces((ClassDoc)var4.next()));
            }
         }

         this.addAll(this.classToSubinterface, var1, (Collection)var2);
      }

      return (Collection)var2;
   }

   private Collection implementingClasses(ClassDoc var1) {
      Object var2 = (Collection)this.classToImplementingClass.get(var1.qualifiedName());
      if (var2 == null) {
         var2 = new TreeSet();
         List var3 = this.classtree.implementingclasses(var1);
         Iterator var4;
         if (var3 != null) {
            ((Collection)var2).addAll(var3);
            var4 = var3.iterator();

            while(var4.hasNext()) {
               ((Collection)var2).addAll(this.subclasses((ClassDoc)var4.next()));
            }
         }

         var4 = this.subinterfaces(var1).iterator();

         while(var4.hasNext()) {
            ((Collection)var2).addAll(this.implementingClasses((ClassDoc)var4.next()));
         }

         this.addAll(this.classToImplementingClass, var1, (Collection)var2);
      }

      return (Collection)var2;
   }

   private void mapExecutable(ExecutableMemberDoc var1) {
      Parameter[] var2 = var1.parameters();
      boolean var3 = var1.isConstructor();
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var2.length; ++var5) {
         Type var6 = var2[var5].type();
         if (!var2[var5].type().isPrimitive() && !var4.contains(var6) && !(var6 instanceof TypeVariable)) {
            this.add(var3 ? this.classToConstructorArgs : this.classToMethodArgs, var6.asClassDoc(), var1);
            var4.add(var6);
            this.mapTypeParameters(var3 ? this.classToConstructorDocArgTypeParam : this.classToExecMemberDocArgTypeParam, var6, var1);
         }

         this.mapAnnotations(var3 ? this.classToConstructorParamAnnotation : this.classToExecMemberDocParamAnnotation, (Object)var2[var5], (ProgramElementDoc)var1);
      }

      ClassDoc[] var7 = var1.thrownExceptions();

      for(int var8 = 0; var8 < var7.length; ++var8) {
         this.add(var3 ? this.classToConstructorThrows : this.classToMethodThrows, var7[var8], var1);
      }

   }

   private List refList(Map var1, ClassDoc var2) {
      Object var3 = (List)var1.get(var2.qualifiedName());
      if (var3 == null) {
         ArrayList var4 = new ArrayList();
         var3 = var4;
         var1.put(var2.qualifiedName(), var4);
      }

      return (List)var3;
   }

   private Set packageSet(ClassDoc var1) {
      Object var2 = (Set)this.classToPackage.get(var1.qualifiedName());
      if (var2 == null) {
         var2 = new TreeSet();
         this.classToPackage.put(var1.qualifiedName(), var2);
      }

      return (Set)var2;
   }

   private Set classSet(ClassDoc var1) {
      Object var2 = (Set)this.classToClass.get(var1.qualifiedName());
      if (var2 == null) {
         TreeSet var3 = new TreeSet();
         var2 = var3;
         this.classToClass.put(var1.qualifiedName(), var3);
      }

      return (Set)var2;
   }

   private void add(Map var1, ClassDoc var2, ProgramElementDoc var3) {
      this.refList(var1, var2).add(var3);
      this.packageSet(var2).add(var3.containingPackage());
      this.classSet(var2).add(var3 instanceof MemberDoc ? ((MemberDoc)var3).containingClass() : (ClassDoc)var3);
   }

   private void addAll(Map var1, ClassDoc var2, Collection var3) {
      if (var3 != null) {
         this.refList(var1, var2).addAll(var3);
         Set var4 = this.packageSet(var2);
         Set var5 = this.classSet(var2);
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            ClassDoc var7 = (ClassDoc)var6.next();
            var4.add(var7.containingPackage());
            var5.add(var7);
         }

      }
   }

   private void mapTypeParameters(Map var1, Object var2, ProgramElementDoc var3) {
      TypeVariable[] var4;
      Type[] var6;
      int var7;
      if (var2 instanceof ClassDoc) {
         var4 = ((ClassDoc)var2).typeParameters();
      } else {
         Type[] var9;
         int var10;
         if (var2 instanceof WildcardType) {
            var9 = ((WildcardType)var2).extendsBounds();

            for(var10 = 0; var10 < var9.length; ++var10) {
               this.addTypeParameterToMap(var1, var9[var10], var3);
            }

            var6 = ((WildcardType)var2).superBounds();

            for(var7 = 0; var7 < var6.length; ++var7) {
               this.addTypeParameterToMap(var1, var6[var7], var3);
            }

            return;
         }

         if (var2 instanceof ParameterizedType) {
            var9 = ((ParameterizedType)var2).typeArguments();

            for(var10 = 0; var10 < var9.length; ++var10) {
               this.addTypeParameterToMap(var1, var9[var10], var3);
            }

            return;
         }

         if (!(var2 instanceof ExecutableMemberDoc)) {
            if (var2 instanceof FieldDoc) {
               Type var8 = ((FieldDoc)var2).type();
               this.mapTypeParameters(var1, var8, var3);
               return;
            }

            return;
         }

         var4 = ((ExecutableMemberDoc)var2).typeParameters();
      }

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var6 = var4[var5].bounds();

         for(var7 = 0; var7 < var6.length; ++var7) {
            this.addTypeParameterToMap(var1, var6[var7], var3);
         }
      }

   }

   private void mapAnnotations(Map var1, Object var2, ProgramElementDoc var3) {
      boolean var5 = false;
      AnnotationDesc[] var4;
      if (var2 instanceof ProgramElementDoc) {
         var4 = ((ProgramElementDoc)var2).annotations();
      } else if (var2 instanceof PackageDoc) {
         var4 = ((PackageDoc)var2).annotations();
         var5 = true;
      } else {
         if (!(var2 instanceof Parameter)) {
            throw new DocletAbortException("should not happen");
         }

         var4 = ((Parameter)var2).annotations();
      }

      for(int var6 = 0; var6 < var4.length; ++var6) {
         AnnotationTypeDoc var7 = var4[var6].annotationType();
         if (var5) {
            this.refList(var1, var7).add(var3);
         } else {
            this.add(var1, var7, var3);
         }
      }

   }

   private void mapAnnotations(Map var1, PackageDoc var2, PackageDoc var3) {
      AnnotationDesc[] var4 = var2.annotations();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         AnnotationTypeDoc var6 = var4[var5].annotationType();
         this.refList(var1, var6).add(var3);
      }

   }

   private void addTypeParameterToMap(Map var1, Type var2, ProgramElementDoc var3) {
      if (var2 instanceof ClassDoc) {
         this.add(var1, (ClassDoc)var2, var3);
      } else if (var2 instanceof ParameterizedType) {
         this.add(var1, ((ParameterizedType)var2).asClassDoc(), var3);
      }

      this.mapTypeParameters(var1, var2, var3);
   }
}
