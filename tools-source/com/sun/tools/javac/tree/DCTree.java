package com.sun.tools.javac.tree;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.BlockTagTree;
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
import com.sun.source.doctree.InlineTagTree;
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
import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.io.StringWriter;
import javax.tools.Diagnostic;

public abstract class DCTree implements DocTree {
   public int pos;

   public long getSourcePosition(DCDocComment var1) {
      return (long)var1.comment.getSourcePos(this.pos);
   }

   public JCDiagnostic.DiagnosticPosition pos(DCDocComment var1) {
      return new JCDiagnostic.SimpleDiagnosticPosition(var1.comment.getSourcePos(this.pos));
   }

   public String toString() {
      StringWriter var1 = new StringWriter();

      try {
         (new DocPretty(var1)).print((DocTree)this);
      } catch (IOException var3) {
         throw new AssertionError(var3);
      }

      return var1.toString();
   }

   public static class DCVersion extends DCBlockTag implements VersionTree {
      public final List body;

      DCVersion(List var1) {
         this.body = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.VERSION;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitVersion(this, var2);
      }

      public List getBody() {
         return this.body;
      }
   }

   public static class DCValue extends DCInlineTag implements ValueTree {
      public final DCReference ref;

      DCValue(DCReference var1) {
         this.ref = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.VALUE;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitValue(this, var2);
      }

      public ReferenceTree getReference() {
         return this.ref;
      }
   }

   public static class DCUnknownInlineTag extends DCInlineTag implements UnknownInlineTagTree {
      public final Name name;
      public final List content;

      DCUnknownInlineTag(Name var1, List var2) {
         this.name = var1;
         this.content = var2;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.UNKNOWN_INLINE_TAG;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitUnknownInlineTag(this, var2);
      }

      public String getTagName() {
         return this.name.toString();
      }

      public List getContent() {
         return this.content;
      }
   }

   public static class DCUnknownBlockTag extends DCBlockTag implements UnknownBlockTagTree {
      public final Name name;
      public final List content;

      DCUnknownBlockTag(Name var1, List var2) {
         this.name = var1;
         this.content = var2;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.UNKNOWN_BLOCK_TAG;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitUnknownBlockTag(this, var2);
      }

      public String getTagName() {
         return this.name.toString();
      }

      public List getContent() {
         return this.content;
      }
   }

   public static class DCThrows extends DCBlockTag implements ThrowsTree {
      public final DocTree.Kind kind;
      public final DCReference name;
      public final List description;

      DCThrows(DocTree.Kind var1, DCReference var2, List var3) {
         Assert.check(var1 == DocTree.Kind.EXCEPTION || var1 == DocTree.Kind.THROWS);
         this.kind = var1;
         this.name = var2;
         this.description = var3;
      }

      public DocTree.Kind getKind() {
         return this.kind;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitThrows(this, var2);
      }

      public ReferenceTree getExceptionName() {
         return this.name;
      }

      public List getDescription() {
         return this.description;
      }
   }

   public static class DCText extends DCTree implements TextTree {
      public final String text;

      DCText(String var1) {
         this.text = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.TEXT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitText(this, var2);
      }

      public String getBody() {
         return this.text;
      }
   }

   public static class DCStartElement extends DCEndPosTree implements StartElementTree {
      public final Name name;
      public final List attrs;
      public final boolean selfClosing;

      DCStartElement(Name var1, List var2, boolean var3) {
         this.name = var1;
         this.attrs = var2;
         this.selfClosing = var3;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.START_ELEMENT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitStartElement(this, var2);
      }

      public Name getName() {
         return this.name;
      }

      public List getAttributes() {
         return this.attrs;
      }

      public boolean isSelfClosing() {
         return this.selfClosing;
      }
   }

   public static class DCSince extends DCBlockTag implements SinceTree {
      public final List body;

      DCSince(List var1) {
         this.body = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.SINCE;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitSince(this, var2);
      }

      public List getBody() {
         return this.body;
      }
   }

   public static class DCSerialField extends DCBlockTag implements SerialFieldTree {
      public final DCIdentifier name;
      public final DCReference type;
      public final List description;

