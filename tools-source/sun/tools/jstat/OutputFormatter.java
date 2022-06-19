package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;

public interface OutputFormatter {
   String getHeader() throws MonitorException;

   String getRow() throws MonitorException;
}
