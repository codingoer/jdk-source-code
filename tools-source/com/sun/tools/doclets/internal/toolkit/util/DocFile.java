package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.tools.JavaFileManager;

public abstract class DocFile {
   private final Configuration configuration;
   protected final JavaFileManager.Location location;
   protected final DocPath path;

   public static DocFile createFileForDirectory(Configuration var0, String var1) {
      return DocFileFactory.getFactory(var0).createFileForDirectory(var1);
   }

   public static DocFile createFileForInput(Configuration var0, String var1) {
      return DocFileFactory.getFactory(var0).createFileForInput(var1);
   }

   public static DocFile createFileForOutput(Configuration var0, DocPath var1) {
      return DocFileFactory.getFactory(var0).createFileForOutput(var1);
   }

   public static Iterable list(Configuration var0, JavaFileManager.Location var1, DocPath var2) {
      return DocFileFactory.getFactory(var0).list(var1, var2);
   }

   protected DocFile(Configuration var1) {
      this.configuration = var1;
      this.location = null;
      this.path = null;
   }

   protected DocFile(Configuration var1, JavaFileManager.Location var2, DocPath var3) {
      this.configuration = var1;
      this.location = var2;
      this.path = var3;
   }

   public abstract InputStream openInputStream() throws IOException;

   public abstract OutputStream openOutputStream() throws IOException, UnsupportedEncodingException;

   public abstract Writer openWriter() throws IOException, UnsupportedEncodingException;

   public void copyFile(DocFile var1) throws IOException {
      InputStream var2 = var1.openInputStream();
      OutputStream var3 = this.openOutputStream();

      try {
         byte[] var4 = new byte[1024];

         int var5;
         while((var5 = var2.read(var4)) != -1) {
            var3.write(var4, 0, var5);
         }
      } catch (FileNotFoundException var10) {
      } catch (SecurityException var11) {
      } finally {
         var2.close();
         var3.close();
      }

   }

   public void copyResource(DocPath var1, boolean var2, boolean var3) {
      if (!this.exists() || var2) {
         try {
            InputStream var4 = Configuration.class.getResourceAsStream(var1.getPath());
            if (var4 != null) {
               OutputStream var5 = this.openOutputStream();

               try {
                  if (!var3) {
                     byte[] var6 = new byte[2048];

                     int var7;
                     while((var7 = var4.read(var6)) > 0) {
                        var5.write(var6, 0, var7);
                     }
                  } else {
                     BufferedReader var20 = new BufferedReader(new InputStreamReader(var4));
                     BufferedWriter var21;
                     if (this.configuration.docencoding == null) {
                        var21 = new BufferedWriter(new OutputStreamWriter(var5));
                     } else {
                        var21 = new BufferedWriter(new OutputStreamWriter(var5, this.configuration.docencoding));
                     }

                     String var8;
                     try {
                        while((var8 = var20.readLine()) != null) {
                           var21.write(var8);
                           var21.write(DocletConstants.NL);
                        }
                     } finally {
                        var20.close();
                        var21.close();
                     }
                  }
               } finally {
                  var4.close();
                  var5.close();
               }

            }
         } catch (IOException var19) {
            var19.printStackTrace(System.err);
            throw new DocletAbortException(var19);
         }
      }
   }

   public abstract boolean canRead();

   public abstract boolean canWrite();

   public abstract boolean exists();

   public abstract String getName();

   public abstract String getPath();

   public abstract boolean isAbsolute();

   public abstract boolean isDirectory();

   public abstract boolean isFile();

   public abstract boolean isSameFile(DocFile var1);

   public abstract Iterable list() throws IOException;

   public abstract boolean mkdirs();

   public abstract DocFile resolve(DocPath var1);

   public abstract DocFile resolve(String var1);

   public abstract DocFile resolveAgainst(JavaFileManager.Location var1);
}
