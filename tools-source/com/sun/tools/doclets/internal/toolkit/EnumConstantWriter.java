package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import java.io.IOException;

public interface EnumConstantWriter {
   Content getEnumConstantsDetailsTreeHeader(ClassDoc var1, Content var2);

   Content getEnumConstantsTreeHeader(FieldDoc var1, Content var2);

   Content getSignature(FieldDoc var1);

   void addDeprecated(FieldDoc var1, Content var2);

   void addComments(FieldDoc var1, Content var2);

   void addTags(FieldDoc var1, Content var2);

   Content getEnumConstantsDetails(Content var1);

   Content getEnumConstants(Content var1, boolean var2);

   void close() throws IOException;
}
