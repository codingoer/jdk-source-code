package com.sun.tools.example.debug.tty;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class TTY implements EventNotifier {
   EventHandler handler = null;
   private List monitorCommands = new ArrayList();
   private int monitorCount = 0;
   private static final String progname = "jdb";
   private volatile boolean shuttingDown = false;
   private static final String[][] commandList = new String[][]{{"!!", "n", "y"}, {"?", "y", "y"}, {"bytecodes", "n", "y"}, {"catch", "y", "n"}, {"class", "n", "y"}, {"classes", "n", "y"}, {"classpath", "n", "y"}, {"clear", "y", "n"}, {"connectors", "y", "y"}, {"cont", "n", "n"}, {"disablegc", "n", "n"}, {"down", "n", "y"}, {"dump", "n", "y"}, {"enablegc", "n", "n"}, {"eval", "n", "y"}, {"exclude", "y", "n"}, {"exit", "y", "y"}, {"extension", "n", "y"}, {"fields", "n", "y"}, {"gc", "n", "n"}, {"help", "y", "y"}, {"ignore", "y", "n"}, {"interrupt", "n", "n"}, {"kill", "n", "n"}, {"lines", "n", "y"}, {"list", "n", "y"}, {"load", "n", "y"}, {"locals", "n", "y"}, {"lock", "n", "n"}, {"memory", "n", "y"}, {"methods", "n", "y"}, {"monitor", "n", "n"}, {"next", "n", "n"}, {"pop", "n", "n"}, {"print", "n", "y"}, {"quit", "y", "y"}, {"read", "y", "y"}, {"redefine", "n", "n"}, {"reenter", "n", "n"}, {"resume", "n", "n"}, {"run", "y", "n"}, {"save", "n", "n"}, {"set", "n", "n"}, {"sourcepath", "y", "y"}, {"step", "n", "n"}, {"stepi", "n", "n"}, {"stop", "y", "n"}, {"suspend", "n", "n"}, {"thread", "n", "y"}, {"threadgroup", "n", "y"}, {"threadgroups", "n", "y"}, {"threadlocks", "n", "y"}, {"threads", "n", "y"}, {"trace", "n", "n"}, {"unmonitor", "n", "n"}, {"untrace", "n", "n"}, {"unwatch", "y", "n"}, {"up", "n", "y"}, {"use", "y", "y"}, {"version", "y", "y"}, {"watch", "y", "n"}, {"where", "n", "y"}, {"wherei", "n", "y"}};

   public void setShuttingDown(boolean var1) {
      this.shuttingDown = var1;
   }

   public boolean isShuttingDown() {
      return this.shuttingDown;
   }

   public void vmStartEvent(VMStartEvent var1) {
      Thread.yield();
      MessageOutput.lnprint("VM Started:");
   }

   public void vmDeathEvent(VMDeathEvent var1) {
   }

   public void vmDisconnectEvent(VMDisconnectEvent var1) {
   }

   public void threadStartEvent(ThreadStartEvent var1) {
   }

   public void threadDeathEvent(ThreadDeathEvent var1) {
   }

   public void classPrepareEvent(ClassPrepareEvent var1) {
   }

   public void classUnloadEvent(ClassUnloadEvent var1) {
   }

   public void breakpointEvent(BreakpointEvent var1) {
      Thread.yield();
      MessageOutput.lnprint("Breakpoint hit:");
   }

   public void fieldWatchEvent(WatchpointEvent var1) {
      Field var2 = var1.field();
      ObjectReference var3 = var1.object();
      Thread.yield();
      if (var1 instanceof ModificationWatchpointEvent) {
         MessageOutput.lnprint("Field access encountered before after", new Object[]{var2, var1.valueCurrent(), ((ModificationWatchpointEvent)var1).valueToBe()});
      } else {
         MessageOutput.lnprint("Field access encountered", var2.toString());
      }

   }

   public void stepEvent(StepEvent var1) {
      Thread.yield();
      MessageOutput.lnprint("Step completed:");
   }

   public void exceptionEvent(ExceptionEvent var1) {
      Thread.yield();
      Location var2 = var1.catchLocation();
      if (var2 == null) {
         MessageOutput.lnprint("Exception occurred uncaught", var1.exception().referenceType().name());
      } else {
         MessageOutput.lnprint("Exception occurred caught", new Object[]{var1.exception().referenceType().name(), Commands.locationString(var2)});
      }

   }

   public void methodEntryEvent(MethodEntryEvent var1) {
      Thread.yield();
      if (var1.request().suspendPolicy() != 0) {
         MessageOutput.lnprint("Method entered:");
      } else {
         MessageOutput.print("Method entered:");
         this.printLocationOfEvent(var1);
      }

   }

   public boolean methodExitEvent(MethodExitEvent var1) {
      Thread.yield();
      Method var2 = Env.atExitMethod();
      Method var3 = var1.method();
      if (var2 != null && !var2.equals(var3)) {
         return false;
      } else {
         if (var1.request().suspendPolicy() != 0) {
            MessageOutput.println();
         }

         if (Env.vm().canGetMethodReturnValues()) {
            MessageOutput.print("Method exitedValue:", var1.returnValue() + "");
         } else {
            MessageOutput.print("Method exited:");
         }

         if (var1.request().suspendPolicy() == 0) {
            this.printLocationOfEvent(var1);
         }

         return true;
      }
   }

   public void vmInterrupted() {
      Thread.yield();
      this.printCurrentLocation();
      Iterator var1 = this.monitorCommands.iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         StringTokenizer var3 = new StringTokenizer(var2);
         var3.nextToken();
         this.executeCommand(var3);
      }

      MessageOutput.printPrompt();
   }

   public void receivedEvent(Event var1) {
   }

   private void printBaseLocation(String var1, Location var2) {
      MessageOutput.println("location", new Object[]{var1, Commands.locationString(var2)});
   }

   private void printCurrentLocation() {
      ThreadInfo var1 = ThreadInfo.getCurrentThreadInfo();

      StackFrame var2;
      try {
         var2 = var1.getCurrentFrame();
      } catch (IncompatibleThreadStateException var7) {
         MessageOutput.println("<location unavailable>");
         return;
      }

      if (var2 == null) {
         MessageOutput.println("No frames on the current call stack");
      } else {
         Location var3 = var2.location();
         this.printBaseLocation(var1.getThread().name(), var3);
         if (var3.lineNumber() != -1) {
            String var4;
            try {
               var4 = Env.sourceLine(var3, var3.lineNumber());
            } catch (IOException var6) {
               var4 = null;
            }

            if (var4 != null) {
               MessageOutput.println("source line number and line", new Object[]{new Integer(var3.lineNumber()), var4});
            }
         }
      }

      MessageOutput.println();
   }

   private void printLocationOfEvent(LocatableEvent var1) {
      this.printBaseLocation(var1.thread().name(), var1.location());
   }

   void help() {
      MessageOutput.println("zz help text");
   }

   private int isCommand(String var1) {
      int var2 = 0;
      int var3 = commandList.length - 1;

      while(var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         String var5 = commandList[var4][0];
         int var6 = var5.compareTo(var1);
         if (var6 < 0) {
            var2 = var4 + 1;
         } else {
            if (var6 <= 0) {
               return var4;
            }

            var3 = var4 - 1;
         }
      }

      return -(var2 + 1);
   }

   private boolean isDisconnectCmd(int var1) {
      return var1 >= 0 && var1 < commandList.length ? commandList[var1][1].equals("y") : false;
   }

   private boolean isReadOnlyCmd(int var1) {
      return var1 >= 0 && var1 < commandList.length ? commandList[var1][2].equals("y") : false;
   }

   void executeCommand(StringTokenizer var1) {
      String var2 = var1.nextToken().toLowerCase();
      boolean var3 = true;
      if (!var2.startsWith("#")) {
         int var4;
         if (Character.isDigit(var2.charAt(0)) && var1.hasMoreTokens()) {
            try {
               var4 = Integer.parseInt(var2);

               for(String var12 = var1.nextToken(""); var4-- > 0; var3 = false) {
                  this.executeCommand(new StringTokenizer(var12));
               }
            } catch (NumberFormatException var11) {
               MessageOutput.println("Unrecognized command.  Try help...", var2);
            }
         } else {
            var4 = this.isCommand(var2);
            if (var4 < 0) {
               MessageOutput.println("Unrecognized command.  Try help...", var2);
            } else if (!Env.connection().isOpen() && !this.isDisconnectCmd(var4)) {
               MessageOutput.println("Command not valid until the VM is started with the run command", var2);
            } else if (Env.connection().isOpen() && !Env.vm().canBeModified() && !this.isReadOnlyCmd(var4)) {
               MessageOutput.println("Command is not supported on a read-only VM connection", var2);
            } else {
               Commands var5 = new Commands();

               try {
                  if (var2.equals("print")) {
                     var5.commandPrint(var1, false);
                     var3 = false;
                  } else if (var2.equals("eval")) {
                     var5.commandPrint(var1, false);
                     var3 = false;
                  } else if (var2.equals("set")) {
                     var5.commandSet(var1);
                     var3 = false;
                  } else if (var2.equals("dump")) {
                     var5.commandPrint(var1, true);
                     var3 = false;
                  } else if (var2.equals("locals")) {
                     var5.commandLocals();
                  } else if (var2.equals("classes")) {
                     var5.commandClasses();
                  } else if (var2.equals("class")) {
                     var5.commandClass(var1);
                  } else if (var2.equals("connectors")) {
                     var5.commandConnectors(Bootstrap.virtualMachineManager());
                  } else if (var2.equals("methods")) {
                     var5.commandMethods(var1);
                  } else if (var2.equals("fields")) {
                     var5.commandFields(var1);
                  } else if (var2.equals("threads")) {
                     var5.commandThreads(var1);
                  } else if (var2.equals("thread")) {
                     var5.commandThread(var1);
                  } else if (var2.equals("suspend")) {
                     var5.commandSuspend(var1);
                  } else if (var2.equals("resume")) {
                     var5.commandResume(var1);
                  } else if (var2.equals("cont")) {
                     var5.commandCont();
                  } else if (var2.equals("threadgroups")) {
                     var5.commandThreadGroups();
                  } else if (var2.equals("threadgroup")) {
                     var5.commandThreadGroup(var1);
                  } else if (var2.equals("catch")) {
                     var5.commandCatchException(var1);
                  } else if (var2.equals("ignore")) {
                     var5.commandIgnoreException(var1);
                  } else if (var2.equals("step")) {
                     var5.commandStep(var1);
                  } else if (var2.equals("stepi")) {
                     var5.commandStepi();
                  } else if (var2.equals("next")) {
                     var5.commandNext();
                  } else if (var2.equals("kill")) {
                     var5.commandKill(var1);
                  } else if (var2.equals("interrupt")) {
                     var5.commandInterrupt(var1);
                  } else if (var2.equals("trace")) {
                     var5.commandTrace(var1);
                  } else if (var2.equals("untrace")) {
                     var5.commandUntrace(var1);
                  } else if (var2.equals("where")) {
                     var5.commandWhere(var1, false);
                  } else if (var2.equals("wherei")) {
                     var5.commandWhere(var1, true);
                  } else if (var2.equals("up")) {
                     var5.commandUp(var1);
                  } else if (var2.equals("down")) {
                     var5.commandDown(var1);
                  } else if (var2.equals("load")) {
                     var5.commandLoad(var1);
                  } else if (var2.equals("run")) {
                     var5.commandRun(var1);
                     if (this.handler == null && Env.connection().isOpen()) {
                        this.handler = new EventHandler(this, false);
                     }
                  } else if (var2.equals("memory")) {
                     var5.commandMemory();
                  } else if (var2.equals("gc")) {
                     var5.commandGC();
                  } else if (var2.equals("stop")) {
                     var5.commandStop(var1);
                  } else if (var2.equals("clear")) {
                     var5.commandClear(var1);
                  } else if (var2.equals("watch")) {
                     var5.commandWatch(var1);
                  } else if (var2.equals("unwatch")) {
                     var5.commandUnwatch(var1);
                  } else if (var2.equals("list")) {
                     var5.commandList(var1);
                  } else if (var2.equals("lines")) {
                     var5.commandLines(var1);
                  } else if (var2.equals("classpath")) {
                     var5.commandClasspath(var1);
                  } else if (!var2.equals("use") && !var2.equals("sourcepath")) {
                     if (var2.equals("monitor")) {
                        this.monitorCommand(var1);
                     } else if (var2.equals("unmonitor")) {
                        this.unmonitorCommand(var1);
                     } else if (var2.equals("lock")) {
                        var5.commandLock(var1);
                        var3 = false;
                     } else if (var2.equals("threadlocks")) {
                        var5.commandThreadlocks(var1);
                     } else if (var2.equals("disablegc")) {
                        var5.commandDisableGC(var1);
                        var3 = false;
                     } else if (var2.equals("enablegc")) {
                        var5.commandEnableGC(var1);
                        var3 = false;
                     } else if (var2.equals("save")) {
                        var5.commandSave(var1);
                        var3 = false;
                     } else if (var2.equals("bytecodes")) {
                        var5.commandBytecodes(var1);
                     } else if (var2.equals("redefine")) {
                        var5.commandRedefine(var1);
                     } else if (var2.equals("pop")) {
                        var5.commandPopFrames(var1, false);
                     } else if (var2.equals("reenter")) {
                        var5.commandPopFrames(var1, true);
                     } else if (var2.equals("extension")) {
                        var5.commandExtension(var1);
                     } else if (var2.equals("exclude")) {
                        var5.commandExclude(var1);
                     } else if (var2.equals("read")) {
                        this.readCommand(var1);
                     } else if (!var2.equals("help") && !var2.equals("?")) {
                        if (var2.equals("version")) {
                           var5.commandVersion("jdb", Bootstrap.virtualMachineManager());
                        } else if (!var2.equals("quit") && !var2.equals("exit")) {
                           MessageOutput.println("Unrecognized command.  Try help...", var2);
                        } else {
                           if (this.handler != null) {
                              this.handler.shutdown();
                           }

                           Env.shutdown();
                        }
                     } else {
                        this.help();
                     }
                  } else {
                     var5.commandUse(var1);
                  }
               } catch (VMCannotBeModifiedException var7) {
                  MessageOutput.println("Command is not supported on a read-only VM connection", var2);
               } catch (UnsupportedOperationException var8) {
                  MessageOutput.println("Command is not supported on the target VM", var2);
               } catch (VMNotConnectedException var9) {
                  MessageOutput.println("Command not valid until the VM is started with the run command", var2);
               } catch (Exception var10) {
                  MessageOutput.printException("Internal exception:", var10);
               }
            }
         }
      }

      if (var3) {
         MessageOutput.printPrompt();
      }

   }

   void monitorCommand(StringTokenizer var1) {
      if (var1.hasMoreTokens()) {
         ++this.monitorCount;
         this.monitorCommands.add(this.monitorCount + ": " + var1.nextToken(""));
      } else {
         Iterator var2 = this.monitorCommands.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            MessageOutput.printDirectln(var3);
         }
      }

   }

   void unmonitorCommand(StringTokenizer var1) {
      if (var1.hasMoreTokens()) {
         String var2 = var1.nextToken();

         try {
            int var3 = Integer.parseInt(var2);
         } catch (NumberFormatException var8) {
            MessageOutput.println("Not a monitor number:", var2);
            return;
         }

         String var4 = var2 + ":";
         Iterator var5 = this.monitorCommands.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            StringTokenizer var7 = new StringTokenizer(var6);
            if (var7.nextToken().equals(var4)) {
               this.monitorCommands.remove(var6);
               MessageOutput.println("Unmonitoring", var6);
               return;
            }
         }

         MessageOutput.println("No monitor numbered:", var2);
      } else {
         MessageOutput.println("Usage: unmonitor <monitor#>");
      }

   }

   void readCommand(StringTokenizer var1) {
      if (var1.hasMoreTokens()) {
         String var2 = var1.nextToken();
         if (!this.readCommandFile(new File(var2))) {
            MessageOutput.println("Could not open:", var2);
         }
      } else {
         MessageOutput.println("Usage: read <command-filename>");
      }

   }

   boolean readCommandFile(File var1) {
      BufferedReader var2 = null;

      try {
         if (var1.canRead()) {
            MessageOutput.println("*** Reading commands from", var1.getPath());
            var2 = new BufferedReader(new FileReader(var1));

            String var3;
            while((var3 = var2.readLine()) != null) {
               StringTokenizer var4 = new StringTokenizer(var3);
               if (var4.hasMoreTokens()) {
                  this.executeCommand(var4);
               }
            }
         }
      } catch (IOException var13) {
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Exception var12) {
            }
         }

      }

      return var2 != null;
   }

   String readStartupCommandFile(String var1, String var2, String var3) {
      File var4 = new File(var1, var2);
      if (!var4.exists()) {
         return null;
      } else {
         String var5;
         try {
            var5 = var4.getCanonicalPath();
         } catch (IOException var7) {
            MessageOutput.println("Could not open:", var4.getPath());
            return null;
         }

         if ((var3 == null || !var3.equals(var5)) && !this.readCommandFile(var4)) {
            MessageOutput.println("Could not open:", var4.getPath());
         }

         return var5;
      }
   }

   public TTY() throws Exception {
      MessageOutput.println("Initializing progname", "jdb");
      if (Env.connection().isOpen() && Env.vm().canBeModified()) {
         this.handler = new EventHandler(this, true);
      }

      try {
         BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
         String var2 = null;
         Thread.currentThread().setPriority(5);
         String var3 = System.getProperty("user.home");
         String var4;
         if ((var4 = this.readStartupCommandFile(var3, "jdb.ini", (String)null)) == null) {
            var4 = this.readStartupCommandFile(var3, ".jdbrc", (String)null);
         }

         String var5 = System.getProperty("user.dir");
         if (this.readStartupCommandFile(var5, "jdb.ini", var4) == null) {
            this.readStartupCommandFile(var5, ".jdbrc", var4);
         }

         MessageOutput.printPrompt();

         while(true) {
            var3 = var1.readLine();
            if (var3 == null) {
               if (!this.isShuttingDown()) {
                  MessageOutput.println("Input stream closed.");
               }

               var3 = "quit";
            }

            if (var3.startsWith("!!") && var2 != null) {
               var3 = var2 + var3.substring(2);
               MessageOutput.printDirectln(var3);
            }

            StringTokenizer var7 = new StringTokenizer(var3);
            if (var7.hasMoreTokens()) {
               var2 = var3;
               this.executeCommand(var7);
            } else {
               MessageOutput.printPrompt();
            }
         }
      } catch (VMDisconnectedException var6) {
         this.handler.handleDisconnectedException();
      }
   }

   private static void usage() {
      MessageOutput.println("zz usage text", new Object[]{"jdb", File.pathSeparator});
      System.exit(1);
   }

   static void usageError(String var0) {
      MessageOutput.println(var0);
      MessageOutput.println();
      usage();
   }

   static void usageError(String var0, String var1) {
      MessageOutput.println(var0, var1);
      MessageOutput.println();
      usage();
   }

   private static boolean supportsSharedMemory() {
      Iterator var0 = Bootstrap.virtualMachineManager().allConnectors().iterator();

      Connector var1;
      do {
         if (!var0.hasNext()) {
            return false;
         }

         var1 = (Connector)var0.next();
      } while(var1.transport() == null || !"dt_shmem".equals(var1.transport().name()));

      return true;
   }

   private static String addressToSocketArgs(String var0) {
      int var1 = var0.indexOf(58);
      if (var1 != -1) {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 1);
         return "hostname=" + var2 + ",port=" + var3;
      } else {
         return "port=" + var0;
      }
   }

   private static boolean hasWhitespace(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (Character.isWhitespace(var0.charAt(var2))) {
            return true;
         }
      }

      return false;
   }

   private static String addArgument(String var0, String var1) {
      if (!hasWhitespace(var1) && var1.indexOf(44) == -1) {
         return var0 + var1 + ' ';
      } else {
         StringBuffer var2 = new StringBuffer(var0);
         var2.append('"');

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            char var4 = var1.charAt(var3);
            if (var4 == '"') {
               var2.append('\\');
            }

            var2.append(var4);
         }

         var2.append("\" ");
         return var2.toString();
      }
   }

   public static void main(String[] var0) throws MissingResourceException {
      String var1 = "";
      String var2 = "";
      int var3 = 0;
      boolean var4 = false;
      String var5 = null;
      MessageOutput.textResources = ResourceBundle.getBundle("com.sun.tools.example.debug.tty.TTYResources", Locale.getDefault());

      label205:
      for(int var6 = 0; var6 < var0.length; ++var6) {
         String var7 = var0[var6];
         String var8;
         if (var7.equals("-dbgtrace")) {
            if (var6 != var0.length - 1 && Character.isDigit(var0[var6 + 1].charAt(0))) {
               var8 = "";

               try {
                  ++var6;
                  var8 = var0[var6];
                  var3 = Integer.decode(var8);
               } catch (NumberFormatException var11) {
                  usageError("dbgtrace flag value must be an integer:", var8);
                  return;
               }
            } else {
               var3 = 16777215;
            }
         } else {
            if (var7.equals("-X")) {
               usageError("Use java minus X to see");
               return;
            }

            if (!var7.equals("-v") && !var7.startsWith("-v:") && !var7.startsWith("-verbose") && !var7.startsWith("-D") && !var7.startsWith("-X") && !var7.equals("-noasyncgc") && !var7.equals("-prof") && !var7.equals("-verify") && !var7.equals("-noverify") && !var7.equals("-verifyremote") && !var7.equals("-verbosegc") && !var7.startsWith("-ms") && !var7.startsWith("-mx") && !var7.startsWith("-ss") && !var7.startsWith("-oss")) {
               if (var7.equals("-tclassic")) {
                  usageError("Classic VM no longer supported.");
                  return;
               }

               if (var7.equals("-tclient")) {
                  var2 = "-client " + var2;
               } else if (var7.equals("-tserver")) {
                  var2 = "-server " + var2;
               } else if (var7.equals("-sourcepath")) {
                  if (var6 == var0.length - 1) {
                     usageError("No sourcepath specified.");
                     return;
                  }

                  ++var6;
                  Env.setSourcePath(var0[var6]);
               } else if (var7.equals("-classpath")) {
                  if (var6 == var0.length - 1) {
                     usageError("No classpath specified.");
                     return;
                  }

                  var2 = addArgument(var2, var7);
                  ++var6;
                  var2 = addArgument(var2, var0[var6]);
               } else if (var7.equals("-attach")) {
                  if (var5 != null) {
                     usageError("cannot redefine existing connection", var7);
                     return;
                  }

                  if (var6 == var0.length - 1) {
                     usageError("No attach address specified.");
                     return;
                  }

                  ++var6;
                  var8 = var0[var6];
                  if (supportsSharedMemory()) {
                     var5 = "com.sun.jdi.SharedMemoryAttach:name=" + var8;
                  } else {
                     String var9 = addressToSocketArgs(var8);
                     var5 = "com.sun.jdi.SocketAttach:" + var9;
                  }
               } else if (!var7.equals("-listen") && !var7.equals("-listenany")) {
                  if (var7.equals("-launch")) {
                     var4 = true;
                  } else {
                     Commands var12;
                     if (var7.equals("-listconnectors")) {
                        var12 = new Commands();
                        var12.commandConnectors(Bootstrap.virtualMachineManager());
                        return;
                     }

                     if (var7.equals("-connect")) {
                        if (var5 != null) {
                           usageError("cannot redefine existing connection", var7);
                           return;
                        }

                        if (var6 == var0.length - 1) {
                           usageError("No connect specification.");
                           return;
                        }

                        ++var6;
                        var5 = var0[var6];
                     } else if (var7.equals("-help")) {
                        usage();
                     } else {
                        if (!var7.equals("-version")) {
                           if (var7.startsWith("-")) {
                              usageError("invalid option", var7);
                              return;
                           }

                           var1 = addArgument("", var7);
                           ++var6;

                           while(true) {
                              if (var6 >= var0.length) {
                                 break label205;
                              }

                              var1 = addArgument(var1, var0[var6]);
                              ++var6;
                           }
                        }

                        var12 = new Commands();
                        var12.commandVersion("jdb", Bootstrap.virtualMachineManager());
                        System.exit(0);
                     }
                  }
               } else {
                  if (var5 != null) {
                     usageError("cannot redefine existing connection", var7);
                     return;
                  }

                  var8 = null;
                  if (var7.equals("-listen")) {
                     if (var6 == var0.length - 1) {
                        usageError("No attach address specified.");
                        return;
                     }

                     ++var6;
                     var8 = var0[var6];
                  }

                  if (supportsSharedMemory()) {
                     var5 = "com.sun.jdi.SharedMemoryListen:";
                     if (var8 != null) {
                        var5 = var5 + "name=" + var8;
                     }
                  } else {
                     var5 = "com.sun.jdi.SocketListen:";
                     if (var8 != null) {
                        var5 = var5 + addressToSocketArgs(var8);
                     }
                  }
               }
            } else {
               var2 = addArgument(var2, var7);
            }
         }
      }

      if (var5 == null) {
         var5 = "com.sun.jdi.CommandLineLaunch:";
      } else if (!var5.endsWith(",") && !var5.endsWith(":")) {
         var5 = var5 + ",";
      }

      var1 = var1.trim();
      var2 = var2.trim();
      if (var1.length() > 0) {
         if (!var5.startsWith("com.sun.jdi.CommandLineLaunch:")) {
            usageError("Cannot specify command line with connector:", var5);
            return;
         }

         var5 = var5 + "main=" + var1 + ",";
      }

      if (var2.length() > 0) {
         if (!var5.startsWith("com.sun.jdi.CommandLineLaunch:")) {
            usageError("Cannot specify target vm arguments with connector:", var5);
            return;
         }

         var5 = var5 + "options=" + var2 + ",";
      }

      try {
         if (!var5.endsWith(",")) {
            var5 = var5 + ",";
         }

         Env.init(var5, var4, var3);
         new TTY();
      } catch (Exception var10) {
         MessageOutput.printException("Internal exception:", var10);
      }

   }
}
