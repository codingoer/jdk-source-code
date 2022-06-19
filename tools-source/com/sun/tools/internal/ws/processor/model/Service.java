package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class Service extends ModelObject {
   private QName name;
   private List ports = new ArrayList();
   private Map portsByName = new HashMap();
   private JavaInterface javaInterface;

   public Service(Entity entity) {
      super(entity);
   }

   public Service(QName name, JavaInterface javaInterface, Entity entity) {
      super(entity);
      this.name = name;
      this.javaInterface = javaInterface;
   }

   public QName getName() {
      return this.name;
   }

   public void setName(QName n) {
      this.name = n;
   }

   public void addPort(Port port) {
      if (this.portsByName.containsKey(port.getName())) {
         throw new ModelException("model.uniqueness", new Object[0]);
      } else {
         this.ports.add(port);
         this.portsByName.put(port.getName(), port);
      }
   }

   public Port getPortByName(QName n) {
      if (this.portsByName.size() != this.ports.size()) {
         this.initializePortsByName();
      }

      return (Port)this.portsByName.get(n);
   }

   public List getPorts() {
      return this.ports;
   }

   public void setPorts(List m) {
      this.ports = m;
   }

   private void initializePortsByName() {
      this.portsByName = new HashMap();
      if (this.ports != null) {
         Iterator iter = this.ports.iterator();

         while(iter.hasNext()) {
            Port port = (Port)iter.next();
            if (port.getName() != null && this.portsByName.containsKey(port.getName())) {
               throw new ModelException("model.uniqueness", new Object[0]);
            }

            this.portsByName.put(port.getName(), port);
         }
      }

   }

   public JavaInterface getJavaIntf() {
      return this.getJavaInterface();
   }

   public JavaInterface getJavaInterface() {
      return this.javaInterface;
   }

   public void setJavaInterface(JavaInterface i) {
      this.javaInterface = i;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }
}
