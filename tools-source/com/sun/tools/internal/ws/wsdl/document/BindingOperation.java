package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class BindingOperation extends Entity implements TWSDLExtensible {
   private ExtensibilityHelper _helper = new ExtensibilityHelper();
   private Documentation _documentation;
   private String _name;
   private BindingInput _input;
   private BindingOutput _output;
   private List _faults = new ArrayList();
   private OperationStyle _style;
   private String _uniqueKey;
   private TWSDLExtensible parent;

   public BindingOperation(Locator locator) {
      super(locator);
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getUniqueKey() {
      if (this._uniqueKey == null) {
         StringBuilder sb = new StringBuilder();
         sb.append(this._name);
         sb.append(' ');
         if (this._input != null) {
            sb.append(this._input.getName());
         } else {
            sb.append(this._name);
            if (this._style == OperationStyle.REQUEST_RESPONSE) {
               sb.append("Request");
            } else if (this._style == OperationStyle.SOLICIT_RESPONSE) {
               sb.append("Response");
            }
         }

         sb.append(' ');
         if (this._output != null) {
            sb.append(this._output.getName());
         } else {
            sb.append(this._name);
            if (this._style == OperationStyle.SOLICIT_RESPONSE) {
               sb.append("Solicit");
            } else if (this._style == OperationStyle.REQUEST_RESPONSE) {
               sb.append("Response");
            }
         }

         this._uniqueKey = sb.toString();
      }

      return this._uniqueKey;
   }

   public OperationStyle getStyle() {
      return this._style;
   }

   public void setStyle(OperationStyle s) {
      this._style = s;
   }

   public BindingInput getInput() {
      return this._input;
   }

   public void setInput(BindingInput i) {
      this._input = i;
   }

   public BindingOutput getOutput() {
      return this._output;
   }

   public void setOutput(BindingOutput o) {
      this._output = o;
   }

   public void addFault(BindingFault f) {
      this._faults.add(f);
   }

   public Iterable faults() {
      return this._faults;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_OPERATION;
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

   public void withAllSubEntitiesDo(EntityAction action) {
      if (this._input != null) {
         action.perform(this._input);
      }

      if (this._output != null) {
         action.perform(this._output);
      }

      Iterator var2 = this._faults.iterator();

      while(var2.hasNext()) {
         BindingFault _fault = (BindingFault)var2.next();
         action.perform(_fault);
      }

      this._helper.withAllSubEntitiesDo(action);
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.preVisit(this);
      this._helper.accept(visitor);
      if (this._input != null) {
         this._input.accept(visitor);
      }

      if (this._output != null) {
         this._output.accept(visitor);
      }

      Iterator var2 = this._faults.iterator();

      while(var2.hasNext()) {
         BindingFault _fault = (BindingFault)var2.next();
         _fault.accept(visitor);
      }

      visitor.postVisit(this);
   }

   public void validateThis() {
      if (this._name == null) {
         this.failValidation("validation.missingRequiredAttribute", "name");
      }

      if (this._style == null) {
         this.failValidation("validation.missingRequiredProperty", "style");
      }

      if (this._style == OperationStyle.ONE_WAY) {
         if (this._input == null) {
            this.failValidation("validation.missingRequiredSubEntity", "input");
         }

         if (this._output != null) {
            this.failValidation("validation.invalidSubEntity", "output");
         }

         if (this._faults != null && !this._faults.isEmpty()) {
            this.failValidation("validation.invalidSubEntity", "fault");
         }
      }

   }

   public void setParent(TWSDLExtensible parent) {
      this.parent = parent;
   }
}
