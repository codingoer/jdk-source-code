package com.sun.tools.internal.ws.processor.model.java;

import com.sun.tools.internal.ws.processor.model.Parameter;
import com.sun.tools.internal.ws.resources.ModelMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JavaMethod {
   private final ErrorReceiver errorReceiver;
   private final String name;
   private final List parameters = new ArrayList();
   private final List exceptions = new ArrayList();
   private final WsimportOptions options;
   private JavaType returnType;

   public JavaMethod(String name, WsimportOptions options, ErrorReceiver receiver) {
      this.name = name;
      this.returnType = null;
      this.errorReceiver = receiver;
      this.options = options;
   }

   public String getName() {
      return this.name;
   }

   public JavaType getReturnType() {
      return this.returnType;
   }

   public void setReturnType(JavaType returnType) {
      this.returnType = returnType;
   }

   private boolean hasParameter(String paramName) {
      Iterator var2 = this.parameters.iterator();

      JavaParameter parameter;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         parameter = (JavaParameter)var2.next();
      } while(!paramName.equals(parameter.getName()));

      return true;
   }

   private Parameter getParameter(String paramName) {
      Iterator var2 = this.parameters.iterator();

      JavaParameter parameter;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         parameter = (JavaParameter)var2.next();
      } while(!paramName.equals(parameter.getName()));

      return parameter.getParameter();
   }

   public void addParameter(JavaParameter param) {
      if (this.hasParameter(param.getName())) {
         if (!this.options.isExtensionMode()) {
            Parameter duplicParam = this.getParameter(param.getName());
            if (param.getParameter().isEmbedded()) {
               this.errorReceiver.error(param.getParameter().getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE_WRAPPER(param.getName(), param.getParameter().getEntityName()));
               this.errorReceiver.error(duplicParam.getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE_WRAPPER(param.getName(), duplicParam.getEntityName()));
            } else {
               this.errorReceiver.error(param.getParameter().getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE(param.getName(), param.getParameter().getEntityName()));
               this.errorReceiver.error(duplicParam.getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE(param.getName(), duplicParam.getEntityName()));
            }

            return;
         }

         param.setName(this.getUniqueName(param.getName()));
      }

      this.parameters.add(param);
   }

   public List getParametersList() {
      return this.parameters;
   }

   public void addException(String exception) {
      if (!this.exceptions.contains(exception)) {
         this.exceptions.add(exception);
      }

   }

   public Iterator getExceptions() {
      return this.exceptions.iterator();
   }

   private String getUniqueName(String param) {
      for(int parmNum = 0; this.hasParameter(param); param = param + Integer.toString(parmNum++)) {
      }

      return param;
   }
}
