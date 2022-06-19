package com.sun.tools.internal.xjc.reader.dtd;

import java.util.LinkedHashSet;
import java.util.Set;

final class Block {
   final boolean isOptional;
   final boolean isRepeated;
   final Set elements = new LinkedHashSet();

   Block(boolean optional, boolean repeated) {
      this.isOptional = optional;
      this.isRepeated = repeated;
   }
}
