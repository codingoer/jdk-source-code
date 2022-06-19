package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.processor.model.jaxb.JAXBModel;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public class Model extends ModelObject {
   private QName name;
   private String targetNamespace;
   private List services = new ArrayList();
   private Map servicesByName = new HashMap();
   private Set extraTypes = new HashSet();
   private String source;
   private JAXBModel jaxBModel = null;

   public Model(Entity entity) {
      super(entity);
   }

   public Model(QName name, Entity entity) {
      super(entity);
      this.name = name;
   }

   public QName getName() {
      return this.name;
   }

   public void setName(QName n) {
      this.name = n;
   }

   public String getTargetNamespaceURI() {
      return this.targetNamespace;
   }

   public void setTargetNamespaceURI(String s) {
      this.targetNamespace = s;
   }

   public void addService(Service service) {
      if (this.servicesByName.containsKey(service.getName())) {
         throw new ModelException("model.uniqueness", new Object[0]);
      } else {
         this.services.add(service);
         this.servicesByName.put(service.getName(), service);
      }
   }

   public Service getServiceByName(QName name) {
      if (this.servicesByName.size() != this.services.size()) {
         this.initializeServicesByName();
      }

      return (Service)this.servicesByName.get(name);
   }

   public List getServices() {
      return this.services;
   }

   public void setServices(List l) {
      this.services = l;
   }

   private void initializeServicesByName() {
      this.servicesByName = new HashMap();
      if (this.services != null) {
         Iterator var1 = this.services.iterator();

         while(var1.hasNext()) {
            Service service = (Service)var1.next();
            if (service.getName() != null && this.servicesByName.containsKey(service.getName())) {
               throw new ModelException("model.uniqueness", new Object[0]);
            }

            this.servicesByName.put(service.getName(), service);
         }
      }

   }

   public void addExtraType(AbstractType type) {
      this.extraTypes.add(type);
   }

   public Iterator getExtraTypes() {
      return this.extraTypes.iterator();
   }

   public Set getExtraTypesSet() {
      return this.extraTypes;
   }

   public void setExtraTypesSet(Set s) {
      this.extraTypes = s;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public String getSource() {
      return this.source;
   }

   public void setSource(String string) {
      this.source = string;
   }

   public void setJAXBModel(JAXBModel jaxBModel) {
      this.jaxBModel = jaxBModel;
   }

   public JAXBModel getJAXBModel() {
      return this.jaxBModel;
   }
}
