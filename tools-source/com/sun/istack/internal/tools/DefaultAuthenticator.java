package com.sun.istack.internal.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.Authenticator.RequestorType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class DefaultAuthenticator extends Authenticator {
   private static DefaultAuthenticator instance;
   private static Authenticator systemAuthenticator = getCurrentAuthenticator();
   private String proxyUser;
   private String proxyPasswd;
   private final List authInfo = new ArrayList();
   private static int counter = 0;

   DefaultAuthenticator() {
      if (System.getProperty("http.proxyUser") != null) {
         this.proxyUser = System.getProperty("http.proxyUser");
      } else {
         this.proxyUser = System.getProperty("proxyUser");
      }

      if (System.getProperty("http.proxyPassword") != null) {
         this.proxyPasswd = System.getProperty("http.proxyPassword");
      } else {
         this.proxyPasswd = System.getProperty("proxyPassword");
      }

   }

   public static synchronized DefaultAuthenticator getAuthenticator() {
      if (instance == null) {
         instance = new DefaultAuthenticator();
         Authenticator.setDefault(instance);
      }

      ++counter;
      return instance;
   }

   public static synchronized void reset() {
      --counter;
      if (instance != null && counter == 0) {
         Authenticator.setDefault(systemAuthenticator);
      }

   }

   protected PasswordAuthentication getPasswordAuthentication() {
      if (this.getRequestorType() == RequestorType.PROXY && this.proxyUser != null && this.proxyPasswd != null) {
         return new PasswordAuthentication(this.proxyUser, this.proxyPasswd.toCharArray());
      } else {
         Iterator var1 = this.authInfo.iterator();

         AuthInfo auth;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            auth = (AuthInfo)var1.next();
         } while(!auth.matchingHost(this.getRequestingURL()));

         return new PasswordAuthentication(auth.getUser(), auth.getPassword().toCharArray());
      }
   }

   public void setProxyAuth(String proxyAuth) {
      if (proxyAuth == null) {
         this.proxyUser = null;
         this.proxyPasswd = null;
      } else {
         int i = proxyAuth.indexOf(58);
         if (i < 0) {
            this.proxyUser = proxyAuth;
            this.proxyPasswd = "";
         } else if (i == 0) {
            this.proxyUser = "";
            this.proxyPasswd = proxyAuth.substring(1);
         } else {
            this.proxyUser = proxyAuth.substring(0, i);
            this.proxyPasswd = proxyAuth.substring(i + 1);
         }
      }

   }

   public void setAuth(File f, Receiver l) {
      Receiver listener = l == null ? new DefaultRImpl() : l;
      BufferedReader in = null;
      FileInputStream fi = null;
      InputStreamReader is = null;

      try {
         LocatorImpl locator = new LocatorImpl();
         locator.setSystemId(f.getAbsolutePath());

         try {
            fi = new FileInputStream(f);
            is = new InputStreamReader(fi, "UTF-8");
            in = new BufferedReader(is);
         } catch (UnsupportedEncodingException var25) {
            ((Receiver)listener).onError(var25, locator);
            return;
         } catch (FileNotFoundException var26) {
            ((Receiver)listener).onError(var26, locator);
            return;
         }

         try {
            int lineno = 1;
            locator.setSystemId(f.getCanonicalPath());

            String text;
            while((text = in.readLine()) != null) {
               locator.setLineNumber(lineno++);
               if (!"".equals(text.trim()) && !text.startsWith("#")) {
                  try {
                     AuthInfo ai = this.parseLine(text);
                     this.authInfo.add(ai);
                  } catch (Exception var23) {
                     ((Receiver)listener).onParsingError(text, locator);
                  }
               }
            }

         } catch (IOException var24) {
            ((Receiver)listener).onError(var24, locator);
            Logger.getLogger(DefaultAuthenticator.class.getName()).log(Level.SEVERE, var24.getMessage(), var24);
         }
      } finally {
         try {
            if (in != null) {
               in.close();
            }

            if (is != null) {
               is.close();
            }

            if (fi != null) {
               fi.close();
            }
         } catch (IOException var22) {
            Logger.getLogger(DefaultAuthenticator.class.getName()).log(Level.SEVERE, (String)null, var22);
         }

      }
   }

   private AuthInfo parseLine(String text) throws Exception {
      URL url;
      int i;
      String password;
      try {
         url = new URL(text);
      } catch (MalformedURLException var7) {
         i = text.indexOf(58, text.indexOf(58) + 1) + 1;
         int j = text.lastIndexOf(64);
         password = text.substring(0, i) + URLEncoder.encode(text.substring(i, j), "UTF-8") + text.substring(j);
         url = new URL(password);
      }

      String authinfo = url.getUserInfo();
      if (authinfo != null) {
         i = authinfo.indexOf(58);
         if (i >= 0) {
            String user = authinfo.substring(0, i);
            password = authinfo.substring(i + 1);
            return new AuthInfo(new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile()), user, URLDecoder.decode(password, "UTF-8"));
         }
      }

      throw new Exception();
   }

   static Authenticator getCurrentAuthenticator() {
      final Field f = getTheAuthenticator();
      if (f == null) {
         return null;
      } else {
         Object var2;
         try {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Void run() {
                  f.setAccessible(true);
                  return null;
               }
            });
            Authenticator var1 = (Authenticator)f.get((Object)null);
            return var1;
         } catch (Exception var6) {
            var2 = null;
         } finally {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Void run() {
                  f.setAccessible(false);
                  return null;
               }
            });
         }

         return (Authenticator)var2;
      }
   }

   private static Field getTheAuthenticator() {
      try {
         return Authenticator.class.getDeclaredField("theAuthenticator");
      } catch (Exception var1) {
         return null;
      }
   }

   static final class AuthInfo {
      private final String user;
      private final String password;
      private final Pattern urlPattern;

      public AuthInfo(URL url, String user, String password) {
         String u = url.toExternalForm().replaceFirst("\\?", "\\\\?");
         this.urlPattern = Pattern.compile(u.replace("*", ".*"), 2);
         this.user = user;
         this.password = password;
      }

      public String getUser() {
         return this.user;
      }

      public String getPassword() {
         return this.password;
      }

      public boolean matchingHost(URL requestingURL) {
         return this.urlPattern.matcher(requestingURL.toExternalForm()).matches();
      }
   }

   private static class DefaultRImpl implements Receiver {
      private DefaultRImpl() {
      }

      public void onParsingError(String line, Locator loc) {
         System.err.println(this.getLocationString(loc) + ": " + line);
      }

      public void onError(Exception e, Locator loc) {
         System.err.println(this.getLocationString(loc) + ": " + e.getMessage());
         Logger.getLogger(DefaultAuthenticator.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      }

      private String getLocationString(Locator l) {
         return "[" + l.getSystemId() + "#" + l.getLineNumber() + "]";
      }

      // $FF: synthetic method
      DefaultRImpl(Object x0) {
         this();
      }
   }

   public interface Receiver {
      void onParsingError(String var1, Locator var2);

      void onError(Exception var1, Locator var2);
   }
}
