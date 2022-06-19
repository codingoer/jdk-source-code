package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeFieldWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeOptionalMemberWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeRequiredMemberWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.ConstantsSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.PackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.ProfilePackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.ProfileSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.WriterFactory;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;

public class WriterFactoryImpl implements WriterFactory {
   private final ConfigurationImpl configuration;

   public WriterFactoryImpl(ConfigurationImpl var1) {
      this.configuration = var1;
   }

   public ConstantsSummaryWriter getConstantsSummaryWriter() throws Exception {
      return new ConstantsSummaryWriterImpl(this.configuration);
   }

   public PackageSummaryWriter getPackageSummaryWriter(PackageDoc var1, PackageDoc var2, PackageDoc var3) throws Exception {
      return new PackageWriterImpl(this.configuration, var1, var2, var3);
   }

   public ProfileSummaryWriter getProfileSummaryWriter(Profile var1, Profile var2, Profile var3) throws Exception {
      return new ProfileWriterImpl(this.configuration, var1, var2, var3);
   }

   public ProfilePackageSummaryWriter getProfilePackageSummaryWriter(PackageDoc var1, PackageDoc var2, PackageDoc var3, Profile var4) throws Exception {
      return new ProfilePackageWriterImpl(this.configuration, var1, var2, var3, var4);
   }

   public ClassWriter getClassWriter(ClassDoc var1, ClassDoc var2, ClassDoc var3, ClassTree var4) throws IOException {
      return new ClassWriterImpl(this.configuration, var1, var2, var3, var4);
   }

   public AnnotationTypeWriter getAnnotationTypeWriter(AnnotationTypeDoc var1, Type var2, Type var3) throws Exception {
      return new AnnotationTypeWriterImpl(this.configuration, var1, var2, var3);
   }

   public AnnotationTypeFieldWriter getAnnotationTypeFieldWriter(AnnotationTypeWriter var1) throws Exception {
      return new AnnotationTypeFieldWriterImpl((SubWriterHolderWriter)var1, var1.getAnnotationTypeDoc());
   }

   public AnnotationTypeOptionalMemberWriter getAnnotationTypeOptionalMemberWriter(AnnotationTypeWriter var1) throws Exception {
      return new AnnotationTypeOptionalMemberWriterImpl((SubWriterHolderWriter)var1, var1.getAnnotationTypeDoc());
   }

   public AnnotationTypeRequiredMemberWriter getAnnotationTypeRequiredMemberWriter(AnnotationTypeWriter var1) throws Exception {
      return new AnnotationTypeRequiredMemberWriterImpl((SubWriterHolderWriter)var1, var1.getAnnotationTypeDoc());
   }

   public EnumConstantWriterImpl getEnumConstantWriter(ClassWriter var1) throws Exception {
      return new EnumConstantWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
   }

   public FieldWriterImpl getFieldWriter(ClassWriter var1) throws Exception {
      return new FieldWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
   }

   public PropertyWriterImpl getPropertyWriter(ClassWriter var1) throws Exception {
      return new PropertyWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
   }

   public MethodWriterImpl getMethodWriter(ClassWriter var1) throws Exception {
      return new MethodWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
   }

   public ConstructorWriterImpl getConstructorWriter(ClassWriter var1) throws Exception {
      return new ConstructorWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
   }

   public MemberSummaryWriter getMemberSummaryWriter(ClassWriter var1, int var2) throws Exception {
      switch (var2) {
         case 0:
            return new NestedClassWriterImpl((SubWriterHolderWriter)var1, var1.getClassDoc());
         case 1:
            return this.getEnumConstantWriter(var1);
         case 2:
            return this.getFieldWriter(var1);
         case 3:
            return this.getConstructorWriter(var1);
         case 4:
            return this.getMethodWriter(var1);
         case 5:
         case 6:
         case 7:
         default:
            return null;
         case 8:
            return this.getPropertyWriter(var1);
      }
   }

   public MemberSummaryWriter getMemberSummaryWriter(AnnotationTypeWriter var1, int var2) throws Exception {
      switch (var2) {
         case 5:
            return (AnnotationTypeFieldWriterImpl)this.getAnnotationTypeFieldWriter(var1);
         case 6:
            return (AnnotationTypeOptionalMemberWriterImpl)this.getAnnotationTypeOptionalMemberWriter(var1);
         case 7:
            return (AnnotationTypeRequiredMemberWriterImpl)this.getAnnotationTypeRequiredMemberWriter(var1);
         default:
            return null;
      }
   }

   public SerializedFormWriter getSerializedFormWriter() throws Exception {
      return new SerializedFormWriterImpl(this.configuration);
   }
}
