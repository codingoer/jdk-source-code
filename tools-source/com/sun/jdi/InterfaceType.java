package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface InterfaceType extends ReferenceType {
   List superinterfaces();

   List subinterfaces();

   List implementors();

   default Value invokeMethod(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
      throw new UnsupportedOperationException();
   }
}
