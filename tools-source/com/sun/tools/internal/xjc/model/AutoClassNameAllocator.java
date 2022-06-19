package com.sun.tools.internal.xjc.model;

import com.sun.tools.internal.xjc.api.ClassNameAllocator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutoClassNameAllocator implements ClassNameAllocator {
   private final ClassNameAllocator core;
   private final Map names = new HashMap();

   public AutoClassNameAllocator(ClassNameAllocator core) {
      this.core = core;
   }

   public String assignClassName(String packageName, String className) {
      className = this.determineName(packageName, className);
      if (this.core != null) {
         className = this.core.assignClassName(packageName, className);
      }

      return className;
   }

   private String determineName(String packageName, String className) {
      Set s = (Set)this.names.get(packageName);
      if (s == null) {
         s = new HashSet();
         this.names.put(packageName, s);
      }

      if (((Set)s).add(className)) {
         return className;
      } else {
         int i;
         for(i = 2; !((Set)s).add(className + i); ++i) {
         }

         return className + i;
      }
   }
}
