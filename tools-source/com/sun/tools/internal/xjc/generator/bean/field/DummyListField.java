package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import java.util.ArrayList;

public class DummyListField extends AbstractListField {
   private final JClass coreList;
   private JMethod $get;

   protected DummyListField(ClassOutlineImpl context, CPropertyInfo prop, JClass coreList) {
      super(context, prop, !coreList.fullName().equals("java.util.ArrayList"));
      this.coreList = coreList.narrow(this.exposedType.boxify());
      this.generate();
   }

   protected void annotate(JAnnotatable field) {
      super.annotate(field);
      if (this.prop instanceof CReferencePropertyInfo) {
         CReferencePropertyInfo pref = (CReferencePropertyInfo)this.prop;
         if (pref.isDummy()) {
            this.annotateDummy(field);
         }
      }

   }

   private void annotateDummy(JAnnotatable field) {
      field.annotate(OverrideAnnotationOf.class);
   }

   protected final JClass getCoreListType() {
      return this.coreList;
   }

   public void generateAccessors() {
   }

   public Accessor create(JExpression targetObject) {
      return new Accessor(targetObject);
   }

   class Accessor extends AbstractListField.Accessor {
      protected Accessor(JExpression $target) {
         super($target);
      }

      public void toRawValue(JBlock block, JVar $var) {
         block.assign($var, JExpr._new(DummyListField.this.codeModel.ref(ArrayList.class).narrow(DummyListField.this.exposedType.boxify())).arg((JExpression)this.$target.invoke(DummyListField.this.$get)));
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         JVar $list = block.decl(DummyListField.this.listT, uniqueName + 'l', this.$target.invoke(DummyListField.this.$get));
         block.invoke($list, (String)"addAll").arg($var);
      }
   }
}
