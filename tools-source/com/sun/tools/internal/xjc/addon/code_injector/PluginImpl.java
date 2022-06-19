package com.sun.tools.internal.xjc.addon.code_injector;

import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.model.CPluginCustomization;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.util.DOMUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.ErrorHandler;

public class PluginImpl extends Plugin {
   public String getOptionName() {
      return "Xinject-code";
   }

   public List getCustomizationURIs() {
      return Collections.singletonList("http://jaxb.dev.java.net/plugin/code-injector");
   }

   public boolean isCustomizationTagName(String nsUri, String localName) {
      return nsUri.equals("http://jaxb.dev.java.net/plugin/code-injector") && localName.equals("code");
   }

   public String getUsage() {
      return "  -Xinject-code      :  inject specified Java code fragments into the generated code";
   }

   public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
      Iterator var4 = model.getClasses().iterator();

      while(var4.hasNext()) {
         ClassOutline co = (ClassOutline)var4.next();
         CPluginCustomization c = co.target.getCustomizations().find("http://jaxb.dev.java.net/plugin/code-injector", "code");
         if (c != null) {
            c.markAsAcknowledged();
            String codeFragment = DOMUtils.getElementText(c.element);
            co.implClass.direct(codeFragment);
         }
      }

      return true;
   }
}
