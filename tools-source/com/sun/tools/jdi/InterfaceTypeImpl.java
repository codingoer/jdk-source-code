package com.sun.tools.jdi;

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class InterfaceTypeImpl extends InvokableTypeImpl implements InterfaceType {
   private SoftReference superinterfacesRef = null;

   protected InterfaceTypeImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public List superinterfaces() {
      List var1 = this.superinterfacesRef == null ? null : (List)this.superinterfacesRef.get();
      if (var1 == null) {
         var1 = this.getInterfaces();
         var1 = Collections.unmodifiableList(var1);
         this.superinterfacesRef = new SoftReference(var1);
      }

      return var1;
   }

   public List subinterfaces() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.vm.allClasses().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         if (var3 instanceof InterfaceType) {
            InterfaceType var4 = (InterfaceType)var3;
            if (var4.isPrepared() && var4.superinterfaces().contains(this)) {
               var1.add(var4);
            }
         }
      }

      return var1;
   }

   public List implementors() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.vm.allClasses().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         if (var3 instanceof ClassType) {
            ClassType var4 = (ClassType)var3;
            if (var4.isPrepared() && var4.interfaces().contains(this)) {
               var1.add(var4);
            }
         }
      }

      return var1;
   }

   public boolean isInitialized() {
      return this.isPrepared();
   }

   public String toString() {
      return "interface " + this.name() + " (" + this.loaderString() + ")";
   }

   InvokableTypeImpl.InvocationResult waitForReply(PacketStream var1) throws JDWPException {
      return new IResult(JDWP.InterfaceType.InvokeMethod.waitForReply(this.vm, var1));
   }

   CommandSender getInvokeMethodSender(ThreadReferenceImpl var1, MethodImpl var2, ValueImpl[] var3, int var4) {
      return () -> {
         return JDWP.InterfaceType.InvokeMethod.enqueueCommand(this.vm, this, var1, var2.ref(), var3, var4);
      };
   }

   ClassType superclass() {
      return null;
   }

   boolean isAssignableTo(ReferenceType var1) {
      return var1.name().equals("java.lang.Object") ? true : super.isAssignableTo(var1);
   }

   List interfaces() {
      return this.superinterfaces();
   }

   boolean canInvoke(Method var1) {
      return this.equals(var1.declaringType());
   }

   private static class IResult implements InvokableTypeImpl.InvocationResult {
      private final JDWP.InterfaceType.InvokeMethod rslt;

      public IResult(JDWP.InterfaceType.InvokeMethod var1) {
         this.rslt = var1;
      }

      public ObjectReferenceImpl getException() {
         return this.rslt.exception;
      }

      public ValueImpl getResult() {
         return this.rslt.returnValue;
      }
   }
}
