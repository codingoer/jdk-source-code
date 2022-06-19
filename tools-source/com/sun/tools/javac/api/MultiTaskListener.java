package com.sun.tools.javac.api;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;
import java.util.Arrays;
import java.util.Collection;

public class MultiTaskListener implements TaskListener {
   public static final Context.Key taskListenerKey = new Context.Key();
   TaskListener[] listeners = new TaskListener[0];
   ClientCodeWrapper ccw;

   public static MultiTaskListener instance(Context var0) {
      MultiTaskListener var1 = (MultiTaskListener)var0.get(taskListenerKey);
      if (var1 == null) {
         var1 = new MultiTaskListener(var0);
      }

      return var1;
   }

   protected MultiTaskListener(Context var1) {
      var1.put((Context.Key)taskListenerKey, (Object)this);
      this.ccw = ClientCodeWrapper.instance(var1);
   }

   public Collection getTaskListeners() {
      return Arrays.asList(this.listeners);
   }

   public boolean isEmpty() {
      return this.listeners.length == 0;
   }

   public void add(TaskListener var1) {
      TaskListener[] var2 = this.listeners;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TaskListener var5 = var2[var4];
         if (this.ccw.unwrap(var5) == var1) {
            throw new IllegalStateException();
         }
      }

      this.listeners = (TaskListener[])Arrays.copyOf(this.listeners, this.listeners.length + 1);
      this.listeners[this.listeners.length - 1] = this.ccw.wrap(var1);
   }

   public void remove(TaskListener var1) {
      for(int var2 = 0; var2 < this.listeners.length; ++var2) {
         if (this.ccw.unwrap(this.listeners[var2]) == var1) {
            TaskListener[] var3 = new TaskListener[this.listeners.length - 1];
            System.arraycopy(this.listeners, 0, var3, 0, var2);
            System.arraycopy(this.listeners, var2 + 1, var3, var2, var3.length - var2);
            this.listeners = var3;
            break;
         }
      }

   }

   public void started(TaskEvent var1) {
      TaskListener[] var2 = this.listeners;
      TaskListener[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TaskListener var6 = var3[var5];
         var6.started(var1);
      }

   }

   public void finished(TaskEvent var1) {
      TaskListener[] var2 = this.listeners;
      TaskListener[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TaskListener var6 = var3[var5];
         var6.finished(var1);
      }

   }

   public String toString() {
      return Arrays.toString(this.listeners);
   }
}
