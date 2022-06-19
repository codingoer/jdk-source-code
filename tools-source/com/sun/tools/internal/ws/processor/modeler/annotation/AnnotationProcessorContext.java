package com.sun.tools.internal.ws.processor.modeler.annotation;

import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Operation;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.model.Service;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class AnnotationProcessorContext {
   private Map seiContextMap = new HashMap();
   private int round = 1;
   private boolean modelCompleted = false;

   public void addSeiContext(Name seiName, SeiContext seiContext) {
      this.seiContextMap.put(seiName, seiContext);
   }

   public SeiContext getSeiContext(Name seiName) {
      SeiContext context = (SeiContext)this.seiContextMap.get(seiName);
      if (context == null) {
         context = new SeiContext();
         this.addSeiContext(seiName, context);
      }

      return context;
   }

   public SeiContext getSeiContext(TypeElement d) {
      return this.getSeiContext(d.getQualifiedName());
   }

   public Collection getSeiContexts() {
      return this.seiContextMap.values();
   }

   public int getRound() {
      return this.round;
   }

   public void incrementRound() {
      ++this.round;
   }

   public static boolean isEncoded(Model model) {
      if (model == null) {
         return false;
      } else {
         Iterator var1 = model.getServices().iterator();

         while(var1.hasNext()) {
            Service service = (Service)var1.next();
            Iterator var3 = service.getPorts().iterator();

            while(var3.hasNext()) {
               Port port = (Port)var3.next();
               Iterator var5 = port.getOperations().iterator();

               while(var5.hasNext()) {
                  Operation operation = (Operation)var5.next();
                  if (operation.getUse() != null && operation.getUse().equals(SOAPUse.LITERAL)) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   public void setModelCompleted(boolean modelCompleted) {
      this.modelCompleted = modelCompleted;
   }

   public boolean isModelCompleted() {
      return this.modelCompleted;
   }

   public static class SeiContext {
      private Map reqOperationWrapperMap = new HashMap();
      private Map resOperationWrapperMap = new HashMap();
      private Map exceptionBeanMap = new HashMap();
      private Name seiImplName;
      private boolean implementsSei;
      private String namespaceUri;

      public SeiContext() {
      }

      /** @deprecated */
      public SeiContext(Name seiName) {
      }

      public void setImplementsSei(boolean implementsSei) {
         this.implementsSei = implementsSei;
      }

      public boolean getImplementsSei() {
         return this.implementsSei;
      }

      public void setNamespaceUri(String namespaceUri) {
         this.namespaceUri = namespaceUri;
      }

      public String getNamespaceUri() {
         return this.namespaceUri;
      }

      public Name getSeiImplName() {
         return this.seiImplName;
      }

      public void setSeiImplName(Name implName) {
         this.seiImplName = implName;
      }

      public void setReqWrapperOperation(ExecutableElement method, WrapperInfo wrapperInfo) {
         this.reqOperationWrapperMap.put(this.methodToString(method), wrapperInfo);
      }

      public WrapperInfo getReqOperationWrapper(ExecutableElement method) {
         return (WrapperInfo)this.reqOperationWrapperMap.get(this.methodToString(method));
      }

      public void setResWrapperOperation(ExecutableElement method, WrapperInfo wrapperInfo) {
         this.resOperationWrapperMap.put(this.methodToString(method), wrapperInfo);
      }

      public WrapperInfo getResOperationWrapper(ExecutableElement method) {
         return (WrapperInfo)this.resOperationWrapperMap.get(this.methodToString(method));
      }

      public String methodToString(ExecutableElement method) {
         StringBuilder buf = new StringBuilder(method.getSimpleName());
         Iterator var3 = method.getParameters().iterator();

         while(var3.hasNext()) {
            VariableElement param = (VariableElement)var3.next();
            buf.append(';').append(param.asType());
         }

         return buf.toString();
      }

      public void clearExceptionMap() {
         this.exceptionBeanMap.clear();
      }

      public void addExceptionBeanEntry(Name exception, FaultInfo faultInfo, ModelBuilder builder) {
         this.exceptionBeanMap.put(exception, faultInfo);
      }

      public FaultInfo getExceptionBeanName(Name exception) {
         return (FaultInfo)this.exceptionBeanMap.get(exception);
      }
   }
}
