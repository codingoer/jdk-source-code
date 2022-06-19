package sun.jvmstat.perfdata.monitor.v1_0;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Map;
import sun.jvmstat.monitor.IntegerMonitor;
import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.StringMonitor;
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
   private static final int PERFDATA_NAMELENGTH_OFFSET = 4;
   private static final int PERFDATA_NAMELENGTH_SIZE = 4;
   private static final int PERFDATA_VECTORLENGTH_OFFSET = 8;
   private static final int PERFDATA_VECTORLENGTH_SIZE = 4;
   private static final int PERFDATA_DATATYPE_OFFSET = 12;
   private static final int PERFDATA_DATATYPE_SIZE = 1;
   private static final int PERFDATA_FLAGS_OFFSET = 13;
   private static final int PERFDATA_FLAGS_SIZE = 1;
   private static final int PERFDATA_DATAUNITS_OFFSET = 14;
   private static final int PERFDATA_DATAUNITS_SIZE = 1;
   private static final int PERFDATA_DATAATTR_OFFSET = 15;
   private static final int PERFDATA_DATAATTR_SIZE = 1;
   private static final int PERFDATA_NAME_OFFSET = 16;
   PerfDataBufferPrologue prologue;
   int nextEntry;
   int pollForEntry;
   int perfDataItem;
   long lastModificationTime;
   int lastUsed;
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
      this.buffer.position(this.prologue.getSize());
      this.nextEntry = this.buffer.position();
      this.perfDataItem = 0;
      int var2 = this.prologue.getUsed();
      long var3 = this.prologue.getModificationTimeStamp();

      for(Monitor var5 = this.getNextMonitorEntry(); var5 != null; var5 = this.getNextMonitorEntry()) {
         var1.put(var5.getName(), var5);
      }

      this.lastUsed = var2;
      this.lastModificationTime = var3;
      this.synchWithTarget(var1);
      this.kludge(var1);
      this.insertedMonitors = new ArrayList(var1.values());
   }

   protected void getNewMonitors(Map var1) throws MonitorException {
      assert Thread.holdsLock(this);

      int var2 = this.prologue.getUsed();
      long var3 = this.prologue.getModificationTimeStamp();
      if (var2 > this.lastUsed || this.lastModificationTime > var3) {
         this.lastUsed = var2;
         this.lastModificationTime = var3;

         for(Monitor var5 = this.getNextMonitorEntry(); var5 != null; var5 = this.getNextMonitorEntry()) {
            String var6 = var5.getName();
            if (!var1.containsKey(var6)) {
               var1.put(var6, var5);
               if (this.insertedMonitors != null) {
                  this.insertedMonitors.add(var5);
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

   protected void synchWithTarget(Map var1) throws MonitorException {
      long var2 = System.currentTimeMillis() + (long)syncWaitMs;
      String var4 = "hotspot.rt.hrt.ticks";
      LongMonitor var5 = (LongMonitor)this.pollFor(var1, var4, var2);
      this.log("synchWithTarget: " + this.lvmid + " ");

      while(var5.longValue() == 0L) {
         this.log(".");

         try {
            Thread.sleep(20L);
         } catch (InterruptedException var7) {
         }

         if (System.currentTimeMillis() > var2) {
            this.lognl("failed: " + this.lvmid);
            throw new MonitorException("Could Not Synchronize with target");
         }
      }

      this.lognl("success: " + this.lvmid);
   }

   protected Monitor pollFor(Map var1, String var2, long var3) throws MonitorException {
      Monitor var5 = null;
      this.log("polling for: " + this.lvmid + "," + var2 + " ");
      this.pollForEntry = this.nextEntry;

      while(true) {
         if ((var5 = (Monitor)var1.get(var2)) == null) {
            this.log(".");

            try {
               Thread.sleep(20L);
            } catch (InterruptedException var8) {
            }

            long var6 = System.currentTimeMillis();
            if (var6 <= var3 && this.overflow.intValue() <= 0) {
               this.getNewMonitors(var1);
               continue;
            }

            this.lognl("failed: " + this.lvmid + "," + var2);
            this.dumpAll(var1, this.lvmid);
            throw new MonitorException("Could not find expected counter");
         }

         this.lognl("success: " + this.lvmid + "," + var2);
         return var5;
      }
   }

   protected void kludge(Map var1) {
      if (!Boolean.getBoolean("sun.jvmstat.perfdata.disableKludge")) {
         String var2 = "java.vm.version";
         StringMonitor var3 = (StringMonitor)var1.get(var2);
         if (var3 == null) {
            var3 = (StringMonitor)this.findByAlias(var2);
         }

         var2 = "java.vm.name";
         StringMonitor var4 = (StringMonitor)var1.get(var2);
         if (var4 == null) {
            var4 = (StringMonitor)this.findByAlias(var2);
         }

         var2 = "hotspot.vm.args";
         StringMonitor var5 = (StringMonitor)var1.get(var2);
         if (var5 == null) {
            var5 = (StringMonitor)this.findByAlias(var2);
         }

         assert var4 != null && var3 != null && var5 != null;

         if (var4.stringValue().indexOf("HotSpot") >= 0 && var3.stringValue().startsWith("1.4.2")) {
            this.kludgeMantis(var1, var5);
         }

      }
   }

   private void kludgeMantis(Map var1, StringMonitor var2) {
      String var3 = "hotspot.gc.collector.0.name";
      StringMonitor var4 = (StringMonitor)var1.get(var3);
      if (var4.stringValue().compareTo("PSScavenge") == 0) {
         boolean var5 = true;
         var3 = "hotspot.vm.flags";
         StringMonitor var6 = (StringMonitor)var1.get(var3);
         String var7 = var6.stringValue() + " " + var2.stringValue();
         int var8 = var7.lastIndexOf("+AggressiveHeap");
         int var9 = var7.lastIndexOf("-UseAdaptiveSizePolicy");
         if (var8 != -1) {
            if (var9 != -1 && var9 > var8) {
               var5 = false;
            }
         } else if (var9 != -1) {
            var5 = false;
         }

         if (var5) {
            String var10 = "hotspot.gc.generation.0.space.0.size";
            String var11 = "hotspot.gc.generation.0.space.1.size";
            String var12 = "hotspot.gc.generation.0.space.2.size";
            var1.remove(var10);
            var1.remove(var11);
            var1.remove(var12);
            String var13 = "hotspot.gc.generation.0.capacity.max";
            LongMonitor var14 = (LongMonitor)var1.get(var13);
            PerfLongMonitor var15 = null;
            LongBuffer var16 = LongBuffer.allocate(1);
            var16.put(var14.longValue());
            var15 = new PerfLongMonitor(var10, Units.BYTES, Variability.CONSTANT, false, var16);
            var1.put(var10, var15);
            var15 = new PerfLongMonitor(var11, Units.BYTES, Variability.CONSTANT, false, var16);
            var1.put(var11, var15);
            var15 = new PerfLongMonitor(var12, Units.BYTES, Variability.CONSTANT, false, var16);
            var1.put(var12, var15);
         }
      }

   }

   protected Monitor getNextMonitorEntry() throws MonitorException {
      Object var1 = null;
      if (this.nextEntry % 4 != 0) {
         throw new MonitorStructureException("Entry index not properly aligned: " + this.nextEntry);
      } else if (this.nextEntry >= 0 && this.nextEntry <= this.buffer.limit()) {
         if (this.nextEntry == this.buffer.limit()) {
            this.lognl("getNextMonitorEntry(): nextEntry == buffer.limit(): returning");
            return null;
         } else {
            this.buffer.position(this.nextEntry);
            int var2 = this.buffer.position();
            int var3 = this.buffer.getInt();
            if (var3 >= 0 && var3 <= this.buffer.limit()) {
               if (var2 + var3 > this.buffer.limit()) {
                  throw new MonitorStructureException("Entry extends beyond end of buffer:  entryStart = " + var2 + " entryLength = " + var3 + " buffer limit = " + this.buffer.limit());
               } else if (var3 == 0) {
                  return null;
               } else {
                  int var4 = this.buffer.getInt();
                  int var5 = this.buffer.getInt();
                  byte var6 = this.buffer.get();
                  byte var7 = this.buffer.get();
                  Units var8 = Units.toUnits(this.buffer.get());
                  Variability var9 = Variability.toVariability(this.buffer.get());
                  boolean var10 = (var7 & 1) != 0;
                  if (var4 > 0 && var4 <= var3) {
                     if (var5 >= 0 && var5 <= var3) {
                        byte[] var11 = new byte[var4 - 1];

                        for(int var12 = 0; var12 < var4 - 1; ++var12) {
                           var11[var12] = this.buffer.get();
                        }

                        String var15 = new String(var11, 0, var4 - 1);
                        if (var9 == Variability.INVALID) {
                           throw new MonitorDataException("Invalid variability attribute: entry index = " + this.perfDataItem + " name = " + var15);
                        } else if (var8 == Units.INVALID) {
                           throw new MonitorDataException("Invalid units attribute:  entry index = " + this.perfDataItem + " name = " + var15);
                        } else {
                           int var13;
                           if (var5 == 0) {
                              if (var6 != BasicType.LONG.intValue()) {
                                 throw new MonitorTypeException("Invalid Monitor type: entry index = " + this.perfDataItem + " name = " + var15 + " type = " + var6);
                              }

                              var13 = var2 + var3 - 8;
                              this.buffer.position(var13);
                              LongBuffer var14 = this.buffer.asLongBuffer();
                              var14.limit(1);
                              var1 = new PerfLongMonitor(var15, var8, var9, var10, var14);
                              ++this.perfDataItem;
                           } else {
                              if (var6 != BasicType.BYTE.intValue()) {
                                 throw new MonitorTypeException("Invalid Monitor type: entry index = " + this.perfDataItem + " name = " + var15 + " type = " + var6);
                              }

                              if (var8 != Units.STRING) {
                                 throw new MonitorTypeException("Invalid Monitor type: entry index = " + this.perfDataItem + " name = " + var15 + " type = " + var6);
                              }

                              var13 = var2 + 16 + var4;
                              this.buffer.position(var13);
                              ByteBuffer var16 = this.buffer.slice();
                              var16.limit(var5);
                              var16.position(0);
                              if (var9 == Variability.CONSTANT) {
                                 var1 = new PerfStringConstantMonitor(var15, var10, var16);
                              } else {
                                 if (var9 != Variability.VARIABLE) {
                                    throw new MonitorDataException("Invalid variability attribute: entry index = " + this.perfDataItem + " name = " + var15 + " variability = " + var9);
                                 }

                                 var1 = new PerfStringVariableMonitor(var15, var10, var16, var5 - 1);
                              }

                              ++this.perfDataItem;
                           }

                           this.nextEntry = var2 + var3;
                           return (Monitor)var1;
                        }
                     } else {
                        throw new MonitorStructureException("Invalid Monitor vector length: " + var5);
                     }
                  } else {
                     throw new MonitorStructureException("Invalid Monitor name length: " + var4);
                  }
               }
            } else {
               throw new MonitorStructureException("Invalid entry length: entryLength = " + var3);
            }
         }
      } else {
         throw new MonitorStructureException("Entry index out of bounds: nextEntry = " + this.nextEntry + ", limit = " + this.buffer.limit());
      }
   }

   private void dumpAll(Map var1, int var2) {
   }

   private void lognl(String var1) {
   }

   private void log(String var1) {
   }
}
