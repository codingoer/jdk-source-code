package com.sun.xml.internal.xsom;

public final class XSVariety {
   public static final XSVariety ATOMIC = new XSVariety("atomic");
   public static final XSVariety UNION = new XSVariety("union");
   public static final XSVariety LIST = new XSVariety("list");
   private final String name;

   private XSVariety(String _name) {
      this.name = _name;
   }

   public String toString() {
      return this.name;
   }
}
