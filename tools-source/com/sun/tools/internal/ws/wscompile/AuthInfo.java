package com.sun.tools.internal.ws.wscompile;

import com.sun.istack.internal.NotNull;
import java.net.URL;
import java.util.regex.Pattern;

public final class AuthInfo {
   private final String user;
   private final String password;
   private final Pattern urlPattern;

   public AuthInfo(@NotNull URL url, @NotNull String user, @NotNull String password) {
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

   public boolean matchingHost(@NotNull URL requestingURL) {
      return this.urlPattern.matcher(requestingURL.toExternalForm()).matches();
   }
}
