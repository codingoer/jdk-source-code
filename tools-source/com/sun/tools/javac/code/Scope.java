package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.Iterator;

public class Scope {
   private int shared;
   public Scope next;
   public Symbol owner;
   Entry[] table;
   int hashMask;
   public Entry elems;
   int nelems;
   List listeners;
   private static final Entry sentinel = new Entry((Symbol)null, (Entry)null, (Entry)null, (Scope)null);
   private static final int INITIAL_SIZE = 16;
   public static final Scope emptyScope = new Scope((Scope)null, (Symbol)null, new Entry[0]);
   static final Filter noFilter = new Filter() {
      public boolean accepts(Symbol var1) {
         return true;
      }
   };

   private Scope(Scope var1, Symbol var2, Entry[] var3) {
      this.nelems = 0;
      this.listeners = List.nil();
      this.next = var1;
      Assert.check(emptyScope == null || var2 != null);
      this.owner = var2;
      this.table = var3;
      this.hashMask = var3.length - 1;
   }

   private Scope(Scope var1, Symbol var2, Entry[] var3, int var4) {
      this(var1, var2, var3);
      this.nelems = var4;
   }

   public Scope(Symbol var1) {
      this((Scope)null, var1, new Entry[16]);
   }

   public Scope dup() {
      return this.dup(this.owner);
   }

   public Scope dup(Symbol var1) {
      Scope var2 = new Scope(this, var1, this.table, this.nelems);
      ++this.shared;
      return var2;
   }

   public Scope dupUnshared() {
      return new Scope(this, this.owner, (Entry[])this.table.clone(), this.nelems);
   }

   public Scope leave() {
      Assert.check(this.shared == 0);
      if (this.table != this.next.table) {
         return this.next;
      } else {
         while(this.elems != null) {
            int var1 = this.getIndex(this.elems.sym.name);
            Entry var2 = this.table[var1];
            Assert.check(var2 == this.elems, (Object)this.elems.sym);
            this.table[var1] = this.elems.shadowed;
            this.elems = this.elems.sibling;
         }

         Assert.check(this.next.shared > 0);
         --this.next.shared;
         this.next.nelems = this.nelems;
         return this.next;
      }
   }

   private void dble() {
      Assert.check(this.shared == 0);
      Entry[] var1 = this.table;
      Entry[] var2 = new Entry[var1.length * 2];

      for(Scope var3 = this; var3 != null; var3 = var3.next) {
         if (var3.table == var1) {
            Assert.check(var3 == this || var3.shared != 0);
            var3.table = var2;
            var3.hashMask = var2.length - 1;
         }
      }

      int var6 = 0;
      int var4 = var1.length;

      while(true) {
         --var4;
         if (var4 < 0) {
            this.nelems = var6;
            return;
         }

         Entry var5 = var1[var4];
         if (var5 != null && var5 != sentinel) {
            this.table[this.getIndex(var5.sym.name)] = var5;
            ++var6;
         }
      }
   }

   public void enter(Symbol var1) {
      Assert.check(this.shared == 0);
      this.enter(var1, this);
   }

   public void enter(Symbol var1, Scope var2) {
      this.enter(var1, var2, var2, false);
   }

   public void enter(Symbol var1, Scope var2, Scope var3, boolean var4) {
      Assert.check(this.shared == 0);
      if (this.nelems * 3 >= this.hashMask * 2) {
         this.dble();
      }

      int var5 = this.getIndex(var1.name);
      Entry var6 = this.table[var5];
      if (var6 == null) {
         var6 = sentinel;
         ++this.nelems;
      }

      Entry var7 = this.makeEntry(var1, var6, this.elems, var2, var3, var4);
      this.table[var5] = var7;
      this.elems = var7;

      for(List var8 = this.listeners; var8.nonEmpty(); var8 = var8.tail) {
         ((ScopeListener)var8.head).symbolAdded(var1, this);
      }

   }

