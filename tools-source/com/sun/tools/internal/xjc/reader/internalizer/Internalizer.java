package com.sun.tools.internal.xjc.reader.internalizer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.util.DOMUtils;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.xsom.SCD;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

class Internalizer {
   private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";
   private final XPath xpath;
   private final DOMForest forest;
   private ErrorReceiver errorHandler;
   private boolean enableSCD;
   private static final String EXTENSION_PREFIXES = "extensionBindingPrefixes";

   static SCDBasedBindingSet transform(DOMForest forest, boolean enableSCD, boolean disableSecureProcessing) {
      return (new Internalizer(forest, enableSCD, disableSecureProcessing)).transform();
   }

   private Internalizer(DOMForest forest, boolean enableSCD, boolean disableSecureProcessing) {
      this.errorHandler = forest.getErrorHandler();
      this.forest = forest;
      this.enableSCD = enableSCD;
      this.xpath = XmlFactory.createXPathFactory(disableSecureProcessing).newXPath();
   }

   private SCDBasedBindingSet transform() {
      Map targetNodes = new HashMap();
      SCDBasedBindingSet scd = new SCDBasedBindingSet(this.forest);
      Iterator var3 = this.forest.outerMostBindings.iterator();

      Element jaxbBindings;
      while(var3.hasNext()) {
         jaxbBindings = (Element)var3.next();
         this.buildTargetNodeMap(jaxbBindings, jaxbBindings, (SCDBasedBindingSet.Target)null, targetNodes, scd);
      }

      var3 = this.forest.outerMostBindings.iterator();

      while(var3.hasNext()) {
         jaxbBindings = (Element)var3.next();
         this.move(jaxbBindings, targetNodes);
      }

      return scd;
   }

   private void validate(Element bindings) {
      NamedNodeMap atts = bindings.getAttributes();

      for(int i = 0; i < atts.getLength(); ++i) {
         Attr a = (Attr)atts.item(i);
         if (a.getNamespaceURI() == null && !a.getLocalName().equals("node") && !a.getLocalName().equals("schemaLocation") && !a.getLocalName().equals("scd") && !a.getLocalName().equals("required") && a.getLocalName().equals("multiple")) {
         }
      }

   }

