package com.sun.tools.javac.processing;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;

public class JavacRoundEnvironment implements RoundEnvironment {
   private final boolean processingOver;
   private final boolean errorRaised;
   private final ProcessingEnvironment processingEnv;
   private final Set rootElements;
   private static final String NOT_AN_ANNOTATION_TYPE = "The argument does not represent an annotation type: ";

   JavacRoundEnvironment(boolean var1, boolean var2, Set var3, ProcessingEnvironment var4) {
      this.processingOver = var1;
      this.errorRaised = var2;
      this.rootElements = var3;
      this.processingEnv = var4;
   }

   public String toString() {
      return String.format("[errorRaised=%b, rootElements=%s, processingOver=%b]", this.errorRaised, this.rootElements, this.processingOver);
   }

   public boolean processingOver() {
      return this.processingOver;
   }

   public boolean errorRaised() {
      return this.errorRaised;
   }

   public Set getRootElements() {
      return this.rootElements;
   }

   public Set getElementsAnnotatedWith(TypeElement var1) {
      Set var2 = Collections.emptySet();
      if (var1.getKind() != ElementKind.ANNOTATION_TYPE) {
         throw new IllegalArgumentException("The argument does not represent an annotation type: " + var1);
      } else {
         AnnotationSetScanner var3 = new AnnotationSetScanner(var2);

         Element var5;
         for(Iterator var4 = this.rootElements.iterator(); var4.hasNext(); var2 = (Set)var3.scan(var5, var1)) {
            var5 = (Element)var4.next();
         }

         return var2;
      }
   }

   public Set getElementsAnnotatedWith(Class var1) {
      if (!var1.isAnnotation()) {
         throw new IllegalArgumentException("The argument does not represent an annotation type: " + var1);
      } else {
         String var2 = var1.getCanonicalName();
         if (var2 == null) {
            return Collections.emptySet();
         } else {
            TypeElement var3 = this.processingEnv.getElementUtils().getTypeElement(var2);
            return var3 == null ? Collections.emptySet() : this.getElementsAnnotatedWith(var3);
         }
      }
   }

   private class AnnotationSetScanner extends ElementScanner8 {
      Set annotatedElements = new LinkedHashSet();

      AnnotationSetScanner(Set var2) {
         super(var2);
      }

      public Set visitType(TypeElement var1, TypeElement var2) {
         this.scan((Iterable)var1.getTypeParameters(), (Object)var2);
         return (Set)super.visitType(var1, var2);
      }

      public Set visitExecutable(ExecutableElement var1, TypeElement var2) {
         this.scan((Iterable)var1.getTypeParameters(), (Object)var2);
         return (Set)super.visitExecutable(var1, var2);
      }

      public Set scan(Element var1, TypeElement var2) {
         List var3 = JavacRoundEnvironment.this.processingEnv.getElementUtils().getAllAnnotationMirrors(var1);
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            AnnotationMirror var5 = (AnnotationMirror)var4.next();
            if (var2.equals(var5.getAnnotationType().asElement())) {
               this.annotatedElements.add(var1);
            }
         }

         var1.accept(this, var2);
         return this.annotatedElements;
      }
   }
}
