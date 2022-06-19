package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.wsdl.framework.Entity;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class Import extends Entity {
   private Documentation _documentation;
   private String _location;
   private String _namespace;

   public Import(Locator locator) {
      super(locator);
   }

   public String getNamespace() {
      return this._namespace;
   }

   public void setNamespace(String s) {
      this._namespace = s;
   }

   public String getLocation() {
      return this._location;
   }

   public void setLocation(String s) {
      this._location = s;
   }

   public QName getElementName() {
      return WSDLConstants.QNAME_IMPORT;
   }

   public Documentation getDocumentation() {
      return this._documentation;
   }

   public void setDocumentation(Documentation d) {
      this._documentation = d;
   }

   public void accept(WSDLDocumentVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public void validateThis() {
      if (this._location == null) {
         this.failValidation("validation.missingRequiredAttribute", "location");
      }

      if (this._namespace == null) {
         this.failValidation("validation.missingRequiredAttribute", "namespace");
      }

   }
}
