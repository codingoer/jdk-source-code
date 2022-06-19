package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.tools.internal.xjc.model.CElementInfo;

public abstract class ElementOutline {
   public final CElementInfo target;
   public final JDefinedClass implClass;

   public abstract Outline parent();

   public PackageOutline _package() {
      return this.parent().getPackageContext(this.implClass._package());
   }

   protected ElementOutline(CElementInfo target, JDefinedClass implClass) {
      this.target = target;
      this.implClass = implClass;
   }
}
