package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JType;

public class SymbolSpace {
   private JType type;
   private final JCodeModel codeModel;

   public SymbolSpace(JCodeModel _codeModel) {
      this.codeModel = _codeModel;
   }

   public JType getType() {
      return (JType)(this.type == null ? this.codeModel.ref(Object.class) : this.type);
   }

   public void setType(JType _type) {
      if (this.type == null) {
         this.type = _type;
      }

   }

   public String toString() {
      return this.type == null ? "undetermined" : this.type.name();
   }
}
