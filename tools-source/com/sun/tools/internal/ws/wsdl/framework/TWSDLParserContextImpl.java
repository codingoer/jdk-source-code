package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.parser.DOMForest;
import com.sun.xml.internal.ws.util.NamespaceSupport;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class TWSDLParserContextImpl implements TWSDLParserContext {
   private static final String PREFIX_XMLNS = "xmlns";
   private boolean _followImports;
   private final AbstractDocument _document;
   private final NamespaceSupport _nsSupport;
   private final ArrayList _listeners;
   private final WSDLLocation _wsdlLocation;
   private final DOMForest forest;
   private final ErrorReceiver errorReceiver;

   public TWSDLParserContextImpl(DOMForest forest, AbstractDocument doc, ArrayList listeners, ErrorReceiver errReceiver) {
      this._document = doc;
      this._listeners = listeners;
      this._nsSupport = new NamespaceSupport();
      this._wsdlLocation = new WSDLLocation();
      this.forest = forest;
      this.errorReceiver = errReceiver;
   }

   public AbstractDocument getDocument() {
      return this._document;
   }

   public boolean getFollowImports() {
      return this._followImports;
   }

   public void setFollowImports(boolean b) {
      this._followImports = b;
   }

   public void push() {
      this._nsSupport.pushContext();
   }

   public void pop() {
      this._nsSupport.popContext();
   }

   public String getNamespaceURI(String prefix) {
      return this._nsSupport.getURI(prefix);
   }

   public Iterable getPrefixes() {
      return this._nsSupport.getPrefixes();
   }

   public String getDefaultNamespaceURI() {
      return this.getNamespaceURI("");
   }

   public void registerNamespaces(Element e) {
      Iterator iter = XmlUtil.getAllAttributes(e);

      while(iter.hasNext()) {
         Attr a = (Attr)iter.next();
         if (a.getName().equals("xmlns")) {
            this._nsSupport.declarePrefix("", a.getValue());
         } else {
            String prefix = XmlUtil.getPrefix(a.getName());
            if (prefix != null && prefix.equals("xmlns")) {
               String nsPrefix = XmlUtil.getLocalPart(a.getName());
               String uri = a.getValue();
               this._nsSupport.declarePrefix(nsPrefix, uri);
            }
         }
      }

   }

   public Locator getLocation(Element e) {
      return this.forest.locatorTable.getStartLocation(e);
   }

   public QName translateQualifiedName(Locator locator, String s) {
      if (s == null) {
         return null;
      } else {
         String prefix = XmlUtil.getPrefix(s);
         String uri = null;
         if (prefix == null) {
            uri = this.getDefaultNamespaceURI();
         } else {
            uri = this.getNamespaceURI(prefix);
            if (uri == null) {
               this.errorReceiver.error(locator, WsdlMessages.PARSING_UNKNOWN_NAMESPACE_PREFIX(prefix));
            }
         }

         return new QName(uri, XmlUtil.getLocalPart(s));
      }
   }

   public void fireIgnoringExtension(Element e, Entity entity) {
      QName name = new QName(e.getNamespaceURI(), e.getLocalName());
      QName parent = entity.getElementName();
      List _targets = null;
      synchronized(this) {
         if (this._listeners != null) {
            _targets = (List)this._listeners.clone();
         }
      }

      if (_targets != null) {
         Iterator iter = _targets.iterator();

         while(iter.hasNext()) {
            ParserListener l = (ParserListener)iter.next();
            l.ignoringExtension(entity, name, parent);
         }
      }

   }

   public void fireDoneParsingEntity(QName element, Entity entity) {
      List _targets = null;
      synchronized(this) {
         if (this._listeners != null) {
            _targets = (List)this._listeners.clone();
         }
      }

      if (_targets != null) {
         Iterator iter = _targets.iterator();

         while(iter.hasNext()) {
            ParserListener l = (ParserListener)iter.next();
            l.doneParsingEntity(element, entity);
         }
      }

   }

   public void pushWSDLLocation() {
      this._wsdlLocation.push();
   }

   public void popWSDLLocation() {
      this._wsdlLocation.pop();
   }

   public void setWSDLLocation(String loc) {
      this._wsdlLocation.setLocation(loc);
   }

   public String getWSDLLocation() {
      return this._wsdlLocation.getLocation();
   }
}
