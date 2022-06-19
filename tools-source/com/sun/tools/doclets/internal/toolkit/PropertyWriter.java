package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import java.io.IOException;

public interface PropertyWriter {
   Content getPropertyDetailsTreeHeader(ClassDoc var1, Content var2);

   Content getPropertyDocTreeHeader(MethodDoc var1, Content var2);

   Content getSignature(MethodDoc var1);

   void addDeprecated(MethodDoc var1, Content var2);

   void addComments(MethodDoc var1, Content var2);

   void addTags(MethodDoc var1, Content var2);

   Content getPropertyDetails(Content var1);

   Content getPropertyDoc(Content var1, boolean var2);

   void close() throws IOException;
}
