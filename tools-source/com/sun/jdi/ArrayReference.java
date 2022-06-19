package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface ArrayReference extends ObjectReference {
   int length();

   Value getValue(int var1);

   List getValues();

   List getValues(int var1, int var2);

   void setValue(int var1, Value var2) throws InvalidTypeException, ClassNotLoadedException;

   void setValues(List var1) throws InvalidTypeException, ClassNotLoadedException;

   void setValues(int var1, List var2, int var3, int var4) throws InvalidTypeException, ClassNotLoadedException;
}
