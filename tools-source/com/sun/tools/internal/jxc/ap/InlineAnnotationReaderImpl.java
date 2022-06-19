package com.sun.tools.internal.jxc.ap;

import com.sun.xml.internal.bind.v2.model.annotation.AbstractInlineAnnotationReaderImpl;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.LocatableAnnotation;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

public final class InlineAnnotationReaderImpl extends AbstractInlineAnnotationReaderImpl {
   public static final InlineAnnotationReaderImpl theInstance = new InlineAnnotationReaderImpl();

   private InlineAnnotationReaderImpl() {
   }

   public Annotation getClassAnnotation(Class a, TypeElement clazz, Locatable srcPos) {
      return LocatableAnnotation.create(clazz.getAnnotation(a), srcPos);
   }

   public Annotation getFieldAnnotation(Class a, VariableElement f, Locatable srcPos) {
      return LocatableAnnotation.create(f.getAnnotation(a), srcPos);
   }

   public boolean hasFieldAnnotation(Class annotationType, VariableElement f) {
      return f.getAnnotation(annotationType) != null;
   }

   public boolean hasClassAnnotation(TypeElement clazz, Class annotationType) {
      return clazz.getAnnotation(annotationType) != null;
   }

   public Annotation[] getAllFieldAnnotations(VariableElement field, Locatable srcPos) {
      return this.getAllAnnotations(field, srcPos);
   }

   public Annotation getMethodAnnotation(Class a, ExecutableElement method, Locatable srcPos) {
      return LocatableAnnotation.create(method.getAnnotation(a), srcPos);
   }

   public boolean hasMethodAnnotation(Class a, ExecutableElement method) {
      return method.getAnnotation(a) != null;
   }

   public Annotation[] getAllMethodAnnotations(ExecutableElement method, Locatable srcPos) {
      return this.getAllAnnotations(method, srcPos);
   }

   private Annotation[] getAllAnnotations(Element decl, Locatable srcPos) {
      List r = new ArrayList();
      Iterator var4 = decl.getAnnotationMirrors().iterator();

      while(var4.hasNext()) {
         AnnotationMirror m = (AnnotationMirror)var4.next();

         try {
            String fullName = ((TypeElement)m.getAnnotationType().asElement()).getQualifiedName().toString();
            Class type = SecureLoader.getClassClassLoader(this.getClass()).loadClass(fullName).asSubclass(Annotation.class);
            Annotation annotation = decl.getAnnotation(type);
            if (annotation != null) {
               r.add(LocatableAnnotation.create(annotation, srcPos));
            }
         } catch (ClassNotFoundException var9) {
         }
      }

      return (Annotation[])r.toArray(new Annotation[r.size()]);
   }

   public Annotation getMethodParameterAnnotation(Class a, ExecutableElement m, int paramIndex, Locatable srcPos) {
      VariableElement[] params = (VariableElement[])m.getParameters().toArray(new VariableElement[m.getParameters().size()]);
      return LocatableAnnotation.create(params[paramIndex].getAnnotation(a), srcPos);
   }

   public Annotation getPackageAnnotation(Class a, TypeElement clazz, Locatable srcPos) {
      return LocatableAnnotation.create(clazz.getEnclosingElement().getAnnotation(a), srcPos);
   }

   public TypeMirror getClassValue(Annotation a, String name) {
      try {
         a.annotationType().getMethod(name).invoke(a);

         assert false;

         throw new IllegalStateException("should throw a MirroredTypeException");
      } catch (IllegalAccessException var5) {
         throw new IllegalAccessError(var5.getMessage());
      } catch (InvocationTargetException var6) {
         if (var6.getCause() instanceof MirroredTypeException) {
            MirroredTypeException me = (MirroredTypeException)var6.getCause();
            return me.getTypeMirror();
         } else {
            throw new RuntimeException(var6);
         }
      } catch (NoSuchMethodException var7) {
         throw new NoSuchMethodError(var7.getMessage());
      }
   }

   public TypeMirror[] getClassArrayValue(Annotation a, String name) {
      try {
         a.annotationType().getMethod(name).invoke(a);

         assert false;

         throw new IllegalStateException("should throw a MirroredTypesException");
      } catch (IllegalAccessException var7) {
         throw new IllegalAccessError(var7.getMessage());
      } catch (InvocationTargetException var8) {
         if (var8.getCause() instanceof MirroredTypesException) {
            MirroredTypesException me = (MirroredTypesException)var8.getCause();
            Collection r = me.getTypeMirrors();
            return (TypeMirror[])r.toArray(new TypeMirror[r.size()]);
         } else if (var8.getCause() instanceof MirroredTypeException) {
            MirroredTypeException me = (MirroredTypeException)var8.getCause();
            TypeMirror tr = me.getTypeMirror();
            TypeMirror[] trArr = new TypeMirror[]{tr};
            return trArr;
         } else {
            throw new RuntimeException(var8);
         }
      } catch (NoSuchMethodException var9) {
         throw new NoSuchMethodError(var9.getMessage());
      }
   }

   protected String fullName(ExecutableElement m) {
      return ((TypeElement)m.getEnclosingElement()).getQualifiedName().toString() + '#' + m.getSimpleName();
   }
}
