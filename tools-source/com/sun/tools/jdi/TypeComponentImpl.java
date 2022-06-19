package com.sun.tools.jdi;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.TypeComponent;
import com.sun.jdi.VirtualMachine;

public abstract class TypeComponentImpl extends MirrorImpl implements TypeComponent {
   protected final long ref;
   protected final String name;
   protected final String signature;
   protected final String genericSignature;
   protected final ReferenceTypeImpl declaringType;
   private final int modifiers;

   TypeComponentImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, String var5, String var6, String var7, int var8) {
      super(var1);
      this.declaringType = var2;
      this.ref = var3;
      this.name = var5;
      this.signature = var6;
      if (var7 != null && var7.length() != 0) {
         this.genericSignature = var7;
      } else {
         this.genericSignature = null;
      }

      this.modifiers = var8;
   }

   public String name() {
      return this.name;
   }

   public String signature() {
      return this.signature;
   }

   public String genericSignature() {
      return this.genericSignature;
   }

   public int modifiers() {
      return this.modifiers;
   }

   public ReferenceType declaringType() {
      return this.declaringType;
   }

   public boolean isStatic() {
      return this.isModifierSet(8);
   }

   public boolean isFinal() {
      return this.isModifierSet(16);
   }

   public boolean isPrivate() {
      return this.isModifierSet(2);
   }

   public boolean isPackagePrivate() {
      return !this.isModifierSet(7);
   }

   public boolean isProtected() {
      return this.isModifierSet(4);
   }

   public boolean isPublic() {
      return this.isModifierSet(1);
   }

   public boolean isSynthetic() {
      return this.isModifierSet(-268435456);
   }

   long ref() {
      return this.ref;
   }

   boolean isModifierSet(int var1) {
      return (this.modifiers & var1) != 0;
   }
}
