package com.sun.istack.internal.tools;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelWorldClassLoader extends ClassLoader implements Closeable {
   private final String prefix;
   private final Set jars;

   public ParallelWorldClassLoader(ClassLoader parent, String prefix) {
      super(parent);
      this.prefix = prefix;
      this.jars = Collections.synchronizedSet(new HashSet());
   }

   protected Class findClass(String name) throws ClassNotFoundException {
      StringBuffer sb = new StringBuffer(name.length() + this.prefix.length() + 6);
      sb.append(this.prefix).append(name.replace('.', '/')).append(".class");
      URL u = this.getParent().getResource(sb.toString());
      if (u == null) {
         throw new ClassNotFoundException(name);
      } else {
         InputStream is = null;
         URLConnection con = null;

         try {
            con = u.openConnection();
            is = con.getInputStream();
         } catch (IOException var23) {
            throw new ClassNotFoundException(name);
         }

         if (is == null) {
            throw new ClassNotFoundException(name);
         } else {
            Class var26;
            try {
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               byte[] buf = new byte[1024];

               int len;
               while((len = is.read(buf)) >= 0) {
                  baos.write(buf, 0, len);
               }

               buf = baos.toByteArray();
               int packIndex = name.lastIndexOf(46);
               if (packIndex != -1) {
                  String pkgname = name.substring(0, packIndex);
                  Package pkg = this.getPackage(pkgname);
                  if (pkg == null) {
                     this.definePackage(pkgname, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                  }
               }

               var26 = this.defineClass(name, buf, 0, buf.length);
            } catch (IOException var24) {
               throw new ClassNotFoundException(name, var24);
            } finally {
               try {
                  if (con != null && con instanceof JarURLConnection) {
                     this.jars.add(((JarURLConnection)con).getJarFile());
                  }
               } catch (IOException var22) {
               }

               if (is != null) {
                  try {
                     is.close();
                  } catch (IOException var21) {
                  }
               }

            }

            return var26;
         }
      }
   }

   protected URL findResource(String name) {
      URL u = this.getParent().getResource(this.prefix + name);
      if (u != null) {
         try {
            this.jars.add(new JarFile(new File(toJarUrl(u).toURI())));
         } catch (URISyntaxException var4) {
            Logger.getLogger(ParallelWorldClassLoader.class.getName()).log(Level.WARNING, (String)null, var4);
         } catch (IOException var5) {
            Logger.getLogger(ParallelWorldClassLoader.class.getName()).log(Level.WARNING, (String)null, var5);
         } catch (ClassNotFoundException var6) {
         }
      }

      return u;
   }

   protected Enumeration findResources(String name) throws IOException {
      Enumeration en = this.getParent().getResources(this.prefix + name);

      while(en.hasMoreElements()) {
         try {
            this.jars.add(new JarFile(new File(toJarUrl((URL)en.nextElement()).toURI())));
         } catch (URISyntaxException var4) {
            Logger.getLogger(ParallelWorldClassLoader.class.getName()).log(Level.WARNING, (String)null, var4);
         } catch (IOException var5) {
            Logger.getLogger(ParallelWorldClassLoader.class.getName()).log(Level.WARNING, (String)null, var5);
         } catch (ClassNotFoundException var6) {
         }
      }

      return en;
   }

   public synchronized void close() throws IOException {
      Iterator var1 = this.jars.iterator();

      while(var1.hasNext()) {
         JarFile jar = (JarFile)var1.next();
         jar.close();
      }

   }

   public static URL toJarUrl(URL res) throws ClassNotFoundException, MalformedURLException {
      String url = res.toExternalForm();
      if (!url.startsWith("jar:")) {
         throw new ClassNotFoundException("Loaded outside a jar " + url);
      } else {
         url = url.substring(4);
         url = url.substring(0, url.lastIndexOf(33));
         url = url.replaceAll(" ", "%20");
         return new URL(url);
      }
   }
}
