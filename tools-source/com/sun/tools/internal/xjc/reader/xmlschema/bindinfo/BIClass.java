package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "class"
)
public final class BIClass extends AbstractDeclarationImpl {
   @XmlAttribute(
      name = "name"
   )
   private String className;
   @XmlAttribute(
      name = "implClass"
   )
   private String userSpecifiedImplClass;
   @XmlAttribute(
      name = "ref"
   )
   private String ref;
   @XmlAttribute(
      name = "recursive",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   private String recursive;
   @XmlElement
   private String javadoc;
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "class");

   protected BIClass() {
   }

   @Nullable
   public String getClassName() {
      if (this.className == null) {
         return null;
      } else {
         BIGlobalBinding gb = this.getBuilder().getGlobalBinding();
         NameConverter nc = this.getBuilder().model.getNameConverter();
         return gb.isJavaNamingConventionEnabled() ? nc.toClassName(this.className) : this.className;
      }
   }

   public String getUserSpecifiedImplClass() {
      return this.userSpecifiedImplClass;
   }

   public String getExistingClassRef() {
      return this.ref;
   }

   public String getRecursive() {
      return this.recursive;
   }

   public String getJavadoc() {
      return this.javadoc;
   }

   public QName getName() {
      return NAME;
   }

   public void setParent(BindInfo p) {
      super.setParent(p);
      if (this.ref != null) {
         this.markAsAcknowledged();
      }

   }
}
