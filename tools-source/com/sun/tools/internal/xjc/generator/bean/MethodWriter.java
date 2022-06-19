package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.outline.ClassOutline;

public abstract class MethodWriter {
   protected final JCodeModel codeModel;

   protected MethodWriter(ClassOutline context) {
      this.codeModel = context.parent().getCodeModel();
   }

   public abstract JMethod declareMethod(JType var1, String var2);

   public final JMethod declareMethod(Class returnType, String methodName) {
      return this.declareMethod((JType)this.codeModel.ref(returnType), methodName);
   }

   public abstract JDocComment javadoc();

   public abstract JVar addParameter(JType var1, String var2);

   public final JVar addParameter(Class type, String name) {
      return this.addParameter((JType)this.codeModel.ref(type), name);
   }
}
