package sun.jvmstat.monitor;

public interface ByteArrayMonitor extends Monitor {
   byte[] byteArrayValue();

   byte byteAt(int var1);
}
