package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;

public class JAXBTypeAndAnnotation {
   TypeAndAnnotation typeAnn;
   JType type;

   public JAXBTypeAndAnnotation(TypeAndAnnotation typeAnn) {
      this.typeAnn = typeAnn;
      this.type = typeAnn.getTypeClass();
   }

   public JAXBTypeAndAnnotation(JType type) {
      this.type = type;
   }

   public JAXBTypeAndAnnotation(TypeAndAnnotation typeAnn, JType type) {
      this.typeAnn = typeAnn;
      this.type = type;
   }

   public void annotate(JAnnotatable typeVar) {
      if (this.typeAnn != null) {
         this.typeAnn.annotate(typeVar);
      }

   }

   public JType getType() {
      return this.type;
   }

   public String getName() {
      return this.type.fullName();
   }

   public TypeAndAnnotation getTypeAnn() {
      return this.typeAnn;
   }

   public void setTypeAnn(TypeAndAnnotation typeAnn) {
      this.typeAnn = typeAnn;
   }

   public void setType(JType type) {
      this.type = type;
   }
}
