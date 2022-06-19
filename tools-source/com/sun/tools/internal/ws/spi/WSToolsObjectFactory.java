package com.sun.tools.internal.ws.spi;

import com.sun.tools.internal.ws.util.WSToolsObjectFactoryImpl;
import com.sun.xml.internal.ws.api.server.Container;
import java.io.OutputStream;

public abstract class WSToolsObjectFactory {
   private static final WSToolsObjectFactory factory = new WSToolsObjectFactoryImpl();

   public static WSToolsObjectFactory newInstance() {
      return factory;
   }

   public abstract boolean wsimport(OutputStream var1, Container var2, String[] var3);

   public boolean wsimport(OutputStream logStream, String[] args) {
      return this.wsimport(logStream, Container.NONE, args);
   }

   public abstract boolean wsgen(OutputStream var1, Container var2, String[] var3);

   public boolean wsgen(OutputStream logStream, String[] args) {
      return this.wsgen(logStream, Container.NONE, args);
   }
}
