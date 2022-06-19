package com.sun.tools.internal.ws.processor.modeler.annotation;

import javax.xml.namespace.QName;

public class FaultInfo {
   public String beanName;
   public TypeMoniker beanTypeMoniker;
   public boolean isWsdlException;
   public QName elementName;

   public FaultInfo() {
   }

   public FaultInfo(String beanName) {
      this.beanName = beanName;
   }

   public FaultInfo(String beanName, boolean isWsdlException) {
      this.beanName = beanName;
      this.isWsdlException = isWsdlException;
   }

   public FaultInfo(TypeMoniker typeMoniker, boolean isWsdlException) {
      this.beanTypeMoniker = typeMoniker;
      this.isWsdlException = isWsdlException;
   }

   public void setIsWsdlException(boolean isWsdlException) {
      this.isWsdlException = isWsdlException;
   }

   public boolean isWsdlException() {
      return this.isWsdlException;
   }

   public void setBeanName(String beanName) {
      this.beanName = beanName;
   }

   public String getBeanName() {
      return this.beanName;
   }

   public void setElementName(QName elementName) {
      this.elementName = elementName;
   }

   public QName getElementName() {
      return this.elementName;
   }

   public void setBeanTypeMoniker(TypeMoniker typeMoniker) {
      this.beanTypeMoniker = typeMoniker;
   }

   public TypeMoniker getBeanTypeMoniker() {
      return this.beanTypeMoniker;
   }
}
