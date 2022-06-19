package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.AbstractDocument;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.QNameAction;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Fault extends Entity implements TWSDLExtensible {
   private Documentation _documentation;
   private String _name;
   private QName _message;
   private String _action;
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private TWSDLExtensible parent;

   public Fault(Locator locator) {
      super(locator);
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public QName getMessage() {
      return this._message;
   }

   public void setMessage(QName n) {
      this._message = n;
   }

   public Message resolveMessage(AbstractDocument document) {
      return (Message)document.find(Kinds.MESSAGE, this._message);
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

   public void withAllQNamesDo(QNameAction action) {
      if (this._message != null) {
         action.perform(this._message);
      }

   }

   public void withAllEntityReferencesDo(EntityReferenceAction action) {
      super.withAllEntityReferencesDo(action);
      if (this._message != null) {
         action.perform(Kinds.MESSAGE, this._message);
      }

   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this._name == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

      if (this._message == null) {
         this.failValidation("validation.missingRequiredAttribute", "message");
      }

   }

   public String getNameValue() {
      return this.getName();
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

   public String getAction() {
      return this._action;
   }

   public void setAction(String _action) {
      this._action = _action;
   }
}
