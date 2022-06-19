package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import java.io.IOException;

public interface MethodWriter {
   Content getMethodDetailsTreeHeader(ClassDoc var1, Content var2);

   Content getMethodDocTreeHeader(MethodDoc var1, Content var2);

   Content getSignature(MethodDoc var1);

   void addDeprecated(MethodDoc var1, Content var2);

   void addComments(Type var1, MethodDoc var2, Content var3);

   void addTags(MethodDoc var1, Content var2);

   Content getMethodDetails(Content var1);

   Content getMethodDoc(Content var1, boolean var2);

   void close() throws IOException;
}
