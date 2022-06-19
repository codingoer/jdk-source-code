package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.FieldWriter;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldBuilder extends AbstractMemberBuilder {
   private final ClassDoc classDoc;
   private final VisibleMemberMap visibleMemberMap;
   private final FieldWriter writer;
   private final List fields;
   private int currentFieldIndex;

   private FieldBuilder(AbstractBuilder.Context var1, ClassDoc var2, FieldWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, 2, this.configuration);
      this.fields = new ArrayList(this.visibleMemberMap.getLeafClassMembers(this.configuration));
      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.fields, this.configuration.getMemberComparator());
      }

   }

   public static FieldBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, FieldWriter var2) {
      return new FieldBuilder(var0, var1, var2);
   }

   public String getName() {
      return "FieldDetails";
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public VisibleMemberMap getVisibleMemberMap() {
      return this.visibleMemberMap;
   }

   public boolean hasMembersToDocument() {
      return this.fields.size() > 0;
   }

   public void buildFieldDoc(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.fields.size();
         if (var3 > 0) {
            Content var4 = this.writer.getFieldDetailsTreeHeader(this.classDoc, var2);

            for(this.currentFieldIndex = 0; this.currentFieldIndex < var3; ++this.currentFieldIndex) {
               Content var5 = this.writer.getFieldDocTreeHeader((FieldDoc)this.fields.get(this.currentFieldIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getFieldDoc(var5, this.currentFieldIndex == var3 - 1));
            }

            var2.addContent(this.writer.getFieldDetails(var4));
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((FieldDoc)this.fields.get(this.currentFieldIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((FieldDoc)this.fields.get(this.currentFieldIndex), var2);
   }

   public void buildFieldComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.writer.addComments((FieldDoc)this.fields.get(this.currentFieldIndex), var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((FieldDoc)this.fields.get(this.currentFieldIndex), var2);
   }

   public FieldWriter getWriter() {
      return this.writer;
   }
}
