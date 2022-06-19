package com.sun.codemodel.internal;

class JAnonymousClass extends JDefinedClass {
   private final JClass base;

   JAnonymousClass(JClass _base) {
      super(_base.owner(), 0, (String)null);
      this.base = _base;
   }

   public String fullName() {
      return this.base.fullName();
   }

   public void generate(JFormatter f) {
      f.t(this.base);
   }
}
