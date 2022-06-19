package com.sun.codemodel.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class JArrayClass extends JClass {
   private final JType componentType;

   JArrayClass(JCodeModel owner, JType component) {
      super(owner);
      this.componentType = component;
   }

   public String name() {
      return this.componentType.name() + "[]";
   }

   public String fullName() {
      return this.componentType.fullName() + "[]";
   }

   public String binaryName() {
      return this.componentType.binaryName() + "[]";
   }

   public void generate(JFormatter f) {
      f.g((JGenerable)this.componentType).p("[]");
   }

   public JPackage _package() {
      return this.owner().rootPackage();
   }

   public JClass _extends() {
      return this.owner().ref(Object.class);
   }

   public Iterator _implements() {
      return Collections.emptyList().iterator();
   }

   public boolean isInterface() {
      return false;
   }

   public boolean isAbstract() {
      return false;
   }

   public JType elementType() {
      return this.componentType;
   }

   public boolean isArray() {
      return true;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof JArrayClass)) {
         return false;
      } else {
         return this.componentType.equals(((JArrayClass)obj).componentType);
      }
   }

   public int hashCode() {
      return this.componentType.hashCode();
   }

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      if (this.componentType.isPrimitive()) {
         return this;
      } else {
         JClass c = ((JClass)this.componentType).substituteParams(variables, bindings);
         return c == this.componentType ? this : new JArrayClass(this.owner(), c);
      }
   }
}
