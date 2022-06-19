package com.sun.codemodel.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class TypedAnnotationWriter implements InvocationHandler, JAnnotationWriter {
   private final JAnnotationUse use;
   private final Class annotation;
   private final Class writerType;
   private Map arrays;

   public TypedAnnotationWriter(Class annotation, Class writer, JAnnotationUse use) {
      this.annotation = annotation;
      this.writerType = writer;
      this.use = use;
   }

   public JAnnotationUse getAnnotationUse() {
      return this.use;
   }

   public Class getAnnotationType() {
      return this.annotation;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() == JAnnotationWriter.class) {
         try {
            return method.invoke(this, args);
         } catch (InvocationTargetException var9) {
            throw var9.getTargetException();
         }
      } else {
         String name = method.getName();
         Object arg = null;
         if (args != null && args.length > 0) {
            arg = args[0];
         }

         Method m = this.annotation.getDeclaredMethod(name);
         Class rt = m.getReturnType();
         if (rt.isArray()) {
            return this.addArrayValue(proxy, name, rt.getComponentType(), method.getReturnType(), arg);
         } else if (Annotation.class.isAssignableFrom(rt)) {
            return (new TypedAnnotationWriter(rt, method.getReturnType(), this.use.annotationParam(name, rt))).createProxy();
         } else if (arg instanceof JType) {
            JType targ = (JType)arg;
            this.checkType(Class.class, rt);
            if (m.getDefaultValue() != null && targ.equals(targ.owner().ref((Class)m.getDefaultValue()))) {
               return proxy;
            } else {
               this.use.param(name, targ);
               return proxy;
            }
         } else {
            this.checkType(arg.getClass(), rt);
            if (m.getDefaultValue() != null && m.getDefaultValue().equals(arg)) {
               return proxy;
            } else if (arg instanceof String) {
               this.use.param(name, (String)arg);
               return proxy;
            } else if (arg instanceof Boolean) {
               this.use.param(name, (Boolean)arg);
               return proxy;
            } else if (arg instanceof Integer) {
               this.use.param(name, (Integer)arg);
               return proxy;
            } else if (arg instanceof Class) {
               this.use.param(name, (Class)arg);
               return proxy;
            } else if (arg instanceof Enum) {
               this.use.param(name, (Enum)arg);
               return proxy;
            } else {
               throw new IllegalArgumentException("Unable to handle this method call " + method.toString());
            }
         }
      }
   }

   private Object addArrayValue(Object proxy, String name, Class itemType, Class expectedReturnType, Object arg) {
      if (this.arrays == null) {
         this.arrays = new HashMap();
      }

      JAnnotationArrayMember m = (JAnnotationArrayMember)this.arrays.get(name);
      if (m == null) {
         m = this.use.paramArray(name);
         this.arrays.put(name, m);
      }

      if (Annotation.class.isAssignableFrom(itemType)) {
         if (!JAnnotationWriter.class.isAssignableFrom(expectedReturnType)) {
            throw new IllegalArgumentException("Unexpected return type " + expectedReturnType);
         } else {
            return (new TypedAnnotationWriter(itemType, expectedReturnType, m.annotate(itemType))).createProxy();
         }
      } else if (arg instanceof JType) {
         this.checkType(Class.class, itemType);
         m.param((JType)arg);
         return proxy;
      } else {
         this.checkType(arg.getClass(), itemType);
         if (arg instanceof String) {
            m.param((String)arg);
            return proxy;
         } else if (arg instanceof Boolean) {
            m.param((Boolean)arg);
            return proxy;
         } else if (arg instanceof Integer) {
            m.param((Integer)arg);
            return proxy;
         } else if (arg instanceof Class) {
            m.param((Class)arg);
            return proxy;
         } else {
            throw new IllegalArgumentException("Unable to handle this method call ");
         }
      }
   }

   private void checkType(Class actual, Class expected) {
      if (expected != actual && !expected.isAssignableFrom(actual)) {
         if (expected != JCodeModel.boxToPrimitive.get(actual)) {
            throw new IllegalArgumentException("Expected " + expected + " but found " + actual);
         }
      }
   }

   private JAnnotationWriter createProxy() {
      return (JAnnotationWriter)Proxy.newProxyInstance(SecureLoader.getClassClassLoader(this.writerType), new Class[]{this.writerType}, this);
   }

   static JAnnotationWriter create(Class w, JAnnotatable annotatable) {
      Class a = findAnnotationType(w);
      return (new TypedAnnotationWriter(a, w, annotatable.annotate(a))).createProxy();
   }

   private static Class findAnnotationType(Class clazz) {
      Type[] var1 = clazz.getGenericInterfaces();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type t = var1[var3];
         if (t instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)t;
            if (p.getRawType() == JAnnotationWriter.class) {
               return (Class)p.getActualTypeArguments()[0];
            }
         }

         if (t instanceof Class) {
            Class r = findAnnotationType((Class)t);
            if (r != null) {
               return r;
            }
         }
      }

      return null;
   }
}
