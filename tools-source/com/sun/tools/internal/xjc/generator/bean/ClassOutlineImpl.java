package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.outline.ClassOutline;

public final class ClassOutlineImpl extends ClassOutline {
   private final BeanGenerator _parent;

   public MethodWriter createMethodWriter() {
      return this._parent.getModel().strategy.createMethodWriter(this);
   }

   public PackageOutlineImpl _package() {
      return (PackageOutlineImpl)super._package();
   }

   ClassOutlineImpl(BeanGenerator _parent, CClassInfo _target, JDefinedClass exposedClass, JDefinedClass _implClass, JClass _implRef) {
      super(_target, exposedClass, _implRef, _implClass);
      this._parent = _parent;
      this._package().classes.add(this);
   }

   public BeanGenerator parent() {
      return this._parent;
   }
}
