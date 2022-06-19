package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StackFrameImpl extends MirrorImpl implements StackFrame, ThreadListener {
   private boolean isValid = true;
   private final ThreadReferenceImpl thread;
   private final long id;
   private final Location location;
   private Map visibleVariables = null;
   private ObjectReference thisObject = null;

   StackFrameImpl(VirtualMachine var1, ThreadReferenceImpl var2, long var3, Location var5) {
      super(var1);
      this.thread = var2;
      this.id = var3;
      this.location = var5;
      var2.addListener(this);
   }

   public boolean threadResumable(ThreadAction var1) {
      synchronized(this.vm.state()) {
         if (this.isValid) {
            this.isValid = false;
            return false;
         } else {
            throw new InternalException("Invalid stack frame thread listener");
         }
      }
   }

   void validateStackFrame() {
      if (!this.isValid) {
         throw new InvalidStackFrameException("Thread has been resumed");
      }
   }

   public Location location() {
      this.validateStackFrame();
      return this.location;
   }

   public ThreadReference thread() {
      this.validateStackFrame();
      return this.thread;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof StackFrameImpl) {
         StackFrameImpl var2 = (StackFrameImpl)var1;
         return this.id == var2.id && this.thread().equals(var2.thread()) && this.location().equals(var2.location()) && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (this.thread().hashCode() << 4) + (int)this.id;
   }

   public ObjectReference thisObject() {
      this.validateStackFrame();
      MethodImpl var1 = (MethodImpl)this.location.method();
      if (!var1.isStatic() && !var1.isNative()) {
         if (this.thisObject == null) {
            PacketStream var2;
            synchronized(this.vm.state()) {
               this.validateStackFrame();
               var2 = JDWP.StackFrame.ThisObject.enqueueCommand(this.vm, this.thread, this.id);
            }

            try {
               this.thisObject = JDWP.StackFrame.ThisObject.waitForReply(this.vm, var2).objectThis;
            } catch (JDWPException var6) {
               switch (var6.errorCode()) {
                  case 10:
                  case 13:
                  case 30:
                     throw new InvalidStackFrameException();
                  default:
                     throw var6.toJDIException();
               }
            }
         }

         return this.thisObject;
      } else {
         return null;
      }
   }

   private void createVisibleVariables() throws AbsentInformationException {
      if (this.visibleVariables == null) {
         List var1 = this.location.method().variables();
         HashMap var2 = new HashMap(var1.size());
         Iterator var3 = var1.iterator();

         while(true) {
            LocalVariable var4;
            String var5;
            LocalVariable var6;
            do {
               do {
                  if (!var3.hasNext()) {
                     this.visibleVariables = var2;
                     return;
                  }

                  var4 = (LocalVariable)var3.next();
                  var5 = var4.name();
               } while(!var4.isVisible(this));

               var6 = (LocalVariable)var2.get(var5);
            } while(var6 != null && !((LocalVariableImpl)var4).hides(var6));

            var2.put(var5, var4);
         }
      }
   }

   public List visibleVariables() throws AbsentInformationException {
      this.validateStackFrame();
      this.createVisibleVariables();
      ArrayList var1 = new ArrayList(this.visibleVariables.values());
      Collections.sort(var1);
      return var1;
   }

   public LocalVariable visibleVariableByName(String var1) throws AbsentInformationException {
      this.validateStackFrame();
      this.createVisibleVariables();
      return (LocalVariable)this.visibleVariables.get(var1);
   }

   public Value getValue(LocalVariable var1) {
      ArrayList var2 = new ArrayList(1);
      var2.add(var1);
      return (Value)this.getValues(var2).get(var1);
   }

   public Map getValues(List var1) {
      this.validateStackFrame();
      this.validateMirrors(var1);
      int var2 = var1.size();
      JDWP.StackFrame.GetValues.SlotInfo[] var3 = new JDWP.StackFrame.GetValues.SlotInfo[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         LocalVariableImpl var5 = (LocalVariableImpl)var1.get(var4);
         if (!var5.isVisible(this)) {
            throw new IllegalArgumentException(var5.name() + " is not valid at this frame location");
         }

         var3[var4] = new JDWP.StackFrame.GetValues.SlotInfo(var5.slot(), (byte)var5.signature().charAt(0));
      }

      PacketStream var11;
      synchronized(this.vm.state()) {
         this.validateStackFrame();
         var11 = JDWP.StackFrame.GetValues.enqueueCommand(this.vm, this.thread, this.id, var3);
      }

      ValueImpl[] var12;
      try {
         var12 = JDWP.StackFrame.GetValues.waitForReply(this.vm, var11).values;
      } catch (JDWPException var10) {
         switch (var10.errorCode()) {
            case 10:
            case 13:
            case 30:
               throw new InvalidStackFrameException();
            default:
               throw var10.toJDIException();
         }
      }

      if (var2 != var12.length) {
         throw new InternalException("Wrong number of values returned from target VM");
      } else {
         HashMap var6 = new HashMap(var2);

         for(int var7 = 0; var7 < var2; ++var7) {
            LocalVariableImpl var8 = (LocalVariableImpl)var1.get(var7);
            var6.put(var8, var12[var7]);
         }

         return var6;
      }
   }

   public void setValue(LocalVariable var1, Value var2) throws InvalidTypeException, ClassNotLoadedException {
      this.validateStackFrame();
      this.validateMirror(var1);
      this.validateMirrorOrNull(var2);
      LocalVariableImpl var3 = (LocalVariableImpl)var1;
      ValueImpl var4 = (ValueImpl)var2;
      if (!var3.isVisible(this)) {
         throw new IllegalArgumentException(var3.name() + " is not valid at this frame location");
      } else {
         try {
            var4 = ValueImpl.prepareForAssignment(var4, var3);
            JDWP.StackFrame.SetValues.SlotInfo[] var5 = new JDWP.StackFrame.SetValues.SlotInfo[]{new JDWP.StackFrame.SetValues.SlotInfo(var3.slot(), var4)};
            PacketStream var6;
            synchronized(this.vm.state()) {
               this.validateStackFrame();
               var6 = JDWP.StackFrame.SetValues.enqueueCommand(this.vm, this.thread, this.id, var5);
            }

            try {
               JDWP.StackFrame.SetValues.waitForReply(this.vm, var6);
            } catch (JDWPException var10) {
               switch (var10.errorCode()) {
                  case 10:
                  case 13:
                  case 30:
                     throw new InvalidStackFrameException();
                  default:
                     throw var10.toJDIException();
               }
            }
         } catch (ClassNotLoadedException var11) {
            if (var4 != null) {
               throw var11;
            }
         }

      }
   }

   public List getArgumentValues() {
      this.validateStackFrame();
      MethodImpl var1 = (MethodImpl)this.location.method();
      List var2 = var1.argumentSignatures();
      int var3 = var2.size();
      JDWP.StackFrame.GetValues.SlotInfo[] var4 = new JDWP.StackFrame.GetValues.SlotInfo[var3];
      int var5;
      if (var1.isStatic()) {
         var5 = 0;
      } else {
         var5 = 1;
      }

      for(int var6 = 0; var6 < var3; ++var6) {
         char var7 = ((String)var2.get(var6)).charAt(0);
         var4[var6] = new JDWP.StackFrame.GetValues.SlotInfo(var5++, (byte)var7);
         if (var7 == 'J' || var7 == 'D') {
            ++var5;
         }
      }

      PacketStream var11;
      synchronized(this.vm.state()) {
         this.validateStackFrame();
         var11 = JDWP.StackFrame.GetValues.enqueueCommand(this.vm, this.thread, this.id, var4);
      }

      ValueImpl[] var12;
      try {
         var12 = JDWP.StackFrame.GetValues.waitForReply(this.vm, var11).values;
      } catch (JDWPException var10) {
         switch (var10.errorCode()) {
            case 10:
            case 13:
            case 30:
               throw new InvalidStackFrameException();
            default:
               throw var10.toJDIException();
         }
      }

      if (var3 != var12.length) {
         throw new InternalException("Wrong number of values returned from target VM");
      } else {
         return Arrays.asList((Value[])var12);
      }
   }

   void pop() throws IncompatibleThreadStateException {
      this.validateStackFrame();
      CommandSender var1 = new CommandSender() {
         public PacketStream send() {
            return JDWP.StackFrame.PopFrames.enqueueCommand(StackFrameImpl.this.vm, StackFrameImpl.this.thread, StackFrameImpl.this.id);
         }
      };

      try {
         PacketStream var2 = this.thread.sendResumingCommand(var1);
         JDWP.StackFrame.PopFrames.waitForReply(this.vm, var2);
      } catch (JDWPException var3) {
         switch (var3.errorCode()) {
            case 10:
               throw new IncompatibleThreadStateException("zombie");
            case 13:
               throw new IncompatibleThreadStateException("Thread not current or suspended");
            case 31:
               throw new InvalidStackFrameException("No more frames on the stack");
            default:
               throw var3.toJDIException();
         }
      }

      this.vm.state().freeze();
   }

   public String toString() {
      return this.location.toString() + " in thread " + this.thread.toString();
   }
}
