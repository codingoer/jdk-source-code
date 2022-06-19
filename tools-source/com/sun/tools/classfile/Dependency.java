package com.sun.tools.classfile;

public interface Dependency {
   Location getOrigin();

   Location getTarget();

   public interface Location {
      String getName();

      String getClassName();

      String getPackageName();
   }

   public interface Finder {
      Iterable findDependencies(ClassFile var1);
   }

   public interface Filter {
      boolean accepts(Dependency var1);
   }
}
