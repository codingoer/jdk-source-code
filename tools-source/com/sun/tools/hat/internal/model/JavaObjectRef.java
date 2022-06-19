package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.util.Misc;

public class JavaObjectRef extends JavaThing {
   private long id;

   public JavaObjectRef(long var1) {
      this.id = var1;
   }

   public long getId() {
      return this.id;
   }

   public boolean isHeapAllocated() {
      return true;
   }

   public JavaThing dereference(Snapshot var1, JavaField var2) {
      return this.dereference(var1, var2, true);
   }

   public JavaThing dereference(Snapshot var1, JavaField var2, boolean var3) {
      if (var2 != null && !var2.hasId()) {
         return new JavaLong(this.id);
      } else if (this.id == 0L) {
         return var1.getNullThing();
      } else {
         Object var4 = var1.findThing(this.id);
         if (var4 == null) {
            if (!var1.getUnresolvedObjectsOK() && var3) {
               String var5 = "WARNING:  Failed to resolve object id " + Misc.toHex(this.id);
               if (var2 != null) {
                  var5 = var5 + " for field " + var2.getName() + " (signature " + var2.getSignature() + ")";
               }

               System.out.println(var5);
            }

            var4 = new HackJavaValue("Unresolved object " + Misc.toHex(this.id), 0);
         }

         return (JavaThing)var4;
      }
   }

   public int getSize() {
      return 0;
   }

   public String toString() {
      return "Unresolved object " + Misc.toHex(this.id);
   }
}
