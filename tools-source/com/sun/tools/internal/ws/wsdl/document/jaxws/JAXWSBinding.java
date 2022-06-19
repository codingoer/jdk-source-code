package com.sun.tools.internal.ws.wsdl.document.jaxws;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class JAXWSBinding extends ExtensionImpl {
   private String wsdlNamespace;
   private String wsdlLocation;
   private String node;
   private String version;
   private CustomName jaxwsPackage;
   private List parameters;
   private Boolean enableWrapperStyle;
   private Boolean enableAsyncMapping;
   private Boolean enableMimeContentMapping;
   private Boolean isProvider;
   private Set jaxbBindings = new HashSet();
   private CustomName className;
   private CustomName methodName;

   public JAXWSBinding(Locator locator) {
      super(locator);
   }

   public void validateThis() {
   }

   public QName getElementName() {
      return JAXWSBindingsConstants.JAXWS_BINDINGS;
   }

   public QName getWSDLElementName() {
      return this.getElementName();
   }

   public void addExtension(ExtensionImpl e) {
   }

   public Iterable extensions() {
      return null;
   }

   public Boolean isEnableAsyncMapping() {
      return this.enableAsyncMapping;
   }

   public void setEnableAsyncMapping(Boolean enableAsyncMapping) {
      this.enableAsyncMapping = enableAsyncMapping;
   }

   public Boolean isEnableMimeContentMapping() {
      return this.enableMimeContentMapping;
   }

   public void setEnableMimeContentMapping(Boolean enableMimeContentMapping) {
      this.enableMimeContentMapping = enableMimeContentMapping;
   }

   public Boolean isEnableWrapperStyle() {
      return this.enableWrapperStyle;
   }

   public void setEnableWrapperStyle(Boolean enableWrapperStyle) {
      this.enableWrapperStyle = enableWrapperStyle;
   }

   public CustomName getJaxwsPackage() {
      return this.jaxwsPackage;
   }

   public void setJaxwsPackage(CustomName jaxwsPackage) {
      this.jaxwsPackage = jaxwsPackage;
   }

   public String getNode() {
      return this.node;
   }

   public void setNode(String node) {
      this.node = node;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getWsdlLocation() {
      return this.wsdlLocation;
   }

   public void setWsdlLocation(String wsdlLocation) {
      this.wsdlLocation = wsdlLocation;
   }

   public String getWsdlNamespace() {
      return this.wsdlNamespace;
   }

   public void setWsdlNamespace(String wsdlNamespace) {
      this.wsdlNamespace = wsdlNamespace;
   }

   public Set getJaxbBindings() {
      return this.jaxbBindings;
   }

   public void addJaxbBindings(Element jaxbBinding) {
      if (this.jaxbBindings != null) {
         this.jaxbBindings.add(jaxbBinding);
      }
   }

   public Boolean isProvider() {
      return this.isProvider;
   }

   public void setProvider(Boolean isProvider) {
      this.isProvider = isProvider;
   }

   public CustomName getMethodName() {
      return this.methodName;
   }

   public void setMethodName(CustomName methodName) {
      this.methodName = methodName;
   }

   public Iterator parameters() {
      return this.parameters.iterator();
   }

   public void addParameter(Parameter parameter) {
      if (this.parameters == null) {
         this.parameters = new ArrayList();
      }

      this.parameters.add(parameter);
   }

   public String getParameterName(String msgName, String wsdlPartName, QName element, boolean wrapperStyle) {
      if (msgName != null && wsdlPartName != null && element != null && this.parameters != null) {
         Iterator var5 = this.parameters.iterator();

         Parameter param;
         label39:
         do {
            do {
               do {
                  do {
                     if (!var5.hasNext()) {
                        return null;
                     }

                     param = (Parameter)var5.next();
                  } while(!param.getMessageName().equals(msgName));
               } while(!param.getPart().equals(wsdlPartName));

               if (wrapperStyle && param.getElement() != null) {
                  continue label39;
               }
            } while(wrapperStyle);

            return param.getName();
         } while(!param.getElement().equals(element));

         return param.getName();
      } else {
         return null;
      }
   }

   public CustomName getClassName() {
      return this.className;
   }

   public void setClassName(CustomName className) {
      this.className = className;
   }
}
