package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class ValidationException extends JAXWSExceptionBase {
   public ValidationException(String key, Object... args) {
      super(key, args);
   }

   public ValidationException(Throwable throwable) {
      super(throwable);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.tools.internal.ws.resources.wsdl";
   }
}
