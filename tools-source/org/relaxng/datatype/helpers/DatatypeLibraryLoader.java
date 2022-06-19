package org.relaxng.datatype.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class DatatypeLibraryLoader implements DatatypeLibraryFactory {
   private final Service service = new Service(DatatypeLibraryFactory.class);

   public DatatypeLibrary createDatatypeLibrary(String uri) {
      Enumeration e = this.service.getProviders();

      DatatypeLibrary library;
      do {
         if (!e.hasMoreElements()) {
            return null;
         }

         DatatypeLibraryFactory factory = (DatatypeLibraryFactory)e.nextElement();
         library = factory.createDatatypeLibrary(uri);
      } while(library == null);

      return library;
   }

   private static class Service {
      private final Class serviceClass;
      private final Enumeration configFiles;
      private Enumeration classNames = null;
      private final Vector providers = new Vector();
      private Loader loader;
      private static final int START = 0;
      private static final int IN_NAME = 1;
      private static final int IN_COMMENT = 2;

      public Service(Class cls) {
         try {
            this.loader = new Loader2();
         } catch (NoSuchMethodError var3) {
            this.loader = new Loader();
         }

         this.serviceClass = cls;
         String resName = "META-INF/services/" + this.serviceClass.getName();
         this.configFiles = this.loader.getResources(resName);
      }

      public Enumeration getProviders() {
         return new ProviderEnumeration();
      }

      private synchronized boolean moreProviders() {
         while(true) {
            if (this.classNames == null) {
               if (!this.configFiles.hasMoreElements()) {
                  return false;
               }

               this.classNames = parseConfigFile((URL)this.configFiles.nextElement());
            } else {
               while(this.classNames.hasMoreElements()) {
                  String className = (String)this.classNames.nextElement();

                  try {
                     Class cls = this.loader.loadClass(className);
                     Object obj = cls.newInstance();
                     if (this.serviceClass.isInstance(obj)) {
                        this.providers.addElement(obj);
                        return true;
                     }
                  } catch (ClassNotFoundException var4) {
                  } catch (InstantiationException var5) {
                  } catch (IllegalAccessException var6) {
                  } catch (LinkageError var7) {
                  }
               }

               this.classNames = null;
            }
         }
      }

      private static Enumeration parseConfigFile(URL url) {
         try {
            InputStream in = url.openStream();

            InputStreamReader r;
            try {
               r = new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException var8) {
               r = new InputStreamReader(in, "UTF8");
            }

            Reader r = new BufferedReader(r);
            Vector tokens = new Vector();
            StringBuffer tokenBuf = new StringBuffer();
            int state = 0;

            while(true) {
               int n = r.read();
               if (n < 0) {
                  if (tokenBuf.length() != 0) {
                     tokens.addElement(tokenBuf.toString());
                  }

                  return tokens.elements();
               }

               char c = (char)n;
               switch (c) {
                  case '\t':
                  case ' ':
                     break;
                  case '\n':
                  case '\r':
                     state = 0;
                     break;
                  case '#':
                     state = 2;
                     break;
                  default:
                     if (state != 2) {
                        state = 1;
                        tokenBuf.append(c);
                     }
               }

               if (tokenBuf.length() != 0 && state != 1) {
                  tokens.addElement(tokenBuf.toString());
                  tokenBuf.setLength(0);
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }

      private static class Loader2 extends Loader {
         private ClassLoader cl = Loader2.class.getClassLoader();

         Loader2() {
            super(null);
            ClassLoader clt = Thread.currentThread().getContextClassLoader();

            for(ClassLoader tem = clt; tem != null; tem = tem.getParent()) {
               if (tem == this.cl) {
                  this.cl = clt;
                  break;
               }
            }

         }

         Enumeration getResources(String resName) {
            try {
               return this.cl.getResources(resName);
            } catch (IOException var3) {
               return new Singleton((Object)null);
            }
         }

         Class loadClass(String name) throws ClassNotFoundException {
            return Class.forName(name, true, this.cl);
         }
      }

      private static class Loader {
         private Loader() {
         }

         Enumeration getResources(String resName) {
            ClassLoader cl = Loader.class.getClassLoader();
            URL url;
            if (cl == null) {
               url = ClassLoader.getSystemResource(resName);
            } else {
               url = cl.getResource(resName);
            }

            return new Singleton(url);
         }

         Class loadClass(String name) throws ClassNotFoundException {
            return Class.forName(name);
         }

         // $FF: synthetic method
         Loader(Object x0) {
            this();
         }
      }

      private static class Singleton implements Enumeration {
         private Object obj;

         private Singleton(Object obj) {
            this.obj = obj;
         }

         public boolean hasMoreElements() {
            return this.obj != null;
         }

         public Object nextElement() {
            if (this.obj == null) {
               throw new NoSuchElementException();
            } else {
               Object tem = this.obj;
               this.obj = null;
               return tem;
            }
         }

         // $FF: synthetic method
         Singleton(Object x0, Object x1) {
            this(x0);
         }
      }

      private class ProviderEnumeration implements Enumeration {
         private int nextIndex;

         private ProviderEnumeration() {
            this.nextIndex = 0;
         }

         public boolean hasMoreElements() {
            return this.nextIndex < Service.this.providers.size() || Service.this.moreProviders();
         }

         public Object nextElement() {
            try {
               return Service.this.providers.elementAt(this.nextIndex++);
            } catch (ArrayIndexOutOfBoundsException var2) {
               throw new NoSuchElementException();
            }
         }

         // $FF: synthetic method
         ProviderEnumeration(Object x1) {
            this();
         }
      }
   }
}
