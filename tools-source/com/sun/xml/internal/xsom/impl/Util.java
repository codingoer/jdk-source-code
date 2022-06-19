package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Util {
   private static XSType[] listDirectSubstitutables(XSType _this) {
      ArrayList r = new ArrayList();
      Iterator itr = ((SchemaImpl)_this.getOwnerSchema()).parent.iterateTypes();

      while(itr.hasNext()) {
         XSType t = (XSType)itr.next();
         if (t.getBaseType() == _this) {
            r.add(t);
         }
      }

      return (XSType[])((XSType[])r.toArray(new XSType[r.size()]));
   }

   public static XSType[] listSubstitutables(XSType _this) {
      Set substitables = new HashSet();
      buildSubstitutables(_this, substitables);
      return (XSType[])((XSType[])substitables.toArray(new XSType[substitables.size()]));
   }

   public static void buildSubstitutables(XSType _this, Set substitutables) {
      if (!_this.isLocal()) {
         buildSubstitutables(_this, _this, substitutables);
      }
   }

   private static void buildSubstitutables(XSType head, XSType _this, Set substitutables) {
      if (isSubstitutable(head, _this)) {
         if (substitutables.add(_this)) {
            XSType[] child = listDirectSubstitutables(_this);

            for(int i = 0; i < child.length; ++i) {
               buildSubstitutables(head, child[i], substitutables);
            }
         }

      }
   }

   private static boolean isSubstitutable(XSType _base, XSType derived) {
      if (_base.isComplexType()) {
         for(XSComplexType base = _base.asComplexType(); base != derived; derived = derived.getBaseType()) {
            if (base.isSubstitutionProhibited(derived.getDerivationMethod())) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }
}
