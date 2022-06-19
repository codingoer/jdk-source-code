package com.sun.tools.javac.file;

import com.sun.tools.javac.util.BaseFileManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CharsetDecoder;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

public abstract class BaseFileObject implements JavaFileObject {
   protected final JavacFileManager fileManager;

   protected BaseFileObject(JavacFileManager var1) {
      this.fileManager = var1;
   }

   public abstract String getShortName();

   public String toString() {
      return this.getClass().getSimpleName() + "[" + this.getName() + "]";
   }

   public NestingKind getNestingKind() {
      return null;
   }

   public Modifier getAccessLevel() {
      return null;
   }

   public Reader openReader(boolean var1) throws IOException {
      return new InputStreamReader(this.openInputStream(), this.getDecoder(var1));
   }

   protected CharsetDecoder getDecoder(boolean var1) {
      throw new UnsupportedOperationException();
   }

   protected abstract String inferBinaryName(Iterable var1);

   protected static JavaFileObject.Kind getKind(String var0) {
      return BaseFileManager.getKind(var0);
   }

   protected static String removeExtension(String var0) {
      int var1 = var0.lastIndexOf(".");
      return var1 == -1 ? var0 : var0.substring(0, var1);
   }

   protected static URI createJarUri(File var0, String var1) {
      URI var2 = var0.toURI().normalize();
      String var3 = var1.startsWith("/") ? "!" : "!/";

      try {
         return new URI("jar:" + var2 + var3 + var1);
      } catch (URISyntaxException var5) {
         throw new CannotCreateUriError(var2 + var3 + var1, var5);
      }
   }

   public static String getSimpleName(FileObject var0) {
      URI var1 = var0.toUri();
      String var2 = var1.getSchemeSpecificPart();
      return var2.substring(var2.lastIndexOf("/") + 1);
   }

   public abstract boolean equals(Object var1);

   public abstract int hashCode();

   protected static class CannotCreateUriError extends Error {
      private static final long serialVersionUID = 9101708840997613546L;

      public CannotCreateUriError(String var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
