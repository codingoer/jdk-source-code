package sun.tools.util;

import java.lang.reflect.Modifier;
import sun.tools.java.ClassDefinition;
import sun.tools.java.MemberDefinition;

public class ModifierFilter extends Modifier {
   public static final long PACKAGE = Long.MIN_VALUE;
   public static final long ALL_ACCESS = -9223372036854775801L;
   private long oneOf;
   private long must;
   private long cannot;
   private static final int ACCESS_BITS = 7;

   public ModifierFilter(long var1) {
      this(var1, 0L, 0L);
   }

   public ModifierFilter(long var1, long var3, long var5) {
      this.oneOf = var1;
      this.must = var3;
      this.cannot = var5;
   }

   public boolean checkModifier(int var1) {
      long var2 = (var1 & 7) == 0 ? (long)var1 | Long.MIN_VALUE : (long)var1;
      return (this.oneOf == 0L || (this.oneOf & var2) != 0L) && (this.must & var2) == this.must && (this.cannot & var2) == 0L;
   }

   public boolean checkMember(MemberDefinition var1) {
      return this.checkModifier(var1.getModifiers());
   }

   public boolean checkClass(ClassDefinition var1) {
      return this.checkModifier(var1.getModifiers());
   }
}
