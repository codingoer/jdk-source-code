package com.sun.tools.internal.ws.wsdl.document.mime;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.wsdl.framework.EntityAction;
import com.sun.tools.internal.ws.wsdl.framework.ExtensibilityHelper;
import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class MIMEPart extends ExtensionImpl implements TWSDLExtensible {
   private String _name;
   private ExtensibilityHelper _helper = new ExtensibilityHelper();

   public MIMEPart(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return MIMEConstants.QNAME_PART;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String s) {
      this._name = s;
   }

   public String getNameValue() {
      return this.getName();
   }

   public String getNamespaceURI() {
      return this.getParent().getNamespaceURI();
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

   public void withAllSubEntitiesDo(EntityAction action) {
      this._helper.withAllSubEntitiesDo(action);
   }

   public void validateThis() {
   }
}
