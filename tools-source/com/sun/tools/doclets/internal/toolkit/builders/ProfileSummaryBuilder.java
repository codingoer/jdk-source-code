package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.ProfileSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;

public class ProfileSummaryBuilder extends AbstractBuilder {
   public static final String ROOT = "ProfileDoc";
   private final Profile profile;
   private final ProfileSummaryWriter profileWriter;
   private Content contentTree;
   private PackageDoc pkg;

   private ProfileSummaryBuilder(AbstractBuilder.Context var1, Profile var2, ProfileSummaryWriter var3) {
      super(var1);
      this.profile = var2;
      this.profileWriter = var3;
   }

   public static ProfileSummaryBuilder getInstance(AbstractBuilder.Context var0, Profile var1, ProfileSummaryWriter var2) {
      return new ProfileSummaryBuilder(var0, var1, var2);
   }

   public void build() throws IOException {
      if (this.profileWriter != null) {
         this.build(this.layoutParser.parseXML("ProfileDoc"), this.contentTree);
      }
   }

   public String getName() {
      return "ProfileDoc";
   }

   public void buildProfileDoc(XMLNode var1, Content var2) throws Exception {
      var2 = this.profileWriter.getProfileHeader(this.profile.name);
      this.buildChildren(var1, var2);
      this.profileWriter.addProfileFooter(var2);
      this.profileWriter.printDocument(var2);
      this.profileWriter.close();
      Util.copyDocFiles(this.configuration, DocPaths.profileSummary(this.profile.name));
   }

   public void buildContent(XMLNode var1, Content var2) {
      Content var3 = this.profileWriter.getContentHeader();
      this.buildChildren(var1, var3);
      var2.addContent(var3);
   }

   public void buildSummary(XMLNode var1, Content var2) {
      Content var3 = this.profileWriter.getSummaryHeader();
      this.buildChildren(var1, var3);
      var2.addContent(this.profileWriter.getSummaryTree(var3));
   }

   public void buildPackageSummary(XMLNode var1, Content var2) {
      PackageDoc[] var3 = (PackageDoc[])this.configuration.profilePackages.get(this.profile.name);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         this.pkg = var3[var4];
         Content var5 = this.profileWriter.getPackageSummaryHeader(this.pkg);
         this.buildChildren(var1, var5);
         var2.addContent(this.profileWriter.getPackageSummaryTree(var5));
      }

   }

   public void buildInterfaceSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Interface_Summary"), this.configuration.getText("doclet.interfaces"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Interface"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.pkg.interfaces();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Interface_Summary"), var3, var4, var2);
      }

   }

   public void buildClassSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Class_Summary"), this.configuration.getText("doclet.classes"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Class"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.pkg.ordinaryClasses();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Class_Summary"), var3, var4, var2);
      }

   }

   public void buildEnumSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Enum_Summary"), this.configuration.getText("doclet.enums"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Enum"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.pkg.enums();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Enum_Summary"), var3, var4, var2);
      }

   }

   public void buildExceptionSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Exception_Summary"), this.configuration.getText("doclet.exceptions"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Exception"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.pkg.exceptions();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Exception_Summary"), var3, var4, var2);
      }

   }

   public void buildErrorSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Error_Summary"), this.configuration.getText("doclet.errors"));
      String[] var4 = new String[]{this.configuration.getText("doclet.Error"), this.configuration.getText("doclet.Description")};
      ClassDoc[] var5 = this.pkg.errors();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Error_Summary"), var3, var4, var2);
      }

   }

   public void buildAnnotationTypeSummary(XMLNode var1, Content var2) {
      String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Annotation_Types_Summary"), this.configuration.getText("doclet.annotationtypes"));
      String[] var4 = new String[]{this.configuration.getText("doclet.AnnotationType"), this.configuration.getText("doclet.Description")};
      AnnotationTypeDoc[] var5 = this.pkg.annotationTypes();
      if (var5.length > 0) {
         this.profileWriter.addClassesSummary(var5, this.configuration.getText("doclet.Annotation_Types_Summary"), var3, var4, var2);
      }

   }
}
