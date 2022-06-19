package com.sun.tools.internal.xjc.reader.gbind;

public final class SinkNode extends Element {
   public String toString() {
      return "#sink";
   }

   boolean isSink() {
      return true;
   }
}
