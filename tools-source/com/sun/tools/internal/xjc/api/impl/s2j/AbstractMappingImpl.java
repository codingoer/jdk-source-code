package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.Property;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElement;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSTerm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

abstract class AbstractMappingImpl implements Mapping {
   protected final JAXBModelImpl parent;
   protected final CElement clazz;
   private List drilldown = null;
   private boolean drilldownComputed = false;

   protected AbstractMappingImpl(JAXBModelImpl parent, CElement clazz) {
      this.parent = parent;
      this.clazz = clazz;
   }

   public final QName getElement() {
      return this.clazz.getElementName();
   }

   public final String getClazz() {
      return ((NType)this.clazz.getType()).fullName();
   }

   public final List getWrapperStyleDrilldown() {
      if (!this.drilldownComputed) {
         this.drilldownComputed = true;
         this.drilldown = this.calcDrilldown();
      }

      return this.drilldown;
   }

   protected abstract List calcDrilldown();

   protected List buildDrilldown(CClassInfo typeBean) {
      if (this.containingChoice(typeBean)) {
         return null;
      } else {
         CClassInfo bc = typeBean.getBaseClass();
         Object result;
         if (bc != null) {
            result = this.buildDrilldown(bc);
            if (result == null) {
               return null;
            }
         } else {
            result = new ArrayList();
         }

         Iterator var4 = typeBean.getProperties().iterator();

         while(var4.hasNext()) {
            CPropertyInfo p = (CPropertyInfo)var4.next();
            if (p instanceof CElementPropertyInfo) {
               CElementPropertyInfo ep = (CElementPropertyInfo)p;
               List ref = ep.getTypes();
               if (ref.size() != 1) {
                  return null;
               }

               ((List)result).add(this.createPropertyImpl(ep, ((CTypeRef)ref.get(0)).getTagName()));
            } else {
               if (!(p instanceof ReferencePropertyInfo)) {
                  return null;
               }

               CReferencePropertyInfo rp = (CReferencePropertyInfo)p;
               Collection elements = rp.getElements();
               if (elements.size() != 1) {
                  return null;
               }

               CElement ref = (CElement)elements.iterator().next();
               if (ref instanceof ClassInfo) {
                  ((List)result).add(this.createPropertyImpl(rp, ref.getElementName()));
               } else {
                  CElementInfo eref = (CElementInfo)ref;
                  if (!eref.getSubstitutionMembers().isEmpty()) {
                     return null;
                  }

                  Object fr;
                  if (rp.isCollection()) {
                     fr = new ElementCollectionAdapter(this.parent.outline.getField(rp), eref);
                  } else {
                     fr = new ElementSingleAdapter(this.parent.outline.getField(rp), eref);
                  }

                  ((List)result).add(new PropertyImpl(this, (FieldOutline)fr, eref.getElementName()));
               }
            }
         }

         return (List)result;
      }
   }

   private boolean containingChoice(CClassInfo typeBean) {
      XSComponent component = typeBean.getSchemaComponent();
      if (component instanceof XSComplexType) {
         XSContentType contentType = ((XSComplexType)component).getContentType();
         XSParticle particle = contentType.asParticle();
         if (particle != null) {
            XSTerm term = particle.getTerm();
            XSModelGroup modelGroup = term.asModelGroup();
            if (modelGroup != null) {
               return modelGroup.getCompositor() == XSModelGroup.Compositor.CHOICE;
            }
         }
      }

      return false;
   }

   private Property createPropertyImpl(CPropertyInfo p, QName tagName) {
      return new PropertyImpl(this, this.parent.outline.getField(p), tagName);
   }
}
