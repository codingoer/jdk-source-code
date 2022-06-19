package com.sun.tools.example.debug.tty;

import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import java.util.Iterator;
import java.util.List;

class ThreadIterator implements Iterator {
   Iterator it = null;
   ThreadGroupIterator tgi;

   ThreadIterator(ThreadGroupReference var1) {
      this.tgi = new ThreadGroupIterator(var1);
   }

   ThreadIterator(List var1) {
      this.tgi = new ThreadGroupIterator(var1);
   }

   ThreadIterator() {
      this.tgi = new ThreadGroupIterator();
   }

   public boolean hasNext() {
      while(this.it == null || !this.it.hasNext()) {
         if (!this.tgi.hasNext()) {
            return false;
         }

         this.it = this.tgi.nextThreadGroup().threads().iterator();
      }

      return true;
   }

   public ThreadReference next() {
      return (ThreadReference)this.it.next();
   }

   public ThreadReference nextThread() {
      return this.next();
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
