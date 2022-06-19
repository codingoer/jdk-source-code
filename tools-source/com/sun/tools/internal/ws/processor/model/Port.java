package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.Provider;

public class Port extends ModelObject {
   private SOAPStyle _style = null;
   private boolean _isWrapped = true;
   private String portGetter;
   private QName _name;
   private List _operations = new ArrayList();
   private JavaInterface _javaInterface;
   private String _address;
   private String _serviceImplName;
   private Map operationsByName = new HashMap();
   public Map portTypes = new HashMap();

   public Port(Entity entity) {
      super(entity);
   }

   public Port(QName name, Entity entity) {
      super(entity);
      this._name = name;
   }

   public QName getName() {
      return this._name;
   }

   public void setName(QName n) {
      this._name = n;
   }

   public void addOperation(Operation operation) {
      this._operations.add(operation);
      this.operationsByName.put(operation.getUniqueName(), operation);
   }

   public Operation getOperationByUniqueName(String name) {
      if (this.operationsByName.size() != this._operations.size()) {
         this.initializeOperationsByName();
      }

      return (Operation)this.operationsByName.get(name);
   }

   private void initializeOperationsByName() {
      this.operationsByName = new HashMap();
      if (this._operations != null) {
         Iterator var1 = this._operations.iterator();

         while(var1.hasNext()) {
            Operation operation = (Operation)var1.next();
            if (operation.getUniqueName() != null && this.operationsByName.containsKey(operation.getUniqueName())) {
               throw new ModelException("model.uniqueness", new Object[0]);
            }

            this.operationsByName.put(operation.getUniqueName(), operation);
         }
      }

   }

   public List getOperations() {
      return this._operations;
   }

   public void setOperations(List l) {
      this._operations = l;
   }

   public JavaInterface getJavaInterface() {
      return this._javaInterface;
   }

   public void setJavaInterface(JavaInterface i) {
      this._javaInterface = i;
   }

   public String getAddress() {
      return this._address;
   }

   public void setAddress(String s) {
      this._address = s;
   }

   public String getServiceImplName() {
      return this._serviceImplName;
   }

   public void setServiceImplName(String name) {
      this._serviceImplName = name;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public boolean isProvider() {
      JavaInterface intf = this.getJavaInterface();
      if (intf != null) {
         String sei = intf.getName();
         if (sei.equals(Provider.class.getName())) {
            return true;
         }
      }

      return false;
   }

   public String getPortGetter() {
      return this.portGetter;
   }

   public void setPortGetter(String portGetterName) {
      this.portGetter = portGetterName;
   }

   public SOAPStyle getStyle() {
      return this._style;
   }

   public void setStyle(SOAPStyle s) {
      this._style = s;
   }

   public boolean isWrapped() {
      return this._isWrapped;
   }

   public void setWrapped(boolean isWrapped) {
      this._isWrapped = isWrapped;
   }
}
