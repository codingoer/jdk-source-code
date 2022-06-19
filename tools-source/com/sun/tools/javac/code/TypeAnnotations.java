package com.sun.tools.javac.code;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.Iterator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;

public class TypeAnnotations {
   protected static final Context.Key typeAnnosKey = new Context.Key();
   final Log log;
   final Names names;
   final Symtab syms;
   final Annotate annotate;
   final Attr attr;

   public static TypeAnnotations instance(Context var0) {
      TypeAnnotations var1 = (TypeAnnotations)var0.get(typeAnnosKey);
      if (var1 == null) {
         var1 = new TypeAnnotations(var0);
      }

      return var1;
   }

   protected TypeAnnotations(Context var1) {
      var1.put((Context.Key)typeAnnosKey, (Object)this);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.annotate = Annotate.instance(var1);
      this.attr = Attr.instance(var1);
      Options var2 = Options.instance(var1);
   }

   public void organizeTypeAnnotationsSignatures(final Env var1, final JCTree.JCClassDecl var2) {
      this.annotate.afterRepeated(new Annotate.Worker() {
         public void run() {
            JavaFileObject var1x = TypeAnnotations.this.log.useSource(var1.toplevel.sourcefile);

            try {
               (TypeAnnotations.this.new TypeAnnotationPositions(true)).scan(var2);
            } finally {
               TypeAnnotations.this.log.useSource(var1x);
            }

         }
      });
   }

   public void validateTypeAnnotationsSignatures(final Env var1, final JCTree.JCClassDecl var2) {
      this.annotate.validate(new Annotate.Worker() {
         public void run() {
            JavaFileObject var1x = TypeAnnotations.this.log.useSource(var1.toplevel.sourcefile);

            try {
               TypeAnnotations.this.attr.validateTypeAnnotations(var2, true);
            } finally {
               TypeAnnotations.this.log.useSource(var1x);
            }

         }
      });
   }

   public void organizeTypeAnnotationsBodies(JCTree.JCClassDecl var1) {
      (new TypeAnnotationPositions(false)).scan(var1);
   }

