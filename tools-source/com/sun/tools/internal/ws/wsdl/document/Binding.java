package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.AbstractDocument;
import com.sun.tools.internal.ws.wsdl.framework.Defining;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.GlobalEntity;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import com.sun.tools.internal.ws.wsdl.framework.QNameAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Binding extends GlobalEntity implements TWSDLExtensible {
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;
   private QName _portType;
   private List _operations = new ArrayList();
   private TWSDLExtensible parent;

   public Binding(Defining defining, Locator locator, ErrorReceiver receiver) {
      super(defining, locator, receiver);
   }

   public void add(BindingOperation operation) {
      this._operations.add(operation);
   }

   public Iterator operations() {
      return this._operations.iterator();
   }

   public QName getPortType() {
      return this._portType;
   }

   public void setPortType(QName n) {
      this._portType = n;
   }

   public PortType resolvePortType(AbstractDocument document) {
      try {
         return (PortType)document.find(Kinds.PORT_TYPE, this._portType);
      } catch (NoSuchEntityException var3) {
         this.errorReceiver.error(this.getLocator(), WsdlMessages.ENTITY_NOT_FOUND_PORT_TYPE(this._portType, new QName(this.getNamespaceURI(), this.getName())));
         throw new AbortException();
      }
   }

   public Kind getKind() {
      return Kinds.BINDING;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_BINDING;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      Iterator iter = this._operations.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      this._helper.withAllSubEntitiesDo(action);
   }

   public void withAllQNamesDo(QNameAction action) {
      super.withAllQNamesDo(action);
      if (this._portType != null) {
         action.perform(this._portType);
      }

   }

   public void withAllEntityReferencesDo(EntityReferenceAction action) {
      super.withAllEntityReferencesDo(action);
      if (this._portType != null) {
         action.perform(Kinds.PORT_TYPE, this._portType);
      }

   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      Iterator iter = this._operations.iterator();

      while(iter.hasNext()) {
         ((BindingOperation)iter.next()).accept(visitor);
      }

      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this.getName() == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

      if (this._portType == null) {
         this.failValidation("validation.missingRequiredAttribute", "type");
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
      return this.parent;
   }

   public void setParent(TWSDLExtensible parent) {
      this.parent = parent;
   }
}
