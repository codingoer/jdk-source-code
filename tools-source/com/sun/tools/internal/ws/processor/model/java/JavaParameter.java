package com.sun.tools.internal.ws.processor.model.java;

import com.sun.tools.internal.ws.processor.model.Parameter;

public class JavaParameter {
   private String name;
   private JavaType type;
   private Parameter parameter;
   private boolean holder;
   private String holderName;

   public JavaParameter() {
   }

   public JavaParameter(String name, JavaType type, Parameter parameter) {
      this(name, type, parameter, false);
   }

   public JavaParameter(String name, JavaType type, Parameter parameter, boolean holder) {
      this.name = name;
      this.type = type;
      this.parameter = parameter;
      this.holder = holder;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String s) {
      this.name = s;
   }

   public JavaType getType() {
      return this.type;
   }

   public void setType(JavaType t) {
      this.type = t;
   }

   public Parameter getParameter() {
      return this.parameter;
   }

   public void setParameter(Parameter p) {
      this.parameter = p;
   }

   public boolean isHolder() {
      return this.holder;
   }

   public void setHolder(boolean b) {
      this.holder = b;
   }

   public String getHolderName() {
      return this.holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }
}
