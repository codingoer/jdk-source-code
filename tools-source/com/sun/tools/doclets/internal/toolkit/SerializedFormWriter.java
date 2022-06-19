package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SerialFieldTag;
import java.io.IOException;

public interface SerializedFormWriter {
   Content getHeader(String var1);

   Content getSerializedSummariesHeader();

   Content getPackageSerializedHeader();

   Content getPackageHeader(String var1);

   Content getClassSerializedHeader();

   Content getClassHeader(ClassDoc var1);

   Content getSerialUIDInfoHeader();

   void addSerialUIDInfo(String var1, String var2, Content var3);

   Content getClassContentHeader();

   SerialFieldWriter getSerialFieldWriter(ClassDoc var1);

   SerialMethodWriter getSerialMethodWriter(ClassDoc var1);

   void close() throws IOException;

   Content getSerializedContent(Content var1);

   void addFooter(Content var1);

   void printDocument(Content var1) throws IOException;

   public interface SerialMethodWriter {
      Content getSerializableMethodsHeader();

      Content getMethodsContentHeader(boolean var1);

      Content getSerializableMethods(String var1, Content var2);

      Content getNoCustomizationMsg(String var1);

      void addMemberHeader(MethodDoc var1, Content var2);

      void addDeprecatedMemberInfo(MethodDoc var1, Content var2);

      void addMemberDescription(MethodDoc var1, Content var2);

      void addMemberTags(MethodDoc var1, Content var2);
   }

   public interface SerialFieldWriter {
      Content getSerializableFieldsHeader();

      Content getFieldsContentHeader(boolean var1);

      Content getSerializableFields(String var1, Content var2);

      void addMemberDeprecatedInfo(FieldDoc var1, Content var2);

      void addMemberDescription(FieldDoc var1, Content var2);

      void addMemberDescription(SerialFieldTag var1, Content var2);

      void addMemberTags(FieldDoc var1, Content var2);

      void addMemberHeader(ClassDoc var1, String var2, String var3, String var4, Content var5);

      boolean shouldPrintOverview(FieldDoc var1);
   }
}
