package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlAttr;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.DeprecatedTaglet;
import com.sun.tools.doclets.internal.toolkit.util.MethodTypes;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMemberWriter {
   protected final ConfigurationImpl configuration;
   protected final SubWriterHolderWriter writer;
   protected final ClassDoc classdoc;
   protected Map typeMap;
   protected Set methodTypes;
   private int methodTypesOr;
   public final boolean nodepr;
   protected boolean printedSummaryHeader;

   public AbstractMemberWriter(SubWriterHolderWriter var1, ClassDoc var2) {
      this.typeMap = new LinkedHashMap();
      this.methodTypes = EnumSet.noneOf(MethodTypes.class);
      this.methodTypesOr = 0;
      this.printedSummaryHeader = false;
      this.configuration = var1.configuration;
      this.writer = var1;
      this.nodepr = this.configuration.nodeprecated;
      this.classdoc = var2;
   }

   public AbstractMemberWriter(SubWriterHolderWriter var1) {
      this(var1, (ClassDoc)null);
   }

   public abstract void addSummaryLabel(Content var1);

   public abstract String getTableSummary();

   public abstract Content getCaption();

   public abstract String[] getSummaryTableHeader(ProgramElementDoc var1);

   public abstract void addInheritedSummaryLabel(ClassDoc var1, Content var2);

   public abstract void addSummaryAnchor(ClassDoc var1, Content var2);

   public abstract void addInheritedSummaryAnchor(ClassDoc var1, Content var2);

   protected abstract void addSummaryType(ProgramElementDoc var1, Content var2);

   protected void addSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
      this.addSummaryLink(LinkInfoImpl.Kind.MEMBER, var1, var2, var3);
   }

   protected abstract void addSummaryLink(LinkInfoImpl.Kind var1, ClassDoc var2, ProgramElementDoc var3, Content var4);

   protected abstract void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3);

   protected abstract Content getDeprecatedLink(ProgramElementDoc var1);

   protected abstract Content getNavSummaryLink(ClassDoc var1, boolean var2);

   protected abstract void addNavDetailLink(boolean var1, Content var2);

   protected void addName(String var1, Content var2) {
      var2.addContent(var1);
   }

   protected String modifierString(MemberDoc var1) {
      int var2 = var1.modifierSpecifier();
      short var3 = 288;
      return Modifier.toString(var2 & ~var3);
   }

   protected String typeString(MemberDoc var1) {
      String var2 = "";
      if (var1 instanceof MethodDoc) {
         var2 = ((MethodDoc)var1).returnType().toString();
      } else if (var1 instanceof FieldDoc) {
         var2 = ((FieldDoc)var1).type().toString();
      }

      return var2;
   }

   protected void addModifiers(MemberDoc var1, Content var2) {
      String var3 = this.modifierString(var1);
      if ((var1.isField() || var1.isMethod()) && this.writer instanceof ClassWriterImpl && ((ClassWriterImpl)this.writer).getClassDoc().isInterface()) {
         var3 = var1.isMethod() && ((MethodDoc)var1).isDefault() ? Util.replaceText(var3, "public", "default").trim() : Util.replaceText(var3, "public", "").trim();
      }

      if (var3.length() > 0) {
         var2.addContent(var3);
         var2.addContent(this.writer.getSpace());
      }

   }

   protected String makeSpace(int var1) {
      if (var1 <= 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.append(' ');
         }

         return var2.toString();
      }
   }

   protected void addModifierAndType(ProgramElementDoc var1, Type var2, Content var3) {
      HtmlTree var4 = new HtmlTree(HtmlTag.CODE);
      this.addModifier(var1, var4);
      if (var2 == null) {
         if (var1.isClass()) {
            var4.addContent("class");
         } else {
            var4.addContent("interface");
         }

         var4.addContent(this.writer.getSpace());
      } else if (var1 instanceof ExecutableMemberDoc && ((ExecutableMemberDoc)var1).typeParameters().length > 0) {
         Content var5 = ((AbstractExecutableMemberWriter)this).getTypeParameters((ExecutableMemberDoc)var1);
         var4.addContent(var5);
         if (var5.charCount() > 10) {
            var4.addContent((Content)(new HtmlTree(HtmlTag.BR)));
         } else {
            var4.addContent(this.writer.getSpace());
         }

         var4.addContent(this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.SUMMARY_RETURN_TYPE, var2)));
      } else {
         var4.addContent(this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.SUMMARY_RETURN_TYPE, var2)));
      }

      var3.addContent((Content)var4);
   }

   private void addModifier(ProgramElementDoc var1, Content var2) {
      if (var1.isProtected()) {
         var2.addContent("protected ");
      } else if (var1.isPrivate()) {
         var2.addContent("private ");
      } else if (!var1.isPublic()) {
         var2.addContent(this.configuration.getText("doclet.Package_private"));
         var2.addContent(" ");
      }

      if (var1.isMethod()) {
         if (!var1.containingClass().isInterface() && ((MethodDoc)var1).isAbstract()) {
            var2.addContent("abstract ");
         }

         if (((MethodDoc)var1).isDefault()) {
            var2.addContent("default ");
         }
      }

      if (var1.isStatic()) {
         var2.addContent("static ");
      }

   }

   protected void addDeprecatedInfo(ProgramElementDoc var1, Content var2) {
      Content var3 = (new DeprecatedTaglet()).getTagletOutput(var1, this.writer.getTagletWriterInstance(false));
      if (!var3.isEmpty()) {
         HtmlTree var5 = HtmlTree.DIV(HtmlStyle.block, var3);
         var2.addContent((Content)var5);
      }

   }

   protected void addComment(ProgramElementDoc var1, Content var2) {
      if (var1.inlineTags().length > 0) {
         this.writer.addInlineComment(var1, var2);
      }

   }

   protected String name(ProgramElementDoc var1) {
      return var1.name();
   }

   protected Content getHead(MemberDoc var1) {
      StringContent var2 = new StringContent(var1.name());
      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.MEMBER_HEADING, var2);
      return var3;
   }

   protected boolean isInherited(ProgramElementDoc var1) {
      return !var1.isPrivate() && (!var1.isPackagePrivate() || var1.containingPackage().equals(this.classdoc.containingPackage()));
   }

   protected void addDeprecatedAPI(List var1, String var2, String var3, String[] var4, Content var5) {
      if (var1.size() > 0) {
         HtmlTree var6 = HtmlTree.TABLE(HtmlStyle.deprecatedSummary, 0, 3, 0, var3, this.writer.getTableCaption(this.configuration.getResource(var2)));
         var6.addContent(this.writer.getSummaryTableHeader(var4, "col"));
         HtmlTree var7 = new HtmlTree(HtmlTag.TBODY);

         for(int var8 = 0; var8 < var1.size(); ++var8) {
            ProgramElementDoc var9 = (ProgramElementDoc)var1.get(var8);
            HtmlTree var10 = HtmlTree.TD(HtmlStyle.colOne, this.getDeprecatedLink(var9));
            if (var9.tags("deprecated").length > 0) {
               this.writer.addInlineDeprecatedComment(var9, var9.tags("deprecated")[0], var10);
            }

            HtmlTree var11 = HtmlTree.TR(var10);
            if (var8 % 2 == 0) {
               var11.addStyle(HtmlStyle.altColor);
            } else {
               var11.addStyle(HtmlStyle.rowColor);
            }

            var7.addContent((Content)var11);
         }

         var6.addContent((Content)var7);
         HtmlTree var12 = HtmlTree.LI(HtmlStyle.blockList, var6);
         HtmlTree var13 = HtmlTree.UL(HtmlStyle.blockList, var12);
         var5.addContent((Content)var13);
      }

   }

   protected void addUseInfo(List var1, Content var2, String var3, Content var4) {
      if (var1 != null) {
         boolean var6 = false;
         if (var1.size() > 0) {
            HtmlTree var7 = HtmlTree.TABLE(HtmlStyle.useSummary, 0, 3, 0, var3, this.writer.getTableCaption(var2));
            HtmlTree var8 = new HtmlTree(HtmlTag.TBODY);
            Iterator var9 = var1.iterator();

            for(int var10 = 0; var9.hasNext(); ++var10) {
               ProgramElementDoc var11 = (ProgramElementDoc)var9.next();
               ClassDoc var12 = var11.containingClass();
               if (!var6) {
                  var7.addContent(this.writer.getSummaryTableHeader(this.getSummaryTableHeader(var11), "col"));
                  var6 = true;
               }

               HtmlTree var13 = new HtmlTree(HtmlTag.TR);
               if (var10 % 2 == 0) {
                  var13.addStyle(HtmlStyle.altColor);
               } else {
                  var13.addStyle(HtmlStyle.rowColor);
               }

               HtmlTree var14 = new HtmlTree(HtmlTag.TD);
               var14.addStyle(HtmlStyle.colFirst);
               this.writer.addSummaryType(this, var11, var14);
               var13.addContent((Content)var14);
               HtmlTree var15 = new HtmlTree(HtmlTag.TD);
               var15.addStyle(HtmlStyle.colLast);
               if (var12 != null && !(var11 instanceof ConstructorDoc) && !(var11 instanceof ClassDoc)) {
                  HtmlTree var16 = new HtmlTree(HtmlTag.SPAN);
                  var16.addStyle(HtmlStyle.typeNameLabel);
                  var16.addContent(var12.name() + ".");
                  var15.addContent((Content)var16);
               }

               this.addSummaryLink(var11 instanceof ClassDoc ? LinkInfoImpl.Kind.CLASS_USE : LinkInfoImpl.Kind.MEMBER, var12, var11, var15);
               this.writer.addSummaryLinkComment(this, var11, var15);
               var13.addContent((Content)var15);
               var8.addContent((Content)var13);
            }

            var7.addContent((Content)var8);
            var4.addContent((Content)var7);
         }

      }
   }

   protected void addNavDetailLink(List var1, Content var2) {
      this.addNavDetailLink(var1.size() > 0, var2);
   }

   protected void addNavSummaryLink(List var1, VisibleMemberMap var2, Content var3) {
      if (var1.size() > 0) {
         var3.addContent(this.getNavSummaryLink((ClassDoc)null, true));
      } else {
         for(ClassDoc var4 = this.classdoc.superclass(); var4 != null; var4 = var4.superclass()) {
            List var5 = var2.getMembersFor(var4);
            if (var5.size() > 0) {
               var3.addContent(this.getNavSummaryLink(var4, true));
               return;
            }
         }

         var3.addContent(this.getNavSummaryLink((ClassDoc)null, false));
      }
   }

   protected void serialWarning(SourcePosition var1, String var2, String var3, String var4) {
      if (this.configuration.serialwarn) {
         this.configuration.getDocletSpecificMsg().warning(var1, var2, var3, var4);
      }

   }

   public ProgramElementDoc[] eligibleMembers(ProgramElementDoc[] var1) {
      return this.nodepr ? Util.excludeDeprecatedMembers(var1) : var1;
   }

   public void addMemberSummary(ClassDoc var1, ProgramElementDoc var2, Tag[] var3, List var4, int var5) {
      HtmlTree var6 = new HtmlTree(HtmlTag.TD);
      var6.addStyle(HtmlStyle.colFirst);
      this.writer.addSummaryType(this, var2, var6);
      HtmlTree var7 = new HtmlTree(HtmlTag.TD);
      this.setSummaryColumnStyle(var7);
      this.addSummaryLink(var1, var2, var7);
      this.writer.addSummaryLinkComment(this, var2, var3, var7);
      HtmlTree var8 = HtmlTree.TR(var6);
      var8.addContent((Content)var7);
      if (var2 instanceof MethodDoc && !var2.isAnnotationTypeElement()) {
         int var9 = var2.isStatic() ? MethodTypes.STATIC.value() : MethodTypes.INSTANCE.value();
         if (var2.containingClass().isInterface()) {
            var9 = ((MethodDoc)var2).isAbstract() ? var9 | MethodTypes.ABSTRACT.value() : var9 | MethodTypes.DEFAULT.value();
         } else {
            var9 = ((MethodDoc)var2).isAbstract() ? var9 | MethodTypes.ABSTRACT.value() : var9 | MethodTypes.CONCRETE.value();
         }

         if (Util.isDeprecated(var2) || Util.isDeprecated(this.classdoc)) {
            var9 |= MethodTypes.DEPRECATED.value();
         }

         this.methodTypesOr |= var9;
         String var10 = "i" + var5;
         this.typeMap.put(var10, var9);
         var8.addAttr(HtmlAttr.ID, var10);
      }

      if (var5 % 2 == 0) {
         var8.addStyle(HtmlStyle.altColor);
      } else {
         var8.addStyle(HtmlStyle.rowColor);
      }

      var4.add(var8);
   }

   public boolean showTabs() {
      Iterator var2 = EnumSet.allOf(MethodTypes.class).iterator();

      while(var2.hasNext()) {
         MethodTypes var3 = (MethodTypes)var2.next();
         int var1 = var3.value();
         if ((var1 & this.methodTypesOr) == var1) {
            this.methodTypes.add(var3);
         }
      }

      boolean var4 = this.methodTypes.size() > 1;
      if (var4) {
         this.methodTypes.add(MethodTypes.ALL);
      }

      return var4;
   }

   public void setSummaryColumnStyle(HtmlTree var1) {
      var1.addStyle(HtmlStyle.colLast);
   }

   public void addInheritedMemberSummary(ClassDoc var1, ProgramElementDoc var2, boolean var3, boolean var4, Content var5) {
      this.writer.addInheritedMemberSummary(this, var1, var2, var3, var5);
   }

   public Content getInheritedSummaryHeader(ClassDoc var1) {
      Content var2 = this.writer.getMemberTreeHeader();
      this.writer.addInheritedSummaryHeader(this, var1, var2);
      return var2;
   }

   public Content getInheritedSummaryLinksTree() {
      return new HtmlTree(HtmlTag.CODE);
   }

   public Content getSummaryTableTree(ClassDoc var1, List var2) {
      return this.writer.getSummaryTableTree(this, var1, var2, this.showTabs());
   }

   public Content getMemberTree(Content var1) {
      return this.writer.getMemberTree(var1);
   }

   public Content getMemberTree(Content var1, boolean var2) {
      return var2 ? HtmlTree.UL(HtmlStyle.blockListLast, var1) : HtmlTree.UL(HtmlStyle.blockList, var1);
   }
}
