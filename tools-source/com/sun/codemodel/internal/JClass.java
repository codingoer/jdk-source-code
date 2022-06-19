package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class JClass extends JType {
   private final JCodeModel _owner;
   protected static final JTypeVar[] EMPTY_ARRAY = new JTypeVar[0];
   private JClass arrayClass;

   protected JClass(JCodeModel _owner) {
      this._owner = _owner;
   }

   public abstract String name();

   public abstract JPackage _package();

   public JClass outer() {
      return null;
   }

   public final JCodeModel owner() {
      return this._owner;
   }

   public abstract JClass _extends();

   public abstract Iterator _implements();

   public JTypeVar[] typeParams() {
      return EMPTY_ARRAY;
   }

   public abstract boolean isInterface();

   public abstract boolean isAbstract();

   public JPrimitiveType getPrimitiveType() {
      return null;
   }

   /** @deprecated */
   public JClass boxify() {
      return this;
   }

   public JType unboxify() {
      JPrimitiveType pt = this.getPrimitiveType();
      return (JType)(pt == null ? this : pt);
   }

   public JClass erasure() {
      return this;
   }

   public final boolean isAssignableFrom(JClass derived) {
      if (derived instanceof JNullType) {
         return true;
      } else if (this == derived) {
         return true;
      } else if (this == this._package().owner().ref(Object.class)) {
         return true;
      } else {
         JClass b = derived._extends();
         if (b != null && this.isAssignableFrom(b)) {
            return true;
         } else {
            if (this.isInterface()) {
               Iterator itfs = derived._implements();

               while(itfs.hasNext()) {
                  if (this.isAssignableFrom((JClass)itfs.next())) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public final JClass getBaseClass(JClass baseType) {
      if (this.erasure().equals(baseType)) {
         return this;
      } else {
         JClass b = this._extends();
         if (b != null) {
            JClass bc = b.getBaseClass(baseType);
            if (bc != null) {
               return bc;
            }
         }

         Iterator itfs = this._implements();

         JClass bc;
         do {
            if (!itfs.hasNext()) {
               return null;
            }

            bc = ((JClass)itfs.next()).getBaseClass(baseType);
         } while(bc == null);

         return bc;
      }
   }

   public final JClass getBaseClass(Class baseType) {
      return this.getBaseClass(this.owner().ref(baseType));
   }

   public JClass array() {
      if (this.arrayClass == null) {
         this.arrayClass = new JArrayClass(this.owner(), this);
      }

      return this.arrayClass;
   }

   public JClass narrow(Class clazz) {
      return this.narrow(this.owner().ref(clazz));
   }

   public JClass narrow(Class... clazz) {
      JClass[] r = new JClass[clazz.length];

      for(int i = 0; i < clazz.length; ++i) {
         r[i] = this.owner().ref(clazz[i]);
      }

      return this.narrow(r);
   }

   public JClass narrow(JClass clazz) {
      return new JNarrowedClass(this, clazz);
   }

   public JClass narrow(JType type) {
      return this.narrow(type.boxify());
   }

   public JClass narrow(JClass... clazz) {
      return new JNarrowedClass(this, Arrays.asList((Object[])clazz.clone()));
   }

   public JClass narrow(List clazz) {
      return new JNarrowedClass(this, new ArrayList(clazz));
   }

   public List getTypeParameters() {
      return Collections.emptyList();
   }

   public final boolean isParameterized() {
      return this.erasure() != this;
   }

   public final JClass wildcard() {
      return new JTypeWildcard(this);
   }

   protected abstract JClass substituteParams(JTypeVar[] var1, List var2);

   public String toString() {
      return this.getClass().getName() + '(' + this.name() + ')';
   }

   public final JExpression dotclass() {
      return JExpr.dotclass(this);
   }

   public final JInvocation staticInvoke(JMethod method) {
      return new JInvocation(this, method);
   }

   public final JInvocation staticInvoke(String method) {
      return new JInvocation(this, method);
   }

   public final JFieldRef staticRef(String field) {
      return new JFieldRef(this, field);
   }

   public final JFieldRef staticRef(JVar field) {
      return new JFieldRef(this, field);
   }

   public void generate(JFormatter f) {
      f.t(this);
   }

   void printLink(JFormatter f) {
      f.p("{@link ").g((JGenerable)this).p('}');
   }
}
