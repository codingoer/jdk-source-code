package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.oql.OQLEngine;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class QueryListener implements Runnable {
   private Snapshot snapshot;
   private OQLEngine engine;
   private int port;

   public QueryListener(int var1) {
      this.port = var1;
      this.snapshot = null;
      this.engine = null;
   }

   public void setModel(Snapshot var1) {
      this.snapshot = var1;
      if (OQLEngine.isOQLSupported()) {
         this.engine = new OQLEngine(var1);
      }

   }

   public void run() {
      try {
         this.waitForRequests();
      } catch (IOException var2) {
         var2.printStackTrace();
         System.exit(1);
      }

   }

   private void waitForRequests() throws IOException {
      ServerSocket var1 = new ServerSocket(this.port);
      Thread var2 = null;

      while(true) {
         Socket var3 = var1.accept();
         Thread var4 = new Thread(new HttpReader(var3, this.snapshot, this.engine));
         if (this.snapshot == null) {
            var4.setPriority(6);
         } else {
            var4.setPriority(4);
            if (var2 != null) {
               try {
                  var2.setPriority(3);
               } catch (Throwable var6) {
               }
            }
         }

         var4.start();
         var2 = var4;
      }
   }
}
