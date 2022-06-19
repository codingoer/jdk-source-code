package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeRequiredMemberWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnotationTypeRequiredMemberBuilder extends AbstractMemberBuilder {
   protected ClassDoc classDoc;
   protected VisibleMemberMap visibleMemberMap;
   protected AnnotationTypeRequiredMemberWriter writer;
   protected List members;
   protected int currentMemberIndex;

   protected AnnotationTypeRequiredMemberBuilder(AbstractBuilder.Context var1, ClassDoc var2, AnnotationTypeRequiredMemberWriter var3, int var4) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, var4, this.configuration);
      this.members = new ArrayList(this.visibleMemberMap.getMembersFor(var2));
      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.members, this.configuration.getMemberComparator());
      }

   }

   public static AnnotationTypeRequiredMemberBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, AnnotationTypeRequiredMemberWriter var2) {
      return new AnnotationTypeRequiredMemberBuilder(var0, var1, var2, 7);
   }

   public String getName() {
      return "AnnotationTypeRequiredMemberDetails";
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public VisibleMemberMap getVisibleMemberMap() {
      return this.visibleMemberMap;
   }

   public boolean hasMembersToDocument() {
      return this.members.size() > 0;
   }

   public void buildAnnotationTypeRequiredMember(XMLNode var1, Content var2) {
      this.buildAnnotationTypeMember(var1, var2);
   }

   public void buildAnnotationTypeMember(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.members.size();
         if (var3 > 0) {
            this.writer.addAnnotationDetailsMarker(var2);

            for(this.currentMemberIndex = 0; this.currentMemberIndex < var3; ++this.currentMemberIndex) {
               Content var4 = this.writer.getMemberTreeHeader();
               this.writer.addAnnotationDetailsTreeHeader(this.classDoc, var4);
               Content var5 = this.writer.getAnnotationDocTreeHeader((MemberDoc)this.members.get(this.currentMemberIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getAnnotationDoc(var5, this.currentMemberIndex == var3 - 1));
               var2.addContent(this.writer.getAnnotationDetails(var4));
            }
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((MemberDoc)this.members.get(this.currentMemberIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((MemberDoc)this.members.get(this.currentMemberIndex), var2);
   }

   public void buildMemberComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.writer.addComments((MemberDoc)this.members.get(this.currentMemberIndex), var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((MemberDoc)this.members.get(this.currentMemberIndex), var2);
   }

   public AnnotationTypeRequiredMemberWriter getWriter() {
      return this.writer;
   }
}
