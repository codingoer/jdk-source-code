package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JVar extends JExpressionImpl implements JDeclaration, JAssignmentTarget, JAnnotatable {
   private JMods mods;
   private JType type;
   private String name;
   private JExpression init;
   private List annotations = null;

   JVar(JMods mods, JType type, String name, JExpression init) {
      this.mods = mods;
      this.type = type;
      this.name = name;
      this.init = init;
   }

   public JVar init(JExpression init) {
      this.init = init;
      return this;
   }

   public String name() {
      return this.name;
   }

   public void name(String name) {
      if (!JJavaName.isJavaIdentifier(name)) {
         throw new IllegalArgumentException();
      } else {
         this.name = name;
      }
   }

   public JType type() {
      return this.type;
   }

   public JMods mods() {
      return this.mods;
   }

   public JType type(JType newType) {
      JType r = this.type;
      if (newType == null) {
         throw new IllegalArgumentException();
      } else {
         this.type = newType;
         return r;
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
      return this.annotate(this.type.owner().ref(clazz));
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

   protected boolean isAnnotated() {
      return this.annotations != null;
   }

   public void bind(JFormatter f) {
      if (this.annotations != null) {
         for(int i = 0; i < this.annotations.size(); ++i) {
            f.g((JGenerable)this.annotations.get(i)).nl();
         }
      }

      f.g((JGenerable)this.mods).g((JGenerable)this.type).id(this.name);
      if (this.init != null) {
         f.p('=').g((JGenerable)this.init);
      }

   }

   public void declare(JFormatter f) {
      f.b(this).p(';').nl();
   }

   public void generate(JFormatter f) {
      f.id(this.name);
   }

   public JExpression assign(JExpression rhs) {
      return JExpr.assign(this, rhs);
   }

   public JExpression assignPlus(JExpression rhs) {
      return JExpr.assignPlus(this, rhs);
   }
}
