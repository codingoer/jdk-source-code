package sun.jvmstat.monitor;

import java.net.URI;
import java.net.URISyntaxException;

public class VmIdentifier {
   private URI uri;

   private URI canonicalize(String var1) throws URISyntaxException {
      if (var1 == null) {
         var1 = "local://0@localhost";
         return new URI(var1);
      } else {
         URI var2 = new URI(var1);
         if (var2.isAbsolute()) {
            if (var2.isOpaque()) {
               var2 = new URI(var2.getScheme(), "//" + var2.getSchemeSpecificPart(), var2.getFragment());
            }
         } else if (!var1.startsWith("//")) {
            if (var2.getFragment() == null) {
               var2 = new URI("//" + var2.getSchemeSpecificPart());
            } else {
               var2 = new URI("//" + var2.getSchemeSpecificPart() + "#" + var2.getFragment());
            }
         }

         return var2;
      }
   }

   private void validate() throws URISyntaxException {
      String var1 = this.getScheme();
      if (var1 == null || var1.compareTo("file") != 0) {
         if (this.getLocalVmId() == -1) {
            throw new URISyntaxException(this.uri.toString(), "Local vmid required");
         }
      }
   }

   public VmIdentifier(String var1) throws URISyntaxException {
      URI var2;
      try {
         var2 = this.canonicalize(var1);
      } catch (URISyntaxException var4) {
         if (var1.startsWith("//")) {
            throw var4;
         }

         var2 = this.canonicalize("//" + var1);
      }

      this.uri = var2;
      this.validate();
   }

   public VmIdentifier(URI var1) throws URISyntaxException {
      this.uri = var1;
      this.validate();
   }

   public HostIdentifier getHostIdentifier() throws URISyntaxException {
      StringBuffer var1 = new StringBuffer();
      if (this.getScheme() != null) {
         var1.append(this.getScheme()).append(":");
      }

      var1.append("//").append(this.getHost());
      if (this.getPort() != -1) {
         var1.append(":").append(this.getPort());
      }

      if (this.getPath() != null) {
         var1.append(this.getPath());
      }

      return new HostIdentifier(var1.toString());
   }

   public String getScheme() {
      return this.uri.getScheme();
   }

   public String getSchemeSpecificPart() {
      return this.uri.getSchemeSpecificPart();
   }

   public String getUserInfo() {
      return this.uri.getUserInfo();
   }

   public String getHost() {
      return this.uri.getHost();
   }

   public int getPort() {
      return this.uri.getPort();
   }

   public String getAuthority() {
      return this.uri.getAuthority();
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

   public int getLocalVmId() {
      int var1 = -1;

      try {
         if (this.uri.getUserInfo() == null) {
            var1 = Integer.parseInt(this.uri.getAuthority());
         } else {
            var1 = Integer.parseInt(this.uri.getUserInfo());
         }
      } catch (NumberFormatException var3) {
      }

      return var1;
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
         return !(var1 instanceof VmIdentifier) ? false : this.uri.equals(((VmIdentifier)var1).uri);
      }
   }

   public String toString() {
      return this.uri.toString();
   }
}
