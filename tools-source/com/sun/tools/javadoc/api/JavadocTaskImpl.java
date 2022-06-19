package com.sun.tools.javadoc.api;

import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javadoc.Start;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.tools.DocumentationTool;

public class JavadocTaskImpl implements DocumentationTool.DocumentationTask {
   private final AtomicBoolean used = new AtomicBoolean();
   private final Context context;
   private Class docletClass;
   private Iterable options;
   private Iterable fileObjects;
   private Locale locale;

   public JavadocTaskImpl(Context var1, Class var2, Iterable var3, Iterable var4) {
      this.context = var1;
      this.docletClass = var2;
      this.options = (Iterable)(var3 == null ? Collections.emptySet() : nullCheck(var3));
      this.fileObjects = (Iterable)(var4 == null ? Collections.emptySet() : nullCheck(var4));
      this.setLocale(Locale.getDefault());
   }

   public void setLocale(Locale var1) {
      if (this.used.get()) {
         throw new IllegalStateException();
      } else {
         this.locale = var1;
      }
   }

   public Boolean call() {
      if (!this.used.getAndSet(true)) {
         this.initContext();
         Start var1 = new Start(this.context);

         try {
            return var1.begin(this.docletClass, this.options, this.fileObjects);
         } catch (ClientCodeException var3) {
            throw new RuntimeException(var3.getCause());
         }
      } else {
         throw new IllegalStateException("multiple calls to method 'call'");
      }
   }

   private void initContext() {
      this.context.put((Class)Locale.class, (Object)this.locale);
   }

   private static Iterable nullCheck(Iterable var0) {
      Iterator var1 = var0.iterator();

      Object var2;
      do {
         if (!var1.hasNext()) {
            return var0;
         }

         var2 = var1.next();
      } while(var2 != null);

      throw new NullPointerException();
   }
}
