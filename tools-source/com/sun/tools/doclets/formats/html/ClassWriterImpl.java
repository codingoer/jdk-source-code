package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.builders.MemberSummaryBuilder;
import com.sun.tools.doclets.internal.toolkit.taglets.ParamTaglet;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javadoc.RootDocImpl;
import java.io.IOException;
import java.util.List;

public class ClassWriterImpl extends SubWriterHolderWriter implements ClassWriter {
   protected final ClassDoc classDoc;
   protected final ClassTree classtree;
   protected final ClassDoc prev;
   protected final ClassDoc next;

   public ClassWriterImpl(ConfigurationImpl var1, ClassDoc var2, ClassDoc var3, ClassDoc var4, ClassTree var5) throws IOException {
      super(var1, DocPath.forClass(var2));
      this.classDoc = var2;
      var1.currentcd = var2;
      this.classtree = var5;
      this.prev = var3;
      this.next = var4;
   }

   protected Content getNavLinkPackage() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_SUMMARY, this.packageLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClass() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.classLabel);
      return var1;
   }

   protected Content getNavLinkClassUse() {
      Content var1 = this.getHyperLink(DocPaths.CLASS_USE.resolve(this.filename), this.useLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   public Content getNavLinkPrevious() {
      HtmlTree var1;
      if (this.prev != null) {
         Content var2 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS, this.prev)).label(this.prevclassLabel).strong(true));
         var1 = HtmlTree.LI(var2);
      } else {
         var1 = HtmlTree.LI(this.prevclassLabel);
      }

      return var1;
   }

   public Content getNavLinkNext() {
      HtmlTree var1;
      if (this.next != null) {
         Content var2 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS, this.next)).label(this.nextclassLabel).strong(true));
         var1 = HtmlTree.LI(var2);
      } else {
         var1 = HtmlTree.LI(this.nextclassLabel);
      }

      return var1;
   }

   public Content getHeader(String var1) {
      String var2 = this.classDoc.containingPackage() != null ? this.classDoc.containingPackage().name() : "";
      String var3 = this.classDoc.name();
      HtmlTree var4 = this.getBody(true, this.getWindowTitle(var3));
      this.addTop(var4);
      this.addNavLinks(true, var4);
      var4.addContent(HtmlConstants.START_OF_CLASS_DATA);
      HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
      var5.addStyle(HtmlStyle.header);
      if (this.configuration.showProfiles) {
         String var6 = "";
         int var7 = this.configuration.profiles.getProfile(this.getTypeNameForProfile(this.classDoc));
         if (var7 > 0) {
            StringContent var8 = new StringContent();

            for(int var9 = var7; var9 < this.configuration.profiles.getProfileCount(); ++var9) {
               var8.addContent(var6);
               var8.addContent(Profile.lookup(var9).name);
               var6 = ", ";
            }

            HtmlTree var15 = HtmlTree.DIV(HtmlStyle.subTitle, var8);
            var5.addContent((Content)var15);
         }
      }

      if (var2.length() > 0) {
         StringContent var10 = new StringContent(var2);
         HtmlTree var12 = HtmlTree.DIV(HtmlStyle.subTitle, var10);
         var5.addContent((Content)var12);
      }

      LinkInfoImpl var11 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_HEADER, this.classDoc);
      var11.linkToSelf = false;
      StringContent var13 = new StringContent(var1);
      HtmlTree var14 = HtmlTree.HEADING(HtmlConstants.CLASS_PAGE_HEADING, true, HtmlStyle.title, var13);
      var14.addContent(this.getTypeParameterLinks(var11));
      var5.addContent((Content)var14);
      var4.addContent((Content)var5);
      return var4;
   }

   public Content getClassContentHeader() {
      return this.getContentHeader();
   }

   public void addFooter(Content var1) {
      var1.addContent(HtmlConstants.END_OF_CLASS_DATA);
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument(this.configuration.metakeywords.getMetaKeywords(this.classDoc), true, var1);
   }

   public Content getClassInfoTreeHeader() {
      return this.getMemberTreeHeader();
   }

   public Content getClassInfo(Content var1) {
      return this.getMemberTree(HtmlStyle.description, var1);
   }

   public void addClassSignature(String var1, Content var2) {
      boolean var3 = this.classDoc.isInterface();
      var2.addContent((Content)(new HtmlTree(HtmlTag.BR)));
      HtmlTree var4 = new HtmlTree(HtmlTag.PRE);
      this.addAnnotationInfo(this.classDoc, var4);
      var4.addContent(var1);
      LinkInfoImpl var5 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_SIGNATURE, this.classDoc);
      var5.linkToSelf = false;
      StringContent var6 = new StringContent(this.classDoc.name());
      Content var7 = this.getTypeParameterLinks(var5);
      if (this.configuration.linksource) {
         this.addSrcLink(this.classDoc, var6, var4);
         var4.addContent(var7);
      } else {
         HtmlTree var8 = HtmlTree.SPAN(HtmlStyle.typeNameLabel, var6);
         var8.addContent(var7);
         var4.addContent((Content)var8);
      }

      if (!var3) {
         Type var13 = Util.getFirstVisibleSuperClass(this.classDoc, this.configuration);
         if (var13 != null) {
            var4.addContent(DocletConstants.NL);
            var4.addContent("extends ");
            Content var9 = this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_SIGNATURE_PARENT_NAME, var13));
            var4.addContent(var9);
         }
      }

      Type[] var14 = this.classDoc.interfaceTypes();
      if (var14 != null && var14.length > 0) {
         int var15 = 0;

         for(int var10 = 0; var10 < var14.length; ++var10) {
            ClassDoc var11 = var14[var10].asClassDoc();
            if (var11.isPublic() || Util.isLinkable(var11, this.configuration)) {
               if (var15 == 0) {
                  var4.addContent(DocletConstants.NL);
                  var4.addContent(var3 ? "extends " : "implements ");
               } else {
                  var4.addContent(", ");
               }

               Content var12 = this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_SIGNATURE_PARENT_NAME, var14[var10]));
               var4.addContent(var12);
               ++var15;
            }
         }
      }

      var2.addContent((Content)var4);
   }

   public void addClassDescription(Content var1) {
      if (!this.configuration.nocomment && this.classDoc.inlineTags().length > 0) {
         this.addInlineComment(this.classDoc, var1);
      }

   }

   public void addClassTagInfo(Content var1) {
      if (!this.configuration.nocomment) {
         this.addTagsInfo(this.classDoc, var1);
      }

   }

   private Content getClassInheritenceTree(Type var1) {
      HtmlTree var3 = new HtmlTree(HtmlTag.UL);
      var3.addStyle(HtmlStyle.inheritance);
      HtmlTree var4 = null;

      Type var2;
      do {
         var2 = Util.getFirstVisibleSuperClass(var1 instanceof ClassDoc ? (ClassDoc)var1 : var1.asClassDoc(), this.configuration);
         if (var2 != null) {
            HtmlTree var5 = new HtmlTree(HtmlTag.UL);
            var5.addStyle(HtmlStyle.inheritance);
            var5.addContent(this.getTreeForClassHelper(var1));
            if (var4 != null) {
               var5.addContent((Content)var4);
            }

            HtmlTree var6 = HtmlTree.LI(var5);
            var4 = var6;
            var1 = var2;
         } else {
            var3.addContent(this.getTreeForClassHelper(var1));
         }
      } while(var2 != null);

      if (var4 != null) {
         var3.addContent((Content)var4);
      }

      return var3;
   }

   private Content getTreeForClassHelper(Type var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.LI);
      Content var3;
      if (var1.equals(this.classDoc)) {
         var3 = this.getTypeParameterLinks(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.TREE, this.classDoc));
         if (this.configuration.shouldExcludeQualifier(this.classDoc.containingPackage().name())) {
            var2.addContent(var1.asClassDoc().name());
            var2.addContent(var3);
         } else {
            var2.addContent(var1.asClassDoc().qualifiedName());
            var2.addContent(var3);
         }
      } else {
         var3 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_TREE_PARENT, var1)).label(this.configuration.getClassName(var1.asClassDoc())));
         var2.addContent(var3);
      }

      return var2;
   }

   public void addClassTree(Content var1) {
      if (this.classDoc.isClass()) {
         var1.addContent(this.getClassInheritenceTree(this.classDoc));
      }
   }

   public void addTypeParamInfo(Content var1) {
      if (this.classDoc.typeParamTags().length > 0) {
         Content var2 = (new ParamTaglet()).getTagletOutput(this.classDoc, this.getTagletWriterInstance(false));
         HtmlTree var3 = HtmlTree.DL(var2);
         var1.addContent((Content)var3);
      }

   }

   public void addSubClassInfo(Content var1) {
      if (this.classDoc.isClass()) {
         if (this.classDoc.qualifiedName().equals("java.lang.Object") || this.classDoc.qualifiedName().equals("org.omg.CORBA.Object")) {
            return;
         }

         List var2 = this.classtree.subs(this.classDoc, false);
         if (var2.size() > 0) {
            Content var3 = this.getResource("doclet.Subclasses");
            HtmlTree var4 = HtmlTree.DT(var3);
            HtmlTree var5 = HtmlTree.DL(var4);
            var5.addContent(this.getClassLinks(LinkInfoImpl.Kind.SUBCLASSES, var2));
            var1.addContent((Content)var5);
         }
      }

   }

   public void addSubInterfacesInfo(Content var1) {
      if (this.classDoc.isInterface()) {
         List var2 = this.classtree.allSubs(this.classDoc, false);
         if (var2.size() > 0) {
            Content var3 = this.getResource("doclet.Subinterfaces");
            HtmlTree var4 = HtmlTree.DT(var3);
            HtmlTree var5 = HtmlTree.DL(var4);
            var5.addContent(this.getClassLinks(LinkInfoImpl.Kind.SUBINTERFACES, var2));
            var1.addContent((Content)var5);
         }
      }

   }

   public void addInterfaceUsageInfo(Content var1) {
      if (this.classDoc.isInterface()) {
         if (!this.classDoc.qualifiedName().equals("java.lang.Cloneable") && !this.classDoc.qualifiedName().equals("java.io.Serializable")) {
            List var2 = this.classtree.implementingclasses(this.classDoc);
            if (var2.size() > 0) {
               Content var3 = this.getResource("doclet.Implementing_Classes");
               HtmlTree var4 = HtmlTree.DT(var3);
               HtmlTree var5 = HtmlTree.DL(var4);
               var5.addContent(this.getClassLinks(LinkInfoImpl.Kind.IMPLEMENTED_CLASSES, var2));
               var1.addContent((Content)var5);
            }

         }
      }
   }

   public void addImplementedInterfacesInfo(Content var1) {
      List var2 = Util.getAllInterfaces(this.classDoc, this.configuration);
      if (this.classDoc.isClass() && var2.size() > 0) {
         Content var3 = this.getResource("doclet.All_Implemented_Interfaces");
         HtmlTree var4 = HtmlTree.DT(var3);
         HtmlTree var5 = HtmlTree.DL(var4);
         var5.addContent(this.getClassLinks(LinkInfoImpl.Kind.IMPLEMENTED_INTERFACES, var2));
         var1.addContent((Content)var5);
      }

   }

   public void addSuperInterfacesInfo(Content var1) {
      List var2 = Util.getAllInterfaces(this.classDoc, this.configuration);
      if (this.classDoc.isInterface() && var2.size() > 0) {
         Content var3 = this.getResource("doclet.All_Superinterfaces");
         HtmlTree var4 = HtmlTree.DT(var3);
         HtmlTree var5 = HtmlTree.DL(var4);
         var5.addContent(this.getClassLinks(LinkInfoImpl.Kind.SUPER_INTERFACES, var2));
         var1.addContent((Content)var5);
      }

   }

   public void addNestedClassInfo(Content var1) {
      ClassDoc var2 = this.classDoc.containingClass();
      if (var2 != null) {
         Content var3;
         if (var2.isInterface()) {
            var3 = this.getResource("doclet.Enclosing_Interface");
         } else {
            var3 = this.getResource("doclet.Enclosing_Class");
         }

         HtmlTree var4 = HtmlTree.DT(var3);
         HtmlTree var5 = HtmlTree.DL(var4);
         HtmlTree var6 = new HtmlTree(HtmlTag.DD);
         var6.addContent(this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS, var2)));
         var5.addContent((Content)var6);
         var1.addContent((Content)var5);
      }

   }

   public void addFunctionalInterfaceInfo(Content var1) {
      if (this.isFunctionalInterface()) {
         HtmlTree var2 = HtmlTree.DT(this.getResource("doclet.Functional_Interface"));
         HtmlTree var3 = HtmlTree.DL(var2);
         HtmlTree var4 = new HtmlTree(HtmlTag.DD);
         var4.addContent(this.getResource("doclet.Functional_Interface_Message"));
         var3.addContent((Content)var4);
         var1.addContent((Content)var3);
      }

   }

   public boolean isFunctionalInterface() {
      if (this.configuration.root instanceof RootDocImpl) {
         RootDocImpl var1 = (RootDocImpl)this.configuration.root;
         AnnotationDesc[] var2 = this.classDoc.annotations();
         AnnotationDesc[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            AnnotationDesc var6 = var3[var5];
            if (var1.isFunctionalInterface(var6)) {
               return true;
            }
         }
      }

      return false;
   }

   public void addClassDeprecationInfo(Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.HR);
      var1.addContent((Content)var2);
      Tag[] var3 = this.classDoc.tags("deprecated");
      if (Util.isDeprecated(this.classDoc)) {
         HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
         HtmlTree var5 = HtmlTree.DIV(HtmlStyle.block, var4);
         if (var3.length > 0) {
            Tag[] var6 = var3[0].inlineTags();
            if (var6.length > 0) {
               var5.addContent(this.getSpace());
               this.addInlineDeprecatedComment(this.classDoc, var3[0], var5);
            }
         }

         var1.addContent((Content)var5);
      }

   }

   private Content getClassLinks(LinkInfoImpl.Kind var1, List var2) {
      Object[] var3 = var2.toArray();
      HtmlTree var4 = new HtmlTree(HtmlTag.DD);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         if (var5 > 0) {
            StringContent var6 = new StringContent(", ");
            var4.addContent((Content)var6);
         }

         Content var7;
         if (var3[var5] instanceof ClassDoc) {
            var7 = this.getLink(new LinkInfoImpl(this.configuration, var1, (ClassDoc)((ClassDoc)var3[var5])));
            var4.addContent(var7);
         } else {
            var7 = this.getLink(new LinkInfoImpl(this.configuration, var1, (Type)((Type)var3[var5])));
            var4.addContent(var7);
         }
      }

      return var4;
   }

   protected Content getNavLinkTree() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_TREE, this.treeLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected void addSummaryDetailLinks(Content var1) {
      try {
         HtmlTree var2 = HtmlTree.DIV(this.getNavSummaryLinks());
         var2.addContent(this.getNavDetailLinks());
         var1.addContent((Content)var2);
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new DocletAbortException(var3);
      }
   }

   protected Content getNavSummaryLinks() throws Exception {
      HtmlTree var1 = HtmlTree.LI(this.summaryLabel);
      var1.addContent(this.getSpace());
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.subNavList, var1);
      MemberSummaryBuilder var3 = (MemberSummaryBuilder)this.configuration.getBuilderFactory().getMemberSummaryBuilder((ClassWriter)this);
      String[] var4 = new String[]{"doclet.navNested", "doclet.navEnum", "doclet.navField", "doclet.navConstructor", "doclet.navMethod"};

      for(int var5 = 0; var5 < var4.length; ++var5) {
         HtmlTree var6 = new HtmlTree(HtmlTag.LI);
         if ((var5 != 1 || this.classDoc.isEnum()) && (var5 != 3 || !this.classDoc.isEnum())) {
            AbstractMemberWriter var7 = (AbstractMemberWriter)var3.getMemberSummaryWriter(var5);
            if (var7 == null) {
               var6.addContent(this.getResource(var4[var5]));
            } else {
               var7.addNavSummaryLink(var3.members(var5), var3.getVisibleMemberMap(var5), var6);
            }

            if (var5 < var4.length - 1) {
               this.addNavGap(var6);
            }

            var2.addContent((Content)var6);
         }
      }

      return var2;
   }

   protected Content getNavDetailLinks() throws Exception {
      HtmlTree var1 = HtmlTree.LI(this.detailLabel);
      var1.addContent(this.getSpace());
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.subNavList, var1);
      MemberSummaryBuilder var3 = (MemberSummaryBuilder)this.configuration.getBuilderFactory().getMemberSummaryBuilder((ClassWriter)this);
      String[] var4 = new String[]{"doclet.navNested", "doclet.navEnum", "doclet.navField", "doclet.navConstructor", "doclet.navMethod"};

      for(int var5 = 1; var5 < var4.length; ++var5) {
         HtmlTree var6 = new HtmlTree(HtmlTag.LI);
         AbstractMemberWriter var7 = (AbstractMemberWriter)var3.getMemberSummaryWriter(var5);
         if ((var5 != 1 || this.classDoc.isEnum()) && (var5 != 3 || !this.classDoc.isEnum())) {
            if (var7 == null) {
               var6.addContent(this.getResource(var4[var5]));
            } else {
               var7.addNavDetailLink(var3.members(var5), var6);
            }

            if (var5 < var4.length - 1) {
               this.addNavGap(var6);
            }

            var2.addContent((Content)var6);
         }
      }

      return var2;
   }

   protected void addNavGap(Content var1) {
      var1.addContent(this.getSpace());
      var1.addContent("|");
      var1.addContent(this.getSpace());
   }

   public ClassDoc getClassDoc() {
      return this.classDoc;
   }
}
