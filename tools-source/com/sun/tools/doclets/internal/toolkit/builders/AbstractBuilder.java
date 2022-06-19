package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.FatalError;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractBuilder {
   protected final Configuration configuration;
   protected final Set containingPackagesSeen;
   protected final LayoutParser layoutParser;
   protected static final boolean DEBUG = false;

   public AbstractBuilder(Context var1) {
      this.configuration = var1.configuration;
      this.containingPackagesSeen = var1.containingPackagesSeen;
      this.layoutParser = var1.layoutParser;
   }

   public abstract String getName();

   public abstract void build() throws IOException;

   protected void build(XMLNode var1, Content var2) {
      String var3 = var1.name;

      try {
         this.invokeMethod("build" + var3, new Class[]{XMLNode.class, Content.class}, new Object[]{var1, var2});
      } catch (NoSuchMethodException var6) {
         var6.printStackTrace();
         this.configuration.root.printError("Unknown element: " + var3);
         throw new DocletAbortException(var6);
      } catch (InvocationTargetException var7) {
         Throwable var5 = var7.getCause();
         if (var5 instanceof FatalError) {
            throw (FatalError)var5;
         } else if (var5 instanceof DocletAbortException) {
            throw (DocletAbortException)var5;
         } else {
            throw new DocletAbortException(var5);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         this.configuration.root.printError("Exception " + var8.getClass().getName() + " thrown while processing element: " + var3);
         throw new DocletAbortException(var8);
      }
   }

   protected void buildChildren(XMLNode var1, Content var2) {
      Iterator var3 = var1.children.iterator();

      while(var3.hasNext()) {
         XMLNode var4 = (XMLNode)var3.next();
         this.build(var4, var2);
      }

   }

   protected void invokeMethod(String var1, Class[] var2, Object[] var3) throws Exception {
      Method var4 = this.getClass().getMethod(var1, var2);
      var4.invoke(this, var3);
   }

   public static class Context {
      final Configuration configuration;
      final Set containingPackagesSeen;
      final LayoutParser layoutParser;

      Context(Configuration var1, Set var2, LayoutParser var3) {
         this.configuration = var1;
         this.containingPackagesSeen = var2;
         this.layoutParser = var3;
      }
   }
}
