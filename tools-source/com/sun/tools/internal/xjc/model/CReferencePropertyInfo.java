package com.sun.tools.internal.xjc.model;

import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.activation.MimeType;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CReferencePropertyInfo extends CPropertyInfo implements ReferencePropertyInfo {
   private final boolean required;
   private final Set elements = new HashSet();
   private final boolean isMixed;
   private WildcardMode wildcard;
   private boolean dummy;
   private boolean content;
   private boolean isMixedExtendedCust = false;

   public CReferencePropertyInfo(String name, boolean collection, boolean required, boolean isMixed, XSComponent source, CCustomizations customizations, Locator locator, boolean dummy, boolean content, boolean isMixedExtended) {
      super(name, (collection || isMixed) && !dummy, source, customizations, locator);
      this.isMixed = isMixed;
      this.required = required;
      this.dummy = dummy;
      this.content = content;
      this.isMixedExtendedCust = isMixedExtended;
   }

   public Set ref() {
      final class RefList extends HashSet {
         RefList() {
            super(CReferencePropertyInfo.this.elements.size());
            this.addAll(CReferencePropertyInfo.this.elements);
         }

         public boolean addAll(Collection col) {
            boolean r = false;

            CTypeInfo e;
            for(Iterator var3 = col.iterator(); var3.hasNext(); r |= this.add(e)) {
               e = (CTypeInfo)var3.next();
               if (e instanceof CElementInfo) {
                  r |= this.addAll(((CElementInfo)e).getSubstitutionMembers());
               }
            }

            return r;
         }
      }

      RefList r = new RefList();
      if (this.wildcard != null) {
         if (this.wildcard.allowDom) {
            r.add(CWildcardTypeInfo.INSTANCE);
         }

         if (this.wildcard.allowTypedObject) {
            r.add(CBuiltinLeafInfo.ANYTYPE);
         }
      }

      if (this.isMixed()) {
         r.add(CBuiltinLeafInfo.STRING);
      }

      return r;
   }

   public Set getElements() {
      return this.elements;
   }

   public boolean isMixed() {
      return this.isMixed;
   }

   public boolean isDummy() {
      return this.dummy;
   }

   public boolean isContent() {
      return this.content;
   }

   public boolean isMixedExtendedCust() {
      return this.isMixedExtendedCust;
   }

   /** @deprecated */
   @Deprecated
   public QName getXmlName() {
      return null;
   }

   public boolean isUnboxable() {
      return false;
   }

   public boolean isOptionalPrimitive() {
      return false;
   }

   public Object accept(CPropertyVisitor visitor) {
      return visitor.onReference(this);
   }

   public CAdapter getAdapter() {
      return null;
   }

   public final PropertyKind kind() {
      return PropertyKind.REFERENCE;
   }

   public ID id() {
      return ID.NONE;
   }

   public WildcardMode getWildcard() {
      return this.wildcard;
   }

   public void setWildcard(WildcardMode mode) {
      this.wildcard = mode;
   }

   public NClass getDOMHandler() {
      return this.getWildcard() != null ? NavigatorImpl.create(W3CDomHandler.class) : null;
   }

   public MimeType getExpectedMimeType() {
      return null;
   }

   public boolean isCollectionNillable() {
      return false;
   }

   public boolean isCollectionRequired() {
      return false;
   }

   public QName getSchemaType() {
      return null;
   }

   public boolean isRequired() {
      return this.required;
   }

   public QName collectElementNames(Map table) {
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         CElement e = (CElement)var2.next();
         QName n = e.getElementName();
         if (table.containsKey(n)) {
            return n;
         }

         table.put(n, this);
      }

      return null;
   }
}
