package com.sun.codemodel.internal;

import java.util.Iterator;
import java.util.List;

final class JTypeWildcard extends JClass {
   private final JClass bound;

   JTypeWildcard(JClass bound) {
      super(bound.owner());
      this.bound = bound;
   }

   public String name() {
      return "? extends " + this.bound.name();
   }

   public String fullName() {
      return "? extends " + this.bound.fullName();
   }

   public JPackage _package() {
      return null;
   }

   public JClass _extends() {
      return this.bound != null ? this.bound : this.owner().ref(Object.class);
   }

   public Iterator _implements() {
      return this.bound._implements();
   }

   public boolean isInterface() {
      return false;
   }

   public boolean isAbstract() {
      return false;
   }

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      JClass nb = this.bound.substituteParams(variables, bindings);
      return nb == this.bound ? this : new JTypeWildcard(nb);
   }

   public void generate(JFormatter f) {
      if (this.bound._extends() == null) {
         f.p("?");
      } else {
         f.p("? extends").g((JGenerable)this.bound);
      }

   }
}
