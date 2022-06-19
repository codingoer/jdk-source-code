package com.sun.tools.javac.comp;

import com.sun.tools.javac.tree.JCTree;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Env implements Iterable {
   public Env next = null;
   public Env outer = null;
   public JCTree tree;
   public JCTree.JCCompilationUnit toplevel;
   public JCTree.JCClassDecl enclClass;
   public JCTree.JCMethodDecl enclMethod;
   public Object info;
   public boolean baseClause = false;

   public Env(JCTree var1, Object var2) {
      this.tree = var1;
      this.toplevel = null;
      this.enclClass = null;
      this.enclMethod = null;
      this.info = var2;
   }

   public Env dup(JCTree var1, Object var2) {
      return this.dupto(new Env(var1, var2));
   }

   public Env dupto(Env var1) {
      var1.next = this;
      var1.outer = this.outer;
      var1.toplevel = this.toplevel;
      var1.enclClass = this.enclClass;
      var1.enclMethod = this.enclMethod;
      return var1;
   }

   public Env dup(JCTree var1) {
      return this.dup(var1, this.info);
   }

   public Env enclosing(JCTree.Tag var1) {
      Env var2;
      for(var2 = this; var2 != null && !var2.tree.hasTag(var1); var2 = var2.next) {
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Env[").append(this.info);
      if (this.outer != null) {
         var1.append(",outer=").append(this.outer);
      }

      var1.append("]");
      return var1.toString();
   }

   public Iterator iterator() {
      return new Iterator() {
         Env next = Env.this;

         public boolean hasNext() {
            return this.next.outer != null;
         }

         public Env next() {
            if (this.hasNext()) {
               Env var1 = this.next;
               this.next = var1.outer;
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
}
