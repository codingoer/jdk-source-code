package com.sun.tools.hat.internal.model;

public class StackFrame {
   public static final int LINE_NUMBER_UNKNOWN = -1;
   public static final int LINE_NUMBER_COMPILED = -2;
   public static final int LINE_NUMBER_NATIVE = -3;
   private String methodName;
   private String methodSignature;
   private String className;
   private String sourceFileName;
   private int lineNumber;

   public StackFrame(String var1, String var2, String var3, String var4, int var5) {
      this.methodName = var1;
      this.methodSignature = var2;
      this.className = var3;
      this.sourceFileName = var4;
      this.lineNumber = var5;
   }

   public void resolve(Snapshot var1) {
   }

   public String getMethodName() {
      return this.methodName;
   }

   public String getMethodSignature() {
      return this.methodSignature;
   }

   public String getClassName() {
      return this.className;
   }

   public String getSourceFileName() {
      return this.sourceFileName;
   }

   public String getLineNumber() {
      switch (this.lineNumber) {
         case -3:
            return "(native method)";
         case -2:
            return "(compiled method)";
         case -1:
            return "(unknown)";
         default:
            return Integer.toString(this.lineNumber, 10);
      }
   }
}
