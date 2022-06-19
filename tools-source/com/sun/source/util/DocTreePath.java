package com.sun.source.util;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import java.util.Iterator;
import jdk.Exported;

@Exported
public class DocTreePath implements Iterable {
   private final TreePath treePath;
   private final DocCommentTree docComment;
   private final DocTree leaf;
   private final DocTreePath parent;

   public static DocTreePath getPath(TreePath var0, DocCommentTree var1, DocTree var2) {
      return getPath(new DocTreePath(var0, var1), var2);
   }

   public static DocTreePath getPath(DocTreePath var0, DocTree var1) {
      var0.getClass();
      var1.getClass();
      if (var0.getLeaf() == var1) {
         return var0;
      } else {
         try {
            class PathFinder extends DocTreePathScanner {
               public DocTreePath scan(DocTree var1, DocTree var2) {
                  if (var1 == var2) {
                     throw new Result(new DocTreePath(this.getCurrentPath(), var2));
                  } else {
                     return (DocTreePath)super.scan((DocTree)var1, var2);
                  }
               }
            }

            (new PathFinder()).scan((DocTreePath)var0, (Object)var1);
            return null;
         } catch (Result var3) {
            class Result extends Error {
               static final long serialVersionUID = -5942088234594905625L;
               DocTreePath path;

               Result(DocTreePath var1) {
                  this.path = var1;
               }
            }

            return var3.path;
         }
      }
   }

   public DocTreePath(TreePath var1, DocCommentTree var2) {
      var1.getClass();
      var2.getClass();
      this.treePath = var1;
      this.docComment = var2;
      this.parent = null;
      this.leaf = var2;
   }

   public DocTreePath(DocTreePath var1, DocTree var2) {
      if (var2.getKind() == DocTree.Kind.DOC_COMMENT) {
         throw new IllegalArgumentException("Use DocTreePath(TreePath, DocCommentTree) to construct DocTreePath for a DocCommentTree.");
      } else {
         this.treePath = var1.treePath;
         this.docComment = var1.docComment;
         this.parent = var1;
         this.leaf = var2;
      }
   }

   public TreePath getTreePath() {
      return this.treePath;
   }

   public DocCommentTree getDocComment() {
      return this.docComment;
   }

   public DocTree getLeaf() {
      return this.leaf;
   }

   public DocTreePath getParentPath() {
      return this.parent;
   }

   public Iterator iterator() {
      return new Iterator() {
         private DocTreePath next = DocTreePath.this;

         public boolean hasNext() {
            return this.next != null;
         }

         public DocTree next() {
            DocTree var1 = this.next.leaf;
            this.next = this.next.parent;
            return var1;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
