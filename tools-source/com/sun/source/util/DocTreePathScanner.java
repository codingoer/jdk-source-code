package com.sun.source.util;

import com.sun.source.doctree.DocTree;
import jdk.Exported;

@Exported
public class DocTreePathScanner extends DocTreeScanner {
   private DocTreePath path;

   public Object scan(DocTreePath var1, Object var2) {
      this.path = var1;

      Object var3;
      try {
         var3 = var1.getLeaf().accept(this, var2);
      } finally {
         this.path = null;
      }

      return var3;
   }

   public Object scan(DocTree var1, Object var2) {
      if (var1 == null) {
         return null;
      } else {
         DocTreePath var3 = this.path;
         this.path = new DocTreePath(this.path, var1);

         Object var4;
         try {
            var4 = var1.accept(this, var2);
         } finally {
            this.path = var3;
         }

         return var4;
      }
   }

   public DocTreePath getCurrentPath() {
      return this.path;
   }
}
