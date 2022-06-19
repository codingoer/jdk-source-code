package com.sun.tools.jdi;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.List;

public class ArrayReferenceImpl extends ObjectReferenceImpl implements ArrayReference {
   int length = -1;

   ArrayReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   protected ClassTypeImpl invokableReferenceType(Method var1) {
      return (ClassTypeImpl)var1.declaringType();
   }

   ArrayTypeImpl arrayType() {
      return (ArrayTypeImpl)this.type();
   }

   public int length() {
      if (this.length == -1) {
         try {
            this.length = JDWP.ArrayReference.Length.process(this.vm, this).arrayLength;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.length;
   }

   public Value getValue(int var1) {
      List var2 = this.getValues(var1, 1);
      return (Value)var2.get(0);
   }

   public List getValues() {
      return this.getValues(0, -1);
   }

   private void validateArrayAccess(int var1, int var2) {
      if (var1 >= 0 && var1 <= this.length()) {
         if (var2 < 0) {
            throw new IndexOutOfBoundsException("Invalid array range length: " + var2);
         } else if (var1 + var2 > this.length()) {
            throw new IndexOutOfBoundsException("Invalid array range: " + var1 + " to " + (var1 + var2 - 1));
         }
      } else {
         throw new IndexOutOfBoundsException("Invalid array index: " + var1);
      }
   }

   private static Object cast(Object var0) {
      return var0;
   }

   public List getValues(int var1, int var2) {
      if (var2 == -1) {
         var2 = this.length() - var1;
      }

      this.validateArrayAccess(var1, var2);
      if (var2 == 0) {
         return new ArrayList();
      } else {
         try {
            List var3 = (List)cast(JDWP.ArrayReference.GetValues.process(this.vm, this, var1, var2).values);
            return var3;
         } catch (JDWPException var5) {
            throw var5.toJDIException();
         }
      }
   }

   public void setValue(int var1, Value var2) throws InvalidTypeException, ClassNotLoadedException {
      ArrayList var3 = new ArrayList(1);
      var3.add(var2);
      this.setValues(var1, var3, 0, 1);
   }

   public void setValues(List var1) throws InvalidTypeException, ClassNotLoadedException {
      this.setValues(0, var1, 0, -1);
   }

   public void setValues(int var1, List var2, int var3, int var4) throws InvalidTypeException, ClassNotLoadedException {
      if (var4 == -1) {
         var4 = Math.min(this.length() - var1, var2.size() - var3);
      }

      this.validateMirrorsOrNulls(var2);
      this.validateArrayAccess(var1, var4);
      if (var3 >= 0 && var3 <= var2.size()) {
         if (var3 + var4 > var2.size()) {
            throw new IndexOutOfBoundsException("Invalid source range: " + var3 + " to " + (var3 + var4 - 1));
         } else {
            boolean var5 = false;
            ValueImpl[] var6 = new ValueImpl[var4];

            for(int var7 = 0; var7 < var4; ++var7) {
               ValueImpl var8 = (ValueImpl)var2.get(var3 + var7);

               try {
                  var6[var7] = ValueImpl.prepareForAssignment(var8, new Component());
                  var5 = true;
               } catch (ClassNotLoadedException var11) {
                  if (var8 != null) {
                     throw var11;
                  }
               }
            }

            if (var5) {
               try {
                  JDWP.ArrayReference.SetValues.process(this.vm, this, var1, var6);
               } catch (JDWPException var10) {
                  throw var10.toJDIException();
               }
            }

         }
      } else {
         throw new IndexOutOfBoundsException("Invalid source index: " + var3);
      }
   }

   public String toString() {
      return "instance of " + this.arrayType().componentTypeName() + "[" + this.length() + "] (id=" + this.uniqueID() + ")";
   }

   byte typeValueKey() {
      return 91;
   }

   void validateAssignment(ValueContainer var1) throws InvalidTypeException, ClassNotLoadedException {
      try {
         super.validateAssignment(var1);
      } catch (ClassNotLoadedException var11) {
         boolean var3 = false;
         JNITypeParser var4 = new JNITypeParser(var1.signature());
         JNITypeParser var5 = new JNITypeParser(this.arrayType().signature());
         int var6 = var4.dimensionCount();
         if (var6 <= var5.dimensionCount()) {
            String var7 = var4.componentSignature(var6);
            Type var8 = var1.findType(var7);
            String var9 = var5.componentSignature(var6);
            Type var10 = this.arrayType().findComponentType(var9);
            var3 = ArrayTypeImpl.isComponentAssignable(var8, var10);
         }

         if (!var3) {
            throw new InvalidTypeException("Cannot assign " + this.arrayType().name() + " to " + var1.typeName());
         }
      }

   }

   class Component implements ValueContainer {
      public Type type() throws ClassNotLoadedException {
         return ArrayReferenceImpl.this.arrayType().componentType();
      }

      public String typeName() {
         return ArrayReferenceImpl.this.arrayType().componentTypeName();
      }

      public String signature() {
         return ArrayReferenceImpl.this.arrayType().componentSignature();
      }

      public Type findType(String var1) throws ClassNotLoadedException {
         return ArrayReferenceImpl.this.arrayType().findComponentType(var1);
      }
   }
}
