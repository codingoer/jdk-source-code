package com.sun.tools.internal.ws.processor.modeler.annotation;

import java.util.List;
import javax.lang.model.type.TypeMirror;

final class MemberInfo implements Comparable {
   private final TypeMirror paramType;
   private final String paramName;
   private final List jaxbAnnotations;

   public MemberInfo(TypeMirror paramType, String paramName, List jaxbAnnotations) {
      this.paramType = paramType;
      this.paramName = paramName;
      this.jaxbAnnotations = jaxbAnnotations;
   }

   public List getJaxbAnnotations() {
      return this.jaxbAnnotations;
   }

   public TypeMirror getParamType() {
      return this.paramType;
   }

   public String getParamName() {
      return this.paramName;
   }

   public int compareTo(MemberInfo member) {
      return this.paramName.compareTo(member.paramName);
   }

   public boolean equals(Object o) {
      return super.equals(o);
   }

   public int hashCode() {
      int hash = 5;
      hash = 47 * hash + (this.paramType != null ? this.paramType.hashCode() : 0);
      hash = 47 * hash + (this.paramName != null ? this.paramName.hashCode() : 0);
      hash = 47 * hash + (this.jaxbAnnotations != null ? this.jaxbAnnotations.hashCode() : 0);
      return hash;
   }
}
