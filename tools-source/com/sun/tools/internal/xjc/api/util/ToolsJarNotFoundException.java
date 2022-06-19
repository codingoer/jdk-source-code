package com.sun.tools.internal.xjc.api.util;

import java.io.File;

public final class ToolsJarNotFoundException extends Exception {
   public final File toolsJar;

   public ToolsJarNotFoundException(File toolsJar) {
      super(calcMessage(toolsJar));
      this.toolsJar = toolsJar;
   }

   private static String calcMessage(File toolsJar) {
      return Messages.TOOLS_JAR_NOT_FOUND.format(toolsJar.getPath());
   }
}
