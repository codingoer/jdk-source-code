package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class PackageListWriter extends PrintWriter {
   private Configuration configuration;

   public PackageListWriter(Configuration var1) throws IOException {
      super(DocFile.createFileForOutput(var1, DocPaths.PACKAGE_LIST).openWriter());
      this.configuration = var1;
   }

   public static void generate(Configuration var0) {
      try {
         PackageListWriter var1 = new PackageListWriter(var0);
         var1.generatePackageListFile(var0.root);
         var1.close();
      } catch (IOException var3) {
         var0.message.error("doclet.exception_encountered", var3.toString(), DocPaths.PACKAGE_LIST);
         throw new DocletAbortException(var3);
      }
   }

   protected void generatePackageListFile(RootDoc var1) {
      PackageDoc[] var2 = this.configuration.packages;
      ArrayList var3 = new ArrayList();

      int var4;
      for(var4 = 0; var4 < var2.length; ++var4) {
         if (!this.configuration.nodeprecated || !Util.isDeprecated(var2[var4])) {
            var3.add(var2[var4].name());
         }
      }

      Collections.sort(var3);

      for(var4 = 0; var4 < var3.size(); ++var4) {
         this.println((String)var3.get(var4));
      }

   }
}
