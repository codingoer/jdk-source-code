package com.sun.codemodel.internal;

import com.sun.codemodel.internal.writer.FileCodeWriter;
import com.sun.codemodel.internal.writer.ProgressCodeWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JCodeModel {
   private HashMap packages = new HashMap();
   private final HashMap refClasses = new HashMap();
   public final JNullType NULL = new JNullType(this);
   public final JPrimitiveType VOID = new JPrimitiveType(this, "void", Void.class);
   public final JPrimitiveType BOOLEAN = new JPrimitiveType(this, "boolean", Boolean.class);
   public final JPrimitiveType BYTE = new JPrimitiveType(this, "byte", Byte.class);
   public final JPrimitiveType SHORT = new JPrimitiveType(this, "short", Short.class);
   public final JPrimitiveType CHAR = new JPrimitiveType(this, "char", Character.class);
   public final JPrimitiveType INT = new JPrimitiveType(this, "int", Integer.class);
   public final JPrimitiveType FLOAT = new JPrimitiveType(this, "float", Float.class);
   public final JPrimitiveType LONG = new JPrimitiveType(this, "long", Long.class);
   public final JPrimitiveType DOUBLE = new JPrimitiveType(this, "double", Double.class);
   protected static final boolean isCaseSensitiveFileSystem = getFileSystemCaseSensitivity();
   private JClass wildcard;
   public static final Map primitiveToBox;
   public static final Map boxToPrimitive;

   private static boolean getFileSystemCaseSensitivity() {
      try {
         if (System.getProperty("com.sun.codemodel.internal.FileSystemCaseSensitive") != null) {
            return true;
         }
      } catch (Exception var1) {
      }

      return File.separatorChar == '/';
   }

   public JPackage _package(String name) {
      JPackage p = (JPackage)this.packages.get(name);
      if (p == null) {
         p = new JPackage(name, this);
         this.packages.put(name, p);
      }

      return p;
   }

   public final JPackage rootPackage() {
      return this._package("");
   }

   public Iterator packages() {
      return this.packages.values().iterator();
   }

   public JDefinedClass _class(String fullyqualifiedName) throws JClassAlreadyExistsException {
      return this._class(fullyqualifiedName, ClassType.CLASS);
   }

   public JClass directClass(String name) {
      return new JDirectClass(this, name);
   }

   public JDefinedClass _class(int mods, String fullyqualifiedName, ClassType t) throws JClassAlreadyExistsException {
      int idx = fullyqualifiedName.lastIndexOf(46);
      return idx < 0 ? this.rootPackage()._class(fullyqualifiedName) : this._package(fullyqualifiedName.substring(0, idx))._class(mods, fullyqualifiedName.substring(idx + 1), t);
   }

   public JDefinedClass _class(String fullyqualifiedName, ClassType t) throws JClassAlreadyExistsException {
      return this._class(1, fullyqualifiedName, t);
   }

   public JDefinedClass _getClass(String fullyQualifiedName) {
      int idx = fullyQualifiedName.lastIndexOf(46);
      return idx < 0 ? this.rootPackage()._getClass(fullyQualifiedName) : this._package(fullyQualifiedName.substring(0, idx))._getClass(fullyQualifiedName.substring(idx + 1));
   }

   /** @deprecated */
   public JDefinedClass newAnonymousClass(JClass baseType) {
      return new JAnonymousClass(baseType);
   }

   public JDefinedClass anonymousClass(JClass baseType) {
      return new JAnonymousClass(baseType);
   }

   public JDefinedClass anonymousClass(Class baseType) {
      return this.anonymousClass(this.ref(baseType));
   }

   public void build(File destDir, PrintStream status) throws IOException {
      this.build(destDir, destDir, status);
   }

   public void build(File srcDir, File resourceDir, PrintStream status) throws IOException {
      CodeWriter src = new FileCodeWriter(srcDir);
      CodeWriter res = new FileCodeWriter(resourceDir);
      if (status != null) {
         src = new ProgressCodeWriter((CodeWriter)src, status);
         res = new ProgressCodeWriter((CodeWriter)res, status);
      }

      this.build((CodeWriter)src, (CodeWriter)res);
   }

   public void build(File destDir) throws IOException {
      this.build(destDir, System.out);
   }

   public void build(File srcDir, File resourceDir) throws IOException {
      this.build(srcDir, resourceDir, System.out);
   }

   public void build(CodeWriter out) throws IOException {
      this.build(out, out);
   }

   public void build(CodeWriter source, CodeWriter resource) throws IOException {
      JPackage[] pkgs = (JPackage[])this.packages.values().toArray(new JPackage[this.packages.size()]);
      JPackage[] var4 = pkgs;
      int var5 = pkgs.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         JPackage pkg = var4[var6];
         pkg.build(source, resource);
      }

      source.close();
      resource.close();
   }

   public int countArtifacts() {
      int r = 0;
      JPackage[] pkgs = (JPackage[])this.packages.values().toArray(new JPackage[this.packages.size()]);
      JPackage[] var3 = pkgs;
      int var4 = pkgs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         JPackage pkg = var3[var5];
         r += pkg.countArtifacts();
      }

      return r;
   }

   public JClass ref(Class clazz) {
      JReferencedClass jrc = (JReferencedClass)this.refClasses.get(clazz);
      if (jrc == null) {
         if (clazz.isPrimitive()) {
            throw new IllegalArgumentException(clazz + " is a primitive");
         }

         if (clazz.isArray()) {
            return new JArrayClass(this, this._ref(clazz.getComponentType()));
         }

         jrc = new JReferencedClass(clazz);
         this.refClasses.put(clazz, jrc);
      }

      return jrc;
   }

   public JType _ref(Class c) {
      return (JType)(c.isPrimitive() ? JType.parse(this, c.getName()) : this.ref(c));
   }

   public JClass ref(String fullyQualifiedClassName) {
      try {
         return this.ref(SecureLoader.getContextClassLoader().loadClass(fullyQualifiedClassName));
      } catch (ClassNotFoundException var4) {
         try {
            return this.ref(Class.forName(fullyQualifiedClassName));
         } catch (ClassNotFoundException var3) {
            return new JDirectClass(this, fullyQualifiedClassName);
         }
      }
   }

   public JClass wildcard() {
      if (this.wildcard == null) {
         this.wildcard = this.ref(Object.class).wildcard();
      }

      return this.wildcard;
   }

   public JType parseType(String name) throws ClassNotFoundException {
      if (name.endsWith("[]")) {
         return this.parseType(name.substring(0, name.length() - 2)).array();
      } else {
         try {
            return JType.parse(this, name);
         } catch (IllegalArgumentException var3) {
            return (new TypeNameParser(name)).parseTypeName();
         }
      }
   }

   static {
      Map m1 = new HashMap();
      Map m2 = new HashMap();
      m1.put(Boolean.class, Boolean.TYPE);
      m1.put(Byte.class, Byte.TYPE);
      m1.put(Character.class, Character.TYPE);
      m1.put(Double.class, Double.TYPE);
      m1.put(Float.class, Float.TYPE);
      m1.put(Integer.class, Integer.TYPE);
      m1.put(Long.class, Long.TYPE);
      m1.put(Short.class, Short.TYPE);
      m1.put(Void.class, Void.TYPE);
      Iterator var2 = m1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry e = (Map.Entry)var2.next();
         m2.put(e.getValue(), e.getKey());
      }

      boxToPrimitive = Collections.unmodifiableMap(m1);
      primitiveToBox = Collections.unmodifiableMap(m2);
   }

   private class JReferencedClass extends JClass implements JDeclaration {
      private final Class _class;

      JReferencedClass(Class _clazz) {
         super(JCodeModel.this);
         this._class = _clazz;

         assert !this._class.isArray();

      }

      public String name() {
         return this._class.getSimpleName().replace('$', '.');
      }

      public String fullName() {
         return this._class.getName().replace('$', '.');
      }

      public String binaryName() {
         return this._class.getName();
      }

      public JClass outer() {
         Class p = this._class.getDeclaringClass();
         return p == null ? null : JCodeModel.this.ref(p);
      }

      public JPackage _package() {
         String name = this.fullName();
         if (name.indexOf(91) != -1) {
            return JCodeModel.this._package("");
         } else {
            int idx = name.lastIndexOf(46);
            return idx < 0 ? JCodeModel.this._package("") : JCodeModel.this._package(name.substring(0, idx));
         }
      }

      public JClass _extends() {
         Class sp = this._class.getSuperclass();
         if (sp == null) {
            return this.isInterface() ? this.owner().ref(Object.class) : null;
         } else {
            return JCodeModel.this.ref(sp);
         }
      }

      public Iterator _implements() {
         final Class[] interfaces = this._class.getInterfaces();
         return new Iterator() {
            private int idx = 0;

            public boolean hasNext() {
               return this.idx < interfaces.length;
            }

            public JClass next() {
               return JCodeModel.this.ref(interfaces[this.idx++]);
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      public boolean isInterface() {
         return this._class.isInterface();
      }

      public boolean isAbstract() {
         return Modifier.isAbstract(this._class.getModifiers());
      }

      public JPrimitiveType getPrimitiveType() {
         Class v = (Class)JCodeModel.boxToPrimitive.get(this._class);
         return v != null ? JType.parse(JCodeModel.this, v.getName()) : null;
      }

      public boolean isArray() {
         return false;
      }

      public void declare(JFormatter f) {
      }

      public JTypeVar[] typeParams() {
         return super.typeParams();
      }

      protected JClass substituteParams(JTypeVar[] variables, List bindings) {
         return this;
      }
   }

   private final class TypeNameParser {
      private final String s;
      private int idx;

      public TypeNameParser(String s) {
         this.s = s;
      }

      JClass parseTypeName() throws ClassNotFoundException {
         int start = this.idx;
         if (this.s.charAt(this.idx) == '?') {
            ++this.idx;
            this.ws();
            String head = this.s.substring(this.idx);
            if (head.startsWith("extends")) {
               this.idx += 7;
               this.ws();
               return this.parseTypeName().wildcard();
            } else if (head.startsWith("super")) {
               throw new UnsupportedOperationException("? super T not implemented");
            } else {
               throw new IllegalArgumentException("only extends/super can follow ?, but found " + this.s.substring(this.idx));
            }
         } else {
            while(this.idx < this.s.length()) {
               char ch = this.s.charAt(this.idx);
               if (!Character.isJavaIdentifierStart(ch) && !Character.isJavaIdentifierPart(ch) && ch != '.') {
                  break;
               }

               ++this.idx;
            }

            JClass clazz = JCodeModel.this.ref(this.s.substring(start, this.idx));
            return this.parseSuffix(clazz);
         }
      }

      private JClass parseSuffix(JClass clazz) throws ClassNotFoundException {
         if (this.idx == this.s.length()) {
            return clazz;
         } else {
            char ch = this.s.charAt(this.idx);
            if (ch == '<') {
               return this.parseSuffix(this.parseArguments(clazz));
            } else if (ch == '[') {
               if (this.s.charAt(this.idx + 1) == ']') {
                  this.idx += 2;
                  return this.parseSuffix(clazz.array());
               } else {
                  throw new IllegalArgumentException("Expected ']' but found " + this.s.substring(this.idx + 1));
               }
            } else {
               return clazz;
            }
         }
      }

      private void ws() {
         while(Character.isWhitespace(this.s.charAt(this.idx)) && this.idx < this.s.length()) {
            ++this.idx;
         }

      }

      private JClass parseArguments(JClass rawType) throws ClassNotFoundException {
         if (this.s.charAt(this.idx) != '<') {
            throw new IllegalArgumentException();
         } else {
            ++this.idx;
            List args = new ArrayList();

            while(true) {
               args.add(this.parseTypeName());
               if (this.idx == this.s.length()) {
                  throw new IllegalArgumentException("Missing '>' in " + this.s);
               }

               char ch = this.s.charAt(this.idx);
               if (ch == '>') {
                  return rawType.narrow((JClass[])args.toArray(new JClass[args.size()]));
               }

               if (ch != ',') {
                  throw new IllegalArgumentException(this.s);
               }

               ++this.idx;
            }
         }
      }
   }
}
