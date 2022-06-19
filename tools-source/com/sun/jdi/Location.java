package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Location extends Mirror, Comparable {
   ReferenceType declaringType();

   Method method();

   long codeIndex();

   String sourceName() throws AbsentInformationException;

   String sourceName(String var1) throws AbsentInformationException;

   String sourcePath() throws AbsentInformationException;

   String sourcePath(String var1) throws AbsentInformationException;

   int lineNumber();

   int lineNumber(String var1);

   boolean equals(Object var1);

   int hashCode();
}
