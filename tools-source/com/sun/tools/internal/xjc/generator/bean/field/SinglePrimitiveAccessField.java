package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;

public class SinglePrimitiveAccessField extends SingleField {
   protected SinglePrimitiveAccessField(ClassOutlineImpl context, CPropertyInfo prop) {
      super(context, prop, true);
   }
}
