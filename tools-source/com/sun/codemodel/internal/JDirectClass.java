package com.sun.codemodel.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class JDirectClass extends JClass {
   private final String fullName;

   public JDirectClass(JCodeModel _owner, String fullName) {
      super(_owner);
      this.fullName = fullName;
   }

   public String name() {
      int i = this.fullName.lastIndexOf(46);
      return i >= 0 ? this.fullName.substring(i + 1) : this.fullName;
   }

   public String fullName() {
      return this.fullName;
   }

   public JPackage _package() {
      int i = this.fullName.lastIndexOf(46);
      return i >= 0 ? this.owner()._package(this.fullName.substring(0, i)) : this.owner().rootPackage();
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

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      return this;
   }
}
