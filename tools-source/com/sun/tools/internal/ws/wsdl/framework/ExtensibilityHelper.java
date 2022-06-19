package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtensibilityHelper {
   private List _extensions;

   public void addExtension(TWSDLExtension e) {
      if (this._extensions == null) {
         this._extensions = new ArrayList();
      }

      this._extensions.add(e);
   }

   public Iterable extensions() {
      return (Iterable)(this._extensions == null ? new ArrayList() : this._extensions);
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      if (this._extensions != null) {
         Iterator iter = this._extensions.iterator();

         while(iter.hasNext()) {
            action.perform((Entity)iter.next());
         }
      }

   }

   public void accept(ExtensionVisitor visitor) throws Exception {
      if (this._extensions != null) {
         Iterator iter = this._extensions.iterator();

         while(iter.hasNext()) {
            ((ExtensionImpl)iter.next()).accept(visitor);
         }
      }

   }
}
