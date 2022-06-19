package com.sun.javadoc;

public interface SerialFieldTag extends Tag, Comparable {
   String fieldName();

   String fieldType();

   ClassDoc fieldTypeDoc();

   String description();

   int compareTo(Object var1);
}
