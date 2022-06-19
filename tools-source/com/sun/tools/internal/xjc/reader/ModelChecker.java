package com.sun.tools.internal.xjc.reader;

import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.Model;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public final class ModelChecker {
   private final Model model = (Model)Ring.get(Model.class);
   private final ErrorReceiver errorReceiver = (ErrorReceiver)Ring.get(ErrorReceiver.class);

   public void check() {
      Iterator var1 = this.model.beans().values().iterator();

      while(var1.hasNext()) {
         CClassInfo ci = (CClassInfo)var1.next();
         this.check(ci);
      }

   }

   private void check(CClassInfo ci) {
      List props = ci.getProperties();
      Map collisionTable = new HashMap();

      label58:
      for(int i = 0; i < props.size(); ++i) {
         CPropertyInfo p1 = (CPropertyInfo)props.get(i);
         if (p1.getName(true).equals("Class")) {
            this.errorReceiver.error(p1.locator, Messages.PROPERTY_CLASS_IS_RESERVED.format());
         } else {
            QName n = p1.collectElementNames(collisionTable);
            if (n != null) {
               CPropertyInfo p2 = (CPropertyInfo)collisionTable.get(n);
               if (p2.getName(true).equals(n.toString()) || p2.getName(false).equals(n.toString())) {
                  this.errorReceiver.error(p1.locator, Messages.DUPLICATE_ELEMENT.format(n));
                  this.errorReceiver.error(p2.locator, Messages.ERR_RELEVANT_LOCATION.format());
               }
            }

            for(int j = i + 1; j < props.size(); ++j) {
               if (this.checkPropertyCollision(p1, (CPropertyInfo)props.get(j))) {
                  continue label58;
               }
            }

            for(CClassInfo c = ci.getBaseClass(); c != null; c = c.getBaseClass()) {
               Iterator var8 = c.getProperties().iterator();

               while(var8.hasNext()) {
                  CPropertyInfo p2 = (CPropertyInfo)var8.next();
                  if (this.checkPropertyCollision(p1, p2)) {
                     continue label58;
                  }
               }
            }
         }
      }

   }

   private boolean checkPropertyCollision(CPropertyInfo p1, CPropertyInfo p2) {
      if (!p1.getName(true).equals(p2.getName(true))) {
         return false;
      } else {
         this.errorReceiver.error(p1.locator, Messages.DUPLICATE_PROPERTY.format(p1.getName(true)));
         this.errorReceiver.error(p2.locator, Messages.ERR_RELEVANT_LOCATION.format());
         return true;
      }
   }
}
