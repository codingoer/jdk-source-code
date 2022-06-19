package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.javac.jvm.Profile;

public interface WriterFactory {
   ConstantsSummaryWriter getConstantsSummaryWriter() throws Exception;

   PackageSummaryWriter getPackageSummaryWriter(PackageDoc var1, PackageDoc var2, PackageDoc var3) throws Exception;

   ProfileSummaryWriter getProfileSummaryWriter(Profile var1, Profile var2, Profile var3) throws Exception;

   ProfilePackageSummaryWriter getProfilePackageSummaryWriter(PackageDoc var1, PackageDoc var2, PackageDoc var3, Profile var4) throws Exception;

   ClassWriter getClassWriter(ClassDoc var1, ClassDoc var2, ClassDoc var3, ClassTree var4) throws Exception;

   AnnotationTypeWriter getAnnotationTypeWriter(AnnotationTypeDoc var1, Type var2, Type var3) throws Exception;

   MethodWriter getMethodWriter(ClassWriter var1) throws Exception;

   AnnotationTypeFieldWriter getAnnotationTypeFieldWriter(AnnotationTypeWriter var1) throws Exception;

   AnnotationTypeOptionalMemberWriter getAnnotationTypeOptionalMemberWriter(AnnotationTypeWriter var1) throws Exception;

   AnnotationTypeRequiredMemberWriter getAnnotationTypeRequiredMemberWriter(AnnotationTypeWriter var1) throws Exception;

   EnumConstantWriter getEnumConstantWriter(ClassWriter var1) throws Exception;

   FieldWriter getFieldWriter(ClassWriter var1) throws Exception;

   PropertyWriter getPropertyWriter(ClassWriter var1) throws Exception;

   ConstructorWriter getConstructorWriter(ClassWriter var1) throws Exception;

   MemberSummaryWriter getMemberSummaryWriter(ClassWriter var1, int var2) throws Exception;

   MemberSummaryWriter getMemberSummaryWriter(AnnotationTypeWriter var1, int var2) throws Exception;

   SerializedFormWriter getSerializedFormWriter() throws Exception;
}
