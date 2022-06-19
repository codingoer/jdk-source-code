package com.sun.tools.example.debug.expr;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.LongValue;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

abstract class LValue {
   protected Value jdiValue;
   static final int STATIC = 0;
   static final int INSTANCE = 1;
   static List primitiveTypeNames = new ArrayList();
   static final int SAME = 0;
   static final int ASSIGNABLE = 1;
   static final int DIFFERENT = 2;

   abstract Value getValue() throws InvocationException, IncompatibleThreadStateException, InvalidTypeException, ClassNotLoadedException, ParseException;

   abstract void setValue0(Value var1) throws ParseException, InvalidTypeException, ClassNotLoadedException;

   abstract void invokeWith(List var1) throws ParseException;

   void setValue(Value var1) throws ParseException {
      try {
         this.setValue0(var1);
      } catch (InvalidTypeException var3) {
         throw new ParseException("Attempt to set value of incorrect type" + var3);
      } catch (ClassNotLoadedException var4) {
         throw new ParseException("Attempt to set value before " + var4.className() + " was loaded" + var4);
      }
   }

   void setValue(LValue var1) throws ParseException {
      this.setValue(var1.interiorGetValue());
   }

   LValue memberLValue(ExpressionParser.GetFrame var1, String var2) throws ParseException {
      try {
         return this.memberLValue(var2, var1.get().thread());
      } catch (IncompatibleThreadStateException var4) {
         throw new ParseException("Thread not suspended");
      }
   }

   LValue memberLValue(String var1, ThreadReference var2) throws ParseException {
      Value var3 = this.interiorGetValue();
      return (LValue)(var3 instanceof ArrayReference && "length".equals(var1) ? new LValueArrayLength((ArrayReference)var3) : new LValueInstanceMember(var3, var1, var2));
   }

   Value getMassagedValue(ExpressionParser.GetFrame var1) throws ParseException {
      Value var2 = this.interiorGetValue();
      if (var2 instanceof ObjectReference && !(var2 instanceof StringReference) && !(var2 instanceof ArrayReference)) {
         StackFrame var3;
         try {
            var3 = var1.get();
         } catch (IncompatibleThreadStateException var6) {
            throw new ParseException("Thread not suspended");
         }

         ThreadReference var4 = var3.thread();
         LValue var5 = this.memberLValue("toString", var4);
         var5.invokeWith(new ArrayList());
         return var5.interiorGetValue();
      } else {
         return var2;
      }
   }

   Value interiorGetValue() throws ParseException {
      try {
         Value var1 = this.getValue();
         return var1;
      } catch (InvocationException var3) {
         throw new ParseException("Unable to complete expression. Exception " + var3.exception() + " thrown");
      } catch (IncompatibleThreadStateException var4) {
         throw new ParseException("Unable to complete expression. Thread not suspended for method invoke");
      } catch (InvalidTypeException var5) {
         throw new ParseException("Unable to complete expression. Method argument type mismatch");
      } catch (ClassNotLoadedException var6) {
         throw new ParseException("Unable to complete expression. Method argument type " + var6.className() + " not yet loaded");
      }
   }

   LValue arrayElementLValue(LValue var1) throws ParseException {
      Value var2 = var1.interiorGetValue();
      if (!(var2 instanceof IntegerValue) && !(var2 instanceof ShortValue) && !(var2 instanceof ByteValue) && !(var2 instanceof CharValue)) {
         throw new ParseException("Array index must be a integer type");
      } else {
         int var3 = ((PrimitiveValue)var2).intValue();
         return new LValueArrayElement(this.interiorGetValue(), var3);
      }
   }

   public String toString() {
      try {
         return this.interiorGetValue().toString();
      } catch (ParseException var2) {
         return "<Parse Exception>";
      }
   }

   static Field fieldByName(ReferenceType var0, String var1, int var2) {
      Field var3 = var0.fieldByName(var1);
      if (var3 != null) {
         boolean var4 = var3.isStatic();
         if (var2 == 0 && !var4 || var2 == 1 && var4) {
            var3 = null;
         }
      }

      return var3;
   }

   static List methodsByName(ReferenceType var0, String var1, int var2) {
      List var3 = var0.methodsByName(var1);
      Iterator var4 = var3.iterator();

      while(true) {
         boolean var6;
         do {
            if (!var4.hasNext()) {
               return var3;
            }

            Method var5 = (Method)var4.next();
            var6 = var5.isStatic();
         } while((var2 != 0 || var6) && (var2 != 1 || !var6));

         var4.remove();
      }
   }

