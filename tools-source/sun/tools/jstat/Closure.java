package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;

interface Closure {
   void visit(Object var1, boolean var2) throws MonitorException;
}
