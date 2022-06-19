package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.oql.OQLEngine;
import com.sun.tools.hat.internal.util.Misc;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;

public class HttpReader implements Runnable {
   private Socket socket;
   private PrintWriter out;
   private Snapshot snapshot;
   private OQLEngine engine;

   public HttpReader(Socket var1, Snapshot var2, OQLEngine var3) {
      this.socket = var1;
      this.snapshot = var2;
      this.engine = var3;
   }

   public void run() {
      BufferedInputStream var1 = null;

      try {
         var1 = new BufferedInputStream(this.socket.getInputStream());
         this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())));
         this.out.println("HTTP/1.0 200 OK");
         this.out.println("Cache-Control: no-cache");
         this.out.println("Pragma: no-cache");
         this.out.println();
         if (var1.read() != 71 || var1.read() != 69 || var1.read() != 84 || var1.read() != 32) {
            this.outputError("Protocol error");
         }

         StringBuilder var3 = new StringBuilder();

         int var2;
         while((var2 = var1.read()) != -1 && var2 != 32) {
            char var4 = (char)var2;
            var3.append(var4);
         }

         String var23 = var3.toString();
         var23 = URLDecoder.decode(var23, "UTF-8");
         Object var5 = null;
         if (this.snapshot != null) {
            if (var23.equals("/")) {
               var5 = new AllClassesQuery(true, this.engine != null);
               ((QueryHandler)var5).setUrlStart("");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.startsWith("/oql/")) {
               if (this.engine != null) {
                  var5 = new OQLQuery(this.engine);
                  ((QueryHandler)var5).setUrlStart("");
                  ((QueryHandler)var5).setQuery(var23.substring(5));
               }
            } else if (var23.startsWith("/oqlhelp/")) {
               if (this.engine != null) {
                  var5 = new OQLHelp();
                  ((QueryHandler)var5).setUrlStart("");
                  ((QueryHandler)var5).setQuery("");
               }
            } else if (var23.equals("/allClassesWithPlatform/")) {
               var5 = new AllClassesQuery(false, this.engine != null);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.equals("/showRoots/")) {
               var5 = new AllRootsQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.equals("/showInstanceCounts/includePlatform/")) {
               var5 = new InstancesCountQuery(false);
               ((QueryHandler)var5).setUrlStart("../../");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.equals("/showInstanceCounts/")) {
               var5 = new InstancesCountQuery(true);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.startsWith("/instances/")) {
               var5 = new InstancesQuery(false);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(11));
            } else if (var23.startsWith("/newInstances/")) {
               var5 = new InstancesQuery(false, true);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(14));
            } else if (var23.startsWith("/allInstances/")) {
               var5 = new InstancesQuery(true);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(14));
            } else if (var23.startsWith("/allNewInstances/")) {
               var5 = new InstancesQuery(true, true);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(17));
            } else if (var23.startsWith("/object/")) {
               var5 = new ObjectQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(8));
            } else if (var23.startsWith("/class/")) {
               var5 = new ClassQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(7));
            } else if (var23.startsWith("/roots/")) {
               var5 = new RootsQuery(false);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(7));
            } else if (var23.startsWith("/allRoots/")) {
               var5 = new RootsQuery(true);
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(10));
            } else if (var23.startsWith("/reachableFrom/")) {
               var5 = new ReachableQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(15));
            } else if (var23.startsWith("/rootStack/")) {
               var5 = new RootStackQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(11));
            } else if (var23.startsWith("/histo/")) {
               var5 = new HistogramQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(7));
            } else if (var23.startsWith("/refsByType/")) {
               var5 = new RefsByTypeQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery(var23.substring(12));
            } else if (var23.startsWith("/finalizerSummary/")) {
               var5 = new FinalizerSummaryQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery("");
            } else if (var23.startsWith("/finalizerObjects/")) {
               var5 = new FinalizerObjectsQuery();
               ((QueryHandler)var5).setUrlStart("../");
               ((QueryHandler)var5).setQuery("");
            }

            if (var5 != null) {
               ((QueryHandler)var5).setOutput(this.out);
               ((QueryHandler)var5).setSnapshot(this.snapshot);
               ((QueryHandler)var5).run();
            } else {
               this.outputError("Query '" + var23 + "' not implemented");
            }

            return;
         }

         this.outputError("The heap snapshot is still being read.");
      } catch (IOException var21) {
         var21.printStackTrace();
         return;
      } finally {
         if (this.out != null) {
            this.out.close();
         }

         try {
            if (var1 != null) {
               var1.close();
            }
         } catch (IOException var20) {
         }

         try {
            this.socket.close();
         } catch (IOException var19) {
         }

      }

   }

   private void outputError(String var1) {
      this.out.println();
      this.out.println("<html><body bgcolor=\"#ffffff\">");
      this.out.println(Misc.encodeHtml(var1));
      this.out.println("</body></html>");
   }
}
