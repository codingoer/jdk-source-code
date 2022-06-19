package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

public class ClassDocImpl extends ProgramElementDocImpl implements ClassDoc {
   public final Type.ClassType type;
   protected final Symbol.ClassSymbol tsym;
   boolean isIncluded;
   private SerializedForm serializedForm;
   private String name;
   private String qualifiedName;
   private String simpleTypeName;

   public ClassDocImpl(DocEnv var1, Symbol.ClassSymbol var2) {
      this(var1, var2, (TreePath)null);
   }

   public ClassDocImpl(DocEnv var1, Symbol.ClassSymbol var2, TreePath var3) {
      super(var1, var2, var3);
      this.isIncluded = false;
      this.type = (Type.ClassType)var2.type;
      this.tsym = var2;
   }

   public com.sun.javadoc.Type getElementType() {
      return null;
   }

   protected long getFlags() {
      return getFlags(this.tsym);
   }

   static long getFlags(Symbol.ClassSymbol var0) {
      try {
         return var0.flags();
      } catch (Symbol.CompletionFailure var2) {
         return getFlags(var0);
      }
   }

   static boolean isAnnotationType(Symbol.ClassSymbol var0) {
      return (getFlags(var0) & 8192L) != 0L;
   }

   protected Symbol.ClassSymbol getContainingClass() {
      return this.tsym.owner.enclClass();
   }

   public boolean isClass() {
      return !Modifier.isInterface(this.getModifiers());
   }

