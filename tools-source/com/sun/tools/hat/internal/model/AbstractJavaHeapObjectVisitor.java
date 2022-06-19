package com.sun.tools.hat.internal.model;

public abstract class AbstractJavaHeapObjectVisitor implements JavaHeapObjectVisitor {
   public abstract void visit(JavaHeapObject var1);

   public boolean exclude(JavaClass var1, JavaField var2) {
      return false;
   }

   public boolean mightExclude() {
      return false;
   }
}
