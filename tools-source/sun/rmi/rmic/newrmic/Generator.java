package sun.rmi.rmic.newrmic;

import com.sun.javadoc.ClassDoc;
import java.io.File;
import java.util.Set;

public interface Generator {
   boolean parseArgs(String[] var1, Main var2);

   Class envClass();

   Set bootstrapClassNames();

   void generate(BatchEnvironment var1, ClassDoc var2, File var3);
}
