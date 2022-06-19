package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.wsdl.framework.Entity;
import javax.xml.namespace.QName;

public class HeaderFault extends Fault {
   private QName _message;
   private String _part;

   public HeaderFault(Entity entity) {
      super(entity);
   }

   public HeaderFault(String name, Entity entity) {
      super(name, entity);
   }

   public QName getMessage() {
      return this._message;
   }

   public void setMessage(QName message) {
      this._message = message;
   }

   public String getPart() {
      return this._part;
   }

   public void setPart(String part) {
      this._part = part;
   }
}
