package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JConditional;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JForEach;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.FieldAccessor;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import java.util.ArrayList;
import java.util.List;

final class ElementCollectionAdapter extends ElementAdapter {
   public ElementCollectionAdapter(FieldOutline core, CElementInfo ei) {
      super(core, ei);
   }

   public JType getRawType() {
      return this.codeModel().ref(List.class).narrow(this.itemType().boxify());
   }

   private JType itemType() {
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
         JCodeModel cm = ElementCollectionAdapter.this.outline().getCodeModel();
         JClass elementType = ElementCollectionAdapter.this.ei.toType(ElementCollectionAdapter.this.outline(), Aspect.EXPOSED).boxify();
         block.assign($var, JExpr._new(cm.ref(ArrayList.class).narrow(ElementCollectionAdapter.this.itemType().boxify())));
         JVar $col = block.decl(ElementCollectionAdapter.this.core.getRawType(), "col" + this.hashCode());
         this.acc.toRawValue(block, $col);
         JForEach loop = block.forEach(elementType, "v" + this.hashCode(), $col);
         JConditional cond = loop.body()._if(loop.var().eq(JExpr._null()));
         cond._then().invoke($var, (String)"add").arg(JExpr._null());
         cond._else().invoke($var, (String)"add").arg((JExpression)loop.var().invoke("getValue"));
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         JCodeModel cm = ElementCollectionAdapter.this.outline().getCodeModel();
         JClass elementType = ElementCollectionAdapter.this.ei.toType(ElementCollectionAdapter.this.outline(), Aspect.EXPOSED).boxify();
         JClass col = cm.ref(ArrayList.class).narrow(elementType);
         JVar $t = block.decl(col, uniqueName + "_col", JExpr._new(col));
         JForEach loop = block.forEach(ElementCollectionAdapter.this.itemType(), uniqueName + "_i", $t);
         loop.body().invoke($var, "add").arg((JExpression)this.createJAXBElement(loop.var()));
         this.acc.fromRawValue(block, uniqueName, $t);
      }
   }
}
