package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.nc.NameClass;

public abstract class DXmlTokenPattern extends DUnaryPattern {
   private final NameClass name;

   public DXmlTokenPattern(NameClass name) {
      this.name = name;
   }

   public NameClass getName() {
      return this.name;
   }

   public final boolean isNullable() {
      return false;
   }
}
