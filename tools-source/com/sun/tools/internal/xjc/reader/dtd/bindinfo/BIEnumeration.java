package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CEnumConstant;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public final class BIEnumeration implements BIConversion {
   private final Element e;
   private final TypeUse xducer;

   private BIEnumeration(Element _e, TypeUse _xducer) {
      this.e = _e;
      this.xducer = _xducer;
   }

   public String name() {
      return DOMUtil.getAttribute(this.e, "name");
   }

   public TypeUse getTransducer() {
      return this.xducer;
   }

   static BIEnumeration create(Element dom, BindInfo parent) {
      return new BIEnumeration(dom, new CEnumLeafInfo(parent.model, (QName)null, new CClassInfoParent.Package(parent.getTargetPackage()), DOMUtil.getAttribute(dom, "name"), CBuiltinLeafInfo.STRING, buildMemberList(parent.model, dom), (XSComponent)null, (CCustomizations)null, DOMLocator.getLocationInfo(dom)));
   }

   static BIEnumeration create(Element dom, BIElement parent) {
      return new BIEnumeration(dom, new CEnumLeafInfo(parent.parent.model, (QName)null, parent.clazz, DOMUtil.getAttribute(dom, "name"), CBuiltinLeafInfo.STRING, buildMemberList(parent.parent.model, dom), (XSComponent)null, (CCustomizations)null, DOMLocator.getLocationInfo(dom)));
   }

   private static List buildMemberList(Model model, Element dom) {
      List r = new ArrayList();
      String members = DOMUtil.getAttribute(dom, "members");
      if (members == null) {
         members = "";
      }

      StringTokenizer tokens = new StringTokenizer(members);

      while(tokens.hasMoreTokens()) {
         String token = tokens.nextToken();
         r.add(new CEnumConstant(model.getNameConverter().toConstantName(token), (String)null, token, (XSComponent)null, (CCustomizations)null, (Locator)null));
      }

      return r;
   }
}
