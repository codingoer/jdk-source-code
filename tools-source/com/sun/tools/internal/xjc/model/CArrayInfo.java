package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.xsom.XSComponent;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CArrayInfo extends AbstractCTypeInfoImpl implements ArrayInfo, CNonElement, NType {
   private final CNonElement itemType;
   private final QName typeName;

   public CArrayInfo(Model model, CNonElement itemType, XSComponent source, CCustomizations customizations) {
      super(model, source, customizations);
      this.itemType = itemType;

      assert itemType.getTypeName() != null;

      this.typeName = ArrayInfoUtil.calcArrayTypeName(itemType.getTypeName());
   }

   public CNonElement getItemType() {
      return this.itemType;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public boolean isSimpleType() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public CNonElement getInfo() {
      return this;
   }

   public JType toType(Outline o, Aspect aspect) {
      return this.itemType.toType(o, aspect).array();
   }

   public NType getType() {
      return this;
   }

   public boolean isBoxedType() {
      return false;
   }

   public String fullName() {
      return ((NType)this.itemType.getType()).fullName() + "[]";
   }

   public Locator getLocator() {
      return Model.EMPTY_LOCATOR;
   }
}
