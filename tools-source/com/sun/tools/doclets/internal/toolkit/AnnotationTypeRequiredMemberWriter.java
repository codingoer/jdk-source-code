package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import java.io.IOException;

public interface AnnotationTypeRequiredMemberWriter {
   Content getMemberTreeHeader();

   void addAnnotationDetailsMarker(Content var1);

   void addAnnotationDetailsTreeHeader(ClassDoc var1, Content var2);

   Content getAnnotationDocTreeHeader(MemberDoc var1, Content var2);

   Content getAnnotationDetails(Content var1);

   Content getAnnotationDoc(Content var1, boolean var2);

   Content getSignature(MemberDoc var1);

   void addDeprecated(MemberDoc var1, Content var2);

   void addComments(MemberDoc var1, Content var2);

   void addTags(MemberDoc var1, Content var2);

   void close() throws IOException;
}
