package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.tools.internal.xjc.generator.bean.field.IsSetFieldRenderer;
import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CValuePropertyInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.TypeUtil;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.util.XSFinder;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

@XmlRootElement(
   name = "property"
)
public final class BIProperty extends AbstractDeclarationImpl {
   @XmlAttribute
   private String name = null;
   @XmlElement
   private String javadoc = null;
   @XmlElement
   private BaseTypeBean baseType = null;
   @XmlAttribute
   private boolean generateFailFastSetterMethod = false;
   @XmlAttribute
   private CollectionTypeAttribute collectionType = null;
   @XmlAttribute
   private OptionalPropertyMode optionalProperty = null;
   @XmlAttribute
   private Boolean generateElementProperty = null;
   @XmlAttribute(
      name = "fixedAttributeAsConstantProperty"
   )
   private Boolean isConstantProperty;
   private final XSFinder hasFixedValue = new XSFinder() {
      public Boolean attributeDecl(XSAttributeDecl decl) {
         return decl.getFixedValue() != null;
      }

      public Boolean attributeUse(XSAttributeUse use) {
         return use.getFixedValue() != null;
      }

      public Boolean schema(XSSchema s) {
         return true;
      }
   };
   private static final XSFunction defaultCustomizationFinder = new XSFunction() {
      public XSComponent attributeUse(XSAttributeUse use) {
         return use.getDecl();
      }

      public XSComponent particle(XSParticle particle) {
         return particle.getTerm();
      }

      public XSComponent schema(XSSchema schema) {
         return null;
      }

      public XSComponent attributeDecl(XSAttributeDecl decl) {
         return decl.getOwnerSchema();
      }

      public XSComponent wildcard(XSWildcard wc) {
         return wc.getOwnerSchema();
      }

      public XSComponent modelGroupDecl(XSModelGroupDecl decl) {
         return decl.getOwnerSchema();
      }

      public XSComponent modelGroup(XSModelGroup group) {
         return group.getOwnerSchema();
      }

      public XSComponent elementDecl(XSElementDecl decl) {
         return decl.getOwnerSchema();
      }

      public XSComponent complexType(XSComplexType type) {
         return type.getOwnerSchema();
      }

      public XSComponent simpleType(XSSimpleType st) {
         return st.getOwnerSchema();
      }

      public XSComponent attGroupDecl(XSAttGroupDecl decl) {
         throw new IllegalStateException();
      }

      public XSComponent empty(XSContentType empty) {
         throw new IllegalStateException();
      }

      public XSComponent annotation(XSAnnotation xsAnnotation) {
         throw new IllegalStateException();
      }

      public XSComponent facet(XSFacet xsFacet) {
         throw new IllegalStateException();
      }

      public XSComponent notation(XSNotation xsNotation) {
         throw new IllegalStateException();
      }

      public XSComponent identityConstraint(XSIdentityConstraint x) {
         throw new IllegalStateException();
      }

      public XSComponent xpath(XSXPath xsxPath) {
         throw new IllegalStateException();
      }
   };
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "property");

   public BIProperty(Locator loc, String _propName, String _javadoc, BaseTypeBean _baseType, CollectionTypeAttribute collectionType, Boolean isConst, OptionalPropertyMode optionalProperty, Boolean genElemProp) {
      super(loc);
      this.name = _propName;
      this.javadoc = _javadoc;
      this.baseType = _baseType;
      this.collectionType = collectionType;
      this.isConstantProperty = isConst;
      this.optionalProperty = optionalProperty;
      this.generateElementProperty = genElemProp;
   }

   protected BIProperty() {
   }

   public Collection getChildren() {
      BIConversion conv = this.getConv();
      return (Collection)(conv == null ? super.getChildren() : Collections.singleton(conv));
   }

   public void setParent(BindInfo parent) {
      super.setParent(parent);
      if (this.baseType != null && this.baseType.conv != null) {
         this.baseType.conv.setParent(parent);
      }

   }

   public String getPropertyName(boolean forConstant) {
      if (this.name != null) {
         BIGlobalBinding gb = this.getBuilder().getGlobalBinding();
         NameConverter nc = this.getBuilder().model.getNameConverter();
         return gb.isJavaNamingConventionEnabled() && !forConstant ? nc.toPropertyName(this.name) : this.name;
      } else {
         BIProperty next = this.getDefault();
         return next != null ? next.getPropertyName(forConstant) : null;
      }
   }

   public String getJavadoc() {
      return this.javadoc;
   }

   public JType getBaseType() {
      if (this.baseType != null && this.baseType.name != null) {
         return TypeUtil.getType(this.getCodeModel(), this.baseType.name, (ErrorReceiver)Ring.get(ErrorReceiver.class), this.getLocation());
      } else {
         BIProperty next = this.getDefault();
         return next != null ? next.getBaseType() : null;
      }
   }

   CollectionTypeAttribute getCollectionType() {
      return this.collectionType != null ? this.collectionType : this.getDefault().getCollectionType();
   }

   @XmlAttribute
   void setGenerateIsSetMethod(boolean b) {
      this.optionalProperty = b ? OptionalPropertyMode.ISSET : OptionalPropertyMode.WRAPPER;
   }

   public OptionalPropertyMode getOptionalPropertyMode() {
      return this.optionalProperty != null ? this.optionalProperty : this.getDefault().getOptionalPropertyMode();
   }

   private Boolean generateElementProperty() {
      if (this.generateElementProperty != null) {
         return this.generateElementProperty;
      } else {
         BIProperty next = this.getDefault();
         return next != null ? next.generateElementProperty() : null;
      }
   }

   public boolean isConstantProperty() {
      if (this.isConstantProperty != null) {
         return this.isConstantProperty;
      } else {
         BIProperty next = this.getDefault();
         if (next != null) {
            return next.isConstantProperty();
         } else {
            throw new AssertionError();
         }
      }
   }

   public CValuePropertyInfo createValueProperty(String defaultName, boolean forConstant, XSComponent source, TypeUse tu, QName typeName) {
      this.markAsAcknowledged();
      this.constantPropertyErrorCheck();
      String name = this.getPropertyName(forConstant);
      if (name == null) {
         name = defaultName;
         if (tu.isCollection() && this.getBuilder().getGlobalBinding().isSimpleMode()) {
            name = JJavaName.getPluralForm(defaultName);
         }
      }

      CValuePropertyInfo prop = (CValuePropertyInfo)this.wrapUp(new CValuePropertyInfo(name, source, this.getCustomizations(source), source.getLocator(), tu, typeName), source);
      BIInlineBinaryData.handle(source, prop);
      return prop;
   }

   public CAttributePropertyInfo createAttributeProperty(XSAttributeUse use, TypeUse tu) {
      boolean forConstant = getCustomization(use).isConstantProperty() && use.getFixedValue() != null;
      String name = this.getPropertyName(forConstant);
      if (name == null) {
         NameConverter conv = this.getBuilder().getNameConverter();
         if (forConstant) {
            name = conv.toConstantName(use.getDecl().getName());
         } else {
            name = conv.toPropertyName(use.getDecl().getName());
         }

         if (tu.isCollection() && this.getBuilder().getGlobalBinding().isSimpleMode()) {
            name = JJavaName.getPluralForm(name);
         }
      }

      this.markAsAcknowledged();
      this.constantPropertyErrorCheck();
      return (CAttributePropertyInfo)this.wrapUp(new CAttributePropertyInfo(name, use, this.getCustomizations(use), use.getLocator(), BGMBuilder.getName(use.getDecl()), tu, BGMBuilder.getName(use.getDecl().getType()), use.isRequired()), use);
   }

   public CElementPropertyInfo createElementProperty(String defaultName, boolean forConstant, XSParticle source, RawTypeSet types) {
      if (!types.refs.isEmpty()) {
         this.markAsAcknowledged();
      }

      this.constantPropertyErrorCheck();
      String name = this.getPropertyName(forConstant);
      if (name == null) {
         name = defaultName;
      }

      CElementPropertyInfo prop = (CElementPropertyInfo)this.wrapUp(new CElementPropertyInfo(name, types.getCollectionMode(), types.id(), types.getExpectedMimeType(), source, this.getCustomizations(source), source.getLocator(), types.isRequired()), source);
      types.addTo(prop);
      BIInlineBinaryData.handle(source.getTerm(), prop);
      return prop;
   }

   public CReferencePropertyInfo createDummyExtendedMixedReferenceProperty(String defaultName, XSComponent source, RawTypeSet types) {
      return this.createReferenceProperty(defaultName, false, source, types, true, true, false, true);
   }

   public CReferencePropertyInfo createContentExtendedMixedReferenceProperty(String defaultName, XSComponent source, RawTypeSet types) {
      return this.createReferenceProperty(defaultName, false, source, types, true, false, true, true);
   }

   public CReferencePropertyInfo createReferenceProperty(String defaultName, boolean forConstant, XSComponent source, RawTypeSet types, boolean isMixed, boolean dummy, boolean content, boolean isMixedExtended) {
      if (types == null) {
         content = true;
      } else if (!types.refs.isEmpty()) {
         this.markAsAcknowledged();
      }

      this.constantPropertyErrorCheck();
      String name = this.getPropertyName(forConstant);
      if (name == null) {
         name = defaultName;
      }

      CReferencePropertyInfo prop = (CReferencePropertyInfo)this.wrapUp(new CReferencePropertyInfo(name, types == null ? true : types.getCollectionMode().isRepeated() || isMixed, types == null ? false : types.isRequired(), isMixed, source, this.getCustomizations(source), source.getLocator(), dummy, content, isMixedExtended), source);
      if (types != null) {
         types.addTo(prop);
      }

      BIInlineBinaryData.handle(source, prop);
      return prop;
   }

   public CPropertyInfo createElementOrReferenceProperty(String defaultName, boolean forConstant, XSParticle source, RawTypeSet types) {
      boolean generateRef;
      switch (types.canBeTypeRefs) {
         case CAN_BE_TYPEREF:
         case SHOULD_BE_TYPEREF:
            Boolean b = this.generateElementProperty();
            if (b == null) {
               generateRef = types.canBeTypeRefs == RawTypeSet.Mode.CAN_BE_TYPEREF;
            } else {
               generateRef = b;
            }
            break;
         case MUST_BE_REFERENCE:
            generateRef = true;
            break;
         default:
            throw new AssertionError();
      }

      return (CPropertyInfo)(generateRef ? this.createReferenceProperty(defaultName, forConstant, source, types, false, false, false, false) : this.createElementProperty(defaultName, forConstant, source, types));
   }

   private CPropertyInfo wrapUp(CPropertyInfo prop, XSComponent source) {
      prop.javadoc = concat(this.javadoc, this.getBuilder().getBindInfo(source).getDocumentation());
      if (prop.javadoc == null) {
         prop.javadoc = "";
      }

      OptionalPropertyMode opm = this.getOptionalPropertyMode();
      Object r;
      if (prop.isCollection()) {
         CollectionTypeAttribute ct = this.getCollectionType();
         r = ct.get(this.getBuilder().model);
      } else {
         FieldRendererFactory frf = this.getBuilder().fieldRendererFactory;
         if (prop.isOptionalPrimitive()) {
            switch (opm) {
               case PRIMITIVE:
                  r = frf.getRequiredUnboxed();
                  break;
               case WRAPPER:
                  r = frf.getSingle();
                  break;
               case ISSET:
                  r = frf.getSinglePrimitiveAccess();
                  break;
               default:
                  throw new Error();
            }
         } else {
            r = frf.getDefault();
         }
      }

      if (opm == OptionalPropertyMode.ISSET) {
         r = new IsSetFieldRenderer((FieldRenderer)r, prop.isOptionalPrimitive() || prop.isCollection(), true);
      }

      prop.realization = (FieldRenderer)r;
      JType bt = this.getBaseType();
      if (bt != null) {
         prop.baseType = bt;
      }

      return prop;
   }

   private CCustomizations getCustomizations(XSComponent src) {
      return this.getBuilder().getBindInfo(src).toCustomizationList();
   }

   private CCustomizations getCustomizations(XSComponent... src) {
      CCustomizations c = null;
      XSComponent[] var3 = src;
      int var4 = src.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         XSComponent s = var3[var5];
         CCustomizations r = this.getCustomizations(s);
         if (c == null) {
            c = r;
         } else {
            c = CCustomizations.merge(c, r);
         }
      }

      return c;
   }

   private CCustomizations getCustomizations(XSAttributeUse src) {
      return src.getDecl().isLocal() ? this.getCustomizations(src, src.getDecl()) : this.getCustomizations((XSComponent)src);
   }

   private CCustomizations getCustomizations(XSParticle src) {
      if (src.getTerm().isElementDecl()) {
         XSElementDecl xed = src.getTerm().asElementDecl();
         if (xed.isGlobal()) {
            return this.getCustomizations((XSComponent)src);
         }
      }

      return this.getCustomizations(src, src.getTerm());
   }

   public void markAsAcknowledged() {
      if (!this.isAcknowledged()) {
         super.markAsAcknowledged();
         BIProperty def = this.getDefault();
         if (def != null) {
            def.markAsAcknowledged();
         }

      }
   }

   private void constantPropertyErrorCheck() {
      if (this.isConstantProperty != null && this.getOwner() != null && !this.hasFixedValue.find(this.getOwner())) {
         ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(this.getLocation(), Messages.ERR_ILLEGAL_FIXEDATTR.format());
         this.isConstantProperty = null;
      }

   }

   protected BIProperty getDefault() {
      if (this.getOwner() == null) {
         return null;
      } else {
         BIProperty next = getDefault(this.getBuilder(), this.getOwner());
         return next == this ? null : next;
      }
   }

   private static BIProperty getDefault(BGMBuilder builder, XSComponent c) {
      while(true) {
         if (c != null) {
            c = (XSComponent)c.apply(defaultCustomizationFinder);
            if (c == null) {
               continue;
            }

            BIProperty prop = (BIProperty)builder.getBindInfo(c).get(BIProperty.class);
            if (prop == null) {
               continue;
            }

            return prop;
         }

         return builder.getGlobalBinding().getDefaultProperty();
      }
   }

   public static BIProperty getCustomization(XSComponent c) {
      BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
      if (c != null) {
         BIProperty prop = (BIProperty)builder.getBindInfo(c).get(BIProperty.class);
         if (prop != null) {
            return prop;
         }
      }

      return getDefault(builder, c);
   }

   private static String concat(String s1, String s2) {
      if (s1 == null) {
         return s2;
      } else {
         return s2 == null ? s1 : s1 + "\n\n" + s2;
      }
   }

   public QName getName() {
      return NAME;
   }

   public BIConversion getConv() {
      return this.baseType != null ? this.baseType.conv : null;
   }

   private static final class BaseTypeBean {
      @XmlElementRef
      BIConversion conv;
      @XmlAttribute
      String name;
   }
}
