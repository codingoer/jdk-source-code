package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldRef;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;

abstract class AbstractFieldWithVar extends AbstractField {
   private JFieldVar field;

   AbstractFieldWithVar(ClassOutlineImpl outline, CPropertyInfo prop) {
      super(outline, prop);
   }

   protected final void createField() {
      this.field = this.outline.implClass.field(2, (JType)this.getFieldType(), this.prop.getName(false));
      this.annotate(this.field);
   }

   protected String getGetterMethod() {
      return !this.getOptions().enableIntrospection ? (this.getFieldType().boxify().getPrimitiveType() == this.codeModel.BOOLEAN ? "is" : "get") + this.prop.getName(true) : (this.getFieldType().isPrimitive() && this.getFieldType().boxify().getPrimitiveType() == this.codeModel.BOOLEAN ? "is" : "get") + this.prop.getName(true);
   }

   protected abstract JType getFieldType();

   protected JFieldVar ref() {
      return this.field;
   }

   public final JType getRawType() {
      return this.exposedType;
   }

   protected abstract class Accessor extends AbstractField.Accessor {
      protected final JFieldRef $ref;

      protected Accessor(JExpression $target) {
         super($target);
         this.$ref = $target.ref((JVar)AbstractFieldWithVar.this.ref());
      }

      public final void toRawValue(JBlock block, JVar $var) {
         if (AbstractFieldWithVar.this.getOptions().enableIntrospection) {
            block.assign($var, this.$target.invoke(AbstractFieldWithVar.this.getGetterMethod()));
         } else {
            block.assign($var, this.$target.invoke(AbstractFieldWithVar.this.getGetterMethod()));
         }

      }

      public final void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         block.invoke(this.$target, "set" + AbstractFieldWithVar.this.prop.getName(true)).arg($var);
      }
   }
}
