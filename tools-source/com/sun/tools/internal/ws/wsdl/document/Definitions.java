package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.AbstractDocument;
import com.sun.tools.internal.ws.wsdl.framework.Defining;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Definitions extends Entity implements Defining, TWSDLExtensible {
   private AbstractDocument _document;
   private ExtensibilityHelper _helper;
   private Documentation _documentation;
   private String _name;
   private String _targetNsURI;
   private Types _types;
   private List _messages;
   private List _portTypes;
   private List _bindings;
   private List _services;
   private List _imports;
   private Set _importedNamespaces;

   public Definitions(AbstractDocument document, Locator locator) {
      super(locator);
      this._document = document;
      this._bindings = new ArrayList();
      this._imports = new ArrayList();
      this._messages = new ArrayList();
      this._portTypes = new ArrayList();
      this._services = new ArrayList();
      this._importedNamespaces = new HashSet();
      this._helper = new ExtensibilityHelper();
   }

   public String getName() {
      return this._name;
   }

   public void setName(String s) {
      this._name = s;
   }

   public String getTargetNamespaceURI() {
      return this._targetNsURI;
   }

   public void setTargetNamespaceURI(String s) {
      this._targetNsURI = s;
   }

   public void setTypes(Types t) {
      this._types = t;
   }

   public Types getTypes() {
      return this._types;
   }

   public void add(Message m) {
      this._document.define(m);
      this._messages.add(m);
   }

   public void add(PortType p) {
      this._document.define(p);
      this._portTypes.add(p);
   }

   public void add(Binding b) {
      this._document.define(b);
      this._bindings.add(b);
   }

   public void add(Service s) {
      this._document.define(s);
      this._services.add(s);
   }

   public void addServiceOveride(Service s) {
      this._services.add(s);
   }

   public void add(Import i) {
      this._imports.add(i);
      this._importedNamespaces.add(i.getNamespace());
   }

   public Iterator imports() {
      return this._imports.iterator();
   }

   public Iterator messages() {
      return this._messages.iterator();
   }

   public Iterator portTypes() {
      return this._portTypes.iterator();
   }

   public Iterator bindings() {
      return this._bindings.iterator();
   }

   public Iterator services() {
      return this._services.iterator();
   }

   public String getNameValue() {
      return this.getName();
   }

   public String getNamespaceURI() {
      return this.getTargetNamespaceURI();
   }

   public QName getWSDLElementName() {
      return WSDLConstants.QNAME_DEFINITIONS;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
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

   public void withAllSubEntitiesDo(EntityAction action) {
      if (this._types != null) {
         action.perform(this._types);
      }

      Iterator iter = this._messages.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      iter = this._portTypes.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      iter = this._bindings.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      iter = this._services.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      iter = this._imports.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      Iterator iter = this._imports.iterator();

      while(iter.hasNext()) {
         ((Import)iter.next()).accept(visitor);
      }

      if (this._types != null) {
         this._types.accept(visitor);
      }

      iter = this._messages.iterator();

      while(iter.hasNext()) {
         ((Message)iter.next()).accept(visitor);
      }

      iter = this._portTypes.iterator();

      while(iter.hasNext()) {
         ((PortType)iter.next()).accept(visitor);
      }

      iter = this._bindings.iterator();

      while(iter.hasNext()) {
         ((Binding)iter.next()).accept(visitor);
      }

      iter = this._services.iterator();

      while(iter.hasNext()) {
         ((Service)iter.next()).accept(visitor);
      }

      this._helper.accept(visitor);
      visitor.postVisit(this);
   }

   public void validateThis() {
   }

   public Map resolveBindings() {
      return this._document.getMap(Kinds.BINDING);
   }

   public QName getElementName() {
      return this.getWSDLElementName();
   }
}
