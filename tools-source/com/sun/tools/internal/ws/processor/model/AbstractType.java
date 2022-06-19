package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.processor.model.java.JavaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract class AbstractType {
   private QName name;
   private JavaType javaType;
   private String version;
   private Map properties;

   protected AbstractType() {
      this.version = null;
   }

   protected AbstractType(QName name) {
      this(name, (JavaType)null, (String)null);
   }

   protected AbstractType(QName name, String version) {
      this(name, (JavaType)null, version);
   }

   protected AbstractType(QName name, JavaType javaType) {
      this(name, javaType, (String)null);
   }

   protected AbstractType(QName name, JavaType javaType, String version) {
      this.version = null;
      this.name = name;
      this.javaType = javaType;
      this.version = version;
   }

   public QName getName() {
      return this.name;
   }

   public void setName(QName name) {
      this.name = name;
   }

   public JavaType getJavaType() {
      return this.javaType;
   }

   public void setJavaType(JavaType javaType) {
      this.javaType = javaType;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public boolean isNillable() {
      return false;
   }

   public boolean isSOAPType() {
      return false;
   }

   public boolean isLiteralType() {
      return false;
   }

   public Object getProperty(String key) {
      return this.properties == null ? null : this.properties.get(key);
   }

   public void setProperty(String key, Object value) {
      if (value == null) {
         this.removeProperty(key);
      } else {
         if (this.properties == null) {
            this.properties = new HashMap();
         }

         this.properties.put(key, value);
      }
   }

   public void removeProperty(String key) {
      if (this.properties != null) {
         this.properties.remove(key);
      }

   }

   public Iterator getProperties() {
      return this.properties == null ? Collections.emptyList().iterator() : this.properties.keySet().iterator();
   }

   public Map getPropertiesMap() {
      return this.properties;
   }

   public void setPropertiesMap(Map m) {
      this.properties = m;
   }
}
