package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.oql.OQLEngine;
import com.sun.tools.hat.internal.oql.OQLException;
import com.sun.tools.hat.internal.oql.ObjectVisitor;

class OQLQuery extends QueryHandler {
   private OQLEngine engine;

   public OQLQuery(OQLEngine var1) {
      this.engine = var1;
   }

   public void run() {
      this.startHtml("Object Query Language (OQL) query");
      String var1 = null;
      if (this.query != null && !this.query.equals("")) {
         int var2 = this.query.indexOf("?query=");
         if (var2 != -1 && this.query.length() > 7) {
            var1 = this.query.substring(var2 + 7);
         }
      }

      this.out.println("<p align='center'><table>");
      this.out.println("<tr><td><b>");
      this.out.println("<a href='/'>All Classes (excluding platform)</a>");
      this.out.println("</b></td>");
      this.out.println("<td><b><a href='/oqlhelp/'>OQL Help</a></b></td></tr>");
      this.out.println("</table></p>");
      this.out.println("<form action='/oql/' method='get'>");
      this.out.println("<p align='center'>");
      this.out.println("<textarea name='query' cols=80 rows=10>");
      if (var1 != null) {
         this.println(var1);
      }

      this.out.println("</textarea>");
      this.out.println("</p>");
      this.out.println("<p align='center'>");
      this.out.println("<input type='submit' value='Execute'></input>");
      this.out.println("</p>");
      this.out.println("</form>");
      if (var1 != null) {
         this.executeQuery(var1);
      }

      this.endHtml();
   }

   private void executeQuery(String var1) {
      try {
         this.out.println("<table border='1'>");
         this.engine.executeQuery(var1, new ObjectVisitor() {
            public boolean visit(Object var1) {
               OQLQuery.this.out.println("<tr><td>");

               try {
                  OQLQuery.this.out.println(OQLQuery.this.engine.toHtml(var1));
               } catch (Exception var3) {
                  OQLQuery.this.printException(var3);
               }

               OQLQuery.this.out.println("</td></tr>");
               return false;
            }
         });
         this.out.println("</table>");
      } catch (OQLException var3) {
         this.printException(var3);
      }

   }
}
