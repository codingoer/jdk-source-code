package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.FieldOutline;

final class ConstFieldRenderer implements FieldRenderer {
   private final FieldRenderer fallback;

   protected ConstFieldRenderer(FieldRenderer fallback) {
      this.fallback = fallback;
   }

   public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo prop) {
      return (FieldOutline)(prop.defaultValue.compute(outline.parent()) == null ? this.fallback.generate(outline, prop) : new ConstField(outline, prop));
   }
}
