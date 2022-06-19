package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIInlineBinaryData;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XmlString;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CElementInfo extends AbstractCElement implements ElementInfo, NType, CClassInfoParent {
   private final QName tagName;
   private NType type;
   private String className;
   public final CClassInfoParent parent;
   private CElementInfo substitutionHead;
   private Set substitutionMembers;
   private final Model model;
   private CElementPropertyInfo property;
   @Nullable
   private String squeezedName;

   public CElementInfo(Model model, QName tagName, CClassInfoParent parent, TypeUse contentType, XmlString defaultValue, XSElementDecl source, CCustomizations customizations, Locator location) {
      super(model, source, location, customizations);
      this.tagName = tagName;
      this.model = model;
      this.parent = parent;
      if (contentType != null) {
         this.initContentType(contentType, source, defaultValue);
      }

      model.add(this);
   }

   public CElementInfo(Model model, QName tagName, CClassInfoParent parent, String className, CCustomizations customizations, Locator location) {
      this(model, tagName, parent, (TypeUse)null, (XmlString)null, (XSElementDecl)null, customizations, location);
      this.className = className;
   }

   public void initContentType(TypeUse contentType, @Nullable XSElementDecl source, XmlString defaultValue) {
      assert this.property == null;

      this.property = new CElementPropertyInfo("Value", contentType.isCollection() ? CElementPropertyInfo.CollectionMode.REPEATED_VALUE : CElementPropertyInfo.CollectionMode.NOT_REPEATED, contentType.idUse(), contentType.getExpectedMimeType(), source, (CCustomizations)null, this.getLocator(), true);
      this.property.setAdapter(contentType.getAdapterUse());
      BIInlineBinaryData.handle(source, this.property);
      this.property.getTypes().add(new CTypeRef(contentType.getInfo(), this.tagName, CTypeRef.getSimpleTypeName(source), true, defaultValue));
      this.type = NavigatorImpl.createParameterizedType(NavigatorImpl.theInstance.ref(JAXBElement.class), this.getContentInMemoryType());
      BIFactoryMethod factoryMethod = (BIFactoryMethod)((BGMBuilder)Ring.get(BGMBuilder.class)).getBindInfo(source).get(BIFactoryMethod.class);
      if (factoryMethod != null) {
         factoryMethod.markAsAcknowledged();
         this.squeezedName = factoryMethod.name;
      }

   }

   public final String getDefaultValue() {
      return ((CTypeRef)this.getProperty().getTypes().get(0)).getDefaultValue();
   }

   public final JPackage _package() {
      return this.parent.getOwnerPackage();
   }

   public CNonElement getContentType() {
      return (CNonElement)this.getProperty().ref().get(0);
   }

   public NType getContentInMemoryType() {
      if (this.getProperty().getAdapter() == null) {
         NType itemType = (NType)this.getContentType().getType();
         return !this.property.isCollection() ? itemType : NavigatorImpl.createParameterizedType(List.class, itemType);
      } else {
         return (NType)this.getProperty().getAdapter().customType;
      }
   }

   public CElementPropertyInfo getProperty() {
      return this.property;
   }

   public CClassInfo getScope() {
      return this.parent instanceof CClassInfo ? (CClassInfo)this.parent : null;
   }

   /** @deprecated */
   public NType getType() {
      return this;
   }

   public QName getElementName() {
      return this.tagName;
   }

   public JType toType(Outline o, Aspect aspect) {
      return (JType)(this.className == null ? this.type.toType(o, aspect) : o.getElement(this).implClass);
   }

   @XmlElement
   public String getSqueezedName() {
      if (this.squeezedName != null) {
         return this.squeezedName;
      } else {
         StringBuilder b = new StringBuilder();
         CClassInfo s = this.getScope();
         if (s != null) {
            b.append(s.getSqueezedName());
         }

         if (this.className != null) {
            b.append(this.className);
         } else {
            b.append(this.model.getNameConverter().toClassName(this.tagName.getLocalPart()));
         }

         return b.toString();
      }
   }

   public CElementInfo getSubstitutionHead() {
      return this.substitutionHead;
   }

   public Collection getSubstitutionMembers() {
      return (Collection)(this.substitutionMembers == null ? Collections.emptyList() : this.substitutionMembers);
   }

   public void setSubstitutionHead(CElementInfo substitutionHead) {
      assert this.substitutionHead == null;

      assert substitutionHead != null;

      this.substitutionHead = substitutionHead;
      if (substitutionHead.substitutionMembers == null) {
         substitutionHead.substitutionMembers = new HashSet();
      }

      substitutionHead.substitutionMembers.add(this);
   }

   public boolean isBoxedType() {
      return false;
   }

   public String fullName() {
      if (this.className == null) {
         return this.type.fullName();
      } else {
         String r = this.parent.fullName();
         return r.length() == 0 ? this.className : r + '.' + this.className;
      }
   }

   public Object accept(CClassInfoParent.Visitor visitor) {
      return visitor.onElement(this);
   }

   public JPackage getOwnerPackage() {
      return this.parent.getOwnerPackage();
   }

   public String shortName() {
      return this.className;
   }

   public boolean hasClass() {
      return this.className != null;
   }
}