   public boolean isOrdinaryClass() {
      if (!this.isEnum() && !this.isInterface() && !this.isAnnotationType()) {
         for(Object var1 = this.type; ((Type)var1).hasTag(TypeTag.CLASS); var1 = this.env.types.supertype((Type)var1)) {
            if (((Type)var1).tsym == this.env.syms.errorType.tsym || ((Type)var1).tsym == this.env.syms.exceptionType.tsym) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isEnum() {
      return (this.getFlags() & 16384L) != 0L && !this.env.legacyDoclet;
   }

   public boolean isInterface() {
      return Modifier.isInterface(this.getModifiers());
   }

   public boolean isException() {
      if (!this.isEnum() && !this.isInterface() && !this.isAnnotationType()) {
         for(Object var1 = this.type; ((Type)var1).hasTag(TypeTag.CLASS); var1 = this.env.types.supertype((Type)var1)) {
            if (((Type)var1).tsym == this.env.syms.exceptionType.tsym) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isError() {
      if (!this.isEnum() && !this.isInterface() && !this.isAnnotationType()) {
         for(Object var1 = this.type; ((Type)var1).hasTag(TypeTag.CLASS); var1 = this.env.types.supertype((Type)var1)) {
            if (((Type)var1).tsym == this.env.syms.errorType.tsym) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isThrowable() {
      if (!this.isEnum() && !this.isInterface() && !this.isAnnotationType()) {
         for(Object var1 = this.type; ((Type)var1).hasTag(TypeTag.CLASS); var1 = this.env.types.supertype((Type)var1)) {
            if (((Type)var1).tsym == this.env.syms.throwableType.tsym) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this.getModifiers());
   }

   public boolean isSynthetic() {
      return (this.getFlags() & 4096L) != 0L;
   }

   public boolean isIncluded() {
      if (this.isIncluded) {
         return true;
      } else {
         if (this.env.shouldDocument(this.tsym)) {
            if (this.containingPackage().isIncluded()) {
               return this.isIncluded = true;
            }

            ClassDoc var1 = this.containingClass();
            if (var1 != null && var1.isIncluded()) {
               return this.isIncluded = true;
            }
         }

         return false;
      }
   }

   public PackageDoc containingPackage() {
      PackageDocImpl var1 = this.env.getPackageDoc(this.tsym.packge());
      if (!var1.setDocPath) {
         FileObject var2;
         try {
            StandardLocation var3 = this.env.fileManager.hasLocation(StandardLocation.SOURCE_PATH) ? StandardLocation.SOURCE_PATH : StandardLocation.CLASS_PATH;
            var2 = this.env.fileManager.getFileForInput(var3, var1.qualifiedName(), "package.html");
         } catch (IOException var9) {
            var2 = null;
         }

         if (var2 == null) {
            SourcePosition var10 = this.position();
            if (this.env.fileManager instanceof StandardJavaFileManager && var10 instanceof SourcePositionImpl) {
               URI var4 = ((SourcePositionImpl)var10).filename.toUri();
               if ("file".equals(var4.getScheme())) {
                  File var5 = new File(var4);
                  File var6 = var5.getParentFile();
                  if (var6 != null) {
                     File var7 = new File(var6, "package.html");
                     if (var7.exists()) {
                        StandardJavaFileManager var8 = (StandardJavaFileManager)this.env.fileManager;
                        var2 = (FileObject)var8.getJavaFileObjects(new File[]{var7}).iterator().next();
                     }
                  }
               }
            }
         }

         var1.setDocPath(var2);
      }

      return var1;
   }

   public String name() {
      if (this.name == null) {
         this.name = getClassName(this.tsym, false);
      }

      return this.name;
   }

   public String qualifiedName() {
      if (this.qualifiedName == null) {
         this.qualifiedName = getClassName(this.tsym, true);
      }

      return this.qualifiedName;
   }

   public String typeName() {
      return this.name();
   }

   public String qualifiedTypeName() {
      return this.qualifiedName();
   }

   public String simpleTypeName() {
      if (this.simpleTypeName == null) {
         this.simpleTypeName = this.tsym.name.toString();
      }

      return this.simpleTypeName;
   }

   public String toString() {
      return classToString(this.env, this.tsym, true);
   }

   static String getClassName(Symbol.ClassSymbol var0, boolean var1) {
      if (var1) {
         return var0.getQualifiedName().toString();
      } else {
         String var2;
         for(var2 = ""; var0 != null; var0 = var0.owner.enclClass()) {
            var2 = var0.name + (var2.equals("") ? "" : ".") + var2;
         }

         return var2;
      }
   }

   static String classToString(DocEnv var0, Symbol.ClassSymbol var1, boolean var2) {
      StringBuilder var3 = new StringBuilder();
      if (!var1.isInner()) {
         var3.append(getClassName(var1, var2));
      } else {
         Symbol.ClassSymbol var4 = var1.owner.enclClass();
         var3.append(classToString(var0, var4, var2)).append('.').append(var1.name);
      }

      var3.append(TypeMaker.typeParametersString(var0, var1, var2));
      return var3.toString();
   }

   static boolean isGeneric(Symbol.ClassSymbol var0) {
      return var0.type.allparams().nonEmpty();
   }

   public TypeVariable[] typeParameters() {
      if (this.env.legacyDoclet) {
         return new TypeVariable[0];
      } else {
         TypeVariable[] var1 = new TypeVariable[this.type.getTypeArguments().length()];
         TypeMaker.getTypes(this.env, this.type.getTypeArguments(), var1);
         return var1;
      }
   }

   public ParamTag[] typeParamTags() {
      return this.env.legacyDoclet ? new ParamTag[0] : this.comment().typeParamTags();
   }

   public String modifiers() {
      return Modifier.toString(this.modifierSpecifier());
   }

   public int modifierSpecifier() {
      int var1 = this.getModifiers();
      return !this.isInterface() && !this.isAnnotationType() ? var1 : var1 & -1025;
   }

   public ClassDoc superclass() {
      if (!this.isInterface() && !this.isAnnotationType()) {
         if (this.tsym == this.env.syms.objectType.tsym) {
            return null;
         } else {
            Symbol.ClassSymbol var1 = (Symbol.ClassSymbol)this.env.types.supertype(this.type).tsym;
            if (var1 == null || var1 == this.tsym) {
               var1 = (Symbol.ClassSymbol)this.env.syms.objectType.tsym;
            }

            return this.env.getClassDoc(var1);
         }
      } else {
         return null;
      }
   }

   public com.sun.javadoc.Type superclassType() {
      if (!this.isInterface() && !this.isAnnotationType() && this.tsym != this.env.syms.objectType.tsym) {
         Type var1 = this.env.types.supertype(this.type);
         return TypeMaker.getType(this.env, var1.hasTag(TypeTag.NONE) ? this.env.syms.objectType : var1);
      } else {
         return null;
      }
   }

   public boolean subclassOf(ClassDoc var1) {
      return this.tsym.isSubClass(((ClassDocImpl)var1).tsym, this.env.types);
   }

   public ClassDoc[] interfaces() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.env.types.interfaces(this.type).iterator();

      while(var2.hasNext()) {
         Type var3 = (Type)var2.next();
         var1.append(this.env.getClassDoc((Symbol.ClassSymbol)var3.tsym));
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public com.sun.javadoc.Type[] interfaceTypes() {
      return TypeMaker.getTypes(this.env, this.env.types.interfaces(this.type));
   }

   public FieldDoc[] fields(boolean var1) {
      return this.fields(var1, false);
   }

   public FieldDoc[] fields() {
      return this.fields(true, false);
   }

   public FieldDoc[] enumConstants() {
      return this.fields(false, true);
   }

   private FieldDoc[] fields(boolean var1, boolean var2) {
      List var3 = List.nil();

      for(Scope.Entry var4 = this.tsym.members().elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym != null && var4.sym.kind == 4) {
            Symbol.VarSymbol var5 = (Symbol.VarSymbol)var4.sym;
            boolean var6 = (var5.flags() & 16384L) != 0L && !this.env.legacyDoclet;
            if (var6 == var2 && (!var1 || this.env.shouldDocument(var5))) {
               var3 = var3.prepend(this.env.getFieldDoc(var5));
            }
         }
      }

      return (FieldDoc[])var3.toArray(new FieldDocImpl[var3.length()]);
   }

   public MethodDoc[] methods(boolean var1) {
      Names var2 = this.tsym.name.table.names;
      List var3 = List.nil();

      for(Scope.Entry var4 = this.tsym.members().elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym != null && var4.sym.kind == 16 && var4.sym.name != var2.init && var4.sym.name != var2.clinit) {
            Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var4.sym;
            if (!var1 || this.env.shouldDocument(var5)) {
               var3 = var3.prepend(this.env.getMethodDoc(var5));
            }
         }
      }

      return (MethodDoc[])var3.toArray(new MethodDocImpl[var3.length()]);
   }

   public MethodDoc[] methods() {
      return this.methods(true);
   }

   public ConstructorDoc[] constructors(boolean var1) {
      Names var2 = this.tsym.name.table.names;
      List var3 = List.nil();

      for(Scope.Entry var4 = this.tsym.members().elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym != null && var4.sym.kind == 16 && var4.sym.name == var2.init) {
            Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var4.sym;
            if (!var1 || this.env.shouldDocument(var5)) {
               var3 = var3.prepend(this.env.getConstructorDoc(var5));
            }
         }
      }

      return (ConstructorDoc[])var3.toArray(new ConstructorDocImpl[var3.length()]);
   }

   public ConstructorDoc[] constructors() {
      return this.constructors(true);
   }

   void addAllClasses(ListBuffer var1, boolean var2) {
      try {
         if (this.isSynthetic()) {
            return;
         }

         if (!JavadocTool.isValidClassName(this.tsym.name.toString())) {
            return;
         }

         if (var2 && !this.env.shouldDocument(this.tsym)) {
            return;
         }

         if (var1.contains(this)) {
            return;
         }

         var1.append(this);
         List var3 = List.nil();

         for(Scope.Entry var4 = this.tsym.members().elems; var4 != null; var4 = var4.sibling) {
            if (var4.sym != null && var4.sym.kind == 2) {
               Symbol.ClassSymbol var5 = (Symbol.ClassSymbol)var4.sym;
               ClassDocImpl var6 = this.env.getClassDoc(var5);
               if (!var6.isSynthetic() && var6 != null) {
                  var3 = var3.prepend(var6);
               }
            }
         }

         while(var3.nonEmpty()) {
            ((ClassDocImpl)var3.head).addAllClasses(var1, var2);
            var3 = var3.tail;
         }
      } catch (Symbol.CompletionFailure var7) {
      }

   }

   public ClassDoc[] innerClasses(boolean var1) {
      ListBuffer var2 = new ListBuffer();

      for(Scope.Entry var3 = this.tsym.members().elems; var3 != null; var3 = var3.sibling) {
         if (var3.sym != null && var3.sym.kind == 2) {
            Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var3.sym;
            if ((var4.flags_field & 4096L) == 0L && (!var1 || this.env.isVisible(var4))) {
               var2.prepend(this.env.getClassDoc(var4));
            }
         }
      }

      return (ClassDoc[])var2.toArray(new ClassDocImpl[var2.length()]);
   }

   public ClassDoc[] innerClasses() {
      return this.innerClasses(true);
   }

   public ClassDoc findClass(String var1) {
      ClassDoc var2 = this.searchClass(var1);
      if (var2 == null) {
         ClassDocImpl var3;
         for(var3 = (ClassDocImpl)this.containingClass(); var3 != null && var3.containingClass() != null; var3 = (ClassDocImpl)var3.containingClass()) {
         }

         var2 = var3 == null ? null : var3.searchClass(var1);
      }

      return var2;
   }

   private ClassDoc searchClass(String var1) {
      Names var2 = this.tsym.name.table.names;
      ClassDocImpl var3 = this.env.lookupClass(var1);
      if (var3 != null) {
         return var3;
      } else {
         ClassDoc[] var4 = this.innerClasses();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ClassDoc var7 = var4[var6];
            if (var7.name().equals(var1) || var7.name().endsWith("." + var1)) {
               return var7;
            }

            ClassDoc var8 = ((ClassDocImpl)var7).searchClass(var1);
            if (var8 != null) {
               return var8;
            }
         }

         ClassDoc var9 = this.containingPackage().findClass(var1);
         if (var9 != null) {
            return var9;
         } else {
            if (this.tsym.completer != null) {
               this.tsym.complete();
            }

            if (this.tsym.sourcefile != null) {
               Env var10 = this.env.enter.getEnv(this.tsym);
               if (var10 == null) {
                  return null;
               }

               Scope.ImportScope var11 = var10.toplevel.namedImportScope;

               Scope.Entry var13;
               ClassDocImpl var14;
               for(var13 = var11.lookup(var2.fromString(var1)); var13.scope != null; var13 = var13.next()) {
                  if (var13.sym.kind == 2) {
                     var14 = this.env.getClassDoc((Symbol.ClassSymbol)var13.sym);
                     return var14;
                  }
               }

               Scope.StarImportScope var12 = var10.toplevel.starImportScope;

               for(var13 = var12.lookup(var2.fromString(var1)); var13.scope != null; var13 = var13.next()) {
                  if (var13.sym.kind == 2) {
                     var14 = this.env.getClassDoc((Symbol.ClassSymbol)var13.sym);
                     return var14;
                  }
               }
            }

            return null;
         }
      }
   }

   private boolean hasParameterTypes(Symbol.MethodSymbol var1, String[] var2) {
      if (var2 == null) {
         return true;
      } else {
         int var3 = 0;
         List var4 = var1.type.getParameterTypes();
         if (var2.length != var4.length()) {
            return false;
         } else {
            Iterator var5 = var4.iterator();

            Type var6;
            String var7;
            do {
               if (!var5.hasNext()) {
                  return true;
               }

               var6 = (Type)var5.next();
               var7 = var2[var3++];
               if (var3 == var2.length) {
                  var7 = var7.replace("...", "[]");
               }
            } while(this.hasTypeName(this.env.types.erasure(var6), var7));

            return false;
         }
      }
   }

   private boolean hasTypeName(Type var1, String var2) {
      return var2.equals(TypeMaker.getTypeName(var1, true)) || var2.equals(TypeMaker.getTypeName(var1, false)) || (this.qualifiedName() + "." + var2).equals(TypeMaker.getTypeName(var1, true));
   }

   public MethodDocImpl findMethod(String var1, String[] var2) {
      return this.searchMethod(var1, var2, new HashSet());
   }

   private MethodDocImpl searchMethod(String var1, String[] var2, Set var3) {
      Names var4 = this.tsym.name.table.names;
      if (var4.init.contentEquals(var1)) {
         return null;
      } else if (var3.contains(this)) {
         return null;
      } else {
         var3.add(this);
         Scope.Entry var7 = this.tsym.members().lookup(var4.fromString(var1));
         if (var2 == null) {
            Symbol.MethodSymbol var8;
            for(var8 = null; var7.scope != null; var7 = var7.next()) {
               if (var7.sym.kind == 16 && var7.sym.name.toString().equals(var1)) {
                  var8 = (Symbol.MethodSymbol)var7.sym;
               }
            }

            if (var8 != null) {
               return this.env.getMethodDoc(var8);
            }
         } else {
            while(var7.scope != null) {
               if (var7.sym != null && var7.sym.kind == 16 && this.hasParameterTypes((Symbol.MethodSymbol)var7.sym, var2)) {
                  return this.env.getMethodDoc((Symbol.MethodSymbol)var7.sym);
               }

               var7 = var7.next();
            }
         }

         ClassDocImpl var5 = (ClassDocImpl)this.superclass();
         MethodDocImpl var6;
         if (var5 != null) {
            var6 = var5.searchMethod(var1, var2, var3);
            if (var6 != null) {
               return var6;
            }
         }

         ClassDoc[] var10 = this.interfaces();

         for(int var9 = 0; var9 < var10.length; ++var9) {
            var5 = (ClassDocImpl)var10[var9];
            var6 = var5.searchMethod(var1, var2, var3);
            if (var6 != null) {
               return var6;
            }
         }

         var5 = (ClassDocImpl)this.containingClass();
         if (var5 != null) {
            var6 = var5.searchMethod(var1, var2, var3);
            if (var6 != null) {
               return var6;
            }
         }

         return null;
      }
   }

   public ConstructorDoc findConstructor(String var1, String[] var2) {
      Names var3 = this.tsym.name.table.names;

      for(Scope.Entry var4 = this.tsym.members().lookup(var3.fromString("<init>")); var4.scope != null; var4 = var4.next()) {
         if (var4.sym.kind == 16 && this.hasParameterTypes((Symbol.MethodSymbol)var4.sym, var2)) {
            return this.env.getConstructorDoc((Symbol.MethodSymbol)var4.sym);
         }
      }

      return null;
   }

   public FieldDoc findField(String var1) {
      return this.searchField(var1, new HashSet());
   }

   private FieldDocImpl searchField(String var1, Set var2) {
      Names var3 = this.tsym.name.table.names;
      if (var2.contains(this)) {
         return null;
      } else {
         var2.add(this);

         for(Scope.Entry var4 = this.tsym.members().lookup(var3.fromString(var1)); var4.scope != null; var4 = var4.next()) {
            if (var4.sym.kind == 4) {
               return this.env.getFieldDoc((Symbol.VarSymbol)var4.sym);
            }
         }

         ClassDocImpl var8 = (ClassDocImpl)this.containingClass();
         FieldDocImpl var5;
         if (var8 != null) {
            var5 = var8.searchField(var1, var2);
            if (var5 != null) {
               return var5;
            }
         }

         var8 = (ClassDocImpl)this.superclass();
         if (var8 != null) {
            var5 = var8.searchField(var1, var2);
            if (var5 != null) {
               return var5;
            }
         }

         ClassDoc[] var9 = this.interfaces();

         for(int var6 = 0; var6 < var9.length; ++var6) {
            var8 = (ClassDocImpl)var9[var6];
            FieldDocImpl var7 = var8.searchField(var1, var2);
            if (var7 != null) {
               return var7;
            }
         }

         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public ClassDoc[] importedClasses() {
      if (this.tsym.sourcefile == null) {
         return new ClassDoc[0];
      } else {
         ListBuffer var1 = new ListBuffer();
         Env var2 = this.env.enter.getEnv(this.tsym);
         if (var2 == null) {
            return new ClassDocImpl[0];
         } else {
            Name var3 = this.tsym.name.table.names.asterisk;
            Iterator var4 = var2.toplevel.defs.iterator();

            while(var4.hasNext()) {
               JCTree var5 = (JCTree)var4.next();
               if (var5.hasTag(JCTree.Tag.IMPORT)) {
                  JCTree var6 = ((JCTree.JCImport)var5).qualid;
                  if (TreeInfo.name(var6) != var3 && (var6.type.tsym.kind & 2) != 0) {
                     var1.append(this.env.getClassDoc((Symbol.ClassSymbol)var6.type.tsym));
                  }
               }
            }

            return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public PackageDoc[] importedPackages() {
      if (this.tsym.sourcefile == null) {
         return new PackageDoc[0];
      } else {
         ListBuffer var1 = new ListBuffer();
         Names var2 = this.tsym.name.table.names;
         var1.append(this.env.getPackageDoc(this.env.reader.enterPackage(var2.java_lang)));
         Env var3 = this.env.enter.getEnv(this.tsym);
         if (var3 == null) {
            return new PackageDocImpl[0];
         } else {
            Iterator var4 = var3.toplevel.defs.iterator();

            while(var4.hasNext()) {
               JCTree var5 = (JCTree)var4.next();
               if (var5.hasTag(JCTree.Tag.IMPORT)) {
                  JCTree var6 = ((JCTree.JCImport)var5).qualid;
                  if (TreeInfo.name(var6) == var2.asterisk) {
                     JCTree.JCFieldAccess var7 = (JCTree.JCFieldAccess)var6;
                     Symbol.TypeSymbol var8 = var7.selected.type.tsym;
                     PackageDocImpl var9 = this.env.getPackageDoc(var8.packge());
                     if (!var1.contains(var9)) {
                        var1.append(var9);
                     }
                  }
               }
            }

            return (PackageDoc[])var1.toArray(new PackageDocImpl[var1.length()]);
         }
      }
   }

   public String dimension() {
      return "";
   }

   public ClassDoc asClassDoc() {
      return this;
   }

   public AnnotationTypeDoc asAnnotationTypeDoc() {
      return null;
   }

   public ParameterizedType asParameterizedType() {
      return null;
   }

   public TypeVariable asTypeVariable() {
      return null;
   }

   public WildcardType asWildcardType() {
      return null;
   }

   public AnnotatedType asAnnotatedType() {
      return null;
   }

   public boolean isPrimitive() {
      return false;
   }

   public boolean isSerializable() {
      try {
         return this.env.types.isSubtype(this.type, this.env.syms.serializableType);
      } catch (Symbol.CompletionFailure var2) {
         return false;
      }
   }

   public boolean isExternalizable() {
      try {
         return this.env.types.isSubtype(this.type, this.env.externalizableSym.type);
      } catch (Symbol.CompletionFailure var2) {
         return false;
      }
   }

   public MethodDoc[] serializationMethods() {
      if (this.serializedForm == null) {
         this.serializedForm = new SerializedForm(this.env, this.tsym, this);
      }

      return this.serializedForm.methods();
   }

   public FieldDoc[] serializableFields() {
      if (this.serializedForm == null) {
         this.serializedForm = new SerializedForm(this.env, this.tsym, this);
      }

      return this.serializedForm.fields();
   }

   public boolean definesSerializableFields() {
      if (this.isSerializable() && !this.isExternalizable()) {
         if (this.serializedForm == null) {
            this.serializedForm = new SerializedForm(this.env, this.tsym, this);
         }

         return this.serializedForm.definesSerializableFields();
      } else {
         return false;
      }
   }

   boolean isRuntimeException() {
      return this.tsym.isSubClass(this.env.syms.runtimeExceptionType.tsym, this.env.types);
   }

   public SourcePosition position() {
      return this.tsym.sourcefile == null ? null : SourcePositionImpl.make(this.tsym.sourcefile, this.tree == null ? -1 : this.tree.pos, this.lineMap);
   }
}