   private void buildTargetNodeMap(Element bindings, @NotNull Node inheritedTarget, @Nullable SCDBasedBindingSet.Target inheritedSCD, Map result, SCDBasedBindingSet scdResult) {
      Node target = inheritedTarget;
      ArrayList targetMultiple = null;
      this.validate(bindings);
      boolean required = true;
      boolean multiple = false;
      String nodeXPath;
      if (bindings.getAttribute("required") != null) {
         nodeXPath = bindings.getAttribute("required");
         if (nodeXPath.equals("no") || nodeXPath.equals("false") || nodeXPath.equals("0")) {
            required = false;
         }
      }

      if (bindings.getAttribute("multiple") != null) {
         nodeXPath = bindings.getAttribute("multiple");
         if (nodeXPath.equals("yes") || nodeXPath.equals("true") || nodeXPath.equals("1")) {
            multiple = true;
         }
      }

      int i;
      int var33;
      if (bindings.getAttributeNode("schemaLocation") != null) {
         nodeXPath = bindings.getAttribute("schemaLocation");
         if (nodeXPath.equals("*")) {
            String[] var28 = this.forest.listSystemIDs();
            i = var28.length;

            for(var33 = 0; var33 < i; ++var33) {
               String systemId = var28[var33];
               if (result.get(bindings) == null) {
                  result.put(bindings, new ArrayList());
               }

               ((List)result.get(bindings)).add(this.forest.get(systemId).getDocumentElement());
               Element[] children = DOMUtils.getChildElements(bindings, "http://java.sun.com/xml/ns/jaxb", "bindings");
               Element[] var37 = children;
               int var17 = children.length;

               for(int var18 = 0; var18 < var17; ++var18) {
                  Element value = var37[var18];
                  this.buildTargetNodeMap(value, this.forest.get(systemId).getDocumentElement(), inheritedSCD, result, scdResult);
               }
            }

            return;
         }

         Document target;
         try {
            URL loc = new URL(new URL(this.forest.getSystemId(bindings.getOwnerDocument())), nodeXPath);
            nodeXPath = loc.toExternalForm();
            target = this.forest.get(nodeXPath);
            if (target == null && loc.getProtocol().startsWith("file")) {
               File f = new File(loc.getFile());
               nodeXPath = (new File(f.getCanonicalPath())).toURI().toString();
            }
         } catch (MalformedURLException var21) {
         } catch (IOException var22) {
            Logger.getLogger(Internalizer.class.getName()).log(Level.FINEST, var22.getLocalizedMessage());
         }

         target = this.forest.get(nodeXPath);
         if (target == null) {
            this.reportError(bindings, Messages.format("Internalizer.IncorrectSchemaReference", nodeXPath, EditDistance.findNearest(nodeXPath, this.forest.listSystemIDs())));
            return;
         }

         target = ((Document)target).getDocumentElement();
      }

      if (bindings.getAttributeNode("node") != null) {
         nodeXPath = bindings.getAttribute("node");

         NodeList nlst;
         try {
            this.xpath.setNamespaceContext(new NamespaceContextImpl(bindings));
            nlst = (NodeList)this.xpath.evaluate(nodeXPath, target, XPathConstants.NODESET);
         } catch (XPathExpressionException var23) {
            if (required) {
               this.reportError(bindings, Messages.format("Internalizer.XPathEvaluationError", var23.getMessage()), var23);
               return;
            }

            return;
         }

         if (nlst.getLength() == 0) {
            if (required) {
               this.reportError(bindings, Messages.format("Internalizer.XPathEvaluatesToNoTarget", nodeXPath));
            }

            return;
         }

         if (nlst.getLength() != 1) {
            if (!multiple) {
               this.reportError(bindings, Messages.format("Internalizer.XPathEvaulatesToTooManyTargets", nodeXPath, nlst.getLength()));
               return;
            }

            if (targetMultiple == null) {
               targetMultiple = new ArrayList();
            }

            for(i = 0; i < nlst.getLength(); ++i) {
               targetMultiple.add(nlst.item(i));
            }
         }

         if (multiple && nlst.getLength() != 1) {
            Iterator var34 = targetMultiple.iterator();

            while(var34.hasNext()) {
               Node rnode = (Node)var34.next();
               if (!(rnode instanceof Element)) {
                  this.reportError(bindings, Messages.format("Internalizer.XPathEvaluatesToNonElement", nodeXPath));
                  return;
               }

               if (!this.forest.logic.checkIfValidTargetNode(this.forest, bindings, (Element)rnode)) {
                  this.reportError(bindings, Messages.format("Internalizer.XPathEvaluatesToNonSchemaElement", nodeXPath, rnode.getNodeName()));
                  return;
               }
            }
         } else {
            Node rnode = nlst.item(0);
            if (!(rnode instanceof Element)) {
               this.reportError(bindings, Messages.format("Internalizer.XPathEvaluatesToNonElement", nodeXPath));
               return;
            }

            if (!this.forest.logic.checkIfValidTargetNode(this.forest, bindings, (Element)rnode)) {
               this.reportError(bindings, Messages.format("Internalizer.XPathEvaluatesToNonSchemaElement", nodeXPath, rnode.getNodeName()));
               return;
            }

            target = rnode;
         }
      }

      if (bindings.getAttributeNode("scd") != null) {
         nodeXPath = bindings.getAttribute("scd");
         if (!this.enableSCD) {
            this.reportError(bindings, Messages.format("SCD_NOT_ENABLED"));
            this.enableSCD = true;
         }

         try {
            inheritedSCD = scdResult.createNewTarget(inheritedSCD, bindings, SCD.create(nodeXPath, new NamespaceContextImpl(bindings)));
         } catch (ParseException var20) {
            this.reportError(bindings, Messages.format("ERR_SCD_EVAL", var20.getMessage()), var20);
            return;
         }
      }

      if (inheritedSCD != null) {
         inheritedSCD.addBinidng(bindings);
      } else {
         Node rnode;
         if (multiple && targetMultiple != null) {
            for(Iterator var30 = targetMultiple.iterator(); var30.hasNext(); ((List)result.get(bindings)).add(rnode)) {
               rnode = (Node)var30.next();
               if (result.get(bindings) == null) {
                  result.put(bindings, new ArrayList());
               }
            }
         } else {
            if (result.get(bindings) == null) {
               result.put(bindings, new ArrayList());
            }

            ((List)result.get(bindings)).add(target);
         }
      }

      Element[] children = DOMUtils.getChildElements(bindings, "http://java.sun.com/xml/ns/jaxb", "bindings");
      Element[] var27 = children;
      i = children.length;

      for(var33 = 0; var33 < i; ++var33) {
         Element value = var27[var33];
         if (multiple && targetMultiple != null) {
            Iterator var15 = targetMultiple.iterator();

            while(var15.hasNext()) {
               Node rnode = (Node)var15.next();
               this.buildTargetNodeMap(value, rnode, inheritedSCD, result, scdResult);
            }
         } else {
            this.buildTargetNodeMap(value, (Node)target, inheritedSCD, result, scdResult);
         }
      }

   }

