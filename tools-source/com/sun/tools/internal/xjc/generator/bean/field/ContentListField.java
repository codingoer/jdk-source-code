package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.generator.bean.MethodWriter;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import java.io.Serializable;
import java.util.ArrayList;

public class ContentListField extends AbstractListField {
   private final JClass coreList;
   private JMethod $get;

   protected ContentListField(ClassOutlineImpl context, CPropertyInfo prop, JClass coreList) {
      super(context, prop, false);
      this.coreList = coreList;
      this.generate();
   }

   protected final JClass getCoreListType() {
      return this.coreList;
   }

   public void generateAccessors() {
      MethodWriter writer = this.outline.createMethodWriter();
      Accessor acc = this.create(JExpr._this());
      this.$get = writer.declareMethod((JType)this.listT, "get" + this.prop.getName(true));
      writer.javadoc().append(this.prop.javadoc);
      JBlock block = this.$get.body();
      this.fixNullRef(block);
      block._return(acc.ref(true));
      String pname = NameConverter.standard.toVariableName(this.prop.getName(true));
      writer.javadoc().append("Gets the value of the " + pname + " property.\n\n<p>\nThis accessor method returns a reference to the live list,\nnot a snapshot. Therefore any modification you make to the\nreturned list will be present inside the JAXB object.\nThis is why there is not a <CODE>set</CODE> method for the " + pname + " property.\n\n<p>\nFor example, to add a new item, do as follows:\n<pre>\n   get" + this.prop.getName(true) + "().add(newItem);\n</pre>\n\n\n");
      writer.javadoc().append("<p>\nObjects of the following type(s) are allowed in the list\n").append(this.listPossibleTypes(this.prop));
   }

   public Accessor create(JExpression targetObject) {
      return new Accessor(targetObject);
   }

   protected JType getType(Aspect aspect) {
      return (JType)(Aspect.IMPLEMENTATION.equals(aspect) ? super.getType(aspect) : this.codeModel.ref(Serializable.class));
   }

   class Accessor extends AbstractListField.Accessor {
      protected Accessor(JExpression $target) {
         super($target);
      }

      public void toRawValue(JBlock block, JVar $var) {
         block.assign($var, JExpr._new(ContentListField.this.codeModel.ref(ArrayList.class).narrow(ContentListField.this.exposedType.boxify())).arg((JExpression)this.$target.invoke(ContentListField.this.$get)));
      }

      public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
         JVar $list = block.decl(ContentListField.this.listT, uniqueName + 'l', this.$target.invoke(ContentListField.this.$get));
         block.invoke($list, (String)"addAll").arg($var);
      }
   }
}
