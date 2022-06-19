package com.sun.tools.internal.xjc.addon.locator;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.BadCommandLineException;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.Locatable;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlTransient;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

public class SourceLocationAddOn extends Plugin {
   private static final String fieldName = "locator";

   public String getOptionName() {
      return "Xlocator";
   }

   public String getUsage() {
      return "  -Xlocator          :  enable source location support for generated code";
   }

   public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
      return 0;
   }

   public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
      Iterator var4 = outline.getClasses().iterator();

      while(var4.hasNext()) {
         ClassOutline ci = (ClassOutline)var4.next();
         JDefinedClass impl = ci.implClass;
         if (ci.getSuperClass() == null) {
            JVar $loc = impl.field(2, (Class)Locator.class, "locator");
            $loc.annotate(XmlLocation.class);
            $loc.annotate(XmlTransient.class);
            impl._implements(Locatable.class);
            impl.method(1, (Class)Locator.class, "sourceLocation").body()._return($loc);
            JMethod setter = impl.method(1, (Class)Void.TYPE, "setSourceLocation");
            JVar $newLoc = setter.param(Locator.class, "newLocator");
            setter.body().assign($loc, $newLoc);
         }
      }

      return true;
   }
}
