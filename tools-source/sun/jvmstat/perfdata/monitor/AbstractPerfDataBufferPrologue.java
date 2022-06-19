package sun.jvmstat.perfdata.monitor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import sun.jvmstat.monitor.MonitorException;

public abstract class AbstractPerfDataBufferPrologue {
   protected ByteBuffer byteBuffer;
   static final int PERFDATA_PROLOG_OFFSET = 0;
   static final int PERFDATA_PROLOG_MAGIC_OFFSET = 0;
   static final int PERFDATA_PROLOG_BYTEORDER_OFFSET = 4;
   static final int PERFDATA_PROLOG_BYTEORDER_SIZE = 1;
   static final int PERFDATA_PROLOG_MAJOR_OFFSET = 5;
   static final int PERFDATA_PROLOG_MAJOR_SIZE = 1;
   static final int PERFDATA_PROLOG_MINOR_OFFSET = 6;
   static final int PERFDATA_PROLOG_MINOR_SIZE = 1;
   static final int PERFDATA_PROLOG_RESERVEDB1_OFFSET = 7;
   static final int PERFDATA_PROLOG_RESERVEDB1_SIZE = 1;
   static final int PERFDATA_PROLOG_SIZE = 8;
   static final byte PERFDATA_BIG_ENDIAN = 0;
   static final byte PERFDATA_LITTLE_ENDIAN = 1;
   static final int PERFDATA_MAGIC = -889274176;
   public static final String PERFDATA_MAJOR_NAME = "sun.perfdata.majorVersion";
   public static final String PERFDATA_MINOR_NAME = "sun.perfdata.minorVersion";

   public AbstractPerfDataBufferPrologue(ByteBuffer var1) throws MonitorException {
      this.byteBuffer = var1.duplicate();
      if (this.getMagic() != -889274176) {
         throw new MonitorVersionException("Bad Magic: " + Integer.toHexString(this.getMagic()));
      } else {
         this.byteBuffer.order(this.getByteOrder());
      }
   }

   public int getMagic() {
      ByteOrder var1 = this.byteBuffer.order();
      this.byteBuffer.order(ByteOrder.BIG_ENDIAN);
      this.byteBuffer.position(0);
      int var2 = this.byteBuffer.getInt();
      this.byteBuffer.order(var1);
      return var2;
   }

   public ByteOrder getByteOrder() {
      this.byteBuffer.position(4);
      byte var1 = this.byteBuffer.get();
      return var1 == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
   }

   public int getMajorVersion() {
      this.byteBuffer.position(5);
      return this.byteBuffer.get();
   }

   public int getMinorVersion() {
      this.byteBuffer.position(6);
      return this.byteBuffer.get();
   }

   public abstract boolean isAccessible();

   public abstract boolean supportsAccessible();

   public int getSize() {
      return 8;
   }

   public IntBuffer majorVersionBuffer() {
      int[] var1 = new int[]{this.getMajorVersion()};
      IntBuffer var2 = IntBuffer.wrap(var1);
      var2.limit(1);
      return var2;
   }

   public IntBuffer minorVersionBuffer() {
      int[] var1 = new int[]{this.getMinorVersion()};
      IntBuffer var2 = IntBuffer.wrap(var1);
      var2.limit(1);
      return var2;
   }

   public static int getMagic(ByteBuffer var0) {
      int var1 = var0.position();
      ByteOrder var2 = var0.order();
      var0.order(ByteOrder.BIG_ENDIAN);
      var0.position(0);
      int var3 = var0.getInt();
      var0.order(var2);
      var0.position(var1);
      return var3;
   }

   public static int getMajorVersion(ByteBuffer var0) {
      int var1 = var0.position();
      var0.position(5);
      byte var2 = var0.get();
      var0.position(var1);
      return var2;
   }

   public static int getMinorVersion(ByteBuffer var0) {
      int var1 = var0.position();
      var0.position(6);
      byte var2 = var0.get();
      var0.position(var1);
      return var2;
   }

   public static ByteOrder getByteOrder(ByteBuffer var0) {
      int var1 = var0.position();
      var0.position(4);
      ByteOrder var2 = var0.get() == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
      var0.position(var1);
      return var2;
   }
}
