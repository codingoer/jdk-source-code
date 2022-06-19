package com.sun.tools.internal.xjc.reader;

import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.Multiplicity;
import com.sun.xml.internal.bind.v2.model.core.ID;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.activation.MimeType;

public final class RawTypeSet {
   public final Set refs;
   public final Mode canBeTypeRefs;
   public final Multiplicity mul;
   private CElementPropertyInfo.CollectionMode collectionMode;

   public RawTypeSet(Set refs, Multiplicity m) {
      this.refs = refs;
      this.mul = m;
      this.canBeTypeRefs = this.canBeTypeRefs();
   }

   public CElementPropertyInfo.CollectionMode getCollectionMode() {
      return this.collectionMode;
   }

   public boolean isRequired() {
      return this.mul.min.compareTo(BigInteger.ZERO) == 1;
   }

   private Mode canBeTypeRefs() {
      Set types = new HashSet();
      this.collectionMode = this.mul.isAtMostOnce() ? CElementPropertyInfo.CollectionMode.NOT_REPEATED : CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT;
      Mode mode = RawTypeSet.Mode.SHOULD_BE_TYPEREF;
      Iterator var3 = this.refs.iterator();

      while(var3.hasNext()) {
         Ref r = (Ref)var3.next();
         mode = mode.or(r.canBeType(this));
         if (mode == RawTypeSet.Mode.MUST_BE_REFERENCE) {
            return mode;
         }

         if (!types.add(r.toTypeRef((CElementPropertyInfo)null).getTarget().getType())) {
            return RawTypeSet.Mode.MUST_BE_REFERENCE;
         }

         if (r.isListOfValues()) {
            if (this.refs.size() > 1 || !this.mul.isAtMostOnce()) {
               return RawTypeSet.Mode.MUST_BE_REFERENCE;
            }

            this.collectionMode = CElementPropertyInfo.CollectionMode.REPEATED_VALUE;
         }
      }

      return mode;
   }

   public void addTo(CElementPropertyInfo prop) {
      assert this.canBeTypeRefs != RawTypeSet.Mode.MUST_BE_REFERENCE;

      if (!this.mul.isZero()) {
         List dst = prop.getTypes();
         Iterator var3 = this.refs.iterator();

         while(var3.hasNext()) {
            Ref t = (Ref)var3.next();
            dst.add(t.toTypeRef(prop));
         }

      }
   }

   public void addTo(CReferencePropertyInfo prop) {
      if (!this.mul.isZero()) {
         Iterator var2 = this.refs.iterator();

         while(var2.hasNext()) {
            Ref t = (Ref)var2.next();
            t.toElementRef(prop);
         }

      }
   }

   public ID id() {
      Iterator var1 = this.refs.iterator();

      ID id;
      do {
         if (!var1.hasNext()) {
            return ID.NONE;
         }

         Ref t = (Ref)var1.next();
         id = t.id();
      } while(id == ID.NONE);

      return id;
   }

   public MimeType getExpectedMimeType() {
      Iterator var1 = this.refs.iterator();

      MimeType mt;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         Ref t = (Ref)var1.next();
         mt = t.getExpectedMimeType();
      } while(mt == null);

      return mt;
   }

   public abstract static class Ref {
      protected abstract CTypeRef toTypeRef(CElementPropertyInfo var1);

      protected abstract void toElementRef(CReferencePropertyInfo var1);

      protected abstract Mode canBeType(RawTypeSet var1);

      protected abstract boolean isListOfValues();

      protected abstract ID id();

      protected MimeType getExpectedMimeType() {
         return null;
      }
   }

   public static enum Mode {
      SHOULD_BE_TYPEREF(0),
      CAN_BE_TYPEREF(1),
      MUST_BE_REFERENCE(2);

      private final int rank;

      private Mode(int rank) {
         this.rank = rank;
      }

      Mode or(Mode var1) {
         // $FF: Couldn't be decompiled
      }
   }
}
