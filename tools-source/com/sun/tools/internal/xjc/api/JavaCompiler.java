package com.sun.tools.internal.xjc.api;

import java.util.Collection;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;

public interface JavaCompiler {
   J2SJAXBModel bind(Collection var1, Map var2, String var3, ProcessingEnvironment var4);
}
