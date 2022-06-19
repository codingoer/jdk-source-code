package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeOptionalMemberWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeRequiredMemberWriter;
import com.sun.tools.doclets.internal.toolkit.Content;

public class AnnotationTypeOptionalMemberBuilder extends AnnotationTypeRequiredMemberBuilder {
   private AnnotationTypeOptionalMemberBuilder(AbstractBuilder.Context var1, ClassDoc var2, AnnotationTypeOptionalMemberWriter var3) {
      super(var1, var2, var3, 6);
   }

   public static AnnotationTypeOptionalMemberBuilder getInstance(AbstractBuilder.Context var0, ClassDoc var1, AnnotationTypeOptionalMemberWriter var2) {
      return new AnnotationTypeOptionalMemberBuilder(var0, var1, var2);
   }

   public String getName() {
      return "AnnotationTypeOptionalMemberDetails";
   }

   public void buildAnnotationTypeOptionalMember(XMLNode var1, Content var2) {
      this.buildAnnotationTypeMember(var1, var2);
   }

   public void buildDefaultValueInfo(XMLNode var1, Content var2) {
      ((AnnotationTypeOptionalMemberWriter)this.writer).addDefaultValueInfo((MemberDoc)this.members.get(this.currentMemberIndex), var2);
   }

   public AnnotationTypeRequiredMemberWriter getWriter() {
      return this.writer;
   }
}
