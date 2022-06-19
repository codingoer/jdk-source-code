package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;
import sun.tools.java.RuntimeConstants;
import sun.tools.java.Type;
import sun.tools.tree.StringExpression;

public final class ConstantPool implements RuntimeConstants {
   Hashtable hash = new Hashtable(101);

   public int index(Object var1) {
      return ((ConstantPoolData)this.hash.get(var1)).index;
   }

   public void put(Object var1) {
      Object var2 = (ConstantPoolData)this.hash.get(var1);
      if (var2 == null) {
         if (var1 instanceof String) {
            var2 = new StringConstantData(this, (String)var1);
         } else if (var1 instanceof StringExpression) {
            var2 = new StringExpressionConstantData(this, (StringExpression)var1);
         } else if (var1 instanceof ClassDeclaration) {
            var2 = new ClassConstantData(this, (ClassDeclaration)var1);
         } else if (var1 instanceof Type) {
            var2 = new ClassConstantData(this, (Type)var1);
         } else if (var1 instanceof MemberDefinition) {
            var2 = new FieldConstantData(this, (MemberDefinition)var1);
         } else if (var1 instanceof NameAndTypeData) {
            var2 = new NameAndTypeConstantData(this, (NameAndTypeData)var1);
         } else if (var1 instanceof Number) {
            var2 = new NumberConstantData(this, (Number)var1);
         }

         this.hash.put(var1, var2);
      }

   }

   public void write(Environment var1, DataOutputStream var2) throws IOException {
      ConstantPoolData[] var3 = new ConstantPoolData[this.hash.size()];
      String[] var4 = new String[var3.length];
      int var5 = 1;
      int var6 = 0;

      int var7;
      for(var7 = 0; var7 < 5; ++var7) {
         Enumeration var9 = this.hash.elements();

         while(var9.hasMoreElements()) {
            ConstantPoolData var10 = (ConstantPoolData)var9.nextElement();
            if (var10.order() == var7) {
               var4[var6] = sortKey(var10);
               var3[var6++] = var10;
            }
         }

         xsort(var3, var4, var6, var6 - 1);
      }

      for(var7 = 0; var7 < var3.length; ++var7) {
         ConstantPoolData var8 = var3[var7];
         var8.index = var5;
         var5 += var8.width();
      }

      var2.writeShort(var5);

      for(var7 = 0; var7 < var6; ++var7) {
         var3[var7].write(var1, var2, this);
      }

   }

   private static String sortKey(ConstantPoolData var0) {
      if (var0 instanceof NumberConstantData) {
         Number var4 = ((NumberConstantData)var0).num;
         String var2 = var4.toString();
         byte var3 = 3;
         if (var4 instanceof Integer) {
            var3 = 0;
         } else if (var4 instanceof Float) {
            var3 = 1;
         } else if (var4 instanceof Long) {
            var3 = 2;
         }

         return "\u0000" + (char)(var2.length() + var3 << 8) + var2;
      } else if (var0 instanceof StringExpressionConstantData) {
         return (String)((StringExpressionConstantData)var0).str.getValue();
      } else if (var0 instanceof FieldConstantData) {
         MemberDefinition var1 = ((FieldConstantData)var0).field;
         return var1.getName() + " " + var1.getType().getTypeSignature() + " " + var1.getClassDeclaration().getName();
      } else if (var0 instanceof NameAndTypeConstantData) {
         return ((NameAndTypeConstantData)var0).name + " " + ((NameAndTypeConstantData)var0).type;
      } else {
         return var0 instanceof ClassConstantData ? ((ClassConstantData)var0).name : ((StringConstantData)var0).str;
      }
   }

   private static void xsort(ConstantPoolData[] var0, String[] var1, int var2, int var3) {
      if (var2 < var3) {
         String var4 = var1[var2];
         int var5 = var2;
         int var6 = var3;

         while(var5 < var6) {
            while(var5 <= var3 && var1[var5].compareTo(var4) <= 0) {
               ++var5;
            }

            while(var6 >= var2 && var1[var6].compareTo(var4) > 0) {
               --var6;
            }

            if (var5 < var6) {
               ConstantPoolData var7 = var0[var5];
               String var8 = var1[var5];
               var0[var5] = var0[var6];
               var0[var6] = var7;
               var1[var5] = var1[var6];
               var1[var6] = var8;
            }
         }

         ConstantPoolData var10 = var0[var2];
         String var9 = var1[var2];
         var0[var2] = var0[var6];
         var0[var6] = var10;
         var1[var2] = var1[var6];
         var1[var6] = var9;
         xsort(var0, var1, var2, var6 - 1);
         xsort(var0, var1, var6 + 1, var3);
      }
   }
}
