package sun.jvmstat.perfdata.monitor.v2_0;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Map;
import sun.jvmstat.monitor.IntegerMonitor;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;
import sun.jvmstat.perfdata.monitor.MonitorDataException;
import sun.jvmstat.perfdata.monitor.MonitorStatus;
import sun.jvmstat.perfdata.monitor.MonitorStructureException;
import sun.jvmstat.perfdata.monitor.MonitorTypeException;
import sun.jvmstat.perfdata.monitor.PerfDataBufferImpl;
import sun.jvmstat.perfdata.monitor.PerfIntegerMonitor;
import sun.jvmstat.perfdata.monitor.PerfLongMonitor;
import sun.jvmstat.perfdata.monitor.PerfStringConstantMonitor;
import sun.jvmstat.perfdata.monitor.PerfStringVariableMonitor;

public class PerfDataBuffer extends PerfDataBufferImpl {
   private static final boolean DEBUG = false;
   private static final int syncWaitMs = Integer.getInteger("sun.jvmstat.perdata.syncWaitMs", 5000);
   private static final ArrayList EMPTY_LIST = new ArrayList(0);
   private static final int PERFDATA_ENTRYLENGTH_OFFSET = 0;
   private static final int PERFDATA_ENTRYLENGTH_SIZE = 4;
   private static final int PERFDATA_NAMEOFFSET_OFFSET = 4;
   private static final int PERFDATA_NAMEOFFSET_SIZE = 4;
   private static final int PERFDATA_VECTORLENGTH_OFFSET = 8;
   private static final int PERFDATA_VECTORLENGTH_SIZE = 4;
   private static final int PERFDATA_DATATYPE_OFFSET = 12;
   private static final int PERFDATA_DATATYPE_SIZE = 1;
   private static final int PERFDATA_FLAGS_OFFSET = 13;
   private static final int PERFDATA_FLAGS_SIZE = 1;
   private static final int PERFDATA_DATAUNITS_OFFSET = 14;
   private static final int PERFDATA_DATAUNITS_SIZE = 1;
   private static final int PERFDATA_DATAVAR_OFFSET = 15;
   private static final int PERFDATA_DATAVAR_SIZE = 1;
   private static final int PERFDATA_DATAOFFSET_OFFSET = 16;
   private static final int PERFDATA_DATAOFFSET_SIZE = 4;
   PerfDataBufferPrologue prologue;
   int nextEntry;
   long lastNumEntries;
   IntegerMonitor overflow;
   ArrayList insertedMonitors;

   public PerfDataBuffer(ByteBuffer var1, int var2) throws MonitorException {
      super(var1, var2);
      this.prologue = new PerfDataBufferPrologue(var1);
      this.buffer.order(this.prologue.getByteOrder());
   }

   protected void buildMonitorMap(Map var1) throws MonitorException {
      assert Thread.holdsLock(this);

      this.buffer.rewind();
      this.buildPseudoMonitors(var1);
      this.synchWithTarget();
      this.nextEntry = this.prologue.getEntryOffset();
      int var2 = this.prologue.getNumEntries();

      for(Monitor var3 = this.getNextMonitorEntry(); var3 != null; var3 = this.getNextMonitorEntry()) {
         var1.put(var3.getName(), var3);
      }

      this.lastNumEntries = (long)var2;
      this.insertedMonitors = new ArrayList(var1.values());
   }

   protected void getNewMonitors(Map var1) throws MonitorException {
      assert Thread.holdsLock(this);

      int var2 = this.prologue.getNumEntries();
      if ((long)var2 > this.lastNumEntries) {
         this.lastNumEntries = (long)var2;

         for(Monitor var3 = this.getNextMonitorEntry(); var3 != null; var3 = this.getNextMonitorEntry()) {
            String var4 = var3.getName();
            if (!var1.containsKey(var4)) {
               var1.put(var4, var3);
               if (this.insertedMonitors != null) {
                  this.insertedMonitors.add(var3);
               }
            }
         }
      }

   }

   protected MonitorStatus getMonitorStatus(Map var1) throws MonitorException {
      assert Thread.holdsLock(this);

      assert this.insertedMonitors != null;

      this.getNewMonitors(var1);
      ArrayList var2 = EMPTY_LIST;
      ArrayList var3 = this.insertedMonitors;
      this.insertedMonitors = new ArrayList();
      return new MonitorStatus(var3, var2);
   }

