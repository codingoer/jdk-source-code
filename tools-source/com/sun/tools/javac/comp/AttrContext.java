package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

public class AttrContext {
   Scope scope = null;
   int staticLevel = 0;
   boolean isSelfCall = false;
   boolean selectSuper = false;
   boolean isSerializable = false;
   Resolve.MethodResolutionPhase pendingResolutionPhase = null;
   Lint lint;
   Symbol enclVar = null;
   Attr.ResultInfo returnResult = null;
   Type defaultSuperCallSite = null;
   JCTree preferredTreeForDiagnostics;

   AttrContext dup(Scope var1) {
      AttrContext var2 = new AttrContext();
      var2.scope = var1;
      var2.staticLevel = this.staticLevel;
      var2.isSelfCall = this.isSelfCall;
      var2.selectSuper = this.selectSuper;
      var2.pendingResolutionPhase = this.pendingResolutionPhase;
      var2.lint = this.lint;
      var2.enclVar = this.enclVar;
      var2.returnResult = this.returnResult;
      var2.defaultSuperCallSite = this.defaultSuperCallSite;
      var2.isSerializable = this.isSerializable;
      var2.preferredTreeForDiagnostics = this.preferredTreeForDiagnostics;
      return var2;
   }

   AttrContext dup() {
      return this.dup(this.scope);
   }

   public Iterable getLocalElements() {
      return (Iterable)(this.scope == null ? List.nil() : this.scope.getElements());
   }

   boolean lastResolveVarargs() {
      return this.pendingResolutionPhase != null && this.pendingResolutionPhase.isVarargsRequired();
   }

   public String toString() {
      return "AttrContext[" + this.scope.toString() + "]";
   }
}
