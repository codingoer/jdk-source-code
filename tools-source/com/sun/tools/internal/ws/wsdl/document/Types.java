package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.ExtensionVisitor;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Types extends Entity implements TWSDLExtensible {
   private TWSDLExtensible parent;
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;

   public Types(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_TYPES;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      visitor.postVisit(this);
   }

   public void validateThis() {
   }

   public String getNameValue() {
      return null;
   }

   public String getNamespaceURI() {
      return this.parent == null ? null : this.parent.getNamespaceURI();
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
      return this.parent;
   }

   public void setParent(TWSDLExtensible parent) {
      this.parent = parent;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(ExtensionVisitor visitor) throws Exception {
      this._helper.accept(visitor);
   }
}
