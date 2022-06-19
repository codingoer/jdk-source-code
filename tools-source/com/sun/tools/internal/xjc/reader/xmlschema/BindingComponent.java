package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.reader.Ring;

public abstract class BindingComponent {
   protected BindingComponent() {
      Ring.add(this);
   }

   protected final ErrorReporter getErrorReporter() {
      return (ErrorReporter)Ring.get(ErrorReporter.class);
   }

   protected final ClassSelector getClassSelector() {
      return (ClassSelector)Ring.get(ClassSelector.class);
   }
}
