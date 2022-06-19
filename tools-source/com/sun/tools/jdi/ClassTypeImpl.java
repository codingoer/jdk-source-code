package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ClassTypeImpl extends InvokableTypeImpl implements ClassType {
   private boolean cachedSuperclass = false;
   private ClassType superclass = null;
   private int lastLine = -1;
   private List interfaces = null;

   protected ClassTypeImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public ClassType superclass() {
      if (!this.cachedSuperclass) {
         ClassTypeImpl var1 = null;

         try {
            var1 = JDWP.ClassType.Superclass.process(this.vm, this).superclass;
         } catch (JDWPException var3) {
            throw var3.toJDIException();
         }

         if (var1 != null) {
            this.superclass = var1;
         }

         this.cachedSuperclass = true;
      }

      return this.superclass;
   }

   public List interfaces() {
      if (this.interfaces == null) {
         this.interfaces = this.getInterfaces();
      }

      return this.interfaces;
   }

   public List allInterfaces() {
      return this.getAllInterfaces();
   }

   public List subclasses() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.vm.allClasses().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         if (var3 instanceof ClassType) {
            ClassType var4 = (ClassType)var3;
            ClassType var5 = var4.superclass();
            if (var5 != null && var5.equals(this)) {
               var1.add((ClassType)var3);
            }
         }
      }

      return var1;
   }

   public boolean isEnum() {
      ClassType var1 = this.superclass();
      return var1 != null && var1.name().equals("java.lang.Enum");
   }

   public void setValue(Field var1, Value var2) throws InvalidTypeException, ClassNotLoadedException {
      this.validateMirror(var1);
      this.validateMirrorOrNull(var2);
      this.validateFieldSet(var1);
      if (!var1.isStatic()) {
         throw new IllegalArgumentException("Must set non-static field through an instance");
      } else {
         try {
            JDWP.ClassType.SetValues.FieldValue[] var3 = new JDWP.ClassType.SetValues.FieldValue[]{new JDWP.ClassType.SetValues.FieldValue(((FieldImpl)var1).ref(), ValueImpl.prepareForAssignment(var2, (FieldImpl)var1))};

            try {
               JDWP.ClassType.SetValues.process(this.vm, this, var3);
            } catch (JDWPException var5) {
               throw var5.toJDIException();
            }
         } catch (ClassNotLoadedException var6) {
            if (var2 != null) {
               throw var6;
            }
         }

      }
   }

   PacketStream sendNewInstanceCommand(final ThreadReferenceImpl var1, final MethodImpl var2, final ValueImpl[] var3, final int var4) {
      CommandSender var5 = new CommandSender() {
         public PacketStream send() {
            return JDWP.ClassType.NewInstance.enqueueCommand(ClassTypeImpl.this.vm, ClassTypeImpl.this, var1, var2.ref(), var3, var4);
         }
      };
      PacketStream var6;
      if ((var4 & 1) != 0) {
         var6 = var1.sendResumingCommand(var5);
      } else {
         var6 = this.vm.sendResumingCommand(var5);
      }

      return var6;
   }

   public ObjectReference newInstance(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
      this.validateMirror(var1);
      this.validateMirror(var2);
      this.validateMirrorsOrNulls(var3);
      MethodImpl var5 = (MethodImpl)var2;
      ThreadReferenceImpl var6 = (ThreadReferenceImpl)var1;
      this.validateConstructorInvocation(var5);
      List var7 = var5.validateAndPrepareArgumentsForInvoke(var3);
      ValueImpl[] var8 = (ValueImpl[])var7.toArray(new ValueImpl[0]);
      JDWP.ClassType.NewInstance var9 = null;

      try {
         PacketStream var10 = this.sendNewInstanceCommand(var6, var5, var8, var4);
         var9 = JDWP.ClassType.NewInstance.waitForReply(this.vm, var10);
      } catch (JDWPException var11) {
         if (var11.errorCode() == 10) {
            throw new IncompatibleThreadStateException();
         }

         throw var11.toJDIException();
      }

      if ((var4 & 1) == 0) {
         this.vm.notifySuspend();
      }

      if (var9.exception != null) {
         throw new InvocationException(var9.exception);
      } else {
         return var9.newObject;
      }
   }

   public Method concreteMethodByName(String var1, String var2) {
      Method var3 = null;
      Iterator var4 = this.visibleMethods().iterator();

      while(var4.hasNext()) {
         Method var5 = (Method)var4.next();
         if (var5.name().equals(var1) && var5.signature().equals(var2) && !var5.isAbstract()) {
            var3 = var5;
            break;
         }
      }

      return var3;
   }

   void validateConstructorInvocation(Method var1) throws InvalidTypeException, InvocationException {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1.declaringType();
      if (!var2.equals(this)) {
         throw new IllegalArgumentException("Invalid constructor");
      } else if (!var1.isConstructor()) {
         throw new IllegalArgumentException("Cannot create instance with non-constructor");
      }
   }

   public String toString() {
      return "class " + this.name() + " (" + this.loaderString() + ")";
   }

   CommandSender getInvokeMethodSender(ThreadReferenceImpl var1, MethodImpl var2, ValueImpl[] var3, int var4) {
      return () -> {
         return JDWP.ClassType.InvokeMethod.enqueueCommand(this.vm, this, var1, var2.ref(), var3, var4);
      };
   }

   InvokableTypeImpl.InvocationResult waitForReply(PacketStream var1) throws JDWPException {
      return new IResult(JDWP.ClassType.InvokeMethod.waitForReply(this.vm, var1));
   }

   boolean canInvoke(Method var1) {
      return ((ReferenceTypeImpl)var1.declaringType()).isAssignableFrom((ReferenceType)this);
   }

   private static class IResult implements InvokableTypeImpl.InvocationResult {
      private final JDWP.ClassType.InvokeMethod rslt;

      public IResult(JDWP.ClassType.InvokeMethod var1) {
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
