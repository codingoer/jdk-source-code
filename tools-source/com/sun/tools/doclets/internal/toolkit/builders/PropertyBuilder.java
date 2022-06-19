package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.PropertyWriter;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertyBuilder extends AbstractMemberBuilder {
   private final ClassDoc classDoc;
   private final VisibleMemberMap visibleMemberMap;
   private final PropertyWriter writer;
   private final List properties;
   private int currentPropertyIndex;

   private PropertyBuilder(AbstractBuilder.Context var1, ClassDoc var2, PropertyWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, 8, this.configuration);
      this.properties = new ArrayList(this.visibleMemberMap.getMembersFor(var2));
      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.properties, this.configuration.getMemberComparator());
      }

   }

   public static PropertyBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, PropertyWriter var2) {
      return new PropertyBuilder(var0, var1, var2);
   }

   public String getName() {
      return "PropertyDetails";
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public VisibleMemberMap getVisibleMemberMap() {
      return this.visibleMemberMap;
   }

   public boolean hasMembersToDocument() {
      return this.properties.size() > 0;
   }

   public void buildPropertyDoc(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.properties.size();
         if (var3 > 0) {
            Content var4 = this.writer.getPropertyDetailsTreeHeader(this.classDoc, var2);

            for(this.currentPropertyIndex = 0; this.currentPropertyIndex < var3; ++this.currentPropertyIndex) {
               Content var5 = this.writer.getPropertyDocTreeHeader((MethodDoc)this.properties.get(this.currentPropertyIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getPropertyDoc(var5, this.currentPropertyIndex == var3 - 1));
            }

            var2.addContent(this.writer.getPropertyDetails(var4));
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((MethodDoc)this.properties.get(this.currentPropertyIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((MethodDoc)this.properties.get(this.currentPropertyIndex), var2);
   }

   public void buildPropertyComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.writer.addComments((MethodDoc)this.properties.get(this.currentPropertyIndex), var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((MethodDoc)this.properties.get(this.currentPropertyIndex), var2);
   }

   public PropertyWriter getWriter() {
      return this.writer;
   }
}
