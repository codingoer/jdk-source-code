package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.WriterFactory;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MemberSummaryBuilder extends AbstractMemberBuilder {
   public static final String NAME = "MemberSummary";
   private final VisibleMemberMap[] visibleMemberMaps;
   private MemberSummaryWriter[] memberSummaryWriters;
   private final ClassDoc classDoc;

   private MemberSummaryBuilder(AbstractBuilder.Context var1, ClassDoc var2) {
      super(var1);
      this.classDoc = var2;
      this.visibleMemberMaps = new VisibleMemberMap[9];

      for(int var3 = 0; var3 < 9; ++var3) {
         this.visibleMemberMaps[var3] = new VisibleMemberMap(var2, var3, this.configuration);
      }

   }

   public static MemberSummaryBuilder getInstance(ClassWriter var0, AbstractBuilder.Context var1) throws Exception {
      MemberSummaryBuilder var2 = new MemberSummaryBuilder(var1, var0.getClassDoc());
      var2.memberSummaryWriters = new MemberSummaryWriter[9];
      WriterFactory var3 = var1.configuration.getWriterFactory();

      for(int var4 = 0; var4 < 9; ++var4) {
         var2.memberSummaryWriters[var4] = var2.visibleMemberMaps[var4].noVisibleMembers() ? null : var3.getMemberSummaryWriter(var0, var4);
      }

      return var2;
   }

   public static MemberSummaryBuilder getInstance(AnnotationTypeWriter var0, AbstractBuilder.Context var1) throws Exception {
      MemberSummaryBuilder var2 = new MemberSummaryBuilder(var1, var0.getAnnotationTypeDoc());
      var2.memberSummaryWriters = new MemberSummaryWriter[9];
      WriterFactory var3 = var1.configuration.getWriterFactory();

      for(int var4 = 0; var4 < 9; ++var4) {
         var2.memberSummaryWriters[var4] = var2.visibleMemberMaps[var4].noVisibleMembers() ? null : var3.getMemberSummaryWriter(var0, var4);
      }

      return var2;
   }

   public String getName() {
      return "MemberSummary";
   }

   public VisibleMemberMap getVisibleMemberMap(int var1) {
      return this.visibleMemberMaps[var1];
   }

   public MemberSummaryWriter getMemberSummaryWriter(int var1) {
      return this.memberSummaryWriters[var1];
   }

   public List members(int var1) {
      return this.visibleMemberMaps[var1].getLeafClassMembers(this.configuration);
   }

   public boolean hasMembersToDocument() {
      if (this.classDoc instanceof AnnotationTypeDoc) {
         return ((AnnotationTypeDoc)this.classDoc).elements().length > 0;
      } else {
         for(int var1 = 0; var1 < 9; ++var1) {
            VisibleMemberMap var2 = this.visibleMemberMaps[var1];
            if (!var2.noVisibleMembers()) {
               return true;
            }
         }

         return false;
      }
   }

   public void buildEnumConstantsSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[1];
      VisibleMemberMap var4 = this.visibleMemberMaps[1];
      this.addSummary(var3, var4, false, var2);
   }

   public void buildAnnotationTypeFieldsSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[5];
      VisibleMemberMap var4 = this.visibleMemberMaps[5];
      this.addSummary(var3, var4, false, var2);
   }

   public void buildAnnotationTypeOptionalMemberSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[6];
      VisibleMemberMap var4 = this.visibleMemberMaps[6];
      this.addSummary(var3, var4, false, var2);
   }

   public void buildAnnotationTypeRequiredMemberSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[7];
      VisibleMemberMap var4 = this.visibleMemberMaps[7];
      this.addSummary(var3, var4, false, var2);
   }

   public void buildFieldsSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[2];
      VisibleMemberMap var4 = this.visibleMemberMaps[2];
      this.addSummary(var3, var4, true, var2);
   }

   public void buildPropertiesSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[8];
      VisibleMemberMap var4 = this.visibleMemberMaps[8];
      this.addSummary(var3, var4, true, var2);
   }

   public void buildNestedClassesSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[0];
      VisibleMemberMap var4 = this.visibleMemberMaps[0];
      this.addSummary(var3, var4, true, var2);
   }

   public void buildMethodsSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[4];
      VisibleMemberMap var4 = this.visibleMemberMaps[4];
      this.addSummary(var3, var4, true, var2);
   }

   public void buildConstructorsSummary(XMLNode var1, Content var2) {
      MemberSummaryWriter var3 = this.memberSummaryWriters[3];
      VisibleMemberMap var4 = this.visibleMemberMaps[3];
      this.addSummary(var3, var4, false, var2);
   }

   private void buildSummary(MemberSummaryWriter var1, VisibleMemberMap var2, LinkedList var3) {
      ArrayList var4 = new ArrayList(var2.getLeafClassMembers(this.configuration));
      if (var4.size() > 0) {
         Collections.sort(var4);
         LinkedList var5 = new LinkedList();

         for(int var6 = 0; var6 < var4.size(); ++var6) {
            ProgramElementDoc var7 = (ProgramElementDoc)var4.get(var6);
            ProgramElementDoc var8 = var2.getPropertyMemberDoc(var7);
            if (var8 != null) {
               this.processProperty(var2, var7, var8);
            }

            Tag[] var9 = var7.firstSentenceTags();
            if (var7 instanceof MethodDoc && var9.length == 0) {
               DocFinder.Output var10 = DocFinder.search(new DocFinder.Input((MethodDoc)var7));
               if (var10.holder != null && var10.holder.firstSentenceTags().length > 0) {
                  var9 = var10.holder.firstSentenceTags();
               }
            }

            var1.addMemberSummary(this.classDoc, var7, var9, var5, var6);
         }

         var3.add(var1.getSummaryTableTree(this.classDoc, var5));
      }

   }

   private void processProperty(VisibleMemberMap var1, ProgramElementDoc var2, ProgramElementDoc var3) {
      StringBuilder var4 = new StringBuilder();
      boolean var5 = this.isSetter(var2);
      boolean var6 = this.isGetter(var2);
      if (var6 || var5) {
         if (var5) {
            var4.append(MessageFormat.format(this.configuration.getText("doclet.PropertySetterWithName"), Util.propertyNameFromMethodName(this.configuration, var2.name())));
         }

         if (var6) {
            var4.append(MessageFormat.format(this.configuration.getText("doclet.PropertyGetterWithName"), Util.propertyNameFromMethodName(this.configuration, var2.name())));
         }

         if (var3.commentText() != null && !var3.commentText().isEmpty()) {
            var4.append(" \n @propertyDescription ");
         }
      }

      var4.append(var3.commentText());
      LinkedList var7 = new LinkedList();
      String[] var8 = new String[]{"@defaultValue", "@since"};
      String[] var9 = var8;
      int var10 = var8.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         String var12 = var9[var11];
         Tag[] var13 = var3.tags(var12);
         if (var13 != null) {
            var7.addAll(Arrays.asList(var13));
         }
      }

      Iterator var14 = var7.iterator();

      while(var14.hasNext()) {
         Tag var16 = (Tag)var14.next();
         var4.append("\n").append(var16.name()).append(" ").append(var16.text());
      }

      if (!var6 && !var5) {
         MethodDoc var15 = (MethodDoc)var1.getGetterForProperty(var2);
         MethodDoc var17 = (MethodDoc)var1.getSetterForProperty(var2);
         if (null != var15 && var4.indexOf("@see #" + var15.name()) == -1) {
            var4.append("\n @see #").append(var15.name()).append("() ");
         }

         if (null != var17 && var4.indexOf("@see #" + var17.name()) == -1) {
            String var18 = var17.parameters()[0].typeName();
            var18 = var18.split("<")[0];
            if (var18.contains(".")) {
               var18 = var18.substring(var18.lastIndexOf(".") + 1);
            }

            var4.append("\n @see #").append(var17.name());
            if (var17.parameters()[0].type().asTypeVariable() == null) {
               var4.append("(").append(var18).append(")");
            }

            var4.append(" \n");
         }
      }

      var2.setRawCommentText(var4.toString());
   }

   private boolean isGetter(ProgramElementDoc var1) {
      String var2 = var1.name();
      return var2.startsWith("get") || var2.startsWith("is");
   }

   private boolean isSetter(ProgramElementDoc var1) {
      return var1.name().startsWith("set");
   }

   private void buildInheritedSummary(MemberSummaryWriter var1, VisibleMemberMap var2, LinkedList var3) {
      Iterator var4 = var2.getVisibleClassesList().iterator();

      while(true) {
         ClassDoc var5;
         List var6;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  var5 = (ClassDoc)var4.next();
               } while(!var5.isPublic() && !Util.isLinkable(var5, this.configuration));
            } while(var5 == this.classDoc);

            var6 = var2.getMembersFor(var5);
         } while(var6.size() <= 0);

         Collections.sort(var6);
         Content var7 = var1.getInheritedSummaryHeader(var5);
         Content var8 = var1.getInheritedSummaryLinksTree();

         for(int var9 = 0; var9 < var6.size(); ++var9) {
            var1.addInheritedMemberSummary(var5.isPackagePrivate() && !Util.isLinkable(var5, this.configuration) ? this.classDoc : var5, (ProgramElementDoc)var6.get(var9), var9 == 0, var9 == var6.size() - 1, var8);
         }

         var7.addContent(var8);
         var3.add(var1.getMemberTree(var7));
      }
   }

   private void addSummary(MemberSummaryWriter var1, VisibleMemberMap var2, boolean var3, Content var4) {
      LinkedList var5 = new LinkedList();
      this.buildSummary(var1, var2, var5);
      if (var3) {
         this.buildInheritedSummary(var1, var2, var5);
      }

      if (!var5.isEmpty()) {
         Content var6 = var1.getMemberSummaryHeader(this.classDoc, var4);

         for(int var7 = 0; var7 < var5.size(); ++var7) {
            var6.addContent((Content)var5.get(var7));
         }

         var4.addContent(var1.getMemberTree(var6));
      }

   }
}
