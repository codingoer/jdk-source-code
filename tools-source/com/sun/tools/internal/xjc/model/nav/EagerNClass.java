package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class EagerNClass extends EagerNType implements NClass {
   final Class c;
   private static final Set boxedTypes = new HashSet();

   public EagerNClass(Class type) {
      super(type);
      this.c = type;
   }

   public boolean isBoxedType() {
      return boxedTypes.contains(this.c);
   }

   public JClass toType(Outline o, Aspect aspect) {
      return o.getCodeModel().ref(this.c);
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this.c.getModifiers());
   }

   static {
      boxedTypes.add(Boolean.class);
      boxedTypes.add(Character.class);
      boxedTypes.add(Byte.class);
      boxedTypes.add(Short.class);
      boxedTypes.add(Integer.class);
      boxedTypes.add(Long.class);
      boxedTypes.add(Float.class);
      boxedTypes.add(Double.class);
   }
}
