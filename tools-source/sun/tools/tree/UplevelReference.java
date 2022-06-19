package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;

public class UplevelReference implements Constants {
   ClassDefinition client;
   LocalMember target;
   LocalMember localArgument;
   MemberDefinition localField;
   UplevelReference next;

   public UplevelReference(ClassDefinition var1, LocalMember var2) {
      this.client = var1;
      this.target = var2;
      Identifier var3;
      int var5;
      if (var2.getName().equals(idThis)) {
         ClassDefinition var4 = var2.getClassDefinition();
         var5 = 0;

         for(ClassDefinition var6 = var4; !var6.isTopLevel(); var6 = var6.getOuterClass()) {
            ++var5;
         }

         var3 = Identifier.lookup("this$" + var5);
      } else {
         var3 = Identifier.lookup("val$" + var2.getName());
      }

      Identifier var8 = var3;
      var5 = 0;

      while(true) {
         boolean var9 = var1.getFirstMatch(var3) != null;

         for(UplevelReference var7 = var1.getReferences(); var7 != null; var7 = var7.next) {
            if (var7.target.getName().equals(var3)) {
               var9 = true;
            }
         }

         if (!var9) {
            this.localArgument = new LocalMember(var2.getWhere(), var1, 524304, var2.getType(), var3);
            return;
         }

         StringBuilder var10000 = (new StringBuilder()).append(var8).append("$");
         ++var5;
         var3 = Identifier.lookup(var10000.append(var5).toString());
      }
   }

   public UplevelReference insertInto(UplevelReference var1) {
      if (var1 != null && !this.isEarlierThan(var1)) {
         UplevelReference var2;
         for(var2 = var1; var2.next != null && !this.isEarlierThan(var2.next); var2 = var2.next) {
         }

         this.next = var2.next;
         var2.next = this;
         return var1;
      } else {
         this.next = var1;
         return this;
      }
   }

   public final boolean isEarlierThan(UplevelReference var1) {
      if (this.isClientOuterField()) {
         return true;
      } else if (var1.isClientOuterField()) {
         return false;
      } else {
         LocalMember var2 = var1.target;
         Identifier var3 = this.target.getName();
         Identifier var4 = var2.getName();
         int var5 = var3.toString().compareTo(var4.toString());
         if (var5 != 0) {
            return var5 < 0;
         } else {
            Identifier var6 = this.target.getClassDefinition().getName();
            Identifier var7 = var2.getClassDefinition().getName();
            int var8 = var6.toString().compareTo(var7.toString());
            return var8 < 0;
         }
      }
   }

   public final LocalMember getTarget() {
      return this.target;
   }

   public final LocalMember getLocalArgument() {
      return this.localArgument;
   }

   public final MemberDefinition getLocalField() {
      return this.localField;
   }

   public final MemberDefinition getLocalField(Environment var1) {
      if (this.localField == null) {
         this.makeLocalField(var1);
      }

      return this.localField;
   }

   public final ClassDefinition getClient() {
      return this.client;
   }

   public final UplevelReference getNext() {
      return this.next;
   }

   public boolean isClientOuterField() {
      MemberDefinition var1 = this.client.findOuterMember();
      return var1 != null && this.localField == var1;
   }

   public boolean localArgumentAvailable(Environment var1, Context var2) {
      MemberDefinition var3 = var2.field;
      if (var3.getClassDefinition() != this.client) {
         throw new CompilerError("localArgumentAvailable");
      } else {
         return var3.isConstructor() || var3.isVariable() || var3.isInitializer();
      }
   }

   public void noteReference(Environment var1, Context var2) {
      if (this.localField == null && !this.localArgumentAvailable(var1, var2)) {
         this.makeLocalField(var1);
      }

   }

   private void makeLocalField(Environment var1) {
      this.client.referencesMustNotBeFrozen();
      int var2 = 524306;
      this.localField = var1.makeMemberDefinition(var1, this.localArgument.getWhere(), this.client, (String)null, var2, this.localArgument.getType(), this.localArgument.getName(), (IdentifierToken[])null, (IdentifierToken[])null, (Object)null);
   }

   public Expression makeLocalReference(Environment var1, Context var2) {
      if (var2.field.getClassDefinition() != this.client) {
         throw new CompilerError("makeLocalReference");
      } else {
         return (Expression)(this.localArgumentAvailable(var1, var2) ? new IdentifierExpression(0L, this.localArgument) : this.makeFieldReference(var1, var2));
      }
   }

   public Expression makeFieldReference(Environment var1, Context var2) {
      Expression var3 = var2.findOuterLink(var1, 0L, this.localField);
      return new FieldExpression(0L, var3, this.localField);
   }

   public void willCodeArguments(Environment var1, Context var2) {
      if (!this.isClientOuterField()) {
         var2.noteReference(var1, this.target);
      }

      if (this.next != null) {
         this.next.willCodeArguments(var1, var2);
      }

   }

   public void codeArguments(Environment var1, Context var2, Assembler var3, long var4, MemberDefinition var6) {
      if (!this.isClientOuterField()) {
         Expression var7 = var2.makeReference(var1, this.target);
         var7.codeValue(var1, var2, var3);
      }

      if (this.next != null) {
         this.next.codeArguments(var1, var2, var3, var4, var6);
      }

   }

   public void codeInitialization(Environment var1, Context var2, Assembler var3, long var4, MemberDefinition var6) {
      if (this.localField != null && !this.isClientOuterField()) {
         Expression var7 = var2.makeReference(var1, this.target);
         Expression var8 = this.makeFieldReference(var1, var2);
         AssignExpression var9 = new AssignExpression(var7.getWhere(), var8, var7);
         var9.type = this.localField.getType();
         var9.code(var1, var2, var3);
      }

      if (this.next != null) {
         this.next.codeInitialization(var1, var2, var3, var4, var6);
      }

   }

   public String toString() {
      return "[" + this.localArgument + " in " + this.client + "]";
   }
}
