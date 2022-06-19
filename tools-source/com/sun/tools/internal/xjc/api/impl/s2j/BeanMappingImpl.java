package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import java.util.List;

final class BeanMappingImpl extends AbstractMappingImpl {
   private final TypeAndAnnotationImpl taa;

   BeanMappingImpl(JAXBModelImpl parent, CClassInfo classInfo) {
      super(parent, classInfo);
      this.taa = new TypeAndAnnotationImpl(this.parent.outline, (TypeUse)this.clazz);

      assert classInfo.isElement();

   }

   public TypeAndAnnotation getType() {
      return this.taa;
   }

   public final String getTypeClass() {
      return this.getClazz();
   }

   public List calcDrilldown() {
      return !((CClassInfo)this.clazz).isOrdered() ? null : this.buildDrilldown((CClassInfo)this.clazz);
   }
}
