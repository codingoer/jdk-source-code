package com.sun.source.util;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.IdentifierTree;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SerialTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.doctree.VersionTree;
import java.util.Iterator;
import jdk.Exported;

@Exported
public class DocTreeScanner implements DocTreeVisitor {
   public Object scan(DocTree var1, Object var2) {
      return var1 == null ? null : var1.accept(this, var2);
   }

   private Object scanAndReduce(DocTree var1, Object var2, Object var3) {
      return this.reduce(this.scan(var1, var2), var3);
   }

   public Object scan(Iterable var1, Object var2) {
      Object var3 = null;
      if (var1 != null) {
         boolean var4 = true;

         for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 = false) {
            DocTree var6 = (DocTree)var5.next();
            var3 = var4 ? this.scan(var6, var2) : this.scanAndReduce(var6, var2, var3);
         }
      }

      return var3;
   }

   private Object scanAndReduce(Iterable var1, Object var2, Object var3) {
      return this.reduce(this.scan(var1, var2), var3);
   }

   public Object reduce(Object var1, Object var2) {
      return var1;
   }

   public Object visitAttribute(AttributeTree var1, Object var2) {
      return null;
   }

   public Object visitAuthor(AuthorTree var1, Object var2) {
      return this.scan((Iterable)var1.getName(), var2);
   }

   public Object visitComment(CommentTree var1, Object var2) {
      return null;
   }

   public Object visitDeprecated(DeprecatedTree var1, Object var2) {
      return this.scan((Iterable)var1.getBody(), var2);
   }

   public Object visitDocComment(DocCommentTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getFirstSentence(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getBody(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getBlockTags(), var2, var3);
      return var3;
   }

   public Object visitDocRoot(DocRootTree var1, Object var2) {
      return null;
   }

   public Object visitEndElement(EndElementTree var1, Object var2) {
      return null;
   }

   public Object visitEntity(EntityTree var1, Object var2) {
      return null;
   }

   public Object visitErroneous(ErroneousTree var1, Object var2) {
      return null;
   }

   public Object visitIdentifier(IdentifierTree var1, Object var2) {
      return null;
   }

   public Object visitInheritDoc(InheritDocTree var1, Object var2) {
      return null;
   }

   public Object visitLink(LinkTree var1, Object var2) {
      Object var3 = this.scan((DocTree)var1.getReference(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getLabel(), var2, var3);
      return var3;
   }

   public Object visitLiteral(LiteralTree var1, Object var2) {
      return null;
   }

   public Object visitParam(ParamTree var1, Object var2) {
      Object var3 = this.scan((DocTree)var1.getName(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getDescription(), var2, var3);
      return var3;
   }

   public Object visitReference(ReferenceTree var1, Object var2) {
      return null;
   }

   public Object visitReturn(ReturnTree var1, Object var2) {
      return this.scan((Iterable)var1.getDescription(), var2);
   }

   public Object visitSee(SeeTree var1, Object var2) {
      return this.scan((Iterable)var1.getReference(), var2);
   }

   public Object visitSerial(SerialTree var1, Object var2) {
      return this.scan((Iterable)var1.getDescription(), var2);
   }

   public Object visitSerialData(SerialDataTree var1, Object var2) {
      return this.scan((Iterable)var1.getDescription(), var2);
   }

   public Object visitSerialField(SerialFieldTree var1, Object var2) {
      Object var3 = this.scan((DocTree)var1.getName(), var2);
      var3 = this.scanAndReduce((DocTree)var1.getType(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getDescription(), var2, var3);
      return var3;
   }

   public Object visitSince(SinceTree var1, Object var2) {
      return this.scan((Iterable)var1.getBody(), var2);
   }

   public Object visitStartElement(StartElementTree var1, Object var2) {
      return this.scan((Iterable)var1.getAttributes(), var2);
   }

   public Object visitText(TextTree var1, Object var2) {
      return null;
   }

   public Object visitThrows(ThrowsTree var1, Object var2) {
      Object var3 = this.scan((DocTree)var1.getExceptionName(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getDescription(), var2, var3);
      return var3;
   }

   public Object visitUnknownBlockTag(UnknownBlockTagTree var1, Object var2) {
      return this.scan((Iterable)var1.getContent(), var2);
   }

   public Object visitUnknownInlineTag(UnknownInlineTagTree var1, Object var2) {
      return this.scan((Iterable)var1.getContent(), var2);
   }

   public Object visitValue(ValueTree var1, Object var2) {
      return this.scan((DocTree)var1.getReference(), var2);
   }

   public Object visitVersion(VersionTree var1, Object var2) {
      return this.scan((Iterable)var1.getBody(), var2);
   }

   public Object visitOther(DocTree var1, Object var2) {
      return null;
   }
}
