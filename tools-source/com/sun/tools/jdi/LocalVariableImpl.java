package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InternalException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class LocalVariableImpl extends MirrorImpl implements LocalVariable, ValueContainer {
   private final Method method;
   private final int slot;
   private final Location scopeStart;
   private final Location scopeEnd;
   private final String name;
   private final String signature;
   private String genericSignature = null;

   LocalVariableImpl(VirtualMachine var1, Method var2, int var3, Location var4, Location var5, String var6, String var7, String var8) {
      super(var1);
      this.method = var2;
      this.slot = var3;
      this.scopeStart = var4;
      this.scopeEnd = var5;
      this.name = var6;
      this.signature = var7;
      if (var8 != null && var8.length() > 0) {
         this.genericSignature = var8;
      } else {
         this.genericSignature = null;
      }

   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof LocalVariableImpl) {
         LocalVariableImpl var2 = (LocalVariableImpl)var1;
         return this.slot() == var2.slot() && this.scopeStart != null && this.scopeStart.equals(var2.scopeStart) && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (this.scopeStart.hashCode() << 4) + this.slot();
   }

   public int compareTo(LocalVariable var1) {
      LocalVariableImpl var2 = (LocalVariableImpl)var1;
      int var3 = this.scopeStart.compareTo(var2.scopeStart);
      if (var3 == 0) {
         var3 = this.slot() - var2.slot();
      }

      return var3;
   }

   public String name() {
      return this.name;
   }

   public String typeName() {
      JNITypeParser var1 = new JNITypeParser(this.signature);
      return var1.typeName();
   }

   public Type type() throws ClassNotLoadedException {
      return this.findType(this.signature());
   }

   public Type findType(String var1) throws ClassNotLoadedException {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.method.declaringType();
      return var2.findType(var1);
   }

   public String signature() {
      return this.signature;
   }

   public String genericSignature() {
      return this.genericSignature;
   }

   public boolean isVisible(StackFrame var1) {
      this.validateMirror(var1);
      Method var2 = var1.location().method();
      if (!var2.equals(this.method)) {
         throw new IllegalArgumentException("frame method different than variable's method");
      } else if (var2.isNative()) {
         return false;
      } else {
         return this.scopeStart.compareTo(var1.location()) <= 0 && this.scopeEnd.compareTo(var1.location()) >= 0;
      }
   }

   public boolean isArgument() {
      try {
         MethodImpl var1 = (MethodImpl)this.scopeStart.method();
         return this.slot < var1.argSlotCount();
      } catch (AbsentInformationException var2) {
         throw new InternalException();
      }
   }

   int slot() {
      return this.slot;
   }

   boolean hides(LocalVariable var1) {
      LocalVariableImpl var2 = (LocalVariableImpl)var1;
      if (this.method.equals(var2.method) && this.name.equals(var2.name)) {
         return this.scopeStart.compareTo(var2.scopeStart) > 0;
      } else {
         return false;
      }
   }

   public String toString() {
      return this.name() + " in " + this.method.toString() + "@" + this.scopeStart.toString();
   }
}
