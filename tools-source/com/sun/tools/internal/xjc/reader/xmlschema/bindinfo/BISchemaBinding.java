package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "schemaBindings"
)
public final class BISchemaBinding extends AbstractDeclarationImpl {
   @XmlElement
   private NameRules nameXmlTransform = new NameRules();
   @XmlElement(
      name = "package"
   )
   private PackageInfo packageInfo = new PackageInfo();
   @XmlAttribute(
      name = "map"
   )
   public boolean map = true;
   private static final NamingRule defaultNamingRule = new NamingRule("", "");
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "schemaBinding");

   public String mangleClassName(String name, XSComponent cmp) {
      if (cmp instanceof XSType) {
         return this.nameXmlTransform.typeName.mangle(name);
      } else if (cmp instanceof XSElementDecl) {
         return this.nameXmlTransform.elementName.mangle(name);
      } else if (cmp instanceof XSAttributeDecl) {
         return this.nameXmlTransform.attributeName.mangle(name);
      } else {
         return !(cmp instanceof XSModelGroup) && !(cmp instanceof XSModelGroupDecl) ? name : this.nameXmlTransform.modelGroupName.mangle(name);
      }
   }

   public String mangleAnonymousTypeClassName(String name) {
      return this.nameXmlTransform.anonymousTypeName.mangle(name);
   }

   public String getPackageName() {
      return this.packageInfo.name;
   }

   public String getJavadoc() {
      return this.packageInfo.javadoc;
   }

   public QName getName() {
      return NAME;
   }

   public static final class NamingRule {
      @XmlAttribute
      private String prefix = "";
      @XmlAttribute
      private String suffix = "";

      public NamingRule(String _prefix, String _suffix) {
         this.prefix = _prefix;
         this.suffix = _suffix;
      }

      public NamingRule() {
      }

      public String mangle(String originalName) {
         return this.prefix + originalName + this.suffix;
      }
   }

   private static final class PackageInfo {
      @XmlAttribute
      String name;
      @XmlElement
      String javadoc;

      private PackageInfo() {
      }

      // $FF: synthetic method
      PackageInfo(Object x0) {
         this();
      }
   }

   @XmlType(
      propOrder = {}
   )
   private static final class NameRules {
      @XmlElement
      NamingRule typeName;
      @XmlElement
      NamingRule elementName;
      @XmlElement
      NamingRule attributeName;
      @XmlElement
      NamingRule modelGroupName;
      @XmlElement
      NamingRule anonymousTypeName;

      private NameRules() {
         this.typeName = BISchemaBinding.defaultNamingRule;
         this.elementName = BISchemaBinding.defaultNamingRule;
         this.attributeName = BISchemaBinding.defaultNamingRule;
         this.modelGroupName = BISchemaBinding.defaultNamingRule;
         this.anonymousTypeName = BISchemaBinding.defaultNamingRule;
      }

      // $FF: synthetic method
      NameRules(Object x0) {
         this();
      }
   }
}
