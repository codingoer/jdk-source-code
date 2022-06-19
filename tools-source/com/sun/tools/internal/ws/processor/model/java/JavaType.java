package com.sun.tools.internal.ws.processor.model.java;

import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;

public abstract class JavaType {
   private String name;
   private String realName;
   private boolean present;
   private boolean holder;
   private boolean holderPresent;
   private String initString;
   private String holderName;
   private JAXBTypeAndAnnotation type;

   public JavaType() {
   }

   public JavaType(JAXBTypeAndAnnotation type) {
      this.type = type;
      this.init(type.getName(), false, (String)null, (String)null);
   }

   public JavaType(String name, boolean present, String initString) {
      this.init(name, present, initString, (String)null);
   }

   public JavaType(String name, boolean present, String initString, String holderName) {
      this.init(name, present, initString, holderName);
   }

   public JAXBTypeAndAnnotation getType() {
      return this.type;
   }

   private void init(String name, boolean present, String initString, String holderName) {
      this.realName = name;
      this.name = name.replace('$', '.');
      this.present = present;
      this.initString = initString;
      this.holderName = holderName;
      this.holder = holderName != null;
   }

   public String getName() {
      return this.name;
   }

   public void doSetName(String name) {
      this.realName = name;
      this.name = name.replace('$', '.');
   }

   public String getRealName() {
      return this.realName;
   }

   public void setRealName(String s) {
      this.realName = s;
   }

   public String getFormalName() {
      return this.name;
   }

   public void setFormalName(String s) {
      this.name = s;
   }

   public boolean isPresent() {
      return this.present;
   }

   public void setPresent(boolean b) {
      this.present = b;
   }

   public boolean isHolder() {
      return this.holder;
   }

   public void setHolder(boolean holder) {
      this.holder = holder;
   }

   public boolean isHolderPresent() {
      return this.holderPresent;
   }

   public void setHolderPresent(boolean holderPresent) {
      this.holderPresent = holderPresent;
   }

   public String getInitString() {
      return this.initString;
   }

   public void setInitString(String s) {
      this.initString = s;
   }

   public String getHolderName() {
      return this.holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }
}
