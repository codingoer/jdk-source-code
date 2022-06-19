package com.sun.codemodel.internal;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class JPackage implements JDeclaration, JGenerable, JClassContainer, JAnnotatable, Comparable, JDocCommentable {
   private String name;
   private final JCodeModel owner;
   private final Map classes = new TreeMap();
   private final Set resources = new HashSet();
   private final Map upperCaseClassMap;
   private List annotations = null;
   private JDocComment jdoc = null;

   JPackage(String name, JCodeModel cw) {
      this.owner = cw;
      if (name.equals(".")) {
         String msg = "Package name . is not allowed";
         throw new IllegalArgumentException(msg);
      } else {
         if (JCodeModel.isCaseSensitiveFileSystem) {
            this.upperCaseClassMap = null;
         } else {
            this.upperCaseClassMap = new HashMap();
         }

         this.name = name;
      }
   }

   public JClassContainer parentContainer() {
      return this.parent();
   }

   public JPackage parent() {
      if (this.name.length() == 0) {
         return null;
      } else {
         int idx = this.name.lastIndexOf(46);
         return this.owner._package(this.name.substring(0, idx));
      }
   }

   public boolean isClass() {
      return false;
   }

   public boolean isPackage() {
      return true;
   }

   public JPackage getPackage() {
      return this;
   }

   public JDefinedClass _class(int mods, String name) throws JClassAlreadyExistsException {
      return this._class(mods, name, ClassType.CLASS);
   }

   /** @deprecated */
   public JDefinedClass _class(int mods, String name, boolean isInterface) throws JClassAlreadyExistsException {
      return this._class(mods, name, isInterface ? ClassType.INTERFACE : ClassType.CLASS);
   }

   public JDefinedClass _class(int mods, String name, ClassType classTypeVal) throws JClassAlreadyExistsException {
      if (this.classes.containsKey(name)) {
         throw new JClassAlreadyExistsException((JDefinedClass)this.classes.get(name));
      } else {
         JDefinedClass c = new JDefinedClass(this, mods, name, classTypeVal);
         if (this.upperCaseClassMap != null) {
            JDefinedClass dc = (JDefinedClass)this.upperCaseClassMap.get(name.toUpperCase());
            if (dc != null) {
               throw new JClassAlreadyExistsException(dc);
            }

            this.upperCaseClassMap.put(name.toUpperCase(), c);
         }

         this.classes.put(name, c);
         return c;
      }
   }

   public JDefinedClass _class(String name) throws JClassAlreadyExistsException {
      return this._class(1, name);
   }

   public JDefinedClass _getClass(String name) {
      return this.classes.containsKey(name) ? (JDefinedClass)this.classes.get(name) : null;
   }

   public int compareTo(JPackage that) {
      return this.name.compareTo(that.name);
   }

   public JDefinedClass _interface(int mods, String name) throws JClassAlreadyExistsException {
      return this._class(mods, name, ClassType.INTERFACE);
   }

   public JDefinedClass _interface(String name) throws JClassAlreadyExistsException {
      return this._interface(1, name);
   }

   public JDefinedClass _annotationTypeDeclaration(String name) throws JClassAlreadyExistsException {
      return this._class(1, name, ClassType.ANNOTATION_TYPE_DECL);
   }

   public JDefinedClass _enum(String name) throws JClassAlreadyExistsException {
      return this._class(1, name, ClassType.ENUM);
   }

   public JResourceFile addResourceFile(JResourceFile rsrc) {
      this.resources.add(rsrc);
      return rsrc;
   }

   public boolean hasResourceFile(String name) {
      Iterator var2 = this.resources.iterator();

      JResourceFile r;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         r = (JResourceFile)var2.next();
      } while(!r.name().equals(name));

      return true;
   }

   public Iterator propertyFiles() {
      return this.resources.iterator();
   }

   public JDocComment javadoc() {
      if (this.jdoc == null) {
         this.jdoc = new JDocComment(this.owner());
      }

      return this.jdoc;
   }

   public void remove(JClass c) {
      if (c._package() != this) {
         throw new IllegalArgumentException("the specified class is not a member of this package, or it is a referenced class");
      } else {
         this.classes.remove(c.name());
         if (this.upperCaseClassMap != null) {
            this.upperCaseClassMap.remove(c.name().toUpperCase());
         }

      }
   }

   public JClass ref(String name) throws ClassNotFoundException {
      if (name.indexOf(46) >= 0) {
         throw new IllegalArgumentException("JClass name contains '.': " + name);
      } else {
         String n = "";
         if (!this.isUnnamed()) {
            n = this.name + '.';
         }

         n = n + name;
         return this.owner.ref(Class.forName(n));
      }
   }

   public JPackage subPackage(String pkg) {
      return this.isUnnamed() ? this.owner()._package(pkg) : this.owner()._package(this.name + '.' + pkg);
   }

   public Iterator classes() {
      return this.classes.values().iterator();
   }

   public boolean isDefined(String classLocalName) {
      Iterator itr = this.classes();

      do {
         if (!itr.hasNext()) {
            return false;
         }
      } while(!((JDefinedClass)itr.next()).name().equals(classLocalName));

      return true;
   }

   public final boolean isUnnamed() {
      return this.name.length() == 0;
   }

   public String name() {
      return this.name;
   }

   public final JCodeModel owner() {
      return this.owner;
   }

   public JAnnotationUse annotate(JClass clazz) {
      if (this.isUnnamed()) {
         throw new IllegalArgumentException("the root package cannot be annotated");
      } else {
         if (this.annotations == null) {
            this.annotations = new ArrayList();
         }

         JAnnotationUse a = new JAnnotationUse(clazz);
         this.annotations.add(a);
         return a;
      }
   }

   public JAnnotationUse annotate(Class clazz) {
      return this.annotate(this.owner.ref(clazz));
   }

   public JAnnotationWriter annotate2(Class clazz) {
      return TypedAnnotationWriter.create(clazz, this);
   }

   public Collection annotations() {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      return Collections.unmodifiableList(this.annotations);
   }

   File toPath(File dir) {
      return this.name == null ? dir : new File(dir, this.name.replace('.', File.separatorChar));
   }

   public void declare(JFormatter f) {
      if (this.name.length() != 0) {
         f.p("package").p(this.name).p(';').nl();
      }

   }

   public void generate(JFormatter f) {
      f.p(this.name);
   }

   void build(CodeWriter src, CodeWriter res) throws IOException {
      Iterator var3 = this.classes.values().iterator();

      while(var3.hasNext()) {
         JDefinedClass c = (JDefinedClass)var3.next();
         if (!c.isHidden()) {
            JFormatter f = this.createJavaSourceFileWriter(src, c.name());
            f.write(c);
            f.close();
         }
      }

      if (this.annotations != null || this.jdoc != null) {
         JFormatter f = this.createJavaSourceFileWriter(src, "package-info");
         if (this.jdoc != null) {
            f.g((JGenerable)this.jdoc);
         }

         if (this.annotations != null) {
            Iterator var8 = this.annotations.iterator();

            while(var8.hasNext()) {
               JAnnotationUse a = (JAnnotationUse)var8.next();
               f.g((JGenerable)a).nl();
            }
         }

         f.d(this);
         f.close();
      }

      var3 = this.resources.iterator();

      while(var3.hasNext()) {
         JResourceFile rsrc = (JResourceFile)var3.next();
         CodeWriter cw = rsrc.isResource() ? res : src;
         OutputStream os = new BufferedOutputStream(cw.openBinary(this, rsrc.name()));
         rsrc.build(os);
         os.close();
      }

   }

   int countArtifacts() {
      int r = 0;
      Iterator var2 = this.classes.values().iterator();

      while(var2.hasNext()) {
         JDefinedClass c = (JDefinedClass)var2.next();
         if (!c.isHidden()) {
            ++r;
         }
      }

      if (this.annotations != null || this.jdoc != null) {
         ++r;
      }

      r += this.resources.size();
      return r;
   }

   private JFormatter createJavaSourceFileWriter(CodeWriter src, String className) throws IOException {
      Writer bw = new BufferedWriter(src.openSource(this, className + ".java"));
      return new JFormatter(new PrintWriter(bw));
   }
}
