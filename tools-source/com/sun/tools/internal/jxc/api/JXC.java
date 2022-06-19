package com.sun.tools.internal.jxc.api;

import com.sun.tools.internal.jxc.api.impl.j2s.JavaCompilerImpl;
import com.sun.tools.internal.xjc.api.JavaCompiler;

public class JXC {
   public static JavaCompiler createJavaCompiler() {
      return new JavaCompilerImpl();
   }
}
