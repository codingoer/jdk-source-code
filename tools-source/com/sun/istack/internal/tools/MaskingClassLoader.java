package com.sun.istack.internal.tools;

import java.util.Collection;

public class MaskingClassLoader extends ClassLoader {
   private final String[] masks;

   public MaskingClassLoader(String... masks) {
      this.masks = masks;
   }

   public MaskingClassLoader(Collection masks) {
      this((String[])masks.toArray(new String[masks.size()]));
   }

   public MaskingClassLoader(ClassLoader parent, String... masks) {
      super(parent);
      this.masks = masks;
   }

   public MaskingClassLoader(ClassLoader parent, Collection masks) {
      this(parent, (String[])masks.toArray(new String[masks.size()]));
   }

   protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
      String[] var3 = this.masks;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String mask = var3[var5];
         if (name.startsWith(mask)) {
            throw new ClassNotFoundException();
         }
      }

      return super.loadClass(name, resolve);
   }
}