   public AnnotationType annotationType(Attribute.Compound var1, Symbol var2) {
      Attribute.Compound var3 = var1.type.tsym.attribute(this.syms.annotationTargetType.tsym);
      if (var3 == null) {
         return inferTargetMetaInfo(var1, var2);
      } else {
         Attribute var4 = var3.member(this.names.value);
         if (!(var4 instanceof Attribute.Array)) {
            Assert.error("annotationType(): bad @Target argument " + var4 + " (" + var4.getClass() + ")");
            return TypeAnnotations.AnnotationType.DECLARATION;
         } else {
            Attribute.Array var5 = (Attribute.Array)var4;
            boolean var6 = false;
            boolean var7 = false;
            Attribute[] var8 = var5.values;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Attribute var11 = var8[var10];
               if (!(var11 instanceof Attribute.Enum)) {
                  Assert.error("annotationType(): unrecognized Attribute kind " + var11 + " (" + var11.getClass() + ")");
                  var6 = true;
               } else {
                  Attribute.Enum var12 = (Attribute.Enum)var11;
                  if (var12.value.name == this.names.TYPE) {
                     if (var2.kind == 2) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.FIELD) {
                     if (var2.kind == 4 && var2.owner.kind != 16) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.METHOD) {
                     if (var2.kind == 16 && !var2.isConstructor()) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.PARAMETER) {
                     if (var2.kind == 4 && var2.owner.kind == 16 && (var2.flags() & 8589934592L) != 0L) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.CONSTRUCTOR) {
                     if (var2.kind == 16 && var2.isConstructor()) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.LOCAL_VARIABLE) {
                     if (var2.kind == 4 && var2.owner.kind == 16 && (var2.flags() & 8589934592L) == 0L) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.ANNOTATION_TYPE) {
                     if (var2.kind == 2 && (var2.flags() & 8192L) != 0L) {
                        var6 = true;
                     }
                  } else if (var12.value.name == this.names.PACKAGE) {
                     if (var2.kind == 1) {
                        var6 = true;
                     }
                  } else if (var12.value.name != this.names.TYPE_USE) {
                     if (var12.value.name != this.names.TYPE_PARAMETER) {
                        Assert.error("annotationType(): unrecognized Attribute name " + var12.value.name + " (" + var12.value.name.getClass() + ")");
                        var6 = true;
                     }
                  } else if (var2.kind == 2 || var2.kind == 4 || var2.kind == 16 && !var2.isConstructor() && !var2.type.getReturnType().hasTag(TypeTag.VOID) || var2.kind == 16 && var2.isConstructor()) {
                     var7 = true;
                  }
               }
            }

            if (var6 && var7) {
               return TypeAnnotations.AnnotationType.BOTH;
            } else if (var7) {
               return TypeAnnotations.AnnotationType.TYPE;
            } else {
               return TypeAnnotations.AnnotationType.DECLARATION;
            }
         }
      }
   }

   private static AnnotationType inferTargetMetaInfo(Attribute.Compound var0, Symbol var1) {
      return TypeAnnotations.AnnotationType.DECLARATION;
   }

   private class TypeAnnotationPositions extends TreeScanner {
      private final boolean sigOnly;
      private ListBuffer frames = new ListBuffer();
      private boolean isInClass = false;
      private JCTree.JCLambda currentLambda = null;

      TypeAnnotationPositions(boolean var2) {
         this.sigOnly = var2;
      }

      protected void push(JCTree var1) {
         this.frames = this.frames.prepend(var1);
      }

      protected JCTree pop() {
         return (JCTree)this.frames.next();
      }

      private JCTree peek2() {
         return (JCTree)this.frames.toList().tail.head;
      }

      public void scan(JCTree var1) {
         this.push(var1);
         super.scan(var1);
         this.pop();
      }

      private void separateAnnotationsKinds(JCTree var1, Type var2, Symbol var3, TypeAnnotationPosition var4) {
         List var5 = var3.getRawAttributes();
         ListBuffer var6 = new ListBuffer();
         ListBuffer var7 = new ListBuffer();
         ListBuffer var8 = new ListBuffer();
         Iterator var9 = var5.iterator();

         while(var9.hasNext()) {
            Attribute.Compound var10 = (Attribute.Compound)var9.next();
            Attribute.TypeCompound var11;
            switch (TypeAnnotations.this.annotationType(var10, var3)) {
               case DECLARATION:
                  var6.append(var10);
                  break;
               case BOTH:
                  var6.append(var10);
                  var11 = this.toTypeCompound(var10, var4);
                  var7.append(var11);
                  break;
               case TYPE:
                  var11 = this.toTypeCompound(var10, var4);
                  var7.append(var11);
                  var8.append(var11);
            }
         }

         var3.resetAnnotations();
         var3.setDeclarationAttributes(var6.toList());
         if (!var7.isEmpty()) {
            List var16 = var7.toList();
            if (var2 == null) {
               var2 = var3.getEnclosingElement().asType();
               this.typeWithAnnotations(var1, var2, var16, var16);
               var3.appendUniqueTypeAttributes(var16);
            } else {
               var2 = this.typeWithAnnotations(var1, var2, var16, var8.toList());
               if (var3.getKind() == ElementKind.METHOD) {
                  var3.type.asMethodType().restype = var2;
               } else if (var3.getKind() == ElementKind.PARAMETER) {
                  var3.type = var2;
                  if (var3.getQualifiedName().equals(TypeAnnotations.this.names._this)) {
                     var3.owner.type.asMethodType().recvtype = var2;
                  } else {
                     Type.MethodType var14 = var3.owner.type.asMethodType();
                     List var15 = ((Symbol.MethodSymbol)var3.owner).params;
                     List var12 = var14.argtypes;

                     ListBuffer var13;
                     for(var13 = new ListBuffer(); var15.nonEmpty(); var15 = var15.tail) {
                        if (var15.head == var3) {
                           var13.add(var2);
                        } else {
                           var13.add(var12.head);
                        }

                        var12 = var12.tail;
                     }

                     var14.argtypes = var13.toList();
                  }
               } else {
                  var3.type = var2;
               }

               var3.appendUniqueTypeAttributes(var16);
               if (var3.getKind() == ElementKind.PARAMETER || var3.getKind() == ElementKind.LOCAL_VARIABLE || var3.getKind() == ElementKind.RESOURCE_VARIABLE || var3.getKind() == ElementKind.EXCEPTION_PARAMETER) {
                  var3.owner.appendUniqueTypeAttributes(var3.getRawTypeAttributes());
               }

            }
         }
      }

      private Type typeWithAnnotations(JCTree var1, Type var2, List var3, List var4) {
         if (var3.isEmpty()) {
            return var2;
         } else {
            Object var7;
            Type var20;
            if (var2.hasTag(TypeTag.ARRAY)) {
               Type.ArrayType var14 = (Type.ArrayType)var2.unannotatedType();
               Type.ArrayType var16 = new Type.ArrayType((Type)null, var14.tsym);
               if (var2.isAnnotated()) {
                  var7 = var16.annotatedType(var2.getAnnotationMirrors());
               } else {
                  var7 = var16;
               }

               JCTree.JCArrayTypeTree var18 = this.arrayTypeTree(var1);
               ListBuffer var19 = new ListBuffer();

               for(var19 = var19.append(TypeAnnotationPosition.TypePathEntry.ARRAY); var14.elemtype.hasTag(TypeTag.ARRAY); var19 = var19.append(TypeAnnotationPosition.TypePathEntry.ARRAY)) {
                  if (var14.elemtype.isAnnotated()) {
                     var20 = var14.elemtype;
                     var14 = (Type.ArrayType)var20.unannotatedType();
                     Type.ArrayType var21 = var16;
                     var16 = new Type.ArrayType((Type)null, var14.tsym);
                     var21.elemtype = var16.annotatedType(var14.elemtype.getAnnotationMirrors());
                  } else {
                     var14 = (Type.ArrayType)var14.elemtype;
                     var16.elemtype = new Type.ArrayType((Type)null, var14.tsym);
                     var16 = (Type.ArrayType)var16.elemtype;
                  }

                  var18 = this.arrayTypeTree(var18.elemtype);
               }

               var20 = this.typeWithAnnotations(var18.elemtype, var14.elemtype, var3, var4);
               var16.elemtype = var20;
               Attribute.TypeCompound var22 = (Attribute.TypeCompound)var3.get(0);
               TypeAnnotationPosition var12 = var22.position;
               var12.location = var12.location.prependList(var19.toList());
               var1.type = (Type)var7;
               return (Type)var7;
            } else if (var2.hasTag(TypeTag.TYPEVAR)) {
               return var2;
            } else if (var2.getKind() == TypeKind.UNION) {
               JCTree.JCTypeUnion var13 = (JCTree.JCTypeUnion)var1;
               JCTree.JCExpression var15 = (JCTree.JCExpression)var13.alternatives.get(0);
               Type var17 = this.typeWithAnnotations(var15, var15.type, var3, var4);
               var15.type = var17;
               return var2;
            } else {
               Type var5 = var2;
               Object var6 = var2.asElement();
               var7 = var1;

               while(var6 != null && ((Element)var6).getKind() != ElementKind.PACKAGE && var5 != null && var5.getKind() != TypeKind.NONE && var5.getKind() != TypeKind.ERROR && (((JCTree)var7).getKind() == Tree.Kind.MEMBER_SELECT || ((JCTree)var7).getKind() == Tree.Kind.PARAMETERIZED_TYPE || ((JCTree)var7).getKind() == Tree.Kind.ANNOTATED_TYPE)) {
                  if (((JCTree)var7).getKind() == Tree.Kind.MEMBER_SELECT) {
                     var5 = var5.getEnclosingType();
                     var6 = ((Element)var6).getEnclosingElement();
                     var7 = ((JCTree.JCFieldAccess)var7).getExpression();
                  } else if (((JCTree)var7).getKind() == Tree.Kind.PARAMETERIZED_TYPE) {
                     var7 = ((JCTree.JCTypeApply)var7).getType();
                  } else {
                     var7 = ((JCTree.JCAnnotatedType)var7).getUnderlyingType();
                  }
               }

               if (var5 != null && var5.hasTag(TypeTag.NONE)) {
                  switch (var4.size()) {
                     case 0:
                        break;
                     case 1:
                        TypeAnnotations.this.log.error(var1.pos(), "cant.type.annotate.scoping.1", new Object[]{var4});
                        break;
                     default:
                        TypeAnnotations.this.log.error(var1.pos(), "cant.type.annotate.scoping", new Object[]{var4});
                  }

                  return var2;
               } else {
                  ListBuffer var8 = new ListBuffer();
                  Type var9 = var5;

                  while(var6 != null && ((Element)var6).getKind() != ElementKind.PACKAGE && var9 != null && var9.getKind() != TypeKind.NONE && var9.getKind() != TypeKind.ERROR) {
                     var9 = var9.getEnclosingType();
                     var6 = ((Element)var6).getEnclosingElement();
                     if (var9 != null && var9.getKind() != TypeKind.NONE) {
                        var8 = var8.append(TypeAnnotationPosition.TypePathEntry.INNER_TYPE);
                     }
                  }

                  if (var8.nonEmpty()) {
                     Attribute.TypeCompound var10 = (Attribute.TypeCompound)var3.get(0);
                     TypeAnnotationPosition var11 = var10.position;
                     var11.location = var11.location.appendList(var8.toList());
                  }

                  var20 = this.typeWithAnnotations(var2, var5, var3);
                  var1.type = var20;
                  return var20;
               }
            }
         }
      }

      private JCTree.JCArrayTypeTree arrayTypeTree(JCTree var1) {
         if (var1.getKind() == Tree.Kind.ARRAY_TYPE) {
            return (JCTree.JCArrayTypeTree)var1;
         } else if (var1.getKind() == Tree.Kind.ANNOTATED_TYPE) {
            return (JCTree.JCArrayTypeTree)((JCTree.JCAnnotatedType)var1).underlyingType;
         } else {
            Assert.error("Could not determine array type from type tree: " + var1);
            return null;
         }
      }

      private Type typeWithAnnotations(Type var1, final Type var2, List var3) {
         Type.Visitor var4 = new Type.Visitor() {
            public Type visitClassType(Type.ClassType var1, List var2x) {
               if (var1 != var2 && var1.getEnclosingType() != Type.noType) {
                  Type.ClassType var3 = new Type.ClassType((Type)var1.getEnclosingType().accept((Type.Visitor)this, var2x), var1.typarams_field, var1.tsym);
                  var3.all_interfaces_field = var1.all_interfaces_field;
                  var3.allparams_field = var1.allparams_field;
                  var3.interfaces_field = var1.interfaces_field;
                  var3.rank_field = var1.rank_field;
                  var3.supertype_field = var1.supertype_field;
                  return var3;
               } else {
                  return var1.annotatedType(var2x);
               }
            }

            public Type visitAnnotatedType(Type.AnnotatedType var1, List var2x) {
               return ((Type)var1.unannotatedType().accept((Type.Visitor)this, var2x)).annotatedType(var1.getAnnotationMirrors());
            }

            public Type visitWildcardType(Type.WildcardType var1, List var2x) {
               return var1.annotatedType(var2x);
            }

            public Type visitArrayType(Type.ArrayType var1, List var2x) {
               Type.ArrayType var3 = new Type.ArrayType((Type)var1.elemtype.accept((Type.Visitor)this, var2x), var1.tsym);
               return var3;
            }

            public Type visitMethodType(Type.MethodType var1, List var2x) {
               return var1;
            }

            public Type visitPackageType(Type.PackageType var1, List var2x) {
               return var1;
            }

            public Type visitTypeVar(Type.TypeVar var1, List var2x) {
               return var1.annotatedType(var2x);
            }

            public Type visitCapturedType(Type.CapturedType var1, List var2x) {
               return var1.annotatedType(var2x);
            }

            public Type visitForAll(Type.ForAll var1, List var2x) {
               return var1;
            }

            public Type visitUndetVar(Type.UndetVar var1, List var2x) {
               return var1;
            }

            public Type visitErrorType(Type.ErrorType var1, List var2x) {
               return var1.annotatedType(var2x);
            }

            public Type visitType(Type var1, List var2x) {
               return var1.annotatedType(var2x);
            }
         };
         return (Type)var1.accept((Type.Visitor)var4, var3);
      }

      private Attribute.TypeCompound toTypeCompound(Attribute.Compound var1, TypeAnnotationPosition var2) {
         return new Attribute.TypeCompound(var1, var2);
      }

      private void resolveFrame(JCTree var1, JCTree var2, List var3, TypeAnnotationPosition var4) {
         List var8;
         JCTree.JCClassDecl var11;
         JCTree.JCMethodDecl var12;
         switch (var2.getKind()) {
            case TYPE_CAST:
               JCTree.JCTypeCast var5 = (JCTree.JCTypeCast)var2;
               var4.type = TargetType.CAST;
               if (!var5.clazz.hasTag(JCTree.Tag.TYPEINTERSECTION)) {
                  var4.type_index = 0;
               }

               var4.pos = var2.pos;
               return;
            case INSTANCE_OF:
               var4.type = TargetType.INSTANCEOF;
               var4.pos = var2.pos;
               return;
            case NEW_CLASS:
               JCTree.JCNewClass var6 = (JCTree.JCNewClass)var2;
               if (var6.def != null) {
                  var11 = var6.def;
                  if (var11.extending == var1) {
                     var4.type = TargetType.CLASS_EXTENDS;
                     var4.type_index = -1;
                  } else if (var11.implementing.contains(var1)) {
                     var4.type = TargetType.CLASS_EXTENDS;
                     var4.type_index = var11.implementing.indexOf(var1);
                  } else {
                     Assert.error("Could not determine position of tree " + var1 + " within frame " + var2);
                  }
               } else if (var6.typeargs.contains(var1)) {
                  var4.type = TargetType.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT;
                  var4.type_index = var6.typeargs.indexOf(var1);
               } else {
                  var4.type = TargetType.NEW;
               }

               var4.pos = var2.pos;
               return;
            case NEW_ARRAY:
               var4.type = TargetType.NEW;
               var4.pos = var2.pos;
               return;
            case ANNOTATION_TYPE:
            case CLASS:
            case ENUM:
            case INTERFACE:
               var4.pos = var2.pos;
               if (((JCTree.JCClassDecl)var2).extending == var1) {
                  var4.type = TargetType.CLASS_EXTENDS;
                  var4.type_index = -1;
               } else if (((JCTree.JCClassDecl)var2).implementing.contains(var1)) {
                  var4.type = TargetType.CLASS_EXTENDS;
                  var4.type_index = ((JCTree.JCClassDecl)var2).implementing.indexOf(var1);
               } else if (((JCTree.JCClassDecl)var2).typarams.contains(var1)) {
                  var4.type = TargetType.CLASS_TYPE_PARAMETER;
                  var4.parameter_index = ((JCTree.JCClassDecl)var2).typarams.indexOf(var1);
               } else {
                  Assert.error("Could not determine position of tree " + var1 + " within frame " + var2);
               }

               return;
            case METHOD:
               var12 = (JCTree.JCMethodDecl)var2;
               var4.pos = var2.pos;
               if (var12.thrown.contains(var1)) {
                  var4.type = TargetType.THROWS;
                  var4.type_index = var12.thrown.indexOf(var1);
               } else if (var12.restype == var1) {
                  var4.type = TargetType.METHOD_RETURN;
               } else if (var12.typarams.contains(var1)) {
                  var4.type = TargetType.METHOD_TYPE_PARAMETER;
                  var4.parameter_index = var12.typarams.indexOf(var1);
               } else {
                  Assert.error("Could not determine position of tree " + var1 + " within frame " + var2);
               }

               return;
            case PARAMETERIZED_TYPE:
               List var18 = var3.tail;
               if (((JCTree.JCTypeApply)var2).clazz != var1) {
                  if (((JCTree.JCTypeApply)var2).arguments.contains(var1)) {
                     JCTree.JCTypeApply var25 = (JCTree.JCTypeApply)var2;
                     int var22 = var25.arguments.indexOf(var1);
                     var4.location = var4.location.prepend(new TypeAnnotationPosition.TypePathEntry(TypeAnnotationPosition.TypePathEntryKind.TYPE_ARGUMENT, var22));
                     Type var23;
                     if (var18.tail != null && ((JCTree)var18.tail.head).hasTag(JCTree.Tag.NEWCLASS)) {
                        var23 = ((JCTree)var18.tail.head).type;
                     } else {
                        var23 = var25.type;
                     }

                     this.locateNestedTypes(var23, var4);
                  } else {
                     Assert.error("Could not determine type argument position of tree " + var1 + " within frame " + var2);
                  }
               }

               this.resolveFrame((JCTree)var18.head, (JCTree)var18.tail.head, var18, var4);
               return;
            case MEMBER_REFERENCE:
               JCTree.JCMemberReference var15 = (JCTree.JCMemberReference)var2;
               if (var15.expr == var1) {
                  switch (var15.mode) {
                     case INVOKE:
                        var4.type = TargetType.METHOD_REFERENCE;
                        break;
                     case NEW:
                        var4.type = TargetType.CONSTRUCTOR_REFERENCE;
                        break;
                     default:
                        Assert.error("Unknown method reference mode " + var15.mode + " for tree " + var1 + " within frame " + var2);
                  }

                  var4.pos = var2.pos;
               } else if (var15.typeargs != null && var15.typeargs.contains(var1)) {
                  int var24 = var15.typeargs.indexOf(var1);
                  var4.type_index = var24;
                  switch (var15.mode) {
                     case INVOKE:
                        var4.type = TargetType.METHOD_REFERENCE_TYPE_ARGUMENT;
                        break;
                     case NEW:
                        var4.type = TargetType.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT;
                        break;
                     default:
                        Assert.error("Unknown method reference mode " + var15.mode + " for tree " + var1 + " within frame " + var2);
                  }

                  var4.pos = var2.pos;
               } else {
                  Assert.error("Could not determine type argument position of tree " + var1 + " within frame " + var2);
               }

               return;
            case ARRAY_TYPE:
               ListBuffer var13 = new ListBuffer();
               var13 = var13.append(TypeAnnotationPosition.TypePathEntry.ARRAY);
               var8 = var3.tail;

               while(true) {
                  while(true) {
                     JCTree var21 = (JCTree)var8.tail.head;
                     if (var21.hasTag(JCTree.Tag.TYPEARRAY)) {
                        var8 = var8.tail;
                        var13 = var13.append(TypeAnnotationPosition.TypePathEntry.ARRAY);
                     } else {
                        if (!var21.hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
                           var4.location = var4.location.prependList(var13.toList());
                           this.resolveFrame((JCTree)var8.head, (JCTree)var8.tail.head, var8, var4);
                           return;
                        }

                        var8 = var8.tail;
                     }
                  }
               }
            case TYPE_PARAMETER:
               if (((JCTree)var3.tail.tail.head).hasTag(JCTree.Tag.CLASSDEF)) {
                  var11 = (JCTree.JCClassDecl)var3.tail.tail.head;
                  var4.type = TargetType.CLASS_TYPE_PARAMETER_BOUND;
                  var4.parameter_index = var11.typarams.indexOf(var3.tail.head);
                  var4.bound_index = ((JCTree.JCTypeParameter)var2).bounds.indexOf(var1);
                  if (((JCTree.JCExpression)((JCTree.JCTypeParameter)var2).bounds.get(0)).type.isInterface()) {
                     ++var4.bound_index;
                  }
               } else if (((JCTree)var3.tail.tail.head).hasTag(JCTree.Tag.METHODDEF)) {
                  var12 = (JCTree.JCMethodDecl)var3.tail.tail.head;
                  var4.type = TargetType.METHOD_TYPE_PARAMETER_BOUND;
                  var4.parameter_index = var12.typarams.indexOf(var3.tail.head);
                  var4.bound_index = ((JCTree.JCTypeParameter)var2).bounds.indexOf(var1);
                  if (((JCTree.JCExpression)((JCTree.JCTypeParameter)var2).bounds.get(0)).type.isInterface()) {
                     ++var4.bound_index;
                  }
               } else {
                  Assert.error("Could not determine position of tree " + var1 + " within frame " + var2);
               }

               var4.pos = var2.pos;
               return;
            case VARIABLE:
               Symbol.VarSymbol var7 = ((JCTree.JCVariableDecl)var2).sym;
               var4.pos = var2.pos;
               switch (var7.getKind()) {
                  case LOCAL_VARIABLE:
                     var4.type = TargetType.LOCAL_VARIABLE;
                     break;
                  case FIELD:
                     var4.type = TargetType.FIELD;
                     break;
                  case PARAMETER:
                     if (var7.getQualifiedName().equals(TypeAnnotations.this.names._this)) {
                        var4.type = TargetType.METHOD_RECEIVER;
                     } else {
                        var4.type = TargetType.METHOD_FORMAL_PARAMETER;
                        var4.parameter_index = this.methodParamIndex(var3, var2);
                     }
                     break;
                  case EXCEPTION_PARAMETER:
                     var4.type = TargetType.EXCEPTION_PARAMETER;
                     break;
                  case RESOURCE_VARIABLE:
                     var4.type = TargetType.RESOURCE_VARIABLE;
                     break;
                  default:
                     Assert.error("Found unexpected type annotation for variable: " + var7 + " with kind: " + var7.getKind());
               }

               if (var7.getKind() != ElementKind.FIELD) {
                  var7.owner.appendUniqueTypeAttributes(var7.getRawTypeAttributes());
               }

               return;
            case ANNOTATED_TYPE:
               if (var2 == var1) {
                  JCTree.JCAnnotatedType var19 = (JCTree.JCAnnotatedType)var2;
                  Type var20 = var19.underlyingType.type;
                  if (var20 == null) {
                     return;
                  }

                  Symbol.TypeSymbol var10 = var20.tsym;
                  if (!var10.getKind().equals(ElementKind.TYPE_PARAMETER) && !var20.getKind().equals(TypeKind.WILDCARD) && !var20.getKind().equals(TypeKind.ARRAY)) {
                     this.locateNestedTypes(var20, var4);
                  }
               }

               var8 = var3.tail;
               this.resolveFrame((JCTree)var8.head, (JCTree)var8.tail.head, var8, var4);
               return;
            case UNION_TYPE:
               var8 = var3.tail;
               this.resolveFrame((JCTree)var8.head, (JCTree)var8.tail.head, var8, var4);
               return;
            case INTERSECTION_TYPE:
               JCTree.JCTypeIntersection var16 = (JCTree.JCTypeIntersection)var2;
               var4.type_index = var16.bounds.indexOf(var1);
               List var17 = var3.tail;
               this.resolveFrame((JCTree)var17.head, (JCTree)var17.tail.head, var17, var4);
               return;
            case METHOD_INVOCATION:
               JCTree.JCMethodInvocation var14 = (JCTree.JCMethodInvocation)var2;
               if (!var14.typeargs.contains(var1)) {
                  Assert.error("{" + var1 + "} is not an argument in the invocation: " + var14);
               }

               Symbol.MethodSymbol var9 = (Symbol.MethodSymbol)TreeInfo.symbol(var14.getMethodSelect());
               if (var9 == null) {
                  Assert.error("could not determine symbol for {" + var14 + "}");
               } else if (var9.isConstructor()) {
                  var4.type = TargetType.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT;
               } else {
                  var4.type = TargetType.METHOD_INVOCATION_TYPE_ARGUMENT;
               }

               var4.pos = var14.pos;
               var4.type_index = var14.typeargs.indexOf(var1);
               return;
            case EXTENDS_WILDCARD:
            case SUPER_WILDCARD:
               var4.location = var4.location.prepend(TypeAnnotationPosition.TypePathEntry.WILDCARD);
               var8 = var3.tail;
               this.resolveFrame((JCTree)var8.head, (JCTree)var8.tail.head, var8, var4);
               return;
            case MEMBER_SELECT:
               var8 = var3.tail;
               this.resolveFrame((JCTree)var8.head, (JCTree)var8.tail.head, var8, var4);
               return;
            default:
               Assert.error("Unresolved frame: " + var2 + " of kind: " + var2.getKind() + "\n    Looking for tree: " + var1);
         }
      }

      private void locateNestedTypes(Type var1, TypeAnnotationPosition var2) {
         ListBuffer var3 = new ListBuffer();

         for(Type var4 = var1.getEnclosingType(); var4 != null && var4.getKind() != TypeKind.NONE && var4.getKind() != TypeKind.ERROR; var4 = var4.getEnclosingType()) {
            var3 = var3.append(TypeAnnotationPosition.TypePathEntry.INNER_TYPE);
         }

         if (var3.nonEmpty()) {
            var2.location = var2.location.prependList(var3.toList());
         }

      }

      private int methodParamIndex(List var1, JCTree var2) {
         List var3;
         for(var3 = var1; ((JCTree)var3.head).getTag() != JCTree.Tag.METHODDEF && ((JCTree)var3.head).getTag() != JCTree.Tag.LAMBDA; var3 = var3.tail) {
         }

         if (((JCTree)var3.head).getTag() == JCTree.Tag.METHODDEF) {
            JCTree.JCMethodDecl var5 = (JCTree.JCMethodDecl)var3.head;
            return var5.params.indexOf(var2);
         } else if (((JCTree)var3.head).getTag() == JCTree.Tag.LAMBDA) {
            JCTree.JCLambda var4 = (JCTree.JCLambda)var3.head;
            return var4.params.indexOf(var2);
         } else {
            Assert.error("methodParamIndex expected to find method or lambda for param: " + var2);
            return -1;
         }
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         if (!this.isInClass) {
            this.isInClass = true;
            if (this.sigOnly) {
               this.scan(var1.mods);
               this.scan(var1.typarams);
               this.scan(var1.extending);
               this.scan(var1.implementing);
            }

            this.scan(var1.defs);
         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         if (var1.sym == null) {
            Assert.error("Visiting tree node before memberEnter");
         }

         if (this.sigOnly) {
            TypeAnnotationPosition var2;
            if (!var1.mods.annotations.isEmpty()) {
               var2 = new TypeAnnotationPosition();
               var2.type = TargetType.METHOD_RETURN;
               if (var1.sym.isConstructor()) {
                  var2.pos = var1.pos;
                  this.separateAnnotationsKinds(var1, (Type)null, var1.sym, var2);
               } else {
                  var2.pos = var1.restype.pos;
                  this.separateAnnotationsKinds(var1.restype, var1.sym.type.getReturnType(), var1.sym, var2);
               }
            }

            if (var1.recvparam != null && var1.recvparam.sym != null && !var1.recvparam.mods.annotations.isEmpty()) {
               var2 = new TypeAnnotationPosition();
               var2.type = TargetType.METHOD_RECEIVER;
               var2.pos = var1.recvparam.vartype.pos;
               this.separateAnnotationsKinds(var1.recvparam.vartype, var1.recvparam.sym.type, var1.recvparam.sym, var2);
            }

            int var6 = 0;

            for(Iterator var3 = var1.params.iterator(); var3.hasNext(); ++var6) {
               JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)var3.next();
               if (!var4.mods.annotations.isEmpty()) {
                  TypeAnnotationPosition var5 = new TypeAnnotationPosition();
                  var5.type = TargetType.METHOD_FORMAL_PARAMETER;
                  var5.parameter_index = var6;
                  var5.pos = var4.vartype.pos;
                  this.separateAnnotationsKinds(var4.vartype, var4.sym.type, var4.sym, var5);
               }
            }
         }

         this.push(var1);
         if (this.sigOnly) {
            this.scan(var1.mods);
            this.scan(var1.restype);
            this.scan(var1.typarams);
            this.scan(var1.recvparam);
            this.scan(var1.params);
            this.scan(var1.thrown);
         } else {
            this.scan(var1.defaultValue);
            this.scan(var1.body);
         }

         this.pop();
      }

      public void visitLambda(JCTree.JCLambda var1) {
         JCTree.JCLambda var2 = this.currentLambda;

         try {
            this.currentLambda = var1;
            int var3 = 0;

            for(Iterator var4 = var1.params.iterator(); var4.hasNext(); ++var3) {
               JCTree.JCVariableDecl var5 = (JCTree.JCVariableDecl)var4.next();
               if (!var5.mods.annotations.isEmpty()) {
                  TypeAnnotationPosition var6 = new TypeAnnotationPosition();
                  var6.type = TargetType.METHOD_FORMAL_PARAMETER;
                  var6.parameter_index = var3;
                  var6.pos = var5.vartype.pos;
                  var6.onLambda = var1;
                  this.separateAnnotationsKinds(var5.vartype, var5.sym.type, var5.sym, var6);
               }
            }

            this.push(var1);
            this.scan(var1.body);
            this.scan(var1.params);
            this.pop();
         } finally {
            this.currentLambda = var2;
         }

      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         if (!var1.mods.annotations.isEmpty()) {
            if (var1.sym == null) {
               Assert.error("Visiting tree node before memberEnter");
            } else if (var1.sym.getKind() != ElementKind.PARAMETER) {
               TypeAnnotationPosition var2;
               if (var1.sym.getKind() == ElementKind.FIELD) {
                  if (this.sigOnly) {
                     var2 = new TypeAnnotationPosition();
                     var2.type = TargetType.FIELD;
                     var2.pos = var1.pos;
                     this.separateAnnotationsKinds(var1.vartype, var1.sym.type, var1.sym, var2);
                  }
               } else if (var1.sym.getKind() == ElementKind.LOCAL_VARIABLE) {
                  var2 = new TypeAnnotationPosition();
                  var2.type = TargetType.LOCAL_VARIABLE;
                  var2.pos = var1.pos;
                  var2.onLambda = this.currentLambda;
                  this.separateAnnotationsKinds(var1.vartype, var1.sym.type, var1.sym, var2);
               } else if (var1.sym.getKind() == ElementKind.EXCEPTION_PARAMETER) {
                  var2 = new TypeAnnotationPosition();
                  var2.type = TargetType.EXCEPTION_PARAMETER;
                  var2.pos = var1.pos;
                  var2.onLambda = this.currentLambda;
                  this.separateAnnotationsKinds(var1.vartype, var1.sym.type, var1.sym, var2);
               } else if (var1.sym.getKind() == ElementKind.RESOURCE_VARIABLE) {
                  var2 = new TypeAnnotationPosition();
                  var2.type = TargetType.RESOURCE_VARIABLE;
                  var2.pos = var1.pos;
                  var2.onLambda = this.currentLambda;
                  this.separateAnnotationsKinds(var1.vartype, var1.sym.type, var1.sym, var2);
               } else if (var1.sym.getKind() != ElementKind.ENUM_CONSTANT) {
                  Assert.error("Unhandled variable kind: " + var1 + " of kind: " + var1.sym.getKind());
               }
            }
         }

         this.push(var1);
         this.scan(var1.mods);
         this.scan(var1.vartype);
         if (!this.sigOnly) {
            this.scan(var1.init);
         }

         this.pop();
      }

      public void visitBlock(JCTree.JCBlock var1) {
         if (!this.sigOnly) {
            this.scan(var1.stats);
         }

      }

      public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
         this.push(var1);
         this.findPosition(var1, var1, var1.annotations);
         this.pop();
         super.visitAnnotatedType(var1);
      }

      public void visitTypeParameter(JCTree.JCTypeParameter var1) {
         this.findPosition(var1, this.peek2(), var1.annotations);
         super.visitTypeParameter(var1);
      }

      private void copyNewClassAnnotationsToOwner(JCTree.JCNewClass var1) {
         Symbol.ClassSymbol var2 = var1.def.sym;
         TypeAnnotationPosition var3 = new TypeAnnotationPosition();
         ListBuffer var4 = new ListBuffer();
         Iterator var5 = var2.getRawTypeAttributes().iterator();

         while(var5.hasNext()) {
            Attribute.TypeCompound var6 = (Attribute.TypeCompound)var5.next();
            var4.append(new Attribute.TypeCompound(var6.type, var6.values, var3));
         }

         var3.type = TargetType.NEW;
         var3.pos = var1.pos;
         var2.owner.appendUniqueTypeAttributes(var4.toList());
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         if (var1.def != null && !var1.def.mods.annotations.isEmpty()) {
            JCTree.JCClassDecl var2 = var1.def;
            TypeAnnotationPosition var3 = new TypeAnnotationPosition();
            var3.type = TargetType.CLASS_EXTENDS;
            var3.pos = var1.pos;
            if (var2.extending == var1.clazz) {
               var3.type_index = -1;
            } else if (var2.implementing.contains(var1.clazz)) {
               var3.type_index = var2.implementing.indexOf(var1.clazz);
            } else {
               Assert.error("Could not determine position of tree " + var1);
            }

            Type var4 = var2.sym.type;
            this.separateAnnotationsKinds(var2, var1.clazz.type, var2.sym, var3);
            this.copyNewClassAnnotationsToOwner(var1);
            var2.sym.type = var4;
         }

         this.scan(var1.encl);
         this.scan(var1.typeargs);
         this.scan(var1.clazz);
         this.scan(var1.args);
      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         this.findPosition(var1, var1, var1.annotations);
         int var2 = var1.dimAnnotations.size();
         ListBuffer var3 = new ListBuffer();

         for(int var4 = 0; var4 < var2; ++var4) {
            TypeAnnotationPosition var5 = new TypeAnnotationPosition();
            var5.pos = var1.pos;
            var5.onLambda = this.currentLambda;
            var5.type = TargetType.NEW;
            if (var4 != 0) {
               var3 = var3.append(TypeAnnotationPosition.TypePathEntry.ARRAY);
               var5.location = var5.location.appendList(var3.toList());
            }

            this.setTypeAnnotationPos((List)var1.dimAnnotations.get(var4), var5);
         }

         JCTree.JCExpression var7 = var1.elemtype;
         var3 = var3.append(TypeAnnotationPosition.TypePathEntry.ARRAY);

         while(var7 != null) {
            if (var7.hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
               JCTree.JCAnnotatedType var8 = (JCTree.JCAnnotatedType)var7;
               TypeAnnotationPosition var6 = new TypeAnnotationPosition();
               var6.type = TargetType.NEW;
               var6.pos = var1.pos;
               var6.onLambda = this.currentLambda;
               this.locateNestedTypes(var7.type, var6);
               var6.location = var6.location.prependList(var3.toList());
               this.setTypeAnnotationPos(var8.annotations, var6);
               var7 = var8.underlyingType;
            } else if (var7.hasTag(JCTree.Tag.TYPEARRAY)) {
               var3 = var3.append(TypeAnnotationPosition.TypePathEntry.ARRAY);
               var7 = ((JCTree.JCArrayTypeTree)var7).elemtype;
            } else {
               if (!var7.hasTag(JCTree.Tag.SELECT)) {
                  break;
               }

               var7 = ((JCTree.JCFieldAccess)var7).selected;
            }
         }

         this.scan(var1.elems);
      }

      private void findPosition(JCTree var1, JCTree var2, List var3) {
         if (!var3.isEmpty()) {
            TypeAnnotationPosition var4 = new TypeAnnotationPosition();
            var4.onLambda = this.currentLambda;
            this.resolveFrame(var1, var2, this.frames.toList(), var4);
            this.setTypeAnnotationPos(var3, var4);
         }

      }

      private void setTypeAnnotationPos(List var1, TypeAnnotationPosition var2) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            JCTree.JCAnnotation var4 = (JCTree.JCAnnotation)var3.next();
            if (var4.attribute != null) {
               ((Attribute.TypeCompound)var4.attribute).position = var2;
            }
         }

      }

      public String toString() {
         return super.toString() + ": sigOnly: " + this.sigOnly;
      }
   }

   public static enum AnnotationType {
      DECLARATION,
      TYPE,
      BOTH;
   }
}
