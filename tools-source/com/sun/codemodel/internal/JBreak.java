package com.sun.codemodel.internal;

final class JBreak implements JStatement {
   private final JLabel label;

   JBreak(JLabel _label) {
      this.label = _label;
   }

   public void state(JFormatter f) {
      if (this.label == null) {
         f.p("break;").nl();
      } else {
         f.p("break").p(this.label.label).p(';').nl();
      }

   }
}
