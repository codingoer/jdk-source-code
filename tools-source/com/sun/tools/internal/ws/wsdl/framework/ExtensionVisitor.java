package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;

public interface ExtensionVisitor {
   void preVisit(TWSDLExtension var1) throws Exception;

   void postVisit(TWSDLExtension var1) throws Exception;
}
