package com.sun.jdi;

import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public interface ObjectReference extends Value {
   int INVOKE_SINGLE_THREADED = 1;
   int INVOKE_NONVIRTUAL = 2;

   ReferenceType referenceType();

   Value getValue(Field var1);

   Map getValues(List var1);

   void setValue(Field var1, Value var2) throws InvalidTypeException, ClassNotLoadedException;

   Value invokeMethod(ThreadReference var1, Method var2, List var3, int var4) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException;

   void disableCollection();

   void enableCollection();

   boolean isCollected();

   long uniqueID();

   List waitingThreads() throws IncompatibleThreadStateException;

   ThreadReference owningThread() throws IncompatibleThreadStateException;

   int entryCount() throws IncompatibleThreadStateException;

   List referringObjects(long var1);

   boolean equals(Object var1);

   int hashCode();
}
