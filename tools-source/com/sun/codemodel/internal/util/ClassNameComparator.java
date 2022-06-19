package com.sun.codemodel.internal.util;

import com.sun.codemodel.internal.JClass;
import java.util.Comparator;

public class ClassNameComparator implements Comparator {
   public static final Comparator theInstance = new ClassNameComparator();

   private ClassNameComparator() {
   }

   public int compare(JClass l, JClass r) {
      return l.fullName().compareTo(r.fullName());
   }
}
