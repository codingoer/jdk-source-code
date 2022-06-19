package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CElementPropertyInfo extends CPropertyInfo implements ElementPropertyInfo {
   private final boolean required;
   private final MimeType expectedMimeType;
   private CAdapter adapter;
   private final boolean isValueList;
   private ID id;
   private final List types = new ArrayList();
   private final List ref = new AbstractList() {
      public CNonElement get(int index) {
         return ((CTypeRef)CElementPropertyInfo.this.getTypes().get(index)).getTarget();
      }

      public int size() {
         return CElementPropertyInfo.this.getTypes().size();
      }
   };

   public CElementPropertyInfo(String name, CollectionMode collection, ID id, MimeType expectedMimeType, XSComponent source, CCustomizations customizations, Locator locator, boolean required) {
      super(name, collection.col, source, customizations, locator);
      this.required = required;
      this.id = id;
      this.expectedMimeType = expectedMimeType;
      this.isValueList = collection.val;
   }

   public ID id() {
      return this.id;
   }

   public List getTypes() {
      return this.types;
   }

   public List ref() {
      return this.ref;
   }

   public QName getSchemaType() {
      if (this.types.size() != 1) {
         return null;
      } else {
         CTypeRef t = (CTypeRef)this.types.get(0);
         return needsExplicitTypeName(t.getTarget(), t.typeName) ? t.typeName : null;
      }
   }

   /** @deprecated */
   @Deprecated
   public QName getXmlName() {
      return null;
   }

   public boolean isCollectionRequired() {
      return false;
   }

   public boolean isCollectionNillable() {
      return false;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isValueList() {
      return this.isValueList;
   }

   public boolean isUnboxable() {
      if (!this.isCollection() && !this.required) {
         return false;
      } else {
         Iterator var1 = this.getTypes().iterator();

         CTypeRef t;
         do {
            if (!var1.hasNext()) {
               return super.isUnboxable();
            }

            t = (CTypeRef)var1.next();
         } while(!t.isNillable());

         return false;
      }
   }

   public boolean isOptionalPrimitive() {
      Iterator var1 = this.getTypes().iterator();

      while(var1.hasNext()) {
         CTypeRef t = (CTypeRef)var1.next();
         if (t.isNillable()) {
            return false;
         }
      }

      return !this.isCollection() && !this.required && super.isUnboxable();
   }

   public Object accept(CPropertyVisitor visitor) {
      return visitor.onElement(this);
   }

   public CAdapter getAdapter() {
      return this.adapter;
   }

   public void setAdapter(CAdapter a) {
      assert this.adapter == null;

      this.adapter = a;
   }

   public final PropertyKind kind() {
      return PropertyKind.ELEMENT;
   }

   public MimeType getExpectedMimeType() {
      return this.expectedMimeType;
   }

   public QName collectElementNames(Map table) {
      Iterator var2 = this.types.iterator();

      while(var2.hasNext()) {
         CTypeRef t = (CTypeRef)var2.next();
         QName n = t.getTagName();
         if (table.containsKey(n)) {
            return n;
         }

         table.put(n, this);
      }

      return null;
   }

   public static enum CollectionMode {
      NOT_REPEATED(false, false),
      REPEATED_ELEMENT(true, false),
      REPEATED_VALUE(true, true);

      private final boolean col;
      private final boolean val;

      private CollectionMode(boolean col, boolean val) {
         this.col = col;
         this.val = val;
      }

      public boolean isRepeated() {
         return this.col;
      }
   }
}
