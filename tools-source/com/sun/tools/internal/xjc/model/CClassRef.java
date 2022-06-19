package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIClass;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIEnum;
import com.sun.xml.internal.xsom.XSComponent;
import javax.xml.namespace.QName;

public final class CClassRef extends AbstractCElement implements NClass, CClass {
   private final String fullyQualifiedClassName;
   private JClass clazz;

   public CClassRef(Model model, XSComponent source, BIClass decl, CCustomizations customizations) {
      super(model, source, decl.getLocation(), customizations);
      this.fullyQualifiedClassName = decl.getExistingClassRef();

      assert this.fullyQualifiedClassName != null;

   }

   public CClassRef(Model model, XSComponent source, BIEnum decl, CCustomizations customizations) {
      super(model, source, decl.getLocation(), customizations);
      this.fullyQualifiedClassName = decl.ref;

      assert this.fullyQualifiedClassName != null;

   }

   public void setAbstract() {
   }

   public boolean isAbstract() {
      return false;
   }

   public NType getType() {
      return this;
   }

   public JClass toType(Outline o, Aspect aspect) {
      if (this.clazz == null) {
         this.clazz = o.getCodeModel().ref(this.fullyQualifiedClassName);
      }

      return this.clazz;
   }

   public String fullName() {
      return this.fullyQualifiedClassName;
   }

   public QName getTypeName() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public CNonElement getInfo() {
      return this;
   }

   public CElement getSubstitutionHead() {
      return null;
   }

   public CClassInfo getScope() {
      return null;
   }

   public QName getElementName() {
      return null;
   }

   public boolean isBoxedType() {
      return false;
   }

   public boolean isSimpleType() {
      return false;
   }
}
