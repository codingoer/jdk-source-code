package com.sun.javadoc;

public interface Doc extends Comparable {
   String commentText();

   Tag[] tags();

   Tag[] tags(String var1);

   SeeTag[] seeTags();

   Tag[] inlineTags();

   Tag[] firstSentenceTags();

   String getRawCommentText();

   void setRawCommentText(String var1);

   String name();

   int compareTo(Object var1);

   boolean isField();

   boolean isEnumConstant();

   boolean isConstructor();

   boolean isMethod();

   boolean isAnnotationTypeElement();

   boolean isInterface();

   boolean isException();

   boolean isError();

   boolean isEnum();

   boolean isAnnotationType();

   boolean isOrdinaryClass();

   boolean isClass();

   boolean isIncluded();

   SourcePosition position();
}