   protected void buildPseudoMonitors(Map var1) {
      PerfIntegerMonitor var2 = null;
      String var3 = null;
      IntBuffer var4 = null;
      var3 = "sun.perfdata.majorVersion";
      var4 = this.prologue.majorVersionBuffer();
      var2 = new PerfIntegerMonitor(var3, Units.NONE, Variability.CONSTANT, false, var4);
      var1.put(var3, var2);
      var3 = "sun.perfdata.minorVersion";
      var4 = this.prologue.minorVersionBuffer();
      var2 = new PerfIntegerMonitor(var3, Units.NONE, Variability.CONSTANT, false, var4);
      var1.put(var3, var2);
      var3 = "sun.perfdata.size";
      var4 = this.prologue.sizeBuffer();
      var2 = new PerfIntegerMonitor(var3, Units.BYTES, Variability.MONOTONIC, false, var4);
      var1.put(var3, var2);
      var3 = "sun.perfdata.used";
      var4 = this.prologue.usedBuffer();
      var2 = new PerfIntegerMonitor(var3, Units.BYTES, Variability.MONOTONIC, false, var4);
      var1.put(var3, var2);
      var3 = "sun.perfdata.overflow";
      var4 = this.prologue.overflowBuffer();
      var2 = new PerfIntegerMonitor(var3, Units.BYTES, Variability.MONOTONIC, false, var4);
      var1.put(var3, var2);
      this.overflow = (IntegerMonitor)var2;
      var3 = "sun.perfdata.timestamp";
      LongBuffer var5 = this.prologue.modificationTimeStampBuffer();
      PerfLongMonitor var6 = new PerfLongMonitor(var3, Units.TICKS, Variability.MONOTONIC, false, var5);
      var1.put(var3, var6);
   }

   protected void synchWithTarget() throws MonitorException {
      long var1 = System.currentTimeMillis() + (long)syncWaitMs;
      this.log("synchWithTarget: " + this.lvmid + " ");

      while(!this.prologue.isAccessible()) {
         this.log(".");

         try {
            Thread.sleep(20L);
         } catch (InterruptedException var4) {
         }

         if (System.currentTimeMillis() > var1) {
            this.logln("failed: " + this.lvmid);
            throw new MonitorException("Could not synchronize with target");
         }
      }

      this.logln("success: " + this.lvmid);
   }

