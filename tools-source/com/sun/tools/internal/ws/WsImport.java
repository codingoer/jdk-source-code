package com.sun.tools.internal.ws;

import com.sun.tools.internal.ws.wscompile.WsimportTool;

public class WsImport {
   public static void main(String[] args) throws Throwable {
      System.exit(Invoker.invoke("com.sun.tools.internal.ws.wscompile.WsimportTool", args));
   }

   public static int doMain(String[] args) throws Throwable {
      return (new WsimportTool(System.out)).run(args) ? 0 : 1;
   }
}
