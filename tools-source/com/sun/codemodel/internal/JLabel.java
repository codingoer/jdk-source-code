package com.sun.codemodel.internal;

public class JLabel implements JStatement {
   final String label;

   JLabel(String _label) {
      this.label = _label;
   }

   public void state(JFormatter f) {
      f.p(this.label + ':').nl();
   }
}
