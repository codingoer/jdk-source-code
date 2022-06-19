package com.sun.tools.javac.api;

import java.util.Locale;
import java.util.MissingResourceException;

public interface Messages {
   void add(String var1) throws MissingResourceException;

   String getLocalizedString(Locale var1, String var2, Object... var3);
}
