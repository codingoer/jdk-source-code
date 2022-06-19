package sun.jvmstat.perfdata.monitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.List;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;

public abstract class AbstractPerfDataBuffer {
   protected PerfDataBufferImpl impl;

   public int getLocalVmId() {
      return this.impl.getLocalVmId();
   }

   public byte[] getBytes() {
      return this.impl.getBytes();
   }

   public int getCapacity() {
      return this.impl.getCapacity();
   }

   public Monitor findByName(String var1) throws MonitorException {
      return this.impl.findByName(var1);
   }

   public List findByPattern(String var1) throws MonitorException {
      return this.impl.findByPattern(var1);
   }

   public MonitorStatus getMonitorStatus() throws MonitorException {
      return this.impl.getMonitorStatus();
   }

   public ByteBuffer getByteBuffer() {
      return this.impl.getByteBuffer();
   }

   protected void createPerfDataBuffer(ByteBuffer var1, int var2) throws MonitorException {
      int var3 = AbstractPerfDataBufferPrologue.getMajorVersion(var1);
      int var4 = AbstractPerfDataBufferPrologue.getMinorVersion(var1);
      String var5 = "sun.jvmstat.perfdata.monitor.v" + var3 + "_" + var4 + ".PerfDataBuffer";

      try {
         Class var6 = Class.forName(var5);
         Constructor var13 = var6.getConstructor(Class.forName("java.nio.ByteBuffer"), Integer.TYPE);
         this.impl = (PerfDataBufferImpl)var13.newInstance(var1, new Integer(var2));
      } catch (ClassNotFoundException var8) {
         throw new IllegalArgumentException("Could not find " + var5 + ": " + var8.getMessage(), var8);
      } catch (NoSuchMethodException var9) {
         throw new IllegalArgumentException("Expected constructor missing in " + var5 + ": " + var9.getMessage(), var9);
      } catch (IllegalAccessException var10) {
         throw new IllegalArgumentException("Unexpected constructor access in " + var5 + ": " + var10.getMessage(), var10);
      } catch (InstantiationException var11) {
         throw new IllegalArgumentException(var5 + "is abstract: " + var11.getMessage(), var11);
      } catch (InvocationTargetException var12) {
         Throwable var7 = var12.getCause();
         if (var7 instanceof MonitorException) {
            throw (MonitorException)var7;
         } else {
            throw new RuntimeException("Unexpected exception: " + var12.getMessage(), var12);
         }
      }
   }
}
