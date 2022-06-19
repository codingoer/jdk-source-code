package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JInvocation;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.ElementOutline;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

final class ElementOutlineImpl extends ElementOutline {
   private final BeanGenerator parent;

   public BeanGenerator parent() {
      return this.parent;
   }

   ElementOutlineImpl(BeanGenerator parent, CElementInfo ei) {
      super(ei, parent.getClassFactory().createClass(parent.getContainer(ei.parent, Aspect.EXPOSED), ei.shortName(), ei.getLocator()));
      this.parent = parent;
      parent.elements.put(ei, this);
      JCodeModel cm = parent.getCodeModel();
      this.implClass._extends(cm.ref(JAXBElement.class).narrow(this.target.getContentInMemoryType().toType(parent, Aspect.EXPOSED).boxify()));
      if (ei.hasClass()) {
         JType implType = ei.getContentInMemoryType().toType(parent, Aspect.IMPLEMENTATION);
         JExpression declaredType = JExpr.cast(cm.ref(Class.class), implType.boxify().dotclass());
         JClass scope = null;
         if (ei.getScope() != null) {
            scope = parent.getClazz(ei.getScope()).implRef;
         }

         JExpression scopeClass = scope == null ? JExpr._null() : scope.dotclass();
         JFieldVar valField = this.implClass.field(26, (Class)QName.class, "NAME", this.createQName(cm, ei.getElementName()));
         JMethod cons = this.implClass.constructor(1);
         cons.body().invoke("super").arg((JExpression)valField).arg((JExpression)declaredType).arg(scopeClass).arg((JExpression)cons.param(implType, "value"));
         JMethod noArgCons = this.implClass.constructor(1);
         noArgCons.body().invoke("super").arg((JExpression)valField).arg((JExpression)declaredType).arg(scopeClass).arg(JExpr._null());
      }

   }

   private JInvocation createQName(JCodeModel codeModel, QName name) {
      return JExpr._new(codeModel.ref(QName.class)).arg(name.getNamespaceURI()).arg(name.getLocalPart());
   }
}
