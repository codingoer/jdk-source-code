package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Position;
import java.lang.reflect.Modifier;
import java.text.CollationKey;
import java.util.Iterator;

public abstract class ProgramElementDocImpl extends DocImpl implements ProgramElementDoc {
   private final Symbol sym;
   JCTree tree = null;
   Position.LineMap lineMap = null;
   private int modifiers = -1;

   protected ProgramElementDocImpl(DocEnv var1, Symbol var2, TreePath var3) {
      super(var1, var3);
      this.sym = var2;
      if (var3 != null) {
         this.tree = (JCTree)var3.getLeaf();
         this.lineMap = ((JCTree.JCCompilationUnit)var3.getCompilationUnit()).lineMap;
      }

   }

   void setTreePath(TreePath var1) {
      super.setTreePath(var1);
      this.tree = (JCTree)var1.getLeaf();
      this.lineMap = ((JCTree.JCCompilationUnit)var1.getCompilationUnit()).lineMap;
   }

   protected abstract Symbol.ClassSymbol getContainingClass();

   protected abstract long getFlags();

   protected int getModifiers() {
      if (this.modifiers == -1) {
         this.modifiers = DocEnv.translateModifiers(this.getFlags());
      }

      return this.modifiers;
   }

   public ClassDoc containingClass() {
      return this.getContainingClass() == null ? null : this.env.getClassDoc(this.getContainingClass());
   }

   public PackageDoc containingPackage() {
      return this.env.getPackageDoc(this.getContainingClass().packge());
   }

   public int modifierSpecifier() {
      int var1 = this.getModifiers();
      return this.isMethod() && this.containingClass().isInterface() ? var1 & -1025 : var1;
   }

   public String modifiers() {
      int var1 = this.getModifiers();
      return !this.isAnnotationTypeElement() && (!this.isMethod() || !this.containingClass().isInterface()) ? Modifier.toString(var1) : Modifier.toString(var1 & -1025);
   }

   public AnnotationDesc[] annotations() {
      AnnotationDesc[] var1 = new AnnotationDesc[this.sym.getRawAttributes().length()];
      int var2 = 0;

      Attribute.Compound var4;
      for(Iterator var3 = this.sym.getRawAttributes().iterator(); var3.hasNext(); var1[var2++] = new AnnotationDescImpl(this.env, var4)) {
         var4 = (Attribute.Compound)var3.next();
      }

      return var1;
   }

   public boolean isPublic() {
      int var1 = this.getModifiers();
      return Modifier.isPublic(var1);
   }

   public boolean isProtected() {
      int var1 = this.getModifiers();
      return Modifier.isProtected(var1);
   }

   public boolean isPrivate() {
      int var1 = this.getModifiers();
      return Modifier.isPrivate(var1);
   }

   public boolean isPackagePrivate() {
      return !this.isPublic() && !this.isPrivate() && !this.isProtected();
   }

   public boolean isStatic() {
      int var1 = this.getModifiers();
      return Modifier.isStatic(var1);
   }

   public boolean isFinal() {
      int var1 = this.getModifiers();
      return Modifier.isFinal(var1);
   }

   CollationKey generateKey() {
      String var1 = this.name();
      return this.env.doclocale.collator.getCollationKey(var1);
   }
}
