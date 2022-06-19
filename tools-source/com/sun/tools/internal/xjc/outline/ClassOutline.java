package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import java.util.List;

public abstract class ClassOutline {
   @NotNull
   public final CClassInfo target;
   @NotNull
   public final JDefinedClass ref;
   @NotNull
   public final JDefinedClass implClass;
   @NotNull
   public final JClass implRef;

   @NotNull
   public abstract Outline parent();

   @NotNull
   public PackageOutline _package() {
      return this.parent().getPackageContext(this.ref._package());
   }

   protected ClassOutline(CClassInfo _target, JDefinedClass exposedClass, JClass implRef, JDefinedClass _implClass) {
      this.target = _target;
      this.ref = exposedClass;
      this.implRef = implRef;
      this.implClass = _implClass;
   }

   public final FieldOutline[] getDeclaredFields() {
      List props = this.target.getProperties();
      FieldOutline[] fr = new FieldOutline[props.size()];

      for(int i = 0; i < fr.length; ++i) {
         fr[i] = this.parent().getField((CPropertyInfo)props.get(i));
      }

      return fr;
   }

   public final ClassOutline getSuperClass() {
      CClassInfo s = this.target.getBaseClass();
      return s == null ? null : this.parent().getClazz(s);
   }
}
