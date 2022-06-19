package com.sun.tools.corba.se.idl;

public class DefaultSymtabFactory implements SymtabFactory {
   public AttributeEntry attributeEntry() {
      return new AttributeEntry();
   }

   public AttributeEntry attributeEntry(InterfaceEntry var1, IDLID var2) {
      return new AttributeEntry(var1, var2);
   }

   public ConstEntry constEntry() {
      return new ConstEntry();
   }

   public ConstEntry constEntry(SymtabEntry var1, IDLID var2) {
      return new ConstEntry(var1, var2);
   }

   public NativeEntry nativeEntry() {
      return new NativeEntry();
   }

   public NativeEntry nativeEntry(SymtabEntry var1, IDLID var2) {
      return new NativeEntry(var1, var2);
   }

   public EnumEntry enumEntry() {
      return new EnumEntry();
   }

   public EnumEntry enumEntry(SymtabEntry var1, IDLID var2) {
      return new EnumEntry(var1, var2);
   }

   public ExceptionEntry exceptionEntry() {
      return new ExceptionEntry();
   }

   public ExceptionEntry exceptionEntry(SymtabEntry var1, IDLID var2) {
      return new ExceptionEntry(var1, var2);
   }

   public ForwardEntry forwardEntry() {
      return new ForwardEntry();
   }

   public ForwardEntry forwardEntry(ModuleEntry var1, IDLID var2) {
      return new ForwardEntry(var1, var2);
   }

   public ForwardValueEntry forwardValueEntry() {
      return new ForwardValueEntry();
   }

   public ForwardValueEntry forwardValueEntry(ModuleEntry var1, IDLID var2) {
      return new ForwardValueEntry(var1, var2);
   }

   public IncludeEntry includeEntry() {
      return new IncludeEntry();
   }

   public IncludeEntry includeEntry(SymtabEntry var1) {
      return new IncludeEntry(var1);
   }

   public InterfaceEntry interfaceEntry() {
      return new InterfaceEntry();
   }

   public InterfaceEntry interfaceEntry(ModuleEntry var1, IDLID var2) {
      return new InterfaceEntry(var1, var2);
   }

   public ValueEntry valueEntry() {
      return new ValueEntry();
   }

   public ValueEntry valueEntry(ModuleEntry var1, IDLID var2) {
      return new ValueEntry(var1, var2);
   }

   public ValueBoxEntry valueBoxEntry() {
      return new ValueBoxEntry();
   }

   public ValueBoxEntry valueBoxEntry(ModuleEntry var1, IDLID var2) {
      return new ValueBoxEntry(var1, var2);
   }

   public MethodEntry methodEntry() {
      return new MethodEntry();
   }

   public MethodEntry methodEntry(InterfaceEntry var1, IDLID var2) {
      return new MethodEntry(var1, var2);
   }

   public ModuleEntry moduleEntry() {
      return new ModuleEntry();
   }

   public ModuleEntry moduleEntry(ModuleEntry var1, IDLID var2) {
      return new ModuleEntry(var1, var2);
   }

   public ParameterEntry parameterEntry() {
      return new ParameterEntry();
   }

   public ParameterEntry parameterEntry(MethodEntry var1, IDLID var2) {
      return new ParameterEntry(var1, var2);
   }

   public PragmaEntry pragmaEntry() {
      return new PragmaEntry();
   }

   public PragmaEntry pragmaEntry(SymtabEntry var1) {
      return new PragmaEntry(var1);
   }

   public PrimitiveEntry primitiveEntry() {
      return new PrimitiveEntry();
   }

   public PrimitiveEntry primitiveEntry(String var1) {
      return new PrimitiveEntry(var1);
   }

   public SequenceEntry sequenceEntry() {
      return new SequenceEntry();
   }

   public SequenceEntry sequenceEntry(SymtabEntry var1, IDLID var2) {
      return new SequenceEntry(var1, var2);
   }

   public StringEntry stringEntry() {
      return new StringEntry();
   }

   public StructEntry structEntry() {
      return new StructEntry();
   }

   public StructEntry structEntry(SymtabEntry var1, IDLID var2) {
      return new StructEntry(var1, var2);
   }

   public TypedefEntry typedefEntry() {
      return new TypedefEntry();
   }

   public TypedefEntry typedefEntry(SymtabEntry var1, IDLID var2) {
      return new TypedefEntry(var1, var2);
   }

   public UnionEntry unionEntry() {
      return new UnionEntry();
   }

   public UnionEntry unionEntry(SymtabEntry var1, IDLID var2) {
      return new UnionEntry(var1, var2);
   }
}
