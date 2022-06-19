package com.sun.codemodel.internal.writer;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FileCodeWriter extends CodeWriter {
   private final File target;
   private final boolean readOnly;
   private final Set readonlyFiles;

   public FileCodeWriter(File target) throws IOException {
      this(target, false);
   }

   public FileCodeWriter(File target, String encoding) throws IOException {
      this(target, false, encoding);
   }

   public FileCodeWriter(File target, boolean readOnly) throws IOException {
      this(target, readOnly, (String)null);
   }

   public FileCodeWriter(File target, boolean readOnly, String encoding) throws IOException {
      this.readonlyFiles = new HashSet();
      this.target = target;
      this.readOnly = readOnly;
      this.encoding = encoding;
      if (!target.exists() || !target.isDirectory()) {
         throw new IOException(target + ": non-existent directory");
      }
   }

   public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
      return new FileOutputStream(this.getFile(pkg, fileName));
   }

   protected File getFile(JPackage pkg, String fileName) throws IOException {
      File dir;
      if (pkg.isUnnamed()) {
         dir = this.target;
      } else {
         dir = new File(this.target, toDirName(pkg));
      }

      if (!dir.exists()) {
         dir.mkdirs();
      }

      File fn = new File(dir, fileName);
      if (fn.exists() && !fn.delete()) {
         throw new IOException(fn + ": Can't delete previous version");
      } else {
         if (this.readOnly) {
            this.readonlyFiles.add(fn);
         }

         return fn;
      }
   }

   public void close() throws IOException {
      Iterator var1 = this.readonlyFiles.iterator();

      while(var1.hasNext()) {
         File f = (File)var1.next();
         f.setReadOnly();
      }

   }

   private static String toDirName(JPackage pkg) {
      return pkg.name().replace('.', File.separatorChar);
   }
}
