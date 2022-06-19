package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;

class NClassByJClass implements NClass {
   final JClass clazz;

   NClassByJClass(JClass clazz) {
      this.clazz = clazz;
   }

   public JClass toType(Outline o, Aspect aspect) {
      return this.clazz;
   }

   public boolean isAbstract() {
      return this.clazz.isAbstract();
   }

   public boolean isBoxedType() {
      return this.clazz.getPrimitiveType() != null;
   }

   public String fullName() {
      return this.clazz.fullName();
   }
}
