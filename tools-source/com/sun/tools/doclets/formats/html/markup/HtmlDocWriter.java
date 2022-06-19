package com.sun.tools.doclets.formats.html.markup;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.SectionName;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocLink;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public abstract class HtmlDocWriter extends HtmlWriter {
   public static final String CONTENT_TYPE = "text/html";

   public HtmlDocWriter(Configuration var1, DocPath var2) throws IOException {
      super(var1, var2);
      var1.message.notice("doclet.Generating_0", DocFile.createFileForOutput(var1, var2).getPath());
   }

   public abstract Configuration configuration();

   public Content getHyperLink(DocPath var1, String var2) {
      return this.getHyperLink((DocPath)var1, new StringContent(var2), false, "", "", "");
   }

   public Content getHyperLink(String var1, Content var2) {
      return this.getHyperLink(this.getDocLink(var1), var2, "", "");
   }

   public Content getHyperLink(SectionName var1, Content var2) {
      return this.getHyperLink(this.getDocLink(var1), var2, "", "");
   }

   public Content getHyperLink(SectionName var1, String var2, Content var3) {
      return this.getHyperLink(this.getDocLink(var1, var2), var3, "", "");
   }

   public DocLink getDocLink(String var1) {
      return DocLink.fragment(this.getName(var1));
   }

   public DocLink getDocLink(SectionName var1) {
      return DocLink.fragment(var1.getName());
   }

   public DocLink getDocLink(SectionName var1, String var2) {
      return DocLink.fragment(var1.getName() + this.getName(var2));
   }

   public String getName(String var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         char var3 = var1.charAt(var4);
         switch (var3) {
            case ' ':
            case '[':
               break;
            case '$':
               if (var4 == 0) {
                  var2.append("Z:Z");
               }

               var2.append(":D");
               break;
            case '(':
            case ')':
            case ',':
            case '<':
            case '>':
               var2.append('-');
               break;
            case ']':
               var2.append(":A");
               break;
            case '_':
               if (var4 == 0) {
                  var2.append("Z:Z");
               }

               var2.append(var3);
               break;
            default:
               var2.append(var3);
         }
      }

      return var2.toString();
   }

   public Content getHyperLink(DocPath var1, Content var2) {
      return this.getHyperLink(var1, var2, "", "");
   }

   public Content getHyperLink(DocLink var1, Content var2) {
      return this.getHyperLink(var1, var2, "", "");
   }

   public Content getHyperLink(DocPath var1, Content var2, boolean var3, String var4, String var5, String var6) {
      return this.getHyperLink(new DocLink(var1), var2, var3, var4, var5, var6);
   }

   public Content getHyperLink(DocLink var1, Content var2, boolean var3, String var4, String var5, String var6) {
      Object var7 = var2;
      if (var3) {
         var7 = HtmlTree.SPAN(HtmlStyle.typeNameLink, var2);
      }

      HtmlTree var8;
      if (var4 != null && var4.length() != 0) {
         var8 = new HtmlTree(HtmlTag.FONT, new Content[]{(Content)var7});
         var8.addAttr(HtmlAttr.CLASS, var4);
         var7 = var8;
      }

      var8 = HtmlTree.A(var1.toString(), (Content)var7);
      if (var5 != null && var5.length() != 0) {
         var8.addAttr(HtmlAttr.TITLE, var5);
      }

      if (var6 != null && var6.length() != 0) {
         var8.addAttr(HtmlAttr.TARGET, var6);
      }

      return var8;
   }

   public Content getHyperLink(DocPath var1, Content var2, String var3, String var4) {
      return this.getHyperLink(new DocLink(var1), var2, var3, var4);
   }

   public Content getHyperLink(DocLink var1, Content var2, String var3, String var4) {
      HtmlTree var5 = HtmlTree.A(var1.toString(), var2);
      if (var3 != null && var3.length() != 0) {
         var5.addAttr(HtmlAttr.TITLE, var3);
      }

      if (var4 != null && var4.length() != 0) {
         var5.addAttr(HtmlAttr.TARGET, var4);
      }

      return var5;
   }

   public String getPkgName(ClassDoc var1) {
      String var2 = var1.containingPackage().name();
      if (var2.length() > 0) {
         var2 = var2 + ".";
         return var2;
      } else {
         return "";
      }
   }

   public boolean getMemberDetailsListPrinted() {
      return this.memberDetailsListPrinted;
   }

   public void printFramesetDocument(String var1, boolean var2, Content var3) throws IOException {
      DocType var4 = DocType.FRAMESET;
      Comment var5 = new Comment(this.configuration.getText("doclet.New_Page"));
      HtmlTree var6 = new HtmlTree(HtmlTag.HEAD);
      var6.addContent((Content)this.getGeneratedBy(!var2));
      HtmlTree var7;
      if (this.configuration.charset.length() > 0) {
         var7 = HtmlTree.META("Content-Type", "text/html", this.configuration.charset);
         var6.addContent((Content)var7);
      }

      var7 = HtmlTree.TITLE(new StringContent(var1));
      var6.addContent((Content)var7);
      var6.addContent(this.getFramesetJavaScript());
      HtmlTree var8 = HtmlTree.HTML(this.configuration.getLocale().getLanguage(), var6, var3);
      HtmlDocument var9 = new HtmlDocument(var4, var5, var8);
      this.write(var9);
   }

   protected Comment getGeneratedBy(boolean var1) {
      String var2 = "Generated by javadoc";
      if (var1) {
         GregorianCalendar var3 = new GregorianCalendar(TimeZone.getDefault());
         Date var4 = var3.getTime();
         var2 = var2 + " (" + this.configuration.getDocletSpecificBuildDate() + ") on " + var4;
      }

      return new Comment(var2);
   }
}
