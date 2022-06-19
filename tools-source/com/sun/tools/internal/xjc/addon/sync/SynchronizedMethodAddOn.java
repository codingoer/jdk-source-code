package com.sun.tools.internal.xjc.addon.sync;

import com.sun.codemodel.internal.JMethod;
import com.sun.tools.internal.xjc.BadCommandLineException;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import java.io.IOException;
import java.util.Iterator;
import org.xml.sax.ErrorHandler;

public class SynchronizedMethodAddOn extends Plugin {
   public String getOptionName() {
      return "Xsync-methods";
   }

   public String getUsage() {
      return "  -Xsync-methods     :  generate accessor methods with the 'synchronized' keyword";
   }

   public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
      return 0;
   }

   public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
      Iterator var4 = model.getClasses().iterator();

      while(var4.hasNext()) {
         ClassOutline co = (ClassOutline)var4.next();
         this.augument(co);
      }

      return true;
   }

   private void augument(ClassOutline co) {
      Iterator var2 = co.implClass.methods().iterator();

      while(var2.hasNext()) {
         JMethod m = (JMethod)var2.next();
         m.getMods().setSynchronized(true);
      }

   }
}
