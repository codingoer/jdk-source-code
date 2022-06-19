package com.sun.tools.internal.xjc.reader.dtd;

import java.util.List;

abstract class Term {
   static final Term EMPTY = new Term() {
      void normalize(List r, boolean optional) {
      }

      void addAllElements(Block b) {
      }

      boolean isOptional() {
         return false;
      }

      boolean isRepeated() {
         return false;
      }
   };

   abstract void normalize(List var1, boolean var2);

   abstract void addAllElements(Block var1);

   abstract boolean isOptional();

   abstract boolean isRepeated();
}
