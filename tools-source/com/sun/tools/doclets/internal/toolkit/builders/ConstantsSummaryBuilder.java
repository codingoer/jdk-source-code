package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.ConstantsSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ConstantsSummaryBuilder extends AbstractBuilder {
   public static final String ROOT = "ConstantSummary";
   public static final int MAX_CONSTANT_VALUE_INDEX_LENGTH = 2;
   protected final ConstantsSummaryWriter writer;
   protected final Set classDocsWithConstFields;
   protected Set printedPackageHeaders;
   private PackageDoc currentPackage;
   private ClassDoc currentClass;
   private Content contentTree;

   private ConstantsSummaryBuilder(AbstractBuilder.Context var1, ConstantsSummaryWriter var2) {
      super(var1);
      this.writer = var2;
      this.classDocsWithConstFields = new HashSet();
   }

   public static ConstantsSummaryBuilder getInstance(AbstractBuilder.Context var0, ConstantsSummaryWriter var1) {
      return new ConstantsSummaryBuilder(var0, var1);
   }

   public void build() throws IOException {
      if (this.writer != null) {
         this.build(this.layoutParser.parseXML("ConstantSummary"), this.contentTree);
      }
   }

   public String getName() {
      return "ConstantSummary";
   }

   public void buildConstantSummary(XMLNode var1, Content var2) throws Exception {
      var2 = this.writer.getHeader();
      this.buildChildren(var1, var2);
      this.writer.addFooter(var2);
      this.writer.printDocument(var2);
      this.writer.close();
   }

   public void buildContents(XMLNode var1, Content var2) {
      Content var3 = this.writer.getContentsHeader();
      PackageDoc[] var4 = this.configuration.packages;
      this.printedPackageHeaders = new HashSet();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (this.hasConstantField(var4[var5]) && !this.hasPrintedPackageIndex(var4[var5].name())) {
            this.writer.addLinkToPackageContent(var4[var5], this.parsePackageName(var4[var5].name()), this.printedPackageHeaders, var3);
         }
      }

      var2.addContent(this.writer.getContentsList(var3));
   }

   public void buildConstantSummaries(XMLNode var1, Content var2) {
      PackageDoc[] var3 = this.configuration.packages;
      this.printedPackageHeaders = new HashSet();
      Content var4 = this.writer.getConstantSummaries();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (this.hasConstantField(var3[var5])) {
            this.currentPackage = var3[var5];
            this.buildChildren(var1, var4);
         }
      }

      var2.addContent(var4);
   }

   public void buildPackageHeader(XMLNode var1, Content var2) {
      String var3 = this.parsePackageName(this.currentPackage.name());
      if (!this.printedPackageHeaders.contains(var3)) {
         this.writer.addPackageName(this.currentPackage, this.parsePackageName(this.currentPackage.name()), var2);
         this.printedPackageHeaders.add(var3);
      }

   }

   public void buildClassConstantSummary(XMLNode var1, Content var2) {
      ClassDoc[] var3 = this.currentPackage.name().length() > 0 ? this.currentPackage.allClasses() : this.configuration.classDocCatalog.allClasses("<Unnamed>");
      Arrays.sort(var3);
      Content var4 = this.writer.getClassConstantHeader();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (this.classDocsWithConstFields.contains(var3[var5]) && var3[var5].isIncluded()) {
            this.currentClass = var3[var5];
            this.buildChildren(var1, var4);
         }
      }

      var2.addContent(var4);
   }

   public void buildConstantMembers(XMLNode var1, Content var2) {
      (new ConstantFieldBuilder(this.currentClass)).buildMembersSummary(var1, var2);
   }

   private boolean hasConstantField(PackageDoc var1) {
      ClassDoc[] var2;
      if (var1.name().length() > 0) {
         var2 = var1.allClasses();
      } else {
         var2 = this.configuration.classDocCatalog.allClasses("<Unnamed>");
      }

      boolean var3 = false;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (var2[var4].isIncluded() && this.hasConstantField(var2[var4])) {
            var3 = true;
         }
      }

      return var3;
   }

   private boolean hasConstantField(ClassDoc var1) {
      VisibleMemberMap var2 = new VisibleMemberMap(var1, 2, this.configuration);
      List var3 = var2.getLeafClassMembers(this.configuration);
      Iterator var4 = var3.iterator();

      FieldDoc var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (FieldDoc)var4.next();
      } while(var5.constantValueExpression() == null);

      this.classDocsWithConstFields.add(var1);
      return true;
   }

   private boolean hasPrintedPackageIndex(String var1) {
      String[] var2 = (String[])this.printedPackageHeaders.toArray(new String[0]);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.startsWith(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   private String parsePackageName(String var1) {
      int var2 = -1;

      for(int var3 = 0; var3 < 2; ++var3) {
         var2 = var1.indexOf(".", var2 + 1);
      }

      if (var2 != -1) {
         var1 = var1.substring(0, var2);
      }

      return var1;
   }

   private class ConstantFieldBuilder {
      protected VisibleMemberMap visibleMemberMapFields = null;
      protected VisibleMemberMap visibleMemberMapEnumConst = null;
      protected ClassDoc classdoc;

      public ConstantFieldBuilder(ClassDoc var2) {
         this.classdoc = var2;
         this.visibleMemberMapFields = new VisibleMemberMap(var2, 2, ConstantsSummaryBuilder.this.configuration);
         this.visibleMemberMapEnumConst = new VisibleMemberMap(var2, 1, ConstantsSummaryBuilder.this.configuration);
      }

      protected void buildMembersSummary(XMLNode var1, Content var2) {
         ArrayList var3 = new ArrayList(this.members());
         if (var3.size() > 0) {
            Collections.sort(var3);
            ConstantsSummaryBuilder.this.writer.addConstantMembers(this.classdoc, var3, var2);
         }

      }

      protected List members() {
         List var1 = this.visibleMemberMapFields.getLeafClassMembers(ConstantsSummaryBuilder.this.configuration);
         var1.addAll(this.visibleMemberMapEnumConst.getLeafClassMembers(ConstantsSummaryBuilder.this.configuration));
         if (var1 != null) {
            Iterator var2 = var1.iterator();
            LinkedList var3 = new LinkedList();

            while(var2.hasNext()) {
               FieldDoc var4 = (FieldDoc)var2.next();
               if (var4.constantValue() != null) {
                  var3.add(var4);
               }
            }

            return var3;
         } else {
            return null;
         }
      }
   }
}
