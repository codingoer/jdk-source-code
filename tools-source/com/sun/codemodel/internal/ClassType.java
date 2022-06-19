package com.sun.codemodel.internal;

public final class ClassType {
   final String declarationToken;
   public static final ClassType CLASS = new ClassType("class");
   public static final ClassType INTERFACE = new ClassType("interface");
   public static final ClassType ANNOTATION_TYPE_DECL = new ClassType("@interface");
   public static final ClassType ENUM = new ClassType("enum");

   private ClassType(String token) {
      this.declarationToken = token;
   }
}
