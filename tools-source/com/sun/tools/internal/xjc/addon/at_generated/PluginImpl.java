package com.sun.tools.internal.xjc.addon.at_generated;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JMethod;
import com.sun.tools.internal.xjc.Driver;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.EnumOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.xml.sax.ErrorHandler;

public class PluginImpl extends Plugin {
   private JClass annotation;
   private String date = null;

   public String getOptionName() {
      return "mark-generated";
   }

   public String getUsage() {
      return "  -mark-generated    :  mark the generated code as @javax.annotation.Generated";
   }

   public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
      this.annotation = model.getCodeModel().ref("javax.annotation.Generated");
      Iterator var4 = model.getClasses().iterator();

      while(var4.hasNext()) {
         ClassOutline co = (ClassOutline)var4.next();
         this.augument(co);
      }

      var4 = model.getEnums().iterator();

      while(var4.hasNext()) {
         EnumOutline eo = (EnumOutline)var4.next();
         this.augument(eo);
      }

      return true;
   }

   private void augument(EnumOutline eo) {
      this.annotate(eo.clazz);
   }

   private void augument(ClassOutline co) {
      this.annotate(co.implClass);
      Iterator var2 = co.implClass.methods().iterator();

      while(var2.hasNext()) {
         JMethod m = (JMethod)var2.next();
         this.annotate(m);
      }

      var2 = co.implClass.fields().values().iterator();

      while(var2.hasNext()) {
         JFieldVar f = (JFieldVar)var2.next();
         this.annotate(f);
      }

   }

   private void annotate(JAnnotatable m) {
      m.annotate(this.annotation).param("value", Driver.class.getName()).param("date", this.getISO8601Date()).param("comments", "JAXB RI v" + Options.getBuildID());
   }

   private String getISO8601Date() {
      if (this.date == null) {
         StringBuffer tstamp = new StringBuffer();
         tstamp.append((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")).format(new Date()));
         tstamp.insert(tstamp.length() - 2, ':');
         this.date = tstamp.toString();
      }

      return this.date;
   }
}
