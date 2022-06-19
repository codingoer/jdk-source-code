package com.sun.tools.internal.xjc.model;

import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XmlString;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

public final class CTypeRef implements TypeRef {
   @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
   private final CNonElement type;
   private final QName elementName;
   @Nullable
   final QName typeName;
   private final boolean nillable;
   public final XmlString defaultValue;

   public CTypeRef(CNonElement type, XSElementDecl decl) {
      this(type, BGMBuilder.getName(decl), getSimpleTypeName(decl), decl.isNillable(), decl.getDefaultValue());
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public static QName getSimpleTypeName(XSElementDecl decl) {
      return decl != null && decl.getType().isSimpleType() ? resolveSimpleTypeName(decl.getType()) : null;
   }

   private static QName resolveSimpleTypeName(XSType declType) {
      QName name = BGMBuilder.getName(declType);
      QName result = null;
      if (name != null && !"http://www.w3.org/2001/XMLSchema".equals(name.getNamespaceURI())) {
         result = resolveSimpleTypeName(declType.getBaseType());
      } else if (!"anySimpleType".equals(declType.getName())) {
         result = name;
      }

      return result;
   }

   public CTypeRef(CNonElement type, QName elementName, QName typeName, boolean nillable, XmlString defaultValue) {
      assert type != null;

      assert elementName != null;

      this.type = type;
      this.elementName = elementName;
      this.typeName = typeName;
      this.nillable = nillable;
      this.defaultValue = defaultValue;
   }

   public CNonElement getTarget() {
      return this.type;
   }

   public QName getTagName() {
      return this.elementName;
   }

   public boolean isNillable() {
      return this.nillable;
   }

   public String getDefaultValue() {
      return this.defaultValue != null ? this.defaultValue.value : null;
   }

   public boolean isLeaf() {
      throw new UnsupportedOperationException();
   }

   public PropertyInfo getSource() {
      throw new UnsupportedOperationException();
   }
}
