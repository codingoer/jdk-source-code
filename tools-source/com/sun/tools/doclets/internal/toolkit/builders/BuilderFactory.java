package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.PropertyWriter;
import com.sun.tools.doclets.internal.toolkit.WriterFactory;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.javac.jvm.Profile;
import java.util.HashSet;

public class BuilderFactory {
   private final Configuration configuration;
   private final WriterFactory writerFactory;
   private final AbstractBuilder.Context context;

   public BuilderFactory(Configuration var1) {
      this.configuration = var1;
      this.writerFactory = var1.getWriterFactory();
      HashSet var2 = new HashSet();
      this.context = new AbstractBuilder.Context(var1, var2, LayoutParser.getInstance(var1));
   }

   public AbstractBuilder getConstantsSummaryBuider() throws Exception {
      return ConstantsSummaryBuilder.getInstance(this.context, this.writerFactory.getConstantsSummaryWriter());
   }

   public AbstractBuilder getPackageSummaryBuilder(PackageDoc var1, PackageDoc var2, PackageDoc var3) throws Exception {
      return PackageSummaryBuilder.getInstance(this.context, var1, this.writerFactory.getPackageSummaryWriter(var1, var2, var3));
   }

   public AbstractBuilder getProfileSummaryBuilder(Profile var1, Profile var2, Profile var3) throws Exception {
      return ProfileSummaryBuilder.getInstance(this.context, var1, this.writerFactory.getProfileSummaryWriter(var1, var2, var3));
   }

   public AbstractBuilder getProfilePackageSummaryBuilder(PackageDoc var1, PackageDoc var2, PackageDoc var3, Profile var4) throws Exception {
      return ProfilePackageSummaryBuilder.getInstance(this.context, var1, this.writerFactory.getProfilePackageSummaryWriter(var1, var2, var3, var4), var4);
   }

   public AbstractBuilder getClassBuilder(ClassDoc var1, ClassDoc var2, ClassDoc var3, ClassTree var4) throws Exception {
      return ClassBuilder.getInstance(this.context, var1, this.writerFactory.getClassWriter(var1, var2, var3, var4));
   }

   public AbstractBuilder getAnnotationTypeBuilder(AnnotationTypeDoc var1, Type var2, Type var3) throws Exception {
      return AnnotationTypeBuilder.getInstance(this.context, var1, this.writerFactory.getAnnotationTypeWriter(var1, var2, var3));
   }

   public AbstractBuilder getMethodBuilder(ClassWriter var1) throws Exception {
      return MethodBuilder.getInstance(this.context, var1.getClassDoc(), this.writerFactory.getMethodWriter(var1));
   }

   public AbstractBuilder getAnnotationTypeFieldsBuilder(AnnotationTypeWriter var1) throws Exception {
      return AnnotationTypeFieldBuilder.getInstance(this.context, var1.getAnnotationTypeDoc(), this.writerFactory.getAnnotationTypeFieldWriter(var1));
   }

   public AbstractBuilder getAnnotationTypeOptionalMemberBuilder(AnnotationTypeWriter var1) throws Exception {
      return AnnotationTypeOptionalMemberBuilder.getInstance(this.context, var1.getAnnotationTypeDoc(), this.writerFactory.getAnnotationTypeOptionalMemberWriter(var1));
   }

   public AbstractBuilder getAnnotationTypeRequiredMemberBuilder(AnnotationTypeWriter var1) throws Exception {
      return AnnotationTypeRequiredMemberBuilder.getInstance(this.context, var1.getAnnotationTypeDoc(), this.writerFactory.getAnnotationTypeRequiredMemberWriter(var1));
   }

   public AbstractBuilder getEnumConstantsBuilder(ClassWriter var1) throws Exception {
      return EnumConstantBuilder.getInstance(this.context, var1.getClassDoc(), this.writerFactory.getEnumConstantWriter(var1));
   }

   public AbstractBuilder getFieldBuilder(ClassWriter var1) throws Exception {
      return FieldBuilder.getInstance(this.context, var1.getClassDoc(), this.writerFactory.getFieldWriter(var1));
   }

   public AbstractBuilder getPropertyBuilder(ClassWriter var1) throws Exception {
      PropertyWriter var2 = this.writerFactory.getPropertyWriter(var1);
      return PropertyBuilder.getInstance(this.context, var1.getClassDoc(), var2);
   }

   public AbstractBuilder getConstructorBuilder(ClassWriter var1) throws Exception {
      return ConstructorBuilder.getInstance(this.context, var1.getClassDoc(), this.writerFactory.getConstructorWriter(var1));
   }

   public AbstractBuilder getMemberSummaryBuilder(ClassWriter var1) throws Exception {
      return MemberSummaryBuilder.getInstance(var1, this.context);
   }

   public AbstractBuilder getMemberSummaryBuilder(AnnotationTypeWriter var1) throws Exception {
      return MemberSummaryBuilder.getInstance(var1, this.context);
   }

   public AbstractBuilder getSerializedFormBuilder() throws Exception {
      return SerializedFormBuilder.getInstance(this.context);
   }
}
