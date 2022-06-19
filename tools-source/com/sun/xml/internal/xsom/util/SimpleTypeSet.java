package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSType;
import java.util.Set;

public class SimpleTypeSet extends TypeSet {
   private final Set typeSet;

   public SimpleTypeSet(Set s) {
      this.typeSet = s;
   }

   public boolean contains(XSType type) {
      return this.typeSet.contains(type);
   }
}
