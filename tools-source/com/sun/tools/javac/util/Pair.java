package com.sun.tools.javac.util;

import java.util.Objects;

public class Pair {
   public final Object fst;
   public final Object snd;

   public Pair(Object var1, Object var2) {
      this.fst = var1;
      this.snd = var2;
   }

   public String toString() {
      return "Pair[" + this.fst + "," + this.snd + "]";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Pair && Objects.equals(this.fst, ((Pair)var1).fst) && Objects.equals(this.snd, ((Pair)var1).snd);
   }

   public int hashCode() {
      if (this.fst == null) {
         return this.snd == null ? 0 : this.snd.hashCode() + 1;
      } else {
         return this.snd == null ? this.fst.hashCode() + 2 : this.fst.hashCode() * 17 + this.snd.hashCode();
      }
   }

   public static Pair of(Object var0, Object var1) {
      return new Pair(var0, var1);
   }
}
