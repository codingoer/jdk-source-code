package sun.tools.jstat;

import java.util.HashMap;
import java.util.Set;

public abstract class Alignment {
   private static int nextOrdinal = 0;
   private static HashMap map = new HashMap();
   private static final String blanks = "                                                                                                                                                               ";
   private final String name;
   private final int value;
   public static final Alignment CENTER = new Alignment("center") {
      protected String align(String var1, int var2) {
         int var3 = var1.length();
         if (var3 >= var2) {
            return var1;
         } else {
            int var4 = var2 - var3;
            int var5 = var4 / 2;
            int var6 = var4 % 2;
            return var5 == 0 ? var1 + "                                                                                                                                                               ".substring(0, var6) : "                                                                                                                                                               ".substring(0, var5) + var1 + "                                                                                                                                                               ".substring(0, var5 + var6);
         }
      }
   };
   public static final Alignment LEFT = new Alignment("left") {
      protected String align(String var1, int var2) {
         int var3 = var1.length();
         if (var3 >= var2) {
            return var1;
         } else {
            int var4 = var2 - var3;
            return var1 + "                                                                                                                                                               ".substring(0, var4);
         }
      }
   };
   public static final Alignment RIGHT = new Alignment("right") {
      protected String align(String var1, int var2) {
         int var3 = var1.length();
         if (var3 >= var2) {
            return var1;
         } else {
            int var4 = var2 - var3;
            return "                                                                                                                                                               ".substring(0, var4) + var1;
         }
      }
   };

   protected abstract String align(String var1, int var2);

   public static Alignment toAlignment(String var0) {
      return (Alignment)map.get(var0);
   }

   public static Set keySet() {
      return map.keySet();
   }

   public String toString() {
      return this.name;
   }

   private Alignment(String var1) {
      this.value = nextOrdinal++;
      this.name = var1;
      map.put(var1, this);
   }

   // $FF: synthetic method
   Alignment(String var1, Object var2) {
      this(var1);
   }
}
