package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.ProfilePackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;

public class ProfilePackageSummaryBuilder extends AbstractBuilder {
   public static final String ROOT = "PackageDoc";
   private final PackageDoc packageDoc;
   private final String profileName;
   private final int profileValue;
   private final ProfilePackageSummaryWriter profilePackageWriter;
   private Content contentTree;

   private ProfilePackageSummaryBuilder(AbstractBuilder.Context var1, PackageDoc var2, ProfilePackageSummaryWriter var3, Profile var4) {
      super(var1);
      this.packageDoc = var2;
      this.profilePackageWriter = var3;
      this.profileName = var4.name;
      this.profileValue = var4.value;
   }

   public static ProfilePackageSummaryBuilder getInstance(AbstractBuilder.Context var0, PackageDoc var1, ProfilePackageSummaryWriter var2, Profile var3) {
      return new ProfilePackageSummaryBuilder(var0, var1, var2, var3);
   }

   public void build() throws IOException {
      if (this.profilePackageWriter != null) {
         this.build(this.layoutParser.parseXML("PackageDoc"), this.contentTree);
      }
   }

   public String getName() {
      return "PackageDoc";
   }

   public void buildPackageDoc(XMLNode var1, Content var2) throws Exception {
      var2 = this.profilePackageWriter.getPackageHeader(Util.getPackageName(this.packageDoc));
      this.buildChildren(var1, var2);
      this.profilePackageWriter.addPackageFooter(var2);
      this.profilePackageWriter.printDocument(var2);
      this.profilePackageWriter.close();
      Util.copyDocFiles(this.configuration, this.packageDoc);
   }

   public void buildContent(XMLNode var1, Content var2) {
      Content var3 = this.profilePackageWriter.getContentHeader();
      this.buildChildren(var1, var3);
      var2.addContent(var3);
   }

   public void buildSummary(XMLNode var1, Content var2) {
      Content var3 = this.profilePackageWriter.getSummaryHeader();
      this.buildChildren(var1, var3);
      var2.addContent(var3);
   }

   public void buildInterfaceSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Interface_Summary"), this.configuration.getText("doclet.interfaces"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Interface"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.packageDoc.isIncluded() ? this.packageDoc.interfaces() : this.configuration.classDocCatalog.interfaces(Util.getPackageName(this.packageDoc));
      if (var5.length > 0) {
         this.profilePackageWriter.addClassesSummary(var5, this.configuration.getText("doclet.Interface_Summary"), var3, var4, var2);
      }

   }

   public void buildClassSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Class_Summary"), this.configuration.getText("doclet.classes"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Class"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.packageDoc.isIncluded() ? this.packageDoc.ordinaryClasses() : this.configuration.classDocCatalog.ordinaryClasses(Util.getPackageName(this.packageDoc));
      if (var5.length > 0) {
         this.profilePackageWriter.addClassesSummary(var5, this.configuration.getText("doclet.Class_Summary"), var3, var4, var2);
      }

   }

   public void buildEnumSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Enum_Summary"), this.configuration.getText("doclet.enums"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Enum"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.packageDoc.isIncluded() ? this.packageDoc.enums() : this.configuration.classDocCatalog.enums(Util.getPackageName(this.packageDoc));
      if (var5.length > 0) {
         this.profilePackageWriter.addClassesSummary(var5, this.configuration.getText("doclet.Enum_Summary"), var3, var4, var2);
      }

   }

   public void buildExceptionSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Exception_Summary"), this.configuration.getText("doclet.exceptions"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Exception"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.packageDoc.isIncluded() ? this.packageDoc.exceptions() : this.configuration.classDocCatalog.exceptions(Util.getPackageName(this.packageDoc));
      if (var5.length > 0) {
         this.profilePackageWriter.addClassesSummary(var5, this.configuration.getText("doclet.Exception_Summary"), var3, var4, var2);
      }

   }

   public void buildErrorSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Error_Summary"), this.configuration.getText("doclet.errors"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Error"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.packageDoc.isIncluded() ? this.packageDoc.errors() : this.configuration.classDocCatalog.errors(Util.getPackageName(this.packageDoc));
      if (var5.length > 0) {
         this.profilePackageWriter.addClassesSummary(var5, this.configuration.getText("doclet.Error_Summary"), var3, var4, var2);
      }

   }

   public void buildAnnotationTypeSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Annotation_Types_Summary"), this.configuration.getText("doclet.annotationtypes"));
      String[] var4 = new String[]{this.configuration.getText("doclet.AnnotationType"), this.configuration.getText("doclet.Description")};
      Object var5 = this.packageDoc.isIncluded() ? this.packageDoc.annotationTypes() : this.configuration.classDocCatalog.annotationTypes(Util.getPackageName(this.packageDoc));
      if (((Object[])var5).length > 0) {
         this.profilePackageWriter.addClassesSummary((ClassDoc[])var5, this.configuration.getText("doclet.Annotation_Types_Summary"), var3, var4, var2);
      }

   }

   public void buildPackageDescription(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.profilePackageWriter.addPackageDescription(var2);
      }
   }

   public void buildPackageTags(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.profilePackageWriter.addPackageTags(var2);
      }
   }
}
