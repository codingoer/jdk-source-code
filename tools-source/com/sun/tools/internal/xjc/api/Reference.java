package com.sun.tools.internal.xjc.api;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public final class Reference {
   public final TypeMirror type;
   public final Element annotations;

   public Reference(ExecutableElement method) {
      this((TypeMirror)method.getReturnType(), (Element)method);
   }

   public Reference(VariableElement param) {
      this((TypeMirror)param.asType(), (Element)param);
   }

   public Reference(TypeElement type, ProcessingEnvironment env) {
      this((TypeMirror)env.getTypeUtils().getDeclaredType(type, new TypeMirror[0]), (Element)type);
   }

   public Reference(TypeMirror type, Element annotations) {
      if (type != null && annotations != null) {
         this.type = type;
         this.annotations = annotations;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Reference)) {
         return false;
      } else {
         Reference that = (Reference)o;
         return this.annotations.equals(that.annotations) && this.type.equals(that.type);
      }
   }

   public int hashCode() {
      return 29 * this.type.hashCode() + this.annotations.hashCode();
   }
}
