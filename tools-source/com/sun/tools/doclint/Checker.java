package com.sun.tools.doclint;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
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
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTreePathScanner;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.DocPretty;
import com.sun.tools.javac.util.StringUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class Checker extends DocTreePathScanner {
   final Env env;
   Set foundParams = new HashSet();
   Set foundThrows = new HashSet();
   Map foundAnchors = new HashMap();
   boolean foundInheritDoc = false;
   boolean foundReturn = false;
   private final Deque tagStack;
   private HtmlTag currHeaderTag;
   private final int implicitHeaderLevel;
   private static final Pattern validName = Pattern.compile("[A-Za-z][A-Za-z0-9-_:.]*");
   private static final Pattern validNumber = Pattern.compile("-?[0-9]+");
   private static final Pattern docRoot = Pattern.compile("(?i)(\\{@docRoot *\\}/?)?(.*)");

   Checker(Env var1) {
      var1.getClass();
      this.env = var1;
      this.tagStack = new LinkedList();
      this.implicitHeaderLevel = var1.implicitHeaderLevel;
   }

   public Void scan(DocCommentTree var1, TreePath var2) {
      this.env.setCurrent(var2, var1);
      boolean var3 = !this.env.currOverriddenMethods.isEmpty();
      if (var2.getLeaf() == var2.getCompilationUnit()) {
         JavaFileObject var4 = var2.getCompilationUnit().getSourceFile();
         boolean var5 = var4.isNameCompatible("package-info", Kind.SOURCE);
         if (var1 == null) {
            if (var5) {
               this.reportMissing("dc.missing.comment");
            }

            return null;
         }

         if (!var5) {
            this.reportReference("dc.unexpected.comment");
         }
      } else if (var1 == null) {
         if (!this.isSynthetic() && !var3) {
            this.reportMissing("dc.missing.comment");
         }

         return null;
      }

      this.tagStack.clear();
      this.currHeaderTag = null;
      this.foundParams.clear();
      this.foundThrows.clear();
      this.foundInheritDoc = false;
      this.foundReturn = false;
      this.scan(new DocTreePath(var2, var1), (Object)null);
      if (!var3) {
         switch (this.env.currElement.getKind()) {
            case METHOD:
            case CONSTRUCTOR:
               ExecutableElement var6 = (ExecutableElement)this.env.currElement;
               this.checkParamsDocumented(var6.getTypeParameters());
               this.checkParamsDocumented(var6.getParameters());
               switch (var6.getReturnType().getKind()) {
                  default:
                     if (!this.foundReturn && !this.foundInheritDoc && !this.env.types.isSameType(var6.getReturnType(), this.env.java_lang_Void)) {
                        this.reportMissing("dc.missing.return");
                     }
                  case VOID:
                  case NONE:
                     this.checkThrowsDocumented(var6.getThrownTypes());
               }
         }
      }

      return null;
   }

   private void reportMissing(String var1, Object... var2) {
      this.env.messages.report(Messages.Group.MISSING, javax.tools.Diagnostic.Kind.WARNING, this.env.currPath.getLeaf(), var1, var2);
   }

   private void reportReference(String var1, Object... var2) {
      this.env.messages.report(Messages.Group.REFERENCE, javax.tools.Diagnostic.Kind.WARNING, this.env.currPath.getLeaf(), var1, var2);
   }

   public Void visitDocComment(DocCommentTree var1, Void var2) {
      super.visitDocComment(var1, var2);
      Iterator var3 = this.tagStack.iterator();

      while(var3.hasNext()) {
         TagStackItem var4 = (TagStackItem)var3.next();
         this.warnIfEmpty((TagStackItem)var4, (DocTree)null);
         if (var4.tree.getKind() == DocTree.Kind.START_ELEMENT && var4.tag.endKind == HtmlTag.EndKind.REQUIRED) {
            StartElementTree var5 = (StartElementTree)var4.tree;
            this.env.messages.error(Messages.Group.HTML, var5, "dc.tag.not.closed", var5.getName());
         }
      }

      return null;
   }

   public Void visitText(TextTree var1, Void var2) {
      if (this.hasNonWhitespace(var1)) {
         this.checkAllowsText(var1);
         this.markEnclosingTag(Checker.Flag.HAS_TEXT);
      }

      return null;
   }

   public Void visitEntity(EntityTree var1, Void var2) {
      this.checkAllowsText(var1);
      this.markEnclosingTag(Checker.Flag.HAS_TEXT);
      String var3 = var1.getName().toString();
      if (var3.startsWith("#")) {
         int var4 = StringUtils.toLowerCase(var3).startsWith("#x") ? Integer.parseInt(var3.substring(2), 16) : Integer.parseInt(var3.substring(1), 10);
         if (!Entity.isValid(var4)) {
            this.env.messages.error(Messages.Group.HTML, var1, "dc.entity.invalid", var3);
         }
      } else if (!Entity.isValid(var3)) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.entity.invalid", var3);
      }

      return null;
   }

   void checkAllowsText(DocTree var1) {
      TagStackItem var2 = (TagStackItem)this.tagStack.peek();
      if (var2 != null && var2.tree.getKind() == DocTree.Kind.START_ELEMENT && !var2.tag.acceptsText() && var2.flags.add(Checker.Flag.REPORTED_BAD_INLINE)) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.text.not.allowed", ((StartElementTree)var2.tree).getName());
      }

   }

   public Void visitStartElement(StartElementTree var1, Void var2) {
      Name var3 = var1.getName();
      HtmlTag var4 = HtmlTag.get(var3);
      TagStackItem var7;
      if (var4 == null) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.unknown", var3);
      } else {
         boolean var5 = false;
         Iterator var6 = this.tagStack.iterator();

         while(var6.hasNext()) {
            var7 = (TagStackItem)var6.next();
            if (var7.tag.accepts(var4)) {
               while(this.tagStack.peek() != var7) {
                  this.warnIfEmpty((TagStackItem)((TagStackItem)this.tagStack.peek()), (DocTree)null);
                  this.tagStack.pop();
               }

               var5 = true;
               break;
            }

            if (var7.tag.endKind != HtmlTag.EndKind.OPTIONAL) {
               var5 = true;
               break;
            }
         }

         if (!var5 && HtmlTag.BODY.accepts(var4)) {
            while(!this.tagStack.isEmpty()) {
               this.warnIfEmpty((TagStackItem)((TagStackItem)this.tagStack.peek()), (DocTree)null);
               this.tagStack.pop();
            }
         }

         this.markEnclosingTag(Checker.Flag.HAS_ELEMENT);
         this.checkStructure(var1, var4);
         switch (var4) {
            case H1:
            case H2:
            case H3:
            case H4:
            case H5:
            case H6:
               this.checkHeader(var1, var4);
         }

         if (var4.flags.contains(HtmlTag.Flag.NO_NEST)) {
            var6 = this.tagStack.iterator();

            while(var6.hasNext()) {
               var7 = (TagStackItem)var6.next();
               if (var4 == var7.tag) {
                  this.env.messages.warning(Messages.Group.HTML, var1, "dc.tag.nested.not.allowed", var3);
                  break;
               }
            }
         }
      }

      if (var1.isSelfClosing()) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.self.closing", var3);
      }

      try {
         TagStackItem var11 = (TagStackItem)this.tagStack.peek();
         TagStackItem var12 = new TagStackItem(var1, var4);
         this.tagStack.push(var12);
         super.visitStartElement(var1, var2);
         if (var4 != null) {
            switch (var4) {
               case CAPTION:
                  if (var11 != null && var11.tag == HtmlTag.TABLE) {
                     var11.flags.add(Checker.Flag.TABLE_HAS_CAPTION);
                  }
                  break;
               case IMG:
                  if (!var12.attrs.contains(HtmlTag.Attr.ALT)) {
                     this.env.messages.error(Messages.Group.ACCESSIBILITY, var1, "dc.no.alt.attr.for.image");
                  }
            }
         }

         var7 = null;
      } finally {
         if (var4 == null || var4.endKind == HtmlTag.EndKind.NONE) {
            this.tagStack.pop();
         }

      }

      return var7;
   }

   private void checkStructure(StartElementTree var1, HtmlTag var2) {
      Name var3;
      var3 = var1.getName();
      TagStackItem var4 = (TagStackItem)this.tagStack.peek();
      label40:
      switch (var2.blockType) {
         case BLOCK:
            if (var4 == null || var4.tag.accepts(var2)) {
               return;
            }

            switch (var4.tree.getKind()) {
               case START_ELEMENT:
                  if (var4.tag.blockType == HtmlTag.BlockType.INLINE) {
                     Name var6 = ((StartElementTree)var4.tree).getName();
                     this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.not.allowed.inline.element", var3, var6);
                     return;
                  }
                  break label40;
               case LINK:
               case LINK_PLAIN:
                  String var5 = var4.tree.getKind().tagName;
                  this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.not.allowed.inline.tag", var3, var5);
                  return;
               default:
                  break label40;
            }
         case INLINE:
            if (var4 != null && !var4.tag.accepts(var2)) {
               break;
            }

            return;
         case LIST_ITEM:
         case TABLE_ITEM:
            if (var4 != null) {
               var4.flags.remove(Checker.Flag.REPORTED_BAD_INLINE);
               if (var4.tag.accepts(var2)) {
                  return;
               }
            }
            break;
         case OTHER:
            switch (var2) {
               default:
                  this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.not.allowed", var3);
               case SCRIPT:
                  return;
            }
      }

      this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.not.allowed.here", var3);
   }

   private void checkHeader(StartElementTree var1, HtmlTag var2) {
      if (this.getHeaderLevel(var2) > this.getHeaderLevel(this.currHeaderTag) + 1) {
         if (this.currHeaderTag == null) {
            this.env.messages.error(Messages.Group.ACCESSIBILITY, var1, "dc.tag.header.sequence.1", var2);
         } else {
            this.env.messages.error(Messages.Group.ACCESSIBILITY, var1, "dc.tag.header.sequence.2", var2, this.currHeaderTag);
         }
      }

      this.currHeaderTag = var2;
   }

   private int getHeaderLevel(HtmlTag var1) {
      if (var1 == null) {
         return this.implicitHeaderLevel;
      } else {
         switch (var1) {
            case H1:
               return 1;
            case H2:
               return 2;
            case H3:
               return 3;
            case H4:
               return 4;
            case H5:
               return 5;
            case H6:
               return 6;
            default:
               throw new IllegalArgumentException();
         }
      }
   }

   public Void visitEndElement(EndElementTree var1, Void var2) {
      Name var3 = var1.getName();
      HtmlTag var4 = HtmlTag.get(var3);
      if (var4 == null) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.unknown", var3);
      } else if (var4.endKind == HtmlTag.EndKind.NONE) {
         this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.end.not.permitted", var3);
      } else {
         boolean var5 = false;

         label61:
         while(true) {
            while(true) {
               if (this.tagStack.isEmpty()) {
                  break label61;
               }

               TagStackItem var6 = (TagStackItem)this.tagStack.peek();
               if (var4 == var6.tag) {
                  switch (var4) {
                     case TABLE:
                        if (!var6.attrs.contains(HtmlTag.Attr.SUMMARY) && !var6.flags.contains(Checker.Flag.TABLE_HAS_CAPTION)) {
                           this.env.messages.error(Messages.Group.ACCESSIBILITY, var1, "dc.no.summary.or.caption.for.table");
                        }
                     default:
                        this.warnIfEmpty((TagStackItem)var6, (DocTree)var1);
                        this.tagStack.pop();
                        var5 = true;
                        break label61;
                  }
               }

               if (var6.tag != null && var6.tag.endKind == HtmlTag.EndKind.REQUIRED) {
                  boolean var7 = false;
                  Iterator var8 = this.tagStack.iterator();

                  while(var8.hasNext()) {
                     TagStackItem var9 = (TagStackItem)var8.next();
                     if (var9.tag == var4) {
                        var7 = true;
                        break;
                     }
                  }

                  if (!var7 || var6.tree.getKind() != DocTree.Kind.START_ELEMENT) {
                     this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.end.unexpected", var3);
                     var5 = true;
                     break label61;
                  }

                  this.env.messages.error(Messages.Group.HTML, var6.tree, "dc.tag.start.unmatched", ((StartElementTree)var6.tree).getName());
                  this.tagStack.pop();
               } else {
                  this.tagStack.pop();
               }
            }
         }

         if (!var5 && this.tagStack.isEmpty()) {
            this.env.messages.error(Messages.Group.HTML, var1, "dc.tag.end.unexpected", var3);
         }
      }

      return (Void)super.visitEndElement(var1, var2);
   }

   void warnIfEmpty(TagStackItem var1, DocTree var2) {
      if (var1.tag != null && var1.tree instanceof StartElementTree && var1.tag.flags.contains(HtmlTag.Flag.EXPECT_CONTENT) && !var1.flags.contains(Checker.Flag.HAS_TEXT) && !var1.flags.contains(Checker.Flag.HAS_ELEMENT) && !var1.flags.contains(Checker.Flag.HAS_INLINE_TAG)) {
         DocTree var3 = var2 != null ? var2 : var1.tree;
         Name var4 = ((StartElementTree)var1.tree).getName();
         this.env.messages.warning(Messages.Group.HTML, var3, "dc.tag.empty", var4);
      }

   }

   public Void visitAttribute(AttributeTree var1, Void var2) {
      HtmlTag var3 = ((TagStackItem)this.tagStack.peek()).tag;
      if (var3 != null) {
         Name var4 = var1.getName();
         HtmlTag.Attr var5 = var3.getAttr(var4);
         if (var5 != null) {
            boolean var6 = ((TagStackItem)this.tagStack.peek()).attrs.add(var5);
            if (!var6) {
               this.env.messages.error(Messages.Group.HTML, var1, "dc.attr.repeated", var4);
            }
         }

         if (!var4.toString().startsWith("on")) {
            HtmlTag.AttrKind var10 = var3.getAttrKind(var4);
            switch (var10) {
               case OK:
               default:
                  break;
               case INVALID:
                  this.env.messages.error(Messages.Group.HTML, var1, "dc.attr.unknown", var4);
                  break;
               case OBSOLETE:
                  this.env.messages.warning(Messages.Group.ACCESSIBILITY, var1, "dc.attr.obsolete", var4);
                  break;
               case USE_CSS:
                  this.env.messages.warning(Messages.Group.ACCESSIBILITY, var1, "dc.attr.obsolete.use.css", var4);
            }
         }

         if (var5 != null) {
            String var7;
            switch (var5) {
               case NAME:
                  if (var3 != HtmlTag.A) {
                     break;
                  }
               case ID:
                  String var11 = this.getAttrValue(var1);
                  if (var11 == null) {
                     this.env.messages.error(Messages.Group.HTML, var1, "dc.anchor.value.missing");
                  } else {
                     if (!validName.matcher(var11).matches()) {
                        this.env.messages.error(Messages.Group.HTML, var1, "dc.invalid.anchor", var11);
                     }

                     if (!this.checkAnchor(var11)) {
                        this.env.messages.error(Messages.Group.HTML, var1, "dc.anchor.already.defined", var11);
                     }
                  }
                  break;
               case HREF:
                  if (var3 == HtmlTag.A) {
                     var7 = this.getAttrValue(var1);
                     if (var7 != null && !var7.isEmpty()) {
                        Matcher var8 = docRoot.matcher(var7);
                        if (var8.matches()) {
                           String var9 = var8.group(2);
                           if (!var9.isEmpty()) {
                              this.checkURI(var1, var9);
                           }
                        } else {
                           this.checkURI(var1, var7);
                        }
                     } else {
                        this.env.messages.error(Messages.Group.HTML, var1, "dc.attr.lacks.value");
                     }
                  }
                  break;
               case VALUE:
                  if (var3 == HtmlTag.LI) {
                     var7 = this.getAttrValue(var1);
                     if (var7 != null && !var7.isEmpty()) {
                        if (!validNumber.matcher(var7).matches()) {
                           this.env.messages.error(Messages.Group.HTML, var1, "dc.attr.not.number");
                        }
                     } else {
                        this.env.messages.error(Messages.Group.HTML, var1, "dc.attr.lacks.value");
                     }
                  }
            }
         }
      }

      return (Void)super.visitAttribute(var1, var2);
   }

   private boolean checkAnchor(String var1) {
      Element var2 = this.getEnclosingPackageOrClass(this.env.currElement);
      if (var2 == null) {
         return true;
      } else {
         Object var3 = (Set)this.foundAnchors.get(var2);
         if (var3 == null) {
            this.foundAnchors.put(var2, var3 = new HashSet());
         }

         return ((Set)var3).add(var1);
      }
   }

   private Element getEnclosingPackageOrClass(Element var1) {
      while(var1 != null) {
         switch (var1.getKind()) {
            case CLASS:
            case ENUM:
            case INTERFACE:
            case PACKAGE:
               return var1;
            default:
               var1 = var1.getEnclosingElement();
         }
      }

      return var1;
   }

   private String getAttrValue(AttributeTree var1) {
      if (var1.getValue() == null) {
         return null;
      } else {
         StringWriter var2 = new StringWriter();

         try {
            (new DocPretty(var2)).print(var1.getValue());
         } catch (IOException var4) {
         }

         return var2.toString();
      }
   }

   private void checkURI(AttributeTree var1, String var2) {
      if (!var2.startsWith("javascript:")) {
         try {
            new URI(var2);
         } catch (URISyntaxException var4) {
            this.env.messages.error(Messages.Group.HTML, var1, "dc.invalid.uri", var2);
         }

      }
   }

   public Void visitAuthor(AuthorTree var1, Void var2) {
      this.warnIfEmpty((DocTree)var1, (List)var1.getName());
      return (Void)super.visitAuthor(var1, var2);
   }

   public Void visitDocRoot(DocRootTree var1, Void var2) {
      this.markEnclosingTag(Checker.Flag.HAS_INLINE_TAG);
      return (Void)super.visitDocRoot(var1, var2);
   }

   public Void visitInheritDoc(InheritDocTree var1, Void var2) {
      this.markEnclosingTag(Checker.Flag.HAS_INLINE_TAG);
      this.foundInheritDoc = true;
      return (Void)super.visitInheritDoc(var1, var2);
   }

   public Void visitLink(LinkTree var1, Void var2) {
      this.markEnclosingTag(Checker.Flag.HAS_INLINE_TAG);
      HtmlTag var3 = var1.getKind() == DocTree.Kind.LINK ? HtmlTag.CODE : HtmlTag.SPAN;
      this.tagStack.push(new TagStackItem(var1, var3));

      Void var4;
      try {
         var4 = (Void)super.visitLink(var1, var2);
      } finally {
         this.tagStack.pop();
      }

      return var4;
   }

   public Void visitLiteral(LiteralTree var1, Void var2) {
      this.markEnclosingTag(Checker.Flag.HAS_INLINE_TAG);
      if (var1.getKind() == DocTree.Kind.CODE) {
         Iterator var3 = this.tagStack.iterator();

         while(var3.hasNext()) {
            TagStackItem var4 = (TagStackItem)var3.next();
            if (var4.tag == HtmlTag.CODE) {
               this.env.messages.warning(Messages.Group.HTML, var1, "dc.tag.code.within.code");
               break;
            }
         }
      }

      return (Void)super.visitLiteral(var1, var2);
   }

   public Void visitParam(ParamTree var1, Void var2) {
      boolean var3 = var1.isTypeParameter();
      IdentifierTree var4 = var1.getName();
      Element var5 = var4 != null ? this.env.trees.getElement(new DocTreePath(this.getCurrentPath(), var4)) : null;
      if (var5 == null) {
         switch (this.env.currElement.getKind()) {
            case CLASS:
            case INTERFACE:
               if (!var3) {
                  this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.invalid.param");
                  break;
               }
            case METHOD:
            case CONSTRUCTOR:
               this.env.messages.error(Messages.Group.REFERENCE, var4, "dc.param.name.not.found");
               break;
            case ENUM:
            default:
               this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.invalid.param");
         }
      } else {
         this.foundParams.add(var5);
      }

      this.warnIfEmpty((DocTree)var1, (List)var1.getDescription());
      return (Void)super.visitParam(var1, var2);
   }

   private void checkParamsDocumented(List var1) {
      if (!this.foundInheritDoc) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Element var3 = (Element)var2.next();
            if (!this.foundParams.contains(var3)) {
               Object var4 = var3.getKind() == ElementKind.TYPE_PARAMETER ? "<" + var3.getSimpleName() + ">" : var3.getSimpleName();
               this.reportMissing("dc.missing.param", var4);
            }
         }

      }
   }

   public Void visitReference(ReferenceTree var1, Void var2) {
      String var3 = var1.getSignature();
      if (var3.contains("<") || var3.contains(">")) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.type.arg.not.allowed");
      }

      Element var4 = this.env.trees.getElement(this.getCurrentPath());
      if (var4 == null) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.ref.not.found");
      }

      return (Void)super.visitReference(var1, var2);
   }

   public Void visitReturn(ReturnTree var1, Void var2) {
      Element var3 = this.env.trees.getElement(this.env.currPath);
      if (var3.getKind() != ElementKind.METHOD || ((ExecutableElement)var3).getReturnType().getKind() == TypeKind.VOID) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.invalid.return");
      }

      this.foundReturn = true;
      this.warnIfEmpty((DocTree)var1, (List)var1.getDescription());
      return (Void)super.visitReturn(var1, var2);
   }

   public Void visitSerialData(SerialDataTree var1, Void var2) {
      this.warnIfEmpty((DocTree)var1, (List)var1.getDescription());
      return (Void)super.visitSerialData(var1, var2);
   }

   public Void visitSerialField(SerialFieldTree var1, Void var2) {
      this.warnIfEmpty((DocTree)var1, (List)var1.getDescription());
      return (Void)super.visitSerialField(var1, var2);
   }

   public Void visitSince(SinceTree var1, Void var2) {
      this.warnIfEmpty((DocTree)var1, (List)var1.getBody());
      return (Void)super.visitSince(var1, var2);
   }

   public Void visitThrows(ThrowsTree var1, Void var2) {
      ReferenceTree var3 = var1.getExceptionName();
      Element var4 = this.env.trees.getElement(new DocTreePath(this.getCurrentPath(), var3));
      if (var4 == null) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.ref.not.found");
      } else if (this.isThrowable(var4.asType())) {
         switch (this.env.currElement.getKind()) {
            case METHOD:
            case CONSTRUCTOR:
               if (this.isCheckedException(var4.asType())) {
                  ExecutableElement var5 = (ExecutableElement)this.env.currElement;
                  this.checkThrowsDeclared(var3, var4.asType(), var5.getThrownTypes());
               }
               break;
            default:
               this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.invalid.throws");
         }
      } else {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.invalid.throws");
      }

      this.warnIfEmpty((DocTree)var1, (List)var1.getDescription());
      return (Void)this.scan(var1.getDescription(), var2);
   }

   private boolean isThrowable(TypeMirror var1) {
      switch (var1.getKind()) {
         case DECLARED:
         case TYPEVAR:
            return this.env.types.isAssignable(var1, this.env.java_lang_Throwable);
         default:
            return false;
      }
   }

   private void checkThrowsDeclared(ReferenceTree var1, TypeMirror var2, List var3) {
      boolean var4 = false;
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         TypeMirror var6 = (TypeMirror)var5.next();
         if (this.env.types.isAssignable(var2, var6)) {
            this.foundThrows.add(var6);
            var4 = true;
         }
      }

      if (!var4) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.exception.not.thrown", var2);
      }

   }

   private void checkThrowsDocumented(List var1) {
      if (!this.foundInheritDoc) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            TypeMirror var3 = (TypeMirror)var2.next();
            if (this.isCheckedException(var3) && !this.foundThrows.contains(var3)) {
               this.reportMissing("dc.missing.throws", var3);
            }
         }

      }
   }

   public Void visitUnknownBlockTag(UnknownBlockTagTree var1, Void var2) {
      this.checkUnknownTag(var1, var1.getTagName());
      return (Void)super.visitUnknownBlockTag(var1, var2);
   }

   public Void visitUnknownInlineTag(UnknownInlineTagTree var1, Void var2) {
      this.checkUnknownTag(var1, var1.getTagName());
      return (Void)super.visitUnknownInlineTag(var1, var2);
   }

   private void checkUnknownTag(DocTree var1, String var2) {
      if (this.env.customTags != null && !this.env.customTags.contains(var2)) {
         this.env.messages.error(Messages.Group.SYNTAX, var1, "dc.tag.unknown", var2);
      }

   }

   public Void visitValue(ValueTree var1, Void var2) {
      ReferenceTree var3 = var1.getReference();
      if (var3 != null && !var3.getSignature().isEmpty()) {
         Element var4 = this.env.trees.getElement(new DocTreePath(this.getCurrentPath(), var3));
         if (!this.isConstant(var4)) {
            this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.value.not.a.constant");
         }
      } else if (!this.isConstant(this.env.currElement)) {
         this.env.messages.error(Messages.Group.REFERENCE, var1, "dc.value.not.allowed.here");
      }

      this.markEnclosingTag(Checker.Flag.HAS_INLINE_TAG);
      return (Void)super.visitValue(var1, var2);
   }

   private boolean isConstant(Element var1) {
      if (var1 == null) {
         return false;
      } else {
         switch (var1.getKind()) {
            case FIELD:
               Object var2 = ((VariableElement)var1).getConstantValue();
               return var2 != null;
            default:
               return false;
         }
      }
   }

   public Void visitVersion(VersionTree var1, Void var2) {
      this.warnIfEmpty((DocTree)var1, (List)var1.getBody());
      return (Void)super.visitVersion(var1, var2);
   }

   public Void visitErroneous(ErroneousTree var1, Void var2) {
      this.env.messages.error(Messages.Group.SYNTAX, var1, (String)null, var1.getDiagnostic().getMessage((Locale)null));
      return null;
   }

   private boolean isCheckedException(TypeMirror var1) {
      return !this.env.types.isAssignable(var1, this.env.java_lang_Error) && !this.env.types.isAssignable(var1, this.env.java_lang_RuntimeException);
   }

   private boolean isSynthetic() {
      switch (this.env.currElement.getKind()) {
         case CONSTRUCTOR:
            TreePath var1 = this.env.currPath;
            return this.env.getPos(var1) == this.env.getPos(var1.getParentPath());
         default:
            return false;
      }
   }

   void markEnclosingTag(Flag var1) {
      TagStackItem var2 = (TagStackItem)this.tagStack.peek();
      if (var2 != null) {
         var2.flags.add(var1);
      }

   }

   String toString(TreePath var1) {
      StringBuilder var2 = new StringBuilder("TreePath[");
      this.toString(var1, var2);
      var2.append("]");
      return var2.toString();
   }

   void toString(TreePath var1, StringBuilder var2) {
      TreePath var3 = var1.getParentPath();
      if (var3 != null) {
         this.toString(var3, var2);
         var2.append(",");
      }

      var2.append(var1.getLeaf().getKind()).append(":").append(this.env.getPos(var1)).append(":S").append(this.env.getStartPos(var1));
   }

   void warnIfEmpty(DocTree var1, List var2) {
      Iterator var3 = var2.iterator();

      DocTree var4;
      do {
         if (!var3.hasNext()) {
            this.env.messages.warning(Messages.Group.SYNTAX, var1, "dc.empty", var1.getKind().tagName);
            return;
         }

         var4 = (DocTree)var3.next();
         switch (var4.getKind()) {
            case TEXT:
               break;
            default:
               return;
         }
      } while(!this.hasNonWhitespace((TextTree)var4));

   }

   boolean hasNonWhitespace(TextTree var1) {
      String var2 = var1.getBody();

      for(int var3 = 0; var3 < var2.length(); ++var3) {
         if (!Character.isWhitespace(var2.charAt(var3))) {
            return true;
         }
      }

      return false;
   }

   static class TagStackItem {
      final DocTree tree;
      final HtmlTag tag;
      final Set attrs;
      final Set flags;

      TagStackItem(DocTree var1, HtmlTag var2) {
         this.tree = var1;
         this.tag = var2;
         this.attrs = EnumSet.noneOf(HtmlTag.Attr.class);
         this.flags = EnumSet.noneOf(Flag.class);
      }

      public String toString() {
         return String.valueOf(this.tag);
      }
   }

   public static enum Flag {
      TABLE_HAS_CAPTION,
      HAS_ELEMENT,
      HAS_INLINE_TAG,
      HAS_TEXT,
      REPORTED_BAD_INLINE;
   }
}
