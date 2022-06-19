package com.sun.tools.corba.se.idl;

public interface SymtabFactory {
   AttributeEntry attributeEntry();

   AttributeEntry attributeEntry(InterfaceEntry var1, IDLID var2);

   ConstEntry constEntry();

   ConstEntry constEntry(SymtabEntry var1, IDLID var2);

   NativeEntry nativeEntry();

   NativeEntry nativeEntry(SymtabEntry var1, IDLID var2);

   EnumEntry enumEntry();

   EnumEntry enumEntry(SymtabEntry var1, IDLID var2);

   ExceptionEntry exceptionEntry();

   ExceptionEntry exceptionEntry(SymtabEntry var1, IDLID var2);

   ForwardEntry forwardEntry();

   ForwardEntry forwardEntry(ModuleEntry var1, IDLID var2);

   ForwardValueEntry forwardValueEntry();

   ForwardValueEntry forwardValueEntry(ModuleEntry var1, IDLID var2);

   IncludeEntry includeEntry();

   IncludeEntry includeEntry(SymtabEntry var1);

   InterfaceEntry interfaceEntry();

   InterfaceEntry interfaceEntry(ModuleEntry var1, IDLID var2);

   ValueEntry valueEntry();

   ValueEntry valueEntry(ModuleEntry var1, IDLID var2);

   ValueBoxEntry valueBoxEntry();

   ValueBoxEntry valueBoxEntry(ModuleEntry var1, IDLID var2);

   MethodEntry methodEntry();

   MethodEntry methodEntry(InterfaceEntry var1, IDLID var2);

   ModuleEntry moduleEntry();

   ModuleEntry moduleEntry(ModuleEntry var1, IDLID var2);

   ParameterEntry parameterEntry();

   ParameterEntry parameterEntry(MethodEntry var1, IDLID var2);

   PragmaEntry pragmaEntry();

   PragmaEntry pragmaEntry(SymtabEntry var1);

   PrimitiveEntry primitiveEntry();

   PrimitiveEntry primitiveEntry(String var1);

   SequenceEntry sequenceEntry();

   SequenceEntry sequenceEntry(SymtabEntry var1, IDLID var2);

   StringEntry stringEntry();

   StructEntry structEntry();

   StructEntry structEntry(SymtabEntry var1, IDLID var2);

   TypedefEntry typedefEntry();

   TypedefEntry typedefEntry(SymtabEntry var1, IDLID var2);

   UnionEntry unionEntry();

   UnionEntry unionEntry(SymtabEntry var1, IDLID var2);
}