   protected Monitor getNextMonitorEntry() throws MonitorException {
      Object var1 = null;
      if (this.nextEntry % 4 != 0) {
         throw new MonitorStructureException("Misaligned entry index: " + Integer.toHexString(this.nextEntry));
      } else if (this.nextEntry >= 0 && this.nextEntry <= this.buffer.limit()) {
         if (this.nextEntry == this.buffer.limit()) {
            this.logln("getNextMonitorEntry(): nextEntry == buffer.limit(): returning");
            return null;
         } else {
            this.buffer.position(this.nextEntry);
            int var2 = this.buffer.position();
            int var3 = this.buffer.getInt();
            if (var3 >= 0 && var3 <= this.buffer.limit()) {
               if (var2 + var3 > this.buffer.limit()) {
                  throw new MonitorStructureException("Entry extends beyond end of buffer:  entryStart = 0x" + Integer.toHexString(var2) + " entryLength = 0x" + Integer.toHexString(var3) + " buffer limit = 0x" + Integer.toHexString(this.buffer.limit()));
               } else if (var3 == 0) {
                  return null;
               } else {
                  int var4 = this.buffer.getInt();
                  int var5 = this.buffer.getInt();
                  byte var6 = this.buffer.get();
                  byte var7 = this.buffer.get();
                  byte var8 = this.buffer.get();
                  byte var9 = this.buffer.get();
                  int var10 = this.buffer.getInt();
                  this.dump_entry_fixed(var2, var4, var5, var6, var7, var8, var9, var10);
                  Units var11 = Units.toUnits(var8);
                  Variability var12 = Variability.toVariability(var9);
                  TypeCode var13 = null;
                  boolean var14 = (var7 & 1) != 0;

                  try {
                     var13 = TypeCode.toTypeCode(var6);
                  } catch (IllegalArgumentException var22) {
                     throw new MonitorStructureException("Illegal type code encountered: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", type_code = " + Integer.toHexString(var6));
                  }

                  if (var4 > var3) {
                     throw new MonitorStructureException("Field extends beyond entry bounds entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", name_offset = 0x" + Integer.toHexString(var4));
                  } else if (var10 > var3) {
                     throw new MonitorStructureException("Field extends beyond entry bounds: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", data_offset = 0x" + Integer.toHexString(var10));
                  } else if (var12 == Variability.INVALID) {
                     throw new MonitorDataException("Invalid variability attribute: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", variability = 0x" + Integer.toHexString(var9));
                  } else if (var11 == Units.INVALID) {
                     throw new MonitorDataException("Invalid units attribute: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", units = 0x" + Integer.toHexString(var8));
                  } else {
                     assert this.buffer.position() == var2 + var4;

                     assert var10 > var4;

                     int var15 = var10 - var4;

                     assert var15 < var3;

                     byte[] var16 = new byte[var15];

                     int var17;
                     byte var18;
                     for(var17 = 0; (var18 = this.buffer.get()) != 0 && var17 < var15; var16[var17++] = var18) {
                     }

                     assert var17 < var15;

                     assert this.buffer.position() <= var2 + var10;

                     String var19 = new String(var16, 0, var17);
                     int var20 = var3 - var10;
                     this.buffer.position(var2 + var10);
                     this.dump_entry_variable(var19, this.buffer, var20);
                     if (var5 == 0) {
                        if (var13 != TypeCode.LONG) {
                           throw new MonitorTypeException("Unexpected type code encountered: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", name = " + var19 + ", type_code = " + var13 + " (0x" + Integer.toHexString(var6) + ")");
                        }

                        LongBuffer var21 = this.buffer.asLongBuffer();
                        var21.limit(1);
                        var1 = new PerfLongMonitor(var19, var11, var12, var14, var21);
                     } else {
                        if (var13 != TypeCode.BYTE) {
                           throw new MonitorTypeException("Unexpected type code encountered: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + ", name = " + var19 + ", type_code = " + var13 + " (0x" + Integer.toHexString(var6) + ")");
                        }

                        if (var11 != Units.STRING) {
                           throw new MonitorTypeException("Unexpected vector type encounterd: entry_offset = " + Integer.toHexString(this.nextEntry) + ", name = " + var19 + ", type_code = " + var13 + " (0x" + Integer.toHexString(var6) + "), units = " + var11 + " (0x" + Integer.toHexString(var8) + ")");
                        }

                        ByteBuffer var23 = this.buffer.slice();
                        var23.limit(var5);
                        if (var12 == Variability.CONSTANT) {
                           var1 = new PerfStringConstantMonitor(var19, var14, var23);
                        } else if (var12 == Variability.VARIABLE) {
                           var1 = new PerfStringVariableMonitor(var19, var14, var23, var5 - 1);
                        } else {
                           if (var12 == Variability.MONOTONIC) {
                              throw new MonitorDataException("Unexpected variability attribute: entry_offset = 0x" + Integer.toHexString(this.nextEntry) + " name = " + var19 + ", variability = " + var12 + " (0x" + Integer.toHexString(var9) + ")");
                           }

                           assert false;
                        }
                     }

                     this.nextEntry = var2 + var3;
                     return (Monitor)var1;
                  }
               }
            } else {
               throw new MonitorStructureException("Invalid entry length: entryLength = " + var3 + " (0x" + Integer.toHexString(var3) + ")");
            }
         }
      } else {
         throw new MonitorStructureException("Entry index out of bounds: " + Integer.toHexString(this.nextEntry) + ", limit = " + Integer.toHexString(this.buffer.limit()));
      }
   }

   private void dumpAll(Map var1, int var2) {
   }

   private void dump_entry_fixed(int var1, int var2, int var3, byte var4, byte var5, byte var6, byte var7, int var8) {
   }

   private void dump_entry_variable(String var1, ByteBuffer var2, int var3) {
   }

   private void logln(String var1) {
   }

   private void log(String var1) {
   }
}
