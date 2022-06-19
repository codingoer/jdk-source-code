package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Arrays;

public class AnnotationTypeBuilder extends AbstractBuilder {
   public static final String ROOT = "AnnotationTypeDoc";
   private final AnnotationTypeDoc annotationTypeDoc;
   private final AnnotationTypeWriter writer;
   private Content contentTree;

   private AnnotationTypeBuilder(AbstractBuilder.Context var1, AnnotationTypeDoc var2, AnnotationTypeWriter var3) {
      super(var1);
      this.annotationTypeDoc = var2;
      this.writer = var3;
   }

   public static AnnotationTypeBuilder getInstance(AbstractBuilder.Context var0, AnnotationTypeDoc var1, AnnotationTypeWriter var2) throws Exception {
      return new AnnotationTypeBuilder(var0, var1, var2);
   }

   public void build() throws IOException {
      this.build(this.layoutParser.parseXML("AnnotationTypeDoc"), this.contentTree);
   }

   public String getName() {
      return "AnnotationTypeDoc";
   }

   public void buildAnnotationTypeDoc(XMLNode var1, Content var2) throws Exception {
      var2 = this.writer.getHeader(this.configuration.getText("doclet.AnnotationType") + " " + this.annotationTypeDoc.name());
      Content var3 = this.writer.getAnnotationContentHeader();
      this.buildChildren(var1, var3);
      var2.addContent(var3);
      this.writer.addFooter(var2);
      this.writer.printDocument(var2);
      this.writer.close();
      this.copyDocFiles();
   }

   private void copyDocFiles() {
      PackageDoc var1 = this.annotationTypeDoc.containingPackage();
      if ((this.configuration.packages == null || Arrays.binarySearch(this.configuration.packages, var1) < 0) && !this.containingPackagesSeen.contains(var1.name())) {
         Util.copyDocFiles(this.configuration, var1);
         this.containingPackagesSeen.add(var1.name());
      }

   }

   public void buildAnnotationTypeInfo(XMLNode var1, Content var2) {
      Content var3 = this.writer.getAnnotationInfoTreeHeader();
      this.buildChildren(var1, var3);
      var2.addContent(this.writer.getAnnotationInfo(var3));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addAnnotationTypeDeprecationInfo(var2);
   }

   public void buildAnnotationTypeSignature(XMLNode var1, Content var2) {
      StringBuilder var3 = new StringBuilder(this.annotationTypeDoc.modifiers() + " ");
      this.writer.addAnnotationTypeSignature(Util.replaceText(var3.toString(), "interface", "@interface"), var2);
   }

   public void buildAnnotationTypeDescription(XMLNode var1, Content var2) {
      this.writer.addAnnotationTypeDescription(var2);
   }

   public void buildAnnotationTypeTagInfo(XMLNode var1, Content var2) {
      this.writer.addAnnotationTypeTagInfo(var2);
   }

   public void buildMemberSummary(XMLNode var1, Content var2) throws Exception {
      Content var3 = this.writer.getMemberTreeHeader();
      this.configuration.getBuilderFactory().getMemberSummaryBuilder(this.writer).buildChildren(var1, var3);
      var2.addContent(this.writer.getMemberSummaryTree(var3));
   }

   public void buildAnnotationTypeMemberDetails(XMLNode var1, Content var2) {
      Content var3 = this.writer.getMemberTreeHeader();
      this.buildChildren(var1, var3);
      if (var3.isValid()) {
         var2.addContent(this.writer.getMemberDetailsTree(var3));
      }

   }

   public void buildAnnotationTypeFieldDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getAnnotationTypeFieldsBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildAnnotationTypeOptionalMemberDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getAnnotationTypeOptionalMemberBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildAnnotationTypeRequiredMemberDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getAnnotationTypeRequiredMemberBuilder(this.writer).buildChildren(var1, var2);
   }
}
