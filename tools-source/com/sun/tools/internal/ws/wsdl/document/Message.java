package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.Defining;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.GlobalEntity;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Message extends GlobalEntity {
   private Documentation _documentation;
   private List _parts = new ArrayList();
   private Map _partsByName = new HashMap();

   public Message(Defining defining, Locator locator, ErrorReceiver errReceiver) {
      super(defining, locator, errReceiver);
   }

   public void add(MessagePart part) {
      if (this._partsByName.get(part.getName()) != null) {
         this.errorReceiver.error(part.getLocator(), WsdlMessages.VALIDATION_DUPLICATE_PART_NAME(this.getName(), part.getName()));
         throw new AbortException();
      } else {
         if (part.getDescriptor() != null && part.getDescriptorKind() != null) {
            this._partsByName.put(part.getName(), part);
            this._parts.add(part);
         } else {
            this.errorReceiver.warning(part.getLocator(), WsdlMessages.PARSING_ELEMENT_OR_TYPE_REQUIRED(part.getName()));
         }

      }
   }

   public Iterator parts() {
      return this._parts.iterator();
   }

   public List getParts() {
      return this._parts;
   }

   public MessagePart getPart(String name) {
      return (MessagePart)this._partsByName.get(name);
   }

   public int numParts() {
      return this._parts.size();
   }

   public Kind getKind() {
      return Kinds.MESSAGE;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_MESSAGE;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      super.withAllSubEntitiesDo(action);
      Iterator iter = this._parts.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      Iterator iter = this._parts.iterator();

      while(iter.hasNext()) {
         ((MessagePart)iter.next()).accept(visitor);
      }

      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this.getName() == null) {
         this.errorReceiver.error(this.getLocator(), WsdlMessages.VALIDATION_MISSING_REQUIRED_ATTRIBUTE("name", "wsdl:message"));
         throw new AbortException();
      }
   }
}
