package sun.jvmstat.monitor;

import java.net.URI;
import java.net.URISyntaxException;

public class HostIdentifier {
   private URI uri;

   private URI canonicalize(String var1) throws URISyntaxException {
      if (var1 != null && var1.compareTo("localhost") != 0) {
         URI var2 = new URI(var1);
         String var3;
         if (var2.isAbsolute()) {
            if (var2.isOpaque()) {
               var3 = var2.getScheme();
               String var4 = var2.getSchemeSpecificPart();
               String var5 = var2.getFragment();
               URI var6 = null;
               int var7 = var1.indexOf(":");
               int var8 = var1.lastIndexOf(":");
               if (var8 != var7) {
                  if (var5 == null) {
                     var6 = new URI(var3 + "://" + var4);
                  } else {
                     var6 = new URI(var3 + "://" + var4 + "#" + var5);
                  }

                  return var6;
               } else {
                  var6 = new URI("//" + var1);
                  return var6;
               }
            } else {
               return var2;
            }
         } else {
            var3 = var2.getSchemeSpecificPart();
            return var3.startsWith("//") ? var2 : new URI("//" + var1);
         }
      } else {
         var1 = "//localhost";
         return new URI(var1);
      }
   }

   public HostIdentifier(String var1) throws URISyntaxException {
      this.uri = this.canonicalize(var1);
   }

   public HostIdentifier(String var1, String var2, String var3, String var4, String var5) throws URISyntaxException {
      this.uri = new URI(var1, var2, var3, var4, var5);
   }

   public HostIdentifier(VmIdentifier var1) {
      StringBuilder var2 = new StringBuilder();
      String var3 = var1.getScheme();
      String var4 = var1.getHost();
      String var5 = var1.getAuthority();
      if (var3 != null && var3.compareTo("file") == 0) {
         try {
            this.uri = new URI("file://localhost");
         } catch (URISyntaxException var11) {
         }

      } else {
         if (var4 != null && var4.compareTo(var5) == 0) {
            var4 = null;
         }

         if (var3 == null) {
            if (var4 == null) {
               var3 = "local";
            } else {
               var3 = "rmi";
            }
         }

         var2.append(var3).append("://");
         if (var4 == null) {
            var2.append("localhost");
         } else {
            var2.append(var4);
         }

         int var6 = var1.getPort();
         if (var6 != -1) {
            var2.append(":").append(var6);
         }

         String var7 = var1.getPath();
         if (var7 != null && var7.length() != 0) {
            var2.append(var7);
         }

         String var8 = var1.getQuery();
         if (var8 != null) {
            var2.append("?").append(var8);
         }

         String var9 = var1.getFragment();
         if (var9 != null) {
            var2.append("#").append(var9);
         }

         try {
            this.uri = new URI(var2.toString());
         } catch (URISyntaxException var12) {
            throw new RuntimeException("Internal Error", var12);
         }
      }
   }

   public VmIdentifier resolve(VmIdentifier var1) throws URISyntaxException, MonitorException {
      String var2 = var1.getScheme();
      String var3 = var1.getHost();
      String var4 = var1.getAuthority();
      if (var2 != null && var2.compareTo("file") == 0) {
         return var1;
      } else {
         if (var3 != null && var3.compareTo(var4) == 0) {
            var3 = null;
         }

         if (var2 == null) {
            var2 = this.getScheme();
         }

         Object var5 = null;
         StringBuffer var6 = new StringBuffer();
         var6.append(var2).append("://");
         String var7 = var1.getUserInfo();
         if (var7 != null) {
            var6.append(var7);
         } else {
            var6.append(var1.getAuthority());
         }

         if (var3 == null) {
            var3 = this.getHost();
         }

         var6.append("@").append(var3);
         int var8 = var1.getPort();
         if (var8 == -1) {
            var8 = this.getPort();
         }

         if (var8 != -1) {
            var6.append(":").append(var8);
         }

         String var9 = var1.getPath();
         if (var9 == null || var9.length() == 0) {
            var9 = this.getPath();
         }

         if (var9 != null && var9.length() > 0) {
            var6.append(var9);
         }

         String var10 = var1.getQuery();
         if (var10 == null) {
            var10 = this.getQuery();
         }

         if (var10 != null) {
            var6.append("?").append(var10);
         }

         String var11 = var1.getFragment();
         if (var11 == null) {
            var11 = this.getFragment();
         }

         if (var11 != null) {
            var6.append("#").append(var11);
         }

         String var12 = var6.toString();
         return new VmIdentifier(var12);
      }
   }

   public String getScheme() {
      return this.uri.isAbsolute() ? this.uri.getScheme() : null;
   }

   public String getSchemeSpecificPart() {
      return this.uri.getSchemeSpecificPart();
   }

   public String getUserInfo() {
      return this.uri.getUserInfo();
   }

   public String getHost() {
      return this.uri.getHost() == null ? "localhost" : this.uri.getHost();
   }

   public int getPort() {
      return this.uri.getPort();
   }

   public String getPath() {
      return this.uri.getPath();
   }

   public String getQuery() {
      return this.uri.getQuery();
   }

   public String getFragment() {
      return this.uri.getFragment();
   }

   public String getMode() {
      String var1 = this.getQuery();
      if (var1 != null) {
         String[] var2 = var1.split("\\+");

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].startsWith("mode=")) {
               int var4 = var2[var3].indexOf(61);
               return var2[var3].substring(var4 + 1);
            }
         }
      }

      return "r";
   }

   public URI getURI() {
      return this.uri;
   }

   public int hashCode() {
      return this.uri.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof HostIdentifier) ? false : this.uri.equals(((HostIdentifier)var1).uri);
      }
   }

   public String toString() {
      return this.uri.toString();
   }
}
