package com.sun.tools.internal.ws.wsdl.document.jaxws;

public class Exception {
   private CustomName className;

   public Exception() {
   }

   public Exception(CustomName name) {
      this.className = name;
   }

   public CustomName getClassName() {
      return this.className;
   }

   public void setClassName(CustomName className) {
      this.className = className;
   }
}
