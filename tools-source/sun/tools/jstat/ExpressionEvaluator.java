package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;

interface ExpressionEvaluator {
   Object evaluate(Expression var1) throws MonitorException;
}
