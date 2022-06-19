package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageRetriever {
   private final Configuration configuration;
   private final String resourcelocation;
   private ResourceBundle messageRB;

   public MessageRetriever(ResourceBundle var1) {
      this.configuration = null;
      this.messageRB = var1;
      this.resourcelocation = null;
   }

   public MessageRetriever(Configuration var1, String var2) {
      this.configuration = var1;
      this.resourcelocation = var2;
   }

   public String getText(String var1, Object... var2) throws MissingResourceException {
      if (this.messageRB == null) {
         try {
            this.messageRB = ResourceBundle.getBundle(this.resourcelocation);
         } catch (MissingResourceException var4) {
            throw new Error("Fatal: Resource (" + this.resourcelocation + ") for javadoc doclets is missing.");
         }
      }

      String var3 = this.messageRB.getString(var1);
      return MessageFormat.format(var3, var2);
   }

   private void printError(SourcePosition var1, String var2) {
      this.configuration.root.printError(var1, var2);
   }

   private void printError(String var1) {
      this.configuration.root.printError(var1);
   }

   private void printWarning(SourcePosition var1, String var2) {
      this.configuration.root.printWarning(var1, var2);
   }

   private void printWarning(String var1) {
      this.configuration.root.printWarning(var1);
   }

   private void printNotice(SourcePosition var1, String var2) {
      this.configuration.root.printNotice(var1, var2);
   }

   private void printNotice(String var1) {
      this.configuration.root.printNotice(var1);
   }

   public void error(SourcePosition var1, String var2, Object... var3) {
      this.printError(var1, this.getText(var2, var3));
   }

   public void error(String var1, Object... var2) {
      this.printError(this.getText(var1, var2));
   }

   public void warning(SourcePosition var1, String var2, Object... var3) {
      if (this.configuration.showMessage(var1, var2)) {
         this.printWarning(var1, this.getText(var2, var3));
      }

   }

   public void warning(String var1, Object... var2) {
      this.printWarning(this.getText(var1, var2));
   }

   public void notice(SourcePosition var1, String var2, Object... var3) {
      this.printNotice(var1, this.getText(var2, var3));
   }

   public void notice(String var1, Object... var2) {
      this.printNotice(this.getText(var1, var2));
   }
}
