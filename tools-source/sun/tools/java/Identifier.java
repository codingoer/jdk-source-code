package sun.tools.java;

import java.util.Hashtable;

public final class Identifier implements Constants {
   static Hashtable hash = new Hashtable(3001, 0.5F);
   String name;
   Object value;
   Type typeObject = null;
   private int ipos;
   public static final char INNERCLASS_PREFIX = ' ';
   private static final String ambigPrefix = "<<ambiguous>>";

   private Identifier(String var1) {
      this.name = var1;
      this.ipos = var1.indexOf(32);
   }

   int getType() {
      return this.value != null && this.value instanceof Integer ? (Integer)this.value : 60;
   }

   void setType(int var1) {
      this.value = new Integer(var1);
   }

   public static synchronized Identifier lookup(String var0) {
      Identifier var1 = (Identifier)hash.get(var0);
      if (var1 == null) {
         hash.put(var0, var1 = new Identifier(var0));
      }

      return var1;
   }

   public static Identifier lookup(Identifier var0, Identifier var1) {
      if (var0 == idNull) {
         return var1;
      } else if (var0.name.charAt(var0.name.length() - 1) == ' ') {
         return lookup(var0.name + var1.name);
      } else {
         Identifier var2 = lookup(var0 + "." + var1);
         if (!var1.isQualified() && !var0.isInner()) {
            var2.value = var0;
         }

         return var2;
      }
   }

   public static Identifier lookupInner(Identifier var0, Identifier var1) {
      Identifier var2;
      if (var0.isInner()) {
         if (var0.name.charAt(var0.name.length() - 1) == ' ') {
            var2 = lookup(var0.name + var1);
         } else {
            var2 = lookup(var0, var1);
         }
      } else {
         var2 = lookup(var0 + "." + ' ' + var1);
      }

      var2.value = var0.value;
      return var2;
   }

   public String toString() {
      return this.name;
   }

   public boolean isQualified() {
      if (this.value == null) {
         int var1 = this.ipos;
         if (var1 <= 0) {
            var1 = this.name.length();
         } else {
            --var1;
         }

         int var2 = this.name.lastIndexOf(46, var1 - 1);
         this.value = var2 < 0 ? idNull : lookup(this.name.substring(0, var2));
      }

      return this.value instanceof Identifier && this.value != idNull;
   }

   public Identifier getQualifier() {
      return this.isQualified() ? (Identifier)this.value : idNull;
   }

   public Identifier getName() {
      return this.isQualified() ? lookup(this.name.substring(((Identifier)this.value).name.length() + 1)) : this;
   }

   public boolean isInner() {
      return this.ipos > 0;
   }

   public Identifier getFlatName() {
      if (this.isQualified()) {
         return this.getName().getFlatName();
      } else if (this.ipos > 0 && this.name.charAt(this.ipos - 1) == '.') {
         if (this.ipos + 1 == this.name.length()) {
            return lookup(this.name.substring(0, this.ipos - 1));
         } else {
            String var1 = this.name.substring(this.ipos + 1);
            String var2 = this.name.substring(0, this.ipos);
            return lookup(var2 + var1);
         }
      } else {
         return this;
      }
   }

   public Identifier getTopName() {
      return !this.isInner() ? this : lookup(this.getQualifier(), this.getFlatName().getHead());
   }

   public Identifier getHead() {
      Identifier var1;
      for(var1 = this; var1.isQualified(); var1 = var1.getQualifier()) {
      }

      return var1;
   }

   public Identifier getTail() {
      Identifier var1 = this.getHead();
      return var1 == this ? idNull : lookup(this.name.substring(var1.name.length() + 1));
   }

   public boolean hasAmbigPrefix() {
      return this.name.startsWith("<<ambiguous>>");
   }

   public Identifier addAmbigPrefix() {
      return lookup("<<ambiguous>>" + this.name);
   }

   public Identifier removeAmbigPrefix() {
      return this.hasAmbigPrefix() ? lookup(this.name.substring("<<ambiguous>>".length())) : this;
   }
}
