package com.sun.tools.internal.ws.processor;

import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class ProcessorException extends JAXWSExceptionBase {
   public ProcessorException(String key, Object... args) {
      super(key, args);
   }

   public ProcessorException(String msg) {
      super(msg);
   }

   public ProcessorException(Throwable throwable) {
      super(throwable);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.tools.internal.ws.resources.processor";
   }
}
