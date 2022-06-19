package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.nc.NameClass;
import java.util.ArrayList;
import java.util.List;

class DuplicateAttributeDetector {
   private List nameClasses = new ArrayList();
   private Alternative alternatives = null;

   boolean addAttribute(NameClass nc) {
      int lim = this.nameClasses.size();

      for(Alternative a = this.alternatives; a != null; a = a.parent) {
         for(int i = a.endIndex; i < lim; ++i) {
            if (nc.hasOverlapWith((NameClass)this.nameClasses.get(i))) {
               return false;
            }
         }

         lim = a.startIndex;
      }

      for(int i = 0; i < lim; ++i) {
         if (nc.hasOverlapWith((NameClass)this.nameClasses.get(i))) {
            return false;
         }
      }

      this.nameClasses.add(nc);
      return true;
   }

   void startChoice() {
      this.alternatives = new Alternative(this.nameClasses.size(), this.alternatives);
   }

   void alternative() {
      this.alternatives.endIndex = this.nameClasses.size();
   }

   void endChoice() {
      this.alternatives = this.alternatives.parent;
   }

   private static class Alternative {
      private int startIndex;
      private int endIndex;
      private Alternative parent;

      private Alternative(int startIndex, Alternative parent) {
         this.startIndex = startIndex;
         this.endIndex = startIndex;
         this.parent = parent;
      }

      // $FF: synthetic method
      Alternative(int x0, Alternative x1, Object x2) {
         this(x0, x1);
      }
   }
}
