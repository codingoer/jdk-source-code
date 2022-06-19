package sun.jvmstat.perfdata.monitor.protocol.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessController;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBuffer;
import sun.misc.Perf;

public class PerfDataBuffer extends AbstractPerfDataBuffer {
   private static final Perf perf = (Perf)AccessController.doPrivileged(new Perf.GetPerfAction());

   public PerfDataBuffer(VmIdentifier var1) throws MonitorException {
      try {
         ByteBuffer var2 = perf.attach(var1.getLocalVmId(), var1.getMode());
         this.createPerfDataBuffer(var2, var1.getLocalVmId());
      } catch (IllegalArgumentException var9) {
         try {
            String var3 = PerfDataFile.getTempDirectory() + "hsperfdata_" + Integer.toString(var1.getLocalVmId());
            File var4 = new File(var3);
            FileChannel var5 = (new RandomAccessFile(var4, "r")).getChannel();
            MappedByteBuffer var6 = var5.map(MapMode.READ_ONLY, 0L, (long)((int)var5.size()));
            var5.close();
            this.createPerfDataBuffer(var6, var1.getLocalVmId());
         } catch (FileNotFoundException var7) {
            throw new MonitorException(var1.getLocalVmId() + " not found", var9);
         } catch (IOException var8) {
            throw new MonitorException("Could not map 1.4.1 file for " + var1.getLocalVmId(), var8);
         }
      } catch (IOException var10) {
         throw new MonitorException("Could not attach to " + var1.getLocalVmId(), var10);
      }

   }
}
