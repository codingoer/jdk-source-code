package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.generator.bean.ImplStructureStrategy;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.SimpleTypeBuilder;
import com.sun.tools.internal.xjc.util.ReadOnlyAdapter;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "globalBindings"
)
public final class BIGlobalBinding extends AbstractDeclarationImpl {
   @XmlTransient
   public NameConverter nameConverter;
   @XmlAttribute(
      name = "enableJavaNamingConventions"
   )
   boolean isJavaNamingConventionEnabled;
   @XmlAttribute(
      name = "mapSimpleTypeDef"
   )
   boolean simpleTypeSubstitution;
   @XmlTransient
   private BIProperty defaultProperty;
   @XmlAttribute
   private boolean fixedAttributeAsConstantProperty;
   @XmlAttribute
   private CollectionTypeAttribute collectionType;
   @XmlAttribute(
      name = "typesafeEnumMemberName"
   )
   EnumMemberMode generateEnumMemberName;
   @XmlAttribute(
      name = "generateValueClass"
   )
   ImplStructureStrategy codeGenerationStrategy;
   @XmlAttribute(
      name = "typesafeEnumBase"
   )
   private Set enumBaseTypes;
   @XmlElement
   private BISerializable serializable;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   ClassNameBean superClass;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   ClassNameBean superInterface;
   @XmlElement(
      name = "simple",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String simpleMode;
   @XmlElement(
      name = "treatRestrictionLikeNewType",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String treatRestrictionLikeNewType;
   @XmlAttribute
   boolean generateElementClass;
   @XmlAttribute
   boolean generateMixedExtensions;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   Boolean generateElementProperty;
   @XmlAttribute
   boolean choiceContentProperty;
   @XmlAttribute
   OptionalPropertyMode optionalProperty;
   @XmlAttribute(
      name = "typesafeEnumMaxMembers"
   )
   int defaultEnumMemberSizeCap;
   @XmlAttribute(
      name = "localScoping"
   )
   LocalScoping flattenClasses;
   @XmlTransient
   private final Map globalConversions;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String noMarshaller;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String noUnmarshaller;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String noValidator;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   String noValidatingUnmarshaller;
   @XmlElement(
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   TypeSubstitutionElement typeSubstitution;
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "globalBindings");

   @XmlAttribute
   void setUnderscoreBinding(UnderscoreBinding ub) {
      this.nameConverter = ub.nc;
   }

   UnderscoreBinding getUnderscoreBinding() {
      throw new IllegalStateException();
   }

   public JDefinedClass getSuperClass() {
      return this.superClass == null ? null : this.superClass.getClazz(ClassType.CLASS);
   }

   public JDefinedClass getSuperInterface() {
      return this.superInterface == null ? null : this.superInterface.getClazz(ClassType.INTERFACE);
   }

   public BIProperty getDefaultProperty() {
      return this.defaultProperty;
   }

   public boolean isJavaNamingConventionEnabled() {
      return this.isJavaNamingConventionEnabled;
   }

   public BISerializable getSerializable() {
      return this.serializable;
   }

   public boolean isGenerateElementClass() {
      return this.generateElementClass;
   }

   public boolean isGenerateMixedExtensions() {
      return this.generateMixedExtensions;
   }

   public boolean isChoiceContentPropertyEnabled() {
      return this.choiceContentProperty;
   }

   public int getDefaultEnumMemberSizeCap() {
      return this.defaultEnumMemberSizeCap;
   }

   public boolean isSimpleMode() {
      return this.simpleMode != null;
   }

   public boolean isRestrictionFreshType() {
      return this.treatRestrictionLikeNewType != null;
   }

   public EnumMemberMode getEnumMemberMode() {
      return this.generateEnumMemberName;
   }

   public boolean isSimpleTypeSubstitution() {
      return this.simpleTypeSubstitution;
   }

   public ImplStructureStrategy getCodeGenerationStrategy() {
      return this.codeGenerationStrategy;
   }

   public LocalScoping getFlattenClasses() {
      return this.flattenClasses;
   }

   public void errorCheck() {
      ErrorReceiver er = (ErrorReceiver)Ring.get(ErrorReceiver.class);
      Iterator var2 = this.enumBaseTypes.iterator();

      while(var2.hasNext()) {
         QName n = (QName)var2.next();
         XSSchemaSet xs = (XSSchemaSet)Ring.get(XSSchemaSet.class);
         XSSimpleType st = xs.getSimpleType(n.getNamespaceURI(), n.getLocalPart());
         if (st == null) {
            er.error(this.loc, Messages.ERR_UNDEFINED_SIMPLE_TYPE.format(n));
         } else if (!SimpleTypeBuilder.canBeMappedToTypeSafeEnum(st)) {
            er.error(this.loc, Messages.ERR_CANNOT_BE_BOUND_TO_SIMPLETYPE.format(n));
         }
      }

   }

   @XmlAttribute
   void setGenerateIsSetMethod(boolean b) {
      this.optionalProperty = b ? OptionalPropertyMode.ISSET : OptionalPropertyMode.WRAPPER;
   }

   @XmlAttribute(
      name = "generateElementProperty"
   )
   private void setGenerateElementPropertyStd(boolean value) {
      this.generateElementProperty = value;
   }

   @XmlElement(
      name = "javaType"
   )
   private void setGlobalConversions(GlobalStandardConversion[] convs) {
      GlobalStandardConversion[] var2 = convs;
      int var3 = convs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GlobalStandardConversion u = var2[var4];
         this.globalConversions.put(u.xmlType, u);
      }

   }

   @XmlElement(
      name = "javaType",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   private void setGlobalConversions2(GlobalVendorConversion[] convs) {
      GlobalVendorConversion[] var2 = convs;
      int var3 = convs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GlobalVendorConversion u = var2[var4];
         this.globalConversions.put(u.xmlType, u);
      }

   }

   @XmlElement(
      name = "serializable",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   void setXjcSerializable(BISerializable s) {
      this.serializable = s;
   }

   public void onSetOwner() {
      super.onSetOwner();
      NameConverter nc = ((Model)Ring.get(Model.class)).options.getNameConverter();
      if (nc != null) {
         this.nameConverter = nc;
      }

   }

   public BIGlobalBinding() {
      this.nameConverter = NameConverter.standard;
      this.isJavaNamingConventionEnabled = true;
      this.simpleTypeSubstitution = false;
      this.fixedAttributeAsConstantProperty = false;
      this.collectionType = new CollectionTypeAttribute();
      this.generateEnumMemberName = EnumMemberMode.SKIP;
      this.codeGenerationStrategy = ImplStructureStrategy.BEAN_ONLY;
      this.serializable = null;
      this.superClass = null;
      this.superInterface = null;
      this.simpleMode = null;
      this.treatRestrictionLikeNewType = null;
      this.generateElementClass = false;
      this.generateMixedExtensions = false;
      this.generateElementProperty = null;
      this.choiceContentProperty = false;
      this.optionalProperty = OptionalPropertyMode.WRAPPER;
      this.defaultEnumMemberSizeCap = 256;
      this.flattenClasses = LocalScoping.NESTED;
      this.globalConversions = new HashMap();
      this.noMarshaller = null;
      this.noUnmarshaller = null;
      this.noValidator = null;
      this.noValidatingUnmarshaller = null;
      this.typeSubstitution = null;
   }

   public void setParent(BindInfo parent) {
      super.setParent(parent);
      if (this.enumBaseTypes == null) {
         this.enumBaseTypes = Collections.singleton(new QName("http://www.w3.org/2001/XMLSchema", "string"));
      }

      this.defaultProperty = new BIProperty(this.getLocation(), (String)null, (String)null, (BIProperty.BaseTypeBean)null, this.collectionType, this.fixedAttributeAsConstantProperty, this.optionalProperty, this.generateElementProperty);
      this.defaultProperty.setParent(parent);
   }

   public void dispatchGlobalConversions(XSSchemaSet schema) {
      Iterator var2 = this.globalConversions.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry e = (Map.Entry)var2.next();
         QName name = (QName)e.getKey();
         BIConversion conv = (BIConversion)e.getValue();
         XSSimpleType st = schema.getSimpleType(name.getNamespaceURI(), name.getLocalPart());
         if (st == null) {
            ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(this.getLocation(), Messages.ERR_UNDEFINED_SIMPLE_TYPE.format(name));
         } else {
            this.getBuilder().getOrCreateBindInfo(st).addDecl(conv);
         }
      }

   }

   public boolean canBeMappedToTypeSafeEnum(QName typeName) {
      return this.enumBaseTypes.contains(typeName);
   }

   public boolean canBeMappedToTypeSafeEnum(String nsUri, String localName) {
      return this.canBeMappedToTypeSafeEnum(new QName(nsUri, localName));
   }

   public boolean canBeMappedToTypeSafeEnum(XSDeclaration decl) {
      return this.canBeMappedToTypeSafeEnum(decl.getTargetNamespace(), decl.getName());
   }

   public QName getName() {
      return NAME;
   }

   public boolean isEqual(BIGlobalBinding b) {
      boolean equal = this.isJavaNamingConventionEnabled == b.isJavaNamingConventionEnabled && this.simpleTypeSubstitution == b.simpleTypeSubstitution && this.fixedAttributeAsConstantProperty == b.fixedAttributeAsConstantProperty && this.generateEnumMemberName == b.generateEnumMemberName && this.codeGenerationStrategy == b.codeGenerationStrategy && this.serializable == b.serializable && this.superClass == b.superClass && this.superInterface == b.superInterface && this.generateElementClass == b.generateElementClass && this.generateMixedExtensions == b.generateMixedExtensions && this.generateElementProperty == b.generateElementProperty && this.choiceContentProperty == b.choiceContentProperty && this.optionalProperty == b.optionalProperty && this.defaultEnumMemberSizeCap == b.defaultEnumMemberSizeCap && this.flattenClasses == b.flattenClasses;
      if (!equal) {
         return false;
      } else {
         return this.isEqual(this.nameConverter, b.nameConverter) && this.isEqual(this.noMarshaller, b.noMarshaller) && this.isEqual(this.noUnmarshaller, b.noUnmarshaller) && this.isEqual(this.noValidator, b.noValidator) && this.isEqual(this.noValidatingUnmarshaller, b.noValidatingUnmarshaller) && this.isEqual(this.typeSubstitution, b.typeSubstitution) && this.isEqual(this.simpleMode, b.simpleMode) && this.isEqual(this.enumBaseTypes, b.enumBaseTypes) && this.isEqual(this.treatRestrictionLikeNewType, b.treatRestrictionLikeNewType) && this.isEqual(this.globalConversions, b.globalConversions);
      }
   }

   private boolean isEqual(Object a, Object b) {
      if (a != null) {
         return a.equals(b);
      } else {
         return b == null;
      }
   }

   static final class GlobalVendorConversion extends BIConversion.UserAdapter {
      @XmlAttribute
      QName xmlType;

      public boolean equals(Object obj) {
         return obj instanceof GlobalVendorConversion ? ((GlobalVendorConversion)obj).xmlType.equals(this.xmlType) : false;
      }

      public int hashCode() {
         int hash = 7;
         hash = 73 * hash + (this.xmlType != null ? this.xmlType.hashCode() : 0);
         return hash;
      }
   }

   static final class GlobalStandardConversion extends BIConversion.User {
      @XmlAttribute
      QName xmlType;

      public boolean equals(Object obj) {
         return obj instanceof GlobalStandardConversion ? ((GlobalStandardConversion)obj).xmlType.equals(this.xmlType) : false;
      }

      public int hashCode() {
         int hash = 7;
         hash = 73 * hash + (this.xmlType != null ? this.xmlType.hashCode() : 0);
         return hash;
      }
   }

   static final class ClassNameAdapter extends ReadOnlyAdapter {
      public String unmarshal(ClassNameBean bean) throws Exception {
         return bean.name;
      }
   }

   static final class ClassNameBean {
      @XmlAttribute(
         required = true
      )
      String name;
      @XmlTransient
      JDefinedClass clazz;

      JDefinedClass getClazz(ClassType t) {
         if (this.clazz != null) {
            return this.clazz;
         } else {
            try {
               JCodeModel codeModel = (JCodeModel)Ring.get(JCodeModel.class);
               this.clazz = codeModel._class(this.name, t);
               this.clazz.hide();
               return this.clazz;
            } catch (JClassAlreadyExistsException var3) {
               return var3.getExistingClass();
            }
         }
      }
   }

   private static final class TypeSubstitutionElement {
      @XmlAttribute
      String type;
   }

   private static enum UnderscoreBinding {
      @XmlEnumValue("asWordSeparator")
      WORD_SEPARATOR(NameConverter.standard),
      @XmlEnumValue("asCharInWord")
      CHAR_IN_WORD(NameConverter.jaxrpcCompatible);

      final NameConverter nc;

      private UnderscoreBinding(NameConverter nc) {
         this.nc = nc;
      }
   }
}
