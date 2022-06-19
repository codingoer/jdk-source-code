package com.sun.tools.internal.ws.wsdl.document.jaxws;

import javax.xml.namespace.QName;

public class Parameter {
   private String part;
   private QName element;
   private String name;
   private String messageName;

   public Parameter(String msgName, String part, QName element, String name) {
      this.part = part;
      this.element = element;
      this.name = name;
      this.messageName = msgName;
   }

   public String getMessageName() {
      return this.messageName;
   }

   public void setMessageName(String messageName) {
      this.messageName = messageName;
   }

   public QName getElement() {
      return this.element;
   }

   public void setElement(QName element) {
      this.element = element;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPart() {
      return this.part;
   }

   public void setPart(String part) {
      this.part = part;
   }
}
