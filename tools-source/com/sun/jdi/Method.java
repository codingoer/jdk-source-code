package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface Method extends TypeComponent, Locatable, Comparable {
   String returnTypeName();

   Type returnType() throws ClassNotLoadedException;

   List argumentTypeNames();

   List argumentTypes() throws ClassNotLoadedException;

   boolean isAbstract();

   default boolean isDefault() {
      throw new UnsupportedOperationException();
   }

   boolean isSynchronized();

   boolean isNative();

   boolean isVarArgs();

   boolean isBridge();

   boolean isConstructor();

   boolean isStaticInitializer();

   boolean isObsolete();

   List allLineLocations() throws AbsentInformationException;

   List allLineLocations(String var1, String var2) throws AbsentInformationException;

   List locationsOfLine(int var1) throws AbsentInformationException;

   List locationsOfLine(String var1, String var2, int var3) throws AbsentInformationException;

   Location locationOfCodeIndex(long var1);

   List variables() throws AbsentInformationException;

   List variablesByName(String var1) throws AbsentInformationException;

   List arguments() throws AbsentInformationException;

   byte[] bytecodes();

   Location location();

   boolean equals(Object var1);

   int hashCode();
}
