package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import java.io.Serializable;
import java.util.ArrayList;

final class DefaultFieldRenderer implements FieldRenderer {
   private final FieldRendererFactory frf;
   private FieldRenderer defaultCollectionFieldRenderer;

   DefaultFieldRenderer(FieldRendererFactory frf) {
      this.frf = frf;
   }

   public DefaultFieldRenderer(FieldRendererFactory frf, FieldRenderer defaultCollectionFieldRenderer) {
      this.frf = frf;
      this.defaultCollectionFieldRenderer = defaultCollectionFieldRenderer;
   }

   public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo prop) {
      return this.decideRenderer(outline, prop).generate(outline, prop);
   }

   private FieldRenderer decideRenderer(ClassOutlineImpl outline, CPropertyInfo prop) {
      if (prop instanceof CReferencePropertyInfo) {
         CReferencePropertyInfo p = (CReferencePropertyInfo)prop;
         if (p.isDummy()) {
            return this.frf.getDummyList(outline.parent().getCodeModel().ref(ArrayList.class));
         }

         if (p.isContent() && p.isMixedExtendedCust()) {
            return this.frf.getContentList(outline.parent().getCodeModel().ref(ArrayList.class).narrow(Serializable.class));
         }
      }

      if (!prop.isCollection()) {
         return prop.isUnboxable() ? this.frf.getRequiredUnboxed() : this.frf.getSingle();
      } else {
         return this.defaultCollectionFieldRenderer == null ? this.frf.getList(outline.parent().getCodeModel().ref(ArrayList.class)) : this.defaultCollectionFieldRenderer;
      }
   }
}
