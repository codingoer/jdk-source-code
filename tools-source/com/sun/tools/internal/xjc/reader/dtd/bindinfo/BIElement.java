package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public final class BIElement {
   final BindInfo parent;
   private final Element e;
   public final CClassInfo clazz;
   private final List contents = new ArrayList();
   private final Map conversions = new HashMap();
   private BIContent rest;
   private final Map attributes = new HashMap();
   private final List constructors = new ArrayList();
   private final String className;

   BIElement(BindInfo bi, Element _e) {
      this.parent = bi;
      this.e = _e;
      Element c = DOMUtil.getElement(this.e, "content");
      if (c != null) {
         if (DOMUtil.getAttribute(c, "property") != null) {
            this.rest = BIContent.create(c, this);
         } else {
            Iterator var4 = DOMUtil.getChildElements(c).iterator();

            while(var4.hasNext()) {
               Element p = (Element)var4.next();
               if (p.getLocalName().equals("rest")) {
                  this.rest = BIContent.create(p, this);
               } else {
                  this.contents.add(BIContent.create(p, this));
               }
            }
         }
      }

      Iterator var6 = DOMUtil.getChildElements(this.e, "attribute").iterator();

      Element c;
      while(var6.hasNext()) {
         c = (Element)var6.next();
         BIAttribute a = new BIAttribute(this, c);
         this.attributes.put(a.name(), a);
      }

      String name;
      if (this.isClass()) {
         name = DOMUtil.getAttribute(this.e, "class");
         if (name == null) {
            name = NameConverter.standard.toClassName(this.name());
         }

         this.className = name;
      } else {
         this.className = null;
      }

      var6 = DOMUtil.getChildElements(this.e, "conversion").iterator();

      while(var6.hasNext()) {
         c = (Element)var6.next();
         BIConversion c = new BIUserConversion(bi, c);
         this.conversions.put(c.name(), c);
      }

      var6 = DOMUtil.getChildElements(this.e, "enumeration").iterator();

      while(var6.hasNext()) {
         c = (Element)var6.next();
         BIConversion c = BIEnumeration.create(c, this);
         this.conversions.put(c.name(), c);
      }

      var6 = DOMUtil.getChildElements(this.e, "constructor").iterator();

      while(var6.hasNext()) {
         c = (Element)var6.next();
         this.constructors.add(new BIConstructor(c));
      }

      name = this.name();
      QName tagName = new QName("", name);
      this.clazz = new CClassInfo(this.parent.model, this.parent.getTargetPackage(), this.className, this.getLocation(), (QName)null, tagName, (XSComponent)null, (CCustomizations)null);
   }

   public Locator getLocation() {
      return DOMLocator.getLocationInfo(this.e);
   }

   public String name() {
      return DOMUtil.getAttribute(this.e, "name");
   }

   public boolean isClass() {
      return "class".equals(this.e.getAttribute("type"));
   }

   public boolean isRoot() {
      return "true".equals(this.e.getAttribute("root"));
   }

   public String getClassName() {
      return this.className;
   }

   public void declareConstructors(CClassInfo src) {
      Iterator var2 = this.constructors.iterator();

      while(var2.hasNext()) {
         BIConstructor c = (BIConstructor)var2.next();
         c.createDeclaration(src);
      }

   }

   public BIConversion getConversion() {
      String cnv = DOMUtil.getAttribute(this.e, "convert");
      return cnv == null ? null : this.conversion(cnv);
   }

   public BIConversion conversion(String name) {
      BIConversion r = (BIConversion)this.conversions.get(name);
      return r != null ? r : this.parent.conversion(name);
   }

   public List getContents() {
      return this.contents;
   }

   public BIAttribute attribute(String name) {
      return (BIAttribute)this.attributes.get(name);
   }

   public BIContent getRest() {
      return this.rest;
   }

   public Locator getSourceLocation() {
      return DOMLocator.getLocationInfo(this.e);
   }
}
