package com.sun.tools.internal.ws.wsdl.document.jaxws;

public class CustomName {
   private String javaDoc;
   private String name;

   public CustomName() {
   }

   public CustomName(String name, String javaDoc) {
      this.name = name;
      this.javaDoc = javaDoc;
   }

   public String getJavaDoc() {
      return this.javaDoc;
   }

   public void setJavaDoc(String javaDoc) {
      this.javaDoc = javaDoc;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
