package com.sun.tools.corba.se.idl;

public interface GenFactory {
   AttributeGen createAttributeGen();

   ConstGen createConstGen();

   EnumGen createEnumGen();

   ExceptionGen createExceptionGen();

   ForwardGen createForwardGen();

   ForwardValueGen createForwardValueGen();

   IncludeGen createIncludeGen();

   InterfaceGen createInterfaceGen();

   ValueGen createValueGen();

   ValueBoxGen createValueBoxGen();

   MethodGen createMethodGen();

   ModuleGen createModuleGen();

   NativeGen createNativeGen();

   ParameterGen createParameterGen();

   PragmaGen createPragmaGen();

   PrimitiveGen createPrimitiveGen();

   SequenceGen createSequenceGen();

   StringGen createStringGen();

   StructGen createStructGen();

   TypedefGen createTypedefGen();

   UnionGen createUnionGen();
}
