package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JClass;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.reflect.Type;
import java.util.Collection;

public final class NavigatorImpl implements Navigator {
   public static final NavigatorImpl theInstance = new NavigatorImpl();

   private NavigatorImpl() {
   }

   public NClass getSuperClass(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public NType getBaseClass(NType nt, NClass base) {
      EagerNClass enc;
      if (nt instanceof EagerNType) {
         EagerNType ent = (EagerNType)nt;
         if (base instanceof EagerNClass) {
            enc = (EagerNClass)base;
            return create((Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(ent.t, enc.c));
         } else {
            return null;
         }
      } else {
         if (nt instanceof NClassByJClass) {
            NClassByJClass nnt = (NClassByJClass)nt;
            if (base instanceof EagerNClass) {
               enc = (EagerNClass)base;
               return this.ref(nnt.clazz.getBaseClass(enc.c));
            }
         }

         throw new UnsupportedOperationException();
      }
   }

   public String getClassName(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public String getTypeName(NType type) {
      return type.fullName();
   }

   public String getClassShortName(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public Collection getDeclaredFields(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public Void getDeclaredField(NClass clazz, String fieldName) {
      throw new UnsupportedOperationException();
   }

   public Collection getDeclaredMethods(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public NClass getDeclaringClassForField(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public NClass getDeclaringClassForMethod(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public NType getFieldType(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public String getFieldName(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public String getMethodName(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public NType getReturnType(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public NType[] getMethodParameters(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public boolean isStaticMethod(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public boolean isFinalMethod(Void aVoid) {
      throw new UnsupportedOperationException();
   }

   public boolean isSubClassOf(NType sub, NType sup) {
      throw new UnsupportedOperationException();
   }

   public NClass ref(Class c) {
      return create(c);
   }

   public NClass ref(JClass c) {
      return c == null ? null : new NClassByJClass(c);
   }

   public NType use(NClass nc) {
      return nc;
   }

   public NClass asDecl(NType nt) {
      return nt instanceof NClass ? (NClass)nt : null;
   }

   public NClass asDecl(Class c) {
      return this.ref(c);
   }

   public boolean isArray(NType nType) {
      throw new UnsupportedOperationException();
   }

   public boolean isArrayButNotByteArray(NType t) {
      throw new UnsupportedOperationException();
   }

   public NType getComponentType(NType nType) {
      throw new UnsupportedOperationException();
   }

   public NType getTypeArgument(NType nt, int i) {
      if (nt instanceof EagerNType) {
         EagerNType ent = (EagerNType)nt;
         return create((Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(ent.t, i));
      } else if (nt instanceof NClassByJClass) {
         NClassByJClass nnt = (NClassByJClass)nt;
         return this.ref((JClass)nnt.clazz.getTypeParameters().get(i));
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public boolean isParameterizedType(NType nt) {
      if (nt instanceof EagerNType) {
         EagerNType ent = (EagerNType)nt;
         return Utils.REFLECTION_NAVIGATOR.isParameterizedType(ent.t);
      } else if (nt instanceof NClassByJClass) {
         NClassByJClass nnt = (NClassByJClass)nt;
         return nnt.clazz.isParameterized();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public boolean isPrimitive(NType type) {
      throw new UnsupportedOperationException();
   }

   public NType getPrimitive(Class primitiveType) {
      return create(primitiveType);
   }

   public static final NType create(Type t) {
      if (t == null) {
         return null;
      } else {
         return (NType)(t instanceof Class ? create((Class)t) : new EagerNType(t));
      }
   }

   public static NClass create(Class c) {
      return c == null ? null : new EagerNClass(c);
   }

   public static NType createParameterizedType(NClass rawType, NType... args) {
      return new NParameterizedType(rawType, args);
   }

   public static NType createParameterizedType(Class rawType, NType... args) {
      return new NParameterizedType(create(rawType), args);
   }

   public Location getClassLocation(final NClass c) {
      return new Location() {
         public String toString() {
            return c.fullName();
         }
      };
   }

   public Location getFieldLocation(Void v) {
      throw new IllegalStateException();
   }

   public Location getMethodLocation(Void v) {
      throw new IllegalStateException();
   }

   public boolean hasDefaultConstructor(NClass nClass) {
      throw new UnsupportedOperationException();
   }

   public boolean isStaticField(Void aVoid) {
      throw new IllegalStateException();
   }

   public boolean isPublicMethod(Void aVoid) {
      throw new IllegalStateException();
   }

   public boolean isPublicField(Void aVoid) {
      throw new IllegalStateException();
   }

   public boolean isEnum(NClass c) {
      return this.isSubClassOf((NType)c, (NType)create(Enum.class));
   }

   public NType erasure(NType type) {
      if (type instanceof NParameterizedType) {
         NParameterizedType pt = (NParameterizedType)type;
         return pt.rawType;
      } else {
         return type;
      }
   }

   public boolean isAbstract(NClass clazz) {
      return clazz.isAbstract();
   }

   /** @deprecated */
   public boolean isFinal(NClass clazz) {
      return false;
   }

   public Void[] getEnumConstants(NClass clazz) {
      throw new UnsupportedOperationException();
   }

   public NType getVoidType() {
      return this.ref(Void.TYPE);
   }

   public String getPackageName(NClass clazz) {
      throw new UnsupportedOperationException();
   }

   public NClass loadObjectFactory(NClass referencePoint, String pkg) {
      throw new UnsupportedOperationException();
   }

   public boolean isBridgeMethod(Void method) {
      throw new UnsupportedOperationException();
   }

   public boolean isOverriding(Void method, NClass clazz) {
      throw new UnsupportedOperationException();
   }

   public boolean isInterface(NClass clazz) {
      throw new UnsupportedOperationException();
   }

   public boolean isTransient(Void f) {
      throw new UnsupportedOperationException();
   }

   public boolean isInnerClass(NClass clazz) {
      throw new UnsupportedOperationException();
   }

   public boolean isSameType(NType t1, NType t2) {
      throw new UnsupportedOperationException();
   }
}
