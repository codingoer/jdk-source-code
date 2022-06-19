package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;

final class FreshComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      return ct.getBaseType() == this.schemas.getAnyType() && !ct.isMixed();
   }

   public void build(final XSComplexType ct) {
      XSContentType contentType = ct.getContentType();
      contentType.visit(new XSContentTypeVisitor() {
         public void simpleType(XSSimpleType st) {
            FreshComplexTypeBuilder.this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
            FreshComplexTypeBuilder.this.simpleTypeBuilder.refererStack.push(ct);
            TypeUse use = FreshComplexTypeBuilder.this.simpleTypeBuilder.build(st);
            FreshComplexTypeBuilder.this.simpleTypeBuilder.refererStack.pop();
            BIProperty prop = BIProperty.getCustomization(ct);
            CPropertyInfo p = prop.createValueProperty("Value", false, ct, use, BGMBuilder.getName(st));
            FreshComplexTypeBuilder.this.selector.getCurrentBean().addProperty(p);
         }

         public void particle(XSParticle p) {
            FreshComplexTypeBuilder.this.builder.recordBindingMode(ct, FreshComplexTypeBuilder.this.bgmBuilder.getParticleBinder().checkFallback(p) ? ComplexTypeBindingMode.FALLBACK_CONTENT : ComplexTypeBindingMode.NORMAL);
            FreshComplexTypeBuilder.this.bgmBuilder.getParticleBinder().build(p);
            XSTerm term = p.getTerm();
            if (term.isModelGroup() && term.asModelGroup().getCompositor() == XSModelGroup.ALL) {
               FreshComplexTypeBuilder.this.selector.getCurrentBean().setOrdered(false);
            }

         }

         public void empty(XSContentType e) {
            FreshComplexTypeBuilder.this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
         }
      });
      this.green.attContainer(ct);
   }
}
