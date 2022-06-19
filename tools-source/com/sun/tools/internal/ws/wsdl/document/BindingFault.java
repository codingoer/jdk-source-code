package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class BindingFault extends Entity implements TWSDLExtensible {
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;
   private String _name;
   private TWSDLExtensible parent;

   public BindingFault(Locator locator) {
      super(locator);
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_FAULT;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public String getNameValue() {
      return this.getName();
   }

   public String getNamespaceURI() {
      return this.getParent().getNamespaceURI();
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

   public void withAllSubEntitiesDo(EntityAction action) {
      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this._name == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

   }

   public void setParent(TWSDLExtensible parent) {
      this.parent = parent;
   }
}
