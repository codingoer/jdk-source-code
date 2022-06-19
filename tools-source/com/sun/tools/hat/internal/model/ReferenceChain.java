package com.sun.tools.hat.internal.model;

public class ReferenceChain {
   JavaHeapObject obj;
   ReferenceChain next;

   public ReferenceChain(JavaHeapObject var1, ReferenceChain var2) {
      this.obj = var1;
      this.next = var2;
   }

   public JavaHeapObject getObj() {
      return this.obj;
   }

   public ReferenceChain getNext() {
      return this.next;
   }

   public int getDepth() {
      int var1 = 1;

      for(ReferenceChain var2 = this.next; var2 != null; var2 = var2.next) {
         ++var1;
      }

      return var1;
   }
}
