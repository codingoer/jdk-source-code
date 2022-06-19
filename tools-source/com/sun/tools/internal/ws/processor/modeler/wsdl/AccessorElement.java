package com.sun.tools.internal.ws.processor.modeler.wsdl;

import javax.xml.namespace.QName;

class AccessorElement {
   private QName type;
   private String name;

   public AccessorElement(String name, QName type) {
      this.type = type;
      this.name = name;
   }

   public QName getType() {
      return this.type;
   }

   public void setType(QName type) {
      this.type = type;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
