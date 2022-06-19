package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.xsom.XSComponent;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public abstract class CPropertyInfo implements PropertyInfo, CCustomizable {
   @XmlTransient
   private CClassInfo parent;
   private String privateName;
   private String publicName;
   private final boolean isCollection;
   @XmlTransient
   public final Locator locator;
   private final XSComponent source;
   public JType baseType;
   public String javadoc = "";
   public boolean inlineBinaryData;
   @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
   public FieldRenderer realization;
   public CDefaultValue defaultValue;
   private final CCustomizations customizations;

   protected CPropertyInfo(String name, boolean collection, XSComponent source, CCustomizations customizations, Locator locator) {
      this.publicName = name;
      String n = null;
      Model m = (Model)Ring.get(Model.class);
      if (m != null) {
         n = m.getNameConverter().toVariableName(name);
      } else {
         n = NameConverter.standard.toVariableName(name);
      }

      if (!JJavaName.isJavaIdentifier(n)) {
         n = '_' + n;
      }

      this.privateName = n;
      this.isCollection = collection;
      this.locator = locator;
      if (customizations == null) {
         this.customizations = CCustomizations.EMPTY;
      } else {
         this.customizations = customizations;
      }

      this.source = source;
   }

   final void setParent(CClassInfo parent) {
      assert this.parent == null;

      assert parent != null;

      this.parent = parent;
      this.customizations.setParent(parent.model, this);
   }

   public CTypeInfo parent() {
      return this.parent;
   }

   public Locator getLocator() {
      return this.locator;
   }

   public final XSComponent getSchemaComponent() {
      return this.source;
   }

   public abstract CAdapter getAdapter();

   /** @deprecated */
   public String getName() {
      return this.getName(false);
   }

   public String getName(boolean isPublic) {
      return isPublic ? this.publicName : this.privateName;
   }

   public void setName(boolean isPublic, String newName) {
      if (isPublic) {
         this.publicName = newName;
      } else {
         this.privateName = newName;
      }

   }

   public String displayName() {
      return this.parent.toString() + '#' + this.getName(false);
   }

   public boolean isCollection() {
      return this.isCollection;
   }

   public abstract Collection ref();

   public boolean isUnboxable() {
      Collection ts = this.ref();
      if (ts.size() != 1) {
         return false;
      } else if (this.baseType != null && this.baseType instanceof JClass) {
         return false;
      } else {
         CTypeInfo t = (CTypeInfo)ts.iterator().next();
         return ((NType)t.getType()).isBoxedType();
      }
   }

   public boolean isOptionalPrimitive() {
      return false;
   }

   public CCustomizations getCustomizations() {
      return this.customizations;
   }

   public boolean inlineBinaryData() {
      return this.inlineBinaryData;
   }

   public abstract Object accept(CPropertyVisitor var1);

   protected static boolean needsExplicitTypeName(TypeUse type, QName typeName) {
      if (typeName == null) {
         return false;
      } else if (!"http://www.w3.org/2001/XMLSchema".equals(typeName.getNamespaceURI())) {
         return false;
      } else if (type.isCollection()) {
         return true;
      } else {
         QName itemType = type.getInfo().getTypeName();
         if (itemType == null) {
            return true;
         } else {
            return !itemType.equals(typeName);
         }
      }
   }

   public QName collectElementNames(Map table) {
      return null;
   }

   public final Annotation readAnnotation(Class annotationType) {
      throw new UnsupportedOperationException();
   }

   public final boolean hasAnnotation(Class annotationType) {
      throw new UnsupportedOperationException();
   }
}
