package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPrimitiveType;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.generator.bean.MethodWriter;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.FieldAccessor;
import com.sun.xml.internal.bind.api.impl.NameConverter;

public class UnboxedField extends AbstractFieldWithVar {
   private final JPrimitiveType ptype;

   protected UnboxedField(ClassOutlineImpl outline, CPropertyInfo prop) {
      super(outline, prop);

      assert this.implType == this.exposedType;

      this.ptype = (JPrimitiveType)this.implType;

      assert this.ptype != null;

      this.createField();
      MethodWriter writer = outline.createMethodWriter();
      NameConverter nc = outline.parent().getModel().getNameConverter();
      JMethod $get = writer.declareMethod((JType)this.ptype, this.getGetterMethod());
      String javadoc = prop.javadoc;
      if (javadoc.length() == 0) {
         javadoc = Messages.DEFAULT_GETTER_JAVADOC.format(nc.toVariableName(prop.getName(true)));
      }

      writer.javadoc().append(javadoc);
      $get.body()._return(this.ref());
      JMethod $set = writer.declareMethod((JType)this.codeModel.VOID, "set" + prop.getName(true));
      JVar $value = writer.addParameter((JType)this.ptype, "value");
      JBlock body = $set.body();
      body.assign(JExpr._this().ref((JVar)this.ref()), $value);
      writer.javadoc().append(Messages.DEFAULT_SETTER_JAVADOC.format(nc.toVariableName(prop.getName(true))));
   }

   protected JType getType(Aspect aspect) {
      return super.getType(aspect).boxify().getPrimitiveType();
   }

   protected JType getFieldType() {
      return this.ptype;
   }

   public FieldAccessor create(JExpression targetObject) {
      return new AbstractFieldWithVar.Accessor(targetObject) {
         public void unsetValues(JBlock body) {
         }

         public JExpression hasSetValue() {
            return JExpr.TRUE;
         }
      };
   }
}
