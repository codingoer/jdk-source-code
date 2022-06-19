package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.Property;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

public class JAXBMapping {
   private QName elementName;
   private JAXBTypeAndAnnotation type;
   private List wrapperStyleDrilldown;

   public JAXBMapping() {
   }

   JAXBMapping(Mapping rawModel) {
      this.elementName = rawModel.getElement();
      TypeAndAnnotation typeAndAnno = rawModel.getType();
      this.type = new JAXBTypeAndAnnotation(typeAndAnno);
      List list = rawModel.getWrapperStyleDrilldown();
      if (list == null) {
         this.wrapperStyleDrilldown = null;
      } else {
         this.wrapperStyleDrilldown = new ArrayList(list.size());
         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            Property p = (Property)var4.next();
            this.wrapperStyleDrilldown.add(new JAXBProperty(p));
         }
      }

   }

   public QName getElementName() {
      return this.elementName;
   }

   public void setElementName(QName elementName) {
      this.elementName = elementName;
   }

   public JAXBTypeAndAnnotation getType() {
      return this.type;
   }

   public List getWrapperStyleDrilldown() {
      return this.wrapperStyleDrilldown;
   }
}
