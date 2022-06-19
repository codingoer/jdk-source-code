package sun.jvmstat.perfdata.monitor.protocol.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBuffer;

public class PerfDataBuffer extends AbstractPerfDataBuffer {
   public PerfDataBuffer(VmIdentifier var1) throws MonitorException {
      File var2 = new File(var1.getURI());
      String var3 = var1.getMode();

      try {
         FileChannel var4 = (new RandomAccessFile(var2, var3)).getChannel();
         MappedByteBuffer var5 = null;
         if (var3.compareTo("r") == 0) {
            var5 = var4.map(MapMode.READ_ONLY, 0L, (long)((int)var4.size()));
         } else {
            if (var3.compareTo("rw") != 0) {
               throw new IllegalArgumentException("Invalid mode: " + var3);
            }

            var5 = var4.map(MapMode.READ_WRITE, 0L, (long)((int)var4.size()));
         }

         var4.close();
         this.createPerfDataBuffer(var5, 0);
      } catch (FileNotFoundException var6) {
         throw new MonitorException("Could not find " + var1.toString());
      } catch (IOException var7) {
         throw new MonitorException("Could not read " + var1.toString());
      }
   }
}
