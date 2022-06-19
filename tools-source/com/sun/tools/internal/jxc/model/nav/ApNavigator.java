package com.sun.tools.internal.jxc.model.nav;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

public final class ApNavigator implements Navigator {
   private final ProcessingEnvironment env;
   private final PrimitiveType primitiveByte;
   private static final Map primitives = new HashMap();
   private static final TypeMirror DUMMY;
   private final SimpleTypeVisitor6 baseClassFinder = new SimpleTypeVisitor6() {
      public TypeMirror visitDeclared(DeclaredType t, TypeElement sup) {
         if (t.asElement().equals(sup)) {
            return t;
         } else {
            Iterator var3 = ApNavigator.this.env.getTypeUtils().directSupertypes(t).iterator();

            TypeMirror rx;
            do {
               TypeMirror r;
               if (!var3.hasNext()) {
                  TypeMirror superclass = ((TypeElement)t.asElement()).getSuperclass();
                  if (!superclass.getKind().equals(TypeKind.NONE)) {
                     r = this.visitDeclared((DeclaredType)superclass, sup);
                     if (r != null) {
                        return r;
                     }
                  }

                  return null;
               }

               r = (TypeMirror)var3.next();
               rx = this.visitDeclared((DeclaredType)r, sup);
            } while(rx == null);

            return rx;
         }
      }

      public TypeMirror visitTypeVariable(TypeVariable t, TypeElement typeElement) {
         Iterator var3 = ((TypeParameterElement)t.asElement()).getBounds().iterator();

         TypeMirror m;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            TypeMirror typeMirror = (TypeMirror)var3.next();
            m = (TypeMirror)this.visit(typeMirror, typeElement);
         } while(m == null);

         return m;
      }

      public TypeMirror visitArray(ArrayType t, TypeElement typeElement) {
         return null;
      }

      public TypeMirror visitWildcard(WildcardType t, TypeElement typeElement) {
         return (TypeMirror)this.visit(t.getExtendsBound(), typeElement);
      }

