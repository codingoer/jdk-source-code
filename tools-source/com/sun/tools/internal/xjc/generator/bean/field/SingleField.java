package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JConditional;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.generator.bean.MethodWriter;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.FieldAccessor;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import java.util.List;

public class SingleField extends AbstractFieldWithVar {
   protected SingleField(ClassOutlineImpl context, CPropertyInfo prop) {
      this(context, prop, false);
   }

   protected SingleField(ClassOutlineImpl context, CPropertyInfo prop, boolean forcePrimitiveAccess) {
      super(context, prop);

      assert !this.exposedType.isPrimitive() && !this.implType.isPrimitive();

      this.createField();
      MethodWriter writer = context.createMethodWriter();
      NameConverter nc = context.parent().getModel().getNameConverter();
      JExpression defaultValue = null;
      if (prop.defaultValue != null) {
         defaultValue = prop.defaultValue.compute(this.outline.parent());
      }

      JType getterType;
      if (this.getOptions().enableIntrospection) {
         if (forcePrimitiveAccess) {
            getterType = this.exposedType.unboxify();
         } else {
            getterType = this.exposedType;
         }
      } else if (defaultValue == null && !forcePrimitiveAccess) {
         getterType = this.exposedType;
      } else {
         getterType = this.exposedType.unboxify();
      }

      JMethod $get = writer.declareMethod(getterType, this.getGetterMethod());
      String javadoc = prop.javadoc;
      if (javadoc.length() == 0) {
         javadoc = Messages.DEFAULT_GETTER_JAVADOC.format(nc.toVariableName(prop.getName(true)));
      }

      writer.javadoc().append(javadoc);
      if (defaultValue == null) {
         $get.body()._return(this.ref());
      } else {
         JConditional cond = $get.body()._if(this.ref().eq(JExpr._null()));
         cond._then()._return(defaultValue);
         cond._else()._return(this.ref());
      }

      List possibleTypes = this.listPossibleTypes(prop);
      writer.javadoc().addReturn().append("possible object is\n").append(possibleTypes);
      JMethod $set = writer.declareMethod((JType)this.codeModel.VOID, "set" + prop.getName(true));
      JType setterType = this.exposedType;
      if (forcePrimitiveAccess) {
         setterType = setterType.unboxify();
      }

      JVar $value = writer.addParameter(setterType, "value");
      JBlock body = $set.body();
      if ($value.type().equals(this.implType)) {
         body.assign(JExpr._this().ref((JVar)this.ref()), $value);
      } else {
         body.assign(JExpr._this().ref((JVar)this.ref()), this.castToImplType($value));
      }

      writer.javadoc().append(Messages.DEFAULT_SETTER_JAVADOC.format(nc.toVariableName(prop.getName(true))));
      writer.javadoc().addParam($value).append("allowed object is\n").append(possibleTypes);
   }

   public final JType getFieldType() {
      return this.implType;
   }

   public FieldAccessor create(JExpression targetObject) {
      return new Accessor(targetObject);
   }

   protected class Accessor extends AbstractFieldWithVar.Accessor {
      protected Accessor(JExpression $target) {
         super($target);
      }

      public void unsetValues(JBlock body) {
         body.assign(this.$ref, JExpr._null());
      }

      public JExpression hasSetValue() {
         return this.$ref.ne(JExpr._null());
      }
   }
}
