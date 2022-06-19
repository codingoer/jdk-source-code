package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.wsdl.framework.Kind;

public class Kinds {
   public static final Kind BINDING = new Kind("wsdl:binding");
   public static final Kind MESSAGE = new Kind("wsdl:message");
   public static final Kind PORT = new Kind("wsdl:port");
   public static final Kind PORT_TYPE = new Kind("wsdl:portType");
   public static final Kind SERVICE = new Kind("wsdl:service");

   private Kinds() {
   }
}
