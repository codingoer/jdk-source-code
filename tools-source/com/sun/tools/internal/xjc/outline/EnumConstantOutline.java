package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JEnumConstant;
import com.sun.tools.internal.xjc.model.CEnumConstant;

public abstract class EnumConstantOutline {
   public final CEnumConstant target;
   public final JEnumConstant constRef;

   protected EnumConstantOutline(CEnumConstant target, JEnumConstant constRef) {
      this.target = target;
      this.constRef = constRef;
   }
}
