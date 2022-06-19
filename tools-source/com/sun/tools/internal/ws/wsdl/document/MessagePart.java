package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.EntityReferenceAction;
import com.sun.tools.internal.ws.wsdl.framework.Kind;
import com.sun.tools.internal.ws.wsdl.framework.QNameAction;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class MessagePart extends Entity {
   public static final int SOAP_BODY_BINDING = 1;
   public static final int SOAP_HEADER_BINDING = 2;
   public static final int SOAP_HEADERFAULT_BINDING = 3;
   public static final int SOAP_FAULT_BINDING = 4;
   public static final int WSDL_MIME_BINDING = 5;
   public static final int PART_NOT_BOUNDED = -1;
   private boolean isRet;
   private String _name;
   private QName _descriptor;
   private Kind _descriptorKind;
   private int _bindingKind;
   private WebParam.Mode mode;

   public MessagePart(Locator locator) {
      super(locator);
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public QName getDescriptor() {
      return this._descriptor;
   }

   public void setDescriptor(QName n) {
      this._descriptor = n;
   }

   public Kind getDescriptorKind() {
      return this._descriptorKind;
   }

   public void setDescriptorKind(Kind k) {
      this._descriptorKind = k;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_PART;
   }

   public int getBindingExtensibilityElementKind() {
      return this._bindingKind;
   }

   public void setBindingExtensibilityElementKind(int kind) {
      this._bindingKind = kind;
   }

   public void withAllQNamesDo(QNameAction action) {
      if (this._descriptor != null) {
         action.perform(this._descriptor);
      }

   }

   public void withAllEntityReferencesDo(EntityReferenceAction action) {
      super.withAllEntityReferencesDo(action);
      if (this._descriptor != null && this._descriptorKind != null) {
         action.perform(this._descriptorKind, this._descriptor);
      }

   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public void validateThis() {
      if (this._descriptor != null && this._descriptor.getLocalPart().equals("")) {
         this.failValidation("validation.invalidElement", this._descriptor.toString());
      }

   }

   public void setMode(WebParam.Mode mode) {
      this.mode = mode;
   }

   public WebParam.Mode getMode() {
      return this.mode;
   }

   public boolean isINOUT() {
      if (this.mode != null) {
         return this.mode == Mode.INOUT;
      } else {
         return false;
      }
   }

   public boolean isIN() {
      if (this.mode != null) {
         return this.mode == Mode.IN;
      } else {
         return false;
      }
   }

   public boolean isOUT() {
      if (this.mode != null) {
         return this.mode == Mode.OUT;
      } else {
         return false;
      }
   }

   public void setReturn(boolean ret) {
      this.isRet = ret;
   }

   public boolean isReturn() {
      return this.isRet;
   }
}
