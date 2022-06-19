package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.xml.internal.ws.api.SOAPVersion;

public abstract class GeneratorExtension {
   public String getBindingValue(String transport, SOAPVersion soapVersion) {
      return null;
   }

   public void writeWebServiceAnnotation(Model model, JCodeModel cm, JDefinedClass cls, Port port) {
   }

   public boolean validateOption(String name) {
      return false;
   }

   public void writeWebServiceClientAnnotation(WsimportOptions options, JCodeModel cm, JDefinedClass cls) {
   }
}