      protected TypeMirror defaultAction(TypeMirror e, TypeElement typeElement) {
         return e;
      }
   };

   public ApNavigator(ProcessingEnvironment env) {
      this.env = env;
      this.primitiveByte = env.getTypeUtils().getPrimitiveType(TypeKind.BYTE);
   }

   public TypeElement getSuperClass(TypeElement typeElement) {
      if (typeElement.getKind().equals(ElementKind.CLASS)) {
         TypeMirror sup = typeElement.getSuperclass();
         return !sup.getKind().equals(TypeKind.NONE) ? (TypeElement)((DeclaredType)sup).asElement() : null;
      } else {
         return this.env.getElementUtils().getTypeElement(Object.class.getName());
      }
   }

   public TypeMirror getBaseClass(TypeMirror type, TypeElement sup) {
      return (TypeMirror)this.baseClassFinder.visit(type, sup);
   }

   public String getClassName(TypeElement t) {
      return t.getQualifiedName().toString();
   }

   public String getTypeName(TypeMirror typeMirror) {
      return typeMirror.toString();
   }

   public String getClassShortName(TypeElement t) {
      return t.getSimpleName().toString();
   }

   public Collection getDeclaredFields(TypeElement typeElement) {
      return ElementFilter.fieldsIn(typeElement.getEnclosedElements());
   }

   public VariableElement getDeclaredField(TypeElement clazz, String fieldName) {
      Iterator var3 = ElementFilter.fieldsIn(clazz.getEnclosedElements()).iterator();

      VariableElement fd;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         fd = (VariableElement)var3.next();
      } while(!fd.getSimpleName().toString().equals(fieldName));

      return fd;
   }

   public Collection getDeclaredMethods(TypeElement typeElement) {
      return ElementFilter.methodsIn(typeElement.getEnclosedElements());
   }

   public TypeElement getDeclaringClassForField(VariableElement f) {
      return (TypeElement)f.getEnclosingElement();
   }

   public TypeElement getDeclaringClassForMethod(ExecutableElement m) {
      return (TypeElement)m.getEnclosingElement();
   }

   public TypeMirror getFieldType(VariableElement f) {
      return f.asType();
   }

   public String getFieldName(VariableElement f) {
      return f.getSimpleName().toString();
   }

   public String getMethodName(ExecutableElement m) {
      return m.getSimpleName().toString();
   }

   public TypeMirror getReturnType(ExecutableElement m) {
      return m.getReturnType();
   }

   public TypeMirror[] getMethodParameters(ExecutableElement m) {
      Collection ps = m.getParameters();
      TypeMirror[] r = new TypeMirror[ps.size()];
      int i = 0;

      VariableElement p;
      for(Iterator var5 = ps.iterator(); var5.hasNext(); r[i++] = p.asType()) {
         p = (VariableElement)var5.next();
      }

      return r;
   }

   public boolean isStaticMethod(ExecutableElement m) {
      return this.hasModifier(m, Modifier.STATIC);
   }

   public boolean isFinalMethod(ExecutableElement m) {
      return this.hasModifier(m, Modifier.FINAL);
   }

   private boolean hasModifier(Element d, Modifier mod) {
      return d.getModifiers().contains(mod);
   }

   public boolean isSubClassOf(TypeMirror sub, TypeMirror sup) {
      return sup == DUMMY ? false : this.env.getTypeUtils().isSubtype(sub, sup);
   }

   private String getSourceClassName(Class clazz) {
      Class d = clazz.getDeclaringClass();
      if (d == null) {
         return clazz.getName();
      } else {
         String shortName = clazz.getName().substring(d.getName().length() + 1);
         return this.getSourceClassName(d) + '.' + shortName;
      }
   }

   public TypeMirror ref(Class c) {
      if (c.isArray()) {
         return this.env.getTypeUtils().getArrayType(this.ref(c.getComponentType()));
      } else if (c.isPrimitive()) {
         return this.getPrimitive(c);
      } else {
         TypeElement t = this.env.getElementUtils().getTypeElement(this.getSourceClassName(c));
         return (TypeMirror)(t == null ? DUMMY : this.env.getTypeUtils().getDeclaredType(t, new TypeMirror[0]));
      }
   }

   public TypeMirror use(TypeElement t) {
      assert t != null;

      return this.env.getTypeUtils().getDeclaredType(t, new TypeMirror[0]);
   }

   public TypeElement asDecl(TypeMirror m) {
      m = this.env.getTypeUtils().erasure(m);
      if (m.getKind().equals(TypeKind.DECLARED)) {
         DeclaredType d = (DeclaredType)m;
         return (TypeElement)d.asElement();
      } else {
         return null;
      }
   }

   public TypeElement asDecl(Class c) {
      return this.env.getElementUtils().getTypeElement(this.getSourceClassName(c));
   }

   public TypeMirror erasure(TypeMirror t) {
      Types tu = this.env.getTypeUtils();
      t = tu.erasure(t);
      if (t.getKind().equals(TypeKind.DECLARED)) {
         DeclaredType dt = (DeclaredType)t;
         if (!dt.getTypeArguments().isEmpty()) {
            return tu.getDeclaredType((TypeElement)dt.asElement(), new TypeMirror[0]);
         }
      }

      return t;
   }

   public boolean isAbstract(TypeElement clazz) {
      return this.hasModifier(clazz, Modifier.ABSTRACT);
   }

   public boolean isFinal(TypeElement clazz) {
      return this.hasModifier(clazz, Modifier.FINAL);
   }

   public VariableElement[] getEnumConstants(TypeElement clazz) {
      List elements = this.env.getElementUtils().getAllMembers(clazz);
      Collection constants = new ArrayList();
      Iterator var4 = elements.iterator();

      while(var4.hasNext()) {
         Element element = (Element)var4.next();
         if (element.getKind().equals(ElementKind.ENUM_CONSTANT)) {
            constants.add((VariableElement)element);
         }
      }

      return (VariableElement[])constants.toArray(new VariableElement[constants.size()]);
   }

   public TypeMirror getVoidType() {
      return this.env.getTypeUtils().getNoType(TypeKind.VOID);
   }

   public String getPackageName(TypeElement clazz) {
      return this.env.getElementUtils().getPackageOf(clazz).getQualifiedName().toString();
   }

   public TypeElement loadObjectFactory(TypeElement referencePoint, String packageName) {
      return this.env.getElementUtils().getTypeElement(packageName + ".ObjectFactory");
   }

   public boolean isBridgeMethod(ExecutableElement method) {
      return method.getModifiers().contains(Modifier.VOLATILE);
   }

   public boolean isOverriding(ExecutableElement method, TypeElement base) {
      Elements elements = this.env.getElementUtils();

      while(true) {
         Iterator var4 = ElementFilter.methodsIn(elements.getAllMembers(base)).iterator();

         while(var4.hasNext()) {
            ExecutableElement m = (ExecutableElement)var4.next();
            if (elements.overrides(method, m, base)) {
               return true;
            }
         }

         if (base.getSuperclass().getKind().equals(TypeKind.NONE)) {
            return false;
         }

         base = (TypeElement)this.env.getTypeUtils().asElement(base.getSuperclass());
      }
   }

   public boolean isInterface(TypeElement clazz) {
      return clazz.getKind().isInterface();
   }

   public boolean isTransient(VariableElement f) {
      return f.getModifiers().contains(Modifier.TRANSIENT);
   }

   public boolean isInnerClass(TypeElement clazz) {
      return clazz.getEnclosingElement() != null && !clazz.getModifiers().contains(Modifier.STATIC);
   }

   public boolean isSameType(TypeMirror t1, TypeMirror t2) {
      return this.env.getTypeUtils().isSameType(t1, t2);
   }

   public boolean isArray(TypeMirror type) {
      return type != null && type.getKind().equals(TypeKind.ARRAY);
   }

   public boolean isArrayButNotByteArray(TypeMirror t) {
      if (!this.isArray(t)) {
         return false;
      } else {
         ArrayType at = (ArrayType)t;
         TypeMirror ct = at.getComponentType();
         return !ct.equals(this.primitiveByte);
      }
   }

   public TypeMirror getComponentType(TypeMirror t) {
      if (this.isArray(t)) {
         ArrayType at = (ArrayType)t;
         return at.getComponentType();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public TypeMirror getTypeArgument(TypeMirror typeMirror, int i) {
      if (typeMirror != null && typeMirror.getKind().equals(TypeKind.DECLARED)) {
         DeclaredType declaredType = (DeclaredType)typeMirror;
         TypeMirror[] args = (TypeMirror[])declaredType.getTypeArguments().toArray(new TypeMirror[declaredType.getTypeArguments().size()]);
         return args[i];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean isParameterizedType(TypeMirror typeMirror) {
      if (typeMirror != null && typeMirror.getKind().equals(TypeKind.DECLARED)) {
         DeclaredType d = (DeclaredType)typeMirror;
         return !d.getTypeArguments().isEmpty();
      } else {
         return false;
      }
   }

   public boolean isPrimitive(TypeMirror t) {
      return t.getKind().isPrimitive();
   }

   public TypeMirror getPrimitive(Class primitiveType) {
      assert primitiveType.isPrimitive();

      return (TypeMirror)(primitiveType == Void.TYPE ? this.getVoidType() : this.env.getTypeUtils().getPrimitiveType((TypeKind)primitives.get(primitiveType)));
   }

   public Location getClassLocation(TypeElement typeElement) {
      Trees trees = Trees.instance(this.env);
      return this.getLocation(typeElement.getQualifiedName().toString(), trees.getPath(typeElement));
   }

   public Location getFieldLocation(VariableElement variableElement) {
      return this.getLocation(variableElement);
   }

   public Location getMethodLocation(ExecutableElement executableElement) {
      return this.getLocation(executableElement);
   }

   public boolean hasDefaultConstructor(TypeElement t) {
      if (t != null && t.getKind().equals(ElementKind.CLASS)) {
         Iterator var2 = ElementFilter.constructorsIn(this.env.getElementUtils().getAllMembers(t)).iterator();

         ExecutableElement init;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            init = (ExecutableElement)var2.next();
         } while(!init.getParameters().isEmpty());

         return true;
      } else {
         return false;
      }
   }

   public boolean isStaticField(VariableElement f) {
      return this.hasModifier(f, Modifier.STATIC);
   }

   public boolean isPublicMethod(ExecutableElement m) {
      return this.hasModifier(m, Modifier.PUBLIC);
   }

   public boolean isPublicField(VariableElement f) {
      return this.hasModifier(f, Modifier.PUBLIC);
   }

   public boolean isEnum(TypeElement t) {
      return t != null && t.getKind().equals(ElementKind.ENUM);
   }

   private Location getLocation(Element element) {
      Trees trees = Trees.instance(this.env);
      return this.getLocation(((TypeElement)element.getEnclosingElement()).getQualifiedName() + "." + element.getSimpleName(), trees.getPath(element));
   }

   private Location getLocation(final String name, final TreePath treePath) {
      return new Location() {
         public String toString() {
            if (treePath == null) {
               return name + " (Unknown Source)";
            } else {
               CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
               Trees trees = Trees.instance(ApNavigator.this.env);
               long startPosition = trees.getSourcePositions().getStartPosition(compilationUnit, treePath.getLeaf());
               return name + "(" + compilationUnit.getSourceFile().getName() + ":" + compilationUnit.getLineMap().getLineNumber(startPosition) + ")";
            }
         }
      };
   }

   static {
      primitives.put(Integer.TYPE, TypeKind.INT);
      primitives.put(Byte.TYPE, TypeKind.BYTE);
      primitives.put(Float.TYPE, TypeKind.FLOAT);
      primitives.put(Boolean.TYPE, TypeKind.BOOLEAN);
      primitives.put(Short.TYPE, TypeKind.SHORT);
      primitives.put(Long.TYPE, TypeKind.LONG);
      primitives.put(Double.TYPE, TypeKind.DOUBLE);
      primitives.put(Character.TYPE, TypeKind.CHAR);
      DUMMY = new TypeMirror() {
         public Object accept(TypeVisitor v, Object p) {
            throw new IllegalStateException();
         }

         public TypeKind getKind() {
            throw new IllegalStateException();
         }

         public List getAnnotationMirrors() {
            throw new IllegalStateException();
         }

         public Annotation getAnnotation(Class annotationType) {
            throw new IllegalStateException();
         }

         public Annotation[] getAnnotationsByType(Class annotationType) {
            throw new IllegalStateException();
         }
      };
   }
}
