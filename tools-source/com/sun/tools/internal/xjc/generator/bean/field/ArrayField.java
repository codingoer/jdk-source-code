package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JAssignmentTarget;
import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JForLoop;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JOp;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.generator.bean.MethodWriter;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import java.util.List;

final class ArrayField extends AbstractListField {
   private JMethod $setAll;
   private JMethod $getAll;

   ArrayField(ClassOutlineImpl context, CPropertyInfo prop) {
      super(context, prop, false);
      this.generateArray();
   }

   protected final void generateArray() {
      this.field = this.outline.implClass.field(2, (JType)this.getCoreListType(), this.prop.getName(false));
      this.annotate(this.field);
      this.generateAccessors();
   }

   public void generateAccessors() {
      MethodWriter writer = this.outline.createMethodWriter();
      Accessor acc = this.create(JExpr._this());
      this.$getAll = writer.declareMethod((JType)this.exposedType.array(), "get" + this.prop.getName(true));
      writer.javadoc().append(this.prop.javadoc);
      JBlock body = this.$getAll.body();
      body._if(acc.ref(true).eq(JExpr._null()))._then()._return(JExpr.newArray(this.exposedType, 0));
      JVar var = body.decl(this.exposedType.array(), "retVal", JExpr.newArray(this.implType, acc.ref(true).ref("length")));
      body.add(this.codeModel.ref(System.class).staticInvoke("arraycopy").arg(acc.ref(true)).arg(JExpr.lit((int)0)).arg((JExpression)var).arg(JExpr.lit((int)0)).arg((JExpression)acc.ref(true).ref("length")));
      body._return(JExpr.direct("retVal"));
      List returnTypes = this.listPossibleTypes(this.prop);
      writer.javadoc().addReturn().append("array of\n").append(returnTypes);
      JMethod $get = writer.declareMethod(this.exposedType, "get" + this.prop.getName(true));
      JVar $idx = writer.addParameter((JType)this.codeModel.INT, "idx");
      $get.body()._if(acc.ref(true).eq(JExpr._null()))._then()._throw(JExpr._new(this.codeModel.ref(IndexOutOfBoundsException.class)));
      writer.javadoc().append(this.prop.javadoc);
      $get.body()._return(acc.ref(true).component($idx));
      writer.javadoc().addReturn().append("one of\n").append(returnTypes);
      JMethod $getLength = writer.declareMethod((JType)this.codeModel.INT, "get" + this.prop.getName(true) + "Length");
      $getLength.body()._if(acc.ref(true).eq(JExpr._null()))._then()._return(JExpr.lit((int)0));
      $getLength.body()._return(acc.ref(true).ref("length"));
      this.$setAll = writer.declareMethod((JType)this.codeModel.VOID, "set" + this.prop.getName(true));
      writer.javadoc().append(this.prop.javadoc);
      JVar $value = writer.addParameter((JType)this.exposedType.array(), "values");
      JVar $len = this.$setAll.body().decl(this.codeModel.INT, "len", $value.ref("length"));
      this.$setAll.body().assign((JAssignmentTarget)acc.ref(true), this.castToImplTypeArray(JExpr.newArray(this.codeModel.ref(this.exposedType.erasure().fullName()), $len)));
      JForLoop _for = this.$setAll.body()._for();
      JVar $i = _for.init(this.codeModel.INT, "i", JExpr.lit((int)0));
      _for.test(JOp.lt($i, $len));
      _for.update($i.incr());
      _for.body().assign(acc.ref(true).component($i), this.castToImplType(acc.box($value.component($i))));
      writer.javadoc().addParam($value).append("allowed objects are\n").append(returnTypes);
      JMethod $set = writer.declareMethod(this.exposedType, "set" + this.prop.getName(true));
      $idx = writer.addParameter((JType)this.codeModel.INT, "idx");
      $value = writer.addParameter(this.exposedType, "value");
      writer.javadoc().append(this.prop.javadoc);
      body = $set.body();
      body._return(JExpr.assign(acc.ref(true).component($idx), this.castToImplType(acc.box($value))));
      writer.javadoc().addParam($value).append("allowed object is\n").append(returnTypes);
   }

   public JType getRawType() {
      return this.exposedType.array();
   }

   protected JClass getCoreListType() {
      return this.exposedType.array();
   }

   public Accessor create(JExpression targetObject) {
      return new Accessor(targetObject);
   }

   protected final JExpression castToImplTypeArray(JExpression exp) {
      return JExpr.cast(this.implType.array(), exp);
   }

   class Accessor extends AbstractListField.Accessor {
      protected Accessor(JExpression $target) {
         super($target);
      }

      public void toRawValue(JBlock block, JVar $var) {
         block.assign($var, this.$target.invoke(ArrayField.this.$getAll));
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         block.invoke(this.$target, ArrayField.this.$setAll).arg($var);
      }

      public JExpression hasSetValue() {
         return this.field.ne(JExpr._null()).cand(this.field.ref("length").gt(JExpr.lit((int)0)));
      }
   }
}
