package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import java.lang.reflect.Type;

class EagerNType implements NType {
   final Type t;

   public EagerNType(Type type) {
      this.t = type;

      assert this.t != null;

   }

   public JType toType(Outline o, Aspect aspect) {
      try {
         return o.getCodeModel().parseType(this.t.toString());
      } catch (ClassNotFoundException var4) {
         throw new NoClassDefFoundError(var4.getMessage());
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof EagerNType)) {
         return false;
      } else {
         EagerNType eagerNType = (EagerNType)o;
         return this.t.equals(eagerNType.t);
      }
   }

   public boolean isBoxedType() {
      return false;
   }

   public int hashCode() {
      return this.t.hashCode();
   }

   public String fullName() {
      return Utils.REFLECTION_NAVIGATOR.getTypeName(this.t);
   }
}
