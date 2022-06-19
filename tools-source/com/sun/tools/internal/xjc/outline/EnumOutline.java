package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import java.util.ArrayList;
import java.util.List;

public abstract class EnumOutline {
   public final CEnumLeafInfo target;
   public final JDefinedClass clazz;
   public final List constants = new ArrayList();

   @NotNull
   public PackageOutline _package() {
      return this.parent().getPackageContext(this.clazz._package());
   }

   @NotNull
   public abstract Outline parent();

   protected EnumOutline(CEnumLeafInfo target, JDefinedClass clazz) {
      this.target = target;
      this.clazz = clazz;
   }
}
