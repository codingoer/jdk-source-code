package com.sun.tools.classfile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Dependencies {
   private Dependency.Filter filter;
   private Dependency.Finder finder;

   public static Dependency.Finder getDefaultFinder() {
      return new APIDependencyFinder(2);
   }

   public static Dependency.Finder getAPIFinder(int var0) {
      return new APIDependencyFinder(var0);
   }

   public static Dependency.Finder getClassDependencyFinder() {
      return new ClassDependencyFinder();
   }

   public Dependency.Finder getFinder() {
      if (this.finder == null) {
         this.finder = getDefaultFinder();
      }

      return this.finder;
   }

   public void setFinder(Dependency.Finder var1) {
      var1.getClass();
      this.finder = var1;
   }

   public static Dependency.Filter getDefaultFilter() {
      return Dependencies.DefaultFilter.instance();
   }

   public static Dependency.Filter getRegexFilter(Pattern var0) {
      return new TargetRegexFilter(var0);
   }

   public static Dependency.Filter getPackageFilter(Set var0, boolean var1) {
      return new TargetPackageFilter(var0, var1);
   }

   public Dependency.Filter getFilter() {
      if (this.filter == null) {
         this.filter = getDefaultFilter();
      }

      return this.filter;
   }

   public void setFilter(Dependency.Filter var1) {
      var1.getClass();
      this.filter = var1;
   }

   public Set findAllDependencies(ClassFileReader var1, Set var2, boolean var3) throws ClassFileNotFoundException {
      final HashSet var4 = new HashSet();
      Recorder var5 = new Recorder() {
         public void addDependency(Dependency var1) {
            var4.add(var1);
         }
      };
      this.findAllDependencies(var1, var2, var3, var5);
      return var4;
   }

   public void findAllDependencies(ClassFileReader var1, Set var2, boolean var3, Recorder var4) throws ClassFileNotFoundException {
      HashSet var5 = new HashSet();
      this.getFinder();
      this.getFilter();
      LinkedList var6 = new LinkedList(var2);

      String var7;
      while((var7 = (String)var6.poll()) != null) {
         assert !var5.contains(var7);

         var5.add(var7);
         ClassFile var8 = var1.getClassFile(var7);
         Iterator var9 = this.finder.findDependencies(var8).iterator();

         while(var9.hasNext()) {
            Dependency var10 = (Dependency)var9.next();
            var4.addDependency(var10);
            if (var3 && this.filter.accepts(var10)) {
               String var11 = var10.getTarget().getClassName();
               if (!var5.contains(var11)) {
                  var6.add(var11);
               }
            }
         }
      }

   }

   abstract static class BasicDependencyFinder implements Dependency.Finder {
      private Map locations = new HashMap();

      Dependency.Location getLocation(String var1) {
         Object var2 = (Dependency.Location)this.locations.get(var1);
         if (var2 == null) {
            this.locations.put(var1, var2 = new SimpleLocation(var1));
         }

         return (Dependency.Location)var2;
      }

      class Visitor implements ConstantPool.Visitor, Type.Visitor {
         private ConstantPool constant_pool;
         private Dependency.Location origin;
         Set deps;

         Visitor(ClassFile var2) {
            try {
               this.constant_pool = var2.constant_pool;
               this.origin = BasicDependencyFinder.this.getLocation(var2.getName());
               this.deps = new HashSet();
            } catch (ConstantPoolException var4) {
               throw new ClassFileError(var4);
            }
         }

         void scan(Descriptor var1, Attributes var2) {
            try {
               this.scan((new Signature(var1.index)).getType(this.constant_pool));
               this.scan(var2);
            } catch (ConstantPoolException var4) {
               throw new ClassFileError(var4);
            }
         }

         void scan(ConstantPool.CPInfo var1) {
            var1.accept(this, (Object)null);
         }

         void scan(Type var1) {
            var1.accept(this, (Object)null);
         }

         void scan(Attributes var1) {
            try {
               Signature_attribute var2 = (Signature_attribute)var1.get("Signature");
               if (var2 != null) {
                  this.scan(var2.getParsedSignature().getType(this.constant_pool));
               }

               this.scan((RuntimeAnnotations_attribute)((RuntimeVisibleAnnotations_attribute)var1.get("RuntimeVisibleAnnotations")));
               this.scan((RuntimeParameterAnnotations_attribute)((RuntimeVisibleParameterAnnotations_attribute)var1.get("RuntimeVisibleParameterAnnotations")));
            } catch (ConstantPoolException var3) {
               throw new ClassFileError(var3);
            }
         }

         private void scan(RuntimeAnnotations_attribute var1) throws ConstantPoolException {
            if (var1 != null) {
               for(int var2 = 0; var2 < var1.annotations.length; ++var2) {
                  int var3 = var1.annotations[var2].type_index;
                  this.scan((new Signature(var3)).getType(this.constant_pool));
               }

            }
         }

         private void scan(RuntimeParameterAnnotations_attribute var1) throws ConstantPoolException {
            if (var1 != null) {
               for(int var2 = 0; var2 < var1.parameter_annotations.length; ++var2) {
                  for(int var3 = 0; var3 < var1.parameter_annotations[var2].length; ++var3) {
                     int var4 = var1.parameter_annotations[var2][var3].type_index;
                     this.scan((new Signature(var4)).getType(this.constant_pool));
                  }
               }

            }
         }

         void addClass(int var1) throws ConstantPoolException {
            if (var1 != 0) {
               String var2 = this.constant_pool.getClassInfo(var1).getBaseName();
               if (var2 != null) {
                  this.addDependency(var2);
               }
            }

         }

         void addClasses(int[] var1) throws ConstantPoolException {
            int[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               int var5 = var2[var4];
               this.addClass(var5);
            }

         }

         private void addDependency(String var1) {
            this.deps.add(new SimpleDependency(this.origin, BasicDependencyFinder.this.getLocation(var1)));
         }

         public Void visitClass(ConstantPool.CONSTANT_Class_info var1, Void var2) {
            try {
               if (var1.getName().startsWith("[")) {
                  (new Signature(var1.name_index)).getType(this.constant_pool).accept(this, (Object)null);
               } else {
                  this.addDependency(var1.getBaseName());
               }

               return null;
            } catch (ConstantPoolException var4) {
               throw new ClassFileError(var4);
            }
         }

         public Void visitDouble(ConstantPool.CONSTANT_Double_info var1, Void var2) {
            return null;
         }

         public Void visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, Void var2) {
            return this.visitRef(var1, var2);
         }

         public Void visitFloat(ConstantPool.CONSTANT_Float_info var1, Void var2) {
            return null;
         }

         public Void visitInteger(ConstantPool.CONSTANT_Integer_info var1, Void var2) {
            return null;
         }

         public Void visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, Void var2) {
            return this.visitRef(var1, var2);
         }

         public Void visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, Void var2) {
            return null;
         }

         public Void visitLong(ConstantPool.CONSTANT_Long_info var1, Void var2) {
            return null;
         }

         public Void visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, Void var2) {
            return null;
         }

         public Void visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, Void var2) {
            return null;
         }

         public Void visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, Void var2) {
            return this.visitRef(var1, var2);
         }

         public Void visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, Void var2) {
            try {
               (new Signature(var1.type_index)).getType(this.constant_pool).accept(this, (Object)null);
               return null;
            } catch (ConstantPoolException var4) {
               throw new ClassFileError(var4);
            }
         }

         public Void visitString(ConstantPool.CONSTANT_String_info var1, Void var2) {
            return null;
         }

         public Void visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, Void var2) {
            return null;
         }

         private Void visitRef(ConstantPool.CPRefInfo var1, Void var2) {
            try {
               this.visitClass(var1.getClassInfo(), var2);
               return null;
            } catch (ConstantPoolException var4) {
               throw new ClassFileError(var4);
            }
         }

         private void findDependencies(Type var1) {
            if (var1 != null) {
               var1.accept(this, (Object)null);
            }

         }

         private void findDependencies(List var1) {
            if (var1 != null) {
               Iterator var2 = var1.iterator();

               while(var2.hasNext()) {
                  Type var3 = (Type)var2.next();
                  var3.accept(this, (Object)null);
               }
            }

         }

         public Void visitSimpleType(Type.SimpleType var1, Void var2) {
            return null;
         }

         public Void visitArrayType(Type.ArrayType var1, Void var2) {
            this.findDependencies(var1.elemType);
            return null;
         }

         public Void visitMethodType(Type.MethodType var1, Void var2) {
            this.findDependencies(var1.paramTypes);
            this.findDependencies(var1.returnType);
            this.findDependencies(var1.throwsTypes);
            this.findDependencies(var1.typeParamTypes);
            return null;
         }

         public Void visitClassSigType(Type.ClassSigType var1, Void var2) {
            this.findDependencies(var1.superclassType);
            this.findDependencies(var1.superinterfaceTypes);
            return null;
         }

         public Void visitClassType(Type.ClassType var1, Void var2) {
            this.findDependencies((Type)var1.outerType);
            this.addDependency(var1.getBinaryName());
            this.findDependencies(var1.typeArgs);
            return null;
         }

         public Void visitTypeParamType(Type.TypeParamType var1, Void var2) {
            this.findDependencies(var1.classBound);
            this.findDependencies(var1.interfaceBounds);
            return null;
         }

         public Void visitWildcardType(Type.WildcardType var1, Void var2) {
            this.findDependencies(var1.boundType);
            return null;
         }
      }
   }

   static class APIDependencyFinder extends BasicDependencyFinder {
      private int showAccess;

      APIDependencyFinder(int var1) {
         switch (var1) {
            case 0:
            case 1:
            case 2:
            case 4:
               this.showAccess = var1;
               return;
            case 3:
            default:
               throw new IllegalArgumentException("invalid access 0x" + Integer.toHexString(var1));
         }
      }

      public Iterable findDependencies(ClassFile var1) {
         try {
            BasicDependencyFinder.Visitor var2 = new BasicDependencyFinder.Visitor(var1);
            var2.addClass(var1.super_class);
            var2.addClasses(var1.interfaces);
            Field[] var3 = var1.fields;
            int var4 = var3.length;

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               Field var6 = var3[var5];
               if (this.checkAccess(var6.access_flags)) {
                  var2.scan(var6.descriptor, var6.attributes);
               }
            }

            Method[] var9 = var1.methods;
            var4 = var9.length;

            for(var5 = 0; var5 < var4; ++var5) {
               Method var10 = var9[var5];
               if (this.checkAccess(var10.access_flags)) {
                  var2.scan(var10.descriptor, var10.attributes);
                  Exceptions_attribute var7 = (Exceptions_attribute)var10.attributes.get("Exceptions");
                  if (var7 != null) {
                     var2.addClasses(var7.exception_index_table);
                  }
               }
            }

            return var2.deps;
         } catch (ConstantPoolException var8) {
            throw new ClassFileError(var8);
         }
      }

      boolean checkAccess(AccessFlags var1) {
         boolean var2 = var1.is(1);
         boolean var3 = var1.is(4);
         boolean var4 = var1.is(2);
         boolean var5 = !var2 && !var3 && !var4;
         if (this.showAccess == 1 && (var3 || var4 || var5)) {
            return false;
         } else if (this.showAccess == 4 && (var4 || var5)) {
            return false;
         } else {
            return this.showAccess != 0 || !var4;
         }
      }
   }

   static class ClassDependencyFinder extends BasicDependencyFinder {
      public Iterable findDependencies(ClassFile var1) {
         BasicDependencyFinder.Visitor var2 = new BasicDependencyFinder.Visitor(var1);
         Iterator var3 = var1.constant_pool.entries().iterator();

         while(var3.hasNext()) {
            ConstantPool.CPInfo var4 = (ConstantPool.CPInfo)var3.next();
            var2.scan(var4);
         }

         try {
            var2.addClass(var1.super_class);
            var2.addClasses(var1.interfaces);
            var2.scan(var1.attributes);
            Field[] var9 = var1.fields;
            int var11 = var9.length;

            int var5;
            for(var5 = 0; var5 < var11; ++var5) {
               Field var6 = var9[var5];
               var2.scan(var6.descriptor, var6.attributes);
            }

            Method[] var10 = var1.methods;
            var11 = var10.length;

            for(var5 = 0; var5 < var11; ++var5) {
               Method var12 = var10[var5];
               var2.scan(var12.descriptor, var12.attributes);
               Exceptions_attribute var7 = (Exceptions_attribute)var12.attributes.get("Exceptions");
               if (var7 != null) {
                  var2.addClasses(var7.exception_index_table);
               }
            }

            return var2.deps;
         } catch (ConstantPoolException var8) {
            throw new ClassFileError(var8);
         }
      }
   }

   static class TargetPackageFilter implements Dependency.Filter {
      private final Set packageNames;
      private final boolean matchSubpackages;

      TargetPackageFilter(Set var1, boolean var2) {
         Iterator var3 = var1.iterator();

         String var4;
         do {
            if (!var3.hasNext()) {
               this.packageNames = var1;
               this.matchSubpackages = var2;
               return;
            }

            var4 = (String)var3.next();
         } while(var4.length() != 0);

         throw new IllegalArgumentException();
      }

      public boolean accepts(Dependency var1) {
         String var2 = var1.getTarget().getPackageName();
         if (this.packageNames.contains(var2)) {
            return true;
         } else {
            if (this.matchSubpackages) {
               Iterator var3 = this.packageNames.iterator();

               while(var3.hasNext()) {
                  String var4 = (String)var3.next();
                  if (var2.startsWith(var4 + ".")) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   static class TargetRegexFilter implements Dependency.Filter {
      private final Pattern pattern;

      TargetRegexFilter(Pattern var1) {
         this.pattern = var1;
      }

      public boolean accepts(Dependency var1) {
         return this.pattern.matcher(var1.getTarget().getClassName()).matches();
      }
   }

   static class DefaultFilter implements Dependency.Filter {
      private static DefaultFilter instance;

      static DefaultFilter instance() {
         if (instance == null) {
            instance = new DefaultFilter();
         }

         return instance;
      }

      public boolean accepts(Dependency var1) {
         return true;
      }
   }

   static class SimpleDependency implements Dependency {
      private Dependency.Location origin;
      private Dependency.Location target;

      public SimpleDependency(Dependency.Location var1, Dependency.Location var2) {
         this.origin = var1;
         this.target = var2;
      }

      public Dependency.Location getOrigin() {
         return this.origin;
      }

      public Dependency.Location getTarget() {
         return this.target;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof SimpleDependency)) {
            return false;
         } else {
            SimpleDependency var2 = (SimpleDependency)var1;
            return this.origin.equals(var2.origin) && this.target.equals(var2.target);
         }
      }

      public int hashCode() {
         return this.origin.hashCode() * 31 + this.target.hashCode();
      }

      public String toString() {
         return this.origin + ":" + this.target;
      }
   }

   static class SimpleLocation implements Dependency.Location {
      private String name;
      private String className;

      public SimpleLocation(String var1) {
         this.name = var1;
         this.className = var1.replace('/', '.');
      }

      public String getName() {
         return this.name;
      }

      public String getClassName() {
         return this.className;
      }

      public String getPackageName() {
         int var1 = this.name.lastIndexOf(47);
         return var1 > 0 ? this.name.substring(0, var1).replace('/', '.') : "";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof SimpleLocation) ? false : this.name.equals(((SimpleLocation)var1).name);
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public String toString() {
         return this.name;
      }
   }

   public interface Recorder {
      void addDependency(Dependency var1);
   }

   public interface ClassFileReader {
      ClassFile getClassFile(String var1) throws ClassFileNotFoundException;
   }

   public static class ClassFileError extends Error {
      private static final long serialVersionUID = 4111110813961313203L;

      public ClassFileError(Throwable var1) {
         this.initCause(var1);
      }
   }

   public static class ClassFileNotFoundException extends Exception {
      private static final long serialVersionUID = 3632265927794475048L;
      public final String className;

      public ClassFileNotFoundException(String var1) {
         super(var1);
         this.className = var1;
      }

      public ClassFileNotFoundException(String var1, Throwable var2) {
         this(var1);
         this.initCause(var2);
      }
   }
}
