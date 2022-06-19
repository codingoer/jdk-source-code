package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.core.WildcardTypeInfo;
import com.sun.xml.internal.xsom.XSComponent;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public final class CWildcardTypeInfo extends AbstractCTypeInfoImpl implements WildcardTypeInfo {
   public static final CWildcardTypeInfo INSTANCE = new CWildcardTypeInfo();

   private CWildcardTypeInfo() {
      super((Model)null, (XSComponent)null, (CCustomizations)null);
   }

   public JType toType(Outline o, Aspect aspect) {
      return o.getCodeModel().ref(Element.class);
   }

   public NType getType() {
      return NavigatorImpl.create(Element.class);
   }

   public Locator getLocator() {
      return Model.EMPTY_LOCATOR;
   }
}
