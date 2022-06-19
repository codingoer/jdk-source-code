package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSType;

public class TypeClosure extends TypeSet {
   private final TypeSet typeSet;

   public TypeClosure(TypeSet typeSet) {
      this.typeSet = typeSet;
   }

   public boolean contains(XSType type) {
      if (this.typeSet.contains(type)) {
         return true;
      } else {
         XSType baseType = type.getBaseType();
         return baseType == null ? false : this.contains(baseType);
      }
   }
}
