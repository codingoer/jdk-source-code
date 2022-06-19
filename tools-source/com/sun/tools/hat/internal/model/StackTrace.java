package com.sun.tools.hat.internal.model;

public class StackTrace {
   private StackFrame[] frames;

   public StackTrace(StackFrame[] var1) {
      this.frames = var1;
   }

   public StackTrace traceForDepth(int var1) {
      if (var1 >= this.frames.length) {
         return this;
      } else {
         StackFrame[] var2 = new StackFrame[var1];
         System.arraycopy(this.frames, 0, var2, 0, var1);
         return new StackTrace(var2);
      }
   }

   public void resolve(Snapshot var1) {
      for(int var2 = 0; var2 < this.frames.length; ++var2) {
         this.frames[var2].resolve(var1);
      }

   }

   public StackFrame[] getFrames() {
      return this.frames;
   }
}
