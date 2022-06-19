package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JExpression;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.xsom.XmlString;

public abstract class CDefaultValue {
   public abstract JExpression compute(Outline var1);

   public static CDefaultValue create(final TypeUse typeUse, final XmlString defaultValue) {
      return new CDefaultValue() {
         public JExpression compute(Outline outline) {
            return typeUse.createConstant(outline, defaultValue);
         }
      };
   }
}
