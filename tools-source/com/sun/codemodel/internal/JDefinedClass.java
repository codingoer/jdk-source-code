package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class JDefinedClass extends JClass implements JDeclaration, JClassContainer, JGenerifiable, JAnnotatable, JDocCommentable {
   private String name;
   private JMods mods;
   private JClass superClass;
   private final Set interfaces;
   final Map fields;
   private JBlock init;
   private JDocComment jdoc;
   private final List constructors;
   private final List methods;
   private Map classes;
   private boolean hideFile;
   public Object metadata;
   private String directBlock;
   private JClassContainer outer;
   private final ClassType classType;
   private final Map enumConstantsByName;
   private List annotations;
   private final JGenerifiableImpl generifiable;

   JDefinedClass(JClassContainer parent, int mods, String name, ClassType classTypeval) {
      this(mods, name, parent, parent.owner(), classTypeval);
   }

   JDefinedClass(JCodeModel owner, int mods, String name) {
      this(mods, name, (JClassContainer)null, owner);
   }

   private JDefinedClass(int mods, String name, JClassContainer parent, JCodeModel owner) {
      this(mods, name, parent, owner, ClassType.CLASS);
   }

   private JDefinedClass(int mods, String name, JClassContainer parent, JCodeModel owner, ClassType classTypeVal) {
      super(owner);
      this.name = null;
      this.interfaces = new TreeSet();
      this.fields = new LinkedHashMap();
      this.init = null;
      this.jdoc = null;
      this.constructors = new ArrayList();
      this.methods = new ArrayList();
      this.hideFile = false;
      this.outer = null;
      this.enumConstantsByName = new LinkedHashMap();
      this.annotations = null;
      this.generifiable = new JGenerifiableImpl() {
         protected JCodeModel owner() {
            return JDefinedClass.this.owner();
         }
      };
      if (name != null) {
         if (name.trim().length() == 0) {
            throw new IllegalArgumentException("JClass name empty");
         }

         if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            String msg = "JClass name " + name + " contains illegal character for beginning of identifier: " + name.charAt(0);
            throw new IllegalArgumentException(msg);
         }

         for(int i = 1; i < name.length(); ++i) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) {
               String msg = "JClass name " + name + " contains illegal character " + name.charAt(i);
               throw new IllegalArgumentException(msg);
            }
         }
      }

      this.classType = classTypeVal;
      if (this.isInterface()) {
         this.mods = JMods.forInterface(mods);
      } else {
         this.mods = JMods.forClass(mods);
      }

      this.name = name;
      this.outer = parent;
   }

   public final boolean isAnonymous() {
      return this.name == null;
   }

   public JDefinedClass _extends(JClass superClass) {
      if (this.classType == ClassType.INTERFACE) {
         if (superClass.isInterface()) {
            return this._implements(superClass);
         } else {
            throw new IllegalArgumentException("unable to set the super class for an interface");
         }
      } else if (superClass == null) {
         throw new NullPointerException();
      } else {
         for(JClass o = superClass.outer(); o != null; o = o.outer()) {
            if (this == o) {
               throw new IllegalArgumentException("Illegal class inheritance loop.  Outer class " + this.name + " may not subclass from inner class: " + o.name());
            }
         }

         this.superClass = superClass;
         return this;
      }
   }

   public JDefinedClass _extends(Class superClass) {
      return this._extends(this.owner().ref(superClass));
   }

   public JClass _extends() {
      if (this.superClass == null) {
         this.superClass = this.owner().ref(Object.class);
      }

      return this.superClass;
   }

   public JDefinedClass _implements(JClass iface) {
      this.interfaces.add(iface);
      return this;
   }

   public JDefinedClass _implements(Class iface) {
      return this._implements(this.owner().ref(iface));
   }

   public Iterator _implements() {
      return this.interfaces.iterator();
   }

   public String name() {
      return this.name;
   }

   public JEnumConstant enumConstant(String name) {
      JEnumConstant ec = (JEnumConstant)this.enumConstantsByName.get(name);
      if (null == ec) {
         ec = new JEnumConstant(this, name);
         this.enumConstantsByName.put(name, ec);
      }

      return ec;
   }

   public String fullName() {
      if (this.outer instanceof JDefinedClass) {
         return ((JDefinedClass)this.outer).fullName() + '.' + this.name();
      } else {
         JPackage p = this._package();
         return p.isUnnamed() ? this.name() : p.name() + '.' + this.name();
      }
   }

   public String binaryName() {
      return this.outer instanceof JDefinedClass ? ((JDefinedClass)this.outer).binaryName() + '$' + this.name() : this.fullName();
   }

   public boolean isInterface() {
      return this.classType == ClassType.INTERFACE;
   }

   public boolean isAbstract() {
      return this.mods.isAbstract();
   }

   public JFieldVar field(int mods, JType type, String name) {
      return this.field(mods, (JType)type, name, (JExpression)null);
   }

   public JFieldVar field(int mods, Class type, String name) {
      return this.field(mods, this.owner()._ref(type), name);
   }

   public JFieldVar field(int mods, JType type, String name, JExpression init) {
      JFieldVar f = new JFieldVar(this, JMods.forField(mods), type, name, init);
      if (this.fields.containsKey(name)) {
         throw new IllegalArgumentException("trying to create the same field twice: " + name);
      } else {
         this.fields.put(name, f);
         return f;
      }
   }

   public boolean isAnnotationTypeDeclaration() {
      return this.classType == ClassType.ANNOTATION_TYPE_DECL;
   }

   public JDefinedClass _annotationTypeDeclaration(String name) throws JClassAlreadyExistsException {
      return this._class(1, name, ClassType.ANNOTATION_TYPE_DECL);
   }

   public JDefinedClass _enum(String name) throws JClassAlreadyExistsException {
      return this._class(1, name, ClassType.ENUM);
   }

   public JDefinedClass _enum(int mods, String name) throws JClassAlreadyExistsException {
      return this._class(mods, name, ClassType.ENUM);
   }

   public ClassType getClassType() {
      return this.classType;
   }

   public JFieldVar field(int mods, Class type, String name, JExpression init) {
      return this.field(mods, this.owner()._ref(type), name, init);
   }

   public Map fields() {
      return Collections.unmodifiableMap(this.fields);
   }

   public void removeField(JFieldVar field) {
      if (this.fields.remove(field.name()) != field) {
         throw new IllegalArgumentException();
      }
   }

   public JBlock init() {
      if (this.init == null) {
         this.init = new JBlock();
      }

      return this.init;
   }

   public JMethod constructor(int mods) {
      JMethod c = new JMethod(mods, this);
      this.constructors.add(c);
      return c;
   }

   public Iterator constructors() {
      return this.constructors.iterator();
   }

   public JMethod getConstructor(JType[] argTypes) {
      Iterator var2 = this.constructors.iterator();

      JMethod m;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         m = (JMethod)var2.next();
      } while(!m.hasSignature(argTypes));

      return m;
   }

   public JMethod method(int mods, JType type, String name) {
      JMethod m = new JMethod(this, mods, type, name);
      this.methods.add(m);
      return m;
   }

   public JMethod method(int mods, Class type, String name) {
      return this.method(mods, this.owner()._ref(type), name);
   }

   public Collection methods() {
      return this.methods;
   }

   public JMethod getMethod(String name, JType[] argTypes) {
      Iterator var3 = this.methods.iterator();

      JMethod m;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         m = (JMethod)var3.next();
      } while(!m.name().equals(name) || !m.hasSignature(argTypes));

      return m;
   }

   public boolean isClass() {
      return true;
   }

   public boolean isPackage() {
      return false;
   }

   public JPackage getPackage() {
      return this.parentContainer().getPackage();
   }

   public JDefinedClass _class(int mods, String name) throws JClassAlreadyExistsException {
      return this._class(mods, name, ClassType.CLASS);
   }

   /** @deprecated */
   public JDefinedClass _class(int mods, String name, boolean isInterface) throws JClassAlreadyExistsException {
      return this._class(mods, name, isInterface ? ClassType.INTERFACE : ClassType.CLASS);
   }

   public JDefinedClass _class(int mods, String name, ClassType classTypeVal) throws JClassAlreadyExistsException {
      String NAME;
      if (JCodeModel.isCaseSensitiveFileSystem) {
         NAME = name.toUpperCase();
      } else {
         NAME = name;
      }

      if (this.getClasses().containsKey(NAME)) {
         throw new JClassAlreadyExistsException((JDefinedClass)this.getClasses().get(NAME));
      } else {
         JDefinedClass c = new JDefinedClass(this, mods, name, classTypeVal);
         this.getClasses().put(NAME, c);
         return c;
      }
   }

   public JDefinedClass _class(String name) throws JClassAlreadyExistsException {
      return this._class(1, name);
   }

   public JDefinedClass _interface(int mods, String name) throws JClassAlreadyExistsException {
      return this._class(mods, name, ClassType.INTERFACE);
   }

   public JDefinedClass _interface(String name) throws JClassAlreadyExistsException {
      return this._interface(1, name);
   }

   public JDocComment javadoc() {
      if (this.jdoc == null) {
         this.jdoc = new JDocComment(this.owner());
      }

      return this.jdoc;
   }

   public void hide() {
      this.hideFile = true;
   }

   public boolean isHidden() {
      return this.hideFile;
   }

   public final Iterator classes() {
      return this.classes == null ? Collections.emptyList().iterator() : this.classes.values().iterator();
   }

   private Map getClasses() {
      if (this.classes == null) {
         this.classes = new TreeMap();
      }

      return this.classes;
   }

   public final JClass[] listClasses() {
      return this.classes == null ? new JClass[0] : (JClass[])this.classes.values().toArray(new JClass[this.classes.values().size()]);
   }

   public JClass outer() {
      return this.outer.isClass() ? (JClass)this.outer : null;
   }

   public void declare(JFormatter f) {
      if (this.jdoc != null) {
         f.nl().g((JGenerable)this.jdoc);
      }

      if (this.annotations != null) {
         Iterator var2 = this.annotations.iterator();

         while(var2.hasNext()) {
            JAnnotationUse annotation = (JAnnotationUse)var2.next();
            f.g((JGenerable)annotation).nl();
         }
      }

      f.g((JGenerable)this.mods).p(this.classType.declarationToken).id(this.name).d(this.generifiable);
      if (this.superClass != null && this.superClass != this.owner().ref(Object.class)) {
         f.nl().i().p("extends").g((JGenerable)this.superClass).nl().o();
      }

      if (!this.interfaces.isEmpty()) {
         if (this.superClass == null) {
            f.nl();
         }

         f.i().p(this.classType == ClassType.INTERFACE ? "extends" : "implements");
         f.g((Collection)this.interfaces);
         f.nl().o();
      }

      this.declareBody(f);
   }

   protected void declareBody(JFormatter f) {
      f.p('{').nl().nl().i();
      boolean first = true;
      Iterator var3;
      if (!this.enumConstantsByName.isEmpty()) {
         for(var3 = this.enumConstantsByName.values().iterator(); var3.hasNext(); first = false) {
            JEnumConstant c = (JEnumConstant)var3.next();
            if (!first) {
               f.p(',').nl();
            }

            f.d(c);
         }

         f.p(';').nl();
      }

      var3 = this.fields.values().iterator();

      while(var3.hasNext()) {
         JFieldVar field = (JFieldVar)var3.next();
         f.d(field);
      }

      if (this.init != null) {
         f.nl().p("static").s(this.init);
      }

      var3 = this.constructors.iterator();

      JMethod m;
      while(var3.hasNext()) {
         m = (JMethod)var3.next();
         f.nl().d(m);
      }

      var3 = this.methods.iterator();

      while(var3.hasNext()) {
         m = (JMethod)var3.next();
         f.nl().d(m);
      }

      if (this.classes != null) {
         var3 = this.classes.values().iterator();

         while(var3.hasNext()) {
            JDefinedClass dc = (JDefinedClass)var3.next();
            f.nl().d(dc);
         }
      }

      if (this.directBlock != null) {
         f.p(this.directBlock);
      }

      f.nl().o().p('}').nl();
   }

   public void direct(String string) {
      if (this.directBlock == null) {
         this.directBlock = string;
      } else {
         this.directBlock = this.directBlock + string;
      }

   }

   public final JPackage _package() {
      JClassContainer p;
      for(p = this.outer; !(p instanceof JPackage); p = p.parentContainer()) {
      }

      return (JPackage)p;
   }

   public final JClassContainer parentContainer() {
      return this.outer;
   }

   public JTypeVar generify(String name) {
      return this.generifiable.generify(name);
   }

   public JTypeVar generify(String name, Class bound) {
      return this.generifiable.generify(name, bound);
   }

   public JTypeVar generify(String name, JClass bound) {
      return this.generifiable.generify(name, bound);
   }

   public JTypeVar[] typeParams() {
      return this.generifiable.typeParams();
   }

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      return this;
   }

   public JAnnotationUse annotate(Class clazz) {
      return this.annotate(this.owner().ref(clazz));
   }

   public JAnnotationUse annotate(JClass clazz) {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      JAnnotationUse a = new JAnnotationUse(clazz);
      this.annotations.add(a);
      return a;
   }

   public JAnnotationWriter annotate2(Class clazz) {
      return TypedAnnotationWriter.create(clazz, this);
   }

   public Collection annotations() {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      return Collections.unmodifiableCollection(this.annotations);
   }

   public JMods mods() {
      return this.mods;
   }
}
