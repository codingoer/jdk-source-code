package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import java.io.IOException;

public interface PackageSummaryWriter {
   Content getPackageHeader(String var1);

   Content getContentHeader();

   Content getSummaryHeader();

   void addClassesSummary(ClassDoc[] var1, String var2, String var3, String[] var4, Content var5);

   void addPackageDescription(Content var1);

   void addPackageTags(Content var1);

   void addPackageFooter(Content var1);

   void printDocument(Content var1) throws IOException;

   void close() throws IOException;
}
