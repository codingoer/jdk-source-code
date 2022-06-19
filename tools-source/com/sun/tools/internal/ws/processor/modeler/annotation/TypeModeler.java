package com.sun.tools.internal.ws.processor.modeler.annotation;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

final class TypeModeler {
   private TypeModeler() {
   }

   public static TypeElement getDeclaration(TypeMirror typeMirror) {
      return typeMirror != null && typeMirror.getKind().equals(TypeKind.DECLARED) ? (TypeElement)((DeclaredType)typeMirror).asElement() : null;
   }

   public static TypeElement getDeclaringClassMethod(TypeMirror theClass, String methodName, TypeMirror[] args) {
      return getDeclaringClassMethod(getDeclaration(theClass), methodName, args);
   }

   public static TypeElement getDeclaringClassMethod(TypeElement theClass, String methodName, TypeMirror[] args) {
      TypeElement retClass = null;
      if (theClass.getKind().equals(ElementKind.CLASS)) {
         TypeMirror superClass = theClass.getSuperclass();
         if (!superClass.getKind().equals(TypeKind.NONE)) {
            retClass = getDeclaringClassMethod(superClass, methodName, args);
         }
      }

      TypeMirror interfaceType;
      if (retClass == null) {
         for(Iterator var7 = theClass.getInterfaces().iterator(); var7.hasNext(); retClass = getDeclaringClassMethod(interfaceType, methodName, args)) {
            interfaceType = (TypeMirror)var7.next();
         }
      }

      if (retClass == null) {
         Collection methods = ElementFilter.methodsIn(theClass.getEnclosedElements());
         Iterator var9 = methods.iterator();

         while(var9.hasNext()) {
            ExecutableElement method = (ExecutableElement)var9.next();
            if (method.getSimpleName().toString().equals(methodName)) {
               retClass = theClass;
               break;
            }
         }
      }

      return retClass;
   }

   public static Collection collectInterfaces(TypeElement type) {
      Collection interfaces = type.getInterfaces();
      Iterator var2 = type.getInterfaces().iterator();

      while(var2.hasNext()) {
         TypeMirror interfaceType = (TypeMirror)var2.next();
         interfaces.addAll(collectInterfaces(getDeclaration(interfaceType)));
      }

      return interfaces;
   }

   public static boolean isSubclass(String subTypeName, String superTypeName, ProcessingEnvironment env) {
      return isSubclass(env.getElementUtils().getTypeElement(subTypeName), env.getElementUtils().getTypeElement(superTypeName), env);
   }

   public static boolean isSubclass(TypeElement subType, TypeElement superType, ProcessingEnvironment env) {
      return !subType.equals(superType) && isSubElement(subType, superType);
   }

   public static TypeMirror getHolderValueType(TypeMirror type, TypeElement defHolder, ProcessingEnvironment env) {
      TypeElement typeElement = getDeclaration(type);
      if (typeElement == null) {
         return null;
      } else {
         if (isSubElement(typeElement, defHolder) && type.getKind().equals(TypeKind.DECLARED)) {
            Collection argTypes = ((DeclaredType)type).getTypeArguments();
            if (argTypes.size() == 1) {
               return (TypeMirror)argTypes.iterator().next();
            }

            if (argTypes.isEmpty()) {
               VariableElement member = getValueMember(typeElement);
               if (member != null) {
                  return member.asType();
               }
            }
         }

         return null;
      }
   }

   public static VariableElement getValueMember(TypeMirror classType) {
      return getValueMember(getDeclaration(classType));
   }

   public static VariableElement getValueMember(TypeElement type) {
      VariableElement member = null;
      Iterator var2 = ElementFilter.fieldsIn(type.getEnclosedElements()).iterator();

      while(var2.hasNext()) {
         VariableElement field = (VariableElement)var2.next();
         if ("value".equals(field.getSimpleName().toString())) {
            member = field;
            break;
         }
      }

      if (member == null && type.getKind().equals(ElementKind.CLASS)) {
         member = getValueMember(type.getSuperclass());
      }

      return member;
   }

   public static boolean isSubElement(TypeElement d1, TypeElement d2) {
      if (d1.equals(d2)) {
         return true;
      } else {
         TypeElement superClassDecl = null;
         if (d1.getKind().equals(ElementKind.CLASS)) {
            TypeMirror superClass = d1.getSuperclass();
            if (!superClass.getKind().equals(TypeKind.NONE)) {
               superClassDecl = (TypeElement)((DeclaredType)superClass).asElement();
               if (superClassDecl.equals(d2)) {
                  return true;
               }
            }
         }

         Iterator var6 = d1.getInterfaces().iterator();

         do {
            if (!var6.hasNext()) {
               return false;
            }

            TypeMirror superIntf = (TypeMirror)var6.next();
            DeclaredType declaredSuperIntf = (DeclaredType)superIntf;
            if (declaredSuperIntf.asElement().equals(d2)) {
               return true;
            }

            if (isSubElement((TypeElement)declaredSuperIntf.asElement(), d2)) {
               return true;
            }
         } while(superClassDecl == null || !isSubElement(superClassDecl, d2));

         return true;
      }
   }
}
