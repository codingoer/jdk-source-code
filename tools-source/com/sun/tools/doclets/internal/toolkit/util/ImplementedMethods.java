package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImplementedMethods {
   private Map interfaces = new HashMap();
   private List methlist = new ArrayList();
   private Configuration configuration;
   private final ClassDoc classdoc;
   private final MethodDoc method;

   public ImplementedMethods(MethodDoc var1, Configuration var2) {
      this.method = var1;
      this.configuration = var2;
      this.classdoc = var1.containingClass();
   }

   public MethodDoc[] build(boolean var1) {
      this.buildImplementedMethodList(var1);
      return (MethodDoc[])this.methlist.toArray(new MethodDoc[this.methlist.size()]);
   }

   public MethodDoc[] build() {
      return this.build(true);
   }

   public Type getMethodHolder(MethodDoc var1) {
      return (Type)this.interfaces.get(var1);
   }

   private void buildImplementedMethodList(boolean var1) {
      List var2 = Util.getAllInterfaces(this.classdoc, this.configuration, var1);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         MethodDoc var5 = Util.findMethod(var4.asClassDoc(), this.method);
         if (var5 != null) {
            this.removeOverriddenMethod(var5);
            if (!this.overridingMethodFound(var5)) {
               this.methlist.add(var5);
               this.interfaces.put(var5, var4);
            }
         }
      }

   }

   private void removeOverriddenMethod(MethodDoc var1) {
      ClassDoc var2 = var1.overriddenClass();
      if (var2 != null) {
         for(int var3 = 0; var3 < this.methlist.size(); ++var3) {
            ClassDoc var4 = ((MethodDoc)this.methlist.get(var3)).containingClass();
            if (var4 == var2 || var2.subclassOf(var4)) {
               this.methlist.remove(var3);
               return;
            }
         }
      }

   }

   private boolean overridingMethodFound(MethodDoc var1) {
      ClassDoc var2 = var1.containingClass();

      for(int var3 = 0; var3 < this.methlist.size(); ++var3) {
         MethodDoc var4 = (MethodDoc)this.methlist.get(var3);
         if (var2 == var4.containingClass()) {
            return true;
         }

         ClassDoc var5 = var4.overriddenClass();
         if (var5 != null && (var5 == var2 || var5.subclassOf(var2))) {
            return true;
         }
      }

      return false;
   }
}
