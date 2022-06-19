package com.sun.tools.internal.ws.wscompile.plugin.at_generated;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.ws.ToolVersion;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.Plugin;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wscompile.WsimportTool;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.xml.sax.SAXException;

public final class PluginImpl extends Plugin {
   private JClass annotation;
   private String date = null;

   public String getOptionName() {
      return "mark-generated";
   }

   public String getUsage() {
      return "  -mark-generated    :  mark the generated code as @javax.annotation.Generated";
   }

   public boolean run(Model model, WsimportOptions wo, ErrorReceiver er) throws SAXException {
      JCodeModel cm = wo.getCodeModel();
      this.annotation = cm.ref("javax.annotation.Generated");
      Iterator i = cm.packages();

      while(i.hasNext()) {
         Iterator j = ((JPackage)i.next()).classes();

         while(j.hasNext()) {
            this.annotate((JAnnotatable)j.next());
         }
      }

      return true;
   }

   private void annotate(JAnnotatable m) {
      m.annotate(this.annotation).param("value", WsimportTool.class.getName()).param("date", this.getISO8601Date()).param("comments", ToolVersion.VERSION.BUILD_VERSION);
   }

   private String getISO8601Date() {
      if (this.date == null) {
         StringBuilder tstamp = new StringBuilder();
         tstamp.append((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")).format(new Date()));
         tstamp.insert(tstamp.length() - 2, ':');
         this.date = tstamp.toString();
      }

      return this.date;
   }
}
