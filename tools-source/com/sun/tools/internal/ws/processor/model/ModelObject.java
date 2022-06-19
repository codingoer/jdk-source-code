package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.Locator;

public abstract class ModelObject {
   private final Entity entity;
   protected ErrorReceiver errorReceiver;
   private String javaDoc;
   private Map _properties;

   public abstract void accept(ModelVisitor var1) throws Exception;

   protected ModelObject(Entity entity) {
      this.entity = entity;
   }

   public void setErrorReceiver(ErrorReceiver errorReceiver) {
      this.errorReceiver = errorReceiver;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Object getProperty(String key) {
      return this._properties == null ? null : this._properties.get(key);
   }

   public void setProperty(String key, Object value) {
      if (value == null) {
         this.removeProperty(key);
      } else {
         if (this._properties == null) {
            this._properties = new HashMap();
         }

         this._properties.put(key, value);
      }
   }

   public void removeProperty(String key) {
      if (this._properties != null) {
         this._properties.remove(key);
      }

   }

   public Iterator getProperties() {
      return this._properties == null ? Collections.emptyList().iterator() : this._properties.keySet().iterator();
   }

   public Locator getLocator() {
      return this.entity.getLocator();
   }

   public Map getPropertiesMap() {
      return this._properties;
   }

   public void setPropertiesMap(Map m) {
      this._properties = m;
   }

   public String getJavaDoc() {
      return this.javaDoc;
   }

   public void setJavaDoc(String javaDoc) {
      this.javaDoc = javaDoc;
   }
}
