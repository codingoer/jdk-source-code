package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.processor.util.ClassNameCollector;
import com.sun.tools.internal.xjc.api.ClassNameAllocator;
import java.util.HashSet;
import java.util.Set;

public class ClassNameAllocatorImpl implements ClassNameAllocator {
   private static final String TYPE_SUFFIX = "_Type";
   private ClassNameCollector classNameCollector;
   private Set jaxbClasses;

   public ClassNameAllocatorImpl(ClassNameCollector classNameCollector) {
      this.classNameCollector = classNameCollector;
      this.jaxbClasses = new HashSet();
   }

   public String assignClassName(String packageName, String className) {
      if (packageName != null && className != null) {
         if (!packageName.equals("") && !className.equals("")) {
            String fullClassName = packageName + "." + className;
            Set seiClassNames = this.classNameCollector.getSeiClassNames();
            if (seiClassNames != null && seiClassNames.contains(fullClassName)) {
               className = className + "_Type";
            }

            this.jaxbClasses.add(packageName + "." + className);
            return className;
         } else {
            return className;
         }
      } else {
         return className;
      }
   }

   public Set getJaxbGeneratedClasses() {
      return this.jaxbClasses;
   }
}
