package com.sun.tools.javac.api;

import com.sun.source.tree.Scope;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class JavacScope implements Scope {
   protected final Env env;

   JavacScope(Env var1) {
      var1.getClass();
      this.env = var1;
   }

   public JavacScope getEnclosingScope() {
      return this.env.outer != null && this.env.outer != this.env ? new JavacScope(this.env.outer) : new JavacScope(this.env) {
         public boolean isStarImportScope() {
            return true;
         }

         public JavacScope getEnclosingScope() {
            return null;
         }

         public Iterable getLocalElements() {
            return this.env.toplevel.starImportScope.getElements();
         }
      };
   }

   public TypeElement getEnclosingClass() {
      return this.env.outer != null && this.env.outer != this.env ? this.env.enclClass.sym : null;
   }

   public ExecutableElement getEnclosingMethod() {
      return this.env.enclMethod == null ? null : this.env.enclMethod.sym;
   }

   public Iterable getLocalElements() {
      return ((AttrContext)this.env.info).getLocalElements();
   }

   public Env getEnv() {
      return this.env;
   }

   public boolean isStarImportScope() {
      return false;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JavacScope)) {
         return false;
      } else {
         JavacScope var2 = (JavacScope)var1;
         return this.env.equals(var2.env) && this.isStarImportScope() == var2.isStarImportScope();
      }
   }

   public int hashCode() {
      return this.env.hashCode() + (this.isStarImportScope() ? 1 : 0);
   }

   public String toString() {
      return "JavacScope[env=" + this.env + ",starImport=" + this.isStarImportScope() + "]";
   }
}
