package com.sun.xml.internal.rngom.digested;

public class DDefine {
   private final String name;
   private DPattern pattern;
   private Boolean nullable;
   DAnnotation annotation;

   public DDefine(String name) {
      this.name = name;
   }

   public DPattern getPattern() {
      return this.pattern;
   }

   public DAnnotation getAnnotation() {
      return this.annotation == null ? DAnnotation.EMPTY : this.annotation;
   }

   public void setPattern(DPattern pattern) {
      this.pattern = pattern;
      this.nullable = null;
   }

   public String getName() {
      return this.name;
   }

   public boolean isNullable() {
      if (this.nullable == null) {
         this.nullable = this.pattern.isNullable() ? Boolean.TRUE : Boolean.FALSE;
      }

      return this.nullable;
   }
}
