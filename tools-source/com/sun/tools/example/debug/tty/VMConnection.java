package com.sun.tools.example.debug.tty;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.PathSearchingVirtualMachine;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VMConnection {
   private VirtualMachine vm;
   private Process process = null;
   private int outputCompleteCount = 0;
   private final Connector connector;
   private final Map connectorArgs;
   private final int traceFlags;

   synchronized void notifyOutputComplete() {
      ++this.outputCompleteCount;
      this.notifyAll();
   }

   synchronized void waitOutputComplete() {
      if (this.process != null) {
         while(this.outputCompleteCount < 2) {
            try {
               this.wait();
            } catch (InterruptedException var2) {
            }
         }
      }

   }

   private Connector findConnector(String var1) {
      Iterator var2 = Bootstrap.virtualMachineManager().allConnectors().iterator();

      Connector var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Connector)var2.next();
      } while(!var3.name().equals(var1));

      return var3;
   }

   private Map parseConnectorArgs(Connector var1, String var2) {
      Map var3 = var1.defaultArguments();
      String var4 = "(quote=[^,]+,)|(\\w+=)(((\"[^\"]*\")|('[^']*')|([^,'\"]+))+,)";
      Pattern var5 = Pattern.compile(var4);

      for(Matcher var6 = var5.matcher(var2); var6.find(); var6 = var5.matcher(var2)) {
         int var7 = var6.start();
         int var8 = var6.end();
         if (var7 > 0) {
            throw new IllegalArgumentException(MessageOutput.format("Illegal connector argument", var2));
         }

         String var9 = var2.substring(var7, var8);
         int var10 = var9.indexOf(61);
         String var11 = var9.substring(0, var10);
         String var12 = var9.substring(var10 + 1, var9.length() - 1);
         if (var11.equals("options")) {
            StringBuilder var13 = new StringBuilder();
            Iterator var14 = splitStringAtNonEnclosedWhiteSpace(var12).iterator();

            while(true) {
               if (!var14.hasNext()) {
                  var12 = var13.toString();
                  break;
               }

               String var15;
               for(var15 = (String)var14.next(); isEnclosed(var15, "\"") || isEnclosed(var15, "'"); var15 = var15.substring(1, var15.length() - 1)) {
               }

               var13.append(var15);
               var13.append(" ");
            }
         }

         Connector.Argument var16 = (Connector.Argument)var3.get(var11);
         if (var16 == null) {
            throw new IllegalArgumentException(MessageOutput.format("Argument is not defined for connector:", new Object[]{var11, var1.name()}));
         }

         var16.setValue(var12);
         var2 = var2.substring(var8);
      }

      if (!var2.equals(",") && var2.length() > 0) {
         throw new IllegalArgumentException(MessageOutput.format("Illegal connector argument", var2));
      } else {
         return var3;
      }
   }

   private static boolean isEnclosed(String var0, String var1) {
      if (var0.indexOf(var1) == 0) {
         int var2 = var0.lastIndexOf(var1);
         if (var2 > 0 && var2 == var0.length() - 1) {
            return true;
         }
      }

      return false;
   }

   private static List splitStringAtNonEnclosedWhiteSpace(String var0) throws IllegalArgumentException {
      ArrayList var1 = new ArrayList();
      int var3 = 0;
      boolean var4 = false;
      char var8 = ' ';
      if (var0 == null) {
         throw new IllegalArgumentException(MessageOutput.format("value string is null"));
      } else {
         char[] var2 = var0.toCharArray();

         for(int var9 = 0; var9 < var2.length; ++var9) {
            int var10;
            switch (var2[var9]) {
               case ' ':
                  if (!isLastChar(var2, var9)) {
                     continue;
                  }

                  var10 = var9;
                  break;
               case '"':
               case '\'':
                  if (var8 == var2[var9] && isNextCharWhitespace(var2, var9)) {
                     var10 = var9;
                     var8 = ' ';
                  } else {
                     if (var8 != ' ' || !isPreviousCharWhitespace(var2, var9)) {
                        continue;
                     }

                     var3 = var9;
                     if (var0.indexOf(var2[var9], var9 + 1) >= 0) {
                        var8 = var2[var9];
                        continue;
                     }

                     if (!isNextCharWhitespace(var2, var9)) {
                        continue;
                     }

                     var10 = var9;
                  }
                  break;
               default:
                  if (var8 != ' ') {
                     continue;
                  }

                  if (isPreviousCharWhitespace(var2, var9)) {
                     var3 = var9;
                  }

                  if (!isNextCharWhitespace(var2, var9)) {
                     continue;
                  }

                  var10 = var9;
            }

            if (var3 > var10) {
               throw new IllegalArgumentException(MessageOutput.format("Illegal option values"));
            }

            ++var10;
            var1.add(var0.substring(var3, var10));
            var3 = var10;
            var9 = var10;
         }

         return var1;
      }
   }

   private static boolean isPreviousCharWhitespace(char[] var0, int var1) {
      return isCharWhitespace(var0, var1 - 1);
   }

   private static boolean isNextCharWhitespace(char[] var0, int var1) {
      return isCharWhitespace(var0, var1 + 1);
   }

   private static boolean isCharWhitespace(char[] var0, int var1) {
      if (var1 >= 0 && var1 < var0.length) {
         return var0[var1] == ' ';
      } else {
         return true;
      }
   }

   private static boolean isLastChar(char[] var0, int var1) {
      return var1 + 1 == var0.length;
   }

   VMConnection(String var1, int var2) {
      int var5 = var1.indexOf(58);
      String var3;
      String var4;
      if (var5 == -1) {
         var3 = var1;
         var4 = "";
      } else {
         var3 = var1.substring(0, var5);
         var4 = var1.substring(var5 + 1);
      }

      this.connector = this.findConnector(var3);
      if (this.connector == null) {
         throw new IllegalArgumentException(MessageOutput.format("No connector named:", var3));
      } else {
         this.connectorArgs = this.parseConnectorArgs(this.connector, var4);
         this.traceFlags = var2;
      }
   }

   synchronized VirtualMachine open() {
      if (this.connector instanceof LaunchingConnector) {
         this.vm = this.launchTarget();
      } else if (this.connector instanceof AttachingConnector) {
         this.vm = this.attachTarget();
      } else {
         if (!(this.connector instanceof ListeningConnector)) {
            throw new InternalError(MessageOutput.format("Invalid connect type"));
         }

         this.vm = this.listenTarget();
      }

      this.vm.setDebugTraceMode(this.traceFlags);
      if (this.vm.canBeModified()) {
         this.setEventRequests(this.vm);
         this.resolveEventRequests();
      }

      if (Env.getSourcePath().length() == 0) {
         if (this.vm instanceof PathSearchingVirtualMachine) {
            PathSearchingVirtualMachine var1 = (PathSearchingVirtualMachine)this.vm;
            Env.setSourcePath(var1.classPath());
         } else {
            Env.setSourcePath(".");
         }
      }

      return this.vm;
   }

   boolean setConnectorArg(String var1, String var2) {
      if (this.vm != null) {
         return false;
      } else {
         Connector.Argument var3 = (Connector.Argument)this.connectorArgs.get(var1);
         if (var3 == null) {
            return false;
         } else {
            var3.setValue(var2);
            return true;
         }
      }
   }

   String connectorArg(String var1) {
      Connector.Argument var2 = (Connector.Argument)this.connectorArgs.get(var1);
      return var2 == null ? "" : var2.value();
   }

   public synchronized VirtualMachine vm() {
      if (this.vm == null) {
         throw new VMNotConnectedException();
      } else {
         return this.vm;
      }
   }

   boolean isOpen() {
      return this.vm != null;
   }

   boolean isLaunch() {
      return this.connector instanceof LaunchingConnector;
   }

   public void disposeVM() {
      try {
         if (this.vm != null) {
            this.vm.dispose();
            this.vm = null;
         }
      } finally {
         if (this.process != null) {
            this.process.destroy();
            this.process = null;
         }

         this.waitOutputComplete();
      }

   }

   private void setEventRequests(VirtualMachine var1) {
      EventRequestManager var2 = var1.eventRequestManager();
      Commands var3 = new Commands();
      var3.commandCatchException(new StringTokenizer("uncaught java.lang.Throwable"));
      ThreadStartRequest var4 = var2.createThreadStartRequest();
      var4.enable();
      ThreadDeathRequest var5 = var2.createThreadDeathRequest();
      var5.enable();
   }

   private void resolveEventRequests() {
      Env.specList.resolveAll();
   }

   private void dumpStream(InputStream var1) throws IOException {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));

      int var3;
      try {
         while((var3 = var2.read()) != -1) {
            MessageOutput.printDirect((char)var3);
         }
      } catch (IOException var6) {
         String var5 = var6.getMessage();
         if (!var5.startsWith("Bad file number")) {
            throw var6;
         }
      }

   }

   private void displayRemoteOutput(final InputStream var1) {
      Thread var2 = new Thread("output reader") {
         public void run() {
            try {
               VMConnection.this.dumpStream(var1);
            } catch (IOException var5) {
               MessageOutput.fatalError("Failed reading output");
            } finally {
               VMConnection.this.notifyOutputComplete();
            }

         }
      };
      var2.setPriority(9);
      var2.start();
   }

   private void dumpFailedLaunchInfo(Process var1) {
      try {
         this.dumpStream(var1.getErrorStream());
         this.dumpStream(var1.getInputStream());
      } catch (IOException var3) {
         MessageOutput.println("Unable to display process output:", var3.getMessage());
      }

   }

   private VirtualMachine launchTarget() {
      LaunchingConnector var1 = (LaunchingConnector)this.connector;

      try {
         VirtualMachine var2 = var1.launch(this.connectorArgs);
         this.process = var2.process();
         this.displayRemoteOutput(this.process.getErrorStream());
         this.displayRemoteOutput(this.process.getInputStream());
         return var2;
      } catch (IOException var3) {
         var3.printStackTrace();
         MessageOutput.fatalError("Unable to launch target VM.");
      } catch (IllegalConnectorArgumentsException var4) {
         var4.printStackTrace();
         MessageOutput.fatalError("Internal debugger error.");
      } catch (VMStartException var5) {
         MessageOutput.println("vmstartexception", var5.getMessage());
         MessageOutput.println();
         this.dumpFailedLaunchInfo(var5.process());
         MessageOutput.fatalError("Target VM failed to initialize.");
      }

      return null;
   }

   private VirtualMachine attachTarget() {
      AttachingConnector var1 = (AttachingConnector)this.connector;

      try {
         return var1.attach(this.connectorArgs);
      } catch (IOException var3) {
         var3.printStackTrace();
         MessageOutput.fatalError("Unable to attach to target VM.");
      } catch (IllegalConnectorArgumentsException var4) {
         var4.printStackTrace();
         MessageOutput.fatalError("Internal debugger error.");
      }

      return null;
   }

   private VirtualMachine listenTarget() {
      ListeningConnector var1 = (ListeningConnector)this.connector;

      try {
         String var2 = var1.startListening(this.connectorArgs);
         MessageOutput.println("Listening at address:", var2);
         this.vm = var1.accept(this.connectorArgs);
         var1.stopListening(this.connectorArgs);
         return this.vm;
      } catch (IOException var3) {
         var3.printStackTrace();
         MessageOutput.fatalError("Unable to attach to target VM.");
      } catch (IllegalConnectorArgumentsException var4) {
         var4.printStackTrace();
         MessageOutput.fatalError("Internal debugger error.");
      }

      return null;
   }
}