      DCSerialField(DCIdentifier var1, DCReference var2, List var3) {
         this.description = var3;
         this.name = var1;
         this.type = var2;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.SERIAL_FIELD;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitSerialField(this, var2);
      }

      public List getDescription() {
         return this.description;
      }

      public IdentifierTree getName() {
         return this.name;
      }

      public ReferenceTree getType() {
         return this.type;
      }
   }

   public static class DCSerialData extends DCBlockTag implements SerialDataTree {
      public final List description;

      DCSerialData(List var1) {
         this.description = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.SERIAL_DATA;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitSerialData(this, var2);
      }

      public List getDescription() {
         return this.description;
      }
   }

   public static class DCSerial extends DCBlockTag implements SerialTree {
      public final List description;

      DCSerial(List var1) {
         this.description = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.SERIAL;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitSerial(this, var2);
      }

      public List getDescription() {
         return this.description;
      }
   }

   public static class DCSee extends DCBlockTag implements SeeTree {
      public final List reference;

      DCSee(List var1) {
         this.reference = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.SEE;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitSee(this, var2);
      }

      public List getReference() {
         return this.reference;
      }
   }

   public static class DCReturn extends DCBlockTag implements ReturnTree {
      public final List description;

      DCReturn(List var1) {
         this.description = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.RETURN;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitReturn(this, var2);
      }

      public List getDescription() {
         return this.description;
      }
   }

   public static class DCReference extends DCEndPosTree implements ReferenceTree {
      public final String signature;
      public final JCTree qualifierExpression;
      public final Name memberName;
      public final List paramTypes;

      DCReference(String var1, JCTree var2, Name var3, List var4) {
         this.signature = var1;
         this.qualifierExpression = var2;
         this.memberName = var3;
         this.paramTypes = var4;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.REFERENCE;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitReference(this, var2);
      }

      public String getSignature() {
         return this.signature;
      }
   }

   public static class DCParam extends DCBlockTag implements ParamTree {
      public final boolean isTypeParameter;
      public final DCIdentifier name;
      public final List description;

      DCParam(boolean var1, DCIdentifier var2, List var3) {
         this.isTypeParameter = var1;
         this.name = var2;
         this.description = var3;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.PARAM;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitParam(this, var2);
      }

      public boolean isTypeParameter() {
         return this.isTypeParameter;
      }

      public IdentifierTree getName() {
         return this.name;
      }

      public List getDescription() {
         return this.description;
      }
   }

   public static class DCLiteral extends DCInlineTag implements LiteralTree {
      public final DocTree.Kind kind;
      public final DCText body;

      DCLiteral(DocTree.Kind var1, DCText var2) {
         Assert.check(var1 == DocTree.Kind.CODE || var1 == DocTree.Kind.LITERAL);
         this.kind = var1;
         this.body = var2;
      }

      public DocTree.Kind getKind() {
         return this.kind;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitLiteral(this, var2);
      }

      public DCText getBody() {
         return this.body;
      }
   }

   public static class DCLink extends DCInlineTag implements LinkTree {
      public final DocTree.Kind kind;
      public final DCReference ref;
      public final List label;

      DCLink(DocTree.Kind var1, DCReference var2, List var3) {
         Assert.check(var1 == DocTree.Kind.LINK || var1 == DocTree.Kind.LINK_PLAIN);
         this.kind = var1;
         this.ref = var2;
         this.label = var3;
      }

      public DocTree.Kind getKind() {
         return this.kind;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitLink(this, var2);
      }

      public ReferenceTree getReference() {
         return this.ref;
      }

      public List getLabel() {
         return this.label;
      }
   }

   public static class DCInheritDoc extends DCInlineTag implements InheritDocTree {
      public DocTree.Kind getKind() {
         return DocTree.Kind.INHERIT_DOC;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitInheritDoc(this, var2);
      }
   }

   public static class DCIdentifier extends DCTree implements IdentifierTree {
      public final Name name;

      DCIdentifier(Name var1) {
         this.name = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.IDENTIFIER;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitIdentifier(this, var2);
      }

      public Name getName() {
         return this.name;
      }
   }

   public static class DCErroneous extends DCTree implements ErroneousTree, JCDiagnostic.DiagnosticPosition {
      public final String body;
      public final JCDiagnostic diag;

