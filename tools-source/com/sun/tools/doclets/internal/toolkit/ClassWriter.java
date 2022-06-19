package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import java.io.IOException;

public interface ClassWriter {
   Content getHeader(String var1);

   Content getClassContentHeader();

   void addClassTree(Content var1);

   Content getClassInfoTreeHeader();

   void addTypeParamInfo(Content var1);

   void addSuperInterfacesInfo(Content var1);

   void addImplementedInterfacesInfo(Content var1);

   void addSubClassInfo(Content var1);

   void addSubInterfacesInfo(Content var1);

   void addInterfaceUsageInfo(Content var1);

   void addFunctionalInterfaceInfo(Content var1);

   void addNestedClassInfo(Content var1);

   Content getClassInfo(Content var1);

   void addClassDeprecationInfo(Content var1);

   void addClassSignature(String var1, Content var2);

   void addClassDescription(Content var1);

   void addClassTagInfo(Content var1);

   Content getMemberTreeHeader();

   void addFooter(Content var1);

   void printDocument(Content var1) throws IOException;

   void close() throws IOException;

   ClassDoc getClassDoc();

   Content getMemberSummaryTree(Content var1);

   Content getMemberDetailsTree(Content var1);
}
