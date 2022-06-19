package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ConstantsSummaryWriter {
   void close() throws IOException;

   Content getHeader();

   Content getContentsHeader();

   void addLinkToPackageContent(PackageDoc var1, String var2, Set var3, Content var4);

   Content getContentsList(Content var1);

   Content getConstantSummaries();

   void addPackageName(PackageDoc var1, String var2, Content var3);

   Content getClassConstantHeader();

   void addConstantMembers(ClassDoc var1, List var2, Content var3);

   void addFooter(Content var1);

   void printDocument(Content var1) throws IOException;
}
