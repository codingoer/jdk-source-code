package com.sun.tools.internal.xjc.reader.dtd;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.AbortException;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CDefaultValue;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import com.sun.tools.internal.xjc.reader.ModelChecker;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIAttribute;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIElement;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIInterface;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BindInfo;
import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.dtdparser.DTDHandlerBase;
import com.sun.xml.internal.dtdparser.DTDParser;
import com.sun.xml.internal.dtdparser.InputEntity;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XmlString;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.xml.namespace.QName;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class TDTDReader extends DTDHandlerBase {
   private final EntityResolver entityResolver;
   final BindInfo bindInfo;
   final Model model = (Model)Ring.get(Model.class);
   private final CodeModelClassFactory classFactory;
   private final ErrorReceiverFilter errorReceiver;
   private final Map elements = new HashMap();
   private final Stack modelGroups = new Stack();
   private Locator locator;
   private static final Map builtinConversions;

   public static Model parse(InputSource dtd, InputSource bindingInfo, ErrorReceiver errorReceiver, Options opts) {
      try {
         Ring old = Ring.begin();

         Model var10;
         try {
            ErrorReceiverFilter ef = new ErrorReceiverFilter(errorReceiver);
            JCodeModel cm = new JCodeModel();
            Model model = new Model(opts, cm, NameConverter.standard, opts.classNameAllocator, (XSSchemaSet)null);
            Ring.add(cm);
            Ring.add(model);
            Ring.add(ErrorReceiver.class, ef);
            TDTDReader reader = new TDTDReader(ef, opts, bindingInfo);
            DTDParser parser = new DTDParser();
            parser.setDtdHandler(reader);
            if (opts.entityResolver != null) {
               parser.setEntityResolver(opts.entityResolver);
            }

            try {
               parser.parse(dtd);
            } catch (SAXParseException var18) {
               Object var11 = null;
               return (Model)var11;
            }

            ((ModelChecker)Ring.get(ModelChecker.class)).check();
            if (ef.hadError()) {
               var10 = null;
               return var10;
            }

            var10 = model;
         } finally {
            Ring.end(old);
         }

         return var10;
      } catch (IOException var20) {
         errorReceiver.error((SAXParseException)(new SAXParseException2(var20.getMessage(), (Locator)null, var20)));
         return null;
      } catch (SAXException var21) {
         errorReceiver.error((SAXParseException)(new SAXParseException2(var21.getMessage(), (Locator)null, var21)));
         return null;
      } catch (AbortException var22) {
         return null;
      }
   }

   protected TDTDReader(ErrorReceiver errorReceiver, Options opts, InputSource _bindInfo) throws AbortException {
      this.entityResolver = opts.entityResolver;
      this.errorReceiver = new ErrorReceiverFilter(errorReceiver);
      this.bindInfo = new BindInfo(this.model, _bindInfo, this.errorReceiver);
      this.classFactory = new CodeModelClassFactory(errorReceiver);
   }

   public void startDTD(InputEntity entity) throws SAXException {
   }

   public void endDTD() throws SAXException {
      Iterator var1 = this.elements.values().iterator();

      while(var1.hasNext()) {
         Element e = (Element)var1.next();
         e.bind();
      }

      if (!this.errorReceiver.hadError()) {
         this.processInterfaceDeclarations();
         this.model.serialVersionUID = this.bindInfo.getSerialVersionUID();
         if (this.model.serialVersionUID != null) {
            this.model.serializable = true;
         }

         this.model.rootClass = this.bindInfo.getSuperClass();
         this.model.rootInterface = this.bindInfo.getSuperInterface();
         this.processConstructorDeclarations();
      }
   }

   private void processInterfaceDeclarations() {
      Map fromName = new HashMap();
      Map decls = new HashMap();
      Iterator var3 = this.bindInfo.interfaces().iterator();

      while(var3.hasNext()) {
         BIInterface decl = (BIInterface)var3.next();
         final JDefinedClass intf = this.classFactory.createInterface(this.bindInfo.getTargetPackage(), decl.name(), this.copyLocator());
         decls.put(decl, intf);
         fromName.put(decl.name(), new InterfaceAcceptor() {
            public void implement(JClass c) {
               intf._implements(c);
            }
         });
      }

      var3 = this.model.beans().values().iterator();

      while(var3.hasNext()) {
         final CClassInfo ci = (CClassInfo)var3.next();
         fromName.put(ci.getName(), new InterfaceAcceptor() {
            public void implement(JClass c) {
               ci._implements(c);
            }
         });
      }

      var3 = decls.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry e = (Map.Entry)var3.next();
         BIInterface decl = (BIInterface)e.getKey();
         JClass c = (JClass)e.getValue();
         String[] var7 = decl.members();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String member = var7[var9];
            InterfaceAcceptor acc = (InterfaceAcceptor)fromName.get(member);
            if (acc == null) {
               this.error(decl.getSourceLocation(), "TDTDReader.BindInfo.NonExistentInterfaceMember", member);
            } else {
               acc.implement(c);
            }
         }
      }

   }

   JPackage getTargetPackage() {
      return this.bindInfo.getTargetPackage();
   }

   private void processConstructorDeclarations() {
      Iterator var1 = this.bindInfo.elements().iterator();

      while(var1.hasNext()) {
         BIElement decl = (BIElement)var1.next();
         Element e = (Element)this.elements.get(decl.name());
         if (e == null) {
            this.error(decl.getSourceLocation(), "TDTDReader.BindInfo.NonExistentElementDeclaration", decl.name());
         } else if (decl.isClass()) {
            decl.declareConstructors(e.getClassInfo());
         }
      }

   }

   public void attributeDecl(String elementName, String attributeName, String attributeType, String[] enumeration, short attributeUse, String defaultValue) throws SAXException {
      this.getOrCreateElement(elementName).attributes.add(this.createAttribute(elementName, attributeName, attributeType, enumeration, attributeUse, defaultValue));
   }

   protected CPropertyInfo createAttribute(String elementName, String attributeName, String attributeType, String[] enums, short attributeUse, String defaultValue) throws SAXException {
      boolean required = attributeUse == 3;
      BIElement edecl = this.bindInfo.element(elementName);
      BIAttribute decl = null;
      if (edecl != null) {
         decl = edecl.attribute(attributeName);
      }

      String propName;
      if (decl == null) {
         propName = this.model.getNameConverter().toPropertyName(attributeName);
      } else {
         propName = decl.getPropertyName();
      }

      QName qname = new QName("", attributeName);
      TypeUse use;
      if (decl != null && decl.getConversion() != null) {
         use = decl.getConversion().getTransducer();
      } else {
         use = (TypeUse)builtinConversions.get(attributeType);
      }

      CPropertyInfo r = new CAttributePropertyInfo(propName, (XSComponent)null, (CCustomizations)null, this.copyLocator(), qname, use, (QName)null, required);
      if (defaultValue != null) {
         r.defaultValue = CDefaultValue.create(use, new XmlString(defaultValue));
      }

      return r;
   }

   Element getOrCreateElement(String elementName) {
      Element r = (Element)this.elements.get(elementName);
      if (r == null) {
         r = new Element(this, elementName);
         this.elements.put(elementName, r);
      }

      return r;
   }

   public void startContentModel(String elementName, short contentModelType) throws SAXException {
      assert this.modelGroups.isEmpty();

      this.modelGroups.push(new ModelGroup());
   }

   public void endContentModel(String elementName, short contentModelType) throws SAXException {
      assert this.modelGroups.size() == 1;

      Term term = ((ModelGroup)this.modelGroups.pop()).wrapUp();
      Element e = this.getOrCreateElement(elementName);
      e.define(contentModelType, term, this.copyLocator());
   }

   public void startModelGroup() throws SAXException {
      this.modelGroups.push(new ModelGroup());
   }

   public void endModelGroup(short occurence) throws SAXException {
      Term t = Occurence.wrap(((ModelGroup)this.modelGroups.pop()).wrapUp(), occurence);
      ((ModelGroup)this.modelGroups.peek()).addTerm(t);
   }

   public void connector(short connectorType) throws SAXException {
      ((ModelGroup)this.modelGroups.peek()).setKind(connectorType);
   }

   public void childElement(String elementName, short occurence) throws SAXException {
      Element child = this.getOrCreateElement(elementName);
      ((ModelGroup)this.modelGroups.peek()).addTerm(Occurence.wrap(child, occurence));
      child.isReferenced = true;
   }

   public void setDocumentLocator(Locator loc) {
      this.locator = loc;
   }

   private Locator copyLocator() {
      return new LocatorImpl(this.locator);
   }

   public void error(SAXParseException e) throws SAXException {
      this.errorReceiver.error(e);
   }

   public void fatalError(SAXParseException e) throws SAXException {
      this.errorReceiver.fatalError(e);
   }

   public void warning(SAXParseException e) throws SAXException {
      this.errorReceiver.warning(e);
   }

   protected final void error(Locator loc, String prop, Object... args) {
      this.errorReceiver.error(loc, Messages.format(prop, args));
   }

   static {
      Map m = new HashMap();
      m.put("CDATA", CBuiltinLeafInfo.NORMALIZED_STRING);
      m.put("ENTITY", CBuiltinLeafInfo.TOKEN);
      m.put("ENTITIES", CBuiltinLeafInfo.STRING.makeCollection());
      m.put("NMTOKEN", CBuiltinLeafInfo.TOKEN);
      m.put("NMTOKENS", CBuiltinLeafInfo.STRING.makeCollection());
      m.put("ID", CBuiltinLeafInfo.ID);
      m.put("IDREF", CBuiltinLeafInfo.IDREF);
      m.put("IDREFS", TypeUseFactory.makeCollection(CBuiltinLeafInfo.IDREF));
      m.put("ENUMERATION", CBuiltinLeafInfo.TOKEN);
      builtinConversions = Collections.unmodifiableMap(m);
   }

   private interface InterfaceAcceptor {
      void implement(JClass var1);
   }
}
