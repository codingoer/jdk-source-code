package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wsdl.document.BindingOperation;
import com.sun.tools.internal.ws.wsdl.document.Definitions;
import com.sun.tools.internal.ws.wsdl.document.Documentation;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import com.sun.tools.internal.ws.wsdl.document.Port;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.tools.internal.ws.wsdl.document.Service;
import com.sun.tools.internal.ws.wsdl.document.jaxws.CustomName;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBinding;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBindingsConstants;
import com.sun.tools.internal.ws.wsdl.document.jaxws.Parameter;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JAXWSBindingExtensionHandler extends AbstractExtensionHandler {
   private static final ContextClassloaderLocal xpf = new ContextClassloaderLocal() {
      protected XPathFactory initialValue() throws Exception {
         return XPathFactory.newInstance();
      }
   };
   private final XPath xpath;

   public JAXWSBindingExtensionHandler(Map extensionHandlerMap) {
      super(extensionHandlerMap);
      this.xpath = ((XPathFactory)xpf.get()).newXPath();
   }

   public String getNamespaceURI() {
      return "http://java.sun.com/xml/ns/jaxws";
   }

   private boolean parseGlobalJAXWSBindings(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      context.push();
      context.registerNamespaces(e);
      JAXWSBinding jaxwsBinding = getJAXWSExtension(parent);
      if (jaxwsBinding == null) {
         jaxwsBinding = new JAXWSBinding(context.getLocation(e));
      }

      String attr = XmlUtil.getAttributeOrNull(e, "wsdlLocation");
      if (attr != null) {
         jaxwsBinding.setWsdlLocation(attr);
      }

      attr = XmlUtil.getAttributeOrNull(e, "node");
      if (attr != null) {
         jaxwsBinding.setNode(attr);
      }

      attr = XmlUtil.getAttributeOrNull(e, "version");
      if (attr != null) {
         jaxwsBinding.setVersion(attr);
      }

      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.PACKAGE)) {
            this.parsePackage(context, jaxwsBinding, e2);
            if (jaxwsBinding.getJaxwsPackage() != null && jaxwsBinding.getJaxwsPackage().getJavaDoc() != null) {
               ((Definitions)parent).setDocumentation(new Documentation(jaxwsBinding.getJaxwsPackage().getJavaDoc()));
            }
         } else if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_WRAPPER_STYLE)) {
            this.parseWrapperStyle(context, jaxwsBinding, e2);
         } else if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_ASYNC_MAPPING)) {
            this.parseAsynMapping(context, jaxwsBinding, e2);
         } else {
            if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_MIME_CONTENT)) {
               Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
               return false;
            }

            this.parseMimeContent(context, jaxwsBinding, e2);
         }
      }

      parent.addExtension(jaxwsBinding);
      context.pop();
      return true;
   }

   private static JAXWSBinding getJAXWSExtension(TWSDLExtensible extensible) {
      Iterator var1 = extensible.extensions().iterator();

      TWSDLExtension extension;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         extension = (TWSDLExtension)var1.next();
      } while(!extension.getClass().equals(JAXWSBinding.class));

      return (JAXWSBinding)extension;
   }

   private void parseProvider(TWSDLParserContext context, JAXWSBinding parent, Element e) {
      String val = e.getTextContent();
      if (val != null) {
         if (!val.equals("false") && !val.equals("0")) {
            if (val.equals("true") || val.equals("1")) {
               parent.setProvider(Boolean.TRUE);
            }
         } else {
            parent.setProvider(Boolean.FALSE);
         }

      }
   }

   private void parsePackage(TWSDLParserContext context, JAXWSBinding parent, Element e) {
      String packageName = XmlUtil.getAttributeOrNull(e, "name");
      parent.setJaxwsPackage(new CustomName(packageName, this.getJavaDoc(e)));
   }

   private void parseWrapperStyle(TWSDLParserContext context, JAXWSBinding parent, Element e) {
      String val = e.getTextContent();
      if (val != null) {
         if (!val.equals("false") && !val.equals("0")) {
            if (val.equals("true") || val.equals("1")) {
               parent.setEnableWrapperStyle(Boolean.TRUE);
            }
         } else {
            parent.setEnableWrapperStyle(Boolean.FALSE);
         }

      }
   }

   private void parseAsynMapping(TWSDLParserContext context, JAXWSBinding parent, Element e) {
      String val = e.getTextContent();
      if (val != null) {
         if (!val.equals("false") && !val.equals("0")) {
            if (val.equals("true") || val.equals("1")) {
               parent.setEnableAsyncMapping(Boolean.TRUE);
            }
         } else {
            parent.setEnableAsyncMapping(Boolean.FALSE);
         }

      }
   }

   private void parseMimeContent(TWSDLParserContext context, JAXWSBinding parent, Element e) {
      String val = e.getTextContent();
      if (val != null) {
         if (!val.equals("false") && !val.equals("0")) {
            if (val.equals("true") || val.equals("1")) {
               parent.setEnableMimeContentMapping(Boolean.TRUE);
            }
         } else {
            parent.setEnableMimeContentMapping(Boolean.FALSE);
         }

      }
   }

   private void parseMethod(TWSDLParserContext context, JAXWSBinding jaxwsBinding, Element e) {
      String methodName = XmlUtil.getAttributeOrNull(e, "name");
      String javaDoc = this.getJavaDoc(e);
      CustomName name = new CustomName(methodName, javaDoc);
      jaxwsBinding.setMethodName(name);
   }

   private void parseParameter(TWSDLParserContext context, JAXWSBinding jaxwsBinding, Element e) {
      String part = XmlUtil.getAttributeOrNull(e, "part");
      Element msgPartElm = this.evaluateXPathNode(e.getOwnerDocument(), part, new NamespaceContextImpl(e));
      Node msgElm = msgPartElm.getParentNode();
      String partName = XmlUtil.getAttributeOrNull(msgPartElm, "name");
      String msgName = XmlUtil.getAttributeOrNull((Element)msgElm, "name");
      if (partName != null && msgName != null) {
         String element = XmlUtil.getAttributeOrNull(e, "childElementName");
         String name = XmlUtil.getAttributeOrNull(e, "name");
         QName elementName = null;
         if (element != null) {
            String uri = e.lookupNamespaceURI(XmlUtil.getPrefix(element));
            elementName = uri == null ? null : new QName(uri, XmlUtil.getLocalPart(element));
         }

         jaxwsBinding.addParameter(new Parameter(msgName, partName, elementName, name));
      }
   }

   private Element evaluateXPathNode(Node target, String expression, NamespaceContext namespaceContext) {
      NodeList nlst;
      try {
         this.xpath.setNamespaceContext(namespaceContext);
         nlst = (NodeList)this.xpath.evaluate(expression, target, XPathConstants.NODESET);
      } catch (XPathExpressionException var6) {
         Util.fail("internalizer.XPathEvaluationError", var6.getMessage());
         return null;
      }

      if (nlst.getLength() == 0) {
         Util.fail("internalizer.XPathEvaluatesToNoTarget", new Object[]{expression});
         return null;
      } else if (nlst.getLength() != 1) {
         Util.fail("internalizer.XPathEvaulatesToTooManyTargets", new Object[]{expression, nlst.getLength()});
         return null;
      } else {
         Node rnode = nlst.item(0);
         if (!(rnode instanceof Element)) {
            Util.fail("internalizer.XPathEvaluatesToNonElement", new Object[]{expression});
            return null;
         } else {
            return (Element)rnode;
         }
      }
   }

   private void parseClass(TWSDLParserContext context, JAXWSBinding jaxwsBinding, Element e) {
      String className = XmlUtil.getAttributeOrNull(e, "name");
      String javaDoc = this.getJavaDoc(e);
      jaxwsBinding.setClassName(new CustomName(className, javaDoc));
   }

   public boolean handleDefinitionsExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return this.parseGlobalJAXWSBindings(context, parent, e);
   }

   public boolean handlePortTypeExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_WRAPPER_STYLE)) {
               this.parseWrapperStyle(context, jaxwsBinding, e2);
            } else if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_ASYNC_MAPPING)) {
               this.parseAsynMapping(context, jaxwsBinding, e2);
            } else {
               if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.CLASS)) {
                  Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
                  return false;
               }

               this.parseClass(context, jaxwsBinding, e2);
               if (jaxwsBinding.getClassName() != null && jaxwsBinding.getClassName().getJavaDoc() != null && parent instanceof PortType) {
                  ((PortType)parent).setDocumentation(new Documentation(jaxwsBinding.getClassName().getJavaDoc()));
               }
            }
         }

         parent.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   public boolean handleOperationExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         if (parent instanceof Operation) {
            return this.handlePortTypeOperation(context, (Operation)parent, e);
         } else {
            return parent instanceof BindingOperation ? this.handleBindingOperation(context, (BindingOperation)parent, e) : false;
         }
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   private boolean handleBindingOperation(TWSDLParserContext context, BindingOperation operation, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_MIME_CONTENT)) {
               this.parseMimeContent(context, jaxwsBinding, e2);
            } else {
               if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.PARAMETER)) {
                  Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
                  return false;
               }

               this.parseParameter(context, jaxwsBinding, e2);
            }
         }

         operation.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   private boolean handlePortTypeOperation(TWSDLParserContext context, Operation parent, Element e) {
      context.push();
      context.registerNamespaces(e);
      JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_WRAPPER_STYLE)) {
            this.parseWrapperStyle(context, jaxwsBinding, e2);
         } else if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_ASYNC_MAPPING)) {
            this.parseAsynMapping(context, jaxwsBinding, e2);
         } else if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.METHOD)) {
            this.parseMethod(context, jaxwsBinding, e2);
            if (jaxwsBinding.getMethodName() != null && jaxwsBinding.getMethodName().getJavaDoc() != null) {
               parent.setDocumentation(new Documentation(jaxwsBinding.getMethodName().getJavaDoc()));
            }
         } else {
            if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.PARAMETER)) {
               Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
               return false;
            }

            this.parseParameter(context, jaxwsBinding, e2);
         }
      }

      parent.addExtension(jaxwsBinding);
      context.pop();
      return true;
   }

   public boolean handleBindingExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.ENABLE_MIME_CONTENT)) {
               Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
               return false;
            }

            this.parseMimeContent(context, jaxwsBinding, e2);
         }

         parent.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.CLASS)) {
               Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
               return false;
            }

            this.parseClass(context, jaxwsBinding, e2);
            if (jaxwsBinding.getClassName() != null && jaxwsBinding.getClassName().getJavaDoc() != null) {
               ((Fault)parent).setDocumentation(new Documentation(jaxwsBinding.getClassName().getJavaDoc()));
            }
         }

         parent.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   public boolean handleServiceExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.CLASS)) {
               Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
               return false;
            }

            this.parseClass(context, jaxwsBinding, e2);
            if (jaxwsBinding.getClassName() != null && jaxwsBinding.getClassName().getJavaDoc() != null) {
               ((Service)parent).setDocumentation(new Documentation(jaxwsBinding.getClassName().getJavaDoc()));
            }
         }

         parent.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, JAXWSBindingsConstants.JAXWS_BINDINGS)) {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      } else {
         context.push();
         context.registerNamespaces(e);
         JAXWSBinding jaxwsBinding = new JAXWSBinding(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.PROVIDER)) {
               this.parseProvider(context, jaxwsBinding, e2);
            } else {
               if (!XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.METHOD)) {
                  Util.fail("parsing.invalidExtensionElement", e2.getTagName(), e2.getNamespaceURI());
                  return false;
               }

               this.parseMethod(context, jaxwsBinding, e2);
               if (jaxwsBinding.getMethodName() != null && jaxwsBinding.getMethodName().getJavaDoc() != null) {
                  ((Port)parent).setDocumentation(new Documentation(jaxwsBinding.getMethodName().getJavaDoc()));
               }
            }
         }

         parent.addExtension(jaxwsBinding);
         context.pop();
         return true;
      }
   }

   private String getJavaDoc(Element e) {
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, JAXWSBindingsConstants.JAVADOC)) {
            return XmlUtil.getTextForNode(e2);
         }
      }

      return null;
   }
}
