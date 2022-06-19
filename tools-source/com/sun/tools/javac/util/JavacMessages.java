package com.sun.tools.javac.util;

import com.sun.tools.javac.api.Messages;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class JavacMessages implements Messages {
   public static final Context.Key messagesKey = new Context.Key();
   private Map bundleCache;
   private List bundleNames;
   private Locale currentLocale;
   private List currentBundles;
   private static final String defaultBundleName = "com.sun.tools.javac.resources.compiler";
   private static ResourceBundle defaultBundle;
   private static JavacMessages defaultMessages;

   public static JavacMessages instance(Context var0) {
      JavacMessages var1 = (JavacMessages)var0.get(messagesKey);
      if (var1 == null) {
         var1 = new JavacMessages(var0);
      }

      return var1;
   }

   public Locale getCurrentLocale() {
      return this.currentLocale;
   }

   public void setCurrentLocale(Locale var1) {
      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      this.currentBundles = this.getBundles(var1);
      this.currentLocale = var1;
   }

   public JavacMessages(Context var1) {
      this("com.sun.tools.javac.resources.compiler", (Locale)var1.get(Locale.class));
      var1.put((Context.Key)messagesKey, (Object)this);
   }

   public JavacMessages(String var1) throws MissingResourceException {
      this(var1, (Locale)null);
   }

   public JavacMessages(String var1, Locale var2) throws MissingResourceException {
      this.bundleNames = List.nil();
      this.bundleCache = new HashMap();
      this.add(var1);
      this.setCurrentLocale(var2);
   }

   public JavacMessages() throws MissingResourceException {
      this("com.sun.tools.javac.resources.compiler");
   }

   public void add(String var1) throws MissingResourceException {
      this.bundleNames = this.bundleNames.prepend(var1);
      if (!this.bundleCache.isEmpty()) {
         this.bundleCache.clear();
      }

      this.currentBundles = null;
   }

   public List getBundles(Locale var1) {
      if (var1 == this.currentLocale && this.currentBundles != null) {
         return this.currentBundles;
      } else {
         SoftReference var2 = (SoftReference)this.bundleCache.get(var1);
         List var3 = var2 == null ? null : (List)var2.get();
         if (var3 == null) {
            var3 = List.nil();
            Iterator var4 = this.bundleNames.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();

               try {
                  ResourceBundle var6 = ResourceBundle.getBundle(var5, var1);
                  var3 = var3.prepend(var6);
               } catch (MissingResourceException var7) {
                  throw new InternalError("Cannot find javac resource bundle for locale " + var1);
               }
            }

            this.bundleCache.put(var1, new SoftReference(var3));
         }

         return var3;
      }
   }

   public String getLocalizedString(String var1, Object... var2) {
      return this.getLocalizedString(this.currentLocale, var1, var2);
   }

   public String getLocalizedString(Locale var1, String var2, Object... var3) {
      if (var1 == null) {
         var1 = this.getCurrentLocale();
      }

      return getLocalizedString(this.getBundles(var1), var2, var3);
   }

   static String getDefaultLocalizedString(String var0, Object... var1) {
      return getLocalizedString(List.of(getDefaultBundle()), var0, var1);
   }

   /** @deprecated */
   @Deprecated
   static JavacMessages getDefaultMessages() {
      if (defaultMessages == null) {
         defaultMessages = new JavacMessages("com.sun.tools.javac.resources.compiler");
      }

      return defaultMessages;
   }

   public static ResourceBundle getDefaultBundle() {
      try {
         if (defaultBundle == null) {
            defaultBundle = ResourceBundle.getBundle("com.sun.tools.javac.resources.compiler");
         }

         return defaultBundle;
      } catch (MissingResourceException var1) {
         throw new Error("Fatal: Resource for compiler is missing", var1);
      }
   }

   private static String getLocalizedString(List var0, String var1, Object... var2) {
      String var3 = null;

      for(List var4 = var0; var4.nonEmpty() && var3 == null; var4 = var4.tail) {
         ResourceBundle var5 = (ResourceBundle)var4.head;

         try {
            var3 = var5.getString(var1);
         } catch (MissingResourceException var7) {
         }
      }

      if (var3 == null) {
         var3 = "compiler message file broken: key=" + var1 + " arguments={0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}";
      }

      return MessageFormat.format(var3, var2);
   }
}
