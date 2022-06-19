package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import java.io.IOException;

public interface ProfileSummaryWriter {
   Content getProfileHeader(String var1);

   Content getContentHeader();

   Content getSummaryHeader();

   Content getSummaryTree(Content var1);

   Content getPackageSummaryHeader(PackageDoc var1);

   Content getPackageSummaryTree(Content var1);

   void addClassesSummary(ClassDoc[] var1, String var2, String var3, String[] var4, Content var5);

   void addProfileFooter(Content var1);

   void printDocument(Content var1) throws IOException;

   void close() throws IOException;
}
