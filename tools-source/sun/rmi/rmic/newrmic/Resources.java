package sun.rmi.rmic.newrmic;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Resources {
   private static ResourceBundle resources = null;
   private static ResourceBundle resourcesExt = null;

   private Resources() {
      throw new AssertionError();
   }

   public static String getText(String var0, String... var1) {
      String var2 = getString(var0);
      if (var2 == null) {
         var2 = "missing resource key: key = \"" + var0 + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
      }

      return MessageFormat.format(var2, (Object[])var1);
   }

   private static String getString(String var0) {
      if (resourcesExt != null) {
         try {
            return resourcesExt.getString(var0);
         } catch (MissingResourceException var3) {
         }
      }

      if (resources != null) {
         try {
            return resources.getString(var0);
         } catch (MissingResourceException var2) {
            return null;
         }
      } else {
         return "missing resource bundle: key = \"" + var0 + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
      }
   }

   static {
      try {
         resources = ResourceBundle.getBundle("sun.rmi.rmic.resources.rmic");
      } catch (MissingResourceException var2) {
      }

      try {
         resourcesExt = ResourceBundle.getBundle("sun.rmi.rmic.resources.rmicext");
      } catch (MissingResourceException var1) {
      }

   }
}
