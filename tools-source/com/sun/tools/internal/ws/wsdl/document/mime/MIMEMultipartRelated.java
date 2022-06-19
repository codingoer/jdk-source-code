package com.sun.tools.internal.ws.wsdl.document.mime;

import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import com.sun.tools.internal.ws.wsdl.framework.ExtensionVisitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class MIMEMultipartRelated extends ExtensionImpl {
   private List _parts = new ArrayList();

   public MIMEMultipartRelated(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return MIMEConstants.QNAME_MULTIPART_RELATED;
   }

   public void add(MIMEPart part) {
      this._parts.add(part);
   }

   public Iterable getParts() {
      return this._parts;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      super.withAllSubEntitiesDo(action);
      Iterator iter = this._parts.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

   }

   public void accept(ExtensionVisitor visitor) throws Exception {
      visitor.preVisit(this);
      visitor.postVisit(this);
   }

   public void validateThis() {
   }
}
