package com.sun.codemodel.internal;

import com.sun.codemodel.internal.util.ClassNameComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class JMethod extends JGenerifiableImpl implements JDeclaration, JAnnotatable, JDocCommentable {
   private JMods mods;
   private JType type = null;
   private String name = null;
   private final List params = new ArrayList();
   private Set _throws;
   private JBlock body = null;
   private JDefinedClass outer;
   private JDocComment jdoc = null;
   private JVar varParam = null;
   private List annotations = null;
   private JExpression defaultValue = null;

   private boolean isConstructor() {
      return this.type == null;
   }

   JMethod(JDefinedClass outer, int mods, JType type, String name) {
      this.mods = JMods.forMethod(mods);
      this.type = type;
      this.name = name;
      this.outer = outer;
   }

   JMethod(int mods, JDefinedClass _class) {
      this.mods = JMods.forMethod(mods);
      this.type = null;
      this.name = _class.name();
      this.outer = _class;
   }

   private Set getThrows() {
      if (this._throws == null) {
         this._throws = new TreeSet(ClassNameComparator.theInstance);
      }

      return this._throws;
   }

   public JMethod _throws(JClass exception) {
      this.getThrows().add(exception);
      return this;
   }

   public JMethod _throws(Class exception) {
      return this._throws(this.outer.owner().ref(exception));
   }

   public List params() {
      return Collections.unmodifiableList(this.params);
   }

   public JVar param(int mods, JType type, String name) {
      JVar v = new JVar(JMods.forVar(mods), type, name, (JExpression)null);
      this.params.add(v);
      return v;
   }

   public JVar param(JType type, String name) {
      return this.param(0, (JType)type, name);
   }

   public JVar param(int mods, Class type, String name) {
      return this.param(mods, this.outer.owner()._ref(type), name);
   }

   public JVar param(Class type, String name) {
      return this.param(this.outer.owner()._ref(type), name);
   }

   public JVar varParam(Class type, String name) {
      return this.varParam(this.outer.owner()._ref(type), name);
   }

   public JVar varParam(JType type, String name) {
      if (!this.hasVarArgs()) {
         this.varParam = new JVar(JMods.forVar(0), type.array(), name, (JExpression)null);
         return this.varParam;
      } else {
         throw new IllegalStateException("Cannot have two varargs in a method,\nCheck if varParam method of JMethod is invoked more than once");
      }
   }

   public JAnnotationUse annotate(JClass clazz) {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      JAnnotationUse a = new JAnnotationUse(clazz);
      this.annotations.add(a);
      return a;
   }

   public JAnnotationUse annotate(Class clazz) {
      return this.annotate(this.owner().ref(clazz));
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

   public boolean hasVarArgs() {
      return this.varParam != null;
   }

   public String name() {
      return this.name;
   }

   public void name(String n) {
      this.name = n;
   }

   public JType type() {
      return this.type;
   }

   public void type(JType t) {
      this.type = t;
   }

   public JType[] listParamTypes() {
      JType[] r = new JType[this.params.size()];

      for(int i = 0; i < r.length; ++i) {
         r[i] = ((JVar)this.params.get(i)).type();
      }

      return r;
   }

   public JType listVarParamType() {
      return this.varParam != null ? this.varParam.type() : null;
   }

   public JVar[] listParams() {
      return (JVar[])this.params.toArray(new JVar[this.params.size()]);
   }

   public JVar listVarParam() {
      return this.varParam;
   }

   public boolean hasSignature(JType[] argTypes) {
      JVar[] p = this.listParams();
      if (p.length != argTypes.length) {
         return false;
      } else {
         for(int i = 0; i < p.length; ++i) {
            if (!p[i].type().equals(argTypes[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public JBlock body() {
      if (this.body == null) {
         this.body = new JBlock();
      }

      return this.body;
   }

   public void declareDefaultValue(JExpression value) {
      this.defaultValue = value;
   }

   public JDocComment javadoc() {
      if (this.jdoc == null) {
         this.jdoc = new JDocComment(this.owner());
      }

      return this.jdoc;
   }

   public void declare(JFormatter f) {
      if (this.jdoc != null) {
         f.g((JGenerable)this.jdoc);
      }

      if (this.annotations != null) {
         Iterator var2 = this.annotations.iterator();

         while(var2.hasNext()) {
            JAnnotationUse a = (JAnnotationUse)var2.next();
            f.g((JGenerable)a).nl();
         }
      }

      f.g((JGenerable)this.mods);
      super.declare(f);
      if (!this.isConstructor()) {
         f.g((JGenerable)this.type);
      }

      f.id(this.name).p('(').i();
      boolean first = true;

      for(Iterator var6 = this.params.iterator(); var6.hasNext(); first = false) {
         JVar var = (JVar)var6.next();
         if (!first) {
            f.p(',');
         }

         if (var.isAnnotated()) {
            f.nl();
         }

         f.b(var);
      }

      if (this.hasVarArgs()) {
         if (!first) {
            f.p(',');
         }

         f.g((JGenerable)this.varParam.type().elementType());
         f.p("... ");
         f.id(this.varParam.name());
      }

      f.o().p(')');
      if (this._throws != null && !this._throws.isEmpty()) {
         f.nl().i().p("throws").g((Collection)this._throws).nl().o();
      }

      if (this.defaultValue != null) {
         f.p("default ");
         f.g((JGenerable)this.defaultValue);
      }

      if (this.body != null) {
         f.s(this.body);
      } else if (!this.outer.isInterface() && !this.outer.isAnnotationTypeDeclaration() && !this.mods.isAbstract() && !this.mods.isNative()) {
         f.s(new JBlock());
      } else {
         f.p(';').nl();
      }

   }

   public JMods mods() {
      return this.mods;
   }

   /** @deprecated */
   public JMods getMods() {
      return this.mods;
   }

   protected JCodeModel owner() {
      return this.outer.owner();
   }
}
