package com.sun.tools.internal.xjc.addon.accessors;

import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.BadCommandLineException;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.xml.sax.ErrorHandler;

public class PluginImpl extends Plugin {
   public String getOptionName() {
      return "Xpropertyaccessors";
   }

   public String getUsage() {
      return "  -Xpropertyaccessors :  Use XmlAccessType PROPERTY instead of FIELD for generated classes";
   }

   public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
      return 0;
   }

   public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
      Iterator var4 = model.getClasses().iterator();

      while(var4.hasNext()) {
         ClassOutline co = (ClassOutline)var4.next();
         Iterator ann = co.ref.annotations().iterator();

         while(ann.hasNext()) {
            try {
               JAnnotationUse a = (JAnnotationUse)ann.next();
               Field clazzField = a.getClass().getDeclaredField("clazz");
               clazzField.setAccessible(true);
               JClass cl = (JClass)clazzField.get(a);
               if (cl.equals(model.getCodeModel()._ref(XmlAccessorType.class))) {
                  a.param("value", (Enum)XmlAccessType.PROPERTY);
                  break;
               }
            } catch (IllegalArgumentException var10) {
               Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, (String)null, var10);
            } catch (IllegalAccessException var11) {
               Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, (String)null, var11);
            } catch (NoSuchFieldException var12) {
               Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, (String)null, var12);
            } catch (SecurityException var13) {
               Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, (String)null, var13);
            }
         }
      }

      return true;
   }
}
