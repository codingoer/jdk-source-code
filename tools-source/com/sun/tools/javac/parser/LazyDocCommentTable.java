package com.sun.tools.javac.parser;

import com.sun.tools.javac.tree.DCTree;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;
import java.util.HashMap;
import java.util.Map;

public class LazyDocCommentTable implements DocCommentTable {
   ParserFactory fac;
   DiagnosticSource diagSource;
   Map table;

   LazyDocCommentTable(ParserFactory var1) {
      this.fac = var1;
      this.diagSource = var1.log.currentSource();
      this.table = new HashMap();
   }

   public boolean hasComment(JCTree var1) {
      return this.table.containsKey(var1);
   }

   public Tokens.Comment getComment(JCTree var1) {
      Entry var2 = (Entry)this.table.get(var1);
      return var2 == null ? null : var2.comment;
   }

   public String getCommentText(JCTree var1) {
      Tokens.Comment var2 = this.getComment(var1);
      return var2 == null ? null : var2.getText();
   }

   public DCTree.DCDocComment getCommentTree(JCTree var1) {
      Entry var2 = (Entry)this.table.get(var1);
      if (var2 == null) {
         return null;
      } else {
         if (var2.tree == null) {
            var2.tree = (new DocCommentParser(this.fac, this.diagSource, var2.comment)).parse();
         }

         return var2.tree;
      }
   }

   public void putComment(JCTree var1, Tokens.Comment var2) {
      this.table.put(var1, new Entry(var2));
   }

   private static class Entry {
      final Tokens.Comment comment;
      DCTree.DCDocComment tree;

      Entry(Tokens.Comment var1) {
         this.comment = var1;
      }
   }
}
