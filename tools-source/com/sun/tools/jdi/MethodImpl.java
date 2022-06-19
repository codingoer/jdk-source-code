package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MethodImpl extends TypeComponentImpl implements Method {
   private JNITypeParser signatureParser;
   ReturnContainer retValContainer = null;

   abstract int argSlotCount() throws AbsentInformationException;

   abstract List allLineLocations(SDE.Stratum var1, String var2) throws AbsentInformationException;

   abstract List locationsOfLine(SDE.Stratum var1, String var2, int var3) throws AbsentInformationException;

   MethodImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, String var5, String var6, String var7, int var8) {
      super(var1, var2, var3, var5, var6, var7, var8);
      this.signatureParser = new JNITypeParser(var6);
   }

   static MethodImpl createMethodImpl(VirtualMachine var0, ReferenceTypeImpl var1, long var2, String var4, String var5, String var6, int var7) {
      return (MethodImpl)((var7 & 1280) != 0 ? new NonConcreteMethodImpl(var0, var1, var2, var4, var5, var6, var7) : new ConcreteMethodImpl(var0, var1, var2, var4, var5, var6, var7));
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof MethodImpl) {
         MethodImpl var2 = (MethodImpl)var1;
         return this.declaringType().equals(var2.declaringType()) && this.ref() == var2.ref() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)this.ref();
   }

   public final List allLineLocations() throws AbsentInformationException {
      return this.allLineLocations((String)this.vm.getDefaultStratum(), (String)null);
   }

   public List allLineLocations(String var1, String var2) throws AbsentInformationException {
      return this.allLineLocations(this.declaringType.stratum(var1), var2);
   }

   public final List locationsOfLine(int var1) throws AbsentInformationException {
      return this.locationsOfLine((String)this.vm.getDefaultStratum(), (String)null, var1);
   }

   public List locationsOfLine(String var1, String var2, int var3) throws AbsentInformationException {
      return this.locationsOfLine(this.declaringType.stratum(var1), var2, var3);
   }

   LineInfo codeIndexToLineInfo(SDE.Stratum var1, long var2) {
      return (LineInfo)(var1.isJava() ? new BaseLineInfo(-1, this.declaringType) : new StratumLineInfo(var1.id(), -1, (String)null, (String)null));
   }

   public String returnTypeName() {
      return this.signatureParser.typeName();
   }

   private String returnSignature() {
      return this.signatureParser.signature();
   }

   public Type returnType() throws ClassNotLoadedException {
      return this.findType(this.returnSignature());
   }

   public Type findType(String var1) throws ClassNotLoadedException {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.declaringType();
      return var2.findType(var1);
   }

   public List argumentTypeNames() {
      return this.signatureParser.argumentTypeNames();
   }

   public List argumentSignatures() {
      return this.signatureParser.argumentSignatures();
   }

   Type argumentType(int var1) throws ClassNotLoadedException {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.declaringType();
      String var3 = (String)this.argumentSignatures().get(var1);
      return var2.findType(var3);
   }

   public List argumentTypes() throws ClassNotLoadedException {
      int var1 = this.argumentSignatures().size();
      ArrayList var2 = new ArrayList(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         Type var4 = this.argumentType(var3);
         var2.add(var4);
      }

      return var2;
   }

   public int compareTo(Method var1) {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.declaringType();
      int var3 = var2.compareTo(var1.declaringType());
      if (var3 == 0) {
         var3 = var2.indexOf((Method)this) - var2.indexOf(var1);
      }

      return var3;
   }

   public boolean isAbstract() {
      return this.isModifierSet(1024);
   }

   public boolean isDefault() {
      return !this.isModifierSet(1024) && !this.isModifierSet(8) && !this.isModifierSet(2) && this.declaringType() instanceof InterfaceType;
   }

   public boolean isSynchronized() {
      return this.isModifierSet(32);
   }

   public boolean isNative() {
      return this.isModifierSet(256);
   }

   public boolean isVarArgs() {
      return this.isModifierSet(128);
   }

   public boolean isBridge() {
      return this.isModifierSet(64);
   }

   public boolean isConstructor() {
      return this.name().equals("<init>");
   }

   public boolean isStaticInitializer() {
      return this.name().equals("<clinit>");
   }

   public boolean isObsolete() {
      try {
         return JDWP.Method.IsObsolete.process(this.vm, this.declaringType, this.ref).isObsolete;
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   ReturnContainer getReturnValueContainer() {
      if (this.retValContainer == null) {
         this.retValContainer = new ReturnContainer();
      }

      return this.retValContainer;
   }

   void handleVarArgs(List var1) throws ClassNotLoadedException, InvalidTypeException {
      List var2 = this.argumentTypes();
      ArrayType var3 = (ArrayType)var2.get(var2.size() - 1);
      Type var4 = var3.componentType();
      int var5 = var1.size();
      int var6 = var2.size();
      if (var5 >= var6 - 1) {
         if (var5 == var6 - 1) {
            ArrayReference var12 = var3.newInstance(0);
            var1.add(var12);
         } else {
            Value var7 = (Value)var1.get(var6 - 1);
            if (var7 != null || var5 != var6) {
               Type var8 = var7 == null ? null : var7.type();
               if (!(var8 instanceof ArrayTypeImpl) || var5 != var6 || !((ArrayTypeImpl)var8).isAssignableTo(var3)) {
                  int var9 = var5 - var6 + 1;
                  ArrayReference var10 = var3.newInstance(var9);
                  var10.setValues(0, var1, var6 - 1, var9);
                  var1.set(var6 - 1, var10);

                  for(int var11 = var6; var11 < var5; ++var11) {
                     var1.remove(var6);
                  }

               }
            }
         }
      }
   }

   List validateAndPrepareArgumentsForInvoke(List var1) throws ClassNotLoadedException, InvalidTypeException {
      ArrayList var2 = new ArrayList(var1);
      if (this.isVarArgs()) {
         this.handleVarArgs(var2);
      }

      int var3 = var2.size();
      JNITypeParser var4 = new JNITypeParser(this.signature());
      List var5 = var4.argumentSignatures();
      if (var5.size() != var3) {
         throw new IllegalArgumentException("Invalid argument count: expected " + var5.size() + ", received " + var2.size());
      } else {
         for(int var6 = 0; var6 < var3; ++var6) {
            Value var7 = (Value)var2.get(var6);
            ValueImpl var8 = ValueImpl.prepareForAssignment(var7, new ArgumentContainer(var6));
            var2.set(var6, var8);
         }

         return var2;
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.declaringType().name());
      var1.append(".");
      var1.append(this.name());
      var1.append("(");
      boolean var2 = true;

      for(Iterator var3 = this.argumentTypeNames().iterator(); var3.hasNext(); var2 = false) {
         String var4 = (String)var3.next();
         if (!var2) {
            var1.append(", ");
         }

         var1.append(var4);
      }

      var1.append(")");
      return var1.toString();
   }

   class ArgumentContainer implements ValueContainer {
      int index;

      ArgumentContainer(int var2) {
         this.index = var2;
      }

      public Type type() throws ClassNotLoadedException {
         return MethodImpl.this.argumentType(this.index);
      }

      public String typeName() {
         return (String)MethodImpl.this.argumentTypeNames().get(this.index);
      }

      public String signature() {
         return (String)MethodImpl.this.argumentSignatures().get(this.index);
      }

      public Type findType(String var1) throws ClassNotLoadedException {
         return MethodImpl.this.findType(var1);
      }
   }

   class ReturnContainer implements ValueContainer {
      public Type type() throws ClassNotLoadedException {
         return MethodImpl.this.returnType();
      }

      public String typeName() {
         return MethodImpl.this.returnTypeName();
      }

      public String signature() {
         return MethodImpl.this.returnSignature();
      }

      public Type findType(String var1) throws ClassNotLoadedException {
         return MethodImpl.this.findType(var1);
      }
   }
}
