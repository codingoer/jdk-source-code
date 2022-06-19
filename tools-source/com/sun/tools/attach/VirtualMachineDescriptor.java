package com.sun.tools.attach;

import com.sun.tools.attach.spi.AttachProvider;
import jdk.Exported;

@Exported
public class VirtualMachineDescriptor {
   private AttachProvider provider;
   private String id;
   private String displayName;
   private volatile int hash;

   public VirtualMachineDescriptor(AttachProvider var1, String var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("identifier cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("display name cannot be null");
      } else {
         this.provider = var1;
         this.id = var2;
         this.displayName = var3;
      }
   }

   public VirtualMachineDescriptor(AttachProvider var1, String var2) {
      this(var1, var2, var2);
   }

   public AttachProvider provider() {
      return this.provider;
   }

   public String id() {
      return this.id;
   }

   public String displayName() {
      return this.displayName;
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         this.hash = this.provider.hashCode() * 127 + this.id.hashCode();
         return this.hash;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof VirtualMachineDescriptor)) {
         return false;
      } else {
         VirtualMachineDescriptor var2 = (VirtualMachineDescriptor)var1;
         if (var2.provider() != this.provider()) {
            return false;
         } else {
            return var2.id().equals(this.id());
         }
      }
   }

   public String toString() {
      String var1 = this.provider.toString() + ": " + this.id;
      if (this.displayName != this.id) {
         var1 = var1 + " " + this.displayName;
      }

      return var1;
   }
}
