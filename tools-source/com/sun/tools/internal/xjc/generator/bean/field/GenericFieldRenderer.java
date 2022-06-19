package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class GenericFieldRenderer implements FieldRenderer {
   private Constructor constructor;

   public GenericFieldRenderer(Class fieldClass) {
      try {
         this.constructor = fieldClass.getDeclaredConstructor(ClassOutlineImpl.class, CPropertyInfo.class);
      } catch (NoSuchMethodException var3) {
         throw new NoSuchMethodError(var3.getMessage());
      }
   }

   public FieldOutline generate(ClassOutlineImpl context, CPropertyInfo prop) {
      try {
         return (FieldOutline)this.constructor.newInstance(context, prop);
      } catch (InstantiationException var5) {
         throw new InstantiationError(var5.getMessage());
      } catch (IllegalAccessException var6) {
         throw new IllegalAccessError(var6.getMessage());
      } catch (InvocationTargetException var7) {
         Throwable t = var7.getTargetException();
         if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         } else if (t instanceof Error) {
            throw (Error)t;
         } else {
            throw new AssertionError(t);
         }
      }
   }
}