   static int argumentsMatch(List var0, List var1) {
      if (var0.size() != var1.size()) {
         return 2;
      } else {
         Iterator var2 = var0.iterator();
         Iterator var3 = var1.iterator();
         byte var4 = 0;

         while(var2.hasNext()) {
            Type var5 = (Type)var2.next();
            Value var6 = (Value)var3.next();
            if (var6 == null && primitiveTypeNames.contains(var5.name())) {
               return 2;
            }

            if (!var6.type().equals(var5)) {
               if (!isAssignableTo(var6.type(), var5)) {
                  return 2;
               }

               var4 = 1;
            }
         }

         return var4;
      }
   }

   static boolean isComponentAssignable(Type var0, Type var1) {
      if (var0 instanceof PrimitiveType) {
         return var0.equals(var1);
      } else {
         return var1 instanceof PrimitiveType ? false : isAssignableTo(var0, var1);
      }
   }

   static boolean isArrayAssignableTo(ArrayType var0, Type var1) {
      if (var1 instanceof ArrayType) {
         try {
            Type var2 = ((ArrayType)var1).componentType();
            return isComponentAssignable(var0.componentType(), var2);
         } catch (ClassNotLoadedException var3) {
            return false;
         }
      } else {
         return var1 instanceof InterfaceType ? var1.name().equals("java.lang.Cloneable") : var1.name().equals("java.lang.Object");
      }
   }

   static boolean isAssignableTo(Type var0, Type var1) {
      if (var0.equals(var1)) {
         return true;
      } else if (var0 instanceof BooleanType) {
         return var1 instanceof BooleanType;
      } else if (var1 instanceof BooleanType) {
         return false;
      } else if (var0 instanceof PrimitiveType) {
         return var1 instanceof PrimitiveType;
      } else if (var1 instanceof PrimitiveType) {
         return false;
      } else if (var0 instanceof ArrayType) {
         return isArrayAssignableTo((ArrayType)var0, var1);
      } else {
         List var2;
         if (var0 instanceof ClassType) {
            ClassType var3 = ((ClassType)var0).superclass();
            if (var3 != null && isAssignableTo(var3, var1)) {
               return true;
            }

            var2 = ((ClassType)var0).interfaces();
         } else {
            var2 = ((InterfaceType)var0).superinterfaces();
         }

         Iterator var5 = var2.iterator();

         InterfaceType var4;
         do {
            if (!var5.hasNext()) {
               return false;
            }

            var4 = (InterfaceType)var5.next();
         } while(!isAssignableTo(var4, var1));

         return true;
      }
   }

   static Method resolveOverload(List var0, List var1) throws ParseException {
      if (var0.size() == 1) {
         return (Method)var0.get(0);
      } else {
         Method var2 = null;
         int var3 = 0;
         Iterator var4 = var0.iterator();

         while(var4.hasNext()) {
            Method var5 = (Method)var4.next();

            List var6;
            try {
               var6 = var5.argumentTypes();
            } catch (ClassNotLoadedException var8) {
               continue;
            }

            int var7 = argumentsMatch(var6, var1);
            if (var7 == 0) {
               return var5;
            }

            if (var7 != 2) {
               var2 = var5;
               ++var3;
            }
         }

         if (var2 != null) {
            if (var3 == 1) {
               return var2;
            } else {
               throw new ParseException("Arguments match multiple methods");
            }
         } else {
            throw new ParseException("Arguments match no method");
         }
      }
   }

