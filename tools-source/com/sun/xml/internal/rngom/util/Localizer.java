package com.sun.xml.internal.rngom.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer {
   private final Class cls;
   private ResourceBundle bundle;
   private final Localizer parent;

   public Localizer(Class cls) {
      this((Localizer)null, cls);
   }

   public Localizer(Localizer parent, Class cls) {
      this.parent = parent;
      this.cls = cls;
   }

   private String getString(String key) {
      try {
         return this.getBundle().getString(key);
      } catch (MissingResourceException var3) {
         if (this.parent != null) {
            return this.parent.getString(key);
         } else {
            throw var3;
         }
      }
   }

   public String message(String key) {
      return MessageFormat.format(this.getString(key));
   }

   public String message(String key, Object arg) {
      return MessageFormat.format(this.getString(key), arg);
   }

   public String message(String key, Object arg1, Object arg2) {
      return MessageFormat.format(this.getString(key), arg1, arg2);
   }

   public String message(String key, Object[] args) {
      return MessageFormat.format(this.getString(key), args);
   }

   private ResourceBundle getBundle() {
      if (this.bundle == null) {
         String s = this.cls.getName();
         int i = s.lastIndexOf(46);
         if (i > 0) {
            s = s.substring(0, i + 1);
         } else {
            s = "";
         }

         this.bundle = ResourceBundle.getBundle(s + "Messages");
      }

      return this.bundle;
   }
}
