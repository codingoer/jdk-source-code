package com.sun.javadoc;

public interface ClassDoc extends ProgramElementDoc, Type {
   boolean isAbstract();

   boolean isSerializable();

   boolean isExternalizable();

   MethodDoc[] serializationMethods();

   FieldDoc[] serializableFields();

   boolean definesSerializableFields();

   ClassDoc superclass();

   Type superclassType();

   boolean subclassOf(ClassDoc var1);

   ClassDoc[] interfaces();

   Type[] interfaceTypes();

   TypeVariable[] typeParameters();

   ParamTag[] typeParamTags();

   FieldDoc[] fields();

   FieldDoc[] fields(boolean var1);

   FieldDoc[] enumConstants();

   MethodDoc[] methods();

   MethodDoc[] methods(boolean var1);

   ConstructorDoc[] constructors();

   ConstructorDoc[] constructors(boolean var1);

   ClassDoc[] innerClasses();

   ClassDoc[] innerClasses(boolean var1);

   ClassDoc findClass(String var1);

   /** @deprecated */
   @Deprecated
   ClassDoc[] importedClasses();

   /** @deprecated */
   @Deprecated
   PackageDoc[] importedPackages();
}
