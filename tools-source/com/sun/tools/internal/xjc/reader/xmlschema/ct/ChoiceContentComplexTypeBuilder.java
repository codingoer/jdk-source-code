package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSParticle;
import java.util.Collections;

final class ChoiceContentComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      if (!this.bgmBuilder.getGlobalBinding().isChoiceContentPropertyEnabled()) {
         return false;
      } else if (ct.getBaseType() != this.schemas.getAnyType()) {
         return false;
      } else {
         XSParticle p = ct.getContentType().asParticle();
         if (p == null) {
            return false;
         } else {
            XSModelGroup mg = this.getTopLevelModelGroup(p);
            if (mg.getCompositor() != XSModelGroup.CHOICE) {
               return false;
            } else {
               return !p.isRepeated();
            }
         }
      }
   }

   private XSModelGroup getTopLevelModelGroup(XSParticle p) {
      XSModelGroup mg = p.getTerm().asModelGroup();
      if (p.getTerm().isModelGroupDecl()) {
         mg = p.getTerm().asModelGroupDecl().getModelGroup();
      }

      return mg;
   }

   public void build(XSComplexType ct) {
      XSParticle p = ct.getContentType().asParticle();
      this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
      this.bgmBuilder.getParticleBinder().build(p, Collections.singleton(p));
      this.green.attContainer(ct);
   }
}
