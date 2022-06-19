package com.sun.tools.internal.ws;

import com.sun.xml.internal.ws.util.Version;

public abstract class ToolVersion {
   public static final Version VERSION = Version.create(ToolVersion.class.getResourceAsStream("version.properties"));

   private ToolVersion() {
   }
}
