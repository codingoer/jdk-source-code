package com.sun.tools.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class JavacompilerMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.tools.internal.ws.resources.javacompiler");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableJAVACOMPILER_CLASSPATH_ERROR(Object arg0) {
      return messageFactory.getMessage("javacompiler.classpath.error", new Object[]{arg0});
   }

   public static String JAVACOMPILER_CLASSPATH_ERROR(Object arg0) {
      return localizer.localize(localizableJAVACOMPILER_CLASSPATH_ERROR(arg0));
   }

   public static Localizable localizableJAVACOMPILER_NOSUCHMETHOD_ERROR(Object arg0) {
      return messageFactory.getMessage("javacompiler.nosuchmethod.error", new Object[]{arg0});
   }

   public static String JAVACOMPILER_NOSUCHMETHOD_ERROR(Object arg0) {
      return localizer.localize(localizableJAVACOMPILER_NOSUCHMETHOD_ERROR(arg0));
   }

   public static Localizable localizableJAVACOMPILER_ERROR(Object arg0) {
      return messageFactory.getMessage("javacompiler.error", new Object[]{arg0});
   }

   public static String JAVACOMPILER_ERROR(Object arg0) {
      return localizer.localize(localizableJAVACOMPILER_ERROR(arg0));
   }
}
