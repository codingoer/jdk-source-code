package com.sun.tools.internal.ws.api;

import com.sun.codemodel.internal.JMethod;
import com.sun.tools.internal.ws.api.wsdl.TWSDLOperation;

/** @deprecated */
public abstract class TJavaGeneratorExtension {
   public abstract void writeMethodAnnotations(TWSDLOperation var1, JMethod var2);
}
