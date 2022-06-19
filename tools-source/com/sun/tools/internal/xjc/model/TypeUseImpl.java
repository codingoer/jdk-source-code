package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JStringLiteral;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.xsom.XmlString;
import javax.activation.MimeType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class TypeUseImpl implements TypeUse {
   private final CNonElement coreType;
   private final boolean collection;
   private final CAdapter adapter;
   private final ID id;
   private final MimeType expectedMimeType;

   public TypeUseImpl(CNonElement itemType, boolean collection, ID id, MimeType expectedMimeType, CAdapter adapter) {
      this.coreType = itemType;
      this.collection = collection;
      this.id = id;
      this.expectedMimeType = expectedMimeType;
      this.adapter = adapter;
   }

   public boolean isCollection() {
      return this.collection;
   }

   public CNonElement getInfo() {
      return this.coreType;
   }

   public CAdapter getAdapterUse() {
      return this.adapter;
   }

   public ID idUse() {
      return this.id;
   }

   public MimeType getExpectedMimeType() {
      return this.expectedMimeType;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof TypeUseImpl)) {
         return false;
      } else {
         TypeUseImpl that = (TypeUseImpl)o;
         if (this.collection != that.collection) {
            return false;
         } else if (this.id != that.id) {
            return false;
         } else {
            if (this.adapter != null) {
               if (!this.adapter.equals(that.adapter)) {
                  return false;
               }
            } else if (that.adapter != null) {
               return false;
            }

            if (this.coreType != null) {
               if (!this.coreType.equals(that.coreType)) {
                  return false;
               }
            } else if (that.coreType != null) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int result = this.coreType != null ? this.coreType.hashCode() : 0;
      result = 29 * result + (this.collection ? 1 : 0);
      result = 29 * result + (this.adapter != null ? this.adapter.hashCode() : 0);
      return result;
   }

   public JExpression createConstant(Outline outline, XmlString lexical) {
      if (this.isCollection()) {
         return null;
      } else if (this.adapter == null) {
         return this.coreType.createConstant(outline, lexical);
      } else {
         JExpression cons = this.coreType.createConstant(outline, lexical);
         Class atype = this.adapter.getAdapterIfKnown();
         if (cons instanceof JStringLiteral && atype != null) {
            JStringLiteral scons = (JStringLiteral)cons;
            XmlAdapter a = (XmlAdapter)ClassFactory.create(atype);

            try {
               Object value = a.unmarshal(scons.str);
               if (value instanceof String) {
                  return JExpr.lit((String)value);
               }
            } catch (Exception var8) {
            }
         }

         return JExpr._new(this.adapter.getAdapterClass(outline)).invoke("unmarshal").arg(cons);
      }
   }
}
