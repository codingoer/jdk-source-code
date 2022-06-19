package com.sun.tools.internal.ws;

import com.sun.tools.internal.ws.wscompile.WsgenTool;

public class WsGen {
   public static void main(String[] args) throws Throwable {
      System.exit(Invoker.invoke("com.sun.tools.internal.ws.wscompile.WsgenTool", args));
   }

   public static int doMain(String[] args) throws Throwable {
      return (new WsgenTool(System.out)).run(args) ? 0 : 1;
   }
}
