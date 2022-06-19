package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.xsom.XSComponent;
import org.xml.sax.Locator;

public final class CEnumConstant implements EnumConstant, CCustomizable {
   public final String name;
   public final String javadoc;
   private final String lexical;
   private CEnumLeafInfo parent;
   private final XSComponent source;
   private final CCustomizations customizations;
   private final Locator locator;

   public CEnumConstant(String name, String javadoc, String lexical, XSComponent source, CCustomizations customizations, Locator loc) {
      assert name != null;

      this.name = name;
      this.javadoc = javadoc;
      this.lexical = lexical;
      this.source = source;
      this.customizations = customizations;
      this.locator = loc;
   }

   public CEnumLeafInfo getEnclosingClass() {
      return this.parent;
   }

   void setParent(CEnumLeafInfo parent) {
      this.parent = parent;
   }

   public String getLexicalValue() {
      return this.lexical;
   }

   public String getName() {
      return this.name;
   }

   public XSComponent getSchemaComponent() {
      return this.source;
   }

   public CCustomizations getCustomizations() {
      return this.customizations;
   }

   public Locator getLocator() {
      return this.locator;
   }
}
