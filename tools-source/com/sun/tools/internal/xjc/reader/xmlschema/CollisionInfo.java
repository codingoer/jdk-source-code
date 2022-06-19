package com.sun.tools.internal.xjc.reader.xmlschema;

import org.xml.sax.Locator;

final class CollisionInfo {
   private final String name;
   private final Locator source1;
   private final Locator source2;

   public CollisionInfo(String name, Locator source1, Locator source2) {
      this.name = name;
      this.source1 = source1;
      this.source2 = source2;
   }

   public String toString() {
      return Messages.format("CollisionInfo.CollisionInfo", this.name, this.printLocator(this.source1), this.printLocator(this.source2));
   }

   private String printLocator(Locator loc) {
      if (loc == null) {
         return "";
      } else {
         int line = loc.getLineNumber();
         String sysId = loc.getSystemId();
         if (sysId == null) {
            sysId = Messages.format("CollisionInfo.UnknownFile");
         }

         return line != -1 ? Messages.format("CollisionInfo.LineXOfY", Integer.toString(line), sysId) : sysId;
      }
   }
}