   Entry makeEntry(Symbol var1, Entry var2, Entry var3, Scope var4, Scope var5, boolean var6) {
      return new Entry(var1, var2, var3, var4);
   }

   public void addScopeListener(ScopeListener var1) {
      this.listeners = this.listeners.prepend(var1);
   }

   public void remove(final Symbol var1) {
      Assert.check(this.shared == 0);
      Entry var2 = this.lookup(var1.name, new Filter() {
         public boolean accepts(Symbol var1x) {
            return var1x == var1;
         }
      });
      if (var2.scope != null) {
         int var3 = this.getIndex(var1.name);
         Entry var4 = this.table[var3];
         if (var4 == var2) {
            this.table[var3] = var2.shadowed;
         } else {
            while(var4.shadowed != var2) {
               var4 = var4.shadowed;
            }

            var4.shadowed = var2.shadowed;
         }

         var4 = this.elems;
         if (var4 == var2) {
            this.elems = var2.sibling;
         } else {
            while(var4.sibling != var2) {
               var4 = var4.sibling;
            }

            var4.sibling = var2.sibling;
         }

         for(List var5 = this.listeners; var5.nonEmpty(); var5 = var5.tail) {
            ((ScopeListener)var5.head).symbolRemoved(var1, this);
         }

      }
   }

   public void enterIfAbsent(Symbol var1) {
      Assert.check(this.shared == 0);

      Entry var2;
      for(var2 = this.lookup(var1.name); var2.scope == this && var2.sym.kind != var1.kind; var2 = var2.next()) {
      }

      if (var2.scope != this) {
         this.enter(var1);
      }

   }

   public boolean includes(Symbol var1) {
      for(Entry var2 = this.lookup(var1.name); var2.scope == this; var2 = var2.next()) {
         if (var2.sym == var1) {
            return true;
         }
      }

      return false;
   }

   public Entry lookup(Name var1) {
      return this.lookup(var1, noFilter);
   }

   public Entry lookup(Name var1, Filter var2) {
      Entry var3 = this.table[this.getIndex(var1)];
      if (var3 != null && var3 != sentinel) {
         while(var3.scope != null && (var3.sym.name != var1 || !var2.accepts(var3.sym))) {
            var3 = var3.shadowed;
         }

         return var3;
      } else {
         return sentinel;
      }
   }

   int getIndex(Name var1) {
      int var2 = var1.hashCode();
      int var3 = var2 & this.hashMask;
      int var4 = this.hashMask - (var2 + (var2 >> 16) << 1);
      int var5 = -1;

      while(true) {
         Entry var6 = this.table[var3];
         if (var6 == null) {
            return var5 >= 0 ? var5 : var3;
         }

         if (var6 == sentinel) {
            if (var5 < 0) {
               var5 = var3;
            }
         } else if (var6.sym.name == var1) {
            return var3;
         }

         var3 = var3 + var4 & this.hashMask;
      }
   }

   public boolean anyMatch(Filter var1) {
      return this.getElements(var1).iterator().hasNext();
   }

   public Iterable getElements() {
      return this.getElements(noFilter);
   }

