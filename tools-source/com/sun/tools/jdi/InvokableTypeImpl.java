package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class InvokableTypeImpl extends ReferenceTypeImpl {
   InvokableTypeImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public final Value invokeMethod(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
      this.validateMirror(var1);
      this.validateMirror(var2);
      this.validateMirrorsOrNulls(var3);
      MethodImpl var5 = (MethodImpl)var2;
      ThreadReferenceImpl var6 = (ThreadReferenceImpl)var1;
      this.validateMethodInvocation(var5);
      List var7 = var5.validateAndPrepareArgumentsForInvoke(var3);
      ValueImpl[] var8 = (ValueImpl[])var7.toArray(new ValueImpl[0]);

      InvocationResult var9;
      try {
         PacketStream var10 = this.sendInvokeCommand(var6, var5, var8, var4);
         var9 = this.waitForReply(var10);
      } catch (JDWPException var11) {
         if (var11.errorCode() == 10) {
            throw new IncompatibleThreadStateException();
         }

         throw var11.toJDIException();
      }

      if ((var4 & 1) == 0) {
         this.vm.notifySuspend();
      }

      if (var9.getException() != null) {
         throw new InvocationException(var9.getException());
      } else {
         return var9.getResult();
      }
   }

   boolean isAssignableTo(ReferenceType var1) {
      ClassTypeImpl var2 = (ClassTypeImpl)this.superclass();
      if (this.equals(var1)) {
         return true;
      } else if (var2 != null && var2.isAssignableTo(var1)) {
         return true;
      } else {
         List var3 = this.interfaces();
         Iterator var4 = var3.iterator();

         InterfaceTypeImpl var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (InterfaceTypeImpl)var4.next();
         } while(!var5.isAssignableTo(var1));

         return true;
      }
   }

   final void addVisibleMethods(Map var1, Set var2) {
      Iterator var3 = this.interfaces().iterator();

      while(var3.hasNext()) {
         InterfaceTypeImpl var4 = (InterfaceTypeImpl)var3.next();
         if (!var2.contains(var4)) {
            var4.addVisibleMethods(var1, var2);
            var2.add(var4);
         }
      }

      ClassTypeImpl var5 = (ClassTypeImpl)this.superclass();
      if (var5 != null) {
         var5.addVisibleMethods(var1, var2);
      }

      this.addToMethodMap(var1, this.methods());
   }

   final void addInterfaces(List var1) {
      List var2 = this.interfaces();
      var1.addAll(this.interfaces());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         InterfaceTypeImpl var4 = (InterfaceTypeImpl)var3.next();
         var4.addInterfaces(var1);
      }

      ClassTypeImpl var5 = (ClassTypeImpl)this.superclass();
      if (var5 != null) {
         var5.addInterfaces(var1);
      }

   }

   final List getAllInterfaces() {
      ArrayList var1 = new ArrayList();
      this.addInterfaces(var1);
      return var1;
   }

   public final List allMethods() {
      ArrayList var1 = new ArrayList(this.methods());

      for(ClassType var2 = this.superclass(); var2 != null; var2 = var2.superclass()) {
         var1.addAll(var2.methods());
      }

      Iterator var3 = this.getAllInterfaces().iterator();

      while(var3.hasNext()) {
         InterfaceType var4 = (InterfaceType)var3.next();
         var1.addAll(var4.methods());
      }

      return var1;
   }

   final List inheritedTypes() {
      ArrayList var1 = new ArrayList();
      if (this.superclass() != null) {
         var1.add(0, this.superclass());
      }

      Iterator var2 = this.interfaces().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         var1.add(var3);
      }

      return var1;
   }

   private PacketStream sendInvokeCommand(ThreadReferenceImpl var1, MethodImpl var2, ValueImpl[] var3, int var4) {
      CommandSender var5 = this.getInvokeMethodSender(var1, var2, var3, var4);
      PacketStream var6;
      if ((var4 & 1) != 0) {
         var6 = var1.sendResumingCommand(var5);
      } else {
         var6 = this.vm.sendResumingCommand(var5);
      }

      return var6;
   }

   private void validateMethodInvocation(Method var1) throws InvalidTypeException, InvocationException {
      if (!this.canInvoke(var1)) {
         throw new IllegalArgumentException("Invalid method");
      } else if (!var1.isStatic()) {
         throw new IllegalArgumentException("Cannot invoke instance method on a class/interface type");
      } else if (var1.isStaticInitializer()) {
         throw new IllegalArgumentException("Cannot invoke static initializer");
      }
   }

   abstract CommandSender getInvokeMethodSender(ThreadReferenceImpl var1, MethodImpl var2, ValueImpl[] var3, int var4);

   abstract InvocationResult waitForReply(PacketStream var1) throws JDWPException;

   abstract ClassType superclass();

   abstract List interfaces();

   abstract boolean canInvoke(Method var1);

   interface InvocationResult {
      ObjectReferenceImpl getException();

      ValueImpl getResult();
   }
}
