package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Arrays;

public class ClassBuilder extends AbstractBuilder {
   public static final String ROOT = "ClassDoc";
   private final ClassDoc classDoc;
   private final ClassWriter writer;
   private final boolean isInterface;
   private final boolean isEnum;
   private Content contentTree;

   private ClassBuilder(AbstractBuilder.Context var1, ClassDoc var2, ClassWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      if (var2.isInterface()) {
         this.isInterface = true;
         this.isEnum = false;
      } else if (var2.isEnum()) {
         this.isInterface = false;
         this.isEnum = true;
         Util.setEnumDocumentation(this.configuration, var2);
      } else {
         this.isInterface = false;
         this.isEnum = false;
      }

   }

   public static ClassBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, ClassWriter var2) {
      return new ClassBuilder(var0, var1, var2);
   }

   public void build() throws IOException {
      this.build(this.layoutParser.parseXML("ClassDoc"), this.contentTree);
   }

   public String getName() {
      return "ClassDoc";
   }

   public void buildClassDoc(XMLNode var1, Content var2) throws Exception {
      String var3;
      if (this.isInterface) {
         var3 = "doclet.Interface";
      } else if (this.isEnum) {
         var3 = "doclet.Enum";
      } else {
         var3 = "doclet.Class";
      }

      var2 = this.writer.getHeader(this.configuration.getText(var3) + " " + this.classDoc.name());
      Content var4 = this.writer.getClassContentHeader();
      this.buildChildren(var1, var4);
      var2.addContent(var4);
      this.writer.addFooter(var2);
      this.writer.printDocument(var2);
      this.writer.close();
      this.copyDocFiles();
   }

   public void buildClassTree(XMLNode var1, Content var2) {
      this.writer.addClassTree(var2);
   }

   public void buildClassInfo(XMLNode var1, Content var2) {
      Content var3 = this.writer.getClassInfoTreeHeader();
      this.buildChildren(var1, var3);
      var2.addContent(this.writer.getClassInfo(var3));
   }

   public void buildTypeParamInfo(XMLNode var1, Content var2) {
      this.writer.addTypeParamInfo(var2);
   }

   public void buildSuperInterfacesInfo(XMLNode var1, Content var2) {
      this.writer.addSuperInterfacesInfo(var2);
   }

   public void buildImplementedInterfacesInfo(XMLNode var1, Content var2) {
      this.writer.addImplementedInterfacesInfo(var2);
   }

   public void buildSubClassInfo(XMLNode var1, Content var2) {
      this.writer.addSubClassInfo(var2);
   }

   public void buildSubInterfacesInfo(XMLNode var1, Content var2) {
      this.writer.addSubInterfacesInfo(var2);
   }

   public void buildInterfaceUsageInfo(XMLNode var1, Content var2) {
      this.writer.addInterfaceUsageInfo(var2);
   }

   public void buildFunctionalInterfaceInfo(XMLNode var1, Content var2) {
      this.writer.addFunctionalInterfaceInfo(var2);
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addClassDeprecationInfo(var2);
   }

   public void buildNestedClassInfo(XMLNode var1, Content var2) {
      this.writer.addNestedClassInfo(var2);
   }

   private void copyDocFiles() {
      PackageDoc var1 = this.classDoc.containingPackage();
      if ((this.configuration.packages == null || Arrays.binarySearch(this.configuration.packages, var1) < 0) && !this.containingPackagesSeen.contains(var1.name())) {
         Util.copyDocFiles(this.configuration, var1);
         this.containingPackagesSeen.add(var1.name());
      }

   }

   public void buildClassSignature(XMLNode var1, Content var2) {
      StringBuilder var3 = new StringBuilder(this.classDoc.modifiers());
      var3.append(var3.length() == 0 ? "" : " ");
      if (this.isEnum) {
         var3.append("enum ");
         int var4;
         if ((var4 = var3.indexOf("abstract")) >= 0) {
            var3.delete(var4, var4 + "abstract".length());
            var3 = new StringBuilder(Util.replaceText(var3.toString(), "  ", " "));
         }

         if ((var4 = var3.indexOf("final")) >= 0) {
            var3.delete(var4, var4 + "final".length());
            var3 = new StringBuilder(Util.replaceText(var3.toString(), "  ", " "));
         }
      } else if (!this.isInterface) {
         var3.append("class ");
      }

      this.writer.addClassSignature(var3.toString(), var2);
   }

   public void buildClassDescription(XMLNode var1, Content var2) {
      this.writer.addClassDescription(var2);
   }

   public void buildClassTagInfo(XMLNode var1, Content var2) {
      this.writer.addClassTagInfo(var2);
   }

   public void buildMemberSummary(XMLNode var1, Content var2) throws Exception {
      Content var3 = this.writer.getMemberTreeHeader();
      this.configuration.getBuilderFactory().getMemberSummaryBuilder(this.writer).buildChildren(var1, var3);
      var2.addContent(this.writer.getMemberSummaryTree(var3));
   }

   public void buildMemberDetails(XMLNode var1, Content var2) {
      Content var3 = this.writer.getMemberTreeHeader();
      this.buildChildren(var1, var3);
      var2.addContent(this.writer.getMemberDetailsTree(var3));
   }

   public void buildEnumConstantsDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getEnumConstantsBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildFieldDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getFieldBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildPropertyDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getPropertyBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildConstructorDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getConstructorBuilder(this.writer).buildChildren(var1, var2);
   }

   public void buildMethodDetails(XMLNode var1, Content var2) throws Exception {
      this.configuration.getBuilderFactory().getMethodBuilder(this.writer).buildChildren(var1, var2);
   }
}
