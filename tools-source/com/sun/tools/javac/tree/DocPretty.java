package com.sun.tools.javac.tree;

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
import com.sun.tools.javac.util.Convert;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class DocPretty implements DocTreeVisitor {
   final Writer out;
   int lmargin = 0;
   final String lineSep = System.getProperty("line.separator");

   public DocPretty(Writer var1) {
      this.out = var1;
   }

   public void print(DocTree var1) throws IOException {
      try {
         if (var1 == null) {
            this.print((Object)"/*missing*/");
         } else {
            var1.accept(this, (Object)null);
         }

      } catch (UncheckedIOException var3) {
         throw new IOException(var3.getMessage(), var3);
      }
   }

   protected void print(Object var1) throws IOException {
      this.out.write(Convert.escapeUnicode(var1.toString()));
   }

   public void print(List var1) throws IOException {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         DocTree var3 = (DocTree)var2.next();
         this.print(var3);
      }

   }

   protected void print(List var1, String var2) throws IOException {
      if (!var1.isEmpty()) {
         boolean var3 = true;

         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = false) {
            DocTree var5 = (DocTree)var4.next();
            if (!var3) {
               this.print((Object)var2);
            }

            this.print(var5);
         }

      }
   }

   protected void println() throws IOException {
      this.out.write(this.lineSep);
   }

   protected void printTagName(DocTree var1) throws IOException {
      this.out.write("@");
      this.out.write(var1.getKind().tagName);
   }

   public Void visitAttribute(AttributeTree var1, Void var2) {
      try {
         this.print((Object)var1.getName());
         String var3;
         switch (var1.getValueKind()) {
            case EMPTY:
               var3 = null;
               break;
            case UNQUOTED:
               var3 = "";
               break;
            case SINGLE:
               var3 = "'";
               break;
            case DOUBLE:
               var3 = "\"";
               break;
            default:
               throw new AssertionError();
         }

         if (var3 != null) {
            this.print((Object)("=" + var3));
            this.print(var1.getValue());
            this.print((Object)var3);
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitAuthor(AuthorTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print(var1.getName());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitComment(CommentTree var1, Void var2) {
      try {
         this.print((Object)var1.getBody());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitDeprecated(DeprecatedTree var1, Void var2) {
      try {
         this.printTagName(var1);
         if (!var1.getBody().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getBody());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitDocComment(DocCommentTree var1, Void var2) {
      try {
         List var3 = var1.getFirstSentence();
         List var4 = var1.getBody();
         List var5 = var1.getBlockTags();
         this.print(var3);
         if (!var3.isEmpty() && !var4.isEmpty()) {
            this.print((Object)" ");
         }

         this.print(var4);
         if ((!var3.isEmpty() || !var4.isEmpty()) && !var5.isEmpty()) {
            this.print((Object)"\n");
         }

         this.print(var5, "\n");
         return null;
      } catch (IOException var6) {
         throw new UncheckedIOException(var6);
      }
   }

   public Void visitDocRoot(DocRootTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.printTagName(var1);
         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitEndElement(EndElementTree var1, Void var2) {
      try {
         this.print((Object)"</");
         this.print((Object)var1.getName());
         this.print((Object)">");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitEntity(EntityTree var1, Void var2) {
      try {
         this.print((Object)"&");
         this.print((Object)var1.getName());
         this.print((Object)";");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitErroneous(ErroneousTree var1, Void var2) {
      try {
         this.print((Object)var1.getBody());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitIdentifier(IdentifierTree var1, Void var2) {
      try {
         this.print((Object)var1.getName());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitInheritDoc(InheritDocTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.printTagName(var1);
         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitLink(LinkTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.printTagName(var1);
         this.print((Object)" ");
         this.print((DocTree)var1.getReference());
         if (!var1.getLabel().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getLabel());
         }

         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitLiteral(LiteralTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.printTagName(var1);
         this.print((Object)" ");
         this.print((DocTree)var1.getBody());
         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitParam(ParamTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         if (var1.isTypeParameter()) {
            this.print((Object)"<");
         }

         this.print((DocTree)var1.getName());
         if (var1.isTypeParameter()) {
            this.print((Object)">");
         }

         if (!var1.getDescription().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getDescription());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitReference(ReferenceTree var1, Void var2) {
      try {
         this.print((Object)var1.getSignature());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitReturn(ReturnTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print(var1.getDescription());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitSee(SeeTree var1, Void var2) {
      try {
         this.printTagName(var1);
         boolean var3 = true;
         boolean var4 = true;
         Iterator var5 = var1.getReference().iterator();

         while(var5.hasNext()) {
            DocTree var6 = (DocTree)var5.next();
            if (var4) {
               this.print((Object)" ");
            }

            var4 = var3 && var6 instanceof ReferenceTree;
            var3 = false;
            this.print(var6);
         }

         return null;
      } catch (IOException var7) {
         throw new UncheckedIOException(var7);
      }
   }

   public Void visitSerial(SerialTree var1, Void var2) {
      try {
         this.printTagName(var1);
         if (!var1.getDescription().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getDescription());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitSerialData(SerialDataTree var1, Void var2) {
      try {
         this.printTagName(var1);
         if (!var1.getDescription().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getDescription());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitSerialField(SerialFieldTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print((DocTree)var1.getName());
         this.print((Object)" ");
         this.print((DocTree)var1.getType());
         if (!var1.getDescription().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getDescription());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitSince(SinceTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print(var1.getBody());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitStartElement(StartElementTree var1, Void var2) {
      try {
         this.print((Object)"<");
         this.print((Object)var1.getName());
         List var3 = var1.getAttributes();
         if (!var3.isEmpty()) {
            this.print((Object)" ");
            this.print(var3);
            DocTree var4 = (DocTree)var1.getAttributes().get(var3.size() - 1);
            if (var1.isSelfClosing() && var4 instanceof AttributeTree && ((AttributeTree)var4).getValueKind() == AttributeTree.ValueKind.UNQUOTED) {
               this.print((Object)" ");
            }
         }

         if (var1.isSelfClosing()) {
            this.print((Object)"/");
         }

         this.print((Object)">");
         return null;
      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public Void visitText(TextTree var1, Void var2) {
      try {
         this.print((Object)var1.getBody());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitThrows(ThrowsTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print((DocTree)var1.getExceptionName());
         if (!var1.getDescription().isEmpty()) {
            this.print((Object)" ");
            this.print(var1.getDescription());
         }

         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitUnknownBlockTag(UnknownBlockTagTree var1, Void var2) {
      try {
         this.print((Object)"@");
         this.print((Object)var1.getTagName());
         this.print((Object)" ");
         this.print(var1.getContent());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitUnknownInlineTag(UnknownInlineTagTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.print((Object)"@");
         this.print((Object)var1.getTagName());
         this.print((Object)" ");
         this.print(var1.getContent());
         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitValue(ValueTree var1, Void var2) {
      try {
         this.print((Object)"{");
         this.printTagName(var1);
         if (var1.getReference() != null) {
            this.print((Object)" ");
            this.print((DocTree)var1.getReference());
         }

         this.print((Object)"}");
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitVersion(VersionTree var1, Void var2) {
      try {
         this.printTagName(var1);
         this.print((Object)" ");
         this.print(var1.getBody());
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public Void visitOther(DocTree var1, Void var2) {
      try {
         this.print((Object)("(UNKNOWN: " + var1 + ")"));
         this.println();
         return null;
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   private static class UncheckedIOException extends Error {
      static final long serialVersionUID = -4032692679158424751L;

      UncheckedIOException(IOException var1) {
         super(var1.getMessage(), var1);
      }
   }
}
