package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectReferenceImpl extends ValueImpl implements ObjectReference, VMListener {
   protected long ref;
   private ReferenceType type = null;
   private int gcDisableCount = 0;
   boolean addedListener = false;
   private static final Cache noInitCache = new Cache();
   private static final Cache markerCache = new Cache();
   private Cache cache;

   private void disableCache() {
      synchronized(this.vm.state()) {
         this.cache = null;
      }
   }

   private void enableCache() {
      synchronized(this.vm.state()) {
         this.cache = markerCache;
      }
   }

   protected Cache newCache() {
      return new Cache();
   }

   protected Cache getCache() {
      synchronized(this.vm.state()) {
         if (this.cache == noInitCache) {
            if (this.vm.state().isSuspended()) {
               this.enableCache();
            } else {
               this.disableCache();
            }
         }

         if (this.cache == markerCache) {
            this.cache = this.newCache();
         }

         return this.cache;
      }
   }

   protected ClassTypeImpl invokableReferenceType(Method var1) {
      return (ClassTypeImpl)this.referenceType();
   }

   ObjectReferenceImpl(VirtualMachine var1, long var2) {
      super(var1);
      this.cache = noInitCache;
      this.ref = var2;
   }

   protected String description() {
      return "ObjectReference " + this.uniqueID();
   }

   public boolean vmSuspended(VMAction var1) {
      this.enableCache();
      return true;
   }

   public boolean vmNotSuspended(VMAction var1) {
      synchronized(this.vm.state()) {
         if (this.cache != null && (this.vm.traceFlags & 16) != 0) {
            this.vm.printTrace("Clearing temporary cache for " + this.description());
         }

         this.disableCache();
         if (this.addedListener) {
            this.addedListener = false;
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ObjectReferenceImpl) {
         ObjectReferenceImpl var2 = (ObjectReferenceImpl)var1;
         return this.ref() == var2.ref() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)this.ref();
   }

   public Type type() {
      return this.referenceType();
   }

   public ReferenceType referenceType() {
      if (this.type == null) {
         try {
            JDWP.ObjectReference.ReferenceType var1 = JDWP.ObjectReference.ReferenceType.process(this.vm, this);
            this.type = this.vm.referenceType(var1.typeID, var1.refTypeTag);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.type;
   }

   public Value getValue(Field var1) {
      ArrayList var2 = new ArrayList(1);
      var2.add(var1);
      Map var3 = this.getValues(var2);
      return (Value)var3.get(var1);
   }

   public Map getValues(List var1) {
      this.validateMirrors(var1);
      ArrayList var2 = new ArrayList(0);
      int var3 = var1.size();
      ArrayList var4 = new ArrayList(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         Field var6 = (Field)var1.get(var5);
         ((ReferenceTypeImpl)this.referenceType()).validateFieldAccess(var6);
         if (var6.isStatic()) {
            var2.add(var6);
         } else {
            var4.add(var6);
         }
      }

      Object var11;
      if (var2.size() > 0) {
         var11 = this.referenceType().getValues(var2);
      } else {
         var11 = new HashMap(var3);
      }

      var3 = var4.size();
      JDWP.ObjectReference.GetValues.Field[] var12 = new JDWP.ObjectReference.GetValues.Field[var3];

      for(int var7 = 0; var7 < var3; ++var7) {
         FieldImpl var8 = (FieldImpl)var4.get(var7);
         var12[var7] = new JDWP.ObjectReference.GetValues.Field(var8.ref());
      }

      ValueImpl[] var13;
      try {
         var13 = JDWP.ObjectReference.GetValues.process(this.vm, this, var12).values;
      } catch (JDWPException var10) {
         throw var10.toJDIException();
      }

      if (var3 != var13.length) {
         throw new InternalException("Wrong number of values returned from target VM");
      } else {
         for(int var14 = 0; var14 < var3; ++var14) {
            FieldImpl var9 = (FieldImpl)var4.get(var14);
            ((Map)var11).put(var9, var13[var14]);
         }

         return (Map)var11;
      }
   }

   public void setValue(Field var1, Value var2) throws InvalidTypeException, ClassNotLoadedException {
      this.validateMirror(var1);
      this.validateMirrorOrNull(var2);
      ((ReferenceTypeImpl)this.referenceType()).validateFieldSet(var1);
      if (var1.isStatic()) {
         ReferenceType var7 = this.referenceType();
         if (var7 instanceof ClassType) {
            ((ClassType)var7).setValue(var1, var2);
         } else {
            throw new IllegalArgumentException("Invalid type for static field set");
         }
      } else {
         try {
            JDWP.ObjectReference.SetValues.FieldValue[] var3 = new JDWP.ObjectReference.SetValues.FieldValue[]{new JDWP.ObjectReference.SetValues.FieldValue(((FieldImpl)var1).ref(), ValueImpl.prepareForAssignment(var2, (FieldImpl)var1))};

            try {
               JDWP.ObjectReference.SetValues.process(this.vm, this, var3);
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

   void validateMethodInvocation(Method var1, int var2) throws InvalidTypeException, InvocationException {
      ReferenceTypeImpl var3 = (ReferenceTypeImpl)var1.declaringType();
      if (!var3.isAssignableFrom((ObjectReference)this)) {
         throw new IllegalArgumentException("Invalid method");
      } else {
         if (var3 instanceof ClassTypeImpl) {
            this.validateClassMethodInvocation(var1, var2);
         } else {
            if (!(var3 instanceof InterfaceTypeImpl)) {
               throw new InvalidTypeException();
            }

            this.validateIfaceMethodInvocation(var1, var2);
         }

      }
   }

   void validateClassMethodInvocation(Method var1, int var2) throws InvalidTypeException, InvocationException {
      ClassTypeImpl var3 = this.invokableReferenceType(var1);
      if (var1.isConstructor()) {
         throw new IllegalArgumentException("Cannot invoke constructor");
      } else if (isNonVirtual(var2) && var1.isAbstract()) {
         throw new IllegalArgumentException("Abstract method");
      } else {
         if (!isNonVirtual(var2)) {
            Method var5 = var3.concreteMethodByName(var1.name(), var1.signature());
            ClassTypeImpl var4 = (ClassTypeImpl)var5.declaringType();
         }

      }
   }

   void validateIfaceMethodInvocation(Method var1, int var2) throws InvalidTypeException, InvocationException {
      if (isNonVirtual(var2) && !var1.isDefault()) {
         throw new IllegalArgumentException("Not a default method");
      }
   }

   PacketStream sendInvokeCommand(final ThreadReferenceImpl var1, final ClassTypeImpl var2, final MethodImpl var3, final ValueImpl[] var4, final int var5) {
      CommandSender var6 = new CommandSender() {
         public PacketStream send() {
            return JDWP.ObjectReference.InvokeMethod.enqueueCommand(ObjectReferenceImpl.this.vm, ObjectReferenceImpl.this, var1, var2, var3.ref(), var4, var5);
         }
      };
      PacketStream var7;
      if ((var5 & 1) != 0) {
         var7 = var1.sendResumingCommand(var6);
      } else {
         var7 = this.vm.sendResumingCommand(var6);
      }

      return var7;
   }

   public Value invokeMethod(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, IncompatibleThreadStateException, InvocationException, ClassNotLoadedException {
      this.validateMirror(var1);
      this.validateMirror(var2);
      this.validateMirrorsOrNulls(var3);
      MethodImpl var5 = (MethodImpl)var2;
      ThreadReferenceImpl var6 = (ThreadReferenceImpl)var1;
      if (var5.isStatic()) {
         if (this.referenceType() instanceof InterfaceType) {
            InterfaceType var13 = (InterfaceType)this.referenceType();
            return var13.invokeMethod(var6, var5, var3, var4);
         } else if (this.referenceType() instanceof ClassType) {
            ClassType var12 = (ClassType)this.referenceType();
            return var12.invokeMethod(var6, var5, var3, var4);
         } else {
            throw new IllegalArgumentException("Invalid type for static method invocation");
         }
      } else {
         this.validateMethodInvocation(var5, var4);
         List var7 = var5.validateAndPrepareArgumentsForInvoke(var3);
         ValueImpl[] var8 = (ValueImpl[])var7.toArray(new ValueImpl[0]);

         JDWP.ObjectReference.InvokeMethod var9;
         try {
            PacketStream var10 = this.sendInvokeCommand(var6, this.invokableReferenceType(var5), var5, var8, var4);
            var9 = JDWP.ObjectReference.InvokeMethod.waitForReply(this.vm, var10);
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
            return var9.returnValue;
         }
      }
   }

   public synchronized void disableCollection() {
      if (this.gcDisableCount == 0) {
         try {
            JDWP.ObjectReference.DisableCollection.process(this.vm, this);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      ++this.gcDisableCount;
   }

   public synchronized void enableCollection() {
      --this.gcDisableCount;
      if (this.gcDisableCount == 0) {
         try {
            JDWP.ObjectReference.EnableCollection.process(this.vm, this);
         } catch (JDWPException var2) {
            if (var2.errorCode() != 20) {
               throw var2.toJDIException();
            }

            return;
         }
      }

   }

   public boolean isCollected() {
      try {
         return JDWP.ObjectReference.IsCollected.process(this.vm, this).isCollected;
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   public long uniqueID() {
      return this.ref();
   }

   JDWP.ObjectReference.MonitorInfo jdwpMonitorInfo() throws IncompatibleThreadStateException {
      JDWP.ObjectReference.MonitorInfo var1 = null;

      try {
         Cache var2;
         synchronized(this.vm.state()) {
            var2 = this.getCache();
            if (var2 != null) {
               var1 = var2.monitorInfo;
               if (var1 == null && !this.vm.state().hasListener(this)) {
                  this.vm.state().addListener(this);
                  this.addedListener = true;
               }
            }
         }

         if (var1 == null) {
            var1 = JDWP.ObjectReference.MonitorInfo.process(this.vm, this);
            if (var2 != null) {
               var2.monitorInfo = var1;
               if ((this.vm.traceFlags & 16) != 0) {
                  this.vm.printTrace("ObjectReference " + this.uniqueID() + " temporarily caching monitor info");
               }
            }
         }

         return var1;
      } catch (JDWPException var6) {
         if (var6.errorCode() == 13) {
            throw new IncompatibleThreadStateException();
         } else {
            throw var6.toJDIException();
         }
      }
   }

   public List waitingThreads() throws IncompatibleThreadStateException {
      return Arrays.asList((ThreadReference[])this.jdwpMonitorInfo().waiters);
   }

   public ThreadReference owningThread() throws IncompatibleThreadStateException {
      return this.jdwpMonitorInfo().owner;
   }

   public int entryCount() throws IncompatibleThreadStateException {
      return this.jdwpMonitorInfo().entryCount;
   }

   public List referringObjects(long var1) {
      if (!this.vm.canGetInstanceInfo()) {
         throw new UnsupportedOperationException("target does not support getting referring objects");
      } else if (var1 < 0L) {
         throw new IllegalArgumentException("maxReferrers is less than zero: " + var1);
      } else {
         int var3 = var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;

         try {
            return Arrays.asList((ObjectReference[])JDWP.ObjectReference.ReferringObjects.process(this.vm, this, var3).referringObjects);
         } catch (JDWPException var5) {
            throw var5.toJDIException();
         }
      }
   }

   long ref() {
      return this.ref;
   }

   boolean isClassObject() {
      return this.referenceType().name().equals("java.lang.Class");
   }

   ValueImpl prepareForAssignmentTo(ValueContainer var1) throws InvalidTypeException, ClassNotLoadedException {
      this.validateAssignment(var1);
      return this;
   }

   void validateAssignment(ValueContainer var1) throws InvalidTypeException, ClassNotLoadedException {
      if (var1.signature().length() == 1) {
         throw new InvalidTypeException("Can't assign object value to primitive");
      } else if (var1.signature().charAt(0) == '[' && this.type().signature().charAt(0) != '[') {
         throw new InvalidTypeException("Can't assign non-array value to an array");
      } else if ("void".equals(var1.typeName())) {
         throw new InvalidTypeException("Can't assign object value to a void");
      } else {
         ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1.type();
         ReferenceTypeImpl var3 = (ReferenceTypeImpl)this.referenceType();
         if (!var3.isAssignableTo(var2)) {
            JNITypeParser var4 = new JNITypeParser(var2.signature());
            String var5 = var4.typeName();
            throw new InvalidTypeException("Can't assign " + this.type().name() + " to " + var5);
         }
      }
   }

   public String toString() {
      return "instance of " + this.referenceType().name() + "(id=" + this.uniqueID() + ")";
   }

   byte typeValueKey() {
      return 76;
   }

   private static boolean isNonVirtual(int var0) {
      return (var0 & 2) != 0;
   }

   protected static class Cache {
      JDWP.ObjectReference.MonitorInfo monitorInfo = null;
   }
}