   public Iterable getElements(final Filter var1) {
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               private Scope currScope = Scope.this;
               private Entry currEntry;

               {
                  this.currEntry = Scope.this.elems;
                  this.update();
               }

               public boolean hasNext() {
                  return this.currEntry != null;
               }

               public Symbol next() {
                  Symbol var1x = this.currEntry == null ? null : this.currEntry.sym;
                  if (this.currEntry != null) {
                     this.currEntry = this.currEntry.sibling;
                  }

                  this.update();
                  return var1x;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }

               private void update() {
                  this.skipToNextMatchingEntry();

                  while(this.currEntry == null && this.currScope.next != null) {
                     this.currScope = this.currScope.next;
                     this.currEntry = this.currScope.elems;
                     this.skipToNextMatchingEntry();
                  }

               }

               void skipToNextMatchingEntry() {
                  while(this.currEntry != null && !var1.accepts(this.currEntry.sym)) {
                     this.currEntry = this.currEntry.sibling;
                  }

               }
            };
         }
      };
   }

   public Iterable getElementsByName(Name var1) {
      return this.getElementsByName(var1, noFilter);
   }

   public Iterable getElementsByName(final Name var1, final Filter var2) {
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               Entry currentEntry = Scope.this.lookup(var1, var2);

               public boolean hasNext() {
                  return this.currentEntry.scope != null;
               }

               public Symbol next() {
                  Entry var1x = this.currentEntry;
                  this.currentEntry = this.currentEntry.next(var2);
                  return var1x.sym;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Scope[");

      for(Scope var2 = this; var2 != null; var2 = var2.next) {
         if (var2 != this) {
            var1.append(" | ");
         }

         for(Entry var3 = var2.elems; var3 != null; var3 = var3.sibling) {
            if (var3 != var2.elems) {
               var1.append(", ");
            }

            var1.append(var3.sym);
         }
      }

      var1.append("]");
      return var1.toString();
   }

   // $FF: synthetic method
   Scope(Scope var1, Symbol var2, Entry[] var3, Object var4) {
      this(var1, var2, var3);
   }

   public static class ErrorScope extends Scope {
      ErrorScope(Scope var1, Symbol var2, Entry[] var3) {
         super(var1, var2, var3, null);
      }

      public ErrorScope(Symbol var1) {
         super(var1);
      }

      public Scope dup() {
         return new ErrorScope(this, this.owner, this.table);
      }

      public Scope dupUnshared() {
         return new ErrorScope(this, this.owner, (Entry[])this.table.clone());
      }

      public Entry lookup(Name var1) {
         Entry var2 = super.lookup(var1);
         return var2.scope == null ? new Entry(this.owner, (Entry)null, (Entry)null, (Scope)null) : var2;
      }
   }

   public static class CompoundScope extends Scope implements ScopeListener {
      public static final Entry[] emptyTable = new Entry[0];
      private List subScopes = List.nil();
      private int mark = 0;

      public CompoundScope(Symbol var1) {
         super((Scope)null, var1, emptyTable, null);
      }

      public void addSubScope(Scope var1) {
         if (var1 != null) {
            this.subScopes = this.subScopes.prepend(var1);
            var1.addScopeListener(this);
            ++this.mark;
            Iterator var2 = this.listeners.iterator();

            while(var2.hasNext()) {
               ScopeListener var3 = (ScopeListener)var2.next();
               var3.symbolAdded((Symbol)null, this);
            }
         }

      }

      public void symbolAdded(Symbol var1, Scope var2) {
         ++this.mark;
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            ScopeListener var4 = (ScopeListener)var3.next();
            var4.symbolAdded(var1, var2);
         }

      }

      public void symbolRemoved(Symbol var1, Scope var2) {
         ++this.mark;
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            ScopeListener var4 = (ScopeListener)var3.next();
            var4.symbolRemoved(var1, var2);
         }

      }

      public int getMark() {
         return this.mark;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("CompoundScope{");
         String var2 = "";

         for(Iterator var3 = this.subScopes.iterator(); var3.hasNext(); var2 = ",") {
            Scope var4 = (Scope)var3.next();
            var1.append(var2);
            var1.append(var4);
         }

         var1.append("}");
         return var1.toString();
      }

      public Iterable getElements(final Filter var1) {
         return new Iterable() {
            public Iterator iterator() {
               return new CompoundScopeIterator(CompoundScope.this.subScopes) {
                  Iterator nextIterator(Scope var1x) {
                     return var1x.getElements(var1).iterator();
                  }
               };
            }
         };
      }

      public Iterable getElementsByName(final Name var1, final Filter var2) {
         return new Iterable() {
            public Iterator iterator() {
               return new CompoundScopeIterator(CompoundScope.this.subScopes) {
                  Iterator nextIterator(Scope var1x) {
                     return var1x.getElementsByName(var1, var2).iterator();
                  }
               };
            }
         };
      }

      public Entry lookup(Name var1, Filter var2) {
         throw new UnsupportedOperationException();
      }

      public Scope dup(Symbol var1) {
         throw new UnsupportedOperationException();
      }

      public void enter(Symbol var1, Scope var2, Scope var3, boolean var4) {
         throw new UnsupportedOperationException();
      }

      public void remove(Symbol var1) {
         throw new UnsupportedOperationException();
      }

      abstract class CompoundScopeIterator implements Iterator {
         private Iterator currentIterator;
         private List scopesToScan;

         public CompoundScopeIterator(List var2) {
            this.scopesToScan = var2;
            this.update();
         }

         abstract Iterator nextIterator(Scope var1);

         public boolean hasNext() {
            return this.currentIterator != null;
         }

         public Symbol next() {
            Symbol var1 = (Symbol)this.currentIterator.next();
            if (!this.currentIterator.hasNext()) {
               this.update();
            }

            return var1;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }

         private void update() {
            while(true) {
               if (this.scopesToScan.nonEmpty()) {
                  this.currentIterator = this.nextIterator((Scope)this.scopesToScan.head);
                  this.scopesToScan = this.scopesToScan.tail;
                  if (!this.currentIterator.hasNext()) {
                     continue;
                  }

                  return;
               }

               this.currentIterator = null;
               return;
            }
         }
      }
   }

   public static class DelegatedScope extends Scope {
      Scope delegatee;
      public static final Entry[] emptyTable = new Entry[0];

      public DelegatedScope(Scope var1) {
         super(var1, var1.owner, emptyTable, null);
         this.delegatee = var1;
      }

      public Scope dup() {
         return new DelegatedScope(this.next);
      }

      public Scope dupUnshared() {
         return new DelegatedScope(this.next);
      }

      public Scope leave() {
         return this.next;
      }

      public void enter(Symbol var1) {
      }

      public void enter(Symbol var1, Scope var2) {
      }

      public void remove(Symbol var1) {
         throw new AssertionError(var1);
      }

      public Entry lookup(Name var1) {
         return this.delegatee.lookup(var1);
      }
   }

   public static class StarImportScope extends ImportScope implements ScopeListener {
      public StarImportScope(Symbol var1) {
         super(var1);
      }

      public void importAll(Scope var1) {
         for(Entry var2 = var1.elems; var2 != null; var2 = var2.sibling) {
            if (var2.sym.kind == 2 && !this.includes(var2.sym)) {
               this.enter(var2.sym, var1);
            }
         }

         var1.addScopeListener(this);
      }

      public void symbolRemoved(Symbol var1, Scope var2) {
         this.remove(var1);
      }

      public void symbolAdded(Symbol var1, Scope var2) {
      }
   }

   public static class ImportScope extends Scope {
      public ImportScope(Symbol var1) {
         super(var1);
      }

      Entry makeEntry(Symbol var1, Entry var2, Entry var3, Scope var4, final Scope var5, final boolean var6) {
         return new Entry(var1, var2, var3, var4) {
            public Scope getOrigin() {
               return var5;
            }

            public boolean isStaticallyImported() {
               return var6;
            }
         };
      }
   }

   public static class Entry {
      public Symbol sym;
      private Entry shadowed;
      public Entry sibling;
      public Scope scope;

      public Entry(Symbol var1, Entry var2, Entry var3, Scope var4) {
         this.sym = var1;
         this.shadowed = var2;
         this.sibling = var3;
         this.scope = var4;
      }

      public Entry next() {
         return this.shadowed;
      }

      public Entry next(Filter var1) {
         return this.shadowed.sym != null && !var1.accepts(this.shadowed.sym) ? this.shadowed.next(var1) : this.shadowed;
      }

      public boolean isStaticallyImported() {
         return false;
      }

      public Scope getOrigin() {
         return this.scope;
      }
   }

   public interface ScopeListener {
      void symbolAdded(Symbol var1, Scope var2);

      void symbolRemoved(Symbol var1, Scope var2);
   }
}
