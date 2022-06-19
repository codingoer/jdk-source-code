package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import java.io.IOException;
import java.util.List;

public interface MemberSummaryWriter {
   Content getMemberSummaryHeader(ClassDoc var1, Content var2);

   Content getSummaryTableTree(ClassDoc var1, List var2);

   void addMemberSummary(ClassDoc var1, ProgramElementDoc var2, Tag[] var3, List var4, int var5);

   Content getInheritedSummaryHeader(ClassDoc var1);

   void addInheritedMemberSummary(ClassDoc var1, ProgramElementDoc var2, boolean var3, boolean var4, Content var5);

   Content getInheritedSummaryLinksTree();

   Content getMemberTree(Content var1);

   void close() throws IOException;
}
