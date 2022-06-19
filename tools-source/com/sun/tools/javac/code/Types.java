package com.sun.tools.javac.code;

import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.jvm.ClassFile;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Warner;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.tools.JavaFileObject.Kind;

public class Types {
   protected static final Context.Key typesKey = new Context.Key();
   final Symtab syms;
   final JavacMessages messages;
   final Names names;
   final boolean allowBoxing;
   final boolean allowCovariantReturns;
   final boolean allowObjectToPrimitiveCast;
   final boolean allowDefaultMethods;
   final ClassReader reader;
   final Check chk;
   final Enter enter;
   JCDiagnostic.Factory diags;
   List warnStack = List.nil();
   final Name capturedName;
   private final FunctionDescriptorLookupError functionDescriptorLookupError;
   public final Warner noWarnings;
   private final UnaryVisitor isUnbounded = new UnaryVisitor() {
      public Boolean visitType(Type var1, Void var2) {
         return true;
      }

      public Boolean visitClassType(Type.ClassType var1, Void var2) {
         List var3 = var1.tsym.type.allparams();

         for(List var4 = var1.allparams(); var3.nonEmpty(); var4 = var4.tail) {
            Type.WildcardType var5 = new Type.WildcardType(Types.this.syms.objectType, BoundKind.UNBOUND, Types.this.syms.boundClass, (Type.TypeVar)((Type)var3.head).unannotatedType());
            if (!Types.this.containsType((Type)((Type)var4.head), (Type)var5)) {
               return false;
            }

            var3 = var3.tail;
         }

         return true;
      }
   };
   private final SimpleVisitor asSub = new SimpleVisitor() {
      public Type visitType(Type var1, Symbol var2) {
         return null;
      }

      public Type visitClassType(Type.ClassType var1, Symbol var2) {
         if (var1.tsym == var2) {
            return var1;
         } else {
            Type var3 = Types.this.asSuper(var2.type, var1.tsym);
            if (var3 == null) {
               return null;
            } else {
               ListBuffer var4 = new ListBuffer();
               ListBuffer var5 = new ListBuffer();

               try {
                  Types.this.adapt(var3, var1, var4, var5);
               } catch (AdaptFailure var11) {
                  return null;
               }

               Type var6 = Types.this.subst(var2.type, var4.toList(), var5.toList());
               if (!Types.this.isSubtype(var6, var1)) {
                  return null;
               } else {
                  ListBuffer var7 = new ListBuffer();

                  List var8;
                  for(var8 = var2.type.allparams(); var8.nonEmpty(); var8 = var8.tail) {
                     if (var6.contains((Type)var8.head) && !var1.contains((Type)var8.head)) {
                        var7.append(var8.head);
                     }
                  }

                  if (var7.nonEmpty()) {
                     if (var1.isRaw()) {
                        var6 = Types.this.erasure(var6);
                     } else {
                        var8 = var7.toList();
                        ListBuffer var9 = new ListBuffer();

                        for(List var10 = var8; var10.nonEmpty(); var10 = var10.tail) {
                           var9.append(new Type.WildcardType(Types.this.syms.objectType, BoundKind.UNBOUND, Types.this.syms.boundClass, (Type.TypeVar)((Type)var10.head).unannotatedType()));
                        }

                        var6 = Types.this.subst(var6, var8, var9.toList());
                     }
                  }

                  return var6;
               }
            }
         }
      }

      public Type visitErrorType(Type.ErrorType var1, Symbol var2) {
         return var1;
      }
   };
   private DescriptorCache descCache = new DescriptorCache();
   private Filter bridgeFilter = new Filter() {
      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && var1.name != Types.this.names.init && var1.name != Types.this.names.clinit && (var1.flags() & 4096L) == 0L;
      }
   };
   private TypeRelation isSubtype = new TypeRelation() {
      private Set cache = new HashSet();

      public Boolean visitType(Type var1, Type var2) {
         switch (var1.getTag()) {
            case BYTE:
               return !var2.hasTag(TypeTag.CHAR) && var1.getTag().isSubRangeOf(var2.getTag());
            case CHAR:
               return !var2.hasTag(TypeTag.SHORT) && var1.getTag().isSubRangeOf(var2.getTag());
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
               return var1.getTag().isSubRangeOf(var2.getTag());
            case BOOLEAN:
            case VOID:
               return var1.hasTag(var2.getTag());
            case TYPEVAR:
               return Types.this.isSubtypeNoCapture(var1.getUpperBound(), var2);
            case BOT:
               return var2.hasTag(TypeTag.BOT) || var2.hasTag(TypeTag.CLASS) || var2.hasTag(TypeTag.ARRAY) || var2.hasTag(TypeTag.TYPEVAR);
            case WILDCARD:
            case NONE:
               return false;
            default:
               throw new AssertionError("isSubtype " + var1.getTag());
         }
      }

      private boolean containsTypeRecursive(Type var1, Type var2) {
         TypePair var3 = Types.this.new TypePair(var1, var2);
         if (this.cache.add(var3)) {
            boolean var4;
            try {
               var4 = Types.this.containsType(var1.getTypeArguments(), var2.getTypeArguments());
            } finally {
               this.cache.remove(var3);
            }

            return var4;
         } else {
            return Types.this.containsType(var1.getTypeArguments(), this.rewriteSupers(var2).getTypeArguments());
         }
      }

      private Type rewriteSupers(Type var1) {
         if (!var1.isParameterized()) {
            return var1;
         } else {
            ListBuffer var2 = new ListBuffer();
            ListBuffer var3 = new ListBuffer();
            Types.this.adaptSelf(var1, var2, var3);
            if (var2.isEmpty()) {
               return var1;
            } else {
               ListBuffer var4 = new ListBuffer();
               boolean var5 = false;

               Object var8;
               for(Iterator var6 = var3.toList().iterator(); var6.hasNext(); var4.append(var8)) {
                  Type var7 = (Type)var6.next();
                  var8 = this.rewriteSupers(var7);
                  if (((Type)var8).isSuperBound() && !((Type)var8).isExtendsBound()) {
                     var8 = new Type.WildcardType(Types.this.syms.objectType, BoundKind.UNBOUND, Types.this.syms.boundClass);
                     var5 = true;
                  } else if (var8 != var7) {
                     var8 = new Type.WildcardType(Types.this.wildUpperBound((Type)var8), BoundKind.EXTENDS, Types.this.syms.boundClass);
                     var5 = true;
                  }
               }

               if (var5) {
                  return Types.this.subst(var1.tsym.type, var2.toList(), var4.toList());
               } else {
                  return var1;
               }
            }
         }
      }

      public Boolean visitClassType(Type.ClassType var1, Type var2) {
         Type var3 = Types.this.asSuper(var1, var2.tsym);
         if (var3 == null) {
            return false;
         } else {
            return !var3.hasTag(TypeTag.CLASS) ? Types.this.isSubtypeNoCapture(var3, var2) : var3.tsym == var2.tsym && (!var2.isParameterized() || this.containsTypeRecursive(var2, var3)) && Types.this.isSubtypeNoCapture(var3.getEnclosingType(), var2.getEnclosingType());
         }
      }

      public Boolean visitArrayType(Type.ArrayType var1, Type var2) {
         if (var2.hasTag(TypeTag.ARRAY)) {
            return var1.elemtype.isPrimitive() ? Types.this.isSameType(var1.elemtype, Types.this.elemtype(var2)) : Types.this.isSubtypeNoCapture(var1.elemtype, Types.this.elemtype(var2));
         } else if (!var2.hasTag(TypeTag.CLASS)) {
            return false;
         } else {
            Name var3 = var2.tsym.getQualifiedName();
            return var3 == Types.this.names.java_lang_Object || var3 == Types.this.names.java_lang_Cloneable || var3 == Types.this.names.java_io_Serializable;
         }
      }

      public Boolean visitUndetVar(Type.UndetVar var1, Type var2) {
         if (var1 != var2 && var1.qtype != var2 && !var2.hasTag(TypeTag.ERROR) && !var2.hasTag(TypeTag.UNKNOWN)) {
            if (var2.hasTag(TypeTag.BOT)) {
               return false;
            } else {
               var1.addBound(Type.UndetVar.InferenceBound.UPPER, var2, Types.this);
               return true;
            }
         } else {
            return true;
         }
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return true;
      }
   };
   TypeRelation isSameTypeLoose = new LooseSameTypeVisitor();
   TypeRelation isSameTypeStrict = new SameTypeVisitor() {
      boolean sameTypeVars(Type.TypeVar var1, Type.TypeVar var2) {
         return var1 == var2;
      }

      protected boolean containsTypes(List var1, List var2) {
         return Types.this.isSameTypes(var1, var2, true);
      }

      public Boolean visitWildcardType(Type.WildcardType var1, Type var2) {
         if (!var2.hasTag(TypeTag.WILDCARD)) {
            return false;
         } else {
            Type.WildcardType var3 = (Type.WildcardType)var2.unannotatedType();
            return var1.kind == var3.kind && Types.this.isSameType(var1.type, var3.type, true);
         }
      }
   };
   TypeRelation isSameAnnotatedType = new LooseSameTypeVisitor() {
      public Boolean visitAnnotatedType(Type.AnnotatedType var1, Type var2) {
         if (!var2.isAnnotated()) {
            return false;
         } else if (!var1.getAnnotationMirrors().containsAll(var2.getAnnotationMirrors())) {
            return false;
         } else {
            return !var2.getAnnotationMirrors().containsAll(var1.getAnnotationMirrors()) ? false : (Boolean)this.visit(var1.unannotatedType(), var2);
         }
      }
   };
   private TypeRelation containsType = new TypeRelation() {
      public Boolean visitType(Type var1, Type var2) {
         return var2.isPartial() ? Types.this.containedBy(var2, var1) : Types.this.isSameType(var1, var2);
      }

      public Boolean visitWildcardType(Type.WildcardType var1, Type var2) {
         return var2.isPartial() ? Types.this.containedBy(var2, var1) : Types.this.isSameWildcard(var1, var2) || var1.type == var2 || Types.this.isCaptureOf(var2, var1) || (var1.isExtendsBound() || Types.this.isSubtypeNoCapture(Types.this.wildLowerBound(var1), Types.this.cvarLowerBound(Types.this.wildLowerBound(var2)))) && (var1.isSuperBound() || Types.this.isSubtypeNoCapture(Types.this.cvarUpperBound(Types.this.wildUpperBound(var2)), Types.this.wildUpperBound(var1)));
      }

      public Boolean visitUndetVar(Type.UndetVar var1, Type var2) {
         return !var2.hasTag(TypeTag.WILDCARD) ? Types.this.isSameType(var1, var2) : false;
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return true;
      }
   };
   private TypeRelation isCastable = new TypeRelation() {
      public Boolean visitType(Type var1, Type var2) {
         if (var2.hasTag(TypeTag.ERROR)) {
            return true;
         } else {
            switch (var1.getTag()) {
               case BYTE:
               case CHAR:
               case SHORT:
               case INT:
               case LONG:
               case FLOAT:
               case DOUBLE:
                  return var2.isNumeric();
               case BOOLEAN:
                  return var2.hasTag(TypeTag.BOOLEAN);
               case VOID:
                  return false;
               case TYPEVAR:
               default:
                  throw new AssertionError();
               case BOT:
                  return Types.this.isSubtype(var1, var2);
            }
         }
      }

      public Boolean visitWildcardType(Type.WildcardType var1, Type var2) {
         return Types.this.isCastable(Types.this.wildUpperBound(var1), var2, (Warner)Types.this.warnStack.head);
      }

      public Boolean visitClassType(Type.ClassType var1, Type var2) {
         if (!var2.hasTag(TypeTag.ERROR) && !var2.hasTag(TypeTag.BOT)) {
            if (var2.hasTag(TypeTag.TYPEVAR)) {
               if (Types.this.isCastable(var1, var2.getUpperBound(), Types.this.noWarnings)) {
                  ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                  return true;
               } else {
                  return false;
               }
            } else if (!var1.isIntersection() && !var2.isIntersection()) {
               if (var2.hasTag(TypeTag.CLASS) || var2.hasTag(TypeTag.ARRAY)) {
                  boolean var3;
                  if ((var3 = Types.this.isSubtype(Types.this.erasure((Type)var1), Types.this.erasure(var2))) || Types.this.isSubtype(Types.this.erasure(var2), Types.this.erasure((Type)var1))) {
                     if (!var3 && var2.hasTag(TypeTag.ARRAY)) {
                        if (!Types.this.isReifiable(var2)) {
                           ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                        }

                        return true;
                     } else if (var2.isRaw()) {
                        return true;
                     } else if (var1.isRaw()) {
                        if (!Types.this.isUnbounded(var2)) {
                           ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                        }

                        return true;
                     } else {
                        Object var4 = var3 ? var1 : var2;
                        Object var5 = var3 ? var2 : var1;
                        Type var9 = Types.this.rewriteQuantifiers((Type)var4, true, false);
                        Type var10 = Types.this.rewriteQuantifiers((Type)var4, false, false);
                        Type var11 = Types.this.rewriteQuantifiers((Type)var5, true, false);
                        Type var12 = Types.this.rewriteQuantifiers((Type)var5, false, false);
                        Type var13 = Types.this.asSub(var12, var10.tsym);
                        Type var14 = var13 == null ? null : Types.this.asSub(var11, var9.tsym);
                        if (var14 == null) {
                           var9 = Types.this.rewriteQuantifiers((Type)var4, true, true);
                           var10 = Types.this.rewriteQuantifiers((Type)var4, false, true);
                           var11 = Types.this.rewriteQuantifiers((Type)var5, true, true);
                           var12 = Types.this.rewriteQuantifiers((Type)var5, false, true);
                           var13 = Types.this.asSub(var12, var10.tsym);
                           var14 = var13 == null ? null : Types.this.asSub(var11, var9.tsym);
                        }

                        if (var14 != null) {
                           if (((Type)var4).tsym != var14.tsym || ((Type)var4).tsym != var13.tsym) {
                              Assert.error(((Type)var4).tsym + " != " + var14.tsym + " != " + var13.tsym);
                           }

                           if (!Types.this.disjointTypes(var9.allparams(), var14.allparams()) && !Types.this.disjointTypes(var9.allparams(), var13.allparams()) && !Types.this.disjointTypes(var10.allparams(), var14.allparams()) && !Types.this.disjointTypes(var10.allparams(), var13.allparams())) {
                              if (var3) {
                                 if (!Types.this.giveWarning((Type)var4, (Type)var5)) {
                                    return true;
                                 }
                              } else if (!Types.this.giveWarning((Type)var5, (Type)var4)) {
                                 return true;
                              }

                              ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                              return true;
                           }
                        }

                        return Types.this.isReifiable(var2) ? Types.this.isSubtypeUnchecked((Type)var4, (Type)var5) : Types.this.isSubtypeUnchecked((Type)var4, (Type)var5, (Warner)Types.this.warnStack.head);
                     }
                  }

                  if (var2.hasTag(TypeTag.CLASS)) {
                     if ((var2.tsym.flags() & 512L) != 0L) {
                        return (var1.tsym.flags() & 16L) == 0L ? Types.this.sideCast(var1, var2, (Warner)Types.this.warnStack.head) : Types.this.sideCastFinal(var1, var2, (Warner)Types.this.warnStack.head);
                     }

                     if ((var1.tsym.flags() & 512L) != 0L) {
                        return (var2.tsym.flags() & 16L) == 0L ? Types.this.sideCast(var1, var2, (Warner)Types.this.warnStack.head) : Types.this.sideCastFinal(var1, var2, (Warner)Types.this.warnStack.head);
                     }

                     return false;
                  }
               }

               return false;
            } else {
               return !var1.isIntersection() ? this.visitIntersectionType((Type.IntersectionClassType)var2.unannotatedType(), var1, true) : this.visitIntersectionType((Type.IntersectionClassType)var1.unannotatedType(), var2, false);
            }
         } else {
            return true;
         }
      }

      boolean visitIntersectionType(Type.IntersectionClassType var1, Type var2, boolean var3) {
         Warner var4 = Types.this.noWarnings;
         Iterator var5 = var1.getComponents().iterator();

         while(true) {
            if (!var5.hasNext()) {
               if (var4.hasLint(Lint.LintCategory.UNCHECKED)) {
                  ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
               }

               return true;
            }

            Type var6 = (Type)var5.next();
            var4.clear();
            if (var3) {
               if (!Types.this.isCastable(var2, var6, var4)) {
                  break;
               }
            } else if (!Types.this.isCastable(var6, var2, var4)) {
               break;
            }
         }

         return false;
      }

      public Boolean visitArrayType(Type.ArrayType var1, Type var2) {
         switch (var2.getTag()) {
            case ARRAY:
               if (!Types.this.elemtype(var1).isPrimitive() && !Types.this.elemtype(var2).isPrimitive()) {
                  return (Boolean)this.visit(Types.this.elemtype(var1), Types.this.elemtype(var2));
               }

               return Types.this.elemtype(var1).hasTag(Types.this.elemtype(var2).getTag());
            case CLASS:
               return Types.this.isSubtype(var1, var2);
            case TYPEVAR:
               if (Types.this.isCastable(var2, var1, Types.this.noWarnings)) {
                  ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                  return true;
               }

               return false;
            case BOT:
            case ERROR:
               return true;
            default:
               return false;
         }
      }

      public Boolean visitTypeVar(Type.TypeVar var1, Type var2) {
         switch (var2.getTag()) {
            case TYPEVAR:
               if (Types.this.isSubtype(var1, var2)) {
                  return true;
               } else {
                  if (Types.this.isCastable(var1.bound, var2, Types.this.noWarnings)) {
                     ((Warner)Types.this.warnStack.head).warn(Lint.LintCategory.UNCHECKED);
                     return true;
                  }

                  return false;
               }
            case BOT:
            case ERROR:
               return true;
            case WILDCARD:
            case NONE:
            default:
               return Types.this.isCastable(var1.bound, var2, (Warner)Types.this.warnStack.head);
         }
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return true;
      }
   };
   private TypeRelation disjointType = new TypeRelation() {
      private Set cache = new HashSet();

      public Boolean visitType(Type var1, Type var2) {
         return var2.hasTag(TypeTag.WILDCARD) ? (Boolean)this.visit(var2, var1) : this.notSoftSubtypeRecursive(var1, var2) || this.notSoftSubtypeRecursive(var2, var1);
      }

      private boolean isCastableRecursive(Type var1, Type var2) {
         TypePair var3 = Types.this.new TypePair(var1, var2);
         if (this.cache.add(var3)) {
            boolean var4;
            try {
               var4 = Types.this.isCastable(var1, var2);
            } finally {
               this.cache.remove(var3);
            }

            return var4;
         } else {
            return true;
         }
      }

      private boolean notSoftSubtypeRecursive(Type var1, Type var2) {
         TypePair var3 = Types.this.new TypePair(var1, var2);
         if (this.cache.add(var3)) {
            boolean var4;
            try {
               var4 = Types.this.notSoftSubtype(var1, var2);
            } finally {
               this.cache.remove(var3);
            }

            return var4;
         } else {
            return false;
         }
      }

      public Boolean visitWildcardType(Type.WildcardType var1, Type var2) {
         if (var1.isUnbound()) {
            return false;
         } else if (!var2.hasTag(TypeTag.WILDCARD)) {
            return var1.isExtendsBound() ? this.notSoftSubtypeRecursive(var2, var1.type) : this.notSoftSubtypeRecursive(var1.type, var2);
         } else if (var2.isUnbound()) {
            return false;
         } else {
            if (var1.isExtendsBound()) {
               if (var2.isExtendsBound()) {
                  return !this.isCastableRecursive(var1.type, Types.this.wildUpperBound(var2));
               }

               if (var2.isSuperBound()) {
                  return this.notSoftSubtypeRecursive(Types.this.wildLowerBound(var2), var1.type);
               }
            } else if (var1.isSuperBound() && var2.isExtendsBound()) {
               return this.notSoftSubtypeRecursive(var1.type, Types.this.wildUpperBound(var2));
            }

            return false;
         }
      }
   };
   private final Type.Mapping cvarLowerBoundMapping = new Type.Mapping("cvarLowerBound") {
      public Type apply(Type var1) {
         return Types.this.cvarLowerBound(var1);
      }
   };
   private UnaryVisitor isReifiable = new UnaryVisitor() {
      public Boolean visitType(Type var1, Void var2) {
         return true;
      }

      public Boolean visitClassType(Type.ClassType var1, Void var2) {
         if (var1.isCompound()) {
            return false;
         } else if (!var1.isParameterized()) {
            return true;
         } else {
            Iterator var3 = var1.allparams().iterator();

            Type var4;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               var4 = (Type)var3.next();
            } while(var4.isUnbound());

            return false;
         }
      }

      public Boolean visitArrayType(Type.ArrayType var1, Void var2) {
         return (Boolean)this.visit(var1.elemtype);
      }

      public Boolean visitTypeVar(Type.TypeVar var1, Void var2) {
         return false;
      }
   };
   private Type.Mapping elemTypeFun = new Type.Mapping("elemTypeFun") {
      public Type apply(Type var1) {
         while(var1.hasTag(TypeTag.TYPEVAR)) {
            var1 = var1.getUpperBound();
         }

         return Types.this.elemtype(var1);
      }
   };
   private SimpleVisitor asSuper = new SimpleVisitor() {
      public Type visitType(Type var1, Symbol var2) {
         return null;
      }

      public Type visitClassType(Type.ClassType var1, Symbol var2) {
         if (var1.tsym == var2) {
            return var1;
         } else {
            Type var3 = Types.this.supertype(var1);
            if (var3.hasTag(TypeTag.CLASS) || var3.hasTag(TypeTag.TYPEVAR)) {
               Type var4 = Types.this.asSuper(var3, var2);
               if (var4 != null) {
                  return var4;
               }
            }

            if ((var2.flags() & 512L) != 0L) {
               for(List var6 = Types.this.interfaces(var1); var6.nonEmpty(); var6 = var6.tail) {
                  if (!((Type)var6.head).hasTag(TypeTag.ERROR)) {
                     Type var5 = Types.this.asSuper((Type)var6.head, var2);
                     if (var5 != null) {
                        return var5;
                     }
                  }
               }
            }

            return null;
         }
      }

      public Type visitArrayType(Type.ArrayType var1, Symbol var2) {
         return Types.this.isSubtype(var1, var2.type) ? var2.type : null;
      }

      public Type visitTypeVar(Type.TypeVar var1, Symbol var2) {
         return (Type)(var1.tsym == var2 ? var1 : Types.this.asSuper(var1.bound, var2));
      }

      public Type visitErrorType(Type.ErrorType var1, Symbol var2) {
         return var1;
      }
   };
   private SimpleVisitor memberType = new SimpleVisitor() {
      public Type visitType(Type var1, Symbol var2) {
         return var2.type;
      }

      public Type visitWildcardType(Type.WildcardType var1, Symbol var2) {
         return Types.this.memberType(Types.this.wildUpperBound(var1), var2);
      }

      public Type visitClassType(Type.ClassType var1, Symbol var2) {
         Symbol var3 = var2.owner;
         long var4 = var2.flags();
         if ((var4 & 8L) == 0L && var3.type.isParameterized()) {
            Type var6 = Types.this.asOuterSuper(var1, var3);
            var6 = var1.isCompound() ? Types.this.capture(var6) : var6;
            if (var6 != null) {
               List var7 = var3.type.allparams();
               List var8 = var6.allparams();
               if (var7.nonEmpty()) {
                  if (var8.isEmpty()) {
                     return Types.this.erasure(var2.type);
                  }

                  return Types.this.subst(var2.type, var7, var8);
               }
            }
         }

         return var2.type;
      }

      public Type visitTypeVar(Type.TypeVar var1, Symbol var2) {
         return Types.this.memberType(var1.bound, var2);
      }

      public Type visitErrorType(Type.ErrorType var1, Symbol var2) {
         return var1;
      }
   };
   private SimpleVisitor erasure = new SimpleVisitor() {
      public Type visitType(Type var1, Boolean var2) {
         return var1.isPrimitive() ? var1 : var1.map(var2 ? Types.this.erasureRecFun : Types.this.erasureFun);
      }

      public Type visitWildcardType(Type.WildcardType var1, Boolean var2) {
         return Types.this.erasure(Types.this.wildUpperBound(var1), var2);
      }

      public Type visitClassType(Type.ClassType var1, Boolean var2) {
         Object var3 = var1.tsym.erasure(Types.this);
         if (var2) {
            var3 = new Type.ErasedClassType(((Type)var3).getEnclosingType(), ((Type)var3).tsym);
         }

         return (Type)var3;
      }

      public Type visitTypeVar(Type.TypeVar var1, Boolean var2) {
         return Types.this.erasure(var1.bound, var2);
      }

      public Type visitErrorType(Type.ErrorType var1, Boolean var2) {
         return var1;
      }

      public Type visitAnnotatedType(Type.AnnotatedType var1, Boolean var2) {
         Type var3 = Types.this.erasure(var1.unannotatedType(), var2);
         if (var3.isAnnotated()) {
            var3 = ((Type.AnnotatedType)var3).unannotatedType();
         }

         return var3.annotatedType(var1.getAnnotationMirrors());
      }
   };
   private Type.Mapping erasureFun = new Type.Mapping("erasure") {
      public Type apply(Type var1) {
         return Types.this.erasure(var1);
      }
   };
   private Type.Mapping erasureRecFun = new Type.Mapping("erasureRecursive") {
      public Type apply(Type var1) {
         return Types.this.erasureRecursive(var1);
      }
   };
   private UnaryVisitor supertype = new UnaryVisitor() {
      public Type visitType(Type var1, Void var2) {
         return Type.noType;
      }

      public Type visitClassType(Type.ClassType var1, Void var2) {
         if (var1.supertype_field == null) {
            Type var3 = ((Symbol.ClassSymbol)var1.tsym).getSuperclass();
            if (var1.isInterface()) {
               var3 = ((Type.ClassType)var1.tsym.type).supertype_field;
            }

            if (var1.supertype_field == null) {
               List var4 = Types.this.classBound(var1).allparams();
               List var5 = var1.tsym.type.allparams();
               if (var1.hasErasedSupertypes()) {
                  var1.supertype_field = Types.this.erasureRecursive(var3);
               } else if (var5.nonEmpty()) {
                  var1.supertype_field = Types.this.subst(var3, var5, var4);
               } else {
                  var1.supertype_field = var3;
               }
            }
         }

         return var1.supertype_field;
      }

      public Type visitTypeVar(Type.TypeVar var1, Void var2) {
         return !var1.bound.hasTag(TypeTag.TYPEVAR) && (var1.bound.isCompound() || var1.bound.isInterface()) ? Types.this.supertype(var1.bound) : var1.bound;
      }

      public Type visitArrayType(Type.ArrayType var1, Void var2) {
         return (Type)(!var1.elemtype.isPrimitive() && !Types.this.isSameType(var1.elemtype, Types.this.syms.objectType) ? new Type.ArrayType(Types.this.supertype(var1.elemtype), var1.tsym) : Types.this.arraySuperType());
      }

      public Type visitErrorType(Type.ErrorType var1, Void var2) {
         return Type.noType;
      }
   };
   private UnaryVisitor interfaces = new UnaryVisitor() {
      public List visitType(Type var1, Void var2) {
         return List.nil();
      }

      public List visitClassType(Type.ClassType var1, Void var2) {
         if (var1.interfaces_field == null) {
            List var3 = ((Symbol.ClassSymbol)var1.tsym).getInterfaces();
            if (var1.interfaces_field == null) {
               Assert.check(var1 != var1.tsym.type, (Object)var1);
               List var4 = var1.allparams();
               List var5 = var1.tsym.type.allparams();
               if (var1.hasErasedSupertypes()) {
                  var1.interfaces_field = Types.this.erasureRecursive(var3);
               } else if (var5.nonEmpty()) {
                  var1.interfaces_field = Types.this.subst(var3, var5, var4);
               } else {
                  var1.interfaces_field = var3;
               }
            }
         }

         return var1.interfaces_field;
      }

      public List visitTypeVar(Type.TypeVar var1, Void var2) {
         if (var1.bound.isCompound()) {
            return Types.this.interfaces(var1.bound);
         } else {
            return var1.bound.isInterface() ? List.of(var1.bound) : List.nil();
         }
      }
   };
   private final UnaryVisitor directSupertypes = new UnaryVisitor() {
      public List visitType(Type var1, Void var2) {
         if (var1.isIntersection()) {
            return this.visitIntersectionType((Type.IntersectionClassType)var1);
         } else {
            Type var3 = Types.this.supertype(var1);
            return var3 != Type.noType && var3 != var1 && var3 != null ? Types.this.interfaces(var1).prepend(var3) : Types.this.interfaces(var1);
         }
      }

      private List visitIntersectionType(Type.IntersectionClassType var1) {
         return var1.getExplicitComponents();
      }
   };
   Map isDerivedRawCache = new HashMap();
   private UnaryVisitor classBound = new UnaryVisitor() {
      public Type visitType(Type var1, Void var2) {
         return var1;
      }

      public Type visitClassType(Type.ClassType var1, Void var2) {
         Type var3 = Types.this.classBound(var1.getEnclosingType());
         return var3 != var1.getEnclosingType() ? new Type.ClassType(var3, var1.getTypeArguments(), var1.tsym) : var1;
      }

      public Type visitTypeVar(Type.TypeVar var1, Void var2) {
         return Types.this.classBound(Types.this.supertype(var1));
      }

      public Type visitErrorType(Type.ErrorType var1, Void var2) {
         return var1;
      }
   };
   private ImplementationCache implCache = new ImplementationCache();
   private MembersClosureCache membersCache = new MembersClosureCache();
   TypeRelation hasSameArgs_strict = new HasSameArgs(true);
   TypeRelation hasSameArgs_nonstrict = new HasSameArgs(false);
   private static final Type.Mapping newInstanceFun = new Type.Mapping("newInstanceFun") {
      public Type apply(Type var1) {
         return new Type.TypeVar(var1.tsym, var1.getUpperBound(), var1.getLowerBound());
      }
   };
   private final MapVisitor methodWithParameters = new MapVisitor() {
      public Type visitType(Type var1, List var2) {
         throw new IllegalArgumentException("Not a method type: " + var1);
      }

      public Type visitMethodType(Type.MethodType var1, List var2) {
         return new Type.MethodType(var2, var1.restype, var1.thrown, var1.tsym);
      }

      public Type visitForAll(Type.ForAll var1, List var2) {
         return new Type.ForAll(var1.tvars, (Type)var1.qtype.accept((Type.Visitor)this, var2));
      }
   };
   private final MapVisitor methodWithThrown = new MapVisitor() {
      public Type visitType(Type var1, List var2) {
         throw new IllegalArgumentException("Not a method type: " + var1);
      }

      public Type visitMethodType(Type.MethodType var1, List var2) {
         return new Type.MethodType(var1.argtypes, var1.restype, var2, var1.tsym);
      }

      public Type visitForAll(Type.ForAll var1, List var2) {
         return new Type.ForAll(var1.tvars, (Type)var1.qtype.accept((Type.Visitor)this, var2));
      }
   };
   private final MapVisitor methodWithReturn = new MapVisitor() {
      public Type visitType(Type var1, Type var2) {
         throw new IllegalArgumentException("Not a method type: " + var1);
      }

      public Type visitMethodType(Type.MethodType var1, Type var2) {
         return new Type.MethodType(var1.argtypes, var2, var1.thrown, var1.tsym);
      }

      public Type visitForAll(Type.ForAll var1, Type var2) {
         return new Type.ForAll(var1.tvars, (Type)var1.qtype.accept((Type.Visitor)this, var2));
      }
   };
   private Map closureCache = new HashMap();
   Set mergeCache = new HashSet();
   private Type arraySuperType = null;
   private static final UnaryVisitor hashCode = new UnaryVisitor() {
      public Integer visitType(Type var1, Void var2) {
         return var1.getTag().ordinal();
      }

      public Integer visitClassType(Type.ClassType var1, Void var2) {
         int var3 = (Integer)this.visit(var1.getEnclosingType());
         var3 *= 127;
         var3 += var1.tsym.flatName().hashCode();

         Type var5;
         for(Iterator var4 = var1.getTypeArguments().iterator(); var4.hasNext(); var3 += (Integer)this.visit(var5)) {
            var5 = (Type)var4.next();
            var3 *= 127;
         }

         return var3;
      }

      public Integer visitMethodType(Type.MethodType var1, Void var2) {
         int var3 = TypeTag.METHOD.ordinal();

         for(List var4 = var1.argtypes; var4.tail != null; var4 = var4.tail) {
            var3 = (var3 << 5) + (Integer)this.visit((Type)var4.head);
         }

         return (var3 << 5) + (Integer)this.visit(var1.restype);
      }

      public Integer visitWildcardType(Type.WildcardType var1, Void var2) {
         int var3 = var1.kind.hashCode();
         if (var1.type != null) {
            var3 *= 127;
            var3 += (Integer)this.visit(var1.type);
         }

         return var3;
      }

      public Integer visitArrayType(Type.ArrayType var1, Void var2) {
         return (Integer)this.visit(var1.elemtype) + 12;
      }

      public Integer visitTypeVar(Type.TypeVar var1, Void var2) {
         return System.identityHashCode(var1.tsym);
      }

      public Integer visitUndetVar(Type.UndetVar var1, Void var2) {
         return System.identityHashCode(var1);
      }

      public Integer visitErrorType(Type.ErrorType var1, Void var2) {
         return 0;
      }
   };

   public static Types instance(Context var0) {
      Types var1 = (Types)var0.get(typesKey);
      if (var1 == null) {
         var1 = new Types(var0);
      }

      return var1;
   }

   protected Types(Context var1) {
      var1.put((Context.Key)typesKey, (Object)this);
      this.syms = Symtab.instance(var1);
      this.names = Names.instance(var1);
      Source var2 = Source.instance(var1);
      this.allowBoxing = var2.allowBoxing();
      this.allowCovariantReturns = var2.allowCovariantReturns();
      this.allowObjectToPrimitiveCast = var2.allowObjectToPrimitiveCast();
      this.allowDefaultMethods = var2.allowDefaultMethods();
      this.reader = ClassReader.instance(var1);
      this.chk = Check.instance(var1);
      this.enter = Enter.instance(var1);
      this.capturedName = this.names.fromString("<captured wildcard>");
      this.messages = JavacMessages.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.functionDescriptorLookupError = new FunctionDescriptorLookupError();
      this.noWarnings = new Warner((JCDiagnostic.DiagnosticPosition)null);
   }

   public Type wildUpperBound(Type var1) {
      if (var1.hasTag(TypeTag.WILDCARD)) {
         Type.WildcardType var2 = (Type.WildcardType)var1.unannotatedType();
         if (var2.isSuperBound()) {
            return var2.bound == null ? this.syms.objectType : var2.bound.bound;
         } else {
            return this.wildUpperBound(var2.type);
         }
      } else {
         return var1.unannotatedType();
      }
   }

   public Type cvarUpperBound(Type var1) {
      if (var1.hasTag(TypeTag.TYPEVAR)) {
         Type.TypeVar var2 = (Type.TypeVar)var1.unannotatedType();
         return (Type)(var2.isCaptured() ? this.cvarUpperBound(var2.bound) : var2);
      } else {
         return var1.unannotatedType();
      }
   }

   public Type wildLowerBound(Type var1) {
      if (var1.hasTag(TypeTag.WILDCARD)) {
         Type.WildcardType var2 = (Type.WildcardType)var1.unannotatedType();
         return var2.isExtendsBound() ? this.syms.botType : this.wildLowerBound(var2.type);
      } else {
         return var1.unannotatedType();
      }
   }

   public Type cvarLowerBound(Type var1) {
      if (var1.hasTag(TypeTag.TYPEVAR)) {
         Type.TypeVar var2 = (Type.TypeVar)var1.unannotatedType();
         return (Type)(var2.isCaptured() ? this.cvarLowerBound(var2.getLowerBound()) : var2);
      } else {
         return var1.unannotatedType();
      }
   }

   public boolean isUnbounded(Type var1) {
      return (Boolean)this.isUnbounded.visit(var1);
   }

   public Type asSub(Type var1, Symbol var2) {
      return (Type)this.asSub.visit(var1, var2);
   }

   public boolean isConvertible(Type var1, Type var2, Warner var3) {
      if (var1.hasTag(TypeTag.ERROR)) {
         return true;
      } else {
         boolean var4 = var1.isPrimitive();
         boolean var5 = var2.isPrimitive();
         if (var4 == var5) {
            return this.isSubtypeUnchecked(var1, var2, var3);
         } else if (!this.allowBoxing) {
            return false;
         } else {
            return var4 ? this.isSubtype(this.boxedClass(var1).type, var2) : this.isSubtype(this.unboxedType(var1), var2);
         }
      }
   }

   public boolean isConvertible(Type var1, Type var2) {
      return this.isConvertible(var1, var2, this.noWarnings);
   }

   public Symbol findDescriptorSymbol(Symbol.TypeSymbol var1) throws FunctionDescriptorLookupError {
      return this.descCache.get(var1).getSymbol();
   }

   public Type findDescriptorType(Type var1) throws FunctionDescriptorLookupError {
      return this.descCache.get(var1.tsym).getType(var1);
   }

   public boolean isFunctionalInterface(Symbol.TypeSymbol var1) {
      try {
         this.findDescriptorSymbol(var1);
         return true;
      } catch (FunctionDescriptorLookupError var3) {
         return false;
      }
   }

   public boolean isFunctionalInterface(Type var1) {
      try {
         this.findDescriptorType(var1);
         return true;
      } catch (FunctionDescriptorLookupError var3) {
         return false;
      }
   }

   public Type removeWildcards(Type var1) {
      Type var2 = this.capture(var1);
      if (var2 != var1) {
         Type var3 = var1.tsym.type;
         ListBuffer var4 = new ListBuffer();
         List var5 = var1.getTypeArguments();
         List var6 = var2.getTypeArguments();

         for(Iterator var7 = var3.getTypeArguments().iterator(); var7.hasNext(); var6 = var6.tail) {
            Type var8 = (Type)var7.next();
            if (((Type)var5.head).hasTag(TypeTag.WILDCARD)) {
               Type.WildcardType var9 = (Type.WildcardType)((Type)var5.head).unannotatedType();
               Type var10;
               switch (var9.kind) {
                  case EXTENDS:
                  case UNBOUND:
                     Type.CapturedType var11 = (Type.CapturedType)((Type)var6.head).unannotatedType();
                     var10 = var11.bound.containsAny(var2.getTypeArguments()) ? var9.type : var11.bound;
                     break;
                  default:
                     var10 = var9.type;
               }

               var4.append(var10);
            } else {
               var4.append(var5.head);
            }

            var5 = var5.tail;
         }

         return this.subst(var3, var3.getTypeArguments(), var4.toList());
      } else {
         return var1;
      }
   }

   public Symbol.ClassSymbol makeFunctionalInterfaceClass(Env var1, Name var2, List var3, long var4) {
      if (var3.isEmpty()) {
         return null;
      } else {
         Symbol var6 = this.findDescriptorSymbol(((Type)var3.head).tsym);
         Type var7 = this.findDescriptorType((Type)var3.head);
         Symbol.ClassSymbol var8 = new Symbol.ClassSymbol(var4, var2, var1.enclClass.sym.outermostClass());
         var8.completer = null;
         var8.members_field = new Scope(var8);
         Symbol.MethodSymbol var9 = new Symbol.MethodSymbol(var6.flags(), var6.name, var7, var8);
         var8.members_field.enter(var9);
         Type.ClassType var10 = new Type.ClassType(Type.noType, List.nil(), var8);
         var10.supertype_field = this.syms.objectType;
         var10.interfaces_field = var3;
         var8.type = var10;
         var8.sourcefile = ((Symbol.ClassSymbol)var8.owner).sourcefile;
         return var8;
      }
   }

   public List functionalInterfaceBridges(Symbol.TypeSymbol var1) {
      Assert.check(this.isFunctionalInterface(var1));
      Symbol var2 = this.findDescriptorSymbol(var1);
      Scope.CompoundScope var3 = this.membersClosure(var1.type, false);
      ListBuffer var4 = new ListBuffer();
      Iterator var5 = var3.getElementsByName(var2.name, this.bridgeFilter).iterator();

      while(true) {
         label41:
         while(true) {
            Symbol var6;
            do {
               do {
                  if (!var5.hasNext()) {
                     return var4.toList();
                  }

                  var6 = (Symbol)var5.next();
               } while(var6 == var2);
            } while(!var2.overrides(var6, var1, this, false));

            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               Symbol var8 = (Symbol)var7.next();
               if (this.isSameType(var8.erasure(this), var6.erasure(this)) || var8.overrides(var6, var1, this, false) && (this.pendingBridges((Symbol.ClassSymbol)var1, var8.enclClass()) || ((Symbol.MethodSymbol)var6).binaryImplementation((Symbol.ClassSymbol)var8.owner, this) != null)) {
                  continue label41;
               }
            }

            var4.add(var6);
         }
      }
   }

   private boolean pendingBridges(Symbol.ClassSymbol var1, Symbol.TypeSymbol var2) {
      if (var1.classfile != null && var1.classfile.getKind() == Kind.CLASS && this.enter.getEnv(var1) == null) {
         return false;
      } else if (var1 == var2) {
         return true;
      } else {
         Iterator var3 = this.interfaces(var1.type).iterator();

         Type var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (Type)var3.next();
         } while(!this.pendingBridges((Symbol.ClassSymbol)var4.tsym, var2));

         return true;
      }
   }

   public boolean isSubtypeUnchecked(Type var1, Type var2) {
      return this.isSubtypeUnchecked(var1, var2, this.noWarnings);
   }

   public boolean isSubtypeUnchecked(Type var1, Type var2, Warner var3) {
      boolean var4 = this.isSubtypeUncheckedInternal(var1, var2, var3);
      if (var4) {
         this.checkUnsafeVarargsConversion(var1, var2, var3);
      }

      return var4;
   }

   private boolean isSubtypeUncheckedInternal(Type var1, Type var2, Warner var3) {
      if (var1.hasTag(TypeTag.ARRAY) && var2.hasTag(TypeTag.ARRAY)) {
         var1 = var1.unannotatedType();
         var2 = var2.unannotatedType();
         return ((Type.ArrayType)var1).elemtype.isPrimitive() ? this.isSameType(this.elemtype(var1), this.elemtype(var2)) : this.isSubtypeUnchecked(this.elemtype(var1), this.elemtype(var2), var3);
      } else if (this.isSubtype(var1, var2)) {
         return true;
      } else if (var1.hasTag(TypeTag.TYPEVAR)) {
         return this.isSubtypeUnchecked(var1.getUpperBound(), var2, var3);
      } else {
         if (!var2.isRaw()) {
            Type var4 = this.asSuper(var1, var2.tsym);
            if (var4 != null && var4.isRaw()) {
               if (this.isReifiable(var2)) {
                  var3.silentWarn(Lint.LintCategory.UNCHECKED);
               } else {
                  var3.warn(Lint.LintCategory.UNCHECKED);
               }

               return true;
            }
         }

         return false;
      }
   }

   private void checkUnsafeVarargsConversion(Type var1, Type var2, Warner var3) {
      if (var1.hasTag(TypeTag.ARRAY) && !this.isReifiable(var1)) {
         var1 = var1.unannotatedType();
         var2 = var2.unannotatedType();
         Type.ArrayType var4 = (Type.ArrayType)var1;
         boolean var5 = false;
         switch (var2.getTag()) {
            case ARRAY:
               Type.ArrayType var6 = (Type.ArrayType)var2;
               var5 = var4.isVarargs() && !var6.isVarargs() && !this.isReifiable(var4);
               break;
            case CLASS:
               var5 = var4.isVarargs();
         }

         if (var5) {
            var3.warn(Lint.LintCategory.VARARGS);
         }

      }
   }

   public final boolean isSubtype(Type var1, Type var2) {
      return this.isSubtype(var1, var2, true);
   }

   public final boolean isSubtypeNoCapture(Type var1, Type var2) {
      return this.isSubtype(var1, var2, false);
   }

   public boolean isSubtype(Type var1, Type var2, boolean var3) {
      if (var1 == var2) {
         return true;
      } else {
         var1 = var1.unannotatedType();
         var2 = var2.unannotatedType();
         if (var1 == var2) {
            return true;
         } else if (var2.isPartial()) {
            return this.isSuperType(var2, var1);
         } else if (var2.isCompound()) {
            Iterator var6 = this.interfaces(var2).prepend(this.supertype(var2)).iterator();

            Type var5;
            do {
               if (!var6.hasNext()) {
                  return true;
               }

               var5 = (Type)var6.next();
            } while(this.isSubtype(var1, var5, var3));

            return false;
         } else {
            if (!var1.hasTag(TypeTag.UNDETVAR) && !var1.isCompound()) {
               Type var4 = this.cvarLowerBound(this.wildLowerBound(var2));
               if (var2 != var4) {
                  return this.isSubtype(var3 ? this.capture(var1) : var1, var4, false);
               }
            }

            return (Boolean)this.isSubtype.visit(var3 ? this.capture(var1) : var1, var2);
         }
      }
   }

   public boolean isSubtypeUnchecked(Type var1, List var2, Warner var3) {
      for(List var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         if (!this.isSubtypeUnchecked(var1, (Type)var4.head, var3)) {
            return false;
         }
      }

      return true;
   }

   public boolean isSubtypes(List var1, List var2) {
      while(var1.tail != null && var2.tail != null && this.isSubtype((Type)var1.head, (Type)var2.head)) {
         var1 = var1.tail;
         var2 = var2.tail;
      }

      return var1.tail == null && var2.tail == null;
   }

   public boolean isSubtypesUnchecked(List var1, List var2, Warner var3) {
      while(var1.tail != null && var2.tail != null && this.isSubtypeUnchecked((Type)var1.head, (Type)var2.head, var3)) {
         var1 = var1.tail;
         var2 = var2.tail;
      }

      return var1.tail == null && var2.tail == null;
   }

   public boolean isSuperType(Type var1, Type var2) {
      switch (var1.getTag()) {
         case ERROR:
            return true;
         case UNDETVAR:
            Type.UndetVar var3 = (Type.UndetVar)var1;
            if (var1 != var2 && var3.qtype != var2 && !var2.hasTag(TypeTag.ERROR) && !var2.hasTag(TypeTag.BOT)) {
               var3.addBound(Type.UndetVar.InferenceBound.LOWER, var2, this);
               return true;
            }

            return true;
         default:
            return this.isSubtype(var2, var1);
      }
   }

   public boolean isSameTypes(List var1, List var2) {
      return this.isSameTypes(var1, var2, false);
   }

   public boolean isSameTypes(List var1, List var2, boolean var3) {
      while(var1.tail != null && var2.tail != null && this.isSameType((Type)var1.head, (Type)var2.head, var3)) {
         var1 = var1.tail;
         var2 = var2.tail;
      }

      return var1.tail == null && var2.tail == null;
   }

   public boolean isSignaturePolymorphic(Symbol.MethodSymbol var1) {
      List var2 = var1.type.getParameterTypes();
      return (var1.flags_field & 256L) != 0L && var1.owner == this.syms.methodHandleType.tsym && var2.length() == 1 && ((Type)var2.head).hasTag(TypeTag.ARRAY) && var1.type.getReturnType().tsym == this.syms.objectType.tsym && ((Type.ArrayType)var2.head).elemtype.tsym == this.syms.objectType.tsym;
   }

   public boolean isSameType(Type var1, Type var2) {
      return this.isSameType(var1, var2, false);
   }

   public boolean isSameType(Type var1, Type var2, boolean var3) {
      return var3 ? (Boolean)this.isSameTypeStrict.visit(var1, var2) : (Boolean)this.isSameTypeLoose.visit(var1, var2);
   }

   public boolean isSameAnnotatedType(Type var1, Type var2) {
      return (Boolean)this.isSameAnnotatedType.visit(var1, var2);
   }

   public boolean containedBy(Type var1, Type var2) {
      switch (var1.getTag()) {
         case ERROR:
            return true;
         case UNDETVAR:
            if (var2.hasTag(TypeTag.WILDCARD)) {
               Type.UndetVar var3 = (Type.UndetVar)var1;
               Type.WildcardType var4 = (Type.WildcardType)var2.unannotatedType();
               Type var5;
               switch (var4.kind) {
                  case EXTENDS:
                     var5 = this.wildUpperBound(var2);
                     var3.addBound(Type.UndetVar.InferenceBound.UPPER, var5, this);
                  case UNBOUND:
                  default:
                     break;
                  case SUPER:
                     var5 = this.wildLowerBound(var2);
                     var3.addBound(Type.UndetVar.InferenceBound.LOWER, var5, this);
               }

               return true;
            }

            return this.isSameType(var1, var2);
         default:
            return this.containsType(var2, var1);
      }
   }

   boolean containsType(List var1, List var2) {
      while(var1.nonEmpty() && var2.nonEmpty() && this.containsType((Type)var1.head, (Type)var2.head)) {
         var1 = var1.tail;
         var2 = var2.tail;
      }

      return var1.isEmpty() && var2.isEmpty();
   }

   public boolean containsType(Type var1, Type var2) {
      return (Boolean)this.containsType.visit(var1, var2);
   }

   public boolean isCaptureOf(Type var1, Type.WildcardType var2) {
      return var1.hasTag(TypeTag.TYPEVAR) && ((Type.TypeVar)var1.unannotatedType()).isCaptured() ? this.isSameWildcard(var2, ((Type.CapturedType)var1.unannotatedType()).wildcard) : false;
   }

   public boolean isSameWildcard(Type.WildcardType var1, Type var2) {
      if (!var2.hasTag(TypeTag.WILDCARD)) {
         return false;
      } else {
         Type.WildcardType var3 = (Type.WildcardType)var2.unannotatedType();
         return var3.kind == var1.kind && var3.type == var1.type;
      }
   }

   public boolean containsTypeEquivalent(List var1, List var2) {
      while(var1.nonEmpty() && var2.nonEmpty() && this.containsTypeEquivalent((Type)var1.head, (Type)var2.head)) {
         var1 = var1.tail;
         var2 = var2.tail;
      }

      return var1.isEmpty() && var2.isEmpty();
   }

   public boolean isEqualityComparable(Type var1, Type var2, Warner var3) {
      if (var2.isNumeric() && var1.isNumeric()) {
         return true;
      } else {
         boolean var4 = var2.isPrimitive();
         boolean var5 = var1.isPrimitive();
         if (!var4 && !var5) {
            return this.isCastable(var1, var2, var3) || this.isCastable(var2, var1, var3);
         } else {
            return false;
         }
      }
   }

   public boolean isCastable(Type var1, Type var2) {
      return this.isCastable(var1, var2, this.noWarnings);
   }

   public boolean isCastable(Type var1, Type var2, Warner var3) {
      if (var1 == var2) {
         return true;
      } else if (var1.isPrimitive() == var2.isPrimitive()) {
         if (var3 != this.warnStack.head) {
            boolean var4;
            try {
               this.warnStack = this.warnStack.prepend(var3);
               this.checkUnsafeVarargsConversion(var1, var2, var3);
               var4 = (Boolean)this.isCastable.visit(var1, var2);
            } finally {
               this.warnStack = this.warnStack.tail;
            }

            return var4;
         } else {
            return (Boolean)this.isCastable.visit(var1, var2);
         }
      } else {
         return this.allowBoxing && (this.isConvertible(var1, var2, var3) || this.allowObjectToPrimitiveCast && var2.isPrimitive() && this.isSubtype(this.boxedClass(var2).type, var1));
      }
   }

   public boolean disjointTypes(List var1, List var2) {
      while(var1.tail != null && var2.tail != null) {
         if (this.disjointType((Type)var1.head, (Type)var2.head)) {
            return true;
         }

         var1 = var1.tail;
         var2 = var2.tail;
      }

      return false;
   }

   public boolean disjointType(Type var1, Type var2) {
      return (Boolean)this.disjointType.visit(var1, var2);
   }

   public List cvarLowerBounds(List var1) {
      return Type.map(var1, this.cvarLowerBoundMapping);
   }

   public boolean notSoftSubtype(Type var1, Type var2) {
      if (var1 == var2) {
         return false;
      } else if (var1.hasTag(TypeTag.TYPEVAR)) {
         Type.TypeVar var3 = (Type.TypeVar)var1;
         return !this.isCastable(var3.bound, this.relaxBound(var2), this.noWarnings);
      } else {
         if (!var2.hasTag(TypeTag.WILDCARD)) {
            var2 = this.cvarUpperBound(var2);
         }

         return !this.isSubtype(var1, this.relaxBound(var2));
      }
   }

   private Type relaxBound(Type var1) {
      if (var1.hasTag(TypeTag.TYPEVAR)) {
         while(true) {
            if (!var1.hasTag(TypeTag.TYPEVAR)) {
               var1 = this.rewriteQuantifiers(var1, true, true);
               break;
            }

            var1 = var1.getUpperBound();
         }
      }

      return var1;
   }

   public boolean isReifiable(Type var1) {
      return (Boolean)this.isReifiable.visit(var1);
   }

   public boolean isArray(Type var1) {
      while(var1.hasTag(TypeTag.WILDCARD)) {
         var1 = this.wildUpperBound(var1);
      }

      return var1.hasTag(TypeTag.ARRAY);
   }

   public Type elemtype(Type var1) {
      switch (var1.getTag()) {
         case ARRAY:
            var1 = var1.unannotatedType();
            return ((Type.ArrayType)var1).elemtype;
         case WILDCARD:
            return this.elemtype(this.wildUpperBound(var1));
         case ERROR:
            return var1;
         case FORALL:
            return this.elemtype(((Type.ForAll)var1).qtype);
         default:
            return null;
      }
   }

   public Type elemtypeOrType(Type var1) {
      Type var2 = this.elemtype(var1);
      return var2 != null ? var2 : var1;
   }

   public int dimensions(Type var1) {
      int var2;
      for(var2 = 0; var1.hasTag(TypeTag.ARRAY); var1 = this.elemtype(var1)) {
         ++var2;
      }

      return var2;
   }

   public Type.ArrayType makeArrayType(Type var1) {
      if (var1.hasTag(TypeTag.VOID) || var1.hasTag(TypeTag.PACKAGE)) {
         Assert.error("Type t must not be a VOID or PACKAGE type, " + var1.toString());
      }

      return new Type.ArrayType(var1, this.syms.arrayClass);
   }

   public Type asSuper(Type var1, Symbol var2) {
      return var2.type == this.syms.objectType ? this.syms.objectType : (Type)this.asSuper.visit(var1, var2);
   }

   public Type asOuterSuper(Type var1, Symbol var2) {
      switch (var1.getTag()) {
         case ARRAY:
            return this.isSubtype(var1, var2.type) ? var2.type : null;
         case CLASS:
            do {
               Type var3 = this.asSuper(var1, var2);
               if (var3 != null) {
                  return var3;
               }

               var1 = var1.getEnclosingType();
            } while(var1.hasTag(TypeTag.CLASS));

            return null;
         case TYPEVAR:
            return this.asSuper(var1, var2);
         case ERROR:
            return var1;
         default:
            return null;
      }
   }

   public Type asEnclosingSuper(Type var1, Symbol var2) {
      switch (((Type)var1).getTag()) {
         case ARRAY:
            return this.isSubtype((Type)var1, var2.type) ? var2.type : null;
         case CLASS:
            do {
               Type var3 = this.asSuper((Type)var1, var2);
               if (var3 != null) {
                  return var3;
               }

               Type var4 = ((Type)var1).getEnclosingType();
               var1 = var4.hasTag(TypeTag.CLASS) ? var4 : (((Type)var1).tsym.owner.enclClass() != null ? ((Type)var1).tsym.owner.enclClass().type : Type.noType);
            } while(((Type)var1).hasTag(TypeTag.CLASS));

            return null;
         case TYPEVAR:
            return this.asSuper((Type)var1, var2);
         case ERROR:
            return (Type)var1;
         default:
            return null;
      }
   }

   public Type memberType(Type var1, Symbol var2) {
      return (var2.flags() & 8L) != 0L ? var2.type : (Type)this.memberType.visit(var1, var2);
   }

   public boolean isAssignable(Type var1, Type var2) {
      return this.isAssignable(var1, var2, this.noWarnings);
   }

   public boolean isAssignable(Type var1, Type var2, Warner var3) {
      if (var1.hasTag(TypeTag.ERROR)) {
         return true;
      } else {
         if (var1.getTag().isSubRangeOf(TypeTag.INT) && var1.constValue() != null) {
            int var4 = ((Number)var1.constValue()).intValue();
            switch (var2.getTag()) {
               case CLASS:
                  switch (this.unboxedType(var2).getTag()) {
                     case BYTE:
                     case CHAR:
                     case SHORT:
                        return this.isAssignable(var1, this.unboxedType(var2), var3);
                     default:
                        return this.isConvertible(var1, var2, var3);
                  }
               case BYTE:
                  if (-128 <= var4 && var4 <= 127) {
                     return true;
                  }
                  break;
               case CHAR:
                  if (0 <= var4 && var4 <= 65535) {
                     return true;
                  }
                  break;
               case SHORT:
                  if (-32768 <= var4 && var4 <= 32767) {
                     return true;
                  }
                  break;
               case INT:
                  return true;
            }
         }

         return this.isConvertible(var1, var2, var3);
      }
   }

   public Type erasure(Type var1) {
      return this.eraseNotNeeded(var1) ? var1 : this.erasure(var1, false);
   }

   private boolean eraseNotNeeded(Type var1) {
      return var1.isPrimitive() || this.syms.stringType.tsym == var1.tsym;
   }

   private Type erasure(Type var1, boolean var2) {
      return var1.isPrimitive() ? var1 : (Type)this.erasure.visit(var1, var2);
   }

   public List erasure(List var1) {
      return Type.map(var1, this.erasureFun);
   }

   public Type erasureRecursive(Type var1) {
      return this.erasure(var1, true);
   }

   public List erasureRecursive(List var1) {
      return Type.map(var1, this.erasureRecFun);
   }

   public Type.IntersectionClassType makeIntersectionType(List var1) {
      return this.makeIntersectionType(var1, ((Type)var1.head).tsym.isInterface());
   }

   public Type.IntersectionClassType makeIntersectionType(List var1, boolean var2) {
      Assert.check(var1.nonEmpty());
      Type var3 = (Type)var1.head;
      if (var2) {
         var1 = var1.prepend(this.syms.objectType);
      }

      Symbol.ClassSymbol var4 = new Symbol.ClassSymbol(1090524161L, Type.moreInfo ? this.names.fromString(var1.toString()) : this.names.empty, (Type)null, this.syms.noSymbol);
      Type.IntersectionClassType var5 = new Type.IntersectionClassType(var1, var4, var2);
      var4.type = var5;
      var4.erasure_field = ((Type)var1.head).hasTag(TypeTag.TYPEVAR) ? this.syms.objectType : this.erasure(var3);
      var4.members_field = new Scope(var4);
      return var5;
   }

   public Type makeIntersectionType(Type var1, Type var2) {
      return this.makeIntersectionType(List.of(var1, var2));
   }

   public Type supertype(Type var1) {
      return (Type)this.supertype.visit(var1);
   }

   public List interfaces(Type var1) {
      return (List)this.interfaces.visit(var1);
   }

   public List directSupertypes(Type var1) {
      return (List)this.directSupertypes.visit(var1);
   }

   public boolean isDirectSuperInterface(Symbol.TypeSymbol var1, Symbol.TypeSymbol var2) {
      Iterator var3 = this.interfaces(var2.type).iterator();

      Type var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (Type)var3.next();
      } while(var1 != var4.tsym);

      return true;
   }

   public boolean isDerivedRaw(Type var1) {
      Boolean var2 = (Boolean)this.isDerivedRawCache.get(var1);
      if (var2 == null) {
         var2 = this.isDerivedRawInternal(var1);
         this.isDerivedRawCache.put(var1, var2);
      }

      return var2;
   }

   public boolean isDerivedRawInternal(Type var1) {
      if (var1.isErroneous()) {
         return false;
      } else {
         return var1.isRaw() || this.supertype(var1) != Type.noType && this.isDerivedRaw(this.supertype(var1)) || this.isDerivedRaw(this.interfaces(var1));
      }
   }

   public boolean isDerivedRaw(List var1) {
      List var2;
      for(var2 = var1; var2.nonEmpty() && !this.isDerivedRaw((Type)var2.head); var2 = var2.tail) {
      }

      return var2.nonEmpty();
   }

   public void setBounds(Type.TypeVar var1, List var2) {
      this.setBounds(var1, var2, ((Type)var2.head).tsym.isInterface());
   }

   public void setBounds(Type.TypeVar var1, List var2, boolean var3) {
      var1.bound = (Type)(var2.tail.isEmpty() ? (Type)var2.head : this.makeIntersectionType(var2, var3));
      var1.rank_field = -1;
   }

   public List getBounds(Type.TypeVar var1) {
      if (var1.bound.hasTag(TypeTag.NONE)) {
         return List.nil();
      } else if (!var1.bound.isErroneous() && var1.bound.isCompound()) {
         return (this.erasure((Type)var1).tsym.flags() & 512L) == 0L ? this.interfaces(var1).prepend(this.supertype(var1)) : this.interfaces(var1);
      } else {
         return List.of(var1.bound);
      }
   }

   public Type classBound(Type var1) {
      return (Type)this.classBound.visit(var1);
   }

   public boolean isSubSignature(Type var1, Type var2) {
      return this.isSubSignature(var1, var2, true);
   }

   public boolean isSubSignature(Type var1, Type var2, boolean var3) {
      return this.hasSameArgs(var1, var2, var3) || this.hasSameArgs(var1, this.erasure(var2), var3);
   }

   public boolean overrideEquivalent(Type var1, Type var2) {
      return this.hasSameArgs(var1, var2) || this.hasSameArgs(var1, this.erasure(var2)) || this.hasSameArgs(this.erasure(var1), var2);
   }

   public boolean overridesObjectMethod(Symbol.TypeSymbol var1, Symbol var2) {
      for(Scope.Entry var3 = this.syms.objectType.tsym.members().lookup(var2.name); var3.scope != null; var3 = var3.next()) {
         if (var2.overrides(var3.sym, var1, this, true)) {
            return true;
         }
      }

      return false;
   }

   public Symbol.MethodSymbol implementation(Symbol.MethodSymbol var1, Symbol.TypeSymbol var2, boolean var3, Filter var4) {
      return this.implCache.get(var1, var2, var3, var4);
   }

   public Scope.CompoundScope membersClosure(Type var1, boolean var2) {
      Scope.CompoundScope var3 = (Scope.CompoundScope)this.membersCache.visit(var1, (Object)null);
      if (var3 == null) {
         Assert.error("type " + var1);
      }

      return (Scope.CompoundScope)(var2 ? this.membersCache.new MembersScope(var3) : var3);
   }

   public Symbol.MethodSymbol firstUnimplementedAbstract(Symbol.ClassSymbol var1) {
      try {
         return this.firstUnimplementedAbstractImpl(var1, var1);
      } catch (Symbol.CompletionFailure var3) {
         this.chk.completionError(this.enter.getEnv(var1).tree.pos(), var3);
         return null;
      }
   }

   private Symbol.MethodSymbol firstUnimplementedAbstractImpl(Symbol.ClassSymbol var1, Symbol.ClassSymbol var2) {
      Symbol.MethodSymbol var3 = null;
      if (var2 == var1 || (var2.flags() & 1536L) != 0L) {
         Scope var4 = var2.members();

         for(Scope.Entry var5 = var4.elems; var3 == null && var5 != null; var5 = var5.sibling) {
            if (var5.sym.kind == 16 && (var5.sym.flags() & 8796095120384L) == 1024L) {
               Symbol.MethodSymbol var6 = (Symbol.MethodSymbol)var5.sym;
               Symbol.MethodSymbol var7 = var6.implementation(var1, this, true);
               if ((var7 == null || var7 == var6) && this.allowDefaultMethods) {
                  Symbol.MethodSymbol var8 = (Symbol.MethodSymbol)this.interfaceCandidates(var1.type, var6).head;
                  if (var8 != null && var8.overrides(var6, var1, this, true)) {
                     var7 = var8;
                  }
               }

               if (var7 == null || var7 == var6) {
                  var3 = var6;
               }
            }
         }

         if (var3 == null) {
            Type var9 = this.supertype(var2.type);
            if (var9.hasTag(TypeTag.CLASS)) {
               var3 = this.firstUnimplementedAbstractImpl(var1, (Symbol.ClassSymbol)var9.tsym);
            }
         }

         for(List var10 = this.interfaces(var2.type); var3 == null && var10.nonEmpty(); var10 = var10.tail) {
            var3 = this.firstUnimplementedAbstractImpl(var1, (Symbol.ClassSymbol)((Type)var10.head).tsym);
         }
      }

      return var3;
   }

   public List interfaceCandidates(Type var1, Symbol.MethodSymbol var2) {
      MethodFilter var3 = new MethodFilter(var2, var1);
      List var4 = List.nil();
      Iterator var5 = this.membersClosure(var1, false).getElements(var3).iterator();

      while(var5.hasNext()) {
         Symbol var6 = (Symbol)var5.next();
         if (!var1.tsym.isInterface() && !var6.owner.isInterface()) {
            return List.of((Symbol.MethodSymbol)var6);
         }

         if (!var4.contains(var6)) {
            var4 = var4.prepend((Symbol.MethodSymbol)var6);
         }
      }

      return this.prune(var4);
   }

   public List prune(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)var3.next();
         boolean var5 = true;
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            Symbol.MethodSymbol var7 = (Symbol.MethodSymbol)var6.next();
            if (var4 != var7 && var7.owner != var4.owner && this.asSuper(var7.owner.type, var4.owner) != null) {
               var5 = false;
               break;
            }
         }

         if (var5) {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   public boolean hasSameArgs(Type var1, Type var2) {
      return this.hasSameArgs(var1, var2, true);
   }

   public boolean hasSameArgs(Type var1, Type var2, boolean var3) {
      return this.hasSameArgs(var1, var2, var3 ? this.hasSameArgs_strict : this.hasSameArgs_nonstrict);
   }

   private boolean hasSameArgs(Type var1, Type var2, TypeRelation var3) {
      return (Boolean)var3.visit(var1, var2);
   }

   public List subst(List var1, List var2, List var3) {
      return (new Subst(var2, var3)).subst(var1);
   }

   public Type subst(Type var1, List var2, List var3) {
      return (new Subst(var2, var3)).subst(var1);
   }

   public List substBounds(List var1, List var2, List var3) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         ListBuffer var4 = new ListBuffer();
         boolean var5 = false;

         Type var9;
         for(Iterator var6 = var1.iterator(); var6.hasNext(); var4.append(var9)) {
            Type var7 = (Type)var6.next();
            Type.TypeVar var8 = (Type.TypeVar)var7;
            var9 = this.subst(var8.bound, var2, var3);
            if (var9 != var8.bound) {
               var5 = true;
            }
         }

         if (!var5) {
            return var1;
         } else {
            ListBuffer var11 = new ListBuffer();
            Iterator var12 = var1.iterator();

            while(var12.hasNext()) {
               Type var14 = (Type)var12.next();
               var11.append(new Type.TypeVar(var14.tsym, (Type)null, this.syms.botType));
            }

            List var13 = var4.toList();
            var2 = var1;

            for(var3 = var11.toList(); !var13.isEmpty(); var13 = var13.tail) {
               var13.head = this.subst((Type)var13.head, var2, var3);
            }

            var13 = var4.toList();

            for(Iterator var15 = var11.toList().iterator(); var15.hasNext(); var13 = var13.tail) {
               var9 = (Type)var15.next();
               Type.TypeVar var10 = (Type.TypeVar)var9;
               var10.bound = (Type)var13.head;
            }

            return var11.toList();
         }
      }
   }

   public Type.TypeVar substBound(Type.TypeVar var1, List var2, List var3) {
      Type var4 = this.subst(var1.bound, var2, var3);
      if (var4 == var1.bound) {
         return var1;
      } else {
         Type.TypeVar var5 = new Type.TypeVar(var1.tsym, (Type)null, this.syms.botType);
         var5.bound = this.subst(var4, List.of(var1), List.of(var5));
         return var5;
      }
   }

   public boolean hasSameBounds(Type.ForAll var1, Type.ForAll var2) {
      List var3 = var1.tvars;

      List var4;
      for(var4 = var2.tvars; var3.nonEmpty() && var4.nonEmpty() && this.isSameType(((Type)var3.head).getUpperBound(), this.subst(((Type)var4.head).getUpperBound(), var2.tvars, var1.tvars)); var4 = var4.tail) {
         var3 = var3.tail;
      }

      return var3.isEmpty() && var4.isEmpty();
   }

   public List newInstances(List var1) {
      List var2 = Type.map(var1, newInstanceFun);

      for(List var3 = var2; var3.nonEmpty(); var3 = var3.tail) {
         Type.TypeVar var4 = (Type.TypeVar)var3.head;
         var4.bound = this.subst(var4.bound, var1, var2);
      }

      return var2;
   }

   public Type createMethodTypeWithParameters(Type var1, List var2) {
      return (Type)var1.accept((Type.Visitor)this.methodWithParameters, var2);
   }

   public Type createMethodTypeWithThrown(Type var1, List var2) {
      return (Type)var1.accept((Type.Visitor)this.methodWithThrown, var2);
   }

   public Type createMethodTypeWithReturn(Type var1, Type var2) {
      return (Type)var1.accept((Type.Visitor)this.methodWithReturn, var2);
   }

   public Type createErrorType(Type var1) {
      return new Type.ErrorType(var1, this.syms.errSymbol);
   }

   public Type createErrorType(Symbol.ClassSymbol var1, Type var2) {
      return new Type.ErrorType(var1, var2);
   }

   public Type createErrorType(Name var1, Symbol.TypeSymbol var2, Type var3) {
      return new Type.ErrorType(var1, var2, var3);
   }

   public int rank(Type var1) {
      var1 = var1.unannotatedType();
      switch (var1.getTag()) {
         case CLASS:
            Type.ClassType var6 = (Type.ClassType)var1;
            if (var6.rank_field < 0) {
               Name var7 = var6.tsym.getQualifiedName();
               if (var7 == this.names.java_lang_Object) {
                  var6.rank_field = 0;
               } else {
                  int var8 = this.rank(this.supertype(var6));

                  for(List var5 = this.interfaces(var6); var5.nonEmpty(); var5 = var5.tail) {
                     if (this.rank((Type)var5.head) > var8) {
                        var8 = this.rank((Type)var5.head);
                     }
                  }

                  var6.rank_field = var8 + 1;
               }
            }

            return var6.rank_field;
         case TYPEVAR:
            Type.TypeVar var2 = (Type.TypeVar)var1;
            if (var2.rank_field < 0) {
               int var3 = this.rank(this.supertype(var2));

               for(List var4 = this.interfaces(var2); var4.nonEmpty(); var4 = var4.tail) {
                  if (this.rank((Type)var4.head) > var3) {
                     var3 = this.rank((Type)var4.head);
                  }
               }

               var2.rank_field = var3 + 1;
            }

            return var2.rank_field;
         case NONE:
         case ERROR:
            return 0;
         default:
            throw new AssertionError();
      }
   }

   public String toString(Type var1, Locale var2) {
      return Printer.createStandardPrinter(this.messages).visit(var1, var2);
   }

   public String toString(Symbol var1, Locale var2) {
      return Printer.createStandardPrinter(this.messages).visit(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public String toString(Type var1) {
      if (var1.hasTag(TypeTag.FORALL)) {
         Type.ForAll var2 = (Type.ForAll)var1;
         return this.typaramsString(var2.tvars) + var2.qtype;
      } else {
         return "" + var1;
      }
   }

   private String typaramsString(List var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append('<');
      boolean var3 = true;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Type var5 = (Type)var4.next();
         if (!var3) {
            var2.append(", ");
         }

         var3 = false;
         this.appendTyparamString((Type.TypeVar)var5.unannotatedType(), var2);
      }

      var2.append('>');
      return var2.toString();
   }

   private void appendTyparamString(Type.TypeVar var1, StringBuilder var2) {
      var2.append(var1);
      if (var1.bound != null && var1.bound.tsym.getQualifiedName() != this.names.java_lang_Object) {
         var2.append(" extends ");
         Type var3 = var1.bound;
         if (!var3.isCompound()) {
            var2.append(var3);
         } else if ((this.erasure((Type)var1).tsym.flags() & 512L) == 0L) {
            var2.append(this.supertype(var1));
            Iterator var4 = this.interfaces(var1).iterator();

            while(var4.hasNext()) {
               Type var5 = (Type)var4.next();
               var2.append('&');
               var2.append(var5);
            }
         } else {
            boolean var7 = true;
            Iterator var8 = this.interfaces(var1).iterator();

            while(var8.hasNext()) {
               Type var6 = (Type)var8.next();
               if (!var7) {
                  var2.append('&');
               }

               var7 = false;
               var2.append(var6);
            }
         }

      }
   }

   public List closure(Type var1) {
      List var2 = (List)this.closureCache.get(var1);
      if (var2 == null) {
         Type var3 = this.supertype(var1);
         if (!var1.isCompound()) {
            if (var3.hasTag(TypeTag.CLASS)) {
               var2 = this.insert(this.closure(var3), var1);
            } else if (var3.hasTag(TypeTag.TYPEVAR)) {
               var2 = this.closure(var3).prepend(var1);
            } else {
               var2 = List.of(var1);
            }
         } else {
            var2 = this.closure(this.supertype(var1));
         }

         for(List var4 = this.interfaces(var1); var4.nonEmpty(); var4 = var4.tail) {
            var2 = this.union(var2, this.closure((Type)var4.head));
         }

         this.closureCache.put(var1, var2);
      }

      return var2;
   }

   public List insert(List var1, Type var2) {
      if (var1.isEmpty()) {
         return var1.prepend(var2);
      } else if (var2.tsym == ((Type)var1.head).tsym) {
         return var1;
      } else {
         return var2.tsym.precedes(((Type)var1.head).tsym, this) ? var1.prepend(var2) : this.insert(var1.tail, var2).prepend(var1.head);
      }
   }

   public List union(List var1, List var2) {
      if (var1.isEmpty()) {
         return var2;
      } else if (var2.isEmpty()) {
         return var1;
      } else if (((Type)var1.head).tsym == ((Type)var2.head).tsym) {
         return this.union(var1.tail, var2.tail).prepend(var1.head);
      } else if (((Type)var1.head).tsym.precedes(((Type)var2.head).tsym, this)) {
         return this.union(var1.tail, var2).prepend(var1.head);
      } else {
         return ((Type)var2.head).tsym.precedes(((Type)var1.head).tsym, this) ? this.union(var1, var2.tail).prepend(var2.head) : this.union(var1.tail, var2).prepend(var1.head);
      }
   }

   public List intersect(List var1, List var2) {
      if (var1 == var2) {
         return var1;
      } else if (!var1.isEmpty() && !var2.isEmpty()) {
         if (((Type)var1.head).tsym.precedes(((Type)var2.head).tsym, this)) {
            return this.intersect(var1.tail, var2);
         } else if (((Type)var2.head).tsym.precedes(((Type)var1.head).tsym, this)) {
            return this.intersect(var1, var2.tail);
         } else if (this.isSameType((Type)var1.head, (Type)var2.head)) {
            return this.intersect(var1.tail, var2.tail).prepend(var1.head);
         } else {
            if (((Type)var1.head).tsym == ((Type)var2.head).tsym && ((Type)var1.head).hasTag(TypeTag.CLASS) && ((Type)var2.head).hasTag(TypeTag.CLASS)) {
               if (((Type)var1.head).isParameterized() && ((Type)var2.head).isParameterized()) {
                  Type var3 = this.merge((Type)var1.head, (Type)var2.head);
                  return this.intersect(var1.tail, var2.tail).prepend(var3);
               }

               if (((Type)var1.head).isRaw() || ((Type)var2.head).isRaw()) {
                  return this.intersect(var1.tail, var2.tail).prepend(this.erasure((Type)var1.head));
               }
            }

            return this.intersect(var1.tail, var2.tail);
         }
      } else {
         return List.nil();
      }
   }

   private Type merge(Type var1, Type var2) {
      Type.ClassType var3 = (Type.ClassType)var1;
      List var4 = var3.getTypeArguments();
      Type.ClassType var5 = (Type.ClassType)var2;
      List var6 = var5.getTypeArguments();
      ListBuffer var7 = new ListBuffer();

      List var8;
      for(var8 = var3.tsym.type.getTypeArguments(); var4.nonEmpty() && var6.nonEmpty() && var8.nonEmpty(); var8 = var8.tail) {
         if (this.containsType((Type)var4.head, (Type)var6.head)) {
            var7.append(var4.head);
         } else if (this.containsType((Type)var6.head, (Type)var4.head)) {
            var7.append(var6.head);
         } else {
            TypePair var9 = new TypePair(var1, var2);
            Type.WildcardType var10;
            if (this.mergeCache.add(var9)) {
               var10 = new Type.WildcardType(this.lub(this.wildUpperBound((Type)var4.head), this.wildUpperBound((Type)var6.head)), BoundKind.EXTENDS, this.syms.boundClass);
               this.mergeCache.remove(var9);
            } else {
               var10 = new Type.WildcardType(this.syms.objectType, BoundKind.UNBOUND, this.syms.boundClass);
            }

            var7.append(var10.withTypeVar((Type)var8.head));
         }

         var4 = var4.tail;
         var6 = var6.tail;
      }

      Assert.check(var4.isEmpty() && var6.isEmpty() && var8.isEmpty());
      return new Type.ClassType(var3.getEnclosingType(), var7.toList(), var3.tsym);
   }

   private Type compoundMin(List var1) {
      if (var1.isEmpty()) {
         return this.syms.objectType;
      } else {
         List var2 = this.closureMin(var1);
         if (var2.isEmpty()) {
            return null;
         } else {
            return (Type)(var2.tail.isEmpty() ? (Type)var2.head : this.makeIntersectionType(var2));
         }
      }
   }

   private List closureMin(List var1) {
      ListBuffer var2 = new ListBuffer();
      ListBuffer var3 = new ListBuffer();

      for(HashSet var4 = new HashSet(); !var1.isEmpty(); var1 = var1.tail) {
         Type var5 = (Type)var1.head;
         boolean var6 = !var4.contains(var5);
         Iterator var7;
         Type var8;
         if (var6 && var5.hasTag(TypeTag.TYPEVAR)) {
            var7 = var1.tail.iterator();

            while(var7.hasNext()) {
               var8 = (Type)var7.next();
               if (this.isSubtypeNoCapture(var8, var5)) {
                  var6 = false;
                  break;
               }
            }
         }

         if (var6) {
            if (var5.isInterface()) {
               var3.append(var5);
            } else {
               var2.append(var5);
            }

            var7 = var1.tail.iterator();

            while(var7.hasNext()) {
               var8 = (Type)var7.next();
               if (this.isSubtypeNoCapture(var5, var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return var2.appendList(var3).toList();
   }

   public Type lub(List var1) {
      return this.lub((Type[])var1.toArray(new Type[var1.length()]));
   }

   public Type lub(Type... var1) {
      int[] var5 = new int[var1.length];
      int var6 = 0;

      for(int var7 = 0; var7 < var1.length; ++var7) {
         Type var8 = var1[var7];
         switch (var8.getTag()) {
            case ARRAY:
               var6 |= var5[var7] = 1;
               break;
            case CLASS:
               var6 |= var5[var7] = 2;
               break;
            case TYPEVAR:
               do {
                  var8 = var8.getUpperBound();
               } while(var8.hasTag(TypeTag.TYPEVAR));

               if (var8.hasTag(TypeTag.ARRAY)) {
                  var6 |= var5[var7] = 1;
               } else {
                  var6 |= var5[var7] = 2;
               }
               break;
            default:
               var5[var7] = 0;
               if (var8.isPrimitive()) {
                  return this.syms.errType;
               }
         }
      }

      Type var10;
      int var18;
      switch (var6) {
         case 0:
            return this.syms.botType;
         case 1:
            Type[] var17 = new Type[var1.length];

            for(var18 = 0; var18 < var1.length; ++var18) {
               Type var20 = var17[var18] = this.elemTypeFun.apply(var1[var18]);
               if (var20.isPrimitive()) {
                  var10 = var1[0];

                  for(int var24 = 1; var24 < var1.length; ++var24) {
                     if (!this.isSameType(var10, var1[var24])) {
                        return this.arraySuperType();
                     }
                  }

                  return var10;
               }
            }

            return new Type.ArrayType(this.lub(var17), this.syms.arrayClass);
         case 2:
            var18 = 0;

            for(int var9 = 0; var9 < var1.length; ++var9) {
               var10 = var1[var9];
               if (var10.hasTag(TypeTag.CLASS) || var10.hasTag(TypeTag.TYPEVAR)) {
                  break;
               }

               ++var18;
            }

            Assert.check(var18 < var1.length);
            List var19 = this.erasedSupertypes(var1[var18]);

            for(int var21 = var18 + 1; var21 < var1.length; ++var21) {
               Type var11 = var1[var21];
               if (var11.hasTag(TypeTag.CLASS) || var11.hasTag(TypeTag.TYPEVAR)) {
                  var19 = this.intersect(var19, this.erasedSupertypes(var11));
               }
            }

            List var22 = this.closureMin(var19);
            List var23 = List.nil();

            List var14;
            for(Iterator var12 = var22.iterator(); var12.hasNext(); var23 = var23.appendList(var14)) {
               Type var13 = (Type)var12.next();
               var14 = List.of(this.asSuper(var1[var18], var13.tsym));

               for(int var15 = var18 + 1; var15 < var1.length; ++var15) {
                  Type var16 = this.asSuper(var1[var15], var13.tsym);
                  var14 = this.intersect(var14, var16 != null ? List.of(var16) : List.nil());
               }
            }

            return this.compoundMin(var23);
         default:
            List var25 = List.of(this.arraySuperType());

            for(int var26 = 0; var26 < var1.length; ++var26) {
               if (var5[var26] != 1) {
                  var25 = var25.prepend(var1[var26]);
               }
            }

            return this.lub(var25);
      }
   }

   List erasedSupertypes(Type var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = this.closure(var1).iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         if (var4.hasTag(TypeTag.TYPEVAR)) {
            var2.append(var4);
         } else {
            var2.append(this.erasure(var4));
         }
      }

      return var2.toList();
   }

   private Type arraySuperType() {
      if (this.arraySuperType == null) {
         synchronized(this) {
            if (this.arraySuperType == null) {
               this.arraySuperType = this.makeIntersectionType(List.of(this.syms.serializableType, this.syms.cloneableType), true);
            }
         }
      }

      return this.arraySuperType;
   }

   public Type glb(List var1) {
      Type var2 = (Type)var1.head;

      Type var4;
      for(Iterator var3 = var1.tail.iterator(); var3.hasNext(); var2 = this.glb(var2, var4)) {
         var4 = (Type)var3.next();
         if (var2.isErroneous()) {
            return var2;
         }
      }

      return var2;
   }

   public Type glb(Type var1, Type var2) {
      if (var2 == null) {
         return var1;
      } else if (!var1.isPrimitive() && !var2.isPrimitive()) {
         if (this.isSubtypeNoCapture(var1, var2)) {
            return var1;
         } else if (this.isSubtypeNoCapture(var2, var1)) {
            return var2;
         } else {
            List var3 = this.union(this.closure(var1), this.closure(var2));
            return this.glbFlattened(var3, var1);
         }
      } else {
         return this.syms.errType;
      }
   }

   private Type glbFlattened(List var1, Type var2) {
      List var3 = this.closureMin(var1);
      if (var3.isEmpty()) {
         return this.syms.objectType;
      } else if (var3.tail.isEmpty()) {
         return (Type)var3.head;
      } else {
         int var4 = 0;
         List var5 = List.nil();
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            Type var7 = (Type)var6.next();
            if (!var7.isInterface()) {
               ++var4;
               Type var8 = this.cvarLowerBound(var7);
               if (var7 != var8 && !var8.hasTag(TypeTag.BOT)) {
                  var5 = this.insert(var5, var8);
               }
            }
         }

         if (var4 > 1) {
            if (var5.isEmpty()) {
               return this.createErrorType(var2);
            } else {
               return this.glbFlattened(this.union(var3, var5), var2);
            }
         } else {
            return this.makeIntersectionType(var3);
         }
      }
   }

   public int hashCode(Type var1) {
      return (Integer)hashCode.visit(var1);
   }

   public boolean resultSubtype(Type var1, Type var2, Warner var3) {
      List var4 = var1.getTypeArguments();
      List var5 = var2.getTypeArguments();
      Type var6 = var1.getReturnType();
      Type var7 = this.subst(var2.getReturnType(), var5, var4);
      return this.covariantReturnType(var6, var7, var3);
   }

   public boolean returnTypeSubstitutable(Type var1, Type var2) {
      return this.hasSameArgs(var1, var2) ? this.resultSubtype(var1, var2, this.noWarnings) : this.covariantReturnType(var1.getReturnType(), this.erasure(var2.getReturnType()), this.noWarnings);
   }

   public boolean returnTypeSubstitutable(Type var1, Type var2, Type var3, Warner var4) {
      if (this.isSameType(var1.getReturnType(), var3)) {
         return true;
      } else if (!var1.getReturnType().isPrimitive() && !var3.isPrimitive()) {
         if (this.hasSameArgs(var1, var2)) {
            return this.covariantReturnType(var1.getReturnType(), var3, var4);
         } else if (!this.allowCovariantReturns) {
            return false;
         } else if (this.isSubtypeUnchecked(var1.getReturnType(), var3, var4)) {
            return true;
         } else if (!this.isSubtype(var1.getReturnType(), this.erasure(var3))) {
            return false;
         } else {
            var4.warn(Lint.LintCategory.UNCHECKED);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean covariantReturnType(Type var1, Type var2, Warner var3) {
      return this.isSameType(var1, var2) || this.allowCovariantReturns && !var1.isPrimitive() && !var2.isPrimitive() && this.isAssignable(var1, var2, var3);
   }

   public Symbol.ClassSymbol boxedClass(Type var1) {
      return this.reader.enterClass(this.syms.boxedName[var1.getTag().ordinal()]);
   }

   public Type boxedTypeOrType(Type var1) {
      return var1.isPrimitive() ? this.boxedClass(var1).type : var1;
   }

   public Type unboxedType(Type var1) {
      if (this.allowBoxing) {
         for(int var2 = 0; var2 < this.syms.boxedName.length; ++var2) {
            Name var3 = this.syms.boxedName[var2];
            if (var3 != null && this.asSuper(var1, this.reader.enterClass(var3)) != null) {
               return this.syms.typeOfTag[var2];
            }
         }
      }

      return Type.noType;
   }

   public Type unboxedTypeOrType(Type var1) {
      Type var2 = this.unboxedType(var1);
      return var2.hasTag(TypeTag.NONE) ? var1 : var2;
   }

   public List capture(List var1) {
      List var2 = List.nil();

      Type var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = var2.prepend(this.capture(var4))) {
         var4 = (Type)var3.next();
      }

      return var2.reverse();
   }

   public Type capture(Type var1) {
      if (!var1.hasTag(TypeTag.CLASS)) {
         return var1;
      } else {
         if (var1.getEnclosingType() != Type.noType) {
            Type var2 = this.capture(var1.getEnclosingType());
            if (var2 != var1.getEnclosingType()) {
               Type var3 = this.memberType(var2, var1.tsym);
               var1 = this.subst(var3, var1.tsym.type.getTypeArguments(), var1.getTypeArguments());
            }
         }

         var1 = var1.unannotatedType();
         Type.ClassType var16 = (Type.ClassType)var1;
         if (!var16.isRaw() && var16.isParameterized()) {
            Type.ClassType var17 = (Type.ClassType)var16.asElement().asType();
            List var4 = var17.getTypeArguments();
            List var5 = var16.getTypeArguments();
            List var6 = this.freshTypeVariables(var5);
            List var7 = var4;
            List var8 = var5;
            List var9 = var6;

            boolean var10;
            for(var10 = false; !var7.isEmpty() && !var8.isEmpty() && !var9.isEmpty(); var9 = var9.tail) {
               if (var9.head != var8.head) {
                  var10 = true;
                  Type.WildcardType var11 = (Type.WildcardType)((Type)var8.head).unannotatedType();
                  Type var12 = ((Type)var7.head).getUpperBound();
                  Type.CapturedType var13 = (Type.CapturedType)((Type)var9.head).unannotatedType();
                  if (var12 == null) {
                     var12 = this.syms.objectType;
                  }

                  switch (var11.kind) {
                     case EXTENDS:
                        var13.bound = this.glb(var11.getExtendsBound(), this.subst(var12, var4, var6));
                        var13.lower = this.syms.botType;
                        break;
                     case UNBOUND:
                        var13.bound = this.subst(var12, var4, var6);
                        var13.lower = this.syms.botType;
                        break;
                     case SUPER:
                        var13.bound = this.subst(var12, var4, var6);
                        var13.lower = var11.getSuperBound();
                  }

                  Type var14 = var13.bound.hasTag(TypeTag.UNDETVAR) ? ((Type.UndetVar)var13.bound).qtype : var13.bound;
                  Type var15 = var13.lower.hasTag(TypeTag.UNDETVAR) ? ((Type.UndetVar)var13.lower).qtype : var13.lower;
                  if (!var13.bound.hasTag(TypeTag.ERROR) && !var13.lower.hasTag(TypeTag.ERROR) && this.isSameType(var14, var15, false)) {
                     var9.head = var13.bound;
                  }
               }

               var7 = var7.tail;
               var8 = var8.tail;
            }

            if (var7.isEmpty() && var8.isEmpty() && var9.isEmpty()) {
               return (Type)(var10 ? new Type.ClassType(var16.getEnclosingType(), var6, var16.tsym) : var1);
            } else {
               return this.erasure(var1);
            }
         } else {
            return var16;
         }
      }
   }

   public List freshTypeVariables(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         if (var4.hasTag(TypeTag.WILDCARD)) {
            var4 = var4.unannotatedType();
            Type var5 = ((Type.WildcardType)var4).getExtendsBound();
            if (var5 == null) {
               var5 = this.syms.objectType;
            }

            var2.append(new Type.CapturedType(this.capturedName, this.syms.noSymbol, var5, this.syms.botType, (Type.WildcardType)var4));
         } else {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   private boolean sideCast(Type var1, Type var2, Warner var3) {
      boolean var4 = false;
      Type var5 = var2;
      if ((var2.tsym.flags() & 512L) == 0L) {
         Assert.check((var1.tsym.flags() & 512L) != 0L);
         var4 = true;
         var2 = var1;
         var1 = var5;
      }

      List var6 = this.superClosure(var2, this.erasure(var1));

      boolean var7;
      for(var7 = var6.isEmpty(); var6.nonEmpty(); var6 = var6.tail) {
         Type var8 = this.asSuper(var1, ((Type)var6.head).tsym);
         Type var9 = (Type)var6.head;
         if (this.disjointTypes(var8.getTypeArguments(), var9.getTypeArguments())) {
            return false;
         }

         boolean var10000;
         label52: {
            label51: {
               if (!var7) {
                  if (var4) {
                     if (!this.giveWarning(var9, var8)) {
                        break label51;
                     }
                  } else if (!this.giveWarning(var8, var9)) {
                     break label51;
                  }
               }

               var10000 = true;
               break label52;
            }

            var10000 = false;
         }

         var7 = var10000;
      }

      if (var7 && !this.isReifiable(var4 ? var1 : var2)) {
         var3.warn(Lint.LintCategory.UNCHECKED);
      }

      if (!this.allowCovariantReturns) {
         this.chk.checkCompatibleAbstracts(var3.pos(), var1, var2);
      }

      return true;
   }

   private boolean sideCastFinal(Type var1, Type var2, Warner var3) {
      boolean var4 = false;
      Type var5 = var2;
      if ((var2.tsym.flags() & 512L) == 0L) {
         Assert.check((var1.tsym.flags() & 512L) != 0L);
         var4 = true;
         var2 = var1;
         var1 = var5;
      }

      Assert.check((var1.tsym.flags() & 16L) != 0L);
      Type var6 = this.asSuper(var1, var2.tsym);
      if (var6 == null) {
         return false;
      } else if (this.disjointTypes(var6.getTypeArguments(), var2.getTypeArguments())) {
         return false;
      } else {
         if (!this.allowCovariantReturns) {
            this.chk.checkCompatibleAbstracts(var3.pos(), var1, var2);
         }

         if (!this.isReifiable(var5)) {
            if (var4) {
               if (!this.giveWarning(var2, var6)) {
                  return true;
               }
            } else if (!this.giveWarning(var6, var2)) {
               return true;
            }

            var3.warn(Lint.LintCategory.UNCHECKED);
         }

         return true;
      }
   }

   private boolean giveWarning(Type var1, Type var2) {
      List var3 = var2.isCompound() ? ((Type.IntersectionClassType)var2.unannotatedType()).getComponents() : List.of(var2);
      Iterator var4 = var3.iterator();

      Type var5;
      Type var6;
      do {
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return false;
                  }

                  var5 = (Type)var4.next();
                  var6 = this.asSub(var1, var5.tsym);
               } while(!var5.isParameterized());
            } while(this.isUnbounded(var5));
         } while(this.isSubtype(var1, var5));
      } while(var6 != null && this.containsType(var5.allparams(), var6.allparams()));

      return true;
   }

   private List superClosure(Type var1, Type var2) {
      List var3 = List.nil();

      for(List var4 = this.interfaces(var1); var4.nonEmpty(); var4 = var4.tail) {
         if (this.isSubtype(var2, this.erasure((Type)var4.head))) {
            var3 = this.insert(var3, (Type)var4.head);
         } else {
            var3 = this.union(var3, this.superClosure((Type)var4.head, var2));
         }
      }

      return var3;
   }

   private boolean containsTypeEquivalent(Type var1, Type var2) {
      return this.isSameType(var1, var2) || this.containsType(var1, var2) && this.containsType(var2, var1);
   }

   public void adapt(Type var1, Type var2, ListBuffer var3, ListBuffer var4) throws AdaptFailure {
      (new Adapter(var3, var4)).adapt(var1, var2);
   }

   private void adaptSelf(Type var1, ListBuffer var2, ListBuffer var3) {
      try {
         this.adapt(var1.tsym.type, var1, var2, var3);
      } catch (AdaptFailure var5) {
         throw new AssertionError(var5);
      }
   }

   private Type rewriteQuantifiers(Type var1, boolean var2, boolean var3) {
      return (Type)(new Rewriter(var2, var3)).visit(var1);
   }

   private Type.WildcardType makeExtendsWildcard(Type var1, Type.TypeVar var2) {
      return var1 == this.syms.objectType ? new Type.WildcardType(this.syms.objectType, BoundKind.UNBOUND, this.syms.boundClass, var2) : new Type.WildcardType(var1, BoundKind.EXTENDS, this.syms.boundClass, var2);
   }

   private Type.WildcardType makeSuperWildcard(Type var1, Type.TypeVar var2) {
      return var1.hasTag(TypeTag.BOT) ? new Type.WildcardType(this.syms.objectType, BoundKind.UNBOUND, this.syms.boundClass, var2) : new Type.WildcardType(var1, BoundKind.SUPER, this.syms.boundClass, var2);
   }

   public Attribute.RetentionPolicy getRetention(Attribute.Compound var1) {
      return this.getRetention((Symbol)var1.type.tsym);
   }

   public Attribute.RetentionPolicy getRetention(Symbol var1) {
      Attribute.RetentionPolicy var2 = Attribute.RetentionPolicy.CLASS;
      Attribute.Compound var3 = var1.attribute(this.syms.retentionType.tsym);
      if (var3 != null) {
         Attribute var4 = var3.member(this.names.value);
         if (var4 != null && var4 instanceof Attribute.Enum) {
            Name var5 = ((Attribute.Enum)var4).value.name;
            if (var5 == this.names.SOURCE) {
               var2 = Attribute.RetentionPolicy.SOURCE;
            } else if (var5 == this.names.CLASS) {
               var2 = Attribute.RetentionPolicy.CLASS;
            } else if (var5 == this.names.RUNTIME) {
               var2 = Attribute.RetentionPolicy.RUNTIME;
            }
         }
      }

      return var2;
   }

   public abstract static class SignatureGenerator {
      private final Types types;

      protected abstract void append(char var1);

      protected abstract void append(byte[] var1);

      protected abstract void append(Name var1);

      protected void classReference(Symbol.ClassSymbol var1) {
      }

      protected SignatureGenerator(Types var1) {
         this.types = var1;
      }

      public void assembleSig(Type var1) {
         var1 = var1.unannotatedType();
         switch (var1.getTag()) {
            case ARRAY:
               Type.ArrayType var2 = (Type.ArrayType)var1;
               this.append('[');
               this.assembleSig(var2.elemtype);
               break;
            case CLASS:
               this.append('L');
               this.assembleClassSig(var1);
               this.append(';');
               break;
            case BYTE:
               this.append('B');
               break;
            case CHAR:
               this.append('C');
               break;
            case SHORT:
               this.append('S');
               break;
            case INT:
               this.append('I');
               break;
            case LONG:
               this.append('J');
               break;
            case FLOAT:
               this.append('F');
               break;
            case DOUBLE:
               this.append('D');
               break;
            case BOOLEAN:
               this.append('Z');
               break;
            case VOID:
               this.append('V');
               break;
            case TYPEVAR:
               this.append('T');
               this.append(var1.tsym.name);
               this.append(';');
               break;
            case BOT:
            case NONE:
            case ERROR:
            case UNDETVAR:
            default:
               throw new AssertionError("typeSig " + var1.getTag());
            case WILDCARD:
               Type.WildcardType var6 = (Type.WildcardType)var1;
               switch (var6.kind) {
                  case EXTENDS:
                     this.append('+');
                     this.assembleSig(var6.type);
                     return;
                  case UNBOUND:
                     this.append('*');
                     return;
                  case SUPER:
                     this.append('-');
                     this.assembleSig(var6.type);
                     return;
                  default:
                     throw new AssertionError(var6.kind);
               }
            case FORALL:
               Type.ForAll var5 = (Type.ForAll)var1;
               this.assembleParamsSig(var5.tvars);
               this.assembleSig(var5.qtype);
               break;
            case METHOD:
               Type.MethodType var3 = (Type.MethodType)var1;
               this.append('(');
               this.assembleSig(var3.argtypes);
               this.append(')');
               this.assembleSig(var3.restype);
               if (this.hasTypeVar(var3.thrown)) {
                  for(List var4 = var3.thrown; var4.nonEmpty(); var4 = var4.tail) {
                     this.append('^');
                     this.assembleSig((Type)var4.head);
                  }
               }
         }

      }

      public boolean hasTypeVar(List var1) {
         while(var1.nonEmpty()) {
            if (((Type)var1.head).hasTag(TypeTag.TYPEVAR)) {
               return true;
            }

            var1 = var1.tail;
         }

         return false;
      }

      public void assembleClassSig(Type var1) {
         var1 = var1.unannotatedType();
         Type.ClassType var2 = (Type.ClassType)var1;
         Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.tsym;
         this.classReference(var3);
         Type var4 = var2.getEnclosingType();
         if (var4.allparams().nonEmpty()) {
            boolean var5 = var3.owner.kind == 16 || var3.name == this.types.names.empty;
            this.assembleClassSig(var5 ? this.types.erasure(var4) : var4);
            this.append((char)(var5 ? '$' : '.'));
            Assert.check(var3.flatname.startsWith(var3.owner.enclClass().flatname));
            this.append(var5 ? var3.flatname.subName(var3.owner.enclClass().flatname.getByteLength() + 1, var3.flatname.getByteLength()) : var3.name);
         } else {
            this.append(ClassFile.externalize(var3.flatname));
         }

         if (var2.getTypeArguments().nonEmpty()) {
            this.append('<');
            this.assembleSig(var2.getTypeArguments());
            this.append('>');
         }

      }

      public void assembleParamsSig(List var1) {
         this.append('<');

         for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
            Type.TypeVar var3 = (Type.TypeVar)var2.head;
            this.append(var3.tsym.name);
            List var4 = this.types.getBounds(var3);
            if ((((Type)var4.head).tsym.flags() & 512L) != 0L) {
               this.append(':');
            }

            for(List var5 = var4; var5.nonEmpty(); var5 = var5.tail) {
               this.append(':');
               this.assembleSig((Type)var5.head);
            }
         }

         this.append('>');
      }

      private void assembleSig(List var1) {
         for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
            this.assembleSig((Type)var2.head);
         }

      }
   }

   public static class MapVisitor extends DefaultTypeVisitor {
      public final Type visit(Type var1) {
         return (Type)var1.accept((Type.Visitor)this, (Object)null);
      }

      public Type visitType(Type var1, Object var2) {
         return var1;
      }
   }

   public abstract static class UnaryVisitor extends SimpleVisitor {
      public final Object visit(Type var1) {
         return var1.accept((Type.Visitor)this, (Object)null);
      }
   }

   public abstract static class TypeRelation extends SimpleVisitor {
   }

   public abstract static class SimpleVisitor extends DefaultTypeVisitor {
      public Object visitCapturedType(Type.CapturedType var1, Object var2) {
         return this.visitTypeVar(var1, var2);
      }

      public Object visitForAll(Type.ForAll var1, Object var2) {
         return this.visit(var1.qtype, var2);
      }

      public Object visitUndetVar(Type.UndetVar var1, Object var2) {
         return this.visit(var1.qtype, var2);
      }
   }

   public abstract static class DefaultSymbolVisitor implements Symbol.Visitor {
      public final Object visit(Symbol var1, Object var2) {
         return var1.accept(this, var2);
      }

      public Object visitClassSymbol(Symbol.ClassSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }

      public Object visitMethodSymbol(Symbol.MethodSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }

      public Object visitOperatorSymbol(Symbol.OperatorSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }

      public Object visitPackageSymbol(Symbol.PackageSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }

      public Object visitTypeSymbol(Symbol.TypeSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }

      public Object visitVarSymbol(Symbol.VarSymbol var1, Object var2) {
         return this.visitSymbol(var1, var2);
      }
   }

   public abstract static class DefaultTypeVisitor implements Type.Visitor {
      public final Object visit(Type var1, Object var2) {
         return var1.accept((Type.Visitor)this, var2);
      }

      public Object visitClassType(Type.ClassType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitWildcardType(Type.WildcardType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitArrayType(Type.ArrayType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitMethodType(Type.MethodType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitPackageType(Type.PackageType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitTypeVar(Type.TypeVar var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitCapturedType(Type.CapturedType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitForAll(Type.ForAll var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitUndetVar(Type.UndetVar var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitErrorType(Type.ErrorType var1, Object var2) {
         return this.visitType(var1, var2);
      }

      public Object visitAnnotatedType(Type.AnnotatedType var1, Object var2) {
         return this.visit(var1.unannotatedType(), var2);
      }
   }

   public static class UniqueType {
      public final Type type;
      final Types types;

      public UniqueType(Type var1, Types var2) {
         this.type = var1;
         this.types = var2;
      }

      public int hashCode() {
         return this.types.hashCode(this.type);
      }

      public boolean equals(Object var1) {
         return var1 instanceof UniqueType && this.types.isSameAnnotatedType(this.type, ((UniqueType)var1).type);
      }

      public String toString() {
         return this.type.toString();
      }
   }

   class Rewriter extends UnaryVisitor {
      boolean high;
      boolean rewriteTypeVars;

      Rewriter(boolean var2, boolean var3) {
         this.high = var2;
         this.rewriteTypeVars = var3;
      }

      public Type visitClassType(Type.ClassType var1, Void var2) {
         ListBuffer var3 = new ListBuffer();
         boolean var4 = false;

         Type var7;
         for(Iterator var5 = var1.allparams().iterator(); var5.hasNext(); var3.append(var7)) {
            Type var6 = (Type)var5.next();
            var7 = (Type)this.visit(var6);
            if (var6 != var7) {
               var4 = true;
            }
         }

         if (var4) {
            return Types.this.subst(var1.tsym.type, var1.tsym.type.allparams(), var3.toList());
         } else {
            return var1;
         }
      }

      public Type visitType(Type var1, Void var2) {
         return var1;
      }

      public Type visitCapturedType(Type.CapturedType var1, Void var2) {
         Type var3 = var1.wildcard.type;
         Type var4 = var3.contains(var1) ? Types.this.erasure(var3) : (Type)this.visit(var3);
         return this.rewriteAsWildcardType((Type)this.visit(var4), var1.wildcard.bound, var1.wildcard.kind);
      }

      public Type visitTypeVar(Type.TypeVar var1, Void var2) {
         if (this.rewriteTypeVars) {
            Type var3 = var1.bound.contains(var1) ? Types.this.erasure(var1.bound) : (Type)this.visit(var1.bound);
            return this.rewriteAsWildcardType(var3, var1, BoundKind.EXTENDS);
         } else {
            return var1;
         }
      }

      public Type visitWildcardType(Type.WildcardType var1, Void var2) {
         Type var3 = (Type)this.visit(var1.type);
         return (Type)(var1.type == var3 ? var1 : this.rewriteAsWildcardType(var3, var1.bound, var1.kind));
      }

      private Type rewriteAsWildcardType(Type var1, Type.TypeVar var2, BoundKind var3) {
         switch (var3) {
            case EXTENDS:
               return this.high ? Types.this.makeExtendsWildcard(this.B(var1), var2) : Types.this.makeExtendsWildcard(Types.this.syms.objectType, var2);
            case UNBOUND:
               return Types.this.makeExtendsWildcard(Types.this.syms.objectType, var2);
            case SUPER:
               return this.high ? Types.this.makeSuperWildcard(Types.this.syms.botType, var2) : Types.this.makeSuperWildcard(this.B(var1), var2);
            default:
               Assert.error("Invalid bound kind " + var3);
               return null;
         }
      }

      Type B(Type var1) {
         while(var1.hasTag(TypeTag.WILDCARD)) {
            Type.WildcardType var2 = (Type.WildcardType)var1.unannotatedType();
            var1 = this.high ? var2.getExtendsBound() : var2.getSuperBound();
            if (var1 == null) {
               var1 = this.high ? Types.this.syms.objectType : Types.this.syms.botType;
            }
         }

         return var1;
      }
   }

   public static class AdaptFailure extends RuntimeException {
      static final long serialVersionUID = -7490231548272701566L;
   }

   class Adapter extends SimpleVisitor {
      ListBuffer from;
      ListBuffer to;
      Map mapping;
      private Set cache = new HashSet();

      Adapter(ListBuffer var2, ListBuffer var3) {
         this.from = var2;
         this.to = var3;
         this.mapping = new HashMap();
      }

      public void adapt(Type var1, Type var2) throws AdaptFailure {
         this.visit(var1, var2);
         List var3 = this.from.toList();

         for(List var4 = this.to.toList(); !var3.isEmpty(); var4 = var4.tail) {
            Type var5 = (Type)this.mapping.get(((Type)var3.head).tsym);
            if (var4.head != var5) {
               var4.head = var5;
            }

            var3 = var3.tail;
         }

      }

      public Void visitClassType(Type.ClassType var1, Type var2) throws AdaptFailure {
         if (var2.hasTag(TypeTag.CLASS)) {
            this.adaptRecursive(var1.allparams(), var2.allparams());
         }

         return null;
      }

      public Void visitArrayType(Type.ArrayType var1, Type var2) throws AdaptFailure {
         if (var2.hasTag(TypeTag.ARRAY)) {
            this.adaptRecursive(Types.this.elemtype(var1), Types.this.elemtype(var2));
         }

         return null;
      }

      public Void visitWildcardType(Type.WildcardType var1, Type var2) throws AdaptFailure {
         if (var1.isExtendsBound()) {
            this.adaptRecursive(Types.this.wildUpperBound(var1), Types.this.wildUpperBound(var2));
         } else if (var1.isSuperBound()) {
            this.adaptRecursive(Types.this.wildLowerBound(var1), Types.this.wildLowerBound(var2));
         }

         return null;
      }

      public Void visitTypeVar(Type.TypeVar var1, Type var2) throws AdaptFailure {
         Type var3 = (Type)this.mapping.get(var1.tsym);
         if (var3 != null) {
            if (var3.isSuperBound() && var2.isSuperBound()) {
               var3 = Types.this.isSubtype(Types.this.wildLowerBound(var3), Types.this.wildLowerBound(var2)) ? var2 : var3;
            } else if (var3.isExtendsBound() && var2.isExtendsBound()) {
               var3 = Types.this.isSubtype(Types.this.wildUpperBound(var3), Types.this.wildUpperBound(var2)) ? var3 : var2;
            } else if (!Types.this.isSameType(var3, var2)) {
               throw new AdaptFailure();
            }
         } else {
            var3 = var2;
            this.from.append(var1);
            this.to.append(var2);
         }

         this.mapping.put(var1.tsym, var3);
         return null;
      }

      public Void visitType(Type var1, Type var2) {
         return null;
      }

      private void adaptRecursive(Type var1, Type var2) {
         TypePair var3 = Types.this.new TypePair(var1, var2);
         if (this.cache.add(var3)) {
            try {
               this.visit(var1, var2);
            } finally {
               this.cache.remove(var3);
            }
         }

      }

      private void adaptRecursive(List var1, List var2) {
         if (var1.length() == var2.length()) {
            while(var1.nonEmpty()) {
               this.adaptRecursive((Type)var1.head, (Type)var2.head);
               var1 = var1.tail;
               var2 = var2.tail;
            }
         }

      }
   }

   class TypePair {
      final Type t1;
      final Type t2;
      boolean strict;

      TypePair(Type var2, Type var3) {
         this(var2, var3, false);
      }

      TypePair(Type var2, Type var3, boolean var4) {
         this.t1 = var2;
         this.t2 = var3;
         this.strict = var4;
      }

      public int hashCode() {
         return 127 * Types.this.hashCode(this.t1) + Types.this.hashCode(this.t2);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof TypePair)) {
            return false;
         } else {
            TypePair var2 = (TypePair)var1;
            return Types.this.isSameType(this.t1, var2.t1, this.strict) && Types.this.isSameType(this.t2, var2.t2, this.strict);
         }
      }
   }

   private class Subst extends UnaryVisitor {
      List from;
      List to;

      public Subst(List var2, List var3) {
         int var4 = var2.length();

         int var5;
         for(var5 = var3.length(); var4 > var5; var2 = var2.tail) {
            --var4;
         }

         while(var4 < var5) {
            --var5;
            var3 = var3.tail;
         }

         this.from = var2;
         this.to = var3;
      }

      Type subst(Type var1) {
         return this.from.tail == null ? var1 : (Type)this.visit(var1);
      }

      List subst(List var1) {
         if (this.from.tail == null) {
            return var1;
         } else {
            boolean var2 = false;
            if (var1.nonEmpty() && this.from.nonEmpty()) {
               Type var3 = this.subst((Type)var1.head);
               List var4 = this.subst(var1.tail);
               if (var3 != var1.head || var4 != var1.tail) {
                  return var4.prepend(var3);
               }
            }

            return var1;
         }
      }

      public Type visitType(Type var1, Void var2) {
         return var1;
      }

      public Type visitMethodType(Type.MethodType var1, Void var2) {
         List var3 = this.subst(var1.argtypes);
         Type var4 = this.subst(var1.restype);
         List var5 = this.subst(var1.thrown);
         return var3 == var1.argtypes && var4 == var1.restype && var5 == var1.thrown ? var1 : new Type.MethodType(var3, var4, var5, var1.tsym);
      }

      public Type visitTypeVar(Type.TypeVar var1, Void var2) {
         List var3 = this.from;

         for(List var4 = this.to; var3.nonEmpty(); var4 = var4.tail) {
            if (var1 == var3.head) {
               return ((Type)var4.head).withTypeVar(var1);
            }

            var3 = var3.tail;
         }

         return var1;
      }

      public Type visitUndetVar(Type.UndetVar var1, Void var2) {
         return var1;
      }

      public Type visitClassType(Type.ClassType var1, Void var2) {
         List var4;
         if (!var1.isCompound()) {
            List var7 = var1.getTypeArguments();
            var4 = this.subst(var7);
            Type var5 = var1.getEnclosingType();
            Type var6 = this.subst(var5);
            return var4 == var7 && var6 == var5 ? var1 : new Type.ClassType(var6, var4, var1.tsym);
         } else {
            Type var3 = this.subst(Types.this.supertype(var1));
            var4 = this.subst(Types.this.interfaces(var1));
            return (Type)(var3 == Types.this.supertype(var1) && var4 == Types.this.interfaces(var1) ? var1 : Types.this.makeIntersectionType(var4.prepend(var3)));
         }
      }

      public Type visitWildcardType(Type.WildcardType var1, Void var2) {
         Type var3 = var1.type;
         if (var1.kind != BoundKind.UNBOUND) {
            var3 = this.subst(var3);
         }

         if (var3 == var1.type) {
            return var1;
         } else {
            if (var1.isExtendsBound() && var3.isExtendsBound()) {
               var3 = Types.this.wildUpperBound(var3);
            }

            return new Type.WildcardType(var3, var1.kind, Types.this.syms.boundClass, var1.bound);
         }
      }

      public Type visitArrayType(Type.ArrayType var1, Void var2) {
         Type var3 = this.subst(var1.elemtype);
         return var3 == var1.elemtype ? var1 : new Type.ArrayType(var3, var1.tsym);
      }

      public Type visitForAll(Type.ForAll var1, Void var2) {
         List var3;
         if (Type.containsAny(this.to, var1.tvars)) {
            var3 = Types.this.newInstances(var1.tvars);
            var1 = new Type.ForAll(var3, Types.this.subst(var1.qtype, var1.tvars, var3));
         }

         var3 = Types.this.substBounds(var1.tvars, this.from, this.to);
         Type var4 = this.subst(var1.qtype);
         if (var3 == var1.tvars && var4 == var1.qtype) {
            return var1;
         } else {
            return var3 == var1.tvars ? new Type.ForAll(var3, var4) : new Type.ForAll(var3, Types.this.subst(var4, var1.tvars, var3));
         }
      }

      public Type visitErrorType(Type.ErrorType var1, Void var2) {
         return var1;
      }
   }

   private class HasSameArgs extends TypeRelation {
      boolean strict;

      public HasSameArgs(boolean var2) {
         this.strict = var2;
      }

      public Boolean visitType(Type var1, Type var2) {
         throw new AssertionError();
      }

      public Boolean visitMethodType(Type.MethodType var1, Type var2) {
         return var2.hasTag(TypeTag.METHOD) && Types.this.containsTypeEquivalent(var1.argtypes, var2.getParameterTypes());
      }

      public Boolean visitForAll(Type.ForAll var1, Type var2) {
         if (!var2.hasTag(TypeTag.FORALL)) {
            return this.strict ? false : this.visitMethodType(var1.asMethodType(), var2);
         } else {
            Type.ForAll var3 = (Type.ForAll)var2;
            return Types.this.hasSameBounds(var1, var3) && (Boolean)this.visit(var1.qtype, Types.this.subst(var3.qtype, var3.tvars, var1.tvars));
         }
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return false;
      }
   }

   private class MethodFilter implements Filter {
      Symbol msym;
      Type site;

      MethodFilter(Symbol var2, Type var3) {
         this.msym = var2;
         this.site = var3;
      }

      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && var1.name == this.msym.name && (var1.flags() & 4096L) == 0L && var1.isInheritedIn(this.site.tsym, Types.this) && Types.this.overrideEquivalent(Types.this.memberType(this.site, var1), Types.this.memberType(this.site, this.msym));
      }
   }

   class MembersClosureCache extends SimpleVisitor {
      private Map _map = new HashMap();
      Set seenTypes = new HashSet();
      Scope.CompoundScope nilScope;

      public Scope.CompoundScope visitType(Type var1, Void var2) {
         if (this.nilScope == null) {
            this.nilScope = new Scope.CompoundScope(Types.this.syms.noSymbol);
         }

         return this.nilScope;
      }

      public Scope.CompoundScope visitClassType(Type.ClassType var1, Void var2) {
         if (!this.seenTypes.add(var1.tsym)) {
            return new Scope.CompoundScope(var1.tsym);
         } else {
            Scope.CompoundScope var10;
            try {
               this.seenTypes.add(var1.tsym);
               Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var1.tsym;
               Scope.CompoundScope var4 = (Scope.CompoundScope)this._map.get(var3);
               if (var4 == null) {
                  var4 = new Scope.CompoundScope(var3);
                  Iterator var5 = Types.this.interfaces(var1).iterator();

                  while(var5.hasNext()) {
                     Type var6 = (Type)var5.next();
                     var4.addSubScope((Scope)this.visit(var6, (Object)null));
                  }

                  var4.addSubScope((Scope)this.visit(Types.this.supertype(var1), (Object)null));
                  var4.addSubScope(var3.members());
                  this._map.put(var3, var4);
               }

               var10 = var4;
            } finally {
               this.seenTypes.remove(var1.tsym);
            }

            return var10;
         }
      }

      public Scope.CompoundScope visitTypeVar(Type.TypeVar var1, Void var2) {
         return (Scope.CompoundScope)this.visit(var1.getUpperBound(), (Object)null);
      }

      class MembersScope extends Scope.CompoundScope {
         Scope.CompoundScope scope;

         public MembersScope(Scope.CompoundScope var2) {
            super(var2.owner);
            this.scope = var2;
         }

         Filter combine(final Filter var1) {
            return new Filter() {
               public boolean accepts(Symbol var1x) {
                  return !var1x.owner.isInterface() && (var1 == null || var1.accepts(var1x));
               }
            };
         }

         public Iterable getElements(Filter var1) {
            return this.scope.getElements(this.combine(var1));
         }

         public Iterable getElementsByName(Name var1, Filter var2) {
            return this.scope.getElementsByName(var1, this.combine(var2));
         }

         public int getMark() {
            return this.scope.getMark();
         }
      }
   }

   class ImplementationCache {
      private WeakHashMap _map = new WeakHashMap();

      Symbol.MethodSymbol get(Symbol.MethodSymbol var1, Symbol.TypeSymbol var2, boolean var3, Filter var4) {
         SoftReference var5 = (SoftReference)this._map.get(var1);
         Object var6 = var5 != null ? (Map)var5.get() : null;
         if (var6 == null) {
            var6 = new HashMap();
            this._map.put(var1, new SoftReference(var6));
         }

         Entry var7 = (Entry)((Map)var6).get(var2);
         Scope.CompoundScope var8 = Types.this.membersClosure(var2.type, true);
         if (var7 != null && var7.matches(var4, var3, var8.getMark())) {
            return var7.cachedImpl;
         } else {
            Symbol.MethodSymbol var9 = this.implementationInternal(var1, var2, var3, var4);
            ((Map)var6).put(var2, new Entry(var9, var4, var3, var8.getMark()));
            return var9;
         }
      }

      private Symbol.MethodSymbol implementationInternal(Symbol.MethodSymbol var1, Symbol.TypeSymbol var2, boolean var3, Filter var4) {
         for(Type var5 = var2.type; var5.hasTag(TypeTag.CLASS) || var5.hasTag(TypeTag.TYPEVAR); var5 = Types.this.supertype(var5)) {
            while(var5.hasTag(TypeTag.TYPEVAR)) {
               var5 = var5.getUpperBound();
            }

            Symbol.TypeSymbol var6 = var5.tsym;

            for(Scope.Entry var7 = var6.members().lookup(var1.name, var4); var7.scope != null; var7 = var7.next(var4)) {
               if (var7.sym != null && var7.sym.overrides(var1, var2, Types.this, var3)) {
                  return (Symbol.MethodSymbol)var7.sym;
               }
            }
         }

         return null;
      }

      class Entry {
         final Symbol.MethodSymbol cachedImpl;
         final Filter implFilter;
         final boolean checkResult;
         final int prevMark;

         public Entry(Symbol.MethodSymbol var2, Filter var3, boolean var4, int var5) {
            this.cachedImpl = var2;
            this.implFilter = var3;
            this.checkResult = var4;
            this.prevMark = var5;
         }

         boolean matches(Filter var1, boolean var2, int var3) {
            return this.implFilter == var1 && this.checkResult == var2 && this.prevMark == var3;
         }
      }
   }

   private class LooseSameTypeVisitor extends SameTypeVisitor {
      private Set cache;

      private LooseSameTypeVisitor() {
         super();
         this.cache = new HashSet();
      }

      boolean sameTypeVars(Type.TypeVar var1, Type.TypeVar var2) {
         return var1.tsym == var2.tsym && this.checkSameBounds(var1, var2);
      }

      protected boolean containsTypes(List var1, List var2) {
         return Types.this.containsTypeEquivalent(var1, var2);
      }

      private boolean checkSameBounds(Type.TypeVar var1, Type.TypeVar var2) {
         TypePair var3 = Types.this.new TypePair(var1, var2, true);
         if (this.cache.add(var3)) {
            boolean var4;
            try {
               var4 = (Boolean)this.visit(var1.getUpperBound(), var2.getUpperBound());
            } finally {
               this.cache.remove(var3);
            }

            return var4;
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      LooseSameTypeVisitor(Object var2) {
         this();
      }
   }

   abstract class SameTypeVisitor extends TypeRelation {
      public Boolean visitType(Type var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else if (var2.isPartial()) {
            return (Boolean)this.visit(var2, var1);
         } else {
            switch (var1.getTag()) {
               case BYTE:
               case CHAR:
               case SHORT:
               case INT:
               case LONG:
               case FLOAT:
               case DOUBLE:
               case BOOLEAN:
               case VOID:
               case BOT:
               case NONE:
                  return var1.hasTag(var2.getTag());
               case TYPEVAR:
                  if (var2.hasTag(TypeTag.TYPEVAR)) {
                     return this.sameTypeVars((Type.TypeVar)var1.unannotatedType(), (Type.TypeVar)var2.unannotatedType());
                  }

                  return var2.isSuperBound() && !var2.isExtendsBound() && (Boolean)this.visit(var1, Types.this.wildUpperBound(var2));
               case WILDCARD:
               default:
                  throw new AssertionError("isSameType " + var1.getTag());
            }
         }
      }

      abstract boolean sameTypeVars(Type.TypeVar var1, Type.TypeVar var2);

      public Boolean visitWildcardType(Type.WildcardType var1, Type var2) {
         return var2.isPartial() ? (Boolean)this.visit(var2, var1) : false;
      }

      public Boolean visitClassType(Type.ClassType var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else if (var2.isPartial()) {
            return (Boolean)this.visit(var2, var1);
         } else if (var2.isSuperBound() && !var2.isExtendsBound()) {
            return (Boolean)this.visit(var1, Types.this.wildUpperBound(var2)) && (Boolean)this.visit(var1, Types.this.wildLowerBound(var2));
         } else if (var1.isCompound() && var2.isCompound()) {
            if (!(Boolean)this.visit(Types.this.supertype(var1), Types.this.supertype(var2))) {
               return false;
            } else {
               HashSet var3 = new HashSet();
               Iterator var4 = Types.this.interfaces(var1).iterator();

               Type var5;
               while(var4.hasNext()) {
                  var5 = (Type)var4.next();
                  var3.add(new UniqueType(var5.unannotatedType(), Types.this));
               }

               var4 = Types.this.interfaces(var2).iterator();

               do {
                  if (!var4.hasNext()) {
                     return var3.isEmpty();
                  }

                  var5 = (Type)var4.next();
               } while(var3.remove(new UniqueType(var5.unannotatedType(), Types.this)));

               return false;
            }
         } else {
            return var1.tsym == var2.tsym && (Boolean)this.visit(var1.getEnclosingType(), var2.getEnclosingType()) && this.containsTypes(var1.getTypeArguments(), var2.getTypeArguments());
         }
      }

      protected abstract boolean containsTypes(List var1, List var2);

      public Boolean visitArrayType(Type.ArrayType var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else {
            return var2.isPartial() ? (Boolean)this.visit(var2, var1) : var2.hasTag(TypeTag.ARRAY) && Types.this.containsTypeEquivalent(var1.elemtype, Types.this.elemtype(var2));
         }
      }

      public Boolean visitMethodType(Type.MethodType var1, Type var2) {
         return Types.this.hasSameArgs(var1, var2) && (Boolean)this.visit(var1.getReturnType(), var2.getReturnType());
      }

      public Boolean visitPackageType(Type.PackageType var1, Type var2) {
         return var1 == var2;
      }

      public Boolean visitForAll(Type.ForAll var1, Type var2) {
         if (!var2.hasTag(TypeTag.FORALL)) {
            return false;
         } else {
            Type.ForAll var3 = (Type.ForAll)var2;
            return Types.this.hasSameBounds(var1, var3) && (Boolean)this.visit(var1.qtype, Types.this.subst(var3.qtype, var3.tvars, var1.tvars));
         }
      }

      public Boolean visitUndetVar(Type.UndetVar var1, Type var2) {
         if (var2.hasTag(TypeTag.WILDCARD)) {
            return false;
         } else if (var1 != var2 && var1.qtype != var2 && !var2.hasTag(TypeTag.ERROR) && !var2.hasTag(TypeTag.UNKNOWN)) {
            var1.addBound(Type.UndetVar.InferenceBound.EQ, var2, Types.this);
            return true;
         } else {
            return true;
         }
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return true;
      }
   }

   class DescriptorFilter implements Filter {
      Symbol.TypeSymbol origin;

      DescriptorFilter(Symbol.TypeSymbol var2) {
         this.origin = var2;
      }

      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && (var1.flags() & 8796093023232L) == 1024L && !Types.this.overridesObjectMethod(this.origin, var1) && (((Symbol.MethodSymbol)Types.this.interfaceCandidates(this.origin.type, (Symbol.MethodSymbol)var1).head).flags() & 8796093022208L) == 0L;
      }
   }

   class DescriptorCache {
      private WeakHashMap _map = new WeakHashMap();

      FunctionDescriptor get(Symbol.TypeSymbol var1) throws FunctionDescriptorLookupError {
         Entry var2 = (Entry)this._map.get(var1);
         Scope.CompoundScope var3 = Types.this.membersClosure(var1.type, false);
         if (var2 != null && var2.matches(var3.getMark())) {
            return var2.cachedDescRes;
         } else {
            FunctionDescriptor var4 = this.findDescriptorInternal(var1, var3);
            this._map.put(var1, new Entry(var4, var3.getMark()));
            return var4;
         }
      }

      public FunctionDescriptor findDescriptorInternal(Symbol.TypeSymbol var1, Scope.CompoundScope var2) throws FunctionDescriptorLookupError {
         if (var1.isInterface() && (var1.flags() & 8192L) == 0L) {
            ListBuffer var3 = new ListBuffer();
            Iterator var4 = var2.getElements(Types.this.new DescriptorFilter(var1)).iterator();

            while(var4.hasNext()) {
               Symbol var5 = (Symbol)var4.next();
               Type var6 = Types.this.memberType(var1.type, var5);
               if (!var3.isEmpty() && (var5.name != ((Symbol)var3.first()).name || !Types.this.overrideEquivalent(var6, Types.this.memberType(var1.type, (Symbol)var3.first())))) {
                  throw this.failure("not.a.functional.intf.1", var1, Types.this.diags.fragment("incompatible.abstracts", Kinds.kindName((Symbol)var1), var1));
               }

               var3.append(var5);
            }

            if (var3.isEmpty()) {
               throw this.failure("not.a.functional.intf.1", var1, Types.this.diags.fragment("no.abstracts", Kinds.kindName((Symbol)var1), var1));
            } else if (var3.size() == 1) {
               return new FunctionDescriptor((Symbol)var3.first());
            } else {
               FunctionDescriptor var9 = this.mergeDescriptors(var1, var3.toList());
               if (var9 == null) {
                  ListBuffer var10 = new ListBuffer();
                  Iterator var11 = var3.iterator();

                  while(var11.hasNext()) {
                     Symbol var7 = (Symbol)var11.next();
                     String var8 = var7.type.getThrownTypes().nonEmpty() ? "descriptor.throws" : "descriptor";
                     var10.append(Types.this.diags.fragment(var8, var7.name, var7.type.getParameterTypes(), var7.type.getReturnType(), var7.type.getThrownTypes()));
                  }

                  JCDiagnostic.MultilineDiagnostic var12 = new JCDiagnostic.MultilineDiagnostic(Types.this.diags.fragment("incompatible.descs.in.functional.intf", Kinds.kindName((Symbol)var1), var1), var10.toList());
                  throw this.failure(var12);
               } else {
                  return var9;
               }
            }
         } else {
            throw this.failure("not.a.functional.intf", var1);
         }
      }

      private FunctionDescriptor mergeDescriptors(Symbol.TypeSymbol var1, List var2) {
         List var3 = List.nil();
         Iterator var4 = var2.iterator();

         while(true) {
            Symbol var5;
            label89:
            while(var4.hasNext()) {
               var5 = (Symbol)var4.next();
               Type var6 = Types.this.memberType(var1.type, var5);
               Iterator var7 = var2.iterator();

               while(var7.hasNext()) {
                  Symbol var8 = (Symbol)var7.next();
                  Type var9 = Types.this.memberType(var1.type, var8);
                  if (!Types.this.isSubSignature(var6, var9)) {
                     continue label89;
                  }
               }

               var3 = var3.prepend(var5);
            }

            if (var3.isEmpty()) {
               return null;
            }

            boolean var15 = false;
            var5 = null;

            Symbol var10;
            Type var11;
            Type var20;
            Iterator var21;
            label78:
            while(var5 == null) {
               Iterator var16 = var3.iterator();

               while(true) {
                  label74:
                  while(var16.hasNext()) {
                     Symbol var18 = (Symbol)var16.next();
                     var20 = Types.this.memberType(var1.type, var18);
                     var21 = var2.iterator();

                     while(var21.hasNext()) {
                        var10 = (Symbol)var21.next();
                        var11 = Types.this.memberType(var1.type, var10);
                        if (var15) {
                           if (!Types.this.returnTypeSubstitutable(var20, var11)) {
                              continue label74;
                           }
                        } else if (!this.isSubtypeInternal(var20.getReturnType(), var11.getReturnType())) {
                           continue label74;
                        }
                     }

                     var5 = var18;
                  }

                  if (var15) {
                     break label78;
                  }

                  var15 = true;
                  break;
               }
            }

            if (var5 == null) {
               return null;
            }

            boolean var17 = !var5.type.hasTag(TypeTag.FORALL);
            final List var19 = null;
            var20 = Types.this.memberType(var1.type, var5);

            List var12;
            for(var21 = var2.iterator(); var21.hasNext(); var19 = var19 == null ? var12 : Types.this.chk.intersect(var12, var19)) {
               var10 = (Symbol)var21.next();
               var11 = Types.this.memberType(var1.type, var10);
               var12 = var11.getThrownTypes();
               if (var17) {
                  var12 = Types.this.erasure(var12);
               } else {
                  Type.ForAll var13 = (Type.ForAll)var20;
                  Type.ForAll var14 = (Type.ForAll)var11;
                  var12 = Types.this.subst(var12, var14.tvars, var13.tvars);
               }
            }

            return new FunctionDescriptor(var5) {
               public Type getType(Type var1) {
                  Type var2 = Types.this.memberType(var1, this.getSymbol());
                  return Types.this.createMethodTypeWithThrown(var2, var19);
               }
            };
         }
      }

      boolean isSubtypeInternal(Type var1, Type var2) {
         return var1.isPrimitive() && var2.isPrimitive() ? Types.this.isSameType(var2, var1) : Types.this.isSubtype(var1, var2);
      }

      FunctionDescriptorLookupError failure(String var1, Object... var2) {
         return this.failure(Types.this.diags.fragment(var1, var2));
      }

      FunctionDescriptorLookupError failure(JCDiagnostic var1) {
         return Types.this.functionDescriptorLookupError.setMessage(var1);
      }

      class Entry {
         final FunctionDescriptor cachedDescRes;
         final int prevMark;

         public Entry(FunctionDescriptor var2, int var3) {
            this.cachedDescRes = var2;
            this.prevMark = var3;
         }

         boolean matches(int var1) {
            return this.prevMark == var1;
         }
      }

      class FunctionDescriptor {
         Symbol descSym;

         FunctionDescriptor(Symbol var2) {
            this.descSym = var2;
         }

         public Symbol getSymbol() {
            return this.descSym;
         }

         public Type getType(Type var1) {
            var1 = Types.this.removeWildcards(var1);
            if (!Types.this.chk.checkValidGenericType(var1)) {
               throw DescriptorCache.this.failure(Types.this.diags.fragment("no.suitable.functional.intf.inst", var1));
            } else {
               return Types.this.memberType(var1, this.descSym);
            }
         }
      }
   }

   public static class FunctionDescriptorLookupError extends RuntimeException {
      private static final long serialVersionUID = 0L;
      JCDiagnostic diagnostic = null;

      FunctionDescriptorLookupError() {
      }

      FunctionDescriptorLookupError setMessage(JCDiagnostic var1) {
         this.diagnostic = var1;
         return this;
      }

      public JCDiagnostic getDiagnostic() {
         return this.diagnostic;
      }
   }
}
