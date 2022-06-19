package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClass;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSType;
import java.util.List;
import javax.xml.namespace.QName;

final class MixedComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      XSType bt = ct.getBaseType();
      if (bt == this.schemas.getAnyType() && ct.isMixed()) {
         return true;
      } else if (bt.isComplexType() && !bt.asComplexType().isMixed() && ct.isMixed() && ct.getDerivationMethod() == 1) {
         return this.bgmBuilder.isGenerateMixedExtensions() || ct.getContentType().asParticle() != null;
      } else {
         return false;
      }
   }

   public void build(XSComplexType ct) {
      XSContentType contentType = ct.getContentType();
      boolean generateMixedExtensions = this.bgmBuilder.isGenerateMixedExtensions();
      if (generateMixedExtensions && (ct.getBaseType() != this.schemas.getAnyType() || !ct.isMixed())) {
         XSComplexType baseType = ct.getBaseType().asComplexType();
         CClass baseClass = this.selector.bindToType(baseType, ct, true);
         this.selector.getCurrentBean().setBaseClass(baseClass);
      }

      this.builder.recordBindingMode(ct, ComplexTypeBindingMode.FALLBACK_CONTENT);
      BIProperty prop = BIProperty.getCustomization(ct);
      Object p;
      if (generateMixedExtensions) {
         List cType = ct.getSubtypes();
         boolean isSubtyped = cType != null && cType.size() > 0;
         if (contentType.asEmpty() != null) {
            if (isSubtyped) {
               p = prop.createContentExtendedMixedReferenceProperty("Content", ct, (RawTypeSet)null);
            } else {
               p = prop.createValueProperty("Content", false, ct, CBuiltinLeafInfo.STRING, (QName)null);
            }
         } else if (contentType.asParticle() == null) {
            p = prop.createContentExtendedMixedReferenceProperty("Content", ct, (RawTypeSet)null);
         } else {
            RawTypeSet ts = RawTypeSetBuilder.build(contentType.asParticle(), false);
            p = prop.createContentExtendedMixedReferenceProperty("Content", ct, ts);
         }
      } else if (contentType.asEmpty() != null) {
         p = prop.createValueProperty("Content", false, ct, CBuiltinLeafInfo.STRING, (QName)null);
      } else {
         RawTypeSet ts = RawTypeSetBuilder.build(contentType.asParticle(), false);
         p = prop.createReferenceProperty("Content", false, ct, ts, true, false, true, false);
      }

      this.selector.getCurrentBean().addProperty((CPropertyInfo)p);
      this.green.attContainer(ct);
   }
}
