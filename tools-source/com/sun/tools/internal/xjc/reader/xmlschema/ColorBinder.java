package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSVisitor;

abstract class ColorBinder extends BindingComponent implements XSVisitor {
   protected final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
   protected final ClassSelector selector = this.getClassSelector();

   protected final CClassInfo getCurrentBean() {
      return this.selector.getCurrentBean();
   }

   protected final XSComponent getCurrentRoot() {
      return this.selector.getCurrentRoot();
   }

   protected final void createSimpleTypeProperty(XSSimpleType type, String propName) {
      BIProperty prop = BIProperty.getCustomization(type);
      SimpleTypeBuilder stb = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
      CPropertyInfo p = prop.createValueProperty(propName, false, type, stb.buildDef(type), BGMBuilder.getName(type));
      this.getCurrentBean().addProperty(p);
   }

   public final void annotation(XSAnnotation xsAnnotation) {
      throw new IllegalStateException();
   }

   public final void schema(XSSchema xsSchema) {
      throw new IllegalStateException();
   }

   public final void facet(XSFacet xsFacet) {
      throw new IllegalStateException();
   }

   public final void notation(XSNotation xsNotation) {
      throw new IllegalStateException();
   }

   public final void identityConstraint(XSIdentityConstraint xsIdentityConstraint) {
      throw new IllegalStateException();
   }

   public final void xpath(XSXPath xsxPath) {
      throw new IllegalStateException();
   }
}
