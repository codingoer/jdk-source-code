package com.sun.tools.hat.internal.oql;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.Snapshot;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class OQLEngine {
   private Object engine;
   private Method evalMethod;
   private Method invokeMethod;
   private Snapshot snapshot;
   private static boolean debug;
   private static boolean oqlSupported;

   public static boolean isOQLSupported() {
      return oqlSupported;
   }

   public OQLEngine(Snapshot var1) {
      if (!isOQLSupported()) {
         throw new UnsupportedOperationException("OQL not supported");
      } else {
         this.init(var1);
      }
   }

   public synchronized void executeQuery(String var1, ObjectVisitor var2) throws OQLException {
      debugPrint("query : " + var1);
      StringTokenizer var3 = new StringTokenizer(var1);
      if (!var3.hasMoreTokens()) {
         throw new OQLException("query syntax error: no 'select' clause");
      } else {
         String var4 = var3.nextToken();
         if (!var4.equals("select")) {
            try {
               Object var12 = this.evalScript(var1);
               var2.visit(var12);
            } catch (Exception var11) {
               throw new OQLException(var11);
            }
         } else {
            var4 = "";

            boolean var5;
            String var6;
            for(var5 = false; var3.hasMoreTokens(); var4 = var4 + " " + var6) {
               var6 = var3.nextToken();
               if (var6.equals("from")) {
                  var5 = true;
                  break;
               }
            }

            if (var4.equals("")) {
               throw new OQLException("query syntax error: 'select' expression can not be empty");
            } else {
               var6 = null;
               boolean var7 = false;
               String var8 = null;
               String var9 = null;
               if (var5) {
                  if (!var3.hasMoreTokens()) {
                     throw new OQLException("query syntax error: class name must follow 'from'");
                  }

                  String var10 = var3.nextToken();
                  if (var10.equals("instanceof")) {
                     var7 = true;
                     if (!var3.hasMoreTokens()) {
                        throw new OQLException("no class name after 'instanceof'");
                     }

                     var6 = var3.nextToken();
                  } else {
                     var6 = var10;
                  }

                  if (!var3.hasMoreTokens()) {
                     throw new OQLException("query syntax error: identifier should follow class name");
                  }

                  var9 = var3.nextToken();
                  if (var9.equals("where")) {
                     throw new OQLException("query syntax error: identifier should follow class name");
                  }

                  if (var3.hasMoreTokens()) {
                     var10 = var3.nextToken();
                     if (!var10.equals("where")) {
                        throw new OQLException("query syntax error: 'where' clause expected after 'from' clause");
                     }

                     for(var8 = ""; var3.hasMoreTokens(); var8 = var8 + " " + var3.nextToken()) {
                     }

                     if (var8.equals("")) {
                        throw new OQLException("query syntax error: 'where' clause cannot have empty expression");
                     }
                  }
               }

               this.executeQuery(new OQLQuery(var4, var7, var6, var9, var8), var2);
            }
         }
      }
   }

   private void executeQuery(OQLQuery var1, ObjectVisitor var2) throws OQLException {
      JavaClass var3 = null;
      if (var1.className != null) {
         var3 = this.snapshot.findClass(var1.className);
         if (var3 == null) {
            throw new OQLException(var1.className + " is not found!");
         }
      }

      StringBuffer var4 = new StringBuffer();
      var4.append("function __select__(");
      if (var1.identifier != null) {
         var4.append(var1.identifier);
      }

      var4.append(") { return ");
      var4.append(var1.selectExpr.replace('\n', ' '));
      var4.append("; }");
      String var5 = var4.toString();
      debugPrint(var5);
      String var6 = null;
      if (var1.whereExpr != null) {
         var4 = new StringBuffer();
         var4.append("function __where__(");
         var4.append(var1.identifier);
         var4.append(") { return ");
         var4.append(var1.whereExpr.replace('\n', ' '));
         var4.append("; }");
         var6 = var4.toString();
      }

      debugPrint(var6);

      try {
         this.evalMethod.invoke(this.engine, var5);
         if (var6 != null) {
            this.evalMethod.invoke(this.engine, var6);
         }

         if (var1.className != null) {
            Enumeration var7 = var3.getInstances(var1.isInstanceOf);

            while(var7.hasMoreElements()) {
               JavaHeapObject var8 = (JavaHeapObject)var7.nextElement();
               Object[] var9 = new Object[]{this.wrapJavaObject(var8)};
               boolean var10 = var6 == null;
               Object var11;
               if (!var10) {
                  var11 = this.call("__where__", var9);
                  if (var11 instanceof Boolean) {
                     var10 = (Boolean)var11;
                  } else if (var11 instanceof Number) {
                     var10 = ((Number)var11).intValue() != 0;
                  } else {
                     var10 = var11 != null;
                  }
               }

               if (var10) {
                  var11 = this.call("__select__", var9);
                  if (var2.visit(var11)) {
                     return;
                  }
               }
            }
         } else {
            Object var13 = this.call("__select__", new Object[0]);
            var2.visit(var13);
         }

      } catch (Exception var12) {
         throw new OQLException(var12);
      }
   }

   public Object evalScript(String var1) throws Exception {
      return this.evalMethod.invoke(this.engine, var1);
   }

   public Object wrapJavaObject(JavaHeapObject var1) throws Exception {
      return this.call("wrapJavaObject", new Object[]{var1});
   }

   public Object toHtml(Object var1) throws Exception {
      return this.call("toHtml", new Object[]{var1});
   }

   public Object call(String var1, Object[] var2) throws Exception {
      return this.invokeMethod.invoke(this.engine, var1, var2);
   }

   private static void debugPrint(String var0) {
      if (debug) {
         System.out.println(var0);
      }

   }

   private void init(Snapshot var1) throws RuntimeException {
      this.snapshot = var1;

      try {
         Class var2 = Class.forName("javax.script.ScriptEngineManager");
         Object var3 = var2.newInstance();
         Method var4 = var2.getMethod("getEngineByName", String.class);
         this.engine = var4.invoke(var3, "js");
         InputStream var5 = this.getInitStream();
         Class var6 = Class.forName("javax.script.ScriptEngine");
         this.evalMethod = var6.getMethod("eval", Reader.class);
         this.evalMethod.invoke(this.engine, new InputStreamReader(var5));
         Class var7 = Class.forName("javax.script.Invocable");
         this.evalMethod = var6.getMethod("eval", String.class);
         this.invokeMethod = var7.getMethod("invokeFunction", String.class, Object[].class);
         Method var8 = var6.getMethod("put", String.class, Object.class);
         var8.invoke(this.engine, "heap", this.call("wrapHeapSnapshot", new Object[]{var1}));
      } catch (Exception var9) {
         if (debug) {
            var9.printStackTrace();
         }

         throw new RuntimeException(var9);
      }
   }

   private InputStream getInitStream() {
      return this.getClass().getResourceAsStream("/com/sun/tools/hat/resources/hat.js");
   }

   static {
      try {
         Class var0 = Class.forName("javax.script.ScriptEngineManager");
         Object var1 = var0.newInstance();
         Method var2 = var0.getMethod("getEngineByName", String.class);
         Object var3 = var2.invoke(var1, "js");
         oqlSupported = var3 != null;
      } catch (Exception var4) {
         oqlSupported = false;
      }

      debug = false;
   }
}
