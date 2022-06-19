package com.sun.tools.internal.ws.processor.modeler.annotation;

public class WrapperInfo {
   public String wrapperName;

   public WrapperInfo() {
   }

   public WrapperInfo(String wrapperName) {
      this.wrapperName = wrapperName;
   }

   public void setWrapperName(String wrapperName) {
      this.wrapperName = wrapperName;
   }

   public String getWrapperName() {
      return this.wrapperName;
   }
}
