package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.parser.DOMForest;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public abstract class AbstractDocument {
   protected final DOMForest forest;
   protected final ErrorReceiver errReceiver;
   private final Map kinds;
   private String _systemId;
   private final Set importedDocuments;
   private final List importedEntities;
   private final Set includedDocuments;
   private final List includedEntities;

   protected AbstractDocument(MetadataFinder forest, ErrorReceiver errReceiver) {
      this.forest = forest;
      this.errReceiver = errReceiver;
      this.kinds = new HashMap();
      this.importedEntities = new ArrayList();
      this.importedDocuments = new HashSet();
      this.includedEntities = new ArrayList();
      this.includedDocuments = new HashSet();
   }

   public String getSystemId() {
      return this._systemId;
   }

   public void setSystemId(String s) {
      if (this._systemId != null && !this._systemId.equals(s)) {
         throw new IllegalArgumentException();
      } else {
         this._systemId = s;
         if (s != null) {
            this.importedDocuments.add(s);
         }

      }
   }

   public void addIncludedDocument(String systemId) {
      this.includedDocuments.add(systemId);
   }

   public boolean isIncludedDocument(String systemId) {
      return this.includedDocuments.contains(systemId);
   }

   public void addIncludedEntity(Entity entity) {
      this.includedEntities.add(entity);
   }

   public void addImportedDocument(String systemId) {
      this.importedDocuments.add(systemId);
   }

   public boolean isImportedDocument(String systemId) {
      return this.importedDocuments.contains(systemId);
   }

   public void addImportedEntity(Entity entity) {
      this.importedEntities.add(entity);
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      if (this.getRoot() != null) {
         action.perform(this.getRoot());
      }

      Iterator iter = this.importedEntities.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      iter = this.includedEntities.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

   }

   public Map getMap(Kind k) {
      Map m = (Map)this.kinds.get(k.getName());
      if (m == null) {
         m = new HashMap();
         this.kinds.put(k.getName(), m);
      }

      return (Map)m;
   }

   public void define(GloballyKnown e) {
      Map map = this.getMap(e.getKind());
      if (e.getName() != null) {
         QName name = new QName(e.getDefining().getTargetNamespaceURI(), e.getName());
         if (map.containsKey(name)) {
            this.errReceiver.error(e.getLocator(), WsdlMessages.ENTITY_DUPLICATE_WITH_TYPE(e.getElementName().getLocalPart(), e.getName()));
            throw new AbortException();
         } else {
            map.put(name, e);
         }
      }
   }

   public GloballyKnown find(Kind k, QName name) {
      Map map = this.getMap(k);
      Object result = map.get(name);
      if (result == null) {
         this.errReceiver.error((Locator)null, (String)WsdlMessages.ENTITY_NOT_FOUND_BY_Q_NAME(k.getName(), name, this._systemId));
         throw new AbortException();
      } else {
         return (GloballyKnown)result;
      }
   }

   public void validateLocally() {
      LocallyValidatingAction action = new LocallyValidatingAction();
      this.withAllSubEntitiesDo(action);
      if (action.getException() != null) {
         throw action.getException();
      }
   }

   public abstract void validate(EntityReferenceValidator var1);

   protected abstract Entity getRoot();

   private static class LocallyValidatingAction implements EntityAction {
      private ValidationException _exception;

      public LocallyValidatingAction() {
      }

      public void perform(Entity entity) {
         try {
            entity.validateThis();
            entity.withAllSubEntitiesDo(this);
         } catch (ValidationException var3) {
            if (this._exception == null) {
               this._exception = var3;
            }
         }

      }

      public ValidationException getException() {
         return this._exception;
      }
   }
}
