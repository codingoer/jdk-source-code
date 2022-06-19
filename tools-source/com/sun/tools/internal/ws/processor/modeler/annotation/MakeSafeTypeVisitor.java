package com.sun.tools.internal.ws.processor.modeler.annotation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

public class MakeSafeTypeVisitor extends SimpleTypeVisitor6 {
   TypeElement collectionType;
   TypeElement mapType;

   public MakeSafeTypeVisitor(ProcessingEnvironment processingEnvironment) {
      this.collectionType = processingEnvironment.getElementUtils().getTypeElement(Collection.class.getName());
      this.mapType = processingEnvironment.getElementUtils().getTypeElement(Map.class.getName());
   }

   public TypeMirror visitDeclared(DeclaredType t, Types types) {
      if (!TypeModeler.isSubElement((TypeElement)t.asElement(), this.collectionType) && !TypeModeler.isSubElement((TypeElement)t.asElement(), this.mapType)) {
         return types.erasure(t);
      } else {
         Collection args = t.getTypeArguments();
         TypeMirror[] safeArgs = new TypeMirror[args.size()];
         int i = 0;

         TypeMirror arg;
         for(Iterator var6 = args.iterator(); var6.hasNext(); safeArgs[i++] = (TypeMirror)this.visit(arg, types)) {
            arg = (TypeMirror)var6.next();
         }

         return types.getDeclaredType((TypeElement)t.asElement(), safeArgs);
      }
   }

   public TypeMirror visitNoType(NoType type, Types types) {
      return type;
   }

   protected TypeMirror defaultAction(TypeMirror e, Types types) {
      return types.erasure(e);
   }
}
