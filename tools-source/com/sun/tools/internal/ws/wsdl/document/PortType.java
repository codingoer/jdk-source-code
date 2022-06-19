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
import com.sun.tools.internal.ws.wsdl.framework.ValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class PortType extends GlobalEntity implements TWSDLExtensible {
   private TWSDLExtensible parent;
   private Documentation _documentation;
   private List _operations = new ArrayList();
   private Set _operationKeys = new HashSet();
   private ExtensibilityHelper _helper = new ExtensibilityHelper();

   public PortType(Defining defining, Locator locator, ErrorReceiver errReceiver) {
      super(defining, locator, errReceiver);
   }

   public void add(Operation operation) {
      String key = operation.getUniqueKey();
      if (this._operationKeys.contains(key)) {
         throw new ValidationException("validation.ambiguousName", new Object[]{operation.getName()});
      } else {
         this._operationKeys.add(key);
         this._operations.add(operation);
      }
   }

   public Iterator operations() {
      return this._operations.iterator();
   }

   public Set getOperationsNamed(String s) {
      Set result = new HashSet();
      Iterator iter = this._operations.iterator();

      while(iter.hasNext()) {
         Operation operation = (Operation)iter.next();
         if (operation.getName().equals(s)) {
            result.add(operation);
         }
      }

      return result;
   }

   public Kind getKind() {
      return Kinds.PORT_TYPE;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_PORT_TYPE;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void withAllSubEntitiesDo(EntityAction action) {
      super.withAllSubEntitiesDo(action);
      Iterator iter = this._operations.iterator();

      while(iter.hasNext()) {
         action.perform((Entity)iter.next());
      }

      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      Iterator iter = this._operations.iterator();

      while(iter.hasNext()) {
         ((Operation)iter.next()).accept(visitor);
      }

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
      return this.parent;
   }

   public void setParent(TWSDLExtensible parent) {
      this.parent = parent;
   }
}
