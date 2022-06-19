package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSType;
import javax.xml.namespace.QName;

final class MultiWildcardComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      if (!this.bgmBuilder.model.options.contentForWildcard) {
         return false;
      } else {
         XSType bt = ct.getBaseType();
         if (bt == this.schemas.getAnyType() && ct.getContentType() != null) {
            XSParticle part = ct.getContentType().asParticle();
            if (part != null && part.getTerm().isModelGroup()) {
               XSParticle[] parts = part.getTerm().asModelGroup().getChildren();
               int wildcardCount = 0;

               for(int i = 0; i < parts.length && wildcardCount <= 1; ++i) {
                  if (parts[i].getTerm().isWildcard()) {
                     ++wildcardCount;
                  }
               }

               return wildcardCount > 1;
            }
         }

         return false;
      }
   }

   public void build(XSComplexType ct) {
      XSContentType contentType = ct.getContentType();
      this.builder.recordBindingMode(ct, ComplexTypeBindingMode.FALLBACK_CONTENT);
      BIProperty prop = BIProperty.getCustomization(ct);
      Object p;
      if (contentType.asEmpty() != null) {
         p = prop.createValueProperty("Content", false, ct, CBuiltinLeafInfo.STRING, (QName)null);
      } else {
         RawTypeSet ts = RawTypeSetBuilder.build(contentType.asParticle(), false);
         p = prop.createReferenceProperty("Content", false, ct, ts, true, false, true, false);
      }

      this.selector.getCurrentBean().addProperty((CPropertyInfo)p);
      this.green.attContainer(ct);
   }
}
