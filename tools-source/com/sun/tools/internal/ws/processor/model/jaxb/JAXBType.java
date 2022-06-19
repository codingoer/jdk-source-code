package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.ws.processor.model.AbstractType;
import com.sun.tools.internal.ws.processor.model.java.JavaType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class JAXBType extends AbstractType {
   private JAXBMapping jaxbMapping;
   private JAXBModel jaxbModel;
   private boolean unwrapped = false;
   private List wrapperChildren;

   public JAXBType(JAXBType jaxbType) {
      this.setName(jaxbType.getName());
      this.jaxbMapping = jaxbType.getJaxbMapping();
      this.jaxbModel = jaxbType.getJaxbModel();
      this.init();
   }

   public JAXBType() {
   }

   public JAXBType(QName name, JavaType type) {
      super(name, type);
   }

   public JAXBType(QName name, JavaType type, JAXBMapping jaxbMapping, JAXBModel jaxbModel) {
      super(name, type);
      this.jaxbMapping = jaxbMapping;
      this.jaxbModel = jaxbModel;
      this.init();
   }

   public void accept(JAXBTypeVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   private void init() {
      if (this.jaxbMapping != null) {
         this.wrapperChildren = this.jaxbMapping.getWrapperStyleDrilldown();
      } else {
         this.wrapperChildren = new ArrayList();
      }

   }

   public boolean isUnwrappable() {
      return this.jaxbMapping != null && this.jaxbMapping.getWrapperStyleDrilldown() != null;
   }

   public boolean hasWrapperChildren() {
      return this.wrapperChildren.size() > 0;
   }

   public boolean isLiteralType() {
      return true;
   }

   public List getWrapperChildren() {
      return this.wrapperChildren;
   }

   public void setWrapperChildren(List children) {
      this.wrapperChildren = children;
   }

   public JAXBMapping getJaxbMapping() {
      return this.jaxbMapping;
   }

   public void setJaxbMapping(JAXBMapping jaxbMapping) {
      this.jaxbMapping = jaxbMapping;
      this.init();
   }

   public void setUnwrapped(boolean unwrapped) {
      this.unwrapped = unwrapped;
   }

   public boolean isUnwrapped() {
      return this.unwrapped;
   }

   public JAXBModel getJaxbModel() {
      return this.jaxbModel;
   }

   public void setJaxbModel(JAXBModel jaxbModel) {
      this.jaxbModel = jaxbModel;
   }
}
