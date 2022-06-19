package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.api.ClassNameAllocator;
import com.sun.tools.internal.xjc.generator.bean.BeanGenerator;
import com.sun.tools.internal.xjc.generator.bean.ImplStructureStrategy;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.xmlschema.Messages;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.util.FlattenIterator;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSSchemaSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class Model implements TypeInfoSet, CCustomizable {
   private final Map beans = new LinkedHashMap();
   private final Map enums = new LinkedHashMap();
   private final Map elementMappings = new HashMap();
   private final Iterable allElements = new Iterable() {
      public Iterator iterator() {
         return new FlattenIterator(Model.this.elementMappings.values());
      }
   };
   private final Map typeUses = new LinkedHashMap();
   private NameConverter nameConverter;
   CCustomizations customizations;
   private boolean packageLevelAnnotations = true;
   public final XSSchemaSet schemaComponent;
   private CCustomizations gloablCustomizations = new CCustomizations();
   @XmlTransient
   public final JCodeModel codeModel;
   public final Options options;
   @XmlAttribute
   public boolean serializable;
   @XmlAttribute
   public Long serialVersionUID;
   @XmlTransient
   public JClass rootClass;
   @XmlTransient
   public JClass rootInterface;
   public ImplStructureStrategy strategy;
   final ClassNameAllocatorWrapper allocator;
   @XmlTransient
   public final SymbolSpace defaultSymbolSpace;
   private final Map symbolSpaces;
   private final Map cache;
   static final Locator EMPTY_LOCATOR;

   public Model(Options opts, JCodeModel cm, NameConverter nc, ClassNameAllocator allocator, XSSchemaSet schemaComponent) {
      this.strategy = ImplStructureStrategy.BEAN_ONLY;
      this.symbolSpaces = new HashMap();
      this.cache = new HashMap();
      this.options = opts;
      this.codeModel = cm;
      this.nameConverter = nc;
      this.defaultSymbolSpace = new SymbolSpace(this.codeModel);
      this.defaultSymbolSpace.setType(this.codeModel.ref(Object.class));
      this.elementMappings.put((Object)null, new HashMap());
      if (opts.automaticNameConflictResolution) {
         allocator = new AutoClassNameAllocator((ClassNameAllocator)allocator);
      }

      this.allocator = new ClassNameAllocatorWrapper((ClassNameAllocator)allocator);
      this.schemaComponent = schemaComponent;
      this.gloablCustomizations.setParent(this, this);
   }

   public void setNameConverter(NameConverter nameConverter) {
      assert this.nameConverter == null;

      assert nameConverter != null;

      this.nameConverter = nameConverter;
   }

   public final NameConverter getNameConverter() {
      return this.nameConverter;
   }

   public boolean isPackageLevelAnnotations() {
      return this.packageLevelAnnotations;
   }

   public void setPackageLevelAnnotations(boolean packageLevelAnnotations) {
      this.packageLevelAnnotations = packageLevelAnnotations;
   }

   public SymbolSpace getSymbolSpace(String name) {
      SymbolSpace ss = (SymbolSpace)this.symbolSpaces.get(name);
      if (ss == null) {
         this.symbolSpaces.put(name, ss = new SymbolSpace(this.codeModel));
      }

      return ss;
   }

   public Outline generateCode(Options opt, ErrorReceiver receiver) {
      ErrorReceiverFilter ehf = new ErrorReceiverFilter(receiver);
      Outline o = BeanGenerator.generate(this, ehf);

      try {
         Iterator var5 = opt.activePlugins.iterator();

         while(var5.hasNext()) {
            Plugin ma = (Plugin)var5.next();
            ma.run(o, opt, ehf);
         }
      } catch (SAXException var9) {
         return null;
      }

      Set check = new HashSet();

      for(CCustomizations c = this.customizations; c != null; c = c.next) {
         if (!check.add(c)) {
            throw new AssertionError();
         }

         Iterator var7 = c.iterator();

         while(var7.hasNext()) {
            CPluginCustomization p = (CPluginCustomization)var7.next();
            if (!p.isAcknowledged()) {
               ehf.error(p.locator, Messages.format("UnusedCustomizationChecker.UnacknolwedgedCustomization", p.element.getNodeName()));
               ehf.error(c.getOwner().getLocator(), Messages.format("UnusedCustomizationChecker.UnacknolwedgedCustomization.Relevant"));
            }
         }
      }

      if (ehf.hadError()) {
         o = null;
      }

      return o;
   }

   public final Map createTopLevelBindings() {
      Map r = new HashMap();
      Iterator var2 = this.beans().values().iterator();

      while(var2.hasNext()) {
         CClassInfo b = (CClassInfo)var2.next();
         if (b.isElement()) {
            r.put(b.getElementName(), b);
         }
      }

      return r;
   }

   public Navigator getNavigator() {
      return NavigatorImpl.theInstance;
   }

   public CNonElement getTypeInfo(NType type) {
      CBuiltinLeafInfo leaf = (CBuiltinLeafInfo)CBuiltinLeafInfo.LEAVES.get(type);
      return (CNonElement)(leaf != null ? leaf : this.getClassInfo((NClass)this.getNavigator().asDecl(type)));
   }

   public CBuiltinLeafInfo getAnyTypeInfo() {
      return CBuiltinLeafInfo.ANYTYPE;
   }

   public CNonElement getTypeInfo(Ref ref) {
      assert !ref.valueList;

      return this.getTypeInfo((NType)ref.type);
   }

   public Map beans() {
      return this.beans;
   }

   public Map enums() {
      return this.enums;
   }

   public Map typeUses() {
      return this.typeUses;
   }

   public Map arrays() {
      return Collections.emptyMap();
   }

   public Map builtins() {
      return CBuiltinLeafInfo.LEAVES;
   }

   public CClassInfo getClassInfo(NClass t) {
      return (CClassInfo)this.beans.get(t);
   }

   public CElementInfo getElementInfo(NClass scope, QName name) {
      Map m = (Map)this.elementMappings.get(scope);
      if (m != null) {
         CElementInfo r = (CElementInfo)m.get(name);
         if (r != null) {
            return r;
         }
      }

      return (CElementInfo)((Map)this.elementMappings.get((Object)null)).get(name);
   }

   public Map getElementMappings(NClass scope) {
      return (Map)this.elementMappings.get(scope);
   }

   public Iterable getAllElements() {
      return this.allElements;
   }

   /** @deprecated */
   public XSComponent getSchemaComponent() {
      return null;
   }

   /** @deprecated */
   public Locator getLocator() {
      LocatorImpl r = new LocatorImpl();
      r.setLineNumber(-1);
      r.setColumnNumber(-1);
      return r;
   }

   public CCustomizations getCustomizations() {
      return this.gloablCustomizations;
   }

   public Map getXmlNs(String namespaceUri) {
      return Collections.emptyMap();
   }

   public Map getSchemaLocations() {
      return Collections.emptyMap();
   }

   public XmlNsForm getElementFormDefault(String nsUri) {
      throw new UnsupportedOperationException();
   }

   public XmlNsForm getAttributeFormDefault(String nsUri) {
      throw new UnsupportedOperationException();
   }

   public void dump(Result out) {
      throw new UnsupportedOperationException();
   }

   void add(CEnumLeafInfo e) {
      this.enums.put(e.getClazz(), e);
   }

   void add(CClassInfo ci) {
      this.beans.put(ci.getClazz(), ci);
   }

   void add(CElementInfo ei) {
      NClass clazz = null;
      if (ei.getScope() != null) {
         clazz = ei.getScope().getClazz();
      }

      Map m = (Map)this.elementMappings.get(clazz);
      if (m == null) {
         this.elementMappings.put(clazz, m = new HashMap());
      }

      ((Map)m).put(ei.getElementName(), ei);
   }

   public CClassInfoParent.Package getPackage(JPackage pkg) {
      CClassInfoParent.Package r = (CClassInfoParent.Package)this.cache.get(pkg);
      if (r == null) {
         this.cache.put(pkg, r = new CClassInfoParent.Package(pkg));
      }

      return r;
   }

   static {
      LocatorImpl l = new LocatorImpl();
      l.setColumnNumber(-1);
      l.setLineNumber(-1);
      EMPTY_LOCATOR = l;
   }
}
