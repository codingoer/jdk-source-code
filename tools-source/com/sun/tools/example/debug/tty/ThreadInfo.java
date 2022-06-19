package com.sun.tools.example.debug.tty;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class ThreadInfo {
   private static List threads = Collections.synchronizedList(new ArrayList());
   private static boolean gotInitialThreads = false;
   private static ThreadInfo current = null;
   private static ThreadGroupReference group = null;
   private final ThreadReference thread;
   private int currentFrameIndex = 0;

   private ThreadInfo(ThreadReference var1) {
      this.thread = var1;
      if (var1 == null) {
         MessageOutput.fatalError("Internal error: null ThreadInfo created");
      }

   }

   private static void initThreads() {
      if (!gotInitialThreads) {
         Iterator var0 = Env.vm().allThreads().iterator();

         while(var0.hasNext()) {
            ThreadReference var1 = (ThreadReference)var0.next();
            threads.add(new ThreadInfo(var1));
         }

         gotInitialThreads = true;
      }

   }

   static void addThread(ThreadReference var0) {
      synchronized(threads) {
         initThreads();
         ThreadInfo var2 = new ThreadInfo(var0);
         if (getThreadInfo(var0) == null) {
            threads.add(var2);
         }

      }
   }

   static void removeThread(ThreadReference var0) {
      if (var0.equals(current)) {
         String var1;
         try {
            var1 = "\"" + var0.name() + "\"";
         } catch (Exception var3) {
            var1 = "";
         }

         setCurrentThread((ThreadReference)null);
         MessageOutput.println();
         MessageOutput.println("Current thread died. Execution continuing...", var1);
      }

      threads.remove(getThreadInfo(var0));
   }

   static List threads() {
      synchronized(threads) {
         initThreads();
         return new ArrayList(threads);
      }
   }

   static void invalidateAll() {
      current = null;
      group = null;
      synchronized(threads) {
         Iterator var1 = threads().iterator();

         while(var1.hasNext()) {
            ThreadInfo var2 = (ThreadInfo)var1.next();
            var2.invalidate();
         }

      }
   }

   static void setThreadGroup(ThreadGroupReference var0) {
      group = var0;
   }

   static void setCurrentThread(ThreadReference var0) {
      if (var0 == null) {
         setCurrentThreadInfo((ThreadInfo)null);
      } else {
         ThreadInfo var1 = getThreadInfo(var0);
         setCurrentThreadInfo(var1);
      }

   }

   static void setCurrentThreadInfo(ThreadInfo var0) {
      current = var0;
      if (current != null) {
         current.invalidate();
      }

   }

   static ThreadInfo getCurrentThreadInfo() {
      return current;
   }

   ThreadReference getThread() {
      return this.thread;
   }

   static ThreadGroupReference group() {
      if (group == null) {
         setThreadGroup((ThreadGroupReference)Env.vm().topLevelThreadGroups().get(0));
      }

      return group;
   }

   static ThreadInfo getThreadInfo(long var0) {
      ThreadInfo var2 = null;
      synchronized(threads) {
         Iterator var4 = threads().iterator();

         while(var4.hasNext()) {
            ThreadInfo var5 = (ThreadInfo)var4.next();
            if (var5.thread.uniqueID() == var0) {
               var2 = var5;
               break;
            }
         }

         return var2;
      }
   }

   static ThreadInfo getThreadInfo(ThreadReference var0) {
      return getThreadInfo(var0.uniqueID());
   }

   static ThreadInfo getThreadInfo(String var0) {
      ThreadInfo var1 = null;
      if (var0.startsWith("t@")) {
         var0 = var0.substring(2);
      }

      try {
         long var2 = Long.decode(var0);
         var1 = getThreadInfo(var2);
      } catch (NumberFormatException var4) {
         var1 = null;
      }

      return var1;
   }

   List getStack() throws IncompatibleThreadStateException {
      return this.thread.frames();
   }

   StackFrame getCurrentFrame() throws IncompatibleThreadStateException {
      return this.thread.frameCount() == 0 ? null : this.thread.frame(this.currentFrameIndex);
   }

   void invalidate() {
      this.currentFrameIndex = 0;
   }

   private void assureSuspended() throws IncompatibleThreadStateException {
      if (!this.thread.isSuspended()) {
         throw new IncompatibleThreadStateException();
      }
   }

   int getCurrentFrameIndex() {
      return this.currentFrameIndex;
   }

   void setCurrentFrameIndex(int var1) throws IncompatibleThreadStateException {
      this.assureSuspended();
      if (var1 >= 0 && var1 < this.thread.frameCount()) {
         this.currentFrameIndex = var1;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void up(int var1) throws IncompatibleThreadStateException {
      this.setCurrentFrameIndex(this.currentFrameIndex + var1);
   }

   void down(int var1) throws IncompatibleThreadStateException {
      this.setCurrentFrameIndex(this.currentFrameIndex - var1);
   }
}
