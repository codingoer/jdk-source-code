package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JPrimitiveType;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.FieldAccessor;

final class ConstField extends AbstractField {
   private final JFieldVar $ref;

   ConstField(ClassOutlineImpl outline, CPropertyInfo prop) {
      super(outline, prop);

      assert !prop.isCollection();

      JPrimitiveType ptype = this.implType.boxify().getPrimitiveType();
      JExpression defaultValue = null;
      if (prop.defaultValue != null) {
         defaultValue = prop.defaultValue.compute(outline.parent());
      }

      this.$ref = outline.ref.field(25, (JType)(ptype != null ? ptype : this.implType), prop.getName(true), defaultValue);
      this.$ref.javadoc().append(prop.javadoc);
      this.annotate(this.$ref);
   }

   public JType getRawType() {
      return this.exposedType;
   }

   public FieldAccessor create(JExpression target) {
      return new Accessor(target);
   }

   private class Accessor extends AbstractField.Accessor {
      Accessor(JExpression $target) {
         super($target);
      }

      public void unsetValues(JBlock body) {
      }

      public JExpression hasSetValue() {
         return null;
      }

      public void toRawValue(JBlock block, JVar $var) {
         throw new UnsupportedOperationException();
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         throw new UnsupportedOperationException();
      }
   }
}
