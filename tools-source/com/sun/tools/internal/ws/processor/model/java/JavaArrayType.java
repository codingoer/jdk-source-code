package com.sun.tools.internal.ws.processor.model.java;

public class JavaArrayType extends JavaType {
   private String elementName;
   private JavaType elementType;
   private String soapArrayHolderName;

   public JavaArrayType() {
   }

   public JavaArrayType(String name) {
      super(name, true, "null");
   }

   public JavaArrayType(String name, String elementName, JavaType elementType) {
      super(name, true, "null");
      this.elementName = elementName;
      this.elementType = elementType;
   }

   public String getElementName() {
      return this.elementName;
   }

   public void setElementName(String name) {
      this.elementName = name;
   }

   public JavaType getElementType() {
      return this.elementType;
   }

   public void setElementType(JavaType type) {
      this.elementType = type;
   }

   public String getSOAPArrayHolderName() {
      return this.soapArrayHolderName;
   }

   public void setSOAPArrayHolderName(String holderName) {
      this.soapArrayHolderName = holderName;
   }
}
