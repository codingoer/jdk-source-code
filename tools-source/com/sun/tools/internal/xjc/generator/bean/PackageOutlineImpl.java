package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlSchemaWriter;
import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElement;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyVisitor;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.CValuePropertyInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.PackageOutline;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;

public final class PackageOutlineImpl implements PackageOutline {
   private final Model _model;
   private final JPackage _package;
   private final ObjectFactoryGenerator objectFactoryGenerator;
   final Set classes = new HashSet();
   private final Set classesView;
   private String mostUsedNamespaceURI;
   private XmlNsForm elementFormDefault;
   private XmlNsForm attributeFormDefault;
   private HashMap uriCountMap;
   private HashMap propUriCountMap;

   public String getMostUsedNamespaceURI() {
      return this.mostUsedNamespaceURI;
   }

   public XmlNsForm getAttributeFormDefault() {
      assert this.attributeFormDefault != null;

      return this.attributeFormDefault;
   }

   public XmlNsForm getElementFormDefault() {
      assert this.elementFormDefault != null;

      return this.elementFormDefault;
   }

   public JPackage _package() {
      return this._package;
   }

   public ObjectFactoryGenerator objectFactoryGenerator() {
      return this.objectFactoryGenerator;
   }

   public Set getClasses() {
      return this.classesView;
   }

   public JDefinedClass objectFactory() {
      return this.objectFactoryGenerator.getObjectFactory();
   }

   protected PackageOutlineImpl(BeanGenerator outline, Model model, JPackage _pkg) {
      this.classesView = Collections.unmodifiableSet(this.classes);
      this.uriCountMap = new HashMap();
      this.propUriCountMap = new HashMap();
      this._model = model;
      this._package = _pkg;
      switch (model.strategy) {
         case BEAN_ONLY:
            this.objectFactoryGenerator = new PublicObjectFactoryGenerator(outline, model, _pkg);
            break;
         case INTF_AND_IMPL:
            this.objectFactoryGenerator = new DualObjectFactoryGenerator(outline, model, _pkg);
            break;
         default:
            throw new IllegalStateException();
      }

   }

   public void calcDefaultValues() {
      if (!this._model.isPackageLevelAnnotations()) {
         this.mostUsedNamespaceURI = "";
         this.elementFormDefault = XmlNsForm.UNQUALIFIED;
      } else {
         CPropertyVisitor propVisitor = new CPropertyVisitor() {
            public Void onElement(CElementPropertyInfo p) {
               Iterator var2 = p.getTypes().iterator();

               while(var2.hasNext()) {
                  CTypeRef tr = (CTypeRef)var2.next();
                  PackageOutlineImpl.this.countURI(PackageOutlineImpl.this.propUriCountMap, tr.getTagName());
               }

               return null;
            }

            public Void onReference(CReferencePropertyInfo p) {
               Iterator var2 = p.getElements().iterator();

               while(var2.hasNext()) {
                  CElement e = (CElement)var2.next();
                  PackageOutlineImpl.this.countURI(PackageOutlineImpl.this.propUriCountMap, e.getElementName());
               }

               return null;
            }

            public Void onAttribute(CAttributePropertyInfo p) {
               return null;
            }

            public Void onValue(CValuePropertyInfo p) {
               return null;
            }
         };
         Iterator var2 = this.classes.iterator();

         while(var2.hasNext()) {
            ClassOutlineImpl co = (ClassOutlineImpl)var2.next();
            CClassInfo ci = co.target;
            this.countURI(this.uriCountMap, ci.getTypeName());
            this.countURI(this.uriCountMap, ci.getElementName());
            Iterator var5 = ci.getProperties().iterator();

            while(var5.hasNext()) {
               CPropertyInfo p = (CPropertyInfo)var5.next();
               p.accept(propVisitor);
            }
         }

         this.mostUsedNamespaceURI = this.getMostUsedURI(this.uriCountMap);
         this.elementFormDefault = this.getFormDefault();
         this.attributeFormDefault = XmlNsForm.UNQUALIFIED;

         try {
            XmlNsForm modelValue = this._model.getAttributeFormDefault(this.mostUsedNamespaceURI);
            this.attributeFormDefault = modelValue;
         } catch (Exception var7) {
         }

         if (!this.mostUsedNamespaceURI.equals("") || this.elementFormDefault == XmlNsForm.QUALIFIED || this.attributeFormDefault == XmlNsForm.QUALIFIED) {
            XmlSchemaWriter w = (XmlSchemaWriter)this._model.strategy.getPackage(this._package, Aspect.IMPLEMENTATION).annotate2(XmlSchemaWriter.class);
            if (!this.mostUsedNamespaceURI.equals("")) {
               w.namespace(this.mostUsedNamespaceURI);
            }

            if (this.elementFormDefault == XmlNsForm.QUALIFIED) {
               w.elementFormDefault(this.elementFormDefault);
            }

            if (this.attributeFormDefault == XmlNsForm.QUALIFIED) {
               w.attributeFormDefault(this.attributeFormDefault);
            }
         }

      }
   }

   private void countURI(HashMap map, QName qname) {
      if (qname != null) {
         String uri = qname.getNamespaceURI();
         if (map.containsKey(uri)) {
            map.put(uri, (Integer)map.get(uri) + 1);
         } else {
            map.put(uri, 1);
         }

      }
   }

   private String getMostUsedURI(HashMap map) {
      String mostPopular = null;
      int count = 0;
      Iterator var4 = map.entrySet().iterator();

      while(true) {
         while(var4.hasNext()) {
            Map.Entry e = (Map.Entry)var4.next();
            String uri = (String)e.getKey();
            int uriCount = (Integer)e.getValue();
            if (mostPopular == null) {
               mostPopular = uri;
               count = uriCount;
            } else if (uriCount > count || uriCount == count && mostPopular.equals("")) {
               mostPopular = uri;
               count = uriCount;
            }
         }

         if (mostPopular == null) {
            return "";
         }

         return mostPopular;
      }
   }

   private XmlNsForm getFormDefault() {
      return this.getMostUsedURI(this.propUriCountMap).equals("") ? XmlNsForm.UNQUALIFIED : XmlNsForm.QUALIFIED;
   }
}
