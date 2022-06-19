package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.TypeVariable;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.lang.reflect.Modifier;
import java.text.CollationKey;
import java.util.Iterator;

public abstract class ExecutableMemberDocImpl extends MemberDocImpl implements ExecutableMemberDoc {
   protected final Symbol.MethodSymbol sym;

   public ExecutableMemberDocImpl(DocEnv var1, Symbol.MethodSymbol var2, TreePath var3) {
      super(var1, var2, var3);
      this.sym = var2;
   }

   public ExecutableMemberDocImpl(DocEnv var1, Symbol.MethodSymbol var2) {
      this(var1, var2, (TreePath)null);
   }

   protected long getFlags() {
      return this.sym.flags();
   }

   protected Symbol.ClassSymbol getContainingClass() {
      return this.sym.enclClass();
   }

   public boolean isNative() {
      return Modifier.isNative(this.getModifiers());
   }

   public boolean isSynchronized() {
      return Modifier.isSynchronized(this.getModifiers());
   }

   public boolean isVarArgs() {
      return (this.sym.flags() & 17179869184L) != 0L && !this.env.legacyDoclet;
   }

   public boolean isSynthetic() {
      return (this.sym.flags() & 4096L) != 0L;
   }

   public boolean isIncluded() {
      return this.containingClass().isIncluded() && this.env.shouldDocument(this.sym);
   }

   public ThrowsTag[] throwsTags() {
      return this.comment().throwsTags();
   }

   public ParamTag[] paramTags() {
      return this.comment().paramTags();
   }

   public ParamTag[] typeParamTags() {
      return this.env.legacyDoclet ? new ParamTag[0] : this.comment().typeParamTags();
   }

   public ClassDoc[] thrownExceptions() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.sym.type.getThrownTypes().iterator();

      while(var2.hasNext()) {
         Type var3 = (Type)var2.next();
         var3 = this.env.types.erasure(var3);
         ClassDocImpl var4 = this.env.getClassDoc((Symbol.ClassSymbol)var3.tsym);
         if (var4 != null) {
            var1.append(var4);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public com.sun.javadoc.Type[] thrownExceptionTypes() {
      return TypeMaker.getTypes(this.env, this.sym.type.getThrownTypes());
   }

   public Parameter[] parameters() {
      List var1 = this.sym.params();
      Parameter[] var2 = new Parameter[var1.length()];
      int var3 = 0;

      Symbol.VarSymbol var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = new ParameterImpl(this.env, var5)) {
         var5 = (Symbol.VarSymbol)var4.next();
      }

      return var2;
   }

   public com.sun.javadoc.Type receiverType() {
      Type var1 = this.sym.type.asMethodType().recvtype;
      return var1 != null ? TypeMaker.getType(this.env, var1, false, true) : null;
   }

   public TypeVariable[] typeParameters() {
      if (this.env.legacyDoclet) {
         return new TypeVariable[0];
      } else {
         TypeVariable[] var1 = new TypeVariable[this.sym.type.getTypeArguments().length()];
         TypeMaker.getTypes(this.env, this.sym.type.getTypeArguments(), var1);
         return var1;
      }
   }

   public String signature() {
      return this.makeSignature(true);
   }

   public String flatSignature() {
      return this.makeSignature(false);
   }

   private String makeSignature(boolean var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("(");
      List var3 = this.sym.type.getParameterTypes();

      while(var3.nonEmpty()) {
         Type var4 = (Type)var3.head;
         var2.append(TypeMaker.getTypeString(this.env, var4, var1));
         var3 = var3.tail;
         if (var3.nonEmpty()) {
            var2.append(", ");
         }
      }

      if (this.isVarArgs()) {
         int var5 = var2.length();
         var2.replace(var5 - 2, var5, "...");
      }

      var2.append(")");
      return var2.toString();
   }

   protected String typeParametersString() {
      return TypeMaker.typeParametersString(this.env, this.sym, true);
   }

   CollationKey generateKey() {
      String var1 = this.name() + this.flatSignature() + this.typeParametersString();
      var1 = var1.replace(',', ' ').replace('&', ' ');
      return this.env.doclocale.collator.getCollationKey(var1);
   }

   public SourcePosition position() {
      return this.sym.enclClass().sourcefile == null ? null : SourcePositionImpl.make(this.sym.enclClass().sourcefile, this.tree == null ? 0 : this.tree.pos, this.lineMap);
   }
}
