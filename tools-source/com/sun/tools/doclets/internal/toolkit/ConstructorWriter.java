package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import java.io.IOException;

public interface ConstructorWriter {
   Content getConstructorDetailsTreeHeader(ClassDoc var1, Content var2);

   Content getConstructorDocTreeHeader(ConstructorDoc var1, Content var2);

   Content getSignature(ConstructorDoc var1);

   void addDeprecated(ConstructorDoc var1, Content var2);

   void addComments(ConstructorDoc var1, Content var2);

   void addTags(ConstructorDoc var1, Content var2);

   Content getConstructorDetails(Content var1);

   Content getConstructorDoc(Content var1, boolean var2);

   void setFoundNonPubConstructor(boolean var1);

   void close() throws IOException;
}
