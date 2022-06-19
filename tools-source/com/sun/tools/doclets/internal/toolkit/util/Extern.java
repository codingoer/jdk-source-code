package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.tools.DocumentationTool.Location;

public class Extern {
   private Map packageToItemMap;
   private final Configuration configuration;
   private boolean linkoffline = false;

   public Extern(Configuration var1) {
      this.configuration = var1;
   }

   public boolean isExternal(ProgramElementDoc var1) {
      if (this.packageToItemMap == null) {
         return false;
      } else {
         return this.packageToItemMap.get(var1.containingPackage().name()) != null;
      }
   }

   public DocLink getExternalLink(String var1, DocPath var2, String var3) {
      return this.getExternalLink(var1, var2, var3, (String)null);
   }

   public DocLink getExternalLink(String var1, DocPath var2, String var3, String var4) {
      Item var5 = this.findPackageItem(var1);
      if (var5 == null) {
         return null;
      } else {
         DocPath var6 = var5.relative ? var2.resolve(var5.path).resolve(var3) : DocPath.create(var5.path).resolve(var3);
         return new DocLink(var6, "is-external=true", var4);
      }
   }

   public boolean link(String var1, String var2, DocErrorReporter var3, boolean var4) {
      this.linkoffline = var4;

      try {
         var1 = this.adjustEndFileSeparator(var1);
         if (this.isUrl(var2)) {
            this.readPackageListFromURL(var1, this.toURL(this.adjustEndFileSeparator(var2)));
         } else {
            this.readPackageListFromFile(var1, DocFile.createFileForInput(this.configuration, var2));
         }

         return true;
      } catch (Fault var6) {
         var3.printWarning(var6.getMessage());
         return false;
      }
   }

   private URL toURL(String var1) throws Fault {
      try {
         return new URL(var1);
      } catch (MalformedURLException var3) {
         throw new Fault(this.configuration.getText("doclet.MalformedURL", var1), var3);
      }
   }

   private Item findPackageItem(String var1) {
      return this.packageToItemMap == null ? null : (Item)this.packageToItemMap.get(var1);
   }

   private String adjustEndFileSeparator(String var1) {
      return var1.endsWith("/") ? var1 : var1 + '/';
   }

   private void readPackageListFromURL(String var1, URL var2) throws Fault {
      try {
         URL var3 = var2.toURI().resolve(DocPaths.PACKAGE_LIST.getPath()).toURL();
         this.readPackageList(var3.openStream(), var1, false);
      } catch (URISyntaxException var4) {
         throw new Fault(this.configuration.getText("doclet.MalformedURL", var2.toString()), var4);
      } catch (MalformedURLException var5) {
         throw new Fault(this.configuration.getText("doclet.MalformedURL", var2.toString()), var5);
      } catch (IOException var6) {
         throw new Fault(this.configuration.getText("doclet.URL_error", var2.toString()), var6);
      }
   }

   private void readPackageListFromFile(String var1, DocFile var2) throws Fault {
      DocFile var3 = var2.resolve(DocPaths.PACKAGE_LIST);
      if (!var3.isAbsolute() && !this.linkoffline) {
         var3 = var3.resolveAgainst(Location.DOCUMENTATION_OUTPUT);
      }

      try {
         if (var3.exists() && var3.canRead()) {
            boolean var4 = !DocFile.createFileForInput(this.configuration, var1).isAbsolute() && !this.isUrl(var1);
            this.readPackageList(var3.openInputStream(), var1, var4);
         } else {
            throw new Fault(this.configuration.getText("doclet.File_error", var3.getPath()), (Exception)null);
         }
      } catch (IOException var5) {
         throw new Fault(this.configuration.getText("doclet.File_error", var3.getPath()), var5);
      }
   }

   private void readPackageList(InputStream var1, String var2, boolean var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new InputStreamReader(var1));
      StringBuilder var5 = new StringBuilder();

      int var6;
      try {
         while((var6 = var4.read()) >= 0) {
            char var7 = (char)var6;
            if (var7 != '\n' && var7 != '\r') {
               var5.append(var7);
            } else if (var5.length() > 0) {
               String var8 = var5.toString();
               String var9 = var2 + var8.replace('.', '/') + '/';
               new Item(var8, var9, var3);
               var5.setLength(0);
            }
         }
      } finally {
         var1.close();
      }

   }

   public boolean isUrl(String var1) {
      try {
         new URL(var1);
         return true;
      } catch (MalformedURLException var3) {
         return false;
      }
   }

   private class Fault extends Exception {
      private static final long serialVersionUID = 0L;

      Fault(String var2, Exception var3) {
         super(var2, var3);
      }
   }

   private class Item {
      final String packageName;
      final String path;
      final boolean relative;

      Item(String var2, String var3, boolean var4) {
         this.packageName = var2;
         this.path = var3;
         this.relative = var4;
         if (Extern.this.packageToItemMap == null) {
            Extern.this.packageToItemMap = new HashMap();
         }

         if (!Extern.this.packageToItemMap.containsKey(var2)) {
            Extern.this.packageToItemMap.put(var2, this);
         }

      }

      public String toString() {
         return this.packageName + (this.relative ? " -> " : " => ") + this.path;
      }
   }
}
