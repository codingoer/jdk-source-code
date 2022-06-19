package sun.rmi.rmic.iiop;

import java.util.Hashtable;

class NameContext {
   private Hashtable table;
   private boolean allowCollisions;

   public static synchronized NameContext forName(String var0, boolean var1, BatchEnvironment var2) {
      NameContext var3 = null;
      if (var0 == null) {
         var0 = "null";
      }

      if (var2.nameContexts == null) {
         var2.nameContexts = new Hashtable();
      } else {
         var3 = (NameContext)var2.nameContexts.get(var0);
      }

      if (var3 == null) {
         var3 = new NameContext(var1);
         var2.nameContexts.put(var0, var3);
      }

      return var3;
   }

   public NameContext(boolean var1) {
      this.allowCollisions = var1;
      this.table = new Hashtable();
   }

   public void assertPut(String var1) throws Exception {
      String var2 = this.add(var1);
      if (var2 != null) {
         throw new Exception(var2);
      }
   }

   public void put(String var1) {
      if (!this.allowCollisions) {
         throw new Error("Must use assertPut(name)");
      } else {
         this.add(var1);
      }
   }

   private String add(String var1) {
      String var2 = var1.toLowerCase();
      Name var3 = (Name)this.table.get(var2);
      if (var3 != null) {
         if (!var1.equals(var3.name)) {
            if (!this.allowCollisions) {
               return new String("\"" + var1 + "\" and \"" + var3.name + "\"");
            }

            var3.collisions = true;
         }
      } else {
         this.table.put(var2, new Name(var1, false));
      }

      return null;
   }

   public String get(String var1) {
      Name var2 = (Name)this.table.get(var1.toLowerCase());
      String var3 = var1;
      if (var2.collisions) {
         int var4 = var1.length();
         boolean var5 = true;

         for(int var6 = 0; var6 < var4; ++var6) {
            if (Character.isUpperCase(var1.charAt(var6))) {
               var3 = var3 + "_";
               var3 = var3 + var6;
               var5 = false;
            }
         }

         if (var5) {
            var3 = var3 + "_";
         }
      }

      return var3;
   }

   public void clear() {
      this.table.clear();
   }

   public class Name {
      public String name;
      public boolean collisions;

      public Name(String var2, boolean var3) {
         this.name = var2;
         this.collisions = var3;
      }
   }
}
