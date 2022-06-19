package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JPrimitiveType;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlJavaTypeAdapterWriter;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapterMarker;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlList;

final class TypeAndAnnotationImpl implements TypeAndAnnotation {
   private final TypeUse typeUse;
   private final Outline outline;

   public TypeAndAnnotationImpl(Outline outline, TypeUse typeUse) {
      this.typeUse = typeUse;
      this.outline = outline;
   }

   public JType getTypeClass() {
      CAdapter a = this.typeUse.getAdapterUse();
      NType nt;
      if (a != null) {
         nt = (NType)a.customType;
      } else {
         nt = (NType)this.typeUse.getInfo().getType();
      }

      JType jt = nt.toType(this.outline, Aspect.EXPOSED);
      JPrimitiveType prim = ((JType)jt).boxify().getPrimitiveType();
      if (!this.typeUse.isCollection() && prim != null) {
         jt = prim;
      }

      if (this.typeUse.isCollection()) {
         jt = ((JType)jt).array();
      }

      return (JType)jt;
   }

   public void annotate(JAnnotatable programElement) {
      if (this.typeUse.getAdapterUse() != null || this.typeUse.isCollection()) {
         CAdapter adapterUse = this.typeUse.getAdapterUse();
         if (adapterUse != null) {
            if (adapterUse.getAdapterIfKnown() == SwaRefAdapterMarker.class) {
               programElement.annotate(XmlAttachmentRef.class);
            } else {
               ((XmlJavaTypeAdapterWriter)programElement.annotate2(XmlJavaTypeAdapterWriter.class)).value((JType)((NClass)adapterUse.adapterType).toType(this.outline, Aspect.EXPOSED));
            }
         }

         if (this.typeUse.isCollection()) {
            programElement.annotate(XmlList.class);
         }

      }
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getTypeClass());
      return builder.toString();
   }

   public boolean equals(Object o) {
      if (!(o instanceof TypeAndAnnotationImpl)) {
         return false;
      } else {
         TypeAndAnnotationImpl that = (TypeAndAnnotationImpl)o;
         return this.typeUse == that.typeUse;
      }
   }

   public int hashCode() {
      return this.typeUse.hashCode();
   }
}
