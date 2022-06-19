package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.Comment;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.DocType;
import com.sun.tools.doclets.formats.html.markup.HtmlAttr;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlDocWriter;
import com.sun.tools.doclets.formats.html.markup.HtmlDocument;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.PackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.taglets.DocRootTaglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocLink;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.ImplementedMethods;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.util.StringUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlDocletWriter extends HtmlDocWriter {
   public final DocPath pathToRoot;
   public final DocPath path;
   public final DocPath filename;
   public final ConfigurationImpl configuration;
   protected boolean printedAnnotationHeading = false;
   protected boolean printedAnnotationFieldHeading = false;
   private boolean isAnnotationDocumented = false;
   private boolean isContainerDocumented = false;
   private static final Pattern docrootPattern = Pattern.compile(Pattern.quote("{@docroot}"), 2);
   static final Set blockTags = new HashSet();

   public HtmlDocletWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.configuration = var1;
      this.path = var2;
      this.pathToRoot = var2.parent().invert();
      this.filename = var2.basename();
   }

   public String replaceDocRootDir(String var1) {
      int var2 = var1.indexOf("{@");
      if (var2 < 0) {
         return var1;
      } else {
         Matcher var3 = docrootPattern.matcher(var1);
         if (!var3.find()) {
            return var1;
         } else {
            StringBuilder var4 = new StringBuilder();
            int var5 = 0;

            do {
               int var6 = var3.start();
               var4.append(var1.substring(var5, var6));
               var5 = var3.end();
               if (this.configuration.docrootparent.length() > 0 && var1.startsWith("/..", var5)) {
                  var4.append(this.configuration.docrootparent);
                  var5 += 3;
               } else {
                  var4.append(this.pathToRoot.isEmpty() ? "." : this.pathToRoot.getPath());
               }

               if (var5 < var1.length() && var1.charAt(var5) != '/') {
                  var4.append('/');
               }
            } while(var3.find());

            var4.append(var1.substring(var5));
            return var4.toString();
         }
      }
   }

   public Content getAllClassesLinkScript(String var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.SCRIPT);
      var2.addAttr(HtmlAttr.TYPE, "text/javascript");
      String var3 = "<!--" + DocletConstants.NL + "  allClassesLink = document.getElementById(\"" + var1 + "\");" + DocletConstants.NL + "  if(window==top) {" + DocletConstants.NL + "    allClassesLink.style.display = \"block\";" + DocletConstants.NL + "  }" + DocletConstants.NL + "  else {" + DocletConstants.NL + "    allClassesLink.style.display = \"none\";" + DocletConstants.NL + "  }" + DocletConstants.NL + "  //-->" + DocletConstants.NL;
      RawHtml var4 = new RawHtml(var3);
      var2.addContent((Content)var4);
      HtmlTree var5 = HtmlTree.DIV(var2);
      return var5;
   }

   private void addMethodInfo(MethodDoc var1, Content var2) {
      ClassDoc[] var3 = var1.containingClass().interfaces();
      MethodDoc var4 = var1.overriddenMethod();
      if (var3.length > 0 && (new ImplementedMethods(var1, this.configuration)).build().length > 0 || var4 != null) {
         MethodWriterImpl.addImplementsInfo(this, var1, var2);
         if (var4 != null) {
            MethodWriterImpl.addOverridden(this, var1.overriddenType(), var4, var2);
         }
      }

   }

   protected void addTagsInfo(Doc var1, Content var2) {
      if (!this.configuration.nocomment) {
         HtmlTree var3 = new HtmlTree(HtmlTag.DL);
         if (var1 instanceof MethodDoc) {
            this.addMethodInfo((MethodDoc)var1, var3);
         }

         ContentBuilder var4 = new ContentBuilder();
         TagletWriter.genTagOuput(this.configuration.tagletManager, var1, this.configuration.tagletManager.getCustomTaglets(var1), this.getTagletWriterInstance(false), var4);
         var3.addContent((Content)var4);
         var2.addContent((Content)var3);
      }
   }

   protected boolean hasSerializationOverviewTags(FieldDoc var1) {
      ContentBuilder var2 = new ContentBuilder();
      TagletWriter.genTagOuput(this.configuration.tagletManager, var1, this.configuration.tagletManager.getCustomTaglets(var1), this.getTagletWriterInstance(false), var2);
      return !var2.isEmpty();
   }

   public TagletWriter getTagletWriterInstance(boolean var1) {
      return new TagletWriterImpl(this, var1);
   }

   public Content getTargetPackageLink(PackageDoc var1, String var2, Content var3) {
      return this.getHyperLink(this.pathString(var1, DocPaths.PACKAGE_SUMMARY), var3, "", var2);
   }

   public Content getTargetProfilePackageLink(PackageDoc var1, String var2, Content var3, String var4) {
      return this.getHyperLink(this.pathString(var1, DocPaths.profilePackageSummary(var4)), var3, "", var2);
   }

   public Content getTargetProfileLink(String var1, Content var2, String var3) {
      return this.getHyperLink(this.pathToRoot.resolve(DocPaths.profileSummary(var3)), var2, "", var1);
   }

   public String getTypeNameForProfile(ClassDoc var1) {
      StringBuilder var2 = new StringBuilder(var1.containingPackage().name().replace(".", "/"));
      var2.append("/").append(var1.name().replace(".", "$"));
      return var2.toString();
   }

   public boolean isTypeInProfile(ClassDoc var1, int var2) {
      return this.configuration.profiles.getProfile(this.getTypeNameForProfile(var1)) <= var2;
   }

   public void addClassesSummary(ClassDoc[] var1, String var2, String var3, String[] var4, Content var5, int var6) {
      if (var1.length > 0) {
         Arrays.sort(var1);
         Content var7 = this.getTableCaption(new RawHtml(var2));
         HtmlTree var8 = HtmlTree.TABLE(HtmlStyle.typeSummary, 0, 3, 0, var3, var7);
         var8.addContent(this.getSummaryTableHeader(var4, "col"));
         HtmlTree var9 = new HtmlTree(HtmlTag.TBODY);

         for(int var10 = 0; var10 < var1.length; ++var10) {
            if (this.isTypeInProfile(var1[var10], var6) && Util.isCoreClass(var1[var10]) && this.configuration.isGeneratedDoc(var1[var10])) {
               Content var11 = this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.PACKAGE, var1[var10]));
               HtmlTree var12 = HtmlTree.TD(HtmlStyle.colFirst, var11);
               HtmlTree var13 = HtmlTree.TR(var12);
               if (var10 % 2 == 0) {
                  var13.addStyle(HtmlStyle.altColor);
               } else {
                  var13.addStyle(HtmlStyle.rowColor);
               }

               HtmlTree var14 = new HtmlTree(HtmlTag.TD);
               var14.addStyle(HtmlStyle.colLast);
               if (Util.isDeprecated(var1[var10])) {
                  var14.addContent(this.deprecatedLabel);
                  if (var1[var10].tags("deprecated").length > 0) {
                     this.addSummaryDeprecatedComment(var1[var10], var1[var10].tags("deprecated")[0], var14);
                  }
               } else {
                  this.addSummaryComment(var1[var10], var14);
               }

               var13.addContent((Content)var14);
               var9.addContent((Content)var13);
            }
         }

         var8.addContent((Content)var9);
         var5.addContent((Content)var8);
      }

   }

   public void printHtmlDocument(String[] var1, boolean var2, Content var3) throws IOException {
      DocType var4 = DocType.TRANSITIONAL;
      Comment var5 = new Comment(this.configuration.getText("doclet.New_Page"));
      HtmlTree var6 = new HtmlTree(HtmlTag.HEAD);
      var6.addContent((Content)this.getGeneratedBy(!this.configuration.notimestamp));
      HtmlTree var7;
      if (this.configuration.charset.length() > 0) {
         var7 = HtmlTree.META("Content-Type", "text/html", this.configuration.charset);
         var6.addContent((Content)var7);
      }

      var6.addContent((Content)this.getTitle());
      HtmlTree var8;
      if (!this.configuration.notimestamp) {
         SimpleDateFormat var9 = new SimpleDateFormat("yyyy-MM-dd");
         var8 = HtmlTree.META("date", var9.format(new Date()));
         var6.addContent((Content)var8);
      }

      if (var1 != null) {
         for(int var10 = 0; var10 < var1.length; ++var10) {
            var8 = HtmlTree.META("keywords", var1[var10]);
            var6.addContent((Content)var8);
         }
      }

      var6.addContent((Content)this.getStyleSheetProperties());
      var6.addContent((Content)this.getScriptProperties());
      var7 = HtmlTree.HTML(this.configuration.getLocale().getLanguage(), var6, var3);
      HtmlDocument var11 = new HtmlDocument(var4, var5, var7);
      this.write(var11);
   }

   public String getWindowTitle(String var1) {
      if (this.configuration.windowtitle.length() > 0) {
         var1 = var1 + " (" + this.configuration.windowtitle + ")";
      }

      return var1;
   }

   public Content getUserHeaderFooter(boolean var1) {
      String var2;
      if (var1) {
         var2 = this.replaceDocRootDir(this.configuration.header);
      } else if (this.configuration.footer.length() != 0) {
         var2 = this.replaceDocRootDir(this.configuration.footer);
      } else {
         var2 = this.replaceDocRootDir(this.configuration.header);
      }

      RawHtml var3 = new RawHtml(var2);
      return var3;
   }

   public void addTop(Content var1) {
      RawHtml var2 = new RawHtml(this.replaceDocRootDir(this.configuration.top));
      var1.addContent((Content)var2);
   }

   public void addBottom(Content var1) {
      RawHtml var2 = new RawHtml(this.replaceDocRootDir(this.configuration.bottom));
      HtmlTree var3 = HtmlTree.SMALL(var2);
      HtmlTree var4 = HtmlTree.P(HtmlStyle.legalCopy, var3);
      var1.addContent((Content)var4);
   }

   protected void addNavLinks(boolean var1, Content var2) {
      if (!this.configuration.nonavbar) {
         String var3 = "allclasses_";
         HtmlTree var4 = new HtmlTree(HtmlTag.DIV);
         Content var5 = this.configuration.getResource("doclet.Skip_navigation_links");
         Content var6;
         HtmlTree var7;
         if (var1) {
            var2.addContent(HtmlConstants.START_OF_TOP_NAVBAR);
            var4.addStyle(HtmlStyle.topNav);
            var3 = var3 + "navbar_top";
            var6 = this.getMarkerAnchor(SectionName.NAVBAR_TOP);
            var4.addContent(var6);
            var7 = HtmlTree.DIV(HtmlStyle.skipNav, this.getHyperLink(this.getDocLink(SectionName.SKIP_NAVBAR_TOP), var5, var5.toString(), ""));
            var4.addContent((Content)var7);
         } else {
            var2.addContent(HtmlConstants.START_OF_BOTTOM_NAVBAR);
            var4.addStyle(HtmlStyle.bottomNav);
            var3 = var3 + "navbar_bottom";
            var6 = this.getMarkerAnchor(SectionName.NAVBAR_BOTTOM);
            var4.addContent(var6);
            var7 = HtmlTree.DIV(HtmlStyle.skipNav, this.getHyperLink(this.getDocLink(SectionName.SKIP_NAVBAR_BOTTOM), var5, var5.toString(), ""));
            var4.addContent((Content)var7);
         }

         if (var1) {
            var4.addContent(this.getMarkerAnchor(SectionName.NAVBAR_TOP_FIRSTROW));
         } else {
            var4.addContent(this.getMarkerAnchor(SectionName.NAVBAR_BOTTOM_FIRSTROW));
         }

         HtmlTree var12 = new HtmlTree(HtmlTag.UL);
         var12.addStyle(HtmlStyle.navList);
         var12.addAttr(HtmlAttr.TITLE, this.configuration.getText("doclet.Navigation"));
         if (this.configuration.createoverview) {
            var12.addContent(this.getNavLinkContents());
         }

         if (this.configuration.packages.length == 1) {
            var12.addContent(this.getNavLinkPackage(this.configuration.packages[0]));
         } else if (this.configuration.packages.length > 1) {
            var12.addContent(this.getNavLinkPackage());
         }

         var12.addContent(this.getNavLinkClass());
         if (this.configuration.classuse) {
            var12.addContent(this.getNavLinkClassUse());
         }

         if (this.configuration.createtree) {
            var12.addContent(this.getNavLinkTree());
         }

         if (!this.configuration.nodeprecated && !this.configuration.nodeprecatedlist) {
            var12.addContent(this.getNavLinkDeprecated());
         }

         if (this.configuration.createindex) {
            var12.addContent(this.getNavLinkIndex());
         }

         if (!this.configuration.nohelp) {
            var12.addContent(this.getNavLinkHelp());
         }

         var4.addContent((Content)var12);
         var7 = HtmlTree.DIV(HtmlStyle.aboutLanguage, this.getUserHeaderFooter(var1));
         var4.addContent((Content)var7);
         var2.addContent((Content)var4);
         HtmlTree var8 = HtmlTree.UL(HtmlStyle.navList, this.getNavLinkPrevious());
         var8.addContent(this.getNavLinkNext());
         HtmlTree var9 = HtmlTree.DIV(HtmlStyle.subNav, var8);
         HtmlTree var10 = HtmlTree.UL(HtmlStyle.navList, this.getNavShowLists());
         var10.addContent(this.getNavHideLists(this.filename));
         var9.addContent((Content)var10);
         HtmlTree var11 = HtmlTree.UL(HtmlStyle.navList, this.getNavLinkClassIndex());
         var11.addAttr(HtmlAttr.ID, var3.toString());
         var9.addContent((Content)var11);
         var9.addContent(this.getAllClassesLinkScript(var3.toString()));
         this.addSummaryDetailLinks(var9);
         if (var1) {
            var9.addContent(this.getMarkerAnchor(SectionName.SKIP_NAVBAR_TOP));
            var2.addContent((Content)var9);
            var2.addContent(HtmlConstants.END_OF_TOP_NAVBAR);
         } else {
            var9.addContent(this.getMarkerAnchor(SectionName.SKIP_NAVBAR_BOTTOM));
            var2.addContent((Content)var9);
            var2.addContent(HtmlConstants.END_OF_BOTTOM_NAVBAR);
         }
      }

   }

   protected Content getNavLinkNext() {
      return this.getNavLinkNext((DocPath)null);
   }

   protected Content getNavLinkPrevious() {
      return this.getNavLinkPrevious((DocPath)null);
   }

   protected void addSummaryDetailLinks(Content var1) {
   }

   protected Content getNavLinkContents() {
      Content var1 = this.getHyperLink(this.pathToRoot.resolve(DocPaths.OVERVIEW_SUMMARY), this.overviewLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkPackage(PackageDoc var1) {
      Content var2 = this.getPackageLink(var1, this.packageLabel);
      HtmlTree var3 = HtmlTree.LI(var2);
      return var3;
   }

   protected Content getNavLinkPackage() {
      HtmlTree var1 = HtmlTree.LI(this.packageLabel);
      return var1;
   }

   protected Content getNavLinkClassUse() {
      HtmlTree var1 = HtmlTree.LI(this.useLabel);
      return var1;
   }

   public Content getNavLinkPrevious(DocPath var1) {
      HtmlTree var2;
      if (var1 != null) {
         var2 = HtmlTree.LI(this.getHyperLink(var1, this.prevLabel, "", ""));
      } else {
         var2 = HtmlTree.LI(this.prevLabel);
      }

      return var2;
   }

   public Content getNavLinkNext(DocPath var1) {
      HtmlTree var2;
      if (var1 != null) {
         var2 = HtmlTree.LI(this.getHyperLink(var1, this.nextLabel, "", ""));
      } else {
         var2 = HtmlTree.LI(this.nextLabel);
      }

      return var2;
   }

   protected Content getNavShowLists(DocPath var1) {
      DocLink var2 = new DocLink(var1, this.path.getPath(), (String)null);
      Content var3 = this.getHyperLink(var2, this.framesLabel, "", "_top");
      HtmlTree var4 = HtmlTree.LI(var3);
      return var4;
   }

   protected Content getNavShowLists() {
      return this.getNavShowLists(this.pathToRoot.resolve(DocPaths.INDEX));
   }

   protected Content getNavHideLists(DocPath var1) {
      Content var2 = this.getHyperLink(var1, this.noframesLabel, "", "_top");
      HtmlTree var3 = HtmlTree.LI(var2);
      return var3;
   }

   protected Content getNavLinkTree() {
      PackageDoc[] var2 = this.configuration.root.specifiedPackages();
      Content var1;
      if (var2.length == 1 && this.configuration.root.specifiedClasses().length == 0) {
         var1 = this.getHyperLink(this.pathString(var2[0], DocPaths.PACKAGE_TREE), this.treeLabel, "", "");
      } else {
         var1 = this.getHyperLink(this.pathToRoot.resolve(DocPaths.OVERVIEW_TREE), this.treeLabel, "", "");
      }

      HtmlTree var3 = HtmlTree.LI(var1);
      return var3;
   }

   protected Content getNavLinkMainTree(String var1) {
      Content var2 = this.getHyperLink(this.pathToRoot.resolve(DocPaths.OVERVIEW_TREE), new StringContent(var1));
      HtmlTree var3 = HtmlTree.LI(var2);
      return var3;
   }

   protected Content getNavLinkClass() {
      HtmlTree var1 = HtmlTree.LI(this.classLabel);
      return var1;
   }

   protected Content getNavLinkDeprecated() {
      Content var1 = this.getHyperLink(this.pathToRoot.resolve(DocPaths.DEPRECATED_LIST), this.deprecatedLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClassIndex() {
      Content var1 = this.getHyperLink(this.pathToRoot.resolve(DocPaths.ALLCLASSES_NOFRAME), this.allclassesLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkIndex() {
      Content var1 = this.getHyperLink(this.pathToRoot.resolve(this.configuration.splitindex ? DocPaths.INDEX_FILES.resolve(DocPaths.indexN(1)) : DocPaths.INDEX_ALL), this.indexLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkHelp() {
      String var1 = this.configuration.helpfile;
      DocPath var2;
      if (var1.isEmpty()) {
         var2 = DocPaths.HELP_DOC;
      } else {
         DocFile var3 = DocFile.createFileForInput(this.configuration, var1);
         var2 = DocPath.create(var3.getName());
      }

      Content var5 = this.getHyperLink(this.pathToRoot.resolve(var2), this.helpLabel, "", "");
      HtmlTree var4 = HtmlTree.LI(var5);
      return var4;
   }

   public Content getSummaryTableHeader(String[] var1, String var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.TR);
      int var4 = var1.length;
      StringContent var5;
      if (var4 == 1) {
         var5 = new StringContent(var1[0]);
         var3.addContent((Content)HtmlTree.TH(HtmlStyle.colOne, var2, var5));
         return var3;
      } else {
         for(int var6 = 0; var6 < var4; ++var6) {
            var5 = new StringContent(var1[var6]);
            if (var6 == 0) {
               var3.addContent((Content)HtmlTree.TH(HtmlStyle.colFirst, var2, var5));
            } else if (var6 == var4 - 1) {
               var3.addContent((Content)HtmlTree.TH(HtmlStyle.colLast, var2, var5));
            } else {
               var3.addContent((Content)HtmlTree.TH(var2, var5));
            }
         }

         return var3;
      }
   }

   public Content getTableCaption(Content var1) {
      HtmlTree var2 = HtmlTree.SPAN(var1);
      Content var3 = this.getSpace();
      HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.tabEnd, var3);
      HtmlTree var5 = HtmlTree.CAPTION(var2);
      var5.addContent((Content)var4);
      return var5;
   }

   public Content getMarkerAnchor(String var1) {
      return this.getMarkerAnchor((String)this.getName(var1), (Content)null);
   }

   public Content getMarkerAnchor(SectionName var1) {
      return this.getMarkerAnchor((String)var1.getName(), (Content)null);
   }

   public Content getMarkerAnchor(SectionName var1, String var2) {
      return this.getMarkerAnchor((String)(var1.getName() + this.getName(var2)), (Content)null);
   }

   public Content getMarkerAnchor(String var1, Content var2) {
      if (var2 == null) {
         var2 = new Comment(" ");
      }

      HtmlTree var3 = HtmlTree.A_NAME(var1, (Content)var2);
      return var3;
   }

   public Content getPackageName(PackageDoc var1) {
      return var1 != null && var1.name().length() != 0 ? this.getPackageLabel(var1.name()) : this.defaultPackageLabel;
   }

   public Content getPackageLabel(String var1) {
      return new StringContent(var1);
   }

   protected void addPackageDeprecatedAPI(List var1, String var2, String var3, String[] var4, Content var5) {
      if (var1.size() > 0) {
         HtmlTree var6 = HtmlTree.TABLE(HtmlStyle.deprecatedSummary, 0, 3, 0, var3, this.getTableCaption(this.configuration.getResource(var2)));
         var6.addContent(this.getSummaryTableHeader(var4, "col"));
         HtmlTree var7 = new HtmlTree(HtmlTag.TBODY);

         for(int var8 = 0; var8 < var1.size(); ++var8) {
            PackageDoc var9 = (PackageDoc)var1.get(var8);
            HtmlTree var10 = HtmlTree.TD(HtmlStyle.colOne, this.getPackageLink(var9, this.getPackageName(var9)));
            if (var9.tags("deprecated").length > 0) {
               this.addInlineDeprecatedComment(var9, var9.tags("deprecated")[0], var10);
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

   protected DocPath pathString(ClassDoc var1, DocPath var2) {
      return this.pathString(var1.containingPackage(), var2);
   }

   protected DocPath pathString(PackageDoc var1, DocPath var2) {
      return this.pathToRoot.resolve(DocPath.forPackage(var1).resolve(var2));
   }

   public Content getPackageLink(PackageDoc var1, String var2) {
      return this.getPackageLink(var1, (Content)(new StringContent(var2)));
   }

   public Content getPackageLink(PackageDoc var1, Content var2) {
      boolean var3 = var1 != null && var1.isIncluded();
      if (!var3) {
         PackageDoc[] var4 = this.configuration.packages;

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var4[var5].equals(var1)) {
               var3 = true;
               break;
            }
         }
      }

      if (!var3 && var1 != null) {
         DocLink var6 = this.getCrossPackageLink(Util.getPackageName(var1));
         return var6 != null ? this.getHyperLink(var6, var2) : var2;
      } else {
         return this.getHyperLink(this.pathString(var1, DocPaths.PACKAGE_SUMMARY), var2);
      }
   }

   public Content italicsClassName(ClassDoc var1, boolean var2) {
      StringContent var3 = new StringContent(var2 ? var1.qualifiedName() : var1.name());
      return (Content)(var1.isInterface() ? HtmlTree.SPAN(HtmlStyle.interfaceName, var3) : var3);
   }

   public void addSrcLink(ProgramElementDoc var1, Content var2, Content var3) {
      if (var1 != null) {
         ClassDoc var4 = var1.containingClass();
         if (var4 == null) {
            var4 = (ClassDoc)var1;
         }

         DocPath var5 = this.pathToRoot.resolve(DocPaths.SOURCE_OUTPUT).resolve(DocPath.forClass(var4));
         Content var6 = this.getHyperLink(var5.fragment(SourceToHTMLConverter.getAnchorName(var1)), var2, "", "");
         var3.addContent(var6);
      }
   }

   public Content getLink(LinkInfoImpl var1) {
      LinkFactoryImpl var2 = new LinkFactoryImpl(this);
      return var2.getLink(var1);
   }

   public Content getTypeParameterLinks(LinkInfoImpl var1) {
      LinkFactoryImpl var2 = new LinkFactoryImpl(this);
      return var2.getTypeParameterLinks(var1, false);
   }

   public Content getCrossClassLink(String var1, String var2, Content var3, boolean var4, String var5, boolean var6) {
      String var7 = "";
      String var8 = var1 == null ? "" : var1;

      Object var10;
      do {
         int var9;
         if ((var9 = var8.lastIndexOf(46)) == -1) {
            return null;
         }

         var7 = var8.substring(var9 + 1, var8.length()) + (var7.length() > 0 ? "." + var7 : "");
         var10 = new StringContent(var7);
         if (var6) {
            var10 = HtmlTree.CODE((Content)var10);
         }

         var8 = var8.substring(0, var9);
      } while(this.getCrossPackageLink(var8) == null);

      DocLink var11 = this.configuration.extern.getExternalLink(var8, this.pathToRoot, var7 + ".html", var2);
      return this.getHyperLink(var11, (Content)(var3 != null && !var3.isEmpty() ? var3 : var10), var4, var5, this.configuration.getText("doclet.Href_Class_Or_Interface_Title", var8), "");
   }

   public boolean isClassLinkable(ClassDoc var1) {
      return var1.isIncluded() ? this.configuration.isGeneratedDoc(var1) : this.configuration.extern.isExternal(var1);
   }

   public DocLink getCrossPackageLink(String var1) {
      return this.configuration.extern.getExternalLink(var1, this.pathToRoot, DocPaths.PACKAGE_SUMMARY.getPath());
   }

   public Content getQualifiedClassLink(LinkInfoImpl.Kind var1, ClassDoc var2) {
      return this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(this.configuration.getClassName(var2)));
   }

   public void addPreQualifiedClassLink(LinkInfoImpl.Kind var1, ClassDoc var2, Content var3) {
      this.addPreQualifiedClassLink(var1, var2, false, var3);
   }

   public Content getPreQualifiedClassLink(LinkInfoImpl.Kind var1, ClassDoc var2, boolean var3) {
      ContentBuilder var4 = new ContentBuilder();
      PackageDoc var5 = var2.containingPackage();
      if (var5 != null && !this.configuration.shouldExcludeQualifier(var5.name())) {
         var4.addContent(this.getPkgName(var2));
      }

      var4.addContent(this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var2.name()).strong(var3)));
      return var4;
   }

   public void addPreQualifiedClassLink(LinkInfoImpl.Kind var1, ClassDoc var2, boolean var3, Content var4) {
      PackageDoc var5 = var2.containingPackage();
      if (var5 != null && !this.configuration.shouldExcludeQualifier(var5.name())) {
         var4.addContent(this.getPkgName(var2));
      }

      var4.addContent(this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var2.name()).strong(var3)));
   }

   public void addPreQualifiedStrongClassLink(LinkInfoImpl.Kind var1, ClassDoc var2, Content var3) {
      this.addPreQualifiedClassLink(var1, var2, true, var3);
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, MemberDoc var2, String var3) {
      return this.getDocLink(var1, var2.containingClass(), var2, new StringContent(var3));
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, MemberDoc var2, String var3, boolean var4) {
      return this.getDocLink(var1, var2.containingClass(), var2, var3, var4);
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, ClassDoc var2, MemberDoc var3, String var4, boolean var5) {
      return this.getDocLink(var1, var2, var3, var4, var5, false);
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, ClassDoc var2, MemberDoc var3, Content var4, boolean var5) {
      return this.getDocLink(var1, var2, var3, var4, var5, false);
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, ClassDoc var2, MemberDoc var3, String var4, boolean var5, boolean var6) {
      return this.getDocLink(var1, var2, var3, (Content)(new StringContent(this.check(var4))), var5, var6);
   }

   String check(String var1) {
      if (var1.matches(".*[&<>].*")) {
         throw new IllegalArgumentException(var1);
      } else {
         return var1;
      }
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, ClassDoc var2, MemberDoc var3, Content var4, boolean var5, boolean var6) {
      if (!var3.isIncluded() && !Util.isLinkable(var2, this.configuration)) {
         return var4;
      } else if (var3 instanceof ExecutableMemberDoc) {
         ExecutableMemberDoc var7 = (ExecutableMemberDoc)var3;
         return this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var4).where(this.getName(this.getAnchor(var7, var6))).strong(var5));
      } else {
         return var3 instanceof MemberDoc ? this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var4).where(this.getName(var3.name())).strong(var5)) : var4;
      }
   }

   public Content getDocLink(LinkInfoImpl.Kind var1, ClassDoc var2, MemberDoc var3, Content var4) {
      if (!var3.isIncluded() && !Util.isLinkable(var2, this.configuration)) {
         return var4;
      } else if (var3 instanceof ExecutableMemberDoc) {
         ExecutableMemberDoc var5 = (ExecutableMemberDoc)var3;
         return this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var4).where(this.getName(this.getAnchor(var5))));
      } else {
         return var3 instanceof MemberDoc ? this.getLink((new LinkInfoImpl(this.configuration, var1, var2)).label(var4).where(this.getName(var3.name()))) : var4;
      }
   }

   public String getAnchor(ExecutableMemberDoc var1) {
      return this.getAnchor(var1, false);
   }

   public String getAnchor(ExecutableMemberDoc var1, boolean var2) {
      if (var2) {
         return var1.name();
      } else {
         StringBuilder var3 = new StringBuilder(var1.signature());
         StringBuilder var4 = new StringBuilder();
         int var5 = 0;

         for(int var6 = 0; var6 < var3.length(); ++var6) {
            char var7 = var3.charAt(var6);
            if (var7 == '<') {
               ++var5;
            } else if (var7 == '>') {
               --var5;
            } else if (var5 == 0) {
               var4.append(var7);
            }
         }

         return var1.name() + var4.toString();
      }
   }

   public Content seeTagToContent(SeeTag var1) {
      String var2 = var1.name();
      if (!var2.startsWith("@link") && !var2.equals("@see")) {
         return new ContentBuilder();
      } else {
         String var3 = this.replaceDocRootDir(Util.normalizeNewlines(var1.text()));
         if (!var3.startsWith("<") && !var3.startsWith("\"")) {
            boolean var4 = var2.equalsIgnoreCase("@linkplain");
            Content var5 = this.plainOrCode(var4, new RawHtml(var1.label()));
            Content var6 = this.plainOrCode(var4, new RawHtml(var3));
            ClassDoc var7 = var1.referencedClass();
            String var8 = var1.referencedClassName();
            MemberDoc var9 = var1.referencedMember();
            String var10 = var1.referencedMemberName();
            if (var7 == null) {
               PackageDoc var14 = var1.referencedPackage();
               if (var14 != null && var14.isIncluded()) {
                  if (var5.isEmpty()) {
                     var5 = this.plainOrCode(var4, new StringContent(var14.name()));
                  }

                  return this.getPackageLink(var14, var5);
               } else {
                  DocLink var13 = this.getCrossPackageLink(var8);
                  if (var13 != null) {
                     return this.getHyperLink(var13, var5.isEmpty() ? var6 : var5);
                  } else {
                     Content var12;
                     if ((var12 = this.getCrossClassLink(var8, var10, var5, false, "", !var4)) != null) {
                        return var12;
                     } else {
                        this.configuration.getDocletSpecificMsg().warning(var1.position(), "doclet.see.class_or_package_not_found", var2, var3);
                        return var5.isEmpty() ? var6 : var5;
                     }
                  }
               }
            } else if (var10 == null) {
               if (var5.isEmpty()) {
                  var5 = this.plainOrCode(var4, new StringContent(var7.name()));
               }

               return this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.DEFAULT, var7)).label(var5));
            } else if (var9 == null) {
               return var5.isEmpty() ? var6 : var5;
            } else {
               ClassDoc var11 = var9.containingClass();
               if (var1.text().trim().startsWith("#") && !var11.isPublic() && !Util.isLinkable(var11, this.configuration)) {
                  if (this instanceof ClassWriterImpl) {
                     var11 = ((ClassWriterImpl)this).getClassDoc();
                  } else if (!var11.isPublic()) {
                     this.configuration.getDocletSpecificMsg().warning(var1.position(), "doclet.see.class_or_package_not_accessible", var2, var11.qualifiedName());
                  } else {
                     this.configuration.getDocletSpecificMsg().warning(var1.position(), "doclet.see.class_or_package_not_found", var2, var3);
                  }
               }

               if (this.configuration.currentcd != var11) {
                  var10 = var11.name() + "." + var10;
               }

               if (var9 instanceof ExecutableMemberDoc && var10.indexOf(40) < 0) {
                  var10 = var10 + ((ExecutableMemberDoc)var9).signature();
               }

               var6 = this.plainOrCode(var4, new StringContent(var10));
               return this.getDocLink(LinkInfoImpl.Kind.SEE_TAG, var11, var9, var5.isEmpty() ? var6 : var5, false);
            }
         } else {
            return new RawHtml(var3);
         }
      }
   }

   private Content plainOrCode(boolean var1, Content var2) {
      return (Content)(!var1 && !var2.isEmpty() ? HtmlTree.CODE(var2) : var2);
   }

   public void addInlineComment(Doc var1, Tag var2, Content var3) {
      this.addCommentTags(var1, var2, var2.inlineTags(), false, false, var3);
   }

   public void addInlineDeprecatedComment(Doc var1, Tag var2, Content var3) {
      this.addCommentTags(var1, var2.inlineTags(), true, false, var3);
   }

   public void addSummaryComment(Doc var1, Content var2) {
      this.addSummaryComment(var1, var1.firstSentenceTags(), var2);
   }

   public void addSummaryComment(Doc var1, Tag[] var2, Content var3) {
      this.addCommentTags(var1, var2, false, true, var3);
   }

   public void addSummaryDeprecatedComment(Doc var1, Tag var2, Content var3) {
      this.addCommentTags(var1, var2.firstSentenceTags(), true, true, var3);
   }

   public void addInlineComment(Doc var1, Content var2) {
      this.addCommentTags(var1, var1.inlineTags(), false, false, var2);
   }

   private void addCommentTags(Doc var1, Tag[] var2, boolean var3, boolean var4, Content var5) {
      this.addCommentTags(var1, (Tag)null, var2, var3, var4, var5);
   }

   private void addCommentTags(Doc var1, Tag var2, Tag[] var3, boolean var4, boolean var5, Content var6) {
      if (!this.configuration.nocomment) {
         Content var8 = this.commentTagsToContent((Tag)null, var1, var3, var5);
         HtmlTree var7;
         if (var4) {
            HtmlTree var9 = HtmlTree.SPAN(HtmlStyle.deprecationComment, var8);
            var7 = HtmlTree.DIV(HtmlStyle.block, var9);
            var6.addContent((Content)var7);
         } else {
            var7 = HtmlTree.DIV(HtmlStyle.block, var8);
            var6.addContent((Content)var7);
         }

         if (var3.length == 0) {
            var6.addContent(this.getSpace());
         }

      }
   }

   public Content commentTagsToContent(Tag var1, Doc var2, Tag[] var3, boolean var4) {
      ContentBuilder var5 = new ContentBuilder();
      boolean var6 = false;
      this.configuration.tagletManager.checkTags(var2, var3, true);

      for(int var7 = 0; var7 < var3.length; ++var7) {
         Tag var8 = var3[var7];
         String var9 = var8.name();
         if (var8 instanceof SeeTag) {
            var5.addContent(this.seeTagToContent((SeeTag)var8));
         } else if (!var9.equals("Text")) {
            boolean var10 = var5.isEmpty();
            Object var11;
            if (this.configuration.docrootparent.length() > 0 && var8.name().equals("@docRoot") && var3[var7 + 1].text().startsWith("/..")) {
               var6 = true;
               var11 = new StringContent(this.configuration.docrootparent);
            } else {
               var11 = TagletWriter.getInlineTagOuput(this.configuration.tagletManager, var1, var8, this.getTagletWriterInstance(var4));
            }

            if (var11 != null) {
               var5.addContent((Content)var11);
            }

            if (var10 && var4 && var8.name().equals("@inheritDoc") && !var5.isEmpty()) {
               break;
            }
         } else {
            String var12 = var8.text();
            if (var6) {
               var12 = var12.replaceFirst("/..", "");
               var6 = false;
            }

            var12 = this.redirectRelativeLinks(var8.holder(), var12);
            var12 = this.replaceDocRootDir(var12);
            if (var4) {
               var12 = removeNonInlineHtmlTags(var12);
            }

            var12 = Util.replaceTabs(this.configuration, var12);
            var12 = Util.normalizeNewlines(var12);
            var5.addContent((Content)(new RawHtml(var12)));
         }
      }

      return var5;
   }

   private boolean shouldNotRedirectRelativeLinks() {
      return this instanceof AnnotationTypeWriter || this instanceof ClassWriter || this instanceof PackageSummaryWriter;
   }

   private String redirectRelativeLinks(Doc var1, String var2) {
      if (var1 != null && !this.shouldNotRedirectRelativeLinks()) {
         DocPath var3;
         if (var1 instanceof ClassDoc) {
            var3 = DocPath.forPackage(((ClassDoc)var1).containingPackage());
         } else if (var1 instanceof MemberDoc) {
            var3 = DocPath.forPackage(((MemberDoc)var1).containingPackage());
         } else {
            if (!(var1 instanceof PackageDoc)) {
               return var2;
            }

            var3 = DocPath.forPackage((PackageDoc)var1);
         }

         int var5 = StringUtils.indexOfIgnoreCase(var2, "<a");
         if (var5 < 0) {
            return var2;
         } else {
            StringBuilder var6 = new StringBuilder(var2);

            while(var5 >= 0) {
               if (var6.length() > var5 + 2 && !Character.isWhitespace(var6.charAt(var5 + 2))) {
                  var5 = StringUtils.indexOfIgnoreCase(var6.toString(), "<a", var5 + 1);
               } else {
                  var5 = var6.indexOf("=", var5) + 1;
                  int var4 = var6.indexOf(">", var5 + 1);
                  if (var5 == 0) {
                     this.configuration.root.printWarning(var1.position(), this.configuration.getText("doclet.malformed_html_link_tag", var2));
                     break;
                  }

                  if (var4 == -1) {
                     break;
                  }

                  if (var6.substring(var5, var4).indexOf("\"") != -1) {
                     var5 = var6.indexOf("\"", var5) + 1;
                     var4 = var6.indexOf("\"", var5 + 1);
                     if (var5 == 0 || var4 == -1) {
                        break;
                     }
                  }

                  String var7 = var6.substring(var5, var4);
                  String var8 = StringUtils.toLowerCase(var7);
                  if (!var8.startsWith("mailto:") && !var8.startsWith("http:") && !var8.startsWith("https:") && !var8.startsWith("file:")) {
                     var7 = "{@" + (new DocRootTaglet()).getName() + "}/" + var3.resolve(var7).getPath();
                     var6.replace(var5, var4, var7);
                  }

                  var5 = StringUtils.indexOfIgnoreCase(var6.toString(), "<a", var5 + 1);
               }
            }

            return var6.toString();
         }
      } else {
         return var2;
      }
   }

   public static String removeNonInlineHtmlTags(String var0) {
      int var1 = var0.length();
      int var2 = 0;
      int var3 = var0.indexOf(60);
      if (var3 < 0) {
         return var0;
      } else {
         StringBuilder var4;
         int var5;
         label44:
         for(var4 = new StringBuilder(); var3 != -1; var3 = var0.indexOf(60, var5)) {
            var5 = var3 + 1;
            if (var5 == var1) {
               break;
            }

            char var6 = var0.charAt(var5);
            if (var6 == '/') {
               ++var5;
               if (var5 == var1) {
                  break;
               }

               var6 = var0.charAt(var5);
            }

            while(isHtmlTagLetterOrDigit(var6)) {
               ++var5;
               if (var5 == var1) {
                  break label44;
               }

               var6 = var0.charAt(var5);
            }

            if (var6 == '>' && blockTags.contains(StringUtils.toLowerCase(var0.substring(var5, var5)))) {
               var4.append(var0, var2, var3);
               var2 = var5 + 1;
            }
         }

         var4.append(var0.substring(var2));
         return var4.toString();
      }
   }

   private static boolean isHtmlTagLetterOrDigit(char var0) {
      return 'a' <= var0 && var0 <= 'z' || 'A' <= var0 && var0 <= 'Z' || '1' <= var0 && var0 <= '6';
   }

   public HtmlTree getStyleSheetProperties() {
      String var1 = this.configuration.stylesheetfile;
      DocPath var2;
      if (var1.isEmpty()) {
         var2 = DocPaths.STYLESHEET;
      } else {
         DocFile var3 = DocFile.createFileForInput(this.configuration, var1);
         var2 = DocPath.create(var3.getName());
      }

      HtmlTree var4 = HtmlTree.LINK("stylesheet", "text/css", this.pathToRoot.resolve(var2).getPath(), "Style");
      return var4;
   }

   public HtmlTree getScriptProperties() {
      HtmlTree var1 = HtmlTree.SCRIPT("text/javascript", this.pathToRoot.resolve(DocPaths.JAVASCRIPT).getPath());
      return var1;
   }

   public boolean isCoreClass(ClassDoc var1) {
      return var1.containingClass() == null || var1.isStatic();
   }

   public void addAnnotationInfo(PackageDoc var1, Content var2) {
      this.addAnnotationInfo(var1, var1.annotations(), var2);
   }

   public void addReceiverAnnotationInfo(ExecutableMemberDoc var1, AnnotationDesc[] var2, Content var3) {
      this.addAnnotationInfo(0, var1, var2, false, var3);
   }

   public void addAnnotationInfo(ProgramElementDoc var1, Content var2) {
      this.addAnnotationInfo(var1, var1.annotations(), var2);
   }

   public boolean addAnnotationInfo(int var1, Doc var2, Parameter var3, Content var4) {
      return this.addAnnotationInfo(var1, var2, var3.annotations(), false, var4);
   }

   private void addAnnotationInfo(Doc var1, AnnotationDesc[] var2, Content var3) {
      this.addAnnotationInfo(0, var1, var2, true, var3);
   }

   private boolean addAnnotationInfo(int var1, Doc var2, AnnotationDesc[] var3, boolean var4, Content var5) {
      List var6 = this.getAnnotations(var1, var3, var4);
      String var7 = "";
      if (var6.isEmpty()) {
         return false;
      } else {
         for(Iterator var8 = var6.iterator(); var8.hasNext(); var7 = " ") {
            Content var9 = (Content)var8.next();
            var5.addContent(var7);
            var5.addContent(var9);
         }

         return true;
      }
   }

   private List getAnnotations(int var1, AnnotationDesc[] var2, boolean var3) {
      return this.getAnnotations(var1, var2, var3, true);
   }

   public List getAnnotations(int var1, AnnotationDesc[] var2, boolean var3, boolean var4) {
      ArrayList var5 = new ArrayList();

      for(int var7 = 0; var7 < var2.length; ++var7) {
         AnnotationTypeDoc var8 = var2[var7].annotationType();
         if (Util.isDocumentedAnnotation(var8) || this.isAnnotationDocumented || this.isContainerDocumented) {
            ContentBuilder var6 = new ContentBuilder();
            this.isAnnotationDocumented = false;
            LinkInfoImpl var9 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.ANNOTATION, var8);
            AnnotationDesc.ElementValuePair[] var10 = var2[var7].elementValues();
            if (var2[var7].isSynthesized()) {
               for(int var11 = 0; var11 < var10.length; ++var11) {
                  AnnotationValue var12 = var10[var11].value();
                  ArrayList var13 = new ArrayList();
                  if (var12.value() instanceof AnnotationValue[]) {
                     AnnotationValue[] var14 = (AnnotationValue[])((AnnotationValue[])var12.value());
                     var13.addAll(Arrays.asList(var14));
                  } else {
                     var13.add(var12);
                  }

                  String var20 = "";

                  for(Iterator var15 = var13.iterator(); var15.hasNext(); var20 = " ") {
                     AnnotationValue var16 = (AnnotationValue)var15.next();
                     var6.addContent(var20);
                     var6.addContent(this.annotationValueToContent(var16));
                  }
               }
            } else if (this.isAnnotationArray(var10)) {
               if (var10.length == 1 && this.isAnnotationDocumented) {
                  AnnotationValue[] var17 = (AnnotationValue[])((AnnotationValue[])var10[0].value().value());
                  ArrayList var18 = new ArrayList();
                  var18.addAll(Arrays.asList(var17));
                  String var19 = "";

                  for(Iterator var21 = var18.iterator(); var21.hasNext(); var19 = " ") {
                     AnnotationValue var22 = (AnnotationValue)var21.next();
                     var6.addContent(var19);
                     var6.addContent(this.annotationValueToContent(var22));
                  }
               } else {
                  this.addAnnotations(var8, var9, var6, var10, var1, false);
               }
            } else {
               this.addAnnotations(var8, var9, var6, var10, var1, var3);
            }

            var6.addContent(var3 ? DocletConstants.NL : "");
            var5.add(var6);
         }
      }

      return var5;
   }

   private void addAnnotations(AnnotationTypeDoc var1, LinkInfoImpl var2, ContentBuilder var3, AnnotationDesc.ElementValuePair[] var4, int var5, boolean var6) {
      var2.label = new StringContent("@" + var1.name());
      var3.addContent(this.getLink(var2));
      if (var4.length > 0) {
         var3.addContent("(");

         for(int var7 = 0; var7 < var4.length; ++var7) {
            if (var7 > 0) {
               var3.addContent(",");
               if (var6) {
                  var3.addContent(DocletConstants.NL);
                  int var8 = var1.name().length() + 2;

                  for(int var9 = 0; var9 < var8 + var5; ++var9) {
                     var3.addContent(" ");
                  }
               }
            }

            var3.addContent(this.getDocLink(LinkInfoImpl.Kind.ANNOTATION, var4[var7].element(), var4[var7].element().name(), false));
            var3.addContent("=");
            AnnotationValue var13 = var4[var7].value();
            ArrayList var14 = new ArrayList();
            if (var13.value() instanceof AnnotationValue[]) {
               AnnotationValue[] var10 = (AnnotationValue[])((AnnotationValue[])var13.value());
               var14.addAll(Arrays.asList(var10));
            } else {
               var14.add(var13);
            }

            var3.addContent(var14.size() == 1 ? "" : "{");
            String var15 = "";

            for(Iterator var11 = var14.iterator(); var11.hasNext(); var15 = ",") {
               AnnotationValue var12 = (AnnotationValue)var11.next();
               var3.addContent(var15);
               var3.addContent(this.annotationValueToContent(var12));
            }

            var3.addContent(var14.size() == 1 ? "" : "}");
            this.isContainerDocumented = false;
         }

         var3.addContent(")");
      }

   }

   private boolean isAnnotationArray(AnnotationDesc.ElementValuePair[] var1) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         AnnotationValue var2 = var1[var3].value();
         if (var2.value() instanceof AnnotationValue[]) {
            AnnotationValue[] var4 = (AnnotationValue[])((AnnotationValue[])var2.value());
            if (var4.length > 1 && var4[0].value() instanceof AnnotationDesc) {
               AnnotationTypeDoc var5 = ((AnnotationDesc)var4[0].value()).annotationType();
               this.isContainerDocumented = true;
               if (Util.isDocumentedAnnotation(var5)) {
                  this.isAnnotationDocumented = true;
               }

               return true;
            }
         }
      }

      return false;
   }

   private Content annotationValueToContent(AnnotationValue var1) {
      if (var1.value() instanceof Type) {
         Type var6 = (Type)var1.value();
         if (var6.asClassDoc() != null) {
            LinkInfoImpl var7 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.ANNOTATION, var6);
            var7.label = new StringContent((var6.asClassDoc().isIncluded() ? var6.typeName() : var6.qualifiedTypeName()) + var6.dimension() + ".class");
            return this.getLink(var7);
         } else {
            return new StringContent(var6.typeName() + var6.dimension() + ".class");
         }
      } else if (!(var1.value() instanceof AnnotationDesc)) {
         return (Content)(var1.value() instanceof MemberDoc ? this.getDocLink(LinkInfoImpl.Kind.ANNOTATION, (MemberDoc)var1.value(), ((MemberDoc)var1.value()).name(), false) : new StringContent(var1.toString()));
      } else {
         List var2 = this.getAnnotations(0, new AnnotationDesc[]{(AnnotationDesc)var1.value()}, false);
         ContentBuilder var3 = new ContentBuilder();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Content var5 = (Content)var4.next();
            var3.addContent(var5);
         }

         return var3;
      }
   }

   public Configuration configuration() {
      return this.configuration;
   }

   static {
      HtmlTag[] var0 = HtmlTag.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         HtmlTag var3 = var0[var2];
         if (var3.blockType == HtmlTag.BlockType.BLOCK) {
            blockTags.add(var3.value);
         }
      }

   }
}
