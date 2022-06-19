package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "typesafeEnumClass"
)
public final class BIEnum extends AbstractDeclarationImpl {
   @XmlAttribute(
      name = "map"
   )
   private boolean map = true;
   @XmlAttribute(
      name = "name"
   )
   public String className = null;
   @XmlAttribute(
      name = "ref"
   )
   public String ref;
   @XmlElement
   public final String javadoc = null;
   @XmlTransient
   public final Map members = new HashMap();
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "enum");

   public boolean isMapped() {
      return this.map;
   }

   public QName getName() {
      return NAME;
   }

   public void setParent(BindInfo p) {
      super.setParent(p);
      Iterator var2 = this.members.values().iterator();

      while(var2.hasNext()) {
         BIEnumMember mem = (BIEnumMember)var2.next();
         mem.setParent(p);
      }

      if (this.ref != null) {
         this.markAsAcknowledged();
      }

   }

   @XmlElement(
      name = "typesafeEnumMember"
   )
   private void setMembers(BIEnumMember2[] mems) {
      BIEnumMember2[] var2 = mems;
      int var3 = mems.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BIEnumMember2 e = var2[var4];
         this.members.put(e.value, e);
      }

   }

   static class BIEnumMember2 extends BIEnumMember {
      @XmlAttribute(
         required = true
      )
      String value;
   }
}
