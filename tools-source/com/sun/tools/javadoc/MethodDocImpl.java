package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import java.lang.reflect.Modifier;

public class MethodDocImpl extends ExecutableMemberDocImpl implements MethodDoc {
   private String name;
   private String qualifiedName;

   public MethodDocImpl(DocEnv var1, Symbol.MethodSymbol var2) {
      super(var1, var2);
   }

   public MethodDocImpl(DocEnv var1, Symbol.MethodSymbol var2, TreePath var3) {
      super(var1, var2, var3);
   }

   public boolean isMethod() {
      return true;
   }

   public boolean isDefault() {
      return (this.sym.flags() & 8796093022208L) != 0L;
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this.getModifiers()) && !this.isDefault();
   }

   public Type returnType() {
      return TypeMaker.getType(this.env, this.sym.type.getReturnType(), false);
   }

   public ClassDoc overriddenClass() {
      Type var1 = this.overriddenType();
      return var1 != null ? var1.asClassDoc() : null;
   }

   public Type overriddenType() {
      if ((this.sym.flags() & 8L) != 0L) {
         return null;
      } else {
         Symbol.ClassSymbol var1 = (Symbol.ClassSymbol)this.sym.owner;

         for(com.sun.tools.javac.code.Type var2 = this.env.types.supertype(var1.type); var2.hasTag(TypeTag.CLASS); var2 = this.env.types.supertype(var2)) {
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.tsym;

            for(Scope.Entry var4 = this.membersOf(var3).lookup(this.sym.name); var4.scope != null; var4 = var4.next()) {
               if (this.sym.overrides(var4.sym, var1, this.env.types, true)) {
                  return TypeMaker.getType(this.env, var2);
               }
            }
         }

         return null;
      }
   }

   public MethodDoc overriddenMethod() {
      if ((this.sym.flags() & 8L) != 0L) {
         return null;
      } else {
         Symbol.ClassSymbol var1 = (Symbol.ClassSymbol)this.sym.owner;

         for(com.sun.tools.javac.code.Type var2 = this.env.types.supertype(var1.type); var2.hasTag(TypeTag.CLASS); var2 = this.env.types.supertype(var2)) {
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.tsym;

            for(Scope.Entry var4 = this.membersOf(var3).lookup(this.sym.name); var4.scope != null; var4 = var4.next()) {
               if (this.sym.overrides(var4.sym, var1, this.env.types, true)) {
                  return this.env.getMethodDoc((Symbol.MethodSymbol)var4.sym);
               }
            }
         }

         return null;
      }
   }

   private Scope membersOf(Symbol.ClassSymbol var1) {
      try {
         return var1.members();
      } catch (Symbol.CompletionFailure var3) {
         return this.membersOf(var1);
      }
   }

   public boolean overrides(MethodDoc var1) {
      Symbol.MethodSymbol var2 = ((MethodDocImpl)var1).sym;
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)this.sym.owner;
      return this.sym.name == var2.name && this.sym != var2 && !this.sym.isStatic() && this.env.types.asSuper(var3.type, var2.owner) != null && this.sym.overrides(var2, var3, this.env.types, false);
   }

   public String name() {
      if (this.name == null) {
         this.name = this.sym.name.toString();
      }

      return this.name;
   }

   public String qualifiedName() {
      if (this.qualifiedName == null) {
         this.qualifiedName = this.sym.enclClass().getQualifiedName() + "." + this.sym.name;
      }

      return this.qualifiedName;
   }

   public String toString() {
      return this.sym.enclClass().getQualifiedName() + "." + this.typeParametersString() + this.name() + this.signature();
   }
}
