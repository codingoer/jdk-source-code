package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.xjc.util.DOMUtils;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

public class Internalizer {
   private final XPath xpath;
   private final DOMForest forest;
   private final ErrorReceiver errorReceiver;
   private static final ContextClassloaderLocal xpf = new ContextClassloaderLocal() {
      protected XPathFactory initialValue() throws Exception {
         return XPathFactory.newInstance();
      }
   };

   public Internalizer(DOMForest forest, WsimportOptions options, ErrorReceiver errorReceiver) {
      this.xpath = ((XPathFactory)xpf.get()).newXPath();
      this.forest = forest;
      this.errorReceiver = errorReceiver;
   }

   public void transform() {
      Iterator var1 = this.forest.outerMostBindings.iterator();

      while(var1.hasNext()) {
         Element jaxwsBinding = (Element)var1.next();
         this.internalize(jaxwsBinding, jaxwsBinding);
      }

   }

   private void validate(Element bindings) {
      NamedNodeMap atts = bindings.getAttributes();

      for(int i = 0; i < atts.getLength(); ++i) {
         Attr a = (Attr)atts.item(i);
         if (a.getNamespaceURI() == null && !a.getLocalName().equals("node") && a.getLocalName().equals("wsdlLocation")) {
         }
      }

   }

   private void internalize(Element bindings, Node inheritedTarget) {
      Node target = inheritedTarget;
      this.validate(bindings);
      if (this.isTopLevelBinding(bindings)) {
         String wsdlLocation;
         if (bindings.getAttributeNode("wsdlLocation") != null) {
            wsdlLocation = bindings.getAttribute("wsdlLocation");

            try {
               wsdlLocation = (new URL(new URL(this.forest.getSystemId(bindings.getOwnerDocument())), wsdlLocation)).toExternalForm();
            } catch (MalformedURLException var14) {
               wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
            }
         } else {
            wsdlLocation = this.forest.getFirstRootDocument();
         }

         target = this.forest.get(wsdlLocation);
         if (target == null) {
            this.reportError(bindings, WsdlMessages.INTERNALIZER_INCORRECT_SCHEMA_REFERENCE(wsdlLocation, EditDistance.findNearest(wsdlLocation, this.forest.listSystemIDs())));
            return;
         }
      }

      Element element = DOMUtil.getFirstElementChild((Node)target);
      int i;
      if (element != null && element.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && element.getLocalName().equals("definitions")) {
         Element type = DOMUtils.getFirstChildElement(element, "http://schemas.xmlsoap.org/wsdl/", "types");
         if (type != null) {
            Element[] var6 = DOMUtils.getChildElements(type, "http://www.w3.org/2001/XMLSchema", "schema");
            int var7 = var6.length;

            for(i = 0; i < var7; ++i) {
               Element schemaElement = var6[i];
               if (!schemaElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", "jaxb")) {
                  schemaElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
               }

               if (!schemaElement.hasAttributeNS("http://java.sun.com/xml/ns/jaxb", "version")) {
                  schemaElement.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "jaxb:version", "2.0");
               }
            }
         }
      }

      NodeList targetNodes = null;
      boolean hasNode = true;
      boolean isToplevelBinding = this.isTopLevelBinding(bindings);
      if ((this.isJAXWSBindings(bindings) || this.isJAXBBindings(bindings)) && bindings.getAttributeNode("node") != null) {
         targetNodes = this.evaluateXPathMultiNode(bindings, (Node)target, bindings.getAttribute("node"), new NamespaceContextImpl(bindings));
      } else if (this.isJAXWSBindings(bindings) && bindings.getAttributeNode("node") == null && !isToplevelBinding) {
         hasNode = false;
      } else if (this.isGlobalBinding(bindings) && !this.isWSDLDefinition((Node)target) && this.isTopLevelBinding(bindings.getParentNode())) {
         targetNodes = this.getWSDLDefintionNode(bindings, (Node)target);
      }

      if (targetNodes != null || !hasNode || isToplevelBinding) {
         int var11;
         Element[] children;
         if (hasNode && targetNodes != null) {
            for(i = 0; i < targetNodes.getLength(); ++i) {
               this.insertBinding(bindings, targetNodes.item(i));
               children = getChildElements(bindings);
               Element[] var10 = children;
               var11 = children.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  Element child = var10[var12];
                  if ("bindings".equals(child.getLocalName())) {
                     this.internalize(child, targetNodes.item(i));
                  }
               }
            }
         }

