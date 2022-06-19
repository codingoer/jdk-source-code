package com.sun.source.util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import java.util.Iterator;
import jdk.Exported;

@Exported
public class TreePath implements Iterable {
   private CompilationUnitTree compilationUnit;
   private Tree leaf;
   private TreePath parent;

   public static TreePath getPath(CompilationUnitTree var0, Tree var1) {
      return getPath(new TreePath(var0), var1);
   }

   public static TreePath getPath(TreePath var0, Tree var1) {
      var0.getClass();
      var1.getClass();
      if (var0.getLeaf() == var1) {
         return var0;
      } else {
         try {
            class PathFinder extends TreePathScanner {
               public TreePath scan(Tree var1, Tree var2) {
                  if (var1 == var2) {
                     throw new Result(new TreePath(this.getCurrentPath(), var2));
                  } else {
                     return (TreePath)super.scan((Tree)var1, var2);
                  }
               }
            }

            (new PathFinder()).scan((TreePath)var0, (Object)var1);
            return null;
         } catch (Result var3) {
            class Result extends Error {
               static final long serialVersionUID = -5942088234594905625L;
               TreePath path;

               Result(TreePath var1) {
                  this.path = var1;
               }
            }

            return var3.path;
         }
      }
   }

   public TreePath(CompilationUnitTree var1) {
      this((TreePath)null, var1);
   }

   public TreePath(TreePath var1, Tree var2) {
      if (var2.getKind() == Tree.Kind.COMPILATION_UNIT) {
         this.compilationUnit = (CompilationUnitTree)var2;
         this.parent = null;
      } else {
         this.compilationUnit = var1.compilationUnit;
         this.parent = var1;
      }

      this.leaf = var2;
   }

   public CompilationUnitTree getCompilationUnit() {
      return this.compilationUnit;
   }

   public Tree getLeaf() {
      return this.leaf;
   }

   public TreePath getParentPath() {
      return this.parent;
   }

   public Iterator iterator() {
      return new Iterator() {
         private TreePath next = TreePath.this;

         public boolean hasNext() {
            return this.next != null;
         }

         public Tree next() {
            Tree var1 = this.next.leaf;
            this.next = this.next.parent;
            return var1;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