   private void move(Element bindings, Map targetNodes) {
      List nodelist = (List)targetNodes.get(bindings);
      if (nodelist != null) {
         Iterator var4 = nodelist.iterator();

         while(var4.hasNext()) {
            Node target = (Node)var4.next();
            if (target == null) {
               return;
            }

            Element[] var6 = DOMUtils.getChildElements(bindings);
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Element item = var6[var8];
               String localName = item.getLocalName();
               if ("bindings".equals(localName)) {
                  this.move(item, targetNodes);
               } else if ("globalBindings".equals(localName)) {
                  Element root = this.forest.getOneDocument().getDocumentElement();
                  if (root.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) {
                     NodeList elements = root.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
                     if (elements == null || elements.getLength() < 1) {
                        this.reportError(item, Messages.format("Internalizer.OrphanedCustomization", item.getNodeName()));
                        return;
                     }

                     this.moveUnder(item, (Element)elements.item(0));
                  } else {
                     this.moveUnder(item, root);
                  }
               } else {
                  if (!(target instanceof Element)) {
                     this.reportError(item, Messages.format("Internalizer.ContextNodeIsNotElement"));
                     return;
                  }

                  if (!this.forest.logic.checkIfValidTargetNode(this.forest, item, (Element)target)) {
                     this.reportError(item, Messages.format("Internalizer.OrphanedCustomization", item.getNodeName()));
                     return;
                  }

                  this.moveUnder(item, (Element)target);
               }
            }
         }

      }
   }

   private void moveUnder(Element decl, Element target) {
      Element realTarget = this.forest.logic.refineTarget(target);
      this.declExtensionNamespace(decl, target);
      Element p = decl;
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

               if (inscopes.add(prefix) && p != decl) {
                  decl.setAttributeNodeNS((Attr)a.cloneNode(true));
               }
            }
         }

         if (p.getParentNode() instanceof Document) {
            if (!inscopes.contains("")) {
               decl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
            }

            if (realTarget.getOwnerDocument() != decl.getOwnerDocument()) {
               Element original = decl;
               decl = (Element)realTarget.getOwnerDocument().importNode(decl, true);
               this.copyLocators(original, decl);
            }

            realTarget.appendChild(decl);
            return;
         }

         p = (Element)p.getParentNode();
      }
   }

   private void declExtensionNamespace(Element decl, Element target) {
      if (!"http://java.sun.com/xml/ns/jaxb".equals(decl.getNamespaceURI())) {
         this.declareExtensionNamespace(target, decl.getNamespaceURI());
      }

      NodeList lst = decl.getChildNodes();

      for(int i = 0; i < lst.getLength(); ++i) {
         Node n = lst.item(i);
         if (n instanceof Element) {
            this.declExtensionNamespace((Element)n, target);
         }
      }

   }

   private void declareExtensionNamespace(Element target, String nsUri) {
      Element root = target.getOwnerDocument().getDocumentElement();
      Attr att = root.getAttributeNodeNS("http://java.sun.com/xml/ns/jaxb", "extensionBindingPrefixes");
      String prefix;
      if (att == null) {
         prefix = this.allocatePrefix(root, "http://java.sun.com/xml/ns/jaxb");
         att = target.getOwnerDocument().createAttributeNS("http://java.sun.com/xml/ns/jaxb", prefix + ':' + "extensionBindingPrefixes");
         root.setAttributeNodeNS(att);
      }

      prefix = this.allocatePrefix(root, nsUri);
      if (att.getValue().indexOf(prefix) == -1) {
         att.setValue(att.getValue() + ' ' + prefix);
      }

   }

   private String allocatePrefix(Element e, String nsUri) {
      NamedNodeMap atts = e.getAttributes();

      for(int i = 0; i < atts.getLength(); ++i) {
         Attr a = (Attr)atts.item(i);
         if ("http://www.w3.org/2000/xmlns/".equals(a.getNamespaceURI()) && a.getName().indexOf(58) != -1 && a.getValue().equals(nsUri)) {
            return a.getLocalName();
         }
      }

      String prefix;
      do {
         prefix = "p" + (int)(Math.random() * 1000000.0) + '_';
      } while(e.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", prefix) != null);

      e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, nsUri);
      return prefix;
   }

   private void copyLocators(Element src, Element dst) {
      this.forest.locatorTable.storeStartLocation(dst, this.forest.locatorTable.getStartLocation(src));
      this.forest.locatorTable.storeEndLocation(dst, this.forest.locatorTable.getEndLocation(src));
      Element[] srcChilds = DOMUtils.getChildElements(src);
      Element[] dstChilds = DOMUtils.getChildElements(dst);

      for(int i = 0; i < srcChilds.length; ++i) {
         this.copyLocators(srcChilds[i], dstChilds[i]);
      }

   }

   private void reportError(Element errorSource, String formattedMsg) {
      this.reportError(errorSource, formattedMsg, (Exception)null);
   }

   private void reportError(Element errorSource, String formattedMsg, Exception nestedException) {
      SAXParseException e = new SAXParseException2(formattedMsg, this.forest.locatorTable.getStartLocation(errorSource), nestedException);
      this.errorHandler.error((SAXParseException)e);
   }
}
