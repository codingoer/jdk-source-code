package com.sun.jdi;

import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public interface ReferenceType extends Type, Comparable, Accessible {
   String name();

   String genericSignature();

   ClassLoaderReference classLoader();

   String sourceName() throws AbsentInformationException;

   List sourceNames(String var1) throws AbsentInformationException;

   List sourcePaths(String var1) throws AbsentInformationException;

   String sourceDebugExtension() throws AbsentInformationException;

   boolean isStatic();

   boolean isAbstract();

   boolean isFinal();

   boolean isPrepared();

   boolean isVerified();

   boolean isInitialized();

   boolean failedToInitialize();

   List fields();

   List visibleFields();

   List allFields();

   Field fieldByName(String var1);

   List methods();

   List visibleMethods();

   List allMethods();

   List methodsByName(String var1);

   List methodsByName(String var1, String var2);

   List nestedTypes();

   Value getValue(Field var1);

   Map getValues(List var1);

   ClassObjectReference classObject();

   List allLineLocations() throws AbsentInformationException;

   List allLineLocations(String var1, String var2) throws AbsentInformationException;

   List locationsOfLine(int var1) throws AbsentInformationException;

   List locationsOfLine(String var1, String var2, int var3) throws AbsentInformationException;

   List availableStrata();

   String defaultStratum();

   List instances(long var1);

   boolean equals(Object var1);

   int hashCode();

   int majorVersion();

   int minorVersion();

   int constantPoolCount();

   byte[] constantPool();
}
