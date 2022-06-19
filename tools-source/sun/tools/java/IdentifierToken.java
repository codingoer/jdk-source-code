package sun.tools.java;

public class IdentifierToken {
   long where;
   int modifiers;
   Identifier id;

   public IdentifierToken(long var1, Identifier var3) {
      this.where = var1;
      this.id = var3;
   }

   public IdentifierToken(Identifier var1) {
      this.where = 0L;
      this.id = var1;
   }

   public IdentifierToken(long var1, Identifier var3, int var4) {
      this.where = var1;
      this.id = var3;
      this.modifiers = var4;
   }

   public long getWhere() {
      return this.where;
   }

   public Identifier getName() {
      return this.id;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String toString() {
      return this.id.toString();
   }

   public static long getWhere(IdentifierToken var0, long var1) {
      return var0 != null && var0.where != 0L ? var0.where : var1;
   }
}
