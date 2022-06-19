package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.internal.toolkit.ConstructorWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConstructorBuilder extends AbstractMemberBuilder {
   public static final String NAME = "ConstructorDetails";
   private int currentConstructorIndex;
   private final ClassDoc classDoc;
   private final VisibleMemberMap visibleMemberMap;
   private final ConstructorWriter writer;
   private final List constructors;

   private ConstructorBuilder(AbstractBuilder.Context var1, ClassDoc var2, ConstructorWriter var3) {
      super(var1);
      this.classDoc = var2;
      this.writer = var3;
      this.visibleMemberMap = new VisibleMemberMap(var2, 3, this.configuration);
      this.constructors = new ArrayList(this.visibleMemberMap.getMembersFor(var2));

      for(int var4 = 0; var4 < this.constructors.size(); ++var4) {
         if (((ProgramElementDoc)this.constructors.get(var4)).isProtected() || ((ProgramElementDoc)this.constructors.get(var4)).isPrivate()) {
            var3.setFoundNonPubConstructor(true);
         }
      }

      if (this.configuration.getMemberComparator() != null) {
         Collections.sort(this.constructors, this.configuration.getMemberComparator());
      }

   }

   public static ConstructorBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, ConstructorWriter var2) {
      return new ConstructorBuilder(var0, var1, var2);
   }

   public String getName() {
      return "ConstructorDetails";
   }

   public boolean hasMembersToDocument() {
      return this.constructors.size() > 0;
   }

   public List members(ClassDoc var1) {
      return this.visibleMemberMap.getMembersFor(var1);
   }

   public ConstructorWriter getWriter() {
      return this.writer;
   }

   public void buildConstructorDoc(XMLNode var1, Content var2) {
      if (this.writer != null) {
         int var3 = this.constructors.size();
         if (var3 > 0) {
            Content var4 = this.writer.getConstructorDetailsTreeHeader(this.classDoc, var2);

            for(this.currentConstructorIndex = 0; this.currentConstructorIndex < var3; ++this.currentConstructorIndex) {
               Content var5 = this.writer.getConstructorDocTreeHeader((ConstructorDoc)this.constructors.get(this.currentConstructorIndex), var4);
               this.buildChildren(var1, var5);
               var4.addContent(this.writer.getConstructorDoc(var5, this.currentConstructorIndex == var3 - 1));
            }

            var2.addContent(this.writer.getConstructorDetails(var4));
         }

      }
   }

   public void buildSignature(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getSignature((ConstructorDoc)this.constructors.get(this.currentConstructorIndex)));
   }

   public void buildDeprecationInfo(XMLNode var1, Content var2) {
      this.writer.addDeprecated((ConstructorDoc)this.constructors.get(this.currentConstructorIndex), var2);
   }

   public void buildConstructorComments(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.writer.addComments((ConstructorDoc)this.constructors.get(this.currentConstructorIndex), var2);
      }

   }

   public void buildTagInfo(XMLNode var1, Content var2) {
      this.writer.addTags((ConstructorDoc)this.constructors.get(this.currentConstructorIndex), var2);
   }
}
