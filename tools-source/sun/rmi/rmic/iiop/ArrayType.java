package sun.rmi.rmic.iiop;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Vector;
import sun.tools.java.ClassDefinition;

public class ArrayType extends Type {
   private Type type;
   private int arrayDimension;
   private String brackets;
   private String bracketsSig;

   public static ArrayType forArray(sun.tools.java.Type var0, ContextStack var1) {
      ArrayType var2 = null;
      sun.tools.java.Type var3 = var0;
      if (var0.getTypeCode() == 9) {
         while(true) {
            if (var3.getTypeCode() != 9) {
               Type var4 = getType(var0, var1);
               if (var4 != null) {
                  if (!(var4 instanceof ArrayType)) {
                     return null;
                  }

                  return (ArrayType)var4;
               }

               Type var5 = CompoundType.makeType(var3, (ClassDefinition)null, var1);
               if (var5 != null) {
                  var2 = new ArrayType(var1, var5, var0.getArrayDimension());
                  putType(var0, var2, var1);
                  var1.push(var2);
                  var1.pop(true);
               }
               break;
            }

            var3 = var3.getElementType();
         }
      }

      return var2;
   }

   public String getSignature() {
      return this.bracketsSig + this.type.getSignature();
   }

   public Type getElementType() {
      return this.type;
   }

   public int getArrayDimension() {
      return this.arrayDimension;
   }

   public String getArrayBrackets() {
      return this.brackets;
   }

   public String toString() {
      return this.getQualifiedName() + this.brackets;
   }

   public String getTypeDescription() {
      return "Array of " + this.type.getTypeDescription();
   }

   public String getTypeName(boolean var1, boolean var2, boolean var3) {
      return var2 ? super.getTypeName(var1, var2, var3) : super.getTypeName(var1, var2, var3) + this.brackets;
   }

   protected void swapInvalidTypes() {
      if (this.type.getStatus() != 1) {
         this.type = this.getValidType(this.type);
      }

   }

   protected boolean addTypes(int var1, HashSet var2, Vector var3) {
      boolean var4 = super.addTypes(var1, var2, var3);
      if (var4) {
         this.getElementType().addTypes(var1, var2, var3);
      }

      return var4;
   }

   private ArrayType(ContextStack var1, Type var2, int var3) {
      super(var1, 262144);
      this.type = var2;
      this.arrayDimension = var3;
      this.brackets = "";
      this.bracketsSig = "";

      for(int var4 = 0; var4 < var3; ++var4) {
         this.brackets = this.brackets + "[]";
         this.bracketsSig = this.bracketsSig + "[";
      }

      String var6 = IDLNames.getArrayName(var2, var3);
      String[] var5 = IDLNames.getArrayModuleNames(var2);
      this.setNames(var2.getIdentifier(), var5, var6);
      this.setRepositoryID();
   }

   protected Class loadClass() {
      Class var1 = null;
      Class var2 = this.type.getClassInstance();
      if (var2 != null) {
         var1 = Array.newInstance(var2, new int[this.arrayDimension]).getClass();
      }

      return var1;
   }

   protected void destroy() {
      super.destroy();
      if (this.type != null) {
         this.type.destroy();
         this.type = null;
      }

      this.brackets = null;
      this.bracketsSig = null;
   }
}
