package com.sun.tools.internal.ws.processor.model;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.ws.processor.model.java.JavaSimpleType;
import com.sun.tools.internal.ws.processor.model.java.JavaType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.concurrent.Future;
import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;

public class AsyncOperation extends Operation {
   private Operation operation;
   private boolean _async;
   private AsyncOperationType _asyncOpType;
   private AbstractType _responseBean;

   public AsyncOperation(Entity entity) {
      super(entity);
   }

   public AsyncOperation(Operation operation, Entity entity) {
      super(operation, entity);
      this.operation = operation;
   }

   public AsyncOperation(QName name, Entity entity) {
      super(name, entity);
   }

   public boolean isAsync() {
      return this._async;
   }

   public void setAsyncType(AsyncOperationType type) {
      this._asyncOpType = type;
      this._async = true;
   }

   public AsyncOperationType getAsyncType() {
      return this._asyncOpType;
   }

   public void setResponseBean(AbstractType type) {
      this._responseBean = type;
   }

   public AbstractType getResponseBeanType() {
      return this._responseBean;
   }

   public JavaType getResponseBeanJavaType() {
      JCodeModel cm = this._responseBean.getJavaType().getType().getType().owner();
      JClass polling;
      if (this._asyncOpType.equals(AsyncOperationType.CALLBACK)) {
         polling = cm.ref(Future.class).narrow(cm.ref(Object.class).wildcard());
         return new JavaSimpleType(new JAXBTypeAndAnnotation(polling));
      } else if (this._asyncOpType.equals(AsyncOperationType.POLLING)) {
         polling = cm.ref(javax.xml.ws.Response.class).narrow(this._responseBean.getJavaType().getType().getType().boxify());
         return new JavaSimpleType(new JAXBTypeAndAnnotation(polling));
      } else {
         return null;
      }
   }

   public JavaType getCallBackType() {
      if (this._asyncOpType.equals(AsyncOperationType.CALLBACK)) {
         JCodeModel cm = this._responseBean.getJavaType().getType().getType().owner();
         JClass cb = cm.ref(AsyncHandler.class).narrow(this._responseBean.getJavaType().getType().getType().boxify());
         return new JavaSimpleType(new JAXBTypeAndAnnotation(cb));
      } else {
         return null;
      }
   }

   public Operation getNormalOperation() {
      return this.operation;
   }

   public void setNormalOperation(Operation operation) {
      this.operation = operation;
   }

   public String getJavaMethodName() {
      return super.getJavaMethodName() + "Async";
   }
}
