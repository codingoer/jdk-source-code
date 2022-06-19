package sun.rmi.rmic.newrmic.jrmp;

import com.sun.javadoc.ClassDoc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sun.rmi.rmic.newrmic.BatchEnvironment;
import sun.rmi.rmic.newrmic.Generator;
import sun.rmi.rmic.newrmic.IndentingWriter;
import sun.rmi.rmic.newrmic.Main;
import sun.rmi.rmic.newrmic.Resources;

public class JrmpGenerator implements Generator {
   private static final Map versionOptions = new HashMap();
   private static final Set bootstrapClassNames;
   private Constants.StubVersion version;

   public JrmpGenerator() {
      this.version = Constants.StubVersion.V1_2;
   }

   public boolean parseArgs(String[] var1, Main var2) {
      String var3 = null;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         String var5 = var1[var4];
         if (versionOptions.containsKey(var5)) {
            if (var3 != null && !var3.equals(var5)) {
               var2.error("rmic.cannot.use.both", var3, var5);
               return false;
            }

            var3 = var5;
            this.version = (Constants.StubVersion)versionOptions.get(var5);
            var1[var4] = null;
         }
      }

      return true;
   }

   public Class envClass() {
      return BatchEnvironment.class;
   }

   public Set bootstrapClassNames() {
      return Collections.unmodifiableSet(bootstrapClassNames);
   }

   public void generate(BatchEnvironment var1, ClassDoc var2, File var3) {
      RemoteClass var4 = RemoteClass.forClass(var1, var2);
      if (var4 != null) {
         StubSkeletonWriter var5 = new StubSkeletonWriter(var1, var4, this.version);
         File var6 = this.sourceFileForClass(var5.stubClassName(), var3);

         try {
            IndentingWriter var7 = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(var6)));
            var5.writeStub(var7);
            var7.close();
            if (var1.verbose()) {
               var1.output(Resources.getText("rmic.wrote", var6.getPath()));
            }

            var1.addGeneratedFile(var6);
         } catch (IOException var10) {
            var1.error("rmic.cant.write", var6.toString());
            return;
         }

         File var11 = this.sourceFileForClass(var5.skeletonClassName(), var3);
         if (this.version != Constants.StubVersion.V1_1 && this.version != Constants.StubVersion.VCOMPAT) {
            File var12 = this.classFileForClass(var5.skeletonClassName(), var3);
            var11.delete();
            var12.delete();
         } else {
            try {
               IndentingWriter var8 = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(var11)));
               var5.writeSkeleton(var8);
               var8.close();
               if (var1.verbose()) {
                  var1.output(Resources.getText("rmic.wrote", var11.getPath()));
               }

               var1.addGeneratedFile(var11);
            } catch (IOException var9) {
               var1.error("rmic.cant.write", var11.toString());
               return;
            }
         }

      }
   }

   private File sourceFileForClass(String var1, File var2) {
      return this.fileForClass(var1, var2, ".java");
   }

   private File classFileForClass(String var1, File var2) {
      return this.fileForClass(var1, var2, ".class");
   }

   private File fileForClass(String var1, File var2, String var3) {
      int var4 = var1.lastIndexOf(46);
      String var5 = var1.substring(var4 + 1) + var3;
      if (var4 != -1) {
         String var6 = var1.substring(0, var4);
         String var7 = var6.replace('.', File.separatorChar);
         File var8 = new File(var2, var7);
         if (!var8.exists()) {
            var8.mkdirs();
         }

         return new File(var8, var5);
      } else {
         return new File(var2, var5);
      }
   }

   static {
      versionOptions.put("-v1.1", Constants.StubVersion.V1_1);
      versionOptions.put("-vcompat", Constants.StubVersion.VCOMPAT);
      versionOptions.put("-v1.2", Constants.StubVersion.V1_2);
      bootstrapClassNames = new HashSet();
      bootstrapClassNames.add("java.lang.Exception");
      bootstrapClassNames.add("java.rmi.Remote");
      bootstrapClassNames.add("java.rmi.RemoteException");
      bootstrapClassNames.add("java.lang.RuntimeException");
   }
}
