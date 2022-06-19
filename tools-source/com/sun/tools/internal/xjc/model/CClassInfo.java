package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;
import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.Language;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CClassInfo extends AbstractCElement implements ClassInfo, CClassInfoParent, CClass, NClass {
   @XmlIDREF
   private CClass baseClass;
   private CClassInfo firstSubclass;
   private CClassInfo nextSibling;
   private final QName typeName;
   @Nullable
   private String squeezedName;
   @Nullable
   private final QName elementName;
   private boolean isOrdered;
   private final List properties;
   public String javadoc;
   @XmlIDREF
   private final CClassInfoParent parent;
   public final String shortName;
   @Nullable
   private String implClass;
   public final Model model;
   private boolean hasAttributeWildcard;
   private static final CClassInfoParent.Visitor calcSqueezedName = new CClassInfoParent.Visitor() {
      public String onBean(CClassInfo bean) {
         return (String)bean.parent.accept(this) + bean.shortName;
      }

      public String onElement(CElementInfo element) {
         return (String)element.parent.accept(this) + element.shortName();
      }

      public String onPackage(JPackage pkg) {
         return "";
      }
   };
   private Set _implements;
   private final List constructors;

   public CClassInfo(Model model, JPackage pkg, String shortName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
      this(model, (CClassInfoParent)model.getPackage(pkg), shortName, location, typeName, elementName, source, customizations);
   }

   public CClassInfo(Model model, CClassInfoParent p, String shortName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
      super(model, source, location, customizations);
      this.nextSibling = null;
      this.isOrdered = true;
      this.properties = new ArrayList();
      this._implements = null;
      this.constructors = new ArrayList(1);
      this.model = model;
      this.parent = p;
      this.shortName = model.allocator.assignClassName(this.parent, shortName);
      this.typeName = typeName;
      this.elementName = elementName;
      Language schemaLanguage = model.options.getSchemaLanguage();
      if (schemaLanguage != null && (schemaLanguage.equals(Language.XMLSCHEMA) || schemaLanguage.equals(Language.WSDL))) {
         BIFactoryMethod factoryMethod = (BIFactoryMethod)((BGMBuilder)Ring.get(BGMBuilder.class)).getBindInfo(source).get(BIFactoryMethod.class);
         if (factoryMethod != null) {
            factoryMethod.markAsAcknowledged();
            this.squeezedName = factoryMethod.name;
         }
      }

      model.add(this);
   }

   public CClassInfo(Model model, JCodeModel cm, String fullName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
      super(model, source, location, customizations);
      this.nextSibling = null;
      this.isOrdered = true;
      this.properties = new ArrayList();
      this._implements = null;
      this.constructors = new ArrayList(1);
      this.model = model;
      int idx = fullName.indexOf(46);
      if (idx < 0) {
         this.parent = model.getPackage(cm.rootPackage());
         this.shortName = model.allocator.assignClassName(this.parent, fullName);
      } else {
         this.parent = model.getPackage(cm._package(fullName.substring(0, idx)));
         this.shortName = model.allocator.assignClassName(this.parent, fullName.substring(idx + 1));
      }

      this.typeName = typeName;
      this.elementName = elementName;
      model.add(this);
   }

   public boolean hasAttributeWildcard() {
      return this.hasAttributeWildcard;
   }

   public void hasAttributeWildcard(boolean hasAttributeWildcard) {
      this.hasAttributeWildcard = hasAttributeWildcard;
   }

   public boolean hasSubClasses() {
      return this.firstSubclass != null;
   }

   public boolean declaresAttributeWildcard() {
      return this.hasAttributeWildcard && !this.inheritsAttributeWildcard();
   }

   public boolean inheritsAttributeWildcard() {
      if (this.getRefBaseClass() != null) {
         CClassRef cref = (CClassRef)this.baseClass;
         if (cref.getSchemaComponent().getForeignAttributes().size() > 0) {
            return true;
         }
      } else {
         for(CClassInfo c = this.getBaseClass(); c != null; c = c.getBaseClass()) {
            if (c.hasAttributeWildcard) {
               return true;
            }
         }
      }

      return false;
   }

   public NClass getClazz() {
      return this;
   }

   public CClassInfo getScope() {
      return null;
   }

   @XmlID
   public String getName() {
      return this.fullName();
   }

   @XmlElement
   public String getSqueezedName() {
      return this.squeezedName != null ? this.squeezedName : (String)calcSqueezedName.onBean(this);
   }

   public List getProperties() {
      return this.properties;
   }

   public boolean hasValueProperty() {
      throw new UnsupportedOperationException();
   }

   public CPropertyInfo getProperty(String name) {
      Iterator var2 = this.properties.iterator();

      CPropertyInfo p;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         p = (CPropertyInfo)var2.next();
      } while(!p.getName(false).equals(name));

      return p;
   }

   public boolean hasProperties() {
      return !this.getProperties().isEmpty();
   }

   public boolean isElement() {
      return this.elementName != null;
   }

   /** @deprecated */
   @Deprecated
   public CNonElement getInfo() {
      return this;
   }

   public Element asElement() {
      return this.isElement() ? this : null;
   }

   public boolean isOrdered() {
      return this.isOrdered;
   }

   /** @deprecated */
   public boolean isFinal() {
      return false;
   }

   public void setOrdered(boolean value) {
      this.isOrdered = value;
   }

   public QName getElementName() {
      return this.elementName;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public boolean isSimpleType() {
      throw new UnsupportedOperationException();
   }

   public String fullName() {
      String r = this.parent.fullName();
      return r.length() == 0 ? this.shortName : r + '.' + this.shortName;
   }

   public CClassInfoParent parent() {
      return this.parent;
   }

   public void setUserSpecifiedImplClass(String implClass) {
      assert this.implClass == null;

      assert implClass != null;

      this.implClass = implClass;
   }

   public String getUserSpecifiedImplClass() {
      return this.implClass;
   }

   public void addProperty(CPropertyInfo prop) {
      if (!prop.ref().isEmpty()) {
         prop.setParent(this);
         this.properties.add(prop);
      }
   }

   public void setBaseClass(CClass base) {
      assert this.baseClass == null;

      assert base != null;

      this.baseClass = base;

      assert this.nextSibling == null;

      if (base instanceof CClassInfo) {
         CClassInfo realBase = (CClassInfo)base;
         this.nextSibling = realBase.firstSubclass;
         realBase.firstSubclass = this;
      }

   }

   public CClassInfo getBaseClass() {
      return this.baseClass instanceof CClassInfo ? (CClassInfo)this.baseClass : null;
   }

   public CClassRef getRefBaseClass() {
      return this.baseClass instanceof CClassRef ? (CClassRef)this.baseClass : null;
   }

   public Iterator listSubclasses() {
      return new Iterator() {
         CClassInfo cur;

         {
            this.cur = CClassInfo.this.firstSubclass;
         }

         public boolean hasNext() {
            return this.cur != null;
         }

         public CClassInfo next() {
            CClassInfo r = this.cur;
            this.cur = this.cur.nextSibling;
            return r;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public CClassInfo getSubstitutionHead() {
      CClassInfo c;
      for(c = this.getBaseClass(); c != null && !c.isElement(); c = c.getBaseClass()) {
      }

      return c;
   }

   public void _implements(JClass c) {
      if (this._implements == null) {
         this._implements = new HashSet();
      }

      this._implements.add(c);
   }

   public void addConstructor(String... fieldNames) {
      this.constructors.add(new Constructor(fieldNames));
   }

   public Collection getConstructors() {
      return this.constructors;
   }

   public final Object accept(CClassInfoParent.Visitor visitor) {
      return visitor.onBean(this);
   }

   public JPackage getOwnerPackage() {
      return this.parent.getOwnerPackage();
   }

   public final NClass getType() {
      return this;
   }

   public final JClass toType(Outline o, Aspect aspect) {
      switch (aspect) {
         case IMPLEMENTATION:
            return o.getClazz(this).implRef;
         case EXPOSED:
            return o.getClazz(this).ref;
         default:
            throw new IllegalStateException();
      }
   }

   public boolean isBoxedType() {
      return false;
   }

   public String toString() {
      return this.fullName();
   }
}
