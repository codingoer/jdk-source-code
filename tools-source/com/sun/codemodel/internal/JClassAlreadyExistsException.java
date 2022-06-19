package com.sun.codemodel.internal;

public class JClassAlreadyExistsException extends Exception {
   private static final long serialVersionUID = 1L;
   private final JDefinedClass existing;

   public JClassAlreadyExistsException(JDefinedClass _existing) {
      this.existing = _existing;
   }

   public JDefinedClass getExistingClass() {
      return this.existing;
   }
}
