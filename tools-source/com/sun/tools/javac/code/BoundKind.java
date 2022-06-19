package com.sun.tools.javac.code;

public enum BoundKind {
   EXTENDS("? extends "),
   SUPER("? super "),
   UNBOUND("?");

   private final String name;

   private BoundKind(String var3) {
      this.name = var3;
   }

   public String toString() {
      return this.name;
   }
}
