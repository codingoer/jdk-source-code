package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Locator;

public abstract class Entity implements Elemental {
   private final Locator locator;
   protected ErrorReceiver errorReceiver;
   private Map _properties;

   public Entity(Locator locator) {
      this.locator = locator;
   }

   public void setErrorReceiver(ErrorReceiver errorReceiver) {
      this.errorReceiver = errorReceiver;
   }

   public Locator getLocator() {
      return this.locator;
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

   public void withAllSubEntitiesDo(EntityAction action) {
   }

   public void withAllQNamesDo(QNameAction action) {
      action.perform(this.getElementName());
   }

   public void withAllEntityReferencesDo(EntityReferenceAction action) {
   }

   public abstract void validateThis();

   protected void failValidation(String key) {
      throw new ValidationException(key, new Object[]{this.getElementName().getLocalPart()});
   }

   protected void failValidation(String key, String arg) {
      throw new ValidationException(key, new Object[]{arg, this.getElementName().getLocalPart()});
   }
}
