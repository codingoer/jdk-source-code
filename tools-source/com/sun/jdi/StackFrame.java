package com.sun.jdi;

import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public interface StackFrame extends Mirror, Locatable {
   Location location();

   ThreadReference thread();

   ObjectReference thisObject();

   List visibleVariables() throws AbsentInformationException;

   LocalVariable visibleVariableByName(String var1) throws AbsentInformationException;

   Value getValue(LocalVariable var1);

   Map getValues(List var1);

   void setValue(LocalVariable var1, Value var2) throws InvalidTypeException, ClassNotLoadedException;

   List getArgumentValues();
}
