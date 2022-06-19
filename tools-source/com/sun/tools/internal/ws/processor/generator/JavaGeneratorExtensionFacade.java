package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.JMethod;
import com.sun.tools.internal.ws.api.TJavaGeneratorExtension;
import com.sun.tools.internal.ws.api.wsdl.TWSDLOperation;

public final class JavaGeneratorExtensionFacade extends TJavaGeneratorExtension {
   private final TJavaGeneratorExtension[] extensions;

   JavaGeneratorExtensionFacade(TJavaGeneratorExtension... extensions) {
      assert extensions != null;

      this.extensions = extensions;
   }

   public void writeMethodAnnotations(TWSDLOperation wsdlOperation, JMethod jMethod) {
      TJavaGeneratorExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TJavaGeneratorExtension e = var3[var5];
         e.writeMethodAnnotations(wsdlOperation, jMethod);
      }

   }
}
