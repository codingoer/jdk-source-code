package com.sun.codemodel.internal;

final class JAtom extends JExpressionImpl {
   private final String what;

   JAtom(String what) {
      this.what = what;
   }

   public void generate(JFormatter f) {
      f.p(this.what);
   }
}