      DCErroneous(String var1, JCDiagnostic.Factory var2, DiagnosticSource var3, String var4, Object... var5) {
         this.body = var1;
         this.diag = var2.error(var3, this, var4, var5);
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.ERRONEOUS;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitErroneous(this, var2);
      }

      public String getBody() {
         return this.body;
      }

      public Diagnostic getDiagnostic() {
         return this.diag;
      }

      public JCTree getTree() {
         return null;
      }

      public int getStartPosition() {
         return this.pos;
      }

      public int getPreferredPosition() {
         return this.pos + this.body.length() - 1;
      }

      public int getEndPosition(EndPosTable var1) {
         return this.pos + this.body.length();
      }
   }

   public static class DCEntity extends DCTree implements EntityTree {
      public final Name name;

      DCEntity(Name var1) {
         this.name = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.ENTITY;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitEntity(this, var2);
      }

      public Name getName() {
         return this.name;
      }
   }

   public static class DCEndElement extends DCTree implements EndElementTree {
      public final Name name;

      DCEndElement(Name var1) {
         this.name = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.END_ELEMENT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitEndElement(this, var2);
      }

      public Name getName() {
         return this.name;
      }
   }

   public static class DCDocRoot extends DCInlineTag implements DocRootTree {
      public DocTree.Kind getKind() {
         return DocTree.Kind.DOC_ROOT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitDocRoot(this, var2);
      }
   }

   public static class DCDeprecated extends DCBlockTag implements DeprecatedTree {
      public final List body;

      DCDeprecated(List var1) {
         this.body = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.DEPRECATED;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitDeprecated(this, var2);
      }

      public List getBody() {
         return this.body;
      }
   }

   public static class DCComment extends DCTree implements CommentTree {
      public final String body;

      DCComment(String var1) {
         this.body = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.COMMENT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitComment(this, var2);
      }

      public String getBody() {
         return this.body;
      }
   }

   public static class DCAuthor extends DCBlockTag implements AuthorTree {
      public final List name;

      DCAuthor(List var1) {
         this.name = var1;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.AUTHOR;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitAuthor(this, var2);
      }

      public List getName() {
         return this.name;
      }
   }

   public static class DCAttribute extends DCTree implements AttributeTree {
      public final Name name;
      public final AttributeTree.ValueKind vkind;
      public final List value;

      DCAttribute(Name var1, AttributeTree.ValueKind var2, List var3) {
         Assert.check(var2 == AttributeTree.ValueKind.EMPTY ? var3 == null : var3 != null);
         this.name = var1;
         this.vkind = var2;
         this.value = var3;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.ATTRIBUTE;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitAttribute(this, var2);
      }

      public Name getName() {
         return this.name;
      }

      public AttributeTree.ValueKind getValueKind() {
         return this.vkind;
      }

      public List getValue() {
         return this.value;
      }
   }

   public abstract static class DCInlineTag extends DCEndPosTree implements InlineTagTree {
      public String getTagName() {
         return this.getKind().tagName;
      }
   }

   public abstract static class DCBlockTag extends DCTree implements BlockTagTree {
      public String getTagName() {
         return this.getKind().tagName;
      }
   }

   public static class DCDocComment extends DCTree implements DocCommentTree {
      public final Tokens.Comment comment;
      public final List firstSentence;
      public final List body;
      public final List tags;

      public DCDocComment(Tokens.Comment var1, List var2, List var3, List var4) {
         this.comment = var1;
         this.firstSentence = var2;
         this.body = var3;
         this.tags = var4;
      }

      public DocTree.Kind getKind() {
         return DocTree.Kind.DOC_COMMENT;
      }

      public Object accept(DocTreeVisitor var1, Object var2) {
         return var1.visitDocComment(this, var2);
      }

      public List getFirstSentence() {
         return this.firstSentence;
      }

      public List getBody() {
         return this.body;
      }

      public List getBlockTags() {
         return this.tags;
      }
   }

   public abstract static class DCEndPosTree extends DCTree {
      private int endPos = -1;

      public int getEndPos(DCDocComment var1) {
         return var1.comment.getSourcePos(this.endPos);
      }

      public DCEndPosTree setEndPos(int var1) {
         this.endPos = var1;
         return this;
      }
   }
}