   static LValue make(VirtualMachine var0, boolean var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, byte var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, char var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, short var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, int var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, long var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, float var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, double var1) {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue make(VirtualMachine var0, String var1) throws ParseException {
      return new LValueConstant(var0.mirrorOf(var1));
   }

   static LValue makeBoolean(VirtualMachine var0, Token var1) {
      return make(var0, var1.image.charAt(0) == 't');
   }

   static LValue makeCharacter(VirtualMachine var0, Token var1) {
      return make(var0, var1.image.charAt(1));
   }

   static LValue makeFloat(VirtualMachine var0, Token var1) {
      return make(var0, Float.valueOf(var1.image));
   }

   static LValue makeDouble(VirtualMachine var0, Token var1) {
      return make(var0, Double.valueOf(var1.image));
   }

   static LValue makeInteger(VirtualMachine var0, Token var1) {
      return make(var0, Integer.parseInt(var1.image));
   }

   static LValue makeShort(VirtualMachine var0, Token var1) {
      return make(var0, Short.parseShort(var1.image));
   }

   static LValue makeLong(VirtualMachine var0, Token var1) {
      return make(var0, Long.parseLong(var1.image));
   }

   static LValue makeByte(VirtualMachine var0, Token var1) {
      return make(var0, Byte.parseByte(var1.image));
   }

   static LValue makeString(VirtualMachine var0, Token var1) throws ParseException {
      int var2 = var1.image.length();
      return make(var0, var1.image.substring(1, var2 - 1));
   }

   static LValue makeNull(VirtualMachine var0, Token var1) throws ParseException {
      return new LValueConstant((Value)null);
   }

   static LValue makeThisObject(VirtualMachine var0, ExpressionParser.GetFrame var1, Token var2) throws ParseException {
      if (var1 == null) {
         throw new ParseException("No current thread");
      } else {
         try {
            StackFrame var3 = var1.get();
            ObjectReference var4 = var3.thisObject();
            if (var4 == null) {
               throw new ParseException("No 'this'.  In native or static method");
            } else {
               return new LValueConstant(var4);
            }
         } catch (IncompatibleThreadStateException var5) {
            throw new ParseException("Thread not suspended");
         }
      }
   }

   static LValue makeNewObject(VirtualMachine var0, ExpressionParser.GetFrame var1, String var2, List var3) throws ParseException {
      List var4 = var0.classesByName(var2);
      if (var4.size() == 0) {
         throw new ParseException("No class named: " + var2);
      } else if (var4.size() > 1) {
         throw new ParseException("More than one class named: " + var2);
      } else {
         ReferenceType var5 = (ReferenceType)var4.get(0);
         if (!(var5 instanceof ClassType)) {
            throw new ParseException("Cannot create instance of interface " + var2);
         } else {
            ClassType var6 = (ClassType)var5;
            ArrayList var7 = new ArrayList(var6.methods());
            Iterator var8 = var7.iterator();

            Method var9;
            while(var8.hasNext()) {
               var9 = (Method)var8.next();
               if (!var9.isConstructor()) {
                  var8.remove();
               }
            }

            var9 = resolveOverload(var7, var3);

            ObjectReference var10;
            try {
               ThreadReference var11 = var1.get().thread();
               var10 = var6.newInstance(var11, var9, var3, 0);
            } catch (InvocationException var12) {
               throw new ParseException("Exception in " + var2 + " constructor: " + var12.exception().referenceType().name());
            } catch (IncompatibleThreadStateException var13) {
               throw new ParseException("Thread not suspended");
            } catch (Exception var14) {
               throw new ParseException("Unable to create " + var2 + " instance");
            }

            return new LValueConstant(var10);
         }
      }
   }

   private static LValue nFields(LValue var0, StringTokenizer var1, ThreadReference var2) throws ParseException {
      return !var1.hasMoreTokens() ? var0 : nFields(var0.memberLValue(var1.nextToken(), var2), var1, var2);
   }

   static LValue makeName(VirtualMachine var0, ExpressionParser.GetFrame var1, String var2) throws ParseException {
      StringTokenizer var3 = new StringTokenizer(var2, ".");
      String var4 = var3.nextToken();
      if (var1 != null) {
         try {
            StackFrame var5 = var1.get();
            ThreadReference var6 = var5.thread();

            LocalVariable var7;
            try {
               var7 = var5.visibleVariableByName(var4);
            } catch (AbsentInformationException var13) {
               var7 = null;
            }

            if (var7 != null) {
               return nFields(new LValueLocal(var5, var7), var3, var6);
            }

            ObjectReference var8 = var5.thisObject();
            if (var8 != null) {
               LValueConstant var9 = new LValueConstant(var8);

               LValue var10;
               try {
                  var10 = var9.memberLValue(var4, var6);
               } catch (ParseException var12) {
                  var10 = null;
               }

               if (var10 != null) {
                  return nFields(var10, var3, var6);
               }
            }

            while(var3.hasMoreTokens()) {
               List var16 = var0.classesByName(var4);
               if (var16.size() > 0) {
                  if (var16.size() > 1) {
                     throw new ParseException("More than one class named: " + var4);
                  }

                  ReferenceType var17 = (ReferenceType)var16.get(0);
                  LValueStaticMember var15 = new LValueStaticMember(var17, var3.nextToken(), var6);
                  return nFields(var15, var3, var6);
               }

               var4 = var4 + '.' + var3.nextToken();
            }
         } catch (IncompatibleThreadStateException var14) {
            throw new ParseException("Thread not suspended");
         }
      }

      throw new ParseException("Name unknown: " + var2);
   }

   static String stringValue(LValue var0, ExpressionParser.GetFrame var1) throws ParseException {
      Value var2 = var0.getMassagedValue(var1);
      if (var2 == null) {
         return "null";
      } else {
         return var2 instanceof StringReference ? ((StringReference)var2).value() : var2.toString();
      }
   }

   static LValue booleanOperation(VirtualMachine var0, Token var1, LValue var2, LValue var3) throws ParseException {
      String var4 = var1.image;
      Value var5 = var2.interiorGetValue();
      Value var6 = var3.interiorGetValue();
      if (var5 instanceof PrimitiveValue && var6 instanceof PrimitiveValue) {
         double var7 = ((PrimitiveValue)var5).doubleValue();
         double var9 = ((PrimitiveValue)var6).doubleValue();
         boolean var11;
         if (var4.equals("<")) {
            var11 = var7 < var9;
         } else if (var4.equals(">")) {
            var11 = var7 > var9;
         } else if (var4.equals("<=")) {
            var11 = var7 <= var9;
         } else if (var4.equals(">=")) {
            var11 = var7 >= var9;
         } else if (var4.equals("==")) {
            var11 = var7 == var9;
         } else {
            if (!var4.equals("!=")) {
               throw new ParseException("Unknown operation: " + var4);
            }

            var11 = var7 != var9;
         }

         return make(var0, var11);
      } else if (var4.equals("==")) {
         return make(var0, var5.equals(var6));
      } else if (var4.equals("!=")) {
         return make(var0, !var5.equals(var6));
      } else {
         throw new ParseException("Operands or '" + var4 + "' must be primitive");
      }
   }

   static LValue operation(VirtualMachine var0, Token var1, LValue var2, LValue var3, ExpressionParser.GetFrame var4) throws ParseException {
      String var5 = var1.image;
      Value var6 = var2.interiorGetValue();
      Value var7 = var3.interiorGetValue();
      if ((var6 instanceof StringReference || var7 instanceof StringReference) && var5.equals("+")) {
         return make(var0, stringValue(var2, var4) + stringValue(var3, var4));
      } else if (!(var6 instanceof ObjectReference) && !(var7 instanceof ObjectReference)) {
         if (!(var6 instanceof BooleanValue) && !(var7 instanceof BooleanValue)) {
            PrimitiveValue var8 = (PrimitiveValue)var6;
            PrimitiveValue var9 = (PrimitiveValue)var7;
            if (!(var8 instanceof DoubleValue) && !(var9 instanceof DoubleValue)) {
               if (!(var8 instanceof FloatValue) && !(var9 instanceof FloatValue)) {
                  if (!(var8 instanceof LongValue) && !(var9 instanceof LongValue)) {
                     int var18 = var8.intValue();
                     int var19 = var9.intValue();
                     int var22;
                     if (var5.equals("+")) {
                        var22 = var18 + var19;
                     } else if (var5.equals("-")) {
                        var22 = var18 - var19;
                     } else if (var5.equals("*")) {
                        var22 = var18 * var19;
                     } else {
                        if (!var5.equals("/")) {
                           throw new ParseException("Unknown operation: " + var5);
                        }

                        var22 = var18 / var19;
                     }

                     return make(var0, var22);
                  } else {
                     long var17 = var8.longValue();
                     long var21 = var9.longValue();
                     long var23;
                     if (var5.equals("+")) {
                        var23 = var17 + var21;
                     } else if (var5.equals("-")) {
                        var23 = var17 - var21;
                     } else if (var5.equals("*")) {
                        var23 = var17 * var21;
                     } else {
                        if (!var5.equals("/")) {
                           throw new ParseException("Unknown operation: " + var5);
                        }

                        var23 = var17 / var21;
                     }

                     return make(var0, var23);
                  }
               } else {
                  float var16 = var8.floatValue();
                  float var11 = var9.floatValue();
                  float var20;
                  if (var5.equals("+")) {
                     var20 = var16 + var11;
                  } else if (var5.equals("-")) {
                     var20 = var16 - var11;
                  } else if (var5.equals("*")) {
                     var20 = var16 * var11;
                  } else {
                     if (!var5.equals("/")) {
                        throw new ParseException("Unknown operation: " + var5);
                     }

                     var20 = var16 / var11;
                  }

                  return make(var0, var20);
               }
            } else {
               double var10 = var8.doubleValue();
               double var12 = var9.doubleValue();
               double var14;
               if (var5.equals("+")) {
                  var14 = var10 + var12;
               } else if (var5.equals("-")) {
                  var14 = var10 - var12;
               } else if (var5.equals("*")) {
                  var14 = var10 * var12;
               } else {
                  if (!var5.equals("/")) {
                     throw new ParseException("Unknown operation: " + var5);
                  }

                  var14 = var10 / var12;
               }

               return make(var0, var14);
            }
         } else {
            throw new ParseException("Invalid operation '" + var5 + "' on a Boolean");
         }
      } else if (var5.equals("==")) {
         return make(var0, var6.equals(var7));
      } else if (var5.equals("!=")) {
         return make(var0, !var6.equals(var7));
      } else {
         throw new ParseException("Invalid operation '" + var5 + "' on an Object");
      }
   }

   static {
      primitiveTypeNames.add("boolean");
      primitiveTypeNames.add("byte");
      primitiveTypeNames.add("char");
      primitiveTypeNames.add("short");
      primitiveTypeNames.add("int");
      primitiveTypeNames.add("long");
      primitiveTypeNames.add("float");
      primitiveTypeNames.add("double");
   }

   private static class LValueConstant extends LValue {
      final Value value;

      LValueConstant(Value var1) {
         this.value = var1;
      }

      Value getValue() {
         if (this.jdiValue == null) {
            this.jdiValue = this.value;
         }

         return this.jdiValue;
      }

      void setValue0(Value var1) throws ParseException {
         throw new ParseException("Cannot set constant: " + this.value);
      }

      void invokeWith(List var1) throws ParseException {
         throw new ParseException("Constant is not a method");
      }
   }

   private static class LValueArrayElement extends LValue {
      final ArrayReference array;
      final int index;

      LValueArrayElement(Value var1, int var2) throws ParseException {
         if (!(var1 instanceof ArrayReference)) {
            throw new ParseException("Must be array type: " + var1);
         } else {
            this.array = (ArrayReference)var1;
            this.index = var2;
         }
      }

      Value getValue() {
         if (this.jdiValue == null) {
            this.jdiValue = this.array.getValue(this.index);
         }

         return this.jdiValue;
      }

      void setValue0(Value var1) throws InvalidTypeException, ClassNotLoadedException {
         this.array.setValue(this.index, var1);
         this.jdiValue = var1;
      }

      void invokeWith(List var1) throws ParseException {
         throw new ParseException("Array element is not a method");
      }
   }

   private static class LValueArrayLength extends LValue {
      final ArrayReference arrayRef;

      LValueArrayLength(ArrayReference var1) {
         this.arrayRef = var1;
      }

      Value getValue() {
         if (this.jdiValue == null) {
            this.jdiValue = this.arrayRef.virtualMachine().mirrorOf(this.arrayRef.length());
         }

         return this.jdiValue;
      }

      void setValue0(Value var1) throws ParseException {
         throw new ParseException("Cannot set constant: " + var1);
      }

      void invokeWith(List var1) throws ParseException {
         throw new ParseException("Array element is not a method");
      }
   }

   private static class LValueStaticMember extends LValue {
      final ReferenceType refType;
      final ThreadReference thread;
      final Field matchingField;
      final List overloads;
      Method matchingMethod = null;
      List methodArguments = null;

      LValueStaticMember(ReferenceType var1, String var2, ThreadReference var3) throws ParseException {
         this.refType = var1;
         this.thread = var3;
         this.matchingField = LValue.fieldByName(var1, var2, 0);
         this.overloads = LValue.methodsByName(var1, var2, 0);
         if (this.matchingField == null && this.overloads.size() == 0) {
            throw new ParseException("No static field or method with the name " + var2 + " in " + var1.name());
         }
      }

      Value getValue() throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, ParseException {
         if (this.jdiValue != null) {
            return this.jdiValue;
         } else if (this.matchingMethod == null) {
            return this.jdiValue = this.refType.getValue(this.matchingField);
         } else if (this.refType instanceof ClassType) {
            ClassType var2 = (ClassType)this.refType;
            return this.jdiValue = var2.invokeMethod(this.thread, this.matchingMethod, this.methodArguments, 0);
         } else if (this.refType instanceof InterfaceType) {
            InterfaceType var1 = (InterfaceType)this.refType;
            return this.jdiValue = var1.invokeMethod(this.thread, this.matchingMethod, this.methodArguments, 0);
         } else {
            throw new InvalidTypeException("Cannot invoke static method on " + this.refType.name());
         }
      }

      void setValue0(Value var1) throws ParseException, InvalidTypeException, ClassNotLoadedException {
         if (this.matchingMethod != null) {
            throw new ParseException("Cannot assign to a method invocation");
         } else if (!(this.refType instanceof ClassType)) {
            throw new ParseException("Cannot set interface field: " + this.refType);
         } else {
            ((ClassType)this.refType).setValue(this.matchingField, var1);
            this.jdiValue = var1;
         }
      }

      void invokeWith(List var1) throws ParseException {
         if (this.matchingMethod != null) {
            throw new ParseException("Invalid consecutive invocations");
         } else {
            this.methodArguments = var1;
            this.matchingMethod = LValue.resolveOverload(this.overloads, var1);
         }
      }
   }

   private static class LValueInstanceMember extends LValue {
      final ObjectReference obj;
      final ThreadReference thread;
      final Field matchingField;
      final List overloads;
      Method matchingMethod = null;
      List methodArguments = null;

      LValueInstanceMember(Value var1, String var2, ThreadReference var3) throws ParseException {
         if (!(var1 instanceof ObjectReference)) {
            throw new ParseException("Cannot access field of primitive type: " + var1);
         } else {
            this.obj = (ObjectReference)var1;
            this.thread = var3;
            ReferenceType var4 = this.obj.referenceType();
            this.matchingField = LValue.fieldByName(var4, var2, 1);
            this.overloads = LValue.methodsByName(var4, var2, 1);
            if (this.matchingField == null && this.overloads.size() == 0) {
               throw new ParseException("No instance field or method with the name " + var2 + " in " + var4.name());
            }
         }
      }

      Value getValue() throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, ParseException {
         if (this.jdiValue != null) {
            return this.jdiValue;
         } else if (this.matchingMethod == null) {
            if (this.matchingField == null) {
               throw new ParseException("No such field in " + this.obj.referenceType().name());
            } else {
               return this.jdiValue = this.obj.getValue(this.matchingField);
            }
         } else {
            return this.jdiValue = this.obj.invokeMethod(this.thread, this.matchingMethod, this.methodArguments, 0);
         }
      }

      void setValue0(Value var1) throws ParseException, InvalidTypeException, ClassNotLoadedException {
         if (this.matchingMethod != null) {
            throw new ParseException("Cannot assign to a method invocation");
         } else {
            this.obj.setValue(this.matchingField, var1);
            this.jdiValue = var1;
         }
      }

      void invokeWith(List var1) throws ParseException {
         if (this.matchingMethod != null) {
            throw new ParseException("Invalid consecutive invocations");
         } else {
            this.methodArguments = var1;
            this.matchingMethod = LValue.resolveOverload(this.overloads, var1);
         }
      }
   }

   private static class LValueLocal extends LValue {
      final StackFrame frame;
      final LocalVariable var;

      LValueLocal(StackFrame var1, LocalVariable var2) {
         this.frame = var1;
         this.var = var2;
      }

      Value getValue() {
         if (this.jdiValue == null) {
            this.jdiValue = this.frame.getValue(this.var);
         }

         return this.jdiValue;
      }

      void setValue0(Value var1) throws InvalidTypeException, ClassNotLoadedException {
         this.frame.setValue(this.var, var1);
         this.jdiValue = var1;
      }

      void invokeWith(List var1) throws ParseException {
         throw new ParseException(this.var.name() + " is not a method");
      }
   }
}
