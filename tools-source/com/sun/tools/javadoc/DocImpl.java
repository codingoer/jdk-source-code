package com.sun.tools.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.source.util.TreePath;
import com.sun.tools.doclets.internal.toolkit.util.FatalError;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Position;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CollationKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.FileObject;

public abstract class DocImpl implements Doc, Comparable {
   protected final DocEnv env;
   protected TreePath treePath;
   private Comment comment;
   private CollationKey collationkey = null;
   protected String documentation;
   private Tag[] firstSentence;
   private Tag[] inlineTags;

   DocImpl(DocEnv var1, TreePath var2) {
      this.treePath = var2;
      this.documentation = getCommentText(var2);
      this.env = var1;
   }

   private static String getCommentText(TreePath var0) {
      if (var0 == null) {
         return null;
      } else {
         JCTree.JCCompilationUnit var1 = (JCTree.JCCompilationUnit)var0.getCompilationUnit();
         JCTree var2 = (JCTree)var0.getLeaf();
         return var1.docComments.getCommentText(var2);
      }
   }

   protected String documentation() {
      if (this.documentation == null) {
         this.documentation = "";
      }

      return this.documentation;
   }

   Comment comment() {
      if (this.comment == null) {
         String var1 = this.documentation();
         if (this.env.javaScriptScanner != null) {
            this.env.javaScriptScanner.parse(var1, new JavaScriptScanner.Reporter() {
               public void report() {
                  DocImpl.this.env.error(DocImpl.this, "javadoc.JavaScript_in_comment");
                  throw new FatalError();
               }
            });
         }

         if (this.env.doclint != null && this.treePath != null && var1.equals(getCommentText(this.treePath))) {
            this.env.doclint.scan(this.treePath);
         }

         this.comment = new Comment(this, var1);
      }

      return this.comment;
   }

   public String commentText() {
      return this.comment().commentText();
   }

   public Tag[] tags() {
      return this.comment().tags();
   }

   public Tag[] tags(String var1) {
      return this.comment().tags(var1);
   }

   public SeeTag[] seeTags() {
      return this.comment().seeTags();
   }

   public Tag[] inlineTags() {
      if (this.inlineTags == null) {
         this.inlineTags = Comment.getInlineTags(this, this.commentText());
      }

      return this.inlineTags;
   }

   public Tag[] firstSentenceTags() {
      if (this.firstSentence == null) {
         this.inlineTags();

         try {
            this.env.setSilent(true);
            this.firstSentence = Comment.firstSentenceTags(this, this.commentText());
         } finally {
            this.env.setSilent(false);
         }
      }

      return this.firstSentence;
   }

   String readHTMLDocumentation(InputStream var1, FileObject var2) throws IOException {
      byte[] var3 = new byte[var1.available()];

      try {
         DataInputStream var4 = new DataInputStream(var1);
         var4.readFully(var3);
      } finally {
         var1.close();
      }

      String var11 = this.env.getEncoding();
      String var5 = var11 != null ? new String(var3, var11) : new String(var3);
      Pattern var6 = Pattern.compile("(?is).*<body\\b[^>]*>(.*)</body\\b.*");
      Matcher var7 = var6.matcher(var5);
      if (var7.matches()) {
         return var7.group(1);
      } else {
         String var8 = var5.matches("(?is).*<body\\b.*") ? "javadoc.End_body_missing_from_html_file" : "javadoc.Body_missing_from_html_file";
         this.env.error(SourcePositionImpl.make(var2, -1, (Position.LineMap)null), var8);
         return "";
      }
   }

   public String getRawCommentText() {
      return this.documentation();
   }

   public void setRawCommentText(String var1) {
      this.treePath = null;
      this.documentation = var1;
      this.comment = null;
   }

   void setTreePath(TreePath var1) {
      this.treePath = var1;
      this.documentation = getCommentText(var1);
      this.comment = null;
   }

   CollationKey key() {
      if (this.collationkey == null) {
         this.collationkey = this.generateKey();
      }

      return this.collationkey;
   }

   CollationKey generateKey() {
      String var1 = this.name();
      return this.env.doclocale.collator.getCollationKey(var1);
   }

   public String toString() {
      return this.qualifiedName();
   }

   public abstract String name();

   public abstract String qualifiedName();

   public int compareTo(Object var1) {
      return this.key().compareTo(((DocImpl)var1).key());
   }

   public boolean isField() {
      return false;
   }

   public boolean isEnumConstant() {
      return false;
   }

   public boolean isConstructor() {
      return false;
   }

   public boolean isMethod() {
      return false;
   }

   public boolean isAnnotationTypeElement() {
      return false;
   }

   public boolean isInterface() {
      return false;
   }

   public boolean isException() {
      return false;
   }

   public boolean isError() {
      return false;
   }

   public boolean isEnum() {
      return false;
   }

   public boolean isAnnotationType() {
      return false;
   }

   public boolean isOrdinaryClass() {
      return false;
   }

   public boolean isClass() {
      return false;
   }

   public abstract boolean isIncluded();

   public SourcePosition position() {
      return null;
   }
}
