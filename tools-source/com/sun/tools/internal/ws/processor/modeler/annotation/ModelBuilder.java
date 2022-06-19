package com.sun.tools.internal.ws.processor.modeler.annotation;

import com.sun.tools.internal.ws.processor.modeler.ModelerException;
import com.sun.tools.internal.ws.wscompile.WsgenOptions;
import java.io.File;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public interface ModelBuilder {
   ProcessingEnvironment getProcessingEnvironment();

   String getOperationName(Name var1);

   TypeMirror getHolderValueType(TypeMirror var1);

   boolean checkAndSetProcessed(TypeElement var1);

   boolean isServiceException(TypeMirror var1);

   boolean isRemote(TypeElement var1);

   boolean canOverWriteClass(String var1);

   WsgenOptions getOptions();

   File getSourceDir();

   void log(String var1);

   void processWarning(String var1);

   void processError(String var1);

   void processError(String var1, Element var2) throws ModelerException;
}
