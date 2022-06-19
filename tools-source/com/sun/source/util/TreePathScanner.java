package com.sun.source.util;

import com.sun.source.tree.Tree;
import jdk.Exported;

@Exported
public class TreePathScanner extends TreeScanner {
   private TreePath path;

   public Object scan(TreePath var1, Object var2) {
      this.path = var1;

      Object var3;
      try {
         var3 = var1.getLeaf().accept(this, var2);
      } finally {
         this.path = null;
      }

      return var3;
   }

   public Object scan(Tree var1, Object var2) {
      if (var1 == null) {
         return null;
      } else {
         TreePath var3 = this.path;
         this.path = new TreePath(this.path, var1);

         Object var4;
         try {
            var4 = var1.accept(this, var2);
         } finally {
            this.path = var3;
         }

         return var4;
      }
   }

   public TreePath getCurrentPath() {
      return this.path;
   }
}
