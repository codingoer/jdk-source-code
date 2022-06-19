package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.tools.internal.xjc.model.Model;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

final class CollectionTypeAttribute {
   @XmlValue
   String collectionType = null;
   @XmlTransient
   private FieldRenderer fr;

   FieldRenderer get(Model m) {
      if (this.fr == null) {
         this.fr = this.calcFr(m);
      }

      return this.fr;
   }

   private FieldRenderer calcFr(Model m) {
      FieldRendererFactory frf = m.options.getFieldRendererFactory();
      if (this.collectionType == null) {
         return frf.getDefault();
      } else {
         return this.collectionType.equals("indexed") ? frf.getArray() : frf.getList(m.codeModel.ref(this.collectionType));
      }
   }
}
