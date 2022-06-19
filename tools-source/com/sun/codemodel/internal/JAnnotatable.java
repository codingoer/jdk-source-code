package com.sun.codemodel.internal;

import java.util.Collection;

public interface JAnnotatable {
   JAnnotationUse annotate(JClass var1);

   JAnnotationUse annotate(Class var1);

   JAnnotationWriter annotate2(Class var1);

   Collection annotations();
}
