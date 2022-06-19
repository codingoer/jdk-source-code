package sun.tools.serialver;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class Res {
   private static ResourceBundle messageRB;

   static void initResource() {
      try {
         messageRB = ResourceBundle.getBundle("sun.tools.serialver.resources.serialver");
      } catch (MissingResourceException var1) {
         throw new Error("Fatal: Resource for serialver is missing");
      }
   }

   static String getText(String var0) {
      return getText(var0, (String)null);
   }

   static String getText(String var0, String var1) {
      return getText(var0, var1, (String)null);
   }

   static String getText(String var0, String var1, String var2) {
      return getText(var0, var1, var2, (String)null);
   }

   static String getText(String var0, String var1, String var2, String var3) {
      if (messageRB == null) {
         initResource();
      }

      try {
         String var4 = messageRB.getString(var0);
         return MessageFormat.format(var4, var1, var2, var3);
      } catch (MissingResourceException var5) {
         throw new Error("Fatal: Resource for serialver is broken. There is no " + var0 + " key in resource.");
      }
   }
}
