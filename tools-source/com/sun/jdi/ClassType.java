package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface ClassType extends ReferenceType {
   int INVOKE_SINGLE_THREADED = 1;

   ClassType superclass();

   List interfaces();

   List allInterfaces();

   List subclasses();

   boolean isEnum();

   void setValue(Field var1, Value var2) throws InvalidTypeException, ClassNotLoadedException;

   Value invokeMethod(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException;

   ObjectReference newInstance(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException;

   Method concreteMethodByName(String var1, String var2);
}
