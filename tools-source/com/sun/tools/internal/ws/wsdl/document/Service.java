package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.Defining;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.GlobalEntity;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Service extends GlobalEntity implements TWSDLExtensible {
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;
   private List _ports = new ArrayList();

   public Service(Defining defining, Locator locator, ErrorReceiver errReceiver) {
      super(defining, locator, errReceiver);
   }

   public void add(Port port) {
      port.setService(this);
      this._ports.add(port);
   }

   public Iterator ports() {
      return this._ports.iterator();
   }

   public Kind getKind() {
      return Kinds.SERVICE;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_SERVICE;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      Iterator iter = this._ports.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      Iterator iter = this._ports.iterator();

      while(iter.hasNext()) {
         ((Port)iter.next()).accept(visitor);
      }

      this._helper.accept(visitor);
      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this.getName() == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

   }

   public String getNameValue() {
      return this.getName();
   }

   public String getNamespaceURI() {
      return this.getDefining().getTargetNamespaceURI();
   }

   public QName getWSDLElementName() {
      return this.getElementName();
   }

   public void addExtension(TWSDLExtension e) {
      this._helper.addExtension(e);
   }

   public Iterable extensions() {
      return this._helper.extensions();
   }

   public TWSDLExtensible getParent() {
      return null;
   }
}
