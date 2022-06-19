package com.sun.source.util;

import jdk.Exported;

@Exported
public interface Plugin {
   String getName();

   void init(JavacTask var1, String... var2);
}
