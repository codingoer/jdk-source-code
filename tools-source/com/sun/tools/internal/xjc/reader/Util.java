package com.sun.tools.internal.xjc.reader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.InputSource;

public class Util {
   public static Object getFileOrURL(String fileOrURL) throws IOException {
      try {
         return new URL(fileOrURL);
      } catch (MalformedURLException var2) {
         return (new File(fileOrURL)).getCanonicalFile();
      }
   }

   public static InputSource getInputSource(String fileOrURL) {
      try {
         Object o = getFileOrURL(fileOrURL);
         if (o instanceof URL) {
            return new InputSource(escapeSpace(((URL)o).toExternalForm()));
         } else {
            String url = ((File)o).toURL().toExternalForm();
            return new InputSource(escapeSpace(url));
         }
      } catch (IOException var3) {
         return new InputSource(fileOrURL);
      }
   }

   public static String escapeSpace(String url) {
      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < url.length(); ++i) {
         if (url.charAt(i) == ' ') {
            buf.append("%20");
         } else {
            buf.append(url.charAt(i));
         }
      }

      return buf.toString();
   }
}
