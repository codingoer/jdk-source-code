package com.sun.codemodel.internal;

class JContinue implements JStatement {
   private final JLabel label;

   JContinue(JLabel _label) {
      this.label = _label;
   }

   public void state(JFormatter f) {
      if (this.label == null) {
         f.p("continue;").nl();
      } else {
         f.p("continue").p(this.label.label).p(';').nl();
      }

   }
}
