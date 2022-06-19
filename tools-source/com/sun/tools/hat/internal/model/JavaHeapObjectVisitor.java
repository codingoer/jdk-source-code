package com.sun.tools.hat.internal.model;

public interface JavaHeapObjectVisitor {
   void visit(JavaHeapObject var1);

   boolean exclude(JavaClass var1, JavaField var2);

   boolean mightExclude();
}
