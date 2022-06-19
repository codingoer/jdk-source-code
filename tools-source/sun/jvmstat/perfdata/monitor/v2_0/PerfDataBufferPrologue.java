package sun.jvmstat.perfdata.monitor.v2_0;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBufferPrologue;

public class PerfDataBufferPrologue extends AbstractPerfDataBufferPrologue {
   private static final int SUPPORTED_MAJOR_VERSION = 2;
   private static final int SUPPORTED_MINOR_VERSION = 0;
   static final int PERFDATA_PROLOG_ACCESSIBLE_OFFSET = 7;
   static final int PERFDATA_PROLOG_ACCESSIBLE_SIZE = 1;
   static final int PERFDATA_PROLOG_USED_OFFSET = 8;
   static final int PERFDATA_PROLOG_USED_SIZE = 4;
   static final int PERFDATA_PROLOG_OVERFLOW_OFFSET = 12;
   static final int PERFDATA_PROLOG_OVERFLOW_SIZE = 4;
   static final int PERFDATA_PROLOG_MODTIMESTAMP_OFFSET = 16;
   static final int PERFDATA_PROLOG_MODTIMESTAMP_SIZE = 8;
   static final int PERFDATA_PROLOG_ENTRYOFFSET_OFFSET = 24;
   static final int PERFDATA_PROLOG_ENTRYOFFSET_SIZE = 4;
   static final int PERFDATA_PROLOG_NUMENTRIES_OFFSET = 28;
   static final int PERFDATA_PROLOG_NUMENTRIES_SIZE = 4;
   static final int PERFDATA_PROLOG_SIZE = 32;
   static final String PERFDATA_BUFFER_SIZE_NAME = "sun.perfdata.size";
   static final String PERFDATA_BUFFER_USED_NAME = "sun.perfdata.used";
   static final String PERFDATA_OVERFLOW_NAME = "sun.perfdata.overflow";
   static final String PERFDATA_MODTIMESTAMP_NAME = "sun.perfdata.timestamp";
   static final String PERFDATA_NUMENTRIES_NAME = "sun.perfdata.entries";

   public PerfDataBufferPrologue(ByteBuffer var1) throws MonitorException {
      super(var1);

      assert this.getMajorVersion() == 2 && this.getMinorVersion() == 0;
   }

   public boolean supportsAccessible() {
      return true;
   }

   public boolean isAccessible() {
      assert this.supportsAccessible();

      this.byteBuffer.position(7);
      byte var1 = this.byteBuffer.get();
      return var1 != 0;
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

   public int getEntryOffset() {
      this.byteBuffer.position(24);
      return this.byteBuffer.getInt();
   }

   public int getNumEntries() {
      this.byteBuffer.position(28);
      return this.byteBuffer.getInt();
   }

   public int getSize() {
      return 32;
   }

   IntBuffer usedBuffer() {
      this.byteBuffer.position(8);
      IntBuffer var1 = this.byteBuffer.asIntBuffer();
      var1.limit(1);
      return var1;
   }

   IntBuffer sizeBuffer() {
      IntBuffer var1 = IntBuffer.allocate(1);
      var1.put(this.byteBuffer.capacity());
      return var1;
   }

   IntBuffer overflowBuffer() {
      this.byteBuffer.position(12);
      IntBuffer var1 = this.byteBuffer.asIntBuffer();
      var1.limit(1);
      return var1;
   }

   LongBuffer modificationTimeStampBuffer() {
      this.byteBuffer.position(16);
      LongBuffer var1 = this.byteBuffer.asLongBuffer();
      var1.limit(1);
      return var1;
   }

   IntBuffer numEntriesBuffer() {
      this.byteBuffer.position(28);
      IntBuffer var1 = this.byteBuffer.asIntBuffer();
      var1.limit(1);
      return var1;
   }
}
