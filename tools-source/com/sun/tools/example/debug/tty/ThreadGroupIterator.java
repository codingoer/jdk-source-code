package com.sun.tools.example.debug.tty;

import com.sun.jdi.ThreadGroupReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

class ThreadGroupIterator implements Iterator {
   private final Stack stack;

   ThreadGroupIterator(List var1) {
      this.stack = new Stack();
      this.push(var1);
   }

   ThreadGroupIterator(ThreadGroupReference var1) {
      this.stack = new Stack();
      ArrayList var2 = new ArrayList();
      var2.add(var1);
      this.push(var2);
   }

   ThreadGroupIterator() {
      this(Env.vm().topLevelThreadGroups());
   }

   private Iterator top() {
      return (Iterator)this.stack.peek();
   }

   private void push(List var1) {
      this.stack.push(var1.iterator());

      while(!this.stack.isEmpty() && !this.top().hasNext()) {
         this.stack.pop();
      }

   }

   public boolean hasNext() {
      return !this.stack.isEmpty();
   }

   public ThreadGroupReference next() {
      return this.nextThreadGroup();
   }

   public ThreadGroupReference nextThreadGroup() {
      ThreadGroupReference var1 = (ThreadGroupReference)this.top().next();
      this.push(var1.threadGroups());
      return var1;
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   static ThreadGroupReference find(String var0) {
      ThreadGroupIterator var1 = new ThreadGroupIterator();

      ThreadGroupReference var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = var1.nextThreadGroup();
      } while(!var2.name().equals(var0));

      return var2;
   }
}
