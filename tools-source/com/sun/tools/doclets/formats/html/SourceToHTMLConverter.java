package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.formats.html.markup.DocType;
import com.sun.tools.doclets.formats.html.markup.HtmlDocument;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javadoc.SourcePositionImpl;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import javax.tools.FileObject;

public class SourceToHTMLConverter {
   private static final int NUM_BLANK_LINES = 60;
   private static final String NEW_LINE;
   private final ConfigurationImpl configuration;
   private final RootDoc rootDoc;
   private DocPath outputdir;
   private DocPath relativePath;

   private SourceToHTMLConverter(ConfigurationImpl var1, RootDoc var2, DocPath var3) {
      this.relativePath = DocPath.empty;
      this.configuration = var1;
      this.rootDoc = var2;
      this.outputdir = var3;
   }

   public static void convertRoot(ConfigurationImpl var0, RootDoc var1, DocPath var2) {
      (new SourceToHTMLConverter(var0, var1, var2)).generate();
   }

   void generate() {
      if (this.rootDoc != null && this.outputdir != null) {
         PackageDoc[] var1 = this.rootDoc.specifiedPackages();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (!this.configuration.nodeprecated || !Util.isDeprecated(var1[var2])) {
               this.convertPackage(var1[var2], this.outputdir);
            }
         }

         ClassDoc[] var4 = this.rootDoc.specifiedClasses();

         for(int var3 = 0; var3 < var4.length; ++var3) {
            if (!this.configuration.nodeprecated || !Util.isDeprecated(var4[var3]) && !Util.isDeprecated(var4[var3].containingPackage())) {
               this.convertClass(var4[var3], this.outputdir);
            }
         }

      }
   }

   public void convertPackage(PackageDoc var1, DocPath var2) {
      if (var1 != null) {
         ClassDoc[] var3 = var1.allClasses();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (!this.configuration.nodeprecated || !Util.isDeprecated(var3[var4])) {
               this.convertClass(var3[var4], var2);
            }
         }

      }
   }

   public void convertClass(ClassDoc var1, DocPath var2) {
      if (var1 != null) {
         try {
            SourcePosition var3 = var1.position();
            if (var3 == null) {
               return;
            }

            Object var4;
            if (var3 instanceof SourcePositionImpl) {
               FileObject var5 = ((SourcePositionImpl)var3).fileObject();
               if (var5 == null) {
                  return;
               }

               var4 = var5.openReader(true);
            } else {
               File var15 = var3.file();
               if (var15 == null) {
                  return;
               }

               var4 = new FileReader(var15);
            }

            LineNumberReader var16 = new LineNumberReader((Reader)var4);
            int var6 = 1;
            this.relativePath = DocPaths.SOURCE_OUTPUT.resolve(DocPath.forPackage(var1)).invert();
            Content var8 = getHeader();
            HtmlTree var9 = new HtmlTree(HtmlTag.PRE);

            String var7;
            try {
               while((var7 = var16.readLine()) != null) {
                  addLineNo(var9, var6);
                  this.addLine(var9, var7, var6);
                  ++var6;
               }
            } finally {
               var16.close();
            }

            addBlankLines(var9);
            HtmlTree var10 = HtmlTree.DIV(HtmlStyle.sourceContainer, var9);
            var8.addContent((Content)var10);
            this.writeToFile(var8, var2.resolve(DocPath.forClass(var1)));
         } catch (IOException var14) {
            var14.printStackTrace();
         }

      }
   }

   private void writeToFile(Content var1, DocPath var2) throws IOException {
      DocType var3 = DocType.TRANSITIONAL;
      HtmlTree var4 = new HtmlTree(HtmlTag.HEAD);
      var4.addContent((Content)HtmlTree.TITLE(new StringContent(this.configuration.getText("doclet.Window_Source_title"))));
      var4.addContent((Content)this.getStyleSheetProperties());
      HtmlTree var5 = HtmlTree.HTML(this.configuration.getLocale().getLanguage(), var4, var1);
      HtmlDocument var6 = new HtmlDocument(var3, var5);
      this.configuration.message.notice("doclet.Generating_0", var2.getPath());
      DocFile var7 = DocFile.createFileForOutput(this.configuration, var2);
      Writer var8 = var7.openWriter();

      try {
         var6.write(var8, true);
      } finally {
         var8.close();
      }

   }

   public HtmlTree getStyleSheetProperties() {
      String var1 = this.configuration.stylesheetfile;
      DocPath var2;
      if (var1.length() > 0) {
         DocFile var3 = DocFile.createFileForInput(this.configuration, var1);
         var2 = DocPath.create(var3.getName());
      } else {
         var2 = DocPaths.STYLESHEET;
      }

      DocPath var5 = this.relativePath.resolve(var2);
      HtmlTree var4 = HtmlTree.LINK("stylesheet", "text/css", var5.getPath(), "Style");
      return var4;
   }

   private static Content getHeader() {
      return new HtmlTree(HtmlTag.BODY);
   }

   private static void addLineNo(Content var0, int var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.SPAN);
      var2.addStyle(HtmlStyle.sourceLineNo);
      if (var1 < 10) {
         var2.addContent("00" + Integer.toString(var1));
      } else if (var1 < 100) {
         var2.addContent("0" + Integer.toString(var1));
      } else {
         var2.addContent(Integer.toString(var1));
      }

      var0.addContent((Content)var2);
   }

   private void addLine(Content var1, String var2, int var3) {
      if (var2 != null) {
         var1.addContent(Util.replaceTabs(this.configuration, var2));
         HtmlTree var4 = HtmlTree.A_NAME("line." + Integer.toString(var3));
         var1.addContent((Content)var4);
         var1.addContent(NEW_LINE);
      }

   }

   private static void addBlankLines(Content var0) {
      for(int var1 = 0; var1 < 60; ++var1) {
         var0.addContent(NEW_LINE);
      }

   }

   public static String getAnchorName(Doc var0) {
      return "line." + var0.position().line();
   }

   static {
      NEW_LINE = DocletConstants.NL;
   }
}
