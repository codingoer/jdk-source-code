package com.sun.codemodel.internal;

import java.util.Iterator;
import java.util.List;

public final class JTypeVar extends JClass implements JDeclaration {
   private final String name;
   private JClass bound;

   JTypeVar(JCodeModel owner, String _name) {
      super(owner);
      this.name = _name;
   }

   public String name() {
      return this.name;
   }

   public String fullName() {
      return this.name;
   }

   public JPackage _package() {
      return null;
   }

   public JTypeVar bound(JClass c) {
      if (this.bound != null) {
         throw new IllegalArgumentException("type variable has an existing class bound " + this.bound);
      } else {
         this.bound = c;
         return this;
      }
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

   public void declare(JFormatter f) {
      f.id(this.name);
      if (this.bound != null) {
         f.p("extends").g((JGenerable)this.bound);
      }

   }

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      for(int i = 0; i < variables.length; ++i) {
         if (variables[i] == this) {
            return (JClass)bindings.get(i);
         }
      }

      return this;
   }

   public void generate(JFormatter f) {
      f.id(this.name);
   }
}
