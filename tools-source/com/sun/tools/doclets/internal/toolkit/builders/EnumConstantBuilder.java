package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.EnumConstantWriter;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumConstantBuilder extends AbstractMemberBuilder {
   private final ClassDoc classDoc;
   private final VisibleMemberMap visibleMemberMap;
   private final EnumConstantWriter writer;
   private final List enumConstants;
   private int currentEnumConstantsIndex;

   private EnumConstantBuilder(AbstractBuilder.Context var1, ClassDoc var2, EnumConstantWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, 1, this.configuration);
      this.enumConstants = new ArrayList(this.visibleMemberMap.getMembersFor(var2));
      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.enumConstants, this.configuration.getMemberComparator());
      }

   }

   public static EnumConstantBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, EnumConstantWriter var2) {
      return new EnumConstantBuilder(var0, var1, var2);
   }

   public String getName() {
      return "EnumConstantDetails";
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public VisibleMemberMap getVisibleMemberMap() {
      return this.visibleMemberMap;
   }

   public boolean hasMembersToDocument() {
      return this.enumConstants.size() > 0;
   }

   public void buildEnumConstant(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.enumConstants.size();
         if (var3 > 0) {
            Content var4 = this.writer.getEnumConstantsDetailsTreeHeader(this.classDoc, var2);

            for(this.currentEnumConstantsIndex = 0; this.currentEnumConstantsIndex < var3; ++this.currentEnumConstantsIndex) {
               Content var5 = this.writer.getEnumConstantsTreeHeader((FieldDoc)this.enumConstants.get(this.currentEnumConstantsIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getEnumConstants(var5, this.currentEnumConstantsIndex == var3 - 1));
            }

            var2.addContent(this.writer.getEnumConstantsDetails(var4));
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((FieldDoc)this.enumConstants.get(this.currentEnumConstantsIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((FieldDoc)this.enumConstants.get(this.currentEnumConstantsIndex), var2);
   }

   public void buildEnumConstantComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.writer.addComments((FieldDoc)this.enumConstants.get(this.currentEnumConstantsIndex), var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((FieldDoc)this.enumConstants.get(this.currentEnumConstantsIndex), var2);
   }

   public EnumConstantWriter getWriter() {
      return this.writer;
   }
}
