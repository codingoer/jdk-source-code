package sun.jvmstat.perfdata.monitor.v2_0;

public class TypeCode {
   private final String name;
   private final char value;
   public static final TypeCode BOOLEAN = new TypeCode("boolean", 'Z');
   public static final TypeCode CHAR = new TypeCode("char", 'C');
   public static final TypeCode FLOAT = new TypeCode("float", 'F');
   public static final TypeCode DOUBLE = new TypeCode("double", 'D');
   public static final TypeCode BYTE = new TypeCode("byte", 'B');
   public static final TypeCode SHORT = new TypeCode("short", 'S');
   public static final TypeCode INT = new TypeCode("int", 'I');
   public static final TypeCode LONG = new TypeCode("long", 'J');
   public static final TypeCode OBJECT = new TypeCode("object", 'L');
   public static final TypeCode ARRAY = new TypeCode("array", '[');
   public static final TypeCode VOID = new TypeCode("void", 'V');
   private static TypeCode[] basicTypes;

   public String toString() {
      return this.name;
   }

   public int toChar() {
      return this.value;
   }

   public static TypeCode toTypeCode(char var0) {
      for(int var1 = 0; var1 < basicTypes.length; ++var1) {
         if (basicTypes[var1].value == var0) {
            return basicTypes[var1];
         }
      }

      throw new IllegalArgumentException();
   }

   public static TypeCode toTypeCode(byte var0) {
      return toTypeCode((char)var0);
   }

   private TypeCode(String var1, char var2) {
      this.name = var1;
      this.value = var2;
   }

   static {
      basicTypes = new TypeCode[]{LONG, BYTE, BOOLEAN, CHAR, FLOAT, DOUBLE, SHORT, INT, OBJECT, ARRAY, VOID};
   }
}
