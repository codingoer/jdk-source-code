package com.sun.tools.internal.ws.processor.modeler.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class TypeMonikerFactory {
   public static TypeMoniker getTypeMoniker(TypeMirror typeMirror) {
      if (typeMirror == null) {
         throw new NullPointerException();
      } else if (typeMirror.getKind().isPrimitive()) {
         return new PrimitiveTypeMoniker((PrimitiveType)typeMirror);
      } else if (typeMirror.getKind().equals(TypeKind.ARRAY)) {
         return new ArrayTypeMoniker((ArrayType)typeMirror);
      } else {
         return (TypeMoniker)(typeMirror.getKind().equals(TypeKind.DECLARED) ? new DeclaredTypeMoniker((DeclaredType)typeMirror) : getTypeMoniker(typeMirror.toString()));
      }
   }

   public static TypeMoniker getTypeMoniker(String typeName) {
      return new StringMoniker(typeName);
   }

   static class StringMoniker implements TypeMoniker {
      private String typeName;

      public StringMoniker(String typeName) {
         this.typeName = typeName;
      }

      public TypeMirror create(ProcessingEnvironment apEnv) {
         return apEnv.getTypeUtils().getDeclaredType(apEnv.getElementUtils().getTypeElement(this.typeName), new TypeMirror[0]);
      }
   }

   static class PrimitiveTypeMoniker implements TypeMoniker {
      private TypeKind kind;

      public PrimitiveTypeMoniker(PrimitiveType type) {
         this.kind = type.getKind();
      }

      public TypeMirror create(ProcessingEnvironment apEnv) {
         return apEnv.getTypeUtils().getPrimitiveType(this.kind);
      }
   }

   static class DeclaredTypeMoniker implements TypeMoniker {
      private Name typeDeclName;
      private Collection typeArgs = new ArrayList();

      public DeclaredTypeMoniker(DeclaredType type) {
         this.typeDeclName = ((TypeElement)type.asElement()).getQualifiedName();
         Iterator var2 = type.getTypeArguments().iterator();

         while(var2.hasNext()) {
            TypeMirror arg = (TypeMirror)var2.next();
            this.typeArgs.add(TypeMonikerFactory.getTypeMoniker(arg));
         }

      }

      public TypeMirror create(ProcessingEnvironment apEnv) {
         TypeElement typeDecl = apEnv.getElementUtils().getTypeElement(this.typeDeclName);
         TypeMirror[] tmpArgs = new TypeMirror[this.typeArgs.size()];
         int idx = 0;

         TypeMoniker moniker;
         for(Iterator var5 = this.typeArgs.iterator(); var5.hasNext(); tmpArgs[idx++] = moniker.create(apEnv)) {
            moniker = (TypeMoniker)var5.next();
         }

         return apEnv.getTypeUtils().getDeclaredType(typeDecl, tmpArgs);
      }
   }

   static class ArrayTypeMoniker implements TypeMoniker {
      private TypeMoniker arrayType;

      public ArrayTypeMoniker(ArrayType type) {
         this.arrayType = TypeMonikerFactory.getTypeMoniker(type.getComponentType());
      }

      public TypeMirror create(ProcessingEnvironment apEnv) {
         return apEnv.getTypeUtils().getArrayType(this.arrayType.create(apEnv));
      }
   }
}
