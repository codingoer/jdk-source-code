package com.sun.tools.javac.tree;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocTree;
import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

public class DocTreeMaker {
   protected static final Context.Key treeMakerKey = new Context.Key();
   public int pos = -1;
   private final JCDiagnostic.Factory diags;

   public static DocTreeMaker instance(Context var0) {
      DocTreeMaker var1 = (DocTreeMaker)var0.get(treeMakerKey);
      if (var1 == null) {
         var1 = new DocTreeMaker(var0);
      }

      return var1;
   }

   protected DocTreeMaker(Context var1) {
      var1.put((Context.Key)treeMakerKey, (Object)this);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.pos = -1;
   }

   public DocTreeMaker at(int var1) {
      this.pos = var1;
      return this;
   }

   public DocTreeMaker at(JCDiagnostic.DiagnosticPosition var1) {
      this.pos = var1 == null ? -1 : var1.getStartPosition();
      return this;
   }

   public DCTree.DCAttribute Attribute(Name var1, AttributeTree.ValueKind var2, List var3) {
      DCTree.DCAttribute var4 = new DCTree.DCAttribute(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public DCTree.DCAuthor Author(List var1) {
      DCTree.DCAuthor var2 = new DCTree.DCAuthor(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCLiteral Code(DCTree.DCText var1) {
      DCTree.DCLiteral var2 = new DCTree.DCLiteral(DocTree.Kind.CODE, var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCComment Comment(String var1) {
      DCTree.DCComment var2 = new DCTree.DCComment(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCDeprecated Deprecated(List var1) {
      DCTree.DCDeprecated var2 = new DCTree.DCDeprecated(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCDocComment DocComment(Tokens.Comment var1, List var2, List var3, List var4) {
      DCTree.DCDocComment var5 = new DCTree.DCDocComment(var1, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public DCTree.DCDocRoot DocRoot() {
      DCTree.DCDocRoot var1 = new DCTree.DCDocRoot();
      var1.pos = this.pos;
      return var1;
   }

   public DCTree.DCEndElement EndElement(Name var1) {
      DCTree.DCEndElement var2 = new DCTree.DCEndElement(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCEntity Entity(Name var1) {
      DCTree.DCEntity var2 = new DCTree.DCEntity(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCErroneous Erroneous(String var1, DiagnosticSource var2, String var3, Object... var4) {
      DCTree.DCErroneous var5 = new DCTree.DCErroneous(var1, this.diags, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public DCTree.DCThrows Exception(DCTree.DCReference var1, List var2) {
      DCTree.DCThrows var3 = new DCTree.DCThrows(DocTree.Kind.EXCEPTION, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCIdentifier Identifier(Name var1) {
      DCTree.DCIdentifier var2 = new DCTree.DCIdentifier(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCInheritDoc InheritDoc() {
      DCTree.DCInheritDoc var1 = new DCTree.DCInheritDoc();
      var1.pos = this.pos;
      return var1;
   }

   public DCTree.DCLink Link(DCTree.DCReference var1, List var2) {
      DCTree.DCLink var3 = new DCTree.DCLink(DocTree.Kind.LINK, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCLink LinkPlain(DCTree.DCReference var1, List var2) {
      DCTree.DCLink var3 = new DCTree.DCLink(DocTree.Kind.LINK_PLAIN, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCLiteral Literal(DCTree.DCText var1) {
      DCTree.DCLiteral var2 = new DCTree.DCLiteral(DocTree.Kind.LITERAL, var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCParam Param(boolean var1, DCTree.DCIdentifier var2, List var3) {
      DCTree.DCParam var4 = new DCTree.DCParam(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public DCTree.DCReference Reference(String var1, JCTree var2, Name var3, List var4) {
      DCTree.DCReference var5 = new DCTree.DCReference(var1, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public DCTree.DCReturn Return(List var1) {
      DCTree.DCReturn var2 = new DCTree.DCReturn(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCSee See(List var1) {
      DCTree.DCSee var2 = new DCTree.DCSee(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCSerial Serial(List var1) {
      DCTree.DCSerial var2 = new DCTree.DCSerial(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCSerialData SerialData(List var1) {
      DCTree.DCSerialData var2 = new DCTree.DCSerialData(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCSerialField SerialField(DCTree.DCIdentifier var1, DCTree.DCReference var2, List var3) {
      DCTree.DCSerialField var4 = new DCTree.DCSerialField(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public DCTree.DCSince Since(List var1) {
      DCTree.DCSince var2 = new DCTree.DCSince(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCStartElement StartElement(Name var1, List var2, boolean var3) {
      DCTree.DCStartElement var4 = new DCTree.DCStartElement(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public DCTree.DCText Text(String var1) {
      DCTree.DCText var2 = new DCTree.DCText(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCThrows Throws(DCTree.DCReference var1, List var2) {
      DCTree.DCThrows var3 = new DCTree.DCThrows(DocTree.Kind.THROWS, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCUnknownBlockTag UnknownBlockTag(Name var1, List var2) {
      DCTree.DCUnknownBlockTag var3 = new DCTree.DCUnknownBlockTag(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCUnknownInlineTag UnknownInlineTag(Name var1, List var2) {
      DCTree.DCUnknownInlineTag var3 = new DCTree.DCUnknownInlineTag(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public DCTree.DCValue Value(DCTree.DCReference var1) {
      DCTree.DCValue var2 = new DCTree.DCValue(var1);
      var2.pos = this.pos;
      return var2;
   }

   public DCTree.DCVersion Version(List var1) {
      DCTree.DCVersion var2 = new DCTree.DCVersion(var1);
      var2.pos = this.pos;
      return var2;
   }
}
