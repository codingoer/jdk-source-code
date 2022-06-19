package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MethodWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodBuilder extends AbstractMemberBuilder {
   private int currentMethodIndex;
   private final ClassDoc classDoc;
   private final VisibleMemberMap visibleMemberMap;
   private final MethodWriter writer;
   private List methods;

   private MethodBuilder(AbstractBuilder.Context var1, ClassDoc var2, MethodWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, 4, this.configuration);
      this.methods = new ArrayList(this.visibleMemberMap.getLeafClassMembers(this.configuration));
      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.methods, this.configuration.getMemberComparator());
      }

   }

   public static MethodBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, MethodWriter var2) {
      return new MethodBuilder(var0, var1, var2);
   }

   public String getName() {
      return "MethodDetails";
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public VisibleMemberMap getVisibleMemberMap() {
      return this.visibleMemberMap;
   }

   public boolean hasMembersToDocument() {
      return this.methods.size() > 0;
   }

   public void buildMethodDoc(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.methods.size();
         if (var3 > 0) {
            Content var4 = this.writer.getMethodDetailsTreeHeader(this.classDoc, var2);

            for(this.currentMethodIndex = 0; this.currentMethodIndex < var3; ++this.currentMethodIndex) {
               Content var5 = this.writer.getMethodDocTreeHeader((MethodDoc)this.methods.get(this.currentMethodIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getMethodDoc(var5, this.currentMethodIndex == var3 - 1));
            }

            var2.addContent(this.writer.getMethodDetails(var4));
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((MethodDoc)this.methods.get(this.currentMethodIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((MethodDoc)this.methods.get(this.currentMethodIndex), var2);
   }

   public void buildMethodComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         MethodDoc var3 = (MethodDoc)this.methods.get(this.currentMethodIndex);
         if (var3.inlineTags().length == 0) {
            DocFinder.Output var4 = DocFinder.search(new DocFinder.Input(var3));
            var3 = var4.inlineTags != null && var4.inlineTags.length > 0 ? (MethodDoc)var4.holder : var3;
         }

         this.writer.addComments(var3.containingClass(), var3, var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((MethodDoc)this.methods.get(this.currentMethodIndex), var2);
   }

   public MethodWriter getWriter() {
      return this.writer;
   }
}