         if (targetNodes == null) {
            Element[] children = getChildElements(bindings);
            children = children;
            int var21 = children.length;

            for(var11 = 0; var11 < var21; ++var11) {
               Element child = children[var11];
               this.internalize(child, (Node)target);
            }
         }

      }
   }

   private void insertBinding(@NotNull Element bindings, @NotNull Node target) {
      if ("bindings".equals(bindings.getLocalName())) {
         Element[] children = DOMUtils.getChildElements(bindings);
         Element[] var4 = children;
         int var5 = children.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Element item = var4[var6];
            if (!"bindings".equals(item.getLocalName())) {
               this.moveUnder(item, (Element)target);
            }
         }
      } else {
         this.moveUnder(bindings, (Element)target);
      }

   }

   private NodeList getWSDLDefintionNode(Node bindings, Node target) {
      return this.evaluateXPathMultiNode(bindings, target, "wsdl:definitions", new NamespaceContext() {
         public String getNamespaceURI(String prefix) {
            return "http://schemas.xmlsoap.org/wsdl/";
         }

         public String getPrefix(String nsURI) {
            throw new UnsupportedOperationException();
         }

         public Iterator getPrefixes(String namespaceURI) {
            throw new UnsupportedOperationException();
         }
      });
   }

   private boolean isWSDLDefinition(Node target) {
      if (target == null) {
         return false;
      } else {
         String localName = target.getLocalName();
         String nsURI = target.getNamespaceURI();
         return fixNull(localName).equals("definitions") && fixNull(nsURI).equals("http://schemas.xmlsoap.org/wsdl/");
      }
   }

   private boolean isTopLevelBinding(Node node) {
      return node.getOwnerDocument().getDocumentElement() == node;
   }

   private boolean isJAXWSBindings(Node bindings) {
      return bindings.getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxws") && bindings.getLocalName().equals("bindings");
   }

   private boolean isJAXBBindings(Node bindings) {
      return bindings.getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb") && bindings.getLocalName().equals("bindings");
   }

   private boolean isGlobalBinding(Node bindings) {
      if (bindings.getNamespaceURI() == null) {
         this.errorReceiver.warning(this.forest.locatorTable.getStartLocation((Element)bindings), WsdlMessages.INVALID_CUSTOMIZATION_NAMESPACE(bindings.getLocalName()));
         return false;
      } else {
         return bindings.getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxws") && (bindings.getLocalName().equals("package") || bindings.getLocalName().equals("enableAsyncMapping") || bindings.getLocalName().equals("enableAdditionalSOAPHeaderMapping") || bindings.getLocalName().equals("enableWrapperStyle") || bindings.getLocalName().equals("enableMIMEContent"));
      }
   }

   private static Element[] getChildElements(Element parent) {
      ArrayList a = new ArrayList();
      NodeList children = parent.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node item = children.item(i);
         if (item instanceof Element && ("http://java.sun.com/xml/ns/jaxws".equals(item.getNamespaceURI()) || "http://java.sun.com/xml/ns/jaxb".equals(item.getNamespaceURI()))) {
            a.add((Element)item);
         }
      }

      return (Element[])a.toArray(new Element[a.size()]);
   }

   private NodeList evaluateXPathMultiNode(Node bindings, Node target, String expression, NamespaceContext namespaceContext) {
      NodeList nlst;
      try {
         this.xpath.setNamespaceContext(namespaceContext);
         nlst = (NodeList)this.xpath.evaluate(expression, target, XPathConstants.NODESET);
      } catch (XPathExpressionException var7) {
         this.reportError((Element)bindings, WsdlMessages.INTERNALIZER_X_PATH_EVALUATION_ERROR(var7.getMessage()), var7);
         return null;
      }

      if (nlst.getLength() == 0) {
         this.reportError((Element)bindings, WsdlMessages.INTERNALIZER_X_PATH_EVALUATES_TO_NO_TARGET(expression));
         return null;
      } else {
         return nlst;
      }
   }

   private boolean isJAXBBindingElement(Element e) {
      return fixNull(e.getNamespaceURI()).equals("http://java.sun.com/xml/ns/jaxb");
   }

   private boolean isJAXWSBindingElement(Element e) {
      return fixNull(e.getNamespaceURI()).equals("http://java.sun.com/xml/ns/jaxws");
   }

   private void moveUnder(Element decl, Element target) {
      if (this.isJAXBBindingElement(decl)) {
         if (!target.hasAttributeNS("http://www.w3.org/2000/xmlns/", "jaxb")) {
            target.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
         }

         if (!target.hasAttributeNS("http://java.sun.com/xml/ns/jaxb", "version")) {
            target.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "jaxb:version", "2.0");
         }

         if (target.getLocalName().equals("schema") && target.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema") && !target.hasAttributeNS("http://java.sun.com/xml/ns/jaxb", "extensionBindingPrefixes")) {
            target.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "jaxb:extensionBindingPrefixes", "xjc");
            target.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
         }

         target = this.refineSchemaTarget(target);
         this.copyInscopeNSAttributes(decl);
      } else {
         if (!this.isJAXWSBindingElement(decl)) {
            return;
         }

         if (!target.hasAttributeNS("http://www.w3.org/2000/xmlns/", "JAXWS")) {
            target.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:JAXWS", "http://java.sun.com/xml/ns/jaxws");
         }

         target = this.refineWSDLTarget(target);
         this.copyInscopeNSAttributes(decl);
      }

      if (target.getOwnerDocument() != decl.getOwnerDocument()) {
         decl = (Element)target.getOwnerDocument().importNode(decl, true);
      }

      target.appendChild(decl);
   }

   private void copyInscopeNSAttributes(Element e) {
      Element p = e;
      Set inscopes = new HashSet();

      while(true) {
         NamedNodeMap atts = p.getAttributes();

         for(int i = 0; i < atts.getLength(); ++i) {
            Attr a = (Attr)atts.item(i);
            if ("http://www.w3.org/2000/xmlns/".equals(a.getNamespaceURI())) {
               String prefix;
               if (a.getName().indexOf(58) == -1) {
                  prefix = "";
               } else {
                  prefix = a.getLocalName();
               }

               if (inscopes.add(prefix) && p != e) {
                  e.setAttributeNodeNS((Attr)a.cloneNode(true));
               }
            }
         }

         if (p.getParentNode() instanceof Document) {
            if (!inscopes.contains("")) {
               e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
            }

            return;
         }

         p = (Element)p.getParentNode();
      }
   }

   public Element refineSchemaTarget(Element target) {
      Element annotation = DOMUtils.getFirstChildElement(target, "http://www.w3.org/2001/XMLSchema", "annotation");
      if (annotation == null) {
         annotation = this.insertXMLSchemaElement(target, "annotation");
      }

      Element appinfo = DOMUtils.getFirstChildElement(annotation, "http://www.w3.org/2001/XMLSchema", "appinfo");
      if (appinfo == null) {
         appinfo = this.insertXMLSchemaElement(annotation, "appinfo");
      }

      return appinfo;
   }

   public Element refineWSDLTarget(Element target) {
      Element JAXWSBindings = DOMUtils.getFirstChildElement(target, "http://java.sun.com/xml/ns/jaxws", "bindings");
      if (JAXWSBindings == null) {
         JAXWSBindings = this.insertJAXWSBindingsElement(target, "bindings");
      }

      return JAXWSBindings;
   }

   private Element insertXMLSchemaElement(Element parent, String localName) {
      String qname = parent.getTagName();
      int idx = qname.indexOf(58);
      if (idx == -1) {
         qname = localName;
      } else {
         qname = qname.substring(0, idx + 1) + localName;
      }

      Element child = parent.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", qname);
      NodeList children = parent.getChildNodes();
      if (children.getLength() == 0) {
         parent.appendChild(child);
      } else {
         parent.insertBefore(child, children.item(0));
      }

      return child;
   }

   private Element insertJAXWSBindingsElement(Element parent, String localName) {
      String qname = "JAXWS:" + localName;
      Element child = parent.getOwnerDocument().createElementNS("http://java.sun.com/xml/ns/jaxws", qname);
      NodeList children = parent.getChildNodes();
      if (children.getLength() == 0) {
         parent.appendChild(child);
      } else {
         parent.insertBefore(child, children.item(0));
      }

      return child;
   }

   @NotNull
   static String fixNull(@Nullable String s) {
      return s == null ? "" : s;
   }

   private void reportError(Element errorSource, String formattedMsg) {
      this.reportError(errorSource, formattedMsg, (Exception)null);
   }

   private void reportError(Element errorSource, String formattedMsg, Exception nestedException) {
      SAXParseException e = new SAXParseException2(formattedMsg, this.forest.locatorTable.getStartLocation(errorSource), nestedException);
      this.errorReceiver.error((SAXParseException)e);
   }
}
