package com.sun.tools.javac.nio;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;

public interface PathFileManager extends JavaFileManager {
   FileSystem getDefaultFileSystem();

   void setDefaultFileSystem(FileSystem var1);

   Iterable getJavaFileObjectsFromPaths(Iterable var1);

   Iterable getJavaFileObjects(Path... var1);

   Path getPath(FileObject var1);

   Iterable getLocation(JavaFileManager.Location var1);

   void setLocation(JavaFileManager.Location var1, Iterable var2) throws IOException;
}
