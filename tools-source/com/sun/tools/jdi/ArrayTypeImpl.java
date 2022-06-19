package com.sun.tools.jdi;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayTypeImpl extends ReferenceTypeImpl implements ArrayType {
   protected ArrayTypeImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public ArrayReference newInstance(int var1) {
      try {
         return (ArrayReference)JDWP.ArrayType.NewInstance.process(this.vm, this, var1).newArray;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   public String componentSignature() {
      return this.signature().substring(1);
   }

   public String componentTypeName() {
      JNITypeParser var1 = new JNITypeParser(this.componentSignature());
      return var1.typeName();
   }

   Type type() throws ClassNotLoadedException {
      return this.findType(this.componentSignature());
   }

   void addVisibleMethods(Map var1, Set var2) {
   }

   public List allMethods() {
      return new ArrayList(0);
   }

   Type findComponentType(String var1) throws ClassNotLoadedException {
      byte var2 = (byte)var1.charAt(0);
      if (!PacketStream.isObjectTag(var2)) {
         return this.vm.primitiveTypeMirror(var2);
      } else {
         JNITypeParser var3 = new JNITypeParser(this.componentSignature());
         List var4 = this.vm.classesByName(var3.typeName());
         Iterator var5 = var4.iterator();

         ReferenceType var6;
         while(true) {
            if (!var5.hasNext()) {
               throw new ClassNotLoadedException(this.componentTypeName());
            }

            var6 = (ReferenceType)var5.next();
            ClassLoaderReference var7 = var6.classLoader();
            if (var7 == null) {
               if (this.classLoader() == null) {
                  break;
               }
            } else if (var7.equals(this.classLoader())) {
               break;
            }
         }

         return var6;
      }
   }

   public Type componentType() throws ClassNotLoadedException {
      return this.findComponentType(this.componentSignature());
   }

   static boolean isComponentAssignable(Type var0, Type var1) {
      if (var1 instanceof PrimitiveType) {
         return var1.equals(var0);
      } else if (var0 instanceof PrimitiveType) {
         return false;
      } else {
         ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1;
         ReferenceTypeImpl var3 = (ReferenceTypeImpl)var0;
         return var2.isAssignableTo(var3);
      }
   }

   boolean isAssignableTo(ReferenceType var1) {
      if (var1 instanceof ArrayType) {
         try {
            Type var2 = ((ArrayType)var1).componentType();
            return isComponentAssignable(var2, this.componentType());
         } catch (ClassNotLoadedException var3) {
            return false;
         }
      } else {
         return var1 instanceof InterfaceType ? var1.name().equals("java.lang.Cloneable") : var1.name().equals("java.lang.Object");
      }
   }

   List inheritedTypes() {
      return new ArrayList(0);
   }

   void getModifiers() {
      if (this.modifiers == -1) {
         try {
            Type var1 = this.componentType();
            if (var1 instanceof PrimitiveType) {
               this.modifiers = 17;
            } else {
               ReferenceType var2 = (ReferenceType)var1;
               this.modifiers = var2.modifiers();
            }
         } catch (ClassNotLoadedException var3) {
            var3.printStackTrace();
         }

      }
   }

   public String toString() {
      return "array class " + this.name() + " (" + this.loaderString() + ")";
   }

   public boolean isPrepared() {
      return true;
   }

   public boolean isVerified() {
      return true;
   }

   public boolean isInitialized() {
      return true;
   }

   public boolean failedToInitialize() {
      return false;
   }

   public boolean isAbstract() {
      return false;
   }

   public boolean isFinal() {
      return true;
   }

   public boolean isStatic() {
      return false;
   }
}
