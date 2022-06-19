package com.sun.tools.javac.model;

import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredMemberList extends AbstractList {
   private final Scope scope;

   public FilteredMemberList(Scope var1) {
      this.scope = var1;
   }

   public int size() {
      int var1 = 0;

      for(Scope.Entry var2 = this.scope.elems; var2 != null; var2 = var2.sibling) {
         if (!unwanted(var2.sym)) {
            ++var1;
         }
      }

      return var1;
   }

   public Symbol get(int var1) {
      for(Scope.Entry var2 = this.scope.elems; var2 != null; var2 = var2.sibling) {
         if (!unwanted(var2.sym) && var1-- == 0) {
            return var2.sym;
         }
      }

      throw new IndexOutOfBoundsException();
   }

   public Iterator iterator() {
      return new Iterator() {
         private Scope.Entry nextEntry;
         private boolean hasNextForSure;

         {
            this.nextEntry = FilteredMemberList.this.scope.elems;
            this.hasNextForSure = false;
         }

         public boolean hasNext() {
            if (this.hasNextForSure) {
               return true;
            } else {
               while(this.nextEntry != null && FilteredMemberList.unwanted(this.nextEntry.sym)) {
                  this.nextEntry = this.nextEntry.sibling;
               }

               this.hasNextForSure = this.nextEntry != null;
               return this.hasNextForSure;
            }
         }

         public Symbol next() {
            if (this.hasNext()) {
               Symbol var1 = this.nextEntry.sym;
               this.nextEntry = this.nextEntry.sibling;
               this.hasNextForSure = false;
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static boolean unwanted(Symbol var0) {
      return var0 == null || (var0.flags() & 4096L) != 0L;
   }
}
