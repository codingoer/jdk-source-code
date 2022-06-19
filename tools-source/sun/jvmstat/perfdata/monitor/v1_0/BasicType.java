package sun.jvmstat.perfdata.monitor.v1_0;

public class BasicType {
   private final String name;
   private final int value;
   public static final BasicType BOOLEAN = new BasicType("boolean", 4);
   public static final BasicType CHAR = new BasicType("char", 5);
   public static final BasicType FLOAT = new BasicType("float", 6);
   public static final BasicType DOUBLE = new BasicType("double", 7);
   public static final BasicType BYTE = new BasicType("byte", 8);
   public static final BasicType SHORT = new BasicType("short", 9);
   public static final BasicType INT = new BasicType("int", 10);
   public static final BasicType LONG = new BasicType("long", 11);
   public static final BasicType OBJECT = new BasicType("object", 12);
   public static final BasicType ARRAY = new BasicType("array", 13);
   public static final BasicType VOID = new BasicType("void", 14);
   public static final BasicType ADDRESS = new BasicType("address", 15);
   public static final BasicType ILLEGAL = new BasicType("illegal", 99);
   private static BasicType[] basicTypes;

   public String toString() {
      return this.name;
   }

   public int intValue() {
      return this.value;
   }

   public static BasicType toBasicType(int var0) {
      for(int var1 = 0; var1 < basicTypes.length; ++var1) {
         if (basicTypes[var1].intValue() == var1) {
            return basicTypes[var1];
         }
      }

      return ILLEGAL;
   }

   private BasicType(String var1, int var2) {
      this.name = var1;
      this.value = var2;
   }

   static {
      basicTypes = new BasicType[]{BOOLEAN, CHAR, FLOAT, DOUBLE, BYTE, SHORT, INT, LONG, OBJECT, ARRAY, VOID, ADDRESS, ILLEGAL};
   }
}
