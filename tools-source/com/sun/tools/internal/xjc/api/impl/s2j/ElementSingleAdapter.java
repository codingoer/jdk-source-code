package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JConditional;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.FieldAccessor;
import com.sun.tools.internal.xjc.outline.FieldOutline;

final class ElementSingleAdapter extends ElementAdapter {
   public ElementSingleAdapter(FieldOutline core, CElementInfo ei) {
      super(core, ei);
   }

   public JType getRawType() {
      return this.ei.getContentInMemoryType().toType(this.outline(), Aspect.EXPOSED);
   }

   public FieldAccessor create(JExpression targetObject) {
      return new FieldAccessorImpl(targetObject);
   }

   final class FieldAccessorImpl extends ElementAdapter.FieldAccessorImpl {
      public FieldAccessorImpl(JExpression target) {
         super(target);
      }

      public void toRawValue(JBlock block, JVar $var) {
         JConditional cond = block._if(this.acc.hasSetValue());
         JVar $v = cond._then().decl(ElementSingleAdapter.this.core.getRawType(), "v" + this.hashCode());
         this.acc.toRawValue(cond._then(), $v);
         cond._then().assign($var, $v.invoke("getValue"));
         cond._else().assign($var, JExpr._null());
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         this.acc.fromRawValue(block, uniqueName, this.createJAXBElement($var));
      }
   }
}
