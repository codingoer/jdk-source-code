package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.api.ErrorListener;
import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.outline.PackageOutline;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

final class JAXBModelImpl implements S2JJAXBModel {
   final Outline outline;
   private final Model model;
   private final Map byXmlName = new HashMap();

   JAXBModelImpl(Outline outline) {
      this.model = outline.getModel();
      this.outline = outline;
      Iterator var2 = this.model.beans().values().iterator();

      while(var2.hasNext()) {
         CClassInfo ci = (CClassInfo)var2.next();
         if (ci.isElement()) {
            this.byXmlName.put(ci.getElementName(), new BeanMappingImpl(this, ci));
         }
      }

      var2 = this.model.getElementMappings((NClass)null).values().iterator();

      while(var2.hasNext()) {
         CElementInfo ei = (CElementInfo)var2.next();
         this.byXmlName.put(ei.getElementName(), new ElementMappingImpl(this, ei));
      }

   }

   public JCodeModel generateCode(Plugin[] extensions, ErrorListener errorListener) {
      return this.outline.getCodeModel();
   }

   public List getAllObjectFactories() {
      List r = new ArrayList();
      Iterator var2 = this.outline.getAllPackageContexts().iterator();

      while(var2.hasNext()) {
         PackageOutline pkg = (PackageOutline)var2.next();
         r.add(pkg.objectFactory());
      }

      return r;
   }

   public final Mapping get(QName elementName) {
      return (Mapping)this.byXmlName.get(elementName);
   }

   public final Collection getMappings() {
      return this.byXmlName.values();
   }

   public TypeAndAnnotation getJavaType(QName xmlTypeName) {
      TypeUse use = (TypeUse)this.model.typeUses().get(xmlTypeName);
      return use == null ? null : new TypeAndAnnotationImpl(this.outline, use);
   }

   public final List getClassList() {
      List classList = new ArrayList();
      Iterator var2 = this.outline.getAllPackageContexts().iterator();

      while(var2.hasNext()) {
         PackageOutline p = (PackageOutline)var2.next();
         classList.add(p.objectFactory().fullName());
      }

      return classList;
   }
}
