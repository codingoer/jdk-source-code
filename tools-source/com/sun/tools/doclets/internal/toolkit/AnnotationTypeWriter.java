package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.AnnotationTypeDoc;
import java.io.IOException;

public interface AnnotationTypeWriter {
   Content getHeader(String var1);

   Content getAnnotationContentHeader();

   Content getAnnotationInfoTreeHeader();

   Content getAnnotationInfo(Content var1);

   void addAnnotationTypeSignature(String var1, Content var2);

   void addAnnotationTypeDescription(Content var1);

   void addAnnotationTypeTagInfo(Content var1);

   void addAnnotationTypeDeprecationInfo(Content var1);

   Content getMemberTreeHeader();

   Content getMemberTree(Content var1);

   Content getMemberSummaryTree(Content var1);

   Content getMemberDetailsTree(Content var1);

   void addFooter(Content var1);

   void printDocument(Content var1) throws IOException;

   void close() throws IOException;

   AnnotationTypeDoc getAnnotationTypeDoc();
}
