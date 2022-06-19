package com.sun.javadoc;

public interface PackageDoc extends Doc {
   ClassDoc[] allClasses(boolean var1);

   ClassDoc[] allClasses();

   ClassDoc[] ordinaryClasses();

   ClassDoc[] exceptions();

   ClassDoc[] errors();

   ClassDoc[] enums();

   ClassDoc[] interfaces();

   AnnotationTypeDoc[] annotationTypes();

   AnnotationDesc[] annotations();

   ClassDoc findClass(String var1);
}
