package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.AbstractDocument;
import com.sun.tools.internal.ws.wsdl.framework.Defining;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.GlobalEntity;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import com.sun.tools.internal.ws.wsdl.framework.QNameAction;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Port extends GlobalEntity implements TWSDLExtensible {
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;
   private Service _service;
   private QName _binding;
   private TWSDLExtensible parent;

   public Port(Defining defining, Locator locator, ErrorReceiver errReceiver) {
      super(defining, locator, errReceiver);
   }

   public Service getService() {
      return this._service;
   }

   public void setService(Service s) {
      this._service = s;
   }

   public QName getBinding() {
      return this._binding;
   }

   public void setBinding(QName n) {
      this._binding = n;
   }

   public Binding resolveBinding(AbstractDocument document) {
      try {
         return (Binding)document.find(Kinds.BINDING, this._binding);
      } catch (NoSuchEntityException var3) {
         this.errorReceiver.error(this.getLocator(), WsdlMessages.ENTITY_NOT_FOUND_BINDING(this._binding, new QName(this.getNamespaceURI(), this.getName())));
         throw new AbortException();
      }
   }

   public Kind getKind() {
      return Kinds.PORT;
   }

   public String getNameValue() {
      return this.getName();
   }

   public String getNamespaceURI() {
      return this.getDefining().getTargetNamespaceURI();
   }

   public QName getWSDLElementName() {
      return WSDLConstants.QNAME_PORT;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void withAllQNamesDo(QNameAction action) {
      super.withAllQNamesDo(action);
      if (this._binding != null) {
         action.perform(this._binding);
      }

   }

   public void withAllEntityReferencesDo(EntityReferenceAction action) {
      super.withAllEntityReferencesDo(action);
      if (this._binding != null) {
         action.perform(Kinds.BINDING, this._binding);
      }

   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this.getName() == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

      if (this._binding == null) {
         this.failValidation("validation.missingRequiredAttribute", "binding");
      }

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

   public QName getElementName() {
      return this.getWSDLElementName();
   }
}
