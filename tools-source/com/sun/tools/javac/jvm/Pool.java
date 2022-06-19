package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.ArrayUtils;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.Name;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Pool {
   public static final int MAX_ENTRIES = 65535;
   public static final int MAX_STRING_LENGTH = 65535;
   int pp;
   Object[] pool;
   Map indices;
   Types types;

   public Pool(int var1, Object[] var2, Types var3) {
      this.pp = var1;
      this.pool = var2;
      this.types = var3;
      this.indices = new HashMap(var2.length);

      for(int var4 = 1; var4 < var1; ++var4) {
         if (var2[var4] != null) {
            this.indices.put(var2[var4], var4);
         }
      }

   }

   public Pool(Types var1) {
      this(1, new Object[64], var1);
   }

   public int numEntries() {
      return this.pp;
   }

   public void reset() {
      this.pp = 1;
      this.indices.clear();
   }

   public int put(Object var1) {
      var1 = this.makePoolValue(var1);
      Integer var2 = (Integer)this.indices.get(var1);
      if (var2 == null) {
         var2 = this.pp;
         this.indices.put(var1, var2);
         this.pool = ArrayUtils.ensureCapacity(this.pool, this.pp);
         this.pool[this.pp++] = var1;
         if (var1 instanceof Long || var1 instanceof Double) {
            this.pool = ArrayUtils.ensureCapacity(this.pool, this.pp);
            this.pool[this.pp++] = null;
         }
      }

      return var2;
   }

   Object makePoolValue(Object var1) {
      if (var1 instanceof Symbol.DynamicMethodSymbol) {
         return new DynamicMethod((Symbol.DynamicMethodSymbol)var1, this.types);
      } else if (var1 instanceof Symbol.MethodSymbol) {
         return new Method((Symbol.MethodSymbol)var1, this.types);
      } else if (var1 instanceof Symbol.VarSymbol) {
         return new Variable((Symbol.VarSymbol)var1, this.types);
      } else {
         return var1 instanceof Type ? new Types.UniqueType((Type)var1, this.types) : var1;
      }
   }

   public int get(Object var1) {
      Integer var2 = (Integer)this.indices.get(var1);
      return var2 == null ? -1 : var2;
   }

   public static class MethodHandle {
      int refKind;
      Symbol refSym;
      Types.UniqueType uniqueType;
      Filter nonInitFilter = new Filter() {
         public boolean accepts(Name var1) {
            return var1 != var1.table.names.init && var1 != var1.table.names.clinit;
         }
      };
      Filter initFilter = new Filter() {
         public boolean accepts(Name var1) {
            return var1 == var1.table.names.init;
         }
      };

      public MethodHandle(int var1, Symbol var2, Types var3) {
         this.refKind = var1;
         this.refSym = var2;
         this.uniqueType = new Types.UniqueType(this.refSym.type, var3);
         this.checkConsistent();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof MethodHandle)) {
            return false;
         } else {
            MethodHandle var2 = (MethodHandle)var1;
            if (var2.refKind != this.refKind) {
               return false;
            } else {
               Symbol var3 = var2.refSym;
               return var3.name == this.refSym.name && var3.owner == this.refSym.owner && ((MethodHandle)var1).uniqueType.equals(this.uniqueType);
            }
         }
      }

      public int hashCode() {
         return this.refKind * 65 + this.refSym.name.hashCode() * 33 + this.refSym.owner.hashCode() * 9 + this.uniqueType.hashCode();
      }

      private void checkConsistent() {
         boolean var1 = false;
         byte var2 = -1;
         Filter var3 = this.nonInitFilter;
         boolean var4 = false;
         switch (this.refKind) {
            case 2:
            case 4:
               var1 = true;
            case 1:
            case 3:
               var2 = 4;
               break;
            case 6:
               var4 = true;
               var1 = true;
            case 5:
               var2 = 16;
               break;
            case 7:
               var4 = true;
               var2 = 16;
               break;
            case 8:
               var3 = this.initFilter;
               var2 = 16;
               break;
            case 9:
               var4 = true;
               var2 = 16;
         }

         Assert.check(!this.refSym.isStatic() || var1);
         Assert.check(this.refSym.kind == var2);
         Assert.check(var3.accepts(this.refSym.name));
         Assert.check(!this.refSym.owner.isInterface() || var4);
      }
   }

   static class Variable extends Symbol.DelegatedSymbol {
      Types.UniqueType uniqueType;

      Variable(Symbol.VarSymbol var1, Types var2) {
         super(var1);
         this.uniqueType = new Types.UniqueType(var1.type, var2);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Variable)) {
            return false;
         } else {
            Symbol.VarSymbol var2 = (Symbol.VarSymbol)((Variable)var1).other;
            Symbol.VarSymbol var3 = (Symbol.VarSymbol)this.other;
            return var2.name == var3.name && var2.owner == var3.owner && ((Variable)var1).uniqueType.equals(this.uniqueType);
         }
      }

      public int hashCode() {
         Symbol.VarSymbol var1 = (Symbol.VarSymbol)this.other;
         return var1.name.hashCode() * 33 + var1.owner.hashCode() * 9 + this.uniqueType.hashCode();
      }
   }

   static class DynamicMethod extends Method {
      public Object[] uniqueStaticArgs;

      DynamicMethod(Symbol.DynamicMethodSymbol var1, Types var2) {
         super(var1, var2);
         this.uniqueStaticArgs = this.getUniqueTypeArray(var1.staticArgs, var2);
      }

      public boolean equals(Object var1) {
         if (!super.equals(var1)) {
            return false;
         } else if (!(var1 instanceof DynamicMethod)) {
            return false;
         } else {
            Symbol.DynamicMethodSymbol var2 = (Symbol.DynamicMethodSymbol)this.other;
            Symbol.DynamicMethodSymbol var3 = (Symbol.DynamicMethodSymbol)((DynamicMethod)var1).other;
            return var2.bsm == var3.bsm && var2.bsmKind == var3.bsmKind && Arrays.equals(this.uniqueStaticArgs, ((DynamicMethod)var1).uniqueStaticArgs);
         }
      }

      public int hashCode() {
         int var1 = super.hashCode();
         Symbol.DynamicMethodSymbol var2 = (Symbol.DynamicMethodSymbol)this.other;
         var1 += var2.bsmKind * 7 + var2.bsm.hashCode() * 11;

         for(int var3 = 0; var3 < var2.staticArgs.length; ++var3) {
            var1 += this.uniqueStaticArgs[var3].hashCode() * 23;
         }

         return var1;
      }

      private Object[] getUniqueTypeArray(Object[] var1, Types var2) {
         Object[] var3 = new Object[var1.length];

         for(int var4 = 0; var4 < var1.length; ++var4) {
            if (var1[var4] instanceof Type) {
               var3[var4] = new Types.UniqueType((Type)var1[var4], var2);
            } else {
               var3[var4] = var1[var4];
            }
         }

         return var3;
      }
   }

   static class Method extends Symbol.DelegatedSymbol {
      Types.UniqueType uniqueType;

      Method(Symbol.MethodSymbol var1, Types var2) {
         super(var1);
         this.uniqueType = new Types.UniqueType(var1.type, var2);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Method)) {
            return false;
         } else {
            Symbol.MethodSymbol var2 = (Symbol.MethodSymbol)((Method)var1).other;
            Symbol.MethodSymbol var3 = (Symbol.MethodSymbol)this.other;
            return var2.name == var3.name && var2.owner == var3.owner && ((Method)var1).uniqueType.equals(this.uniqueType);
         }
      }

      public int hashCode() {
         Symbol.MethodSymbol var1 = (Symbol.MethodSymbol)this.other;
         return var1.name.hashCode() * 33 + var1.owner.hashCode() * 9 + this.uniqueType.hashCode();
      }
   }
}
