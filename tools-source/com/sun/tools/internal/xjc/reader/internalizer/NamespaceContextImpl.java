package com.sun.tools.internal.xjc.reader.internalizer;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class NamespaceContextImpl implements NamespaceContext {
   private final Element e;

   public NamespaceContextImpl(Element e) {
      this.e = e;
   }

   public String getNamespaceURI(String prefix) {
      Node parent = this.e;
      String namespace = null;
      String prefixColon = prefix + ':';
      short type;
      if (prefix.equals("xml")) {
         namespace = "http://www.w3.org/XML/1998/namespace";
      } else {
         for(; null != parent && null == namespace && ((type = ((Node)parent).getNodeType()) == 1 || type == 5); parent = ((Node)parent).getParentNode()) {
            if (type == 1) {
               if (((Node)parent).getNodeName().startsWith(prefixColon)) {
                  return ((Node)parent).getNamespaceURI();
               }

               NamedNodeMap nnm = ((Node)parent).getAttributes();

               for(int i = 0; i < nnm.getLength(); ++i) {
                  Node attr = nnm.item(i);
                  String aname = attr.getNodeName();
                  boolean isPrefix = aname.startsWith("xmlns:");
                  if (isPrefix || aname.equals("xmlns")) {
                     int index = aname.indexOf(58);
                     String p = isPrefix ? aname.substring(index + 1) : "";
                     if (p.equals(prefix)) {
                        namespace = attr.getNodeValue();
                        break;
                     }
                  }
               }
            }
         }
      }

      return prefix.equals("") ? "" : namespace;
   }

   public String getPrefix(String namespaceURI) {
      throw new UnsupportedOperationException();
   }

   public Iterator getPrefixes(String namespaceURI) {
      throw new UnsupportedOperationException();
   }
}
