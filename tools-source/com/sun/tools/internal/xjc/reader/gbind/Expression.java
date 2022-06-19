package com.sun.tools.internal.xjc.reader.gbind;

public abstract class Expression {
   public static final Expression EPSILON = new Expression() {
      ElementSet lastSet() {
         return ElementSet.EMPTY_SET;
      }

      boolean isNullable() {
         return true;
      }

      void buildDAG(ElementSet incoming) {
      }

      public String toString() {
         return "-";
      }
   };

   abstract ElementSet lastSet();

   abstract boolean isNullable();

   abstract void buildDAG(ElementSet var1);
}
