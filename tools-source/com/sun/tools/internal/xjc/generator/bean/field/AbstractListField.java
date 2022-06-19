package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldRef;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JOp;
import com.sun.codemodel.internal.JPrimitiveType;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import java.util.List;

abstract class AbstractListField extends AbstractField {
   protected JFieldVar field;
   private JMethod internalGetter;
   protected final JPrimitiveType primitiveType;
   protected final JClass listT;
   private final boolean eagerInstanciation;

   protected AbstractListField(ClassOutlineImpl outline, CPropertyInfo prop, boolean eagerInstanciation) {
      super(outline, prop);
      this.listT = this.codeModel.ref(List.class).narrow(this.exposedType.boxify());
      this.eagerInstanciation = eagerInstanciation;
      if (this.implType instanceof JPrimitiveType) {
         assert this.implType == this.exposedType;

         this.primitiveType = (JPrimitiveType)this.implType;
      } else {
         this.primitiveType = null;
      }

   }

   protected final void generate() {
      this.field = this.outline.implClass.field(2, (JType)this.listT, this.prop.getName(false));
      if (this.eagerInstanciation) {
         this.field.init(this.newCoreList());
      }

      this.annotate(this.field);
      this.generateAccessors();
   }

   private void generateInternalGetter() {
      this.internalGetter = this.outline.implClass.method(2, (JType)this.listT, "_get" + this.prop.getName(true));
      if (!this.eagerInstanciation) {
         this.fixNullRef(this.internalGetter.body());
      }

      this.internalGetter.body()._return(this.field);
   }

   protected final void fixNullRef(JBlock block) {
      block._if(this.field.eq(JExpr._null()))._then().assign(this.field, this.newCoreList());
   }

   public JType getRawType() {
      return this.codeModel.ref(List.class).narrow(this.exposedType.boxify());
   }

   private JExpression newCoreList() {
      return JExpr._new(this.getCoreListType());
   }

   protected abstract JClass getCoreListType();

   protected abstract void generateAccessors();

   protected abstract class Accessor extends AbstractField.Accessor {
      protected final JFieldRef field;

      protected Accessor(JExpression $target) {
         super($target);
         this.field = $target.ref((JVar)AbstractListField.this.field);
      }

      protected final JExpression unbox(JExpression exp) {
         return AbstractListField.this.primitiveType == null ? exp : AbstractListField.this.primitiveType.unwrap(exp);
      }

      protected final JExpression box(JExpression exp) {
         return AbstractListField.this.primitiveType == null ? exp : AbstractListField.this.primitiveType.wrap(exp);
      }

      protected final JExpression ref(boolean canBeNull) {
         if (canBeNull) {
            return this.field;
         } else {
            if (AbstractListField.this.internalGetter == null) {
               AbstractListField.this.generateInternalGetter();
            }

            return this.$target.invoke(AbstractListField.this.internalGetter);
         }
      }

      public JExpression count() {
         return JOp.cond(this.field.eq(JExpr._null()), JExpr.lit((int)0), this.field.invoke("size"));
      }

      public void unsetValues(JBlock body) {
         body.assign(this.field, JExpr._null());
      }

      public JExpression hasSetValue() {
         return this.field.ne(JExpr._null()).cand(this.field.invoke("isEmpty").not());
      }
   }
}
