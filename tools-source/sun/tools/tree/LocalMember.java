package sun.tools.tree;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class LocalMember extends MemberDefinition {
   int number;
   int readcount;
   int writecount;
   int scopeNumber;
   LocalMember originalOfCopy;
   LocalMember prev;

   public int getScopeNumber() {
      return this.scopeNumber;
   }

   public LocalMember(long var1, ClassDefinition var3, int var4, Type var5, Identifier var6) {
      super(var1, var3, var4, var5, var6, (IdentifierToken[])null, (Node)null);
      this.number = -1;
   }

   public LocalMember(ClassDefinition var1) {
      super(var1);
      this.number = -1;
      this.name = var1.getLocalName();
   }

   LocalMember(MemberDefinition var1) {
      this(0L, (ClassDefinition)null, 0, var1.getType(), idClass);
      this.accessPeer = var1;
   }

   final MemberDefinition getMember() {
      return this.name == idClass ? this.accessPeer : null;
   }

   public boolean isLocal() {
      return true;
   }

   public LocalMember copyInline(Context var1) {
      LocalMember var2 = new LocalMember(this.where, this.clazz, this.modifiers, this.type, this.name);
      var2.readcount = this.readcount;
      var2.writecount = this.writecount;
      var2.originalOfCopy = this;
      var2.addModifiers(131072);
      if (this.accessPeer != null && (this.accessPeer.getModifiers() & 131072) == 0) {
         throw new CompilerError("local copyInline");
      } else {
         this.accessPeer = var2;
         return var2;
      }
   }

   public LocalMember getCurrentInlineCopy(Context var1) {
      MemberDefinition var2 = this.accessPeer;
      if (var2 != null && (var2.getModifiers() & 131072) != 0) {
         LocalMember var3 = (LocalMember)var2;
         return var3;
      } else {
         return this;
      }
   }

   public static LocalMember[] copyArguments(Context var0, MemberDefinition var1) {
      Vector var2 = var1.getArguments();
      LocalMember[] var3 = new LocalMember[var2.size()];
      var2.copyInto(var3);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var3[var4].copyInline(var0);
      }

      return var3;
   }

   public static void doneWithArguments(Context var0, LocalMember[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].originalOfCopy.accessPeer == var1[var2]) {
            var1[var2].originalOfCopy.accessPeer = null;
         }
      }

   }

   public boolean isInlineable(Environment var1, boolean var2) {
      return (this.getModifiers() & 1048576) != 0;
   }

   public boolean isUsed() {
      return this.readcount != 0 || this.writecount != 0;
   }

   LocalMember getAccessVar() {
      return (LocalMember)this.accessPeer;
   }

   void setAccessVar(LocalMember var1) {
      this.accessPeer = var1;
   }

   MemberDefinition getAccessVarMember() {
      return this.accessPeer;
   }

   void setAccessVarMember(MemberDefinition var1) {
      this.accessPeer = var1;
   }

   public Node getValue(Environment var1) {
      return (Expression)this.getValue();
   }

   public int getNumber(Context var1) {
      return this.number;
   }
}
