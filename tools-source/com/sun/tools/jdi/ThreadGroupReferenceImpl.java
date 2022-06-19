package com.sun.tools.jdi;

import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ThreadGroupReferenceImpl extends ObjectReferenceImpl implements ThreadGroupReference, VMListener {
   String name;
   ThreadGroupReference parent;
   boolean triedParent;

   protected ObjectReferenceImpl.Cache newCache() {
      return new Cache();
   }

   ThreadGroupReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
      this.vm.state().addListener(this);
   }

   protected String description() {
      return "ThreadGroupReference " + this.uniqueID();
   }

   public String name() {
      if (this.name == null) {
         try {
            this.name = JDWP.ThreadGroupReference.Name.process(this.vm, this).groupName;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.name;
   }

   public ThreadGroupReference parent() {
      if (!this.triedParent) {
         try {
            this.parent = JDWP.ThreadGroupReference.Parent.process(this.vm, this).parentGroup;
            this.triedParent = true;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.parent;
   }

   public void suspend() {
      Iterator var1 = this.threads().iterator();

      while(var1.hasNext()) {
         ThreadReference var2 = (ThreadReference)var1.next();
         var2.suspend();
      }

      var1 = this.threadGroups().iterator();

      while(var1.hasNext()) {
         ThreadGroupReference var3 = (ThreadGroupReference)var1.next();
         var3.suspend();
      }

   }

   public void resume() {
      Iterator var1 = this.threads().iterator();

      while(var1.hasNext()) {
         ThreadReference var2 = (ThreadReference)var1.next();
         var2.resume();
      }

      var1 = this.threadGroups().iterator();

      while(var1.hasNext()) {
         ThreadGroupReference var3 = (ThreadGroupReference)var1.next();
         var3.resume();
      }

   }

   private JDWP.ThreadGroupReference.Children kids() {
      JDWP.ThreadGroupReference.Children var1 = null;

      try {
         Cache var2 = (Cache)this.getCache();
         if (var2 != null) {
            var1 = var2.kids;
         }

         if (var1 == null) {
            var1 = JDWP.ThreadGroupReference.Children.process(this.vm, this);
            if (var2 != null) {
               var2.kids = var1;
               if ((this.vm.traceFlags & 16) != 0) {
                  this.vm.printTrace(this.description() + " temporarily caching children ");
               }
            }
         }

         return var1;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   public List threads() {
      return Arrays.asList((ThreadReference[])this.kids().childThreads);
   }

   public List threadGroups() {
      return Arrays.asList((ThreadGroupReference[])this.kids().childGroups);
   }

   public String toString() {
      return "instance of " + this.referenceType().name() + "(name='" + this.name() + "', id=" + this.uniqueID() + ")";
   }

   byte typeValueKey() {
      return 103;
   }

   private static class Cache extends ObjectReferenceImpl.Cache {
      JDWP.ThreadGroupReference.Children kids;

      private Cache() {
         this.kids = null;
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }
}
