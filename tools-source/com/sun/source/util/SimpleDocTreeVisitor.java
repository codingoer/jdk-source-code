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
public class SimpleDocTreeVisitor implements DocTreeVisitor {
   protected final Object DEFAULT_VALUE;

   protected SimpleDocTreeVisitor() {
      this.DEFAULT_VALUE = null;
   }

   protected SimpleDocTreeVisitor(Object var1) {
      this.DEFAULT_VALUE = var1;
   }

   protected Object defaultAction(DocTree var1, Object var2) {
      return this.DEFAULT_VALUE;
   }

   public final Object visit(DocTree var1, Object var2) {
      return var1 == null ? null : var1.accept(this, var2);
   }

   public final Object visit(Iterable var1, Object var2) {
      Object var3 = null;
      DocTree var5;
      if (var1 != null) {
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = this.visit(var5, var2)) {
            var5 = (DocTree)var4.next();
         }
      }

      return var3;
   }

   public Object visitAttribute(AttributeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitAuthor(AuthorTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitComment(CommentTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitDeprecated(DeprecatedTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitDocComment(DocCommentTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitDocRoot(DocRootTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitEndElement(EndElementTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitEntity(EntityTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitErroneous(ErroneousTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitIdentifier(IdentifierTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitInheritDoc(InheritDocTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitLink(LinkTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitLiteral(LiteralTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitParam(ParamTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitReference(ReferenceTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitReturn(ReturnTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSee(SeeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSerial(SerialTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSerialData(SerialDataTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSerialField(SerialFieldTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSince(SinceTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitStartElement(StartElementTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitText(TextTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitThrows(ThrowsTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitUnknownBlockTag(UnknownBlockTagTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitUnknownInlineTag(UnknownInlineTagTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitValue(ValueTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitVersion(VersionTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitOther(DocTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }
}
