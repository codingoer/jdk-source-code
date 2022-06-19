package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.framework.AbstractDocument;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceAction;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceValidator;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import com.sun.tools.internal.ws.wsdl.framework.ValidationException;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class WSDLDocument extends AbstractDocument {
   private Definitions _definitions;

   public WSDLDocument(MetadataFinder forest, ErrorReceiver errReceiver) {
      super(forest, errReceiver);
   }

   public Definitions getDefinitions() {
      return this._definitions;
   }

   public void setDefinitions(Definitions d) {
      this._definitions = d;
   }

   public QName[] getAllServiceQNames() {
      ArrayList serviceQNames = new ArrayList();
      Iterator iter = this.getDefinitions().services();

      while(iter.hasNext()) {
         Service next = (Service)iter.next();
         String targetNamespace = next.getDefining().getTargetNamespaceURI();
         String localName = next.getName();
         QName serviceQName = new QName(targetNamespace, localName);
         serviceQNames.add(serviceQName);
      }

      return (QName[])((QName[])serviceQNames.toArray(new QName[serviceQNames.size()]));
   }

   public QName[] getAllPortQNames() {
      ArrayList portQNames = new ArrayList();
      Iterator iter = this.getDefinitions().services();

      while(iter.hasNext()) {
         Service next = (Service)iter.next();
         Iterator piter = next.ports();

         while(piter.hasNext()) {
            Port pnext = (Port)piter.next();
            String targetNamespace = pnext.getDefining().getTargetNamespaceURI();
            String localName = pnext.getName();
            QName portQName = new QName(targetNamespace, localName);
            portQNames.add(portQName);
         }
      }

      return (QName[])((QName[])portQNames.toArray(new QName[portQNames.size()]));
   }

   public QName[] getPortQNames(String serviceNameLocalPart) {
      ArrayList portQNames = new ArrayList();
      Iterator iter = this.getDefinitions().services();

      while(true) {
         Service next;
         do {
            if (!iter.hasNext()) {
               return (QName[])((QName[])portQNames.toArray(new QName[portQNames.size()]));
            }

            next = (Service)iter.next();
         } while(!next.getName().equals(serviceNameLocalPart));

         Iterator piter = next.ports();

         while(piter.hasNext()) {
            Port pnext = (Port)piter.next();
            String targetNamespace = pnext.getDefining().getTargetNamespaceURI();
            String localName = pnext.getName();
            QName portQName = new QName(targetNamespace, localName);
            portQNames.add(portQName);
         }
      }
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      this._definitions.accept(visitor);
   }

   public void validate(EntityReferenceValidator validator) {
      GloballyValidatingAction action = new GloballyValidatingAction(this, validator);
      this.withAllSubEntitiesDo(action);
      if (action.getException() != null) {
         throw action.getException();
      }
   }

   protected Entity getRoot() {
      return this._definitions;
   }

   private static class GloballyValidatingAction implements EntityAction, EntityReferenceAction {
      private ValidationException _exception;
      private AbstractDocument _document;
      private EntityReferenceValidator _validator;

      public GloballyValidatingAction(AbstractDocument document, EntityReferenceValidator validator) {
         this._document = document;
         this._validator = validator;
      }

      public void perform(Entity entity) {
         try {
            entity.validateThis();
            entity.withAllEntityReferencesDo(this);
            entity.withAllSubEntitiesDo(this);
         } catch (ValidationException var3) {
            if (this._exception == null) {
               this._exception = var3;
            }
         }

      }

      public void perform(Kind kind, QName name) {
         try {
            this._document.find(kind, name);
         } catch (NoSuchEntityException var4) {
            if (this._exception == null && (this._validator == null || !this._validator.isValid(kind, name))) {
               this._exception = var4;
            }
         }

      }

      public ValidationException getException() {
         return this._exception;
      }
   }
}
