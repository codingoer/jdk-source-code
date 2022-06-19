package com.sun.codemodel.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class JNullType extends JClass {
   JNullType(JCodeModel _owner) {
      super(_owner);
   }

   public String name() {
      return "null";
   }

   public String fullName() {
      return "null";
   }

   public JPackage _package() {
      return this.owner()._package("");
   }

   public JClass _extends() {
      return null;
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
