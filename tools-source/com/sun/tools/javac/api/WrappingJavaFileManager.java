package com.sun.tools.javac.api;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class WrappingJavaFileManager extends ForwardingJavaFileManager {
   protected WrappingJavaFileManager(JavaFileManager var1) {
      super(var1);
   }

   protected FileObject wrap(FileObject var1) {
      return var1;
   }

   protected JavaFileObject wrap(JavaFileObject var1) {
      return (JavaFileObject)this.wrap((FileObject)var1);
   }

   protected FileObject unwrap(FileObject var1) {
      return var1;
   }

   protected JavaFileObject unwrap(JavaFileObject var1) {
      return (JavaFileObject)this.unwrap((FileObject)var1);
   }

   protected Iterable wrap(Iterable var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JavaFileObject var4 = (JavaFileObject)var3.next();
         var2.add(this.wrap(var4));
      }

      return Collections.unmodifiableList(var2);
   }

   protected URI unwrap(URI var1) {
      return var1;
   }

   public Iterable list(JavaFileManager.Location var1, String var2, Set var3, boolean var4) throws IOException {
      return this.wrap(super.list(var1, var2, var3, var4));
   }

   public String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2) {
      return super.inferBinaryName(var1, this.unwrap(var2));
   }

   public JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException {
      return this.wrap(super.getJavaFileForInput(var1, var2, var3));
   }

   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
      return this.wrap(super.getJavaFileForOutput(var1, var2, var3, this.unwrap(var4)));
   }

   public FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException {
      return this.wrap(super.getFileForInput(var1, var2, var3));
   }

   public FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException {
      return this.wrap(super.getFileForOutput(var1, var2, var3, this.unwrap(var4)));
   }
}
