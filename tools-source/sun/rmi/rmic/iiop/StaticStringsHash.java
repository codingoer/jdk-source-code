package sun.rmi.rmic.iiop;

public class StaticStringsHash {
   public String[] strings = null;
   public int[] keys = null;
   public int[][] buckets = (int[][])null;
   public String method = null;
   private int length;
   private int[] tempKeys;
   private int[] bucketSizes;
   private int bucketCount;
   private int maxDepth;
   private int minStringLength = Integer.MAX_VALUE;
   private int keyKind;
   private int charAt;
   private static final int LENGTH = 0;
   private static final int CHAR_AT = 1;
   private static final int HASH_CODE = 2;
   private static final int CHAR_AT_MAX_LINES = 50;
   private static final int CHAR_AT_MAX_CHARS = 1000;

   public int getKey(String var1) {
      switch (this.keyKind) {
         case 0:
            return var1.length();
         case 1:
            return var1.charAt(this.charAt);
         case 2:
            return var1.hashCode();
         default:
            throw new Error("Bad keyKind");
      }
   }

   public StaticStringsHash(String[] var1) {
      this.strings = var1;
      this.length = var1.length;
      this.tempKeys = new int[this.length];
      this.bucketSizes = new int[this.length];
      this.setMinStringLength();
      int var2 = this.getKeys(0);
      int var3 = -1;
      boolean var4 = false;
      int var6;
      int var7;
      if (var2 > 1) {
         int var5 = this.minStringLength;
         if (this.length > 50 && this.length * var5 > 1000) {
            var5 = this.length / 1000;
         }

         this.charAt = 0;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = this.getKeys(1);
            if (var7 < var2) {
               var2 = var7;
               var3 = var6;
               if (var7 == 1) {
                  break;
               }
            }

            ++this.charAt;
         }

         this.charAt = var3;
         if (var2 > 1) {
            var6 = this.getKeys(2);
            if (var6 < var2 - 3) {
               var4 = true;
            }
         }

         if (!var4) {
            if (var3 >= 0) {
               this.getKeys(1);
            } else {
               this.getKeys(0);
            }
         }
      }

      this.keys = new int[this.bucketCount];
      System.arraycopy(this.tempKeys, 0, this.keys, 0, this.bucketCount);

      boolean var11;
      do {
         var11 = false;

         for(var6 = 0; var6 < this.bucketCount - 1; ++var6) {
            if (this.keys[var6] > this.keys[var6 + 1]) {
               var7 = this.keys[var6];
               this.keys[var6] = this.keys[var6 + 1];
               this.keys[var6 + 1] = var7;
               var7 = this.bucketSizes[var6];
               this.bucketSizes[var6] = this.bucketSizes[var6 + 1];
               this.bucketSizes[var6 + 1] = var7;
               var11 = true;
            }
         }
      } while(var11);

      var6 = this.findUnusedKey();
      this.buckets = new int[this.bucketCount][];

      int var8;
      for(var7 = 0; var7 < this.bucketCount; ++var7) {
         this.buckets[var7] = new int[this.bucketSizes[var7]];

         for(var8 = 0; var8 < this.bucketSizes[var7]; ++var8) {
            this.buckets[var7][var8] = var6;
         }
      }

      for(var7 = 0; var7 < var1.length; ++var7) {
         var8 = this.getKey(var1[var7]);

         for(int var9 = 0; var9 < this.bucketCount; ++var9) {
            if (this.keys[var9] == var8) {
               int var10;
               for(var10 = 0; this.buckets[var9][var10] != var6; ++var10) {
               }

               this.buckets[var9][var10] = var7;
               break;
            }
         }
      }

   }

   public static void main(String[] var0) {
      StaticStringsHash var1 = new StaticStringsHash(var0);
      System.out.println();
      System.out.println("    public boolean contains(String key) {");
      System.out.println("        switch (key." + var1.method + ") {");

      for(int var2 = 0; var2 < var1.buckets.length; ++var2) {
         System.out.println("            case " + var1.keys[var2] + ": ");

         for(int var3 = 0; var3 < var1.buckets[var2].length; ++var3) {
            if (var3 > 0) {
               System.out.print("                } else ");
            } else {
               System.out.print("                ");
            }

            System.out.println("if (key.equals(\"" + var1.strings[var1.buckets[var2][var3]] + "\")) {");
            System.out.println("                    return true;");
         }

         System.out.println("                }");
      }

      System.out.println("        }");
      System.out.println("        return false;");
      System.out.println("    }");
   }

   private void resetKeys(int var1) {
      this.keyKind = var1;
      switch (var1) {
         case 0:
            this.method = "length()";
            break;
         case 1:
            this.method = "charAt(" + this.charAt + ")";
            break;
         case 2:
            this.method = "hashCode()";
      }

      this.maxDepth = 1;
      this.bucketCount = 0;

      for(int var2 = 0; var2 < this.length; ++var2) {
         this.tempKeys[var2] = 0;
         this.bucketSizes[var2] = 0;
      }

   }

   private void setMinStringLength() {
      for(int var1 = 0; var1 < this.length; ++var1) {
         if (this.strings[var1].length() < this.minStringLength) {
            this.minStringLength = this.strings[var1].length();
         }
      }

   }

   private int findUnusedKey() {
      int var1 = 0;
      int var2 = this.keys.length;

      while(true) {
         boolean var3 = false;

         for(int var4 = 0; var4 < var2; ++var4) {
            if (this.keys[var4] == var1) {
               var3 = true;
               break;
            }
         }

         if (!var3) {
            return var1;
         }

         --var1;
      }
   }

   private int getKeys(int var1) {
      this.resetKeys(var1);

      for(int var2 = 0; var2 < this.strings.length; ++var2) {
         this.addKey(this.getKey(this.strings[var2]));
      }

      return this.maxDepth;
   }

   private void addKey(int var1) {
      boolean var2 = true;

      for(int var3 = 0; var3 < this.bucketCount; ++var3) {
         if (this.tempKeys[var3] == var1) {
            var2 = false;
            int var10002 = this.bucketSizes[var3]++;
            if (this.bucketSizes[var3] > this.maxDepth) {
               this.maxDepth = this.bucketSizes[var3];
            }
            break;
         }
      }

      if (var2) {
         this.tempKeys[this.bucketCount] = var1;
         this.bucketSizes[this.bucketCount] = 1;
         ++this.bucketCount;
      }

   }
}
