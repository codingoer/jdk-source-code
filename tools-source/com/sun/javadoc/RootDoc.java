package com.sun.javadoc;

public interface RootDoc extends Doc, DocErrorReporter {
   String[][] options();

   PackageDoc[] specifiedPackages();

   ClassDoc[] specifiedClasses();

   ClassDoc[] classes();

   PackageDoc packageNamed(String var1);

   ClassDoc classNamed(String var1);
}
