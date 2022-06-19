package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class JInvocation extends JExpressionImpl implements JStatement {
   private JGenerable object;
   private String name;
   private JMethod method;
   private boolean isConstructor;
   private List args;
   private JType type;

   JInvocation(JExpression object, String name) {
      this((JGenerable)object, (String)name);
   }

   JInvocation(JExpression object, JMethod method) {
      this((JGenerable)object, (JMethod)method);
   }

   JInvocation(JClass type, String name) {
      this((JGenerable)type, (String)name);
   }

   JInvocation(JClass type, JMethod method) {
      this((JGenerable)type, (JMethod)method);
   }

   private JInvocation(JGenerable object, String name) {
      this.isConstructor = false;
      this.args = new ArrayList();
      this.type = null;
      this.object = object;
      if (name.indexOf(46) >= 0) {
         throw new IllegalArgumentException("method name contains '.': " + name);
      } else {
         this.name = name;
      }
   }

   private JInvocation(JGenerable object, JMethod method) {
      this.isConstructor = false;
      this.args = new ArrayList();
      this.type = null;
      this.object = object;
      this.method = method;
   }

   JInvocation(JType c) {
      this.isConstructor = false;
      this.args = new ArrayList();
      this.type = null;
      this.isConstructor = true;
      this.type = c;
   }

   public JInvocation arg(JExpression arg) {
      if (arg == null) {
         throw new IllegalArgumentException();
      } else {
         this.args.add(arg);
         return this;
      }
   }

   public JInvocation arg(String v) {
      return this.arg(JExpr.lit(v));
   }

   public JExpression[] listArgs() {
      return (JExpression[])this.args.toArray(new JExpression[this.args.size()]);
   }

   public void generate(JFormatter f) {
      if (this.isConstructor && this.type.isArray()) {
         f.p("new").g((JGenerable)this.type).p('{');
      } else if (this.isConstructor) {
         f.p("new").g((JGenerable)this.type).p('(');
      } else {
         String name = this.name;
         if (name == null) {
            name = this.method.name();
         }

         if (this.object != null) {
            f.g(this.object).p('.').p(name).p('(');
         } else {
            f.id(name).p('(');
         }
      }

      f.g((Collection)this.args);
      if (this.isConstructor && this.type.isArray()) {
         f.p('}');
      } else {
         f.p(')');
      }

      if (this.type instanceof JDefinedClass && ((JDefinedClass)this.type).isAnonymous()) {
         ((JAnonymousClass)this.type).declareBody(f);
      }

   }

   public void state(JFormatter f) {
      f.g((JGenerable)this).p(';').nl();
   }
}
