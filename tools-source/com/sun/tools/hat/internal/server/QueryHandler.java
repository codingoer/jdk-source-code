package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaField;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.JavaObject;
import com.sun.tools.hat.internal.model.JavaStatic;
import com.sun.tools.hat.internal.model.JavaThing;
import com.sun.tools.hat.internal.model.Root;
import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.model.StackFrame;
import com.sun.tools.hat.internal.model.StackTrace;
import com.sun.tools.hat.internal.util.Misc;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

abstract class QueryHandler {
   protected String urlStart;
   protected String query;
   protected PrintWriter out;
   protected Snapshot snapshot;

   abstract void run();

   void setUrlStart(String var1) {
      this.urlStart = var1;
   }

   void setQuery(String var1) {
      this.query = var1;
   }

   void setOutput(PrintWriter var1) {
      this.out = var1;
   }

   void setSnapshot(Snapshot var1) {
      this.snapshot = var1;
   }

   protected String encodeForURL(String var1) {
      try {
         var1 = URLEncoder.encode(var1, "UTF-8");
      } catch (UnsupportedEncodingException var3) {
         var3.printStackTrace();
      }

      return var1;
   }

   protected void startHtml(String var1) {
      this.out.print("<html><title>");
      this.print(var1);
      this.out.println("</title>");
      this.out.println("<body bgcolor=\"#ffffff\"><center><h1>");
      this.print(var1);
      this.out.println("</h1></center>");
   }

   protected void endHtml() {
      this.out.println("</body></html>");
   }

   protected void error(String var1) {
      this.println(var1);
   }

   protected void printAnchorStart() {
      this.out.print("<a href=\"");
      this.out.print(this.urlStart);
   }

   protected void printThingAnchorTag(long var1) {
      this.printAnchorStart();
      this.out.print("object/");
      this.printHex(var1);
      this.out.print("\">");
   }

   protected void printObject(JavaObject var1) {
      this.printThing(var1);
   }

   protected void printThing(JavaThing var1) {
      if (var1 == null) {
         this.out.print("null");
      } else {
         if (var1 instanceof JavaHeapObject) {
            JavaHeapObject var2 = (JavaHeapObject)var1;
            long var3 = var2.getId();
            if (var3 != -1L) {
               if (var2.isNew()) {
                  this.out.println("<strong>");
               }

               this.printThingAnchorTag(var3);
            }

            this.print(var1.toString());
            if (var3 != -1L) {
               if (var2.isNew()) {
                  this.out.println("[new]</strong>");
               }

               this.out.print(" (" + var2.getSize() + " bytes)");
               this.out.println("</a>");
            }
         } else {
            this.print(var1.toString());
         }

      }
   }

   protected void printRoot(Root var1) {
      StackTrace var2 = var1.getStackTrace();
      boolean var3 = var2 != null && var2.getFrames().length != 0;
      if (var3) {
         this.printAnchorStart();
         this.out.print("rootStack/");
         this.printHex((long)var1.getIndex());
         this.out.print("\">");
      }

      this.print(var1.getDescription());
      if (var3) {
         this.out.print("</a>");
      }

   }

   protected void printClass(JavaClass var1) {
      if (var1 == null) {
         this.out.println("null");
      } else {
         this.printAnchorStart();
         this.out.print("class/");
         this.print(this.encodeForURL(var1));
         this.out.print("\">");
         this.print(var1.toString());
         this.out.println("</a>");
      }
   }

   protected String encodeForURL(JavaClass var1) {
      return var1.getId() == -1L ? this.encodeForURL(var1.getName()) : var1.getIdString();
   }

   protected void printField(JavaField var1) {
      this.print(var1.getName() + " (" + var1.getSignature() + ")");
   }

   protected void printStatic(JavaStatic var1) {
      JavaField var2 = var1.getField();
      this.printField(var2);
      this.out.print(" : ");
      if (var2.hasId()) {
         JavaThing var3 = var1.getValue();
         this.printThing(var3);
      } else {
         this.print(var1.getValue().toString());
      }

   }

   protected void printStackTrace(StackTrace var1) {
      StackFrame[] var2 = var1.getFrames();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         StackFrame var4 = var2[var3];
         String var5 = var4.getClassName();
         this.out.print("<font color=purple>");
         this.print(var5);
         this.out.print("</font>");
         this.print("." + var4.getMethodName() + "(" + var4.getMethodSignature() + ")");
         this.out.print(" <bold>:</bold> ");
         this.print(var4.getSourceFileName() + " line " + var4.getLineNumber());
         this.out.println("<br>");
      }

   }

   protected void printException(Throwable var1) {
      this.println(var1.getMessage());
      this.out.println("<pre>");
      StringWriter var2 = new StringWriter();
      var1.printStackTrace(new PrintWriter(var2));
      this.print(var2.toString());
      this.out.println("</pre>");
   }

   protected void printHex(long var1) {
      if (this.snapshot.getIdentifierSize() == 4) {
         this.out.print(Misc.toHex((int)var1));
      } else {
         this.out.print(Misc.toHex(var1));
      }

   }

   protected long parseHex(String var1) {
      return Misc.parseHex(var1);
   }

   protected void print(String var1) {
      this.out.print(Misc.encodeHtml(var1));
   }

   protected void println(String var1) {
      this.out.println(Misc.encodeHtml(var1));
   }
}
