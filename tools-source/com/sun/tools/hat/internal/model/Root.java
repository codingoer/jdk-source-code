package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.util.Misc;

public class Root {
   private long id;
   private long refererId;
   private int index;
   private int type;
   private String description;
   private JavaHeapObject referer;
   private StackTrace stackTrace;
   public static final int INVALID_TYPE = 0;
   public static final int UNKNOWN = 1;
   public static final int SYSTEM_CLASS = 2;
   public static final int NATIVE_LOCAL = 3;
   public static final int NATIVE_STATIC = 4;
   public static final int THREAD_BLOCK = 5;
   public static final int BUSY_MONITOR = 6;
   public static final int JAVA_LOCAL = 7;
   public static final int NATIVE_STACK = 8;
   public static final int JAVA_STATIC = 9;

   public Root(long var1, long var3, int var5, String var6) {
      this(var1, var3, var5, var6, (StackTrace)null);
   }

   public Root(long var1, long var3, int var5, String var6, StackTrace var7) {
      this.index = -1;
      this.referer = null;
      this.stackTrace = null;
      this.id = var1;
      this.refererId = var3;
      this.type = var5;
      this.description = var6;
      this.stackTrace = var7;
   }

   public long getId() {
      return this.id;
   }

   public String getIdString() {
      return Misc.toHex(this.id);
   }

   public String getDescription() {
      return "".equals(this.description) ? this.getTypeName() + " Reference" : this.description;
   }

   public int getType() {
      return this.type;
   }

   public String getTypeName() {
      switch (this.type) {
         case 0:
            return "Invalid (?!?)";
         case 1:
            return "Unknown";
         case 2:
            return "System Class";
         case 3:
            return "JNI Local";
         case 4:
            return "JNI Global";
         case 5:
            return "Thread Block";
         case 6:
            return "Busy Monitor";
         case 7:
            return "Java Local";
         case 8:
            return "Native Stack (possibly Java local)";
         case 9:
            return "Java Static";
         default:
            return "??";
      }
   }

   public Root mostInteresting(Root var1) {
      return var1.type > this.type ? var1 : this;
   }

   public JavaHeapObject getReferer() {
      return this.referer;
   }

   public StackTrace getStackTrace() {
      return this.stackTrace;
   }

   public int getIndex() {
      return this.index;
   }

   void resolve(Snapshot var1) {
      if (this.refererId != 0L) {
         this.referer = var1.findThing(this.refererId);
      }

      if (this.stackTrace != null) {
         this.stackTrace.resolve(var1);
      }

   }

   void setIndex(int var1) {
      this.index = var1;
   }
}
