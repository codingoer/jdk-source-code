package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.ws.processor.model.AbstractType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class RpcLitStructure extends AbstractType {
   private List members;
   private JAXBModel jaxbModel;

   public RpcLitStructure() {
   }

   public RpcLitStructure(QName name, JAXBModel jaxbModel) {
      this.setName(name);
      this.jaxbModel = jaxbModel;
      this.members = new ArrayList();
   }

   public RpcLitStructure(QName name, JAXBModel jaxbModel, List members) {
      this.setName(name);
      this.members = members;
   }

   public void accept(JAXBTypeVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public List getRpcLitMembers() {
      return this.members;
   }

   public List setRpcLitMembers(List members) {
      return this.members = members;
   }

   public void addRpcLitMember(RpcLitMember member) {
      this.members.add(member);
   }

   public JAXBModel getJaxbModel() {
      return this.jaxbModel;
   }

   public void setJaxbModel(JAXBModel jaxbModel) {
      this.jaxbModel = jaxbModel;
   }

   public boolean isLiteralType() {
      return true;
   }
}
