package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.ws.processor.model.AbstractType;
import javax.xml.namespace.QName;

public class RpcLitMember extends AbstractType {
   private String javaTypeName;
   private QName schemaTypeName;

   public RpcLitMember() {
   }

   public RpcLitMember(QName name, String javaTypeName) {
      this.setName(name);
      this.javaTypeName = javaTypeName;
   }

   public RpcLitMember(QName name, String javaTypeName, QName schemaTypeName) {
      this.setName(name);
      this.javaTypeName = javaTypeName;
      this.schemaTypeName = schemaTypeName;
   }

   public String getJavaTypeName() {
      return this.javaTypeName;
   }

   public void setJavaTypeName(String type) {
      this.javaTypeName = type;
   }

   public QName getSchemaTypeName() {
      return this.schemaTypeName;
   }

   public void setSchemaTypeName(QName type) {
      this.schemaTypeName = type;
   }
}
