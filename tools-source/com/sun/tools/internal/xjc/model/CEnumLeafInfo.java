package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JExpression;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XmlString;
import java.util.Collection;
import java.util.Iterator;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CEnumLeafInfo implements EnumLeafInfo, NClass, CNonElement {
   public final Model model;
   public final CClassInfoParent parent;
   public final String shortName;
   private final QName typeName;
   private final XSComponent source;
   public final CNonElement base;
   public final Collection members;
   private final CCustomizations customizations;
   private final Locator sourceLocator;
   public String javadoc;

   public CEnumLeafInfo(Model model, QName typeName, CClassInfoParent container, String shortName, CNonElement base, Collection _members, XSComponent source, CCustomizations customizations, Locator _sourceLocator) {
      this.model = model;
      this.parent = container;
      this.shortName = model.allocator.assignClassName(this.parent, shortName);
      this.base = base;
      this.members = _members;
      this.source = source;
      if (customizations == null) {
         customizations = CCustomizations.EMPTY;
      }

      this.customizations = customizations;
      this.sourceLocator = _sourceLocator;
      this.typeName = typeName;
      Iterator var10 = this.members.iterator();

      while(var10.hasNext()) {
         CEnumConstant mem = (CEnumConstant)var10.next();
         mem.setParent(this);
      }

      model.add(this);
   }

   public Locator getLocator() {
      return this.sourceLocator;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public NType getType() {
      return this;
   }

   /** @deprecated */
   public boolean canBeReferencedByIDREF() {
      return false;
   }

   public boolean isElement() {
      return false;
   }

   public QName getElementName() {
      return null;
   }

   public Element asElement() {
      return null;
   }

   public NClass getClazz() {
      return this;
   }

   public XSComponent getSchemaComponent() {
      return this.source;
   }

   public JClass toType(Outline o, Aspect aspect) {
      return o.getEnum(this).clazz;
   }

   public boolean isAbstract() {
      return false;
   }

   public boolean isBoxedType() {
      return false;
   }

   public String fullName() {
      return this.parent.fullName() + '.' + this.shortName;
   }

   public boolean isPrimitive() {
      return false;
   }

   public boolean isSimpleType() {
      return true;
   }

   public boolean needsValueField() {
      Iterator var1 = this.members.iterator();

      CEnumConstant cec;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         cec = (CEnumConstant)var1.next();
      } while(cec.getName().equals(cec.getLexicalValue()));

      return true;
   }

   public JExpression createConstant(Outline outline, XmlString literal) {
      JClass type = this.toType(outline, Aspect.EXPOSED);
      Iterator var4 = this.members.iterator();

      CEnumConstant mem;
      do {
         if (!var4.hasNext()) {
            return null;
         }

         mem = (CEnumConstant)var4.next();
      } while(!mem.getLexicalValue().equals(literal.value));

      return type.staticRef(mem.getName());
   }

   /** @deprecated */
   @Deprecated
   public boolean isCollection() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public CAdapter getAdapterUse() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public CNonElement getInfo() {
      return this;
   }

   public ID idUse() {
      return ID.NONE;
   }

   public MimeType getExpectedMimeType() {
      return null;
   }

   public Collection getConstants() {
      return this.members;
   }

   public NonElement getBaseType() {
      return this.base;
   }

   public CCustomizations getCustomizations() {
      return this.customizations;
   }

   public Locatable getUpstream() {
      throw new UnsupportedOperationException();
   }

   public Location getLocation() {
      throw new UnsupportedOperationException();
   }
}
