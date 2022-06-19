package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import java.util.List;

final class ElementMappingImpl extends AbstractMappingImpl {
   private final TypeAndAnnotation taa;

   protected ElementMappingImpl(JAXBModelImpl parent, CElementInfo elementInfo) {
      super(parent, elementInfo);
      TypeUse t = ((CElementInfo)this.clazz).getContentType();
      if (((CElementInfo)this.clazz).getProperty().isCollection()) {
         t = TypeUseFactory.makeCollection((TypeUse)t);
      }

      CAdapter a = ((CElementInfo)this.clazz).getProperty().getAdapter();
      if (a != null) {
         t = TypeUseFactory.adapt((TypeUse)t, a);
      }

      this.taa = new TypeAndAnnotationImpl(parent.outline, (TypeUse)t);
   }

   public TypeAndAnnotation getType() {
      return this.taa;
   }

   public final List calcDrilldown() {
      CElementPropertyInfo p = ((CElementInfo)this.clazz).getProperty();
      if (p.getAdapter() != null) {
         return null;
      } else if (p.isCollection()) {
         return null;
      } else {
         CTypeInfo typeClass = (CTypeInfo)p.ref().get(0);
         if (!(typeClass instanceof CClassInfo)) {
            return null;
         } else {
            CClassInfo ci = (CClassInfo)typeClass;
            if (ci.isAbstract()) {
               return null;
            } else {
               return !ci.isOrdered() ? null : this.buildDrilldown(ci);
            }
         }
      }
   }
}
