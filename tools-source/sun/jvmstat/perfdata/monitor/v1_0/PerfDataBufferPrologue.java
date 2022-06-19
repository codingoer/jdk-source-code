package sun.jvmstat.perfdata.monitor.v1_0;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBufferPrologue;

public class PerfDataBufferPrologue extends AbstractPerfDataBufferPrologue {
   private static final int SUPPORTED_MAJOR_VERSION = 1;
   private static final int SUPPORTED_MINOR_VERSION = 0;
   static final int PERFDATA_PROLOG_USED_OFFSET = 8;
   static final int PERFDATA_PROLOG_USED_SIZE = 4;
   static final int PERFDATA_PROLOG_OVERFLOW_OFFSET = 12;
   static final int PERFDATA_PROLOG_OVERFLOW_SIZE = 4;
   static final int PERFDATA_PROLOG_MODTIMESTAMP_OFFSET = 16;
   static final int PERFDATA_PROLOG_MODTIMESTAMP_SIZE = 8;
   static final int PERFDATA_PROLOG_SIZE = 24;
   static final String PERFDATA_BUFFER_SIZE_NAME = "sun.perfdata.size";
   static final String PERFDATA_BUFFER_USED_NAME = "sun.perfdata.used";
   static final String PERFDATA_OVERFLOW_NAME = "sun.perfdata.overflow";
   static final String PERFDATA_MODTIMESTAMP_NAME = "sun.perfdata.timestamp";

   public PerfDataBufferPrologue(ByteBuffer var1) throws MonitorException {
      super(var1);

      assert this.getMajorVersion() == 1 && this.getMinorVersion() == 0;
   }

   public boolean supportsAccessible() {
      return false;
   }

   public boolean isAccessible() {
      return true;
   }

   public int getUsed() {
      this.byteBuffer.position(8);
      return this.byteBuffer.getInt();
   }

   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   public int getOverflow() {
      this.byteBuffer.position(12);
      return this.byteBuffer.getInt();
   }

   public long getModificationTimeStamp() {
      this.byteBuffer.position(16);
      return this.byteBuffer.getLong();
   }

   public int getSize() {
      return 24;
   }

   public IntBuffer usedBuffer() {
      this.byteBuffer.position(8);
      IntBuffer var1 = this.byteBuffer.asIntBuffer();
      var1.limit(1);
      return var1;
   }

   public IntBuffer sizeBuffer() {
      IntBuffer var1 = IntBuffer.allocate(1);
      var1.put(this.byteBuffer.capacity());
      return var1;
   }

   public IntBuffer overflowBuffer() {
      this.byteBuffer.position(12);
      IntBuffer var1 = this.byteBuffer.asIntBuffer();
      var1.limit(1);
      return var1;
   }

   public LongBuffer modificationTimeStampBuffer() {
      this.byteBuffer.position(16);
      LongBuffer var1 = this.byteBuffer.asLongBuffer();
      var1.limit(1);
      return var1;
   }
}
