package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.CClass;
import com.sun.tools.internal.xjc.model.CDefaultValue;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;

public class BindPurple extends ColorBinder {
   public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
      throw new UnsupportedOperationException();
   }

   public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
      throw new UnsupportedOperationException();
   }

   public void attributeUse(XSAttributeUse use) {
      boolean hasFixedValue = use.getFixedValue() != null;
      BIProperty pc = BIProperty.getCustomization(use);
      boolean toConstant = pc.isConstantProperty() && hasFixedValue;
      TypeUse attType = this.bindAttDecl(use.getDecl());
      CPropertyInfo prop = pc.createAttributeProperty(use, attType);
      if (toConstant) {
         prop.defaultValue = CDefaultValue.create(attType, use.getFixedValue());
         prop.realization = this.builder.fieldRendererFactory.getConst(prop.realization);
      } else if (!attType.isCollection() && (prop.baseType == null || !prop.baseType.isPrimitive())) {
         if (use.getDefaultValue() != null) {
            prop.defaultValue = CDefaultValue.create(attType, use.getDefaultValue());
         } else if (use.getFixedValue() != null) {
            prop.defaultValue = CDefaultValue.create(attType, use.getFixedValue());
         }
      } else if (prop.baseType != null && prop.baseType.isPrimitive()) {
         ErrorReporter errorReporter = (ErrorReporter)Ring.get(ErrorReporter.class);
         errorReporter.warning(prop.getLocator(), "WARN_DEFAULT_VALUE_PRIMITIVE_TYPE", prop.baseType.name());
      }

      this.getCurrentBean().addProperty(prop);
   }

   private TypeUse bindAttDecl(XSAttributeDecl decl) {
      SimpleTypeBuilder stb = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
      stb.refererStack.push(decl);

      TypeUse var3;
      try {
         var3 = stb.build(decl.getType());
      } finally {
         stb.refererStack.pop();
      }

      return var3;
   }

   public void complexType(XSComplexType ct) {
      CClass ctBean = this.selector.bindToType(ct, (XSComponent)null, false);
      if (this.getCurrentBean() != ctBean) {
         this.getCurrentBean().setBaseClass(ctBean);
      }

   }

   public void wildcard(XSWildcard xsWildcard) {
      this.getCurrentBean().hasAttributeWildcard(true);
   }

   public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
      throw new UnsupportedOperationException();
   }

   public void modelGroup(XSModelGroup xsModelGroup) {
      throw new UnsupportedOperationException();
   }

   public void elementDecl(XSElementDecl xsElementDecl) {
      throw new UnsupportedOperationException();
   }

   public void simpleType(XSSimpleType type) {
      this.createSimpleTypeProperty(type, "Value");
   }

   public void particle(XSParticle xsParticle) {
      throw new UnsupportedOperationException();
   }

   public void empty(XSContentType ct) {
   }
}
