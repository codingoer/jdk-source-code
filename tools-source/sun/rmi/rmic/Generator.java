package sun.rmi.rmic;

import java.io.File;
import sun.tools.java.ClassDefinition;

public interface Generator {
   boolean parseArgs(String[] var1, Main var2);

   void generate(BatchEnvironment var1, ClassDefinition var2, File var3);
}
