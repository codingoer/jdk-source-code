package sun.rmi.rmic.iiop;

import sun.tools.java.CompilerError;

public class ContextStack {
   public static final int TOP = 1;
   public static final int METHOD = 2;
   public static final int METHOD_RETURN = 3;
   public static final int METHOD_ARGUMENT = 4;
   public static final int METHOD_EXCEPTION = 5;
   public static final int MEMBER = 6;
   public static final int MEMBER_CONSTANT = 7;
   public static final int MEMBER_STATIC = 8;
   public static final int MEMBER_TRANSIENT = 9;
   public static final int IMPLEMENTS = 10;
   public static final int EXTENDS = 11;
   private static final String[] CODE_NAMES = new String[]{"UNKNOWN ", "Top level type ", "Method ", "Return parameter ", "Parameter ", "Exception ", "Member ", "Constant member ", "Static member ", "Transient member ", "Implements ", "Extends "};
   private int currentIndex = -1;
   private int maxIndex = 100;
   private TypeContext[] stack;
   private int newCode;
   private BatchEnvironment env;
   private boolean trace;
   private TypeContext tempContext;
   private static final String TRACE_INDENT = "   ";

   public ContextStack(BatchEnvironment var1) {
      this.stack = new TypeContext[this.maxIndex];
      this.newCode = 1;
      this.env = null;
      this.trace = false;
      this.tempContext = new TypeContext();
      this.env = var1;
      var1.contextStack = this;
   }

   public boolean anyErrors() {
      return this.env.nerrors > 0;
   }

   public void setTrace(boolean var1) {
      this.trace = var1;
   }

   public boolean isTraceOn() {
      return this.trace;
   }

   public BatchEnvironment getEnv() {
      return this.env;
   }

   public void setNewContextCode(int var1) {
      this.newCode = var1;
   }

   public int getCurrentContextCode() {
      return this.newCode;
   }

   final void traceCallStack() {
      if (this.trace) {
         dumpCallStack();
      }

   }

   public static final void dumpCallStack() {
      (new Error()).printStackTrace(System.out);
   }

   private final void tracePrint(String var1, boolean var2) {
      int var3 = var1.length() + this.currentIndex * "   ".length();
      StringBuffer var4 = new StringBuffer(var3);

      for(int var5 = 0; var5 < this.currentIndex; ++var5) {
         var4.append("   ");
      }

      var4.append(var1);
      if (var2) {
         var4.append("\n");
      }

      System.out.print(var4.toString());
   }

   final void trace(String var1) {
      if (this.trace) {
         this.tracePrint(var1, false);
      }

   }

   final void traceln(String var1) {
      if (this.trace) {
         this.tracePrint(var1, true);
      }

   }

   final void traceExistingType(Type var1) {
      if (this.trace) {
         this.tempContext.set(this.newCode, var1);
         this.traceln(this.toResultString(this.tempContext, true, true));
      }

   }

   public TypeContext push(ContextElement var1) {
      ++this.currentIndex;
      if (this.currentIndex == this.maxIndex) {
         int var2 = this.maxIndex * 2;
         TypeContext[] var3 = new TypeContext[var2];
         System.arraycopy(this.stack, 0, var3, 0, this.maxIndex);
         this.maxIndex = var2;
         this.stack = var3;
      }

      TypeContext var4 = this.stack[this.currentIndex];
      if (var4 == null) {
         var4 = new TypeContext();
         this.stack[this.currentIndex] = var4;
      }

      var4.set(this.newCode, var1);
      this.traceln(this.toTrialString(var4));
      return var4;
   }

   public TypeContext pop(boolean var1) {
      if (this.currentIndex < 0) {
         throw new CompilerError("Nothing on stack!");
      } else {
         this.newCode = this.stack[this.currentIndex].getCode();
         this.traceln(this.toResultString(this.stack[this.currentIndex], var1, false));
         Type var2 = this.stack[this.currentIndex].getCandidateType();
         if (var2 != null) {
            if (var1) {
               var2.setStatus(1);
            } else {
               var2.setStatus(2);
            }
         }

         --this.currentIndex;
         if (this.currentIndex < 0) {
            if (var1) {
               Type.updateAllInvalidTypes(this);
            }

            return null;
         } else {
            return this.stack[this.currentIndex];
         }
      }
   }

   public int size() {
      return this.currentIndex + 1;
   }

   public TypeContext getContext(int var1) {
      if (this.currentIndex < var1) {
         throw new Error("Index out of range");
      } else {
         return this.stack[var1];
      }
   }

   public TypeContext getContext() {
      if (this.currentIndex < 0) {
         throw new Error("Nothing on stack!");
      } else {
         return this.stack[this.currentIndex];
      }
   }

   public boolean isParentAValue() {
      return this.currentIndex > 0 ? this.stack[this.currentIndex - 1].isValue() : false;
   }

   public TypeContext getParentContext() {
      return this.currentIndex > 0 ? this.stack[this.currentIndex - 1] : null;
   }

   public String getContextCodeString() {
      return this.currentIndex >= 0 ? CODE_NAMES[this.newCode] : CODE_NAMES[0];
   }

   public static String getContextCodeString(int var0) {
      return CODE_NAMES[var0];
   }

   private String toTrialString(TypeContext var1) {
      int var2 = var1.getCode();
      return var2 != 2 && var2 != 6 ? var1.toString() + " (trying " + var1.getTypeDescription() + ")" : var1.toString();
   }

   private String toResultString(TypeContext var1, boolean var2, boolean var3) {
      int var4 = var1.getCode();
      if (var4 != 2 && var4 != 6) {
         if (var2) {
            String var5 = var1.toString() + " --> " + var1.getTypeDescription();
            if (var3) {
               return var5 + " [Previously mapped]";
            }

            return var5;
         }
      } else if (var2) {
         return var1.toString() + " --> [Mapped]";
      }

      return var1.toString() + " [Did not map]";
   }

   public void clear() {
      for(int var1 = 0; var1 < this.stack.length; ++var1) {
         if (this.stack[var1] != null) {
            this.stack[var1].destroy();
         }
      }

   }
}
