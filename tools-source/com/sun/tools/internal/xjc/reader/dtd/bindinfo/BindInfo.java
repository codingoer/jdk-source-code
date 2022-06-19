package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.AbortException;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.SchemaCache;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CPluginCustomization;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.tools.internal.xjc.util.ForkContentHandler;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class BindInfo {
   protected final ErrorReceiver errorReceiver;
   final Model model;
   private final String defaultPackage;
   final JCodeModel codeModel;
   final CodeModelClassFactory classFactory;
   private final Element dom;
   private final Map conversions;
   private final Map elements;
   private final Map interfaces;
   private static final String XJC_NS = "http://java.sun.com/xml/ns/jaxb/xjc";
   private static SchemaCache bindingFileSchema = new SchemaCache(BindInfo.class.getResource("bindingfile.xsd"));

   public BindInfo(Model model, InputSource source, ErrorReceiver _errorReceiver) throws AbortException {
      this(model, parse(model, source, _errorReceiver), _errorReceiver);
   }

   public BindInfo(Model model, Document _dom, ErrorReceiver _errorReceiver) {
      this.conversions = new HashMap();
      this.elements = new HashMap();
      this.interfaces = new HashMap();
      this.model = model;
      this.dom = _dom.getDocumentElement();
      this.codeModel = model.codeModel;
      this.errorReceiver = _errorReceiver;
      this.classFactory = new CodeModelClassFactory(_errorReceiver);
      this.defaultPackage = model.options.defaultPackage;
      model.getCustomizations().addAll(this.getGlobalCustomizations());
      Iterator var4 = DOMUtil.getChildElements(this.dom, "element").iterator();

      Element itf;
      while(var4.hasNext()) {
         itf = (Element)var4.next();
         BIElement e = new BIElement(this, itf);
         this.elements.put(e.name(), e);
      }

      BIUserConversion.addBuiltinConversions(this, this.conversions);
      var4 = DOMUtil.getChildElements(this.dom, "conversion").iterator();

      while(var4.hasNext()) {
         itf = (Element)var4.next();
         BIConversion c = new BIUserConversion(this, itf);
         this.conversions.put(c.name(), c);
      }

      var4 = DOMUtil.getChildElements(this.dom, "enumeration").iterator();

      while(var4.hasNext()) {
         itf = (Element)var4.next();
         BIConversion c = BIEnumeration.create(itf, this);
         this.conversions.put(c.name(), c);
      }

      var4 = DOMUtil.getChildElements(this.dom, "interface").iterator();

      while(var4.hasNext()) {
         itf = (Element)var4.next();
         BIInterface c = new BIInterface(itf);
         this.interfaces.put(c.name(), c);
      }

   }

   public Long getSerialVersionUID() {
      Element serial = DOMUtil.getElement(this.dom, "http://java.sun.com/xml/ns/jaxb/xjc", "serializable");
      if (serial == null) {
         return null;
      } else {
         String v = DOMUtil.getAttribute(serial, "uid");
         if (v == null) {
            v = "1";
         }

         return new Long(v);
      }
   }

   public JClass getSuperClass() {
      Element sc = DOMUtil.getElement(this.dom, "http://java.sun.com/xml/ns/jaxb/xjc", "superClass");
      if (sc == null) {
         return null;
      } else {
         JDefinedClass c;
         try {
            String v = DOMUtil.getAttribute(sc, "name");
            if (v == null) {
               return null;
            }

            c = this.codeModel._class(v);
            c.hide();
         } catch (JClassAlreadyExistsException var4) {
            c = var4.getExistingClass();
         }

         return c;
      }
   }

   public JClass getSuperInterface() {
      Element sc = DOMUtil.getElement(this.dom, "http://java.sun.com/xml/ns/jaxb/xjc", "superInterface");
      if (sc == null) {
         return null;
      } else {
         String name = DOMUtil.getAttribute(sc, "name");
         if (name == null) {
            return null;
         } else {
            JDefinedClass c;
            try {
               c = this.codeModel._class(name, ClassType.INTERFACE);
               c.hide();
            } catch (JClassAlreadyExistsException var5) {
               c = var5.getExistingClass();
            }

            return c;
         }
      }
   }

   public JPackage getTargetPackage() {
      if (this.model.options.defaultPackage != null) {
         return this.codeModel._package(this.model.options.defaultPackage);
      } else {
         String p;
         if (this.defaultPackage != null) {
            p = this.defaultPackage;
         } else {
            p = this.getOption("package", "");
         }

         return this.codeModel._package(p);
      }
   }

   public BIConversion conversion(String name) {
      BIConversion r = (BIConversion)this.conversions.get(name);
      if (r == null) {
         throw new AssertionError("undefined conversion name: this should be checked by the validator before we read it");
      } else {
         return r;
      }
   }

   public BIElement element(String name) {
      return (BIElement)this.elements.get(name);
   }

   public Collection elements() {
      return this.elements.values();
   }

   public Collection interfaces() {
      return this.interfaces.values();
   }

   private CCustomizations getGlobalCustomizations() {
      CCustomizations r = null;
      Iterator var2 = DOMUtil.getChildElements(this.dom).iterator();

      while(var2.hasNext()) {
         Element e = (Element)var2.next();
         if (this.model.options.pluginURIs.contains(e.getNamespaceURI())) {
            if (r == null) {
               r = new CCustomizations();
            }

            r.add(new CPluginCustomization(e, DOMLocator.getLocationInfo(e)));
         }
      }

      if (r == null) {
         r = CCustomizations.EMPTY;
      }

      return new CCustomizations(r);
   }

   private String getOption(String attName, String defaultValue) {
      Element opt = DOMUtil.getElement(this.dom, "options");
      if (opt != null) {
         String s = DOMUtil.getAttribute(opt, attName);
         if (s != null) {
            return s;
         }
      }

      return defaultValue;
   }

   private static Document parse(Model model, InputSource is, ErrorReceiver receiver) throws AbortException {
      try {
         ValidatorHandler validator = bindingFileSchema.newValidator();
         SAXParserFactory pf = XmlFactory.createParserFactory(model.options.disableXmlSecurity);
         DocumentBuilderFactory domFactory = XmlFactory.createDocumentBuilderFactory(model.options.disableXmlSecurity);
         DOMBuilder builder = new DOMBuilder(domFactory);
         ErrorReceiverFilter controller = new ErrorReceiverFilter(receiver);
         validator.setErrorHandler(controller);
         XMLReader reader = pf.newSAXParser().getXMLReader();
         reader.setErrorHandler(controller);
         DTDExtensionBindingChecker checker = new DTDExtensionBindingChecker("", model.options, controller);
         checker.setContentHandler(validator);
         reader.setContentHandler(new ForkContentHandler(checker, builder));
         reader.parse(is);
         if (controller.hadError()) {
            throw new AbortException();
         }

         return (Document)builder.getDOM();
      } catch (IOException var10) {
         receiver.error((SAXParseException)(new SAXParseException2(var10.getMessage(), (Locator)null, var10)));
      } catch (SAXException var11) {
         receiver.error((SAXParseException)(new SAXParseException2(var11.getMessage(), (Locator)null, var11)));
      } catch (ParserConfigurationException var12) {
         receiver.error((SAXParseException)(new SAXParseException2(var12.getMessage(), (Locator)null, var12)));
      }

      throw new AbortException();
   }
}
