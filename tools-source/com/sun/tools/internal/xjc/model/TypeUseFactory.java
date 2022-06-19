package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javax.activation.MimeType;

public final class TypeUseFactory {
   private TypeUseFactory() {
   }

   public static TypeUse makeID(TypeUse t, ID id) {
      if (t.idUse() != ID.NONE) {
         throw new IllegalStateException();
      } else {
         return new TypeUseImpl(t.getInfo(), t.isCollection(), id, t.getExpectedMimeType(), t.getAdapterUse());
      }
   }

   public static TypeUse makeMimeTyped(TypeUse t, MimeType mt) {
      if (t.getExpectedMimeType() != null) {
         throw new IllegalStateException();
      } else {
         return new TypeUseImpl(t.getInfo(), t.isCollection(), t.idUse(), mt, t.getAdapterUse());
      }
   }

   public static TypeUse makeCollection(TypeUse t) {
      if (t.isCollection()) {
         return t;
      } else {
         CAdapter au = t.getAdapterUse();
         if (au != null && !au.isWhitespaceAdapter()) {
            TODO.checkSpec();
            return CBuiltinLeafInfo.STRING_LIST;
         } else {
            return new TypeUseImpl(t.getInfo(), true, t.idUse(), t.getExpectedMimeType(), (CAdapter)null);
         }
      }
   }

   public static TypeUse adapt(TypeUse t, CAdapter adapter) {
      assert t.getAdapterUse() == null;

      return new TypeUseImpl(t.getInfo(), t.isCollection(), t.idUse(), t.getExpectedMimeType(), adapter);
   }

   public static TypeUse adapt(TypeUse t, Class adapter, boolean copy) {
      return adapt(t, new CAdapter(adapter, copy));
   }
}
