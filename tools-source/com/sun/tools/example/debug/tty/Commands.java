package com.sun.tools.example.debug.tty;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PathSearchingVirtualMachine;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.tools.example.debug.expr.ExpressionParser;
import com.sun.tools.example.debug.expr.ParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

class Commands {
   static String methodTraceCommand = null;

   private Value evaluate(String var1) {
      Value var2 = null;
      ExpressionParser.GetFrame var3 = null;

      try {
         final ThreadInfo var4 = ThreadInfo.getCurrentThreadInfo();
         if (var4 != null && var4.getCurrentFrame() != null) {
            var3 = new ExpressionParser.GetFrame() {
               public StackFrame get() throws IncompatibleThreadStateException {
                  return var4.getCurrentFrame();
               }
            };
         }

         var2 = ExpressionParser.evaluate(var1, Env.vm(), var3);
      } catch (InvocationException var9) {
         MessageOutput.println("Exception in expression:", var9.exception().referenceType().name());
      } catch (Exception var10) {
         String var5 = var10.getMessage();
         if (var5 == null) {
            MessageOutput.printException(var5, var10);
         } else {
            String var6;
            try {
               var6 = MessageOutput.format(var5);
            } catch (MissingResourceException var8) {
               var6 = var10.toString();
            }

            MessageOutput.printDirectln(var6);
         }
      }

      return var2;
   }

   private String getStringValue() {
      Value var1 = null;
      String var2 = null;

      try {
         var1 = ExpressionParser.getMassagedValue();
         var2 = var1.toString();
      } catch (ParseException var8) {
         String var4 = var8.getMessage();
         if (var4 == null) {
            MessageOutput.printException(var4, var8);
         } else {
            String var5;
            try {
               var5 = MessageOutput.format(var4);
            } catch (MissingResourceException var7) {
               var5 = var8.toString();
            }

            MessageOutput.printDirectln(var5);
         }
      }

      return var2;
   }

   private ThreadInfo doGetThread(String var1) {
      ThreadInfo var2 = ThreadInfo.getThreadInfo(var1);
      if (var2 == null) {
         MessageOutput.println("is not a valid thread id", var1);
      }

      return var2;
   }

   String typedName(Method var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append(var1.name());
      var2.append("(");
      List var3 = var1.argumentTypeNames();
      int var4 = var3.size() - 1;

      for(int var5 = 0; var5 < var4; ++var5) {
         var2.append((String)var3.get(var5));
         var2.append(", ");
      }

      if (var4 >= 0) {
         String var6 = (String)var3.get(var4);
         if (var1.isVarArgs()) {
            var2.append(var6.substring(0, var6.length() - 2));
            var2.append("...");
         } else {
            var2.append(var6);
         }
      }

      var2.append(")");
      return var2.toString();
   }

   void commandConnectors(VirtualMachineManager var1) {
      List var2 = var1.allConnectors();
      if (var2.isEmpty()) {
         MessageOutput.println("Connectors available");
      }

      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Connector var4 = (Connector)var3.next();
         String var5 = var4.transport() == null ? "null" : var4.transport().name();
         MessageOutput.println();
         MessageOutput.println("Connector and Transport name", new Object[]{var4.name(), var5});
         MessageOutput.println("Connector description", var4.description());

         Connector.Argument var7;
         for(Iterator var6 = var4.defaultArguments().values().iterator(); var6.hasNext(); MessageOutput.println("Connector description", var7.description())) {
            var7 = (Connector.Argument)var6.next();
            MessageOutput.println();
            boolean var8 = var7.mustSpecify();
            if (var7.value() != null && var7.value() != "") {
               MessageOutput.println(var8 ? "Connector required argument default" : "Connector argument default", new Object[]{var7.name(), var7.value()});
            } else {
               MessageOutput.println(var8 ? "Connector required argument nodefault" : "Connector argument nodefault", var7.name());
            }
         }
      }

   }

   void commandClasses() {
      StringBuffer var1 = new StringBuffer();
      Iterator var2 = Env.vm().allClasses().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         var1.append(var3.name());
         var1.append("\n");
      }

      MessageOutput.print("** classes list **", var1.toString());
   }

   void commandClass(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No class specified.");
      } else {
         String var2 = var1.nextToken();
         boolean var3 = false;
         if (var1.hasMoreTokens()) {
            if (!var1.nextToken().toLowerCase().equals("all")) {
               MessageOutput.println("Invalid option on class command");
               return;
            }

            var3 = true;
         }

         ReferenceType var4 = Env.getReferenceTypeFromToken(var2);
         if (var4 == null) {
            MessageOutput.println("is not a valid id or class name", var2);
         } else {
            if (var4 instanceof ClassType) {
               ClassType var5 = (ClassType)var4;
               MessageOutput.println("Class:", var5.name());

               for(ClassType var6 = var5.superclass(); var6 != null; var6 = var3 ? var6.superclass() : null) {
                  MessageOutput.println("extends:", var6.name());
               }

               List var7 = var3 ? var5.allInterfaces() : var5.interfaces();
               Iterator var8 = var7.iterator();

               while(var8.hasNext()) {
                  InterfaceType var9 = (InterfaceType)var8.next();
                  MessageOutput.println("implements:", var9.name());
               }

               var8 = var5.subclasses().iterator();

               while(var8.hasNext()) {
                  ClassType var16 = (ClassType)var8.next();
                  MessageOutput.println("subclass:", var16.name());
               }

               var8 = var5.nestedTypes().iterator();

               while(var8.hasNext()) {
                  ReferenceType var17 = (ReferenceType)var8.next();
                  MessageOutput.println("nested:", var17.name());
               }
            } else if (var4 instanceof InterfaceType) {
               InterfaceType var10 = (InterfaceType)var4;
               MessageOutput.println("Interface:", var10.name());
               Iterator var12 = var10.superinterfaces().iterator();

               InterfaceType var13;
               while(var12.hasNext()) {
                  var13 = (InterfaceType)var12.next();
                  MessageOutput.println("extends:", var13.name());
               }

               var12 = var10.subinterfaces().iterator();

               while(var12.hasNext()) {
                  var13 = (InterfaceType)var12.next();
                  MessageOutput.println("subinterface:", var13.name());
               }

               var12 = var10.implementors().iterator();

               while(var12.hasNext()) {
                  ClassType var14 = (ClassType)var12.next();
                  MessageOutput.println("implementor:", var14.name());
               }

               var12 = var10.nestedTypes().iterator();

               while(var12.hasNext()) {
                  ReferenceType var15 = (ReferenceType)var12.next();
                  MessageOutput.println("nested:", var15.name());
               }
            } else {
               ArrayType var11 = (ArrayType)var4;
               MessageOutput.println("Array:", var11.name());
            }

         }
      }
   }

   void commandMethods(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No class specified.");
      } else {
         String var2 = var1.nextToken();
         ReferenceType var3 = Env.getReferenceTypeFromToken(var2);
         if (var3 != null) {
            StringBuffer var4 = new StringBuffer();
            Iterator var5 = var3.allMethods().iterator();

            while(var5.hasNext()) {
               Method var6 = (Method)var5.next();
               var4.append(var6.declaringType().name());
               var4.append(" ");
               var4.append(this.typedName(var6));
               var4.append('\n');
            }

            MessageOutput.print("** methods list **", var4.toString());
         } else {
            MessageOutput.println("is not a valid id or class name", var2);
         }

      }
   }

   void commandFields(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No class specified.");
      } else {
         String var2 = var1.nextToken();
         ReferenceType var3 = Env.getReferenceTypeFromToken(var2);
         if (var3 != null) {
            List var4 = var3.allFields();
            List var5 = var3.visibleFields();
            StringBuffer var6 = new StringBuffer();

            String var9;
            for(Iterator var7 = var4.iterator(); var7.hasNext(); var6.append(var9)) {
               Field var8 = (Field)var7.next();
               if (!var5.contains(var8)) {
                  var9 = MessageOutput.format("list field typename and name hidden", new Object[]{var8.typeName(), var8.name()});
               } else if (!var8.declaringType().equals(var3)) {
                  var9 = MessageOutput.format("list field typename and name inherited", new Object[]{var8.typeName(), var8.name(), var8.declaringType().name()});
               } else {
                  var9 = MessageOutput.format("list field typename and name", new Object[]{var8.typeName(), var8.name()});
               }
            }

            MessageOutput.print("** fields list **", var6.toString());
         } else {
            MessageOutput.println("is not a valid id or class name", var2);
         }

      }
   }

   private void printThreadGroup(ThreadGroupReference var1) {
      ThreadIterator var2 = new ThreadIterator(var1);
      MessageOutput.println("Thread Group:", var1.name());
      int var3 = 0;

      int var4;
      ThreadReference var5;
      for(var4 = 0; var2.hasNext(); var4 = Math.max(var4, var5.name().length())) {
         var5 = var2.next();
         var3 = Math.max(var3, Env.description(var5).length());
      }

      var2 = new ThreadIterator(var1);

      while(true) {
         do {
            if (!var2.hasNext()) {
               return;
            }

            var5 = var2.next();
         } while(var5.threadGroup() == null);

         if (!var5.threadGroup().equals(var1)) {
            var1 = var5.threadGroup();
            MessageOutput.println("Thread Group:", var1.name());
         }

         StringBuffer var6 = new StringBuffer(Env.description(var5));

         for(int var7 = var6.length(); var7 < var3; ++var7) {
            var6.append(" ");
         }

         StringBuffer var9 = new StringBuffer(var5.name());

         for(int var8 = var9.length(); var8 < var4; ++var8) {
            var9.append(" ");
         }

         String var10;
         switch (var5.status()) {
            case -1:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name unknownStatus BP";
               } else {
                  var10 = "Thread description name unknownStatus";
               }
               break;
            case 0:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name zombieStatus BP";
               } else {
                  var10 = "Thread description name zombieStatus";
               }
               break;
            case 1:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name runningStatus BP";
               } else {
                  var10 = "Thread description name runningStatus";
               }
               break;
            case 2:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name sleepingStatus BP";
               } else {
                  var10 = "Thread description name sleepingStatus";
               }
               break;
            case 3:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name waitingStatus BP";
               } else {
                  var10 = "Thread description name waitingStatus";
               }
               break;
            case 4:
               if (var5.isAtBreakpoint()) {
                  var10 = "Thread description name condWaitstatus BP";
               } else {
                  var10 = "Thread description name condWaitstatus";
               }
               break;
            default:
               throw new InternalError(MessageOutput.format("Invalid thread status."));
         }

         MessageOutput.println(var10, new Object[]{var6.toString(), var9.toString()});
      }
   }

   void commandThreads(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         this.printThreadGroup(ThreadInfo.group());
      } else {
         String var2 = var1.nextToken();
         ThreadGroupReference var3 = ThreadGroupIterator.find(var2);
         if (var3 == null) {
            MessageOutput.println("is not a valid threadgroup name", var2);
         } else {
            this.printThreadGroup(var3);
         }

      }
   }

   void commandThreadGroups() {
      ThreadGroupIterator var1 = new ThreadGroupIterator();
      int var2 = 0;

      while(var1.hasNext()) {
         ThreadGroupReference var3 = var1.nextThreadGroup();
         ++var2;
         MessageOutput.println("thread group number description name", new Object[]{new Integer(var2), Env.description(var3), var3.name()});
      }

   }

   void commandThread(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Thread number not specified.");
      } else {
         ThreadInfo var2 = this.doGetThread(var1.nextToken());
         if (var2 != null) {
            ThreadInfo.setCurrentThreadInfo(var2);
         }

      }
   }

   void commandThreadGroup(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Threadgroup name not specified.");
      } else {
         String var2 = var1.nextToken();
         ThreadGroupReference var3 = ThreadGroupIterator.find(var2);
         if (var3 == null) {
            MessageOutput.println("is not a valid threadgroup name", var2);
         } else {
            ThreadInfo.setThreadGroup(var3);
         }

      }
   }

   void commandRun(StringTokenizer var1) {
      VMConnection var2 = Env.connection();
      if (!var2.isLaunch()) {
         if (!var1.hasMoreTokens()) {
            this.commandCont();
         } else {
            MessageOutput.println("run <args> command is valid only with launched VMs");
         }

      } else if (var2.isOpen()) {
         MessageOutput.println("VM already running. use cont to continue after events.");
      } else {
         String var3;
         if (var1.hasMoreTokens()) {
            var3 = var1.nextToken("");
            boolean var4 = var2.setConnectorArg("main", var3);
            if (!var4) {
               MessageOutput.println("Unable to set main class and arguments");
               return;
            }
         } else {
            var3 = var2.connectorArg("main");
            if (var3.length() == 0) {
               MessageOutput.println("Main class and arguments must be specified");
               return;
            }
         }

         MessageOutput.println("run", var3);
         var2.open();
      }
   }

   void commandLoad(StringTokenizer var1) {
      MessageOutput.println("The load command is no longer supported.");
   }

   private List allThreads(ThreadGroupReference var1) {
      ArrayList var2 = new ArrayList();
      var2.addAll(var1.threads());
      Iterator var3 = var1.threadGroups().iterator();

      while(var3.hasNext()) {
         ThreadGroupReference var4 = (ThreadGroupReference)var3.next();
         var2.addAll(this.allThreads(var4));
      }

      return var2;
   }

   void commandSuspend(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         Env.vm().suspend();
         MessageOutput.println("All threads suspended.");
      } else {
         while(var1.hasMoreTokens()) {
            ThreadInfo var2 = this.doGetThread(var1.nextToken());
            if (var2 != null) {
               var2.getThread().suspend();
            }
         }
      }

   }

   void commandResume(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         ThreadInfo.invalidateAll();
         Env.vm().resume();
         MessageOutput.println("All threads resumed.");
      } else {
         while(var1.hasMoreTokens()) {
            ThreadInfo var2 = this.doGetThread(var1.nextToken());
            if (var2 != null) {
               var2.invalidate();
               var2.getThread().resume();
            }
         }
      }

   }

   void commandCont() {
      if (ThreadInfo.getCurrentThreadInfo() == null) {
         MessageOutput.println("Nothing suspended.");
      } else {
         ThreadInfo.invalidateAll();
         Env.vm().resume();
      }
   }

   void clearPreviousStep(ThreadReference var1) {
      EventRequestManager var2 = Env.vm().eventRequestManager();
      Iterator var3 = var2.stepRequests().iterator();

      while(var3.hasNext()) {
         StepRequest var4 = (StepRequest)var3.next();
         if (var4.thread().equals(var1)) {
            var2.deleteEventRequest(var4);
            break;
         }
      }

   }

   void commandStep(StringTokenizer var1) {
      ThreadInfo var2 = ThreadInfo.getCurrentThreadInfo();
      if (var2 == null) {
         MessageOutput.println("Nothing suspended.");
      } else {
         byte var3;
         if (var1.hasMoreTokens() && var1.nextToken().toLowerCase().equals("up")) {
            var3 = 3;
         } else {
            var3 = 1;
         }

         this.clearPreviousStep(var2.getThread());
         EventRequestManager var4 = Env.vm().eventRequestManager();
         StepRequest var5 = var4.createStepRequest(var2.getThread(), -2, var3);
         if (var3 == 1) {
            Env.addExcludes(var5);
         }

         var5.addCountFilter(1);
         var5.enable();
         ThreadInfo.invalidateAll();
         Env.vm().resume();
      }
   }

   void commandStepi() {
      ThreadInfo var1 = ThreadInfo.getCurrentThreadInfo();
      if (var1 == null) {
         MessageOutput.println("Nothing suspended.");
      } else {
         this.clearPreviousStep(var1.getThread());
         EventRequestManager var2 = Env.vm().eventRequestManager();
         StepRequest var3 = var2.createStepRequest(var1.getThread(), -1, 1);
         Env.addExcludes(var3);
         var3.addCountFilter(1);
         var3.enable();
         ThreadInfo.invalidateAll();
         Env.vm().resume();
      }
   }

   void commandNext() {
      ThreadInfo var1 = ThreadInfo.getCurrentThreadInfo();
      if (var1 == null) {
         MessageOutput.println("Nothing suspended.");
      } else {
         this.clearPreviousStep(var1.getThread());
         EventRequestManager var2 = Env.vm().eventRequestManager();
         StepRequest var3 = var2.createStepRequest(var1.getThread(), -2, 2);
         Env.addExcludes(var3);
         var3.addCountFilter(1);
         var3.enable();
         ThreadInfo.invalidateAll();
         Env.vm().resume();
      }
   }

   void doKill(ThreadReference var1, StringTokenizer var2) {
      if (!var2.hasMoreTokens()) {
         MessageOutput.println("No exception object specified.");
      } else {
         String var3 = var2.nextToken("");
         Value var4 = this.evaluate(var3);
         if (var4 != null && var4 instanceof ObjectReference) {
            try {
               var1.stop((ObjectReference)var4);
               MessageOutput.println("killed", var1.toString());
            } catch (InvalidTypeException var6) {
               MessageOutput.println("Invalid exception object");
            }
         } else {
            MessageOutput.println("Expression must evaluate to an object");
         }

      }
   }

   void doKillThread(final ThreadReference var1, final StringTokenizer var2) {
      AsyncExecution var10001 = new AsyncExecution() {
         void action() {
            Commands.this.doKill(var1, var2);
         }
      };
   }

   void commandKill(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Usage: kill <thread id> <throwable>");
      } else {
         ThreadInfo var2 = this.doGetThread(var1.nextToken());
         if (var2 != null) {
            MessageOutput.println("killing thread:", var2.getThread().name());
            this.doKillThread(var2.getThread(), var1);
         }
      }
   }

   void listCaughtExceptions() {
      boolean var1 = true;
      Iterator var2 = Env.specList.eventRequestSpecs().iterator();

      while(var2.hasNext()) {
         EventRequestSpec var3 = (EventRequestSpec)var2.next();
         if (var3 instanceof ExceptionSpec) {
            if (var1) {
               var1 = false;
               MessageOutput.println("Exceptions caught:");
            }

            MessageOutput.println("tab", var3.toString());
         }
      }

      if (var1) {
         MessageOutput.println("No exceptions caught.");
      }

   }

   private EventRequestSpec parseExceptionSpec(StringTokenizer var1) {
      String var2 = var1.nextToken();
      boolean var3 = false;
      boolean var4 = false;
      EventRequestSpec var5 = null;
      String var6 = null;
      if (var2.equals("uncaught")) {
         var3 = false;
         var4 = true;
      } else if (var2.equals("caught")) {
         var3 = true;
         var4 = false;
      } else if (var2.equals("all")) {
         var3 = true;
         var4 = true;
      } else {
         var3 = true;
         var4 = true;
         var6 = var2;
      }

      if (var6 == null && var1.hasMoreTokens()) {
         var6 = var1.nextToken();
      }

      if (var6 != null && (var3 || var4)) {
         try {
            var5 = Env.specList.createExceptionCatch(var6, var3, var4);
         } catch (ClassNotFoundException var8) {
            MessageOutput.println("is not a valid class name", var6);
         }
      }

      return var5;
   }

   void commandCatchException(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         this.listCaughtExceptions();
      } else {
         EventRequestSpec var2 = this.parseExceptionSpec(var1);
         if (var2 != null) {
            this.resolveNow(var2);
         } else {
            MessageOutput.println("Usage: catch exception");
         }
      }

   }

   void commandIgnoreException(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         this.listCaughtExceptions();
      } else {
         EventRequestSpec var2 = this.parseExceptionSpec(var1);
         if (Env.specList.delete(var2)) {
            MessageOutput.println("Removed:", var2.toString());
         } else {
            if (var2 != null) {
               MessageOutput.println("Not found:", var2.toString());
            }

            MessageOutput.println("Usage: ignore exception");
         }
      }

   }

   void commandUp(StringTokenizer var1) {
      ThreadInfo var2 = ThreadInfo.getCurrentThreadInfo();
      if (var2 == null) {
         MessageOutput.println("Current thread not set.");
      } else {
         int var3 = 1;
         if (var1.hasMoreTokens()) {
            String var4 = var1.nextToken();

            int var5;
            try {
               NumberFormat var6 = NumberFormat.getNumberInstance();
               var6.setParseIntegerOnly(true);
               Number var7 = var6.parse(var4);
               var5 = var7.intValue();
            } catch (java.text.ParseException var10) {
               var5 = 0;
            }

            if (var5 <= 0) {
               MessageOutput.println("Usage: up [n frames]");
               return;
            }

            var3 = var5;
         }

         try {
            var2.up(var3);
         } catch (IncompatibleThreadStateException var8) {
            MessageOutput.println("Current thread isnt suspended.");
         } catch (ArrayIndexOutOfBoundsException var9) {
            MessageOutput.println("End of stack.");
         }

      }
   }

   void commandDown(StringTokenizer var1) {
      ThreadInfo var2 = ThreadInfo.getCurrentThreadInfo();
      if (var2 == null) {
         MessageOutput.println("Current thread not set.");
      } else {
         int var3 = 1;
         if (var1.hasMoreTokens()) {
            String var4 = var1.nextToken();

            int var5;
            try {
               NumberFormat var6 = NumberFormat.getNumberInstance();
               var6.setParseIntegerOnly(true);
               Number var7 = var6.parse(var4);
               var5 = var7.intValue();
            } catch (java.text.ParseException var10) {
               var5 = 0;
            }

            if (var5 <= 0) {
               MessageOutput.println("Usage: down [n frames]");
               return;
            }

            var3 = var5;
         }

         try {
            var2.down(var3);
         } catch (IncompatibleThreadStateException var8) {
            MessageOutput.println("Current thread isnt suspended.");
         } catch (ArrayIndexOutOfBoundsException var9) {
            MessageOutput.println("End of stack.");
         }

      }
   }

   private void dumpStack(ThreadInfo var1, boolean var2) {
      List var3 = null;

      try {
         var3 = var1.getStack();
      } catch (IncompatibleThreadStateException var7) {
         MessageOutput.println("Current thread isnt suspended.");
         return;
      }

      if (var3 == null) {
         MessageOutput.println("Thread is not running (no stack).");
      } else {
         int var4 = var3.size();

         for(int var5 = var1.getCurrentFrameIndex(); var5 < var4; ++var5) {
            StackFrame var6 = (StackFrame)var3.get(var5);
            this.dumpFrame(var5, var2, var6);
         }
      }

   }

   private void dumpFrame(int var1, boolean var2, StackFrame var3) {
      Location var4 = var3.location();
      long var5 = -1L;
      if (var2) {
         var5 = var4.codeIndex();
      }

      Method var7 = var4.method();
      long var8 = (long)var4.lineNumber();
      String var10 = null;
      if (var7.isNative()) {
         var10 = MessageOutput.format("native method");
      } else if (var8 != -1L) {
         try {
            var10 = var4.sourceName() + MessageOutput.format("line number", new Object[]{new Long(var8)});
         } catch (AbsentInformationException var12) {
            var10 = MessageOutput.format("unknown");
         }
      }

      if (var5 != -1L) {
         MessageOutput.println("stack frame dump with pc", new Object[]{new Integer(var1 + 1), var7.declaringType().name(), var7.name(), var10, new Long(var5)});
      } else {
         MessageOutput.println("stack frame dump", new Object[]{new Integer(var1 + 1), var7.declaringType().name(), var7.name(), var10});
      }

   }

   void commandWhere(StringTokenizer var1, boolean var2) {
      if (!var1.hasMoreTokens()) {
         ThreadInfo var3 = ThreadInfo.getCurrentThreadInfo();
         if (var3 == null) {
            MessageOutput.println("No thread specified.");
            return;
         }

         this.dumpStack(var3, var2);
      } else {
         String var6 = var1.nextToken();
         if (var6.toLowerCase().equals("all")) {
            Iterator var4 = ThreadInfo.threads().iterator();

            while(var4.hasNext()) {
               ThreadInfo var5 = (ThreadInfo)var4.next();
               MessageOutput.println("Thread:", var5.getThread().name());
               this.dumpStack(var5, var2);
            }
         } else {
            ThreadInfo var7 = this.doGetThread(var6);
            if (var7 != null) {
               ThreadInfo.setCurrentThreadInfo(var7);
               this.dumpStack(var7, var2);
            }
         }
      }

   }

   void commandInterrupt(StringTokenizer var1) {
      ThreadInfo var2;
      if (!var1.hasMoreTokens()) {
         var2 = ThreadInfo.getCurrentThreadInfo();
         if (var2 == null) {
            MessageOutput.println("No thread specified.");
            return;
         }

         var2.getThread().interrupt();
      } else {
         var2 = this.doGetThread(var1.nextToken());
         if (var2 != null) {
            var2.getThread().interrupt();
         }
      }

   }

   void commandMemory() {
      MessageOutput.println("The memory command is no longer supported.");
   }

   void commandGC() {
      MessageOutput.println("The gc command is no longer necessary.");
   }

   static String locationString(Location var0) {
      return MessageOutput.format("locationString", new Object[]{var0.declaringType().name(), var0.method().name(), new Integer(var0.lineNumber()), new Long(var0.codeIndex())});
   }

   void listBreakpoints() {
      boolean var1 = true;
      Iterator var2 = Env.specList.eventRequestSpecs().iterator();

      while(var2.hasNext()) {
         EventRequestSpec var3 = (EventRequestSpec)var2.next();
         if (var3 instanceof BreakpointSpec) {
            if (var1) {
               var1 = false;
               MessageOutput.println("Breakpoints set:");
            }

            MessageOutput.println("tab", var3.toString());
         }
      }

      if (var1) {
         MessageOutput.println("No breakpoints set.");
      }

   }

   private void printBreakpointCommandUsage(String var1, String var2) {
      MessageOutput.println("printbreakpointcommandusage", new Object[]{var1, var2});
   }

   protected BreakpointSpec parseBreakpointSpec(StringTokenizer var1, String var2, String var3) {
      BreakpointSpec var4 = null;

      try {
         String var5 = var1.nextToken(":( \t\n\r");

         String var6;
         try {
            var6 = var1.nextToken("").trim();
         } catch (NoSuchElementException var16) {
            var6 = null;
         }

         String var8;
         if (var6 != null && var6.startsWith(":")) {
            var1 = new StringTokenizer(var6.substring(1));
            String var18 = var5;
            var8 = var1.nextToken();
            NumberFormat var19 = NumberFormat.getNumberInstance();
            var19.setParseIntegerOnly(true);
            Number var20 = var19.parse(var8);
            int var11 = var20.intValue();
            if (var1.hasMoreTokens()) {
               this.printBreakpointCommandUsage(var2, var3);
               return null;
            }

            try {
               var4 = Env.specList.createBreakpoint(var18, var11);
            } catch (ClassNotFoundException var15) {
               MessageOutput.println("is not a valid class name", var5);
            }
         } else {
            int var7 = var5.lastIndexOf(".");
            if (var7 <= 0 || var7 >= var5.length() - 1) {
               this.printBreakpointCommandUsage(var2, var3);
               return null;
            }

            var8 = var5.substring(var7 + 1);
            String var9 = var5.substring(0, var7);
            ArrayList var10 = null;
            if (var6 != null) {
               if (!var6.startsWith("(") || !var6.endsWith(")")) {
                  MessageOutput.println("Invalid method specification:", var8 + var6);
                  this.printBreakpointCommandUsage(var2, var3);
                  return null;
               }

               var6 = var6.substring(1, var6.length() - 1);
               var10 = new ArrayList();
               var1 = new StringTokenizer(var6, ",");

               while(var1.hasMoreTokens()) {
                  var10.add(var1.nextToken());
               }
            }

            try {
               var4 = Env.specList.createBreakpoint(var9, var8, var10);
            } catch (MalformedMemberNameException var13) {
               MessageOutput.println("is not a valid method name", var8);
            } catch (ClassNotFoundException var14) {
               MessageOutput.println("is not a valid class name", var9);
            }
         }

         return var4;
      } catch (Exception var17) {
         this.printBreakpointCommandUsage(var2, var3);
         return null;
      }
   }

   private void resolveNow(EventRequestSpec var1) {
      boolean var2 = Env.specList.addEagerlyResolve(var1);
      if (var2 && !var1.isResolved()) {
         MessageOutput.println("Deferring.", var1.toString());
      }

   }

   void commandStop(StringTokenizer var1) {
      byte var3 = 2;
      if (!var1.hasMoreTokens()) {
         this.listBreakpoints();
      } else {
         String var2 = var1.nextToken();
         if (var2.equals("go") && var1.hasMoreTokens()) {
            var3 = 0;
            var2 = var1.nextToken();
         } else if (var2.equals("thread") && var1.hasMoreTokens()) {
            var3 = 1;
            var2 = var1.nextToken();
         }

         BreakpointSpec var4 = this.parseBreakpointSpec(var1, "stop at", "stop in");
         if (var4 != null) {
            if (var2.equals("at") && var4.isMethodBreakpoint()) {
               MessageOutput.println("Use stop at to set a breakpoint at a line number");
               this.printBreakpointCommandUsage("stop at", "stop in");
               return;
            }

            var4.suspendPolicy = var3;
            this.resolveNow(var4);
         }

      }
   }

   void commandClear(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         this.listBreakpoints();
      } else {
         BreakpointSpec var2 = this.parseBreakpointSpec(var1, "clear", "clear");
         if (var2 != null) {
            if (Env.specList.delete(var2)) {
               MessageOutput.println("Removed:", var2.toString());
            } else {
               MessageOutput.println("Not found:", var2.toString());
            }
         }

      }
   }

   private List parseWatchpointSpec(StringTokenizer var1) {
      ArrayList var2 = new ArrayList();
      boolean var3 = false;
      boolean var4 = false;
      byte var5 = 2;
      String var6 = var1.nextToken();
      if (var6.equals("go")) {
         var5 = 0;
         var6 = var1.nextToken();
      } else if (var6.equals("thread")) {
         var5 = 1;
         var6 = var1.nextToken();
      }

      if (var6.equals("access")) {
         var3 = true;
         var6 = var1.nextToken();
      } else if (var6.equals("all")) {
         var3 = true;
         var4 = true;
         var6 = var1.nextToken();
      } else {
         var4 = true;
      }

      int var7 = var6.lastIndexOf(46);
      if (var7 < 0) {
         MessageOutput.println("Class containing field must be specified.");
         return var2;
      } else {
         String var8 = var6.substring(0, var7);
         var6 = var6.substring(var7 + 1);

         try {
            WatchpointSpec var9;
            if (var3) {
               var9 = Env.specList.createAccessWatchpoint(var8, var6);
               var9.suspendPolicy = var5;
               var2.add(var9);
            }

            if (var4) {
               var9 = Env.specList.createModificationWatchpoint(var8, var6);
               var9.suspendPolicy = var5;
               var2.add(var9);
            }
         } catch (MalformedMemberNameException var10) {
            MessageOutput.println("is not a valid field name", var6);
         } catch (ClassNotFoundException var11) {
            MessageOutput.println("is not a valid class name", var8);
         }

         return var2;
      }
   }

   void commandWatch(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Field to watch not specified");
      } else {
         Iterator var2 = this.parseWatchpointSpec(var1).iterator();

         while(var2.hasNext()) {
            WatchpointSpec var3 = (WatchpointSpec)var2.next();
            this.resolveNow(var3);
         }

      }
   }

   void commandUnwatch(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Field to unwatch not specified");
      } else {
         Iterator var2 = this.parseWatchpointSpec(var1).iterator();

         while(var2.hasNext()) {
            WatchpointSpec var3 = (WatchpointSpec)var2.next();
            if (Env.specList.delete(var3)) {
               MessageOutput.println("Removed:", var3.toString());
            } else {
               MessageOutput.println("Not found:", var3.toString());
            }
         }

      }
   }

   void turnOnExitTrace(ThreadInfo var1, int var2) {
      EventRequestManager var3 = Env.vm().eventRequestManager();
      MethodExitRequest var4 = var3.createMethodExitRequest();
      if (var1 != null) {
         var4.addThreadFilter(var1.getThread());
      }

      Env.addExcludes(var4);
      var4.setSuspendPolicy(var2);
      var4.enable();
   }

   void commandTrace(StringTokenizer var1) {
      byte var3 = 2;
      ThreadInfo var4 = null;
      String var5 = " ";
      if (var1.hasMoreTokens()) {
         String var2 = var1.nextToken();
         if (var2.equals("go")) {
            var3 = 0;
            var5 = " go ";
            if (var1.hasMoreTokens()) {
               var2 = var1.nextToken();
            }
         } else if (var2.equals("thread")) {
            var3 = 1;
            if (var1.hasMoreTokens()) {
               var2 = var1.nextToken();
            }
         }

         MethodEntryRequest var6;
         if (var2.equals("method")) {
            var6 = null;
            if (!var1.hasMoreTokens()) {
               MessageOutput.println("Can only trace");
               return;
            }

            String var7 = var1.nextToken();
            if (var7.equals("exits") || var7.equals("exit")) {
               if (var1.hasMoreTokens()) {
                  var4 = this.doGetThread(var1.nextToken());
               }

               String var12;
               if (var7.equals("exit")) {
                  StackFrame var8;
                  try {
                     var8 = ThreadInfo.getCurrentThreadInfo().getCurrentFrame();
                  } catch (IncompatibleThreadStateException var10) {
                     MessageOutput.println("Current thread isnt suspended.");
                     return;
                  }

                  Env.setAtExitMethod(var8.location().method());
                  var12 = MessageOutput.format("trace" + var5 + "method exit in effect for", Env.atExitMethod().toString());
               } else {
                  var12 = MessageOutput.format("trace" + var5 + "method exits in effect");
               }

               this.commandUntrace(new StringTokenizer("methods"));
               this.turnOnExitTrace(var4, var3);
               methodTraceCommand = var12;
               return;
            }
         }

         if (var2.equals("methods")) {
            EventRequestManager var11 = Env.vm().eventRequestManager();
            if (var1.hasMoreTokens()) {
               var4 = this.doGetThread(var1.nextToken());
            }

            if (var4 != null) {
               var6 = var11.createMethodEntryRequest();
               var6.addThreadFilter(var4.getThread());
            } else {
               this.commandUntrace(new StringTokenizer("methods"));
               var6 = var11.createMethodEntryRequest();
            }

            Env.addExcludes(var6);
            var6.setSuspendPolicy(var3);
            var6.enable();
            this.turnOnExitTrace(var4, var3);
            methodTraceCommand = MessageOutput.format("trace" + var5 + "methods in effect");
         } else {
            MessageOutput.println("Can only trace");
         }
      } else {
         if (methodTraceCommand != null) {
            MessageOutput.printDirectln(methodTraceCommand);
         }

      }
   }

   void commandUntrace(StringTokenizer var1) {
      String var2 = null;
      EventRequestManager var3 = Env.vm().eventRequestManager();
      if (var1.hasMoreTokens()) {
         var2 = var1.nextToken();
      }

      if (var2 == null || var2.equals("methods")) {
         var3.deleteEventRequests(var3.methodEntryRequests());
         var3.deleteEventRequests(var3.methodExitRequests());
         Env.setAtExitMethod((Method)null);
         methodTraceCommand = null;
      }

   }

   void commandList(StringTokenizer var1) {
      StackFrame var2 = null;
      ThreadInfo var3 = ThreadInfo.getCurrentThreadInfo();
      if (var3 == null) {
         MessageOutput.println("No thread specified.");
      } else {
         try {
            var2 = var3.getCurrentFrame();
         } catch (IncompatibleThreadStateException var12) {
            MessageOutput.println("Current thread isnt suspended.");
            return;
         }

         if (var2 == null) {
            MessageOutput.println("No frames on the current call stack");
         } else {
            Location var4 = var2.location();
            if (var4.method().isNative()) {
               MessageOutput.println("Current method is native");
            } else {
               String var5 = null;

               try {
                  var5 = var4.sourceName();
                  ReferenceType var6 = var4.declaringType();
                  int var7 = var4.lineNumber();
                  if (var1.hasMoreTokens()) {
                     String var8 = var1.nextToken();

                     try {
                        NumberFormat var9 = NumberFormat.getNumberInstance();
                        var9.setParseIntegerOnly(true);
                        Number var19 = var9.parse(var8);
                        var7 = var19.intValue();
                     } catch (java.text.ParseException var13) {
                        List var10 = var6.methodsByName(var8);
                        if (var10 == null || var10.size() == 0) {
                           MessageOutput.println("is not a valid line number or method name for", new Object[]{var8, var6.name()});
                           return;
                        }

                        if (var10.size() > 1) {
                           MessageOutput.println("is an ambiguous method name in", new Object[]{var8, var6.name()});
                           return;
                        }

                        var4 = ((Method)var10.get(0)).location();
                        var7 = var4.lineNumber();
                     }
                  }

                  int var17 = Math.max(var7 - 4, 1);
                  int var18 = var17 + 9;
                  if (var7 < 0) {
                     MessageOutput.println("Line number information not available for");
                  } else if (Env.sourceLine(var4, var7) == null) {
                     MessageOutput.println("is an invalid line number for", new Object[]{new Integer(var7), var6.name()});
                  } else {
                     for(int var20 = var17; var20 <= var18; ++var20) {
                        String var11 = Env.sourceLine(var4, var20);
                        if (var11 == null) {
                           break;
                        }

                        if (var20 == var7) {
                           MessageOutput.println("source line number current line and line", new Object[]{new Integer(var20), var11});
                        } else {
                           MessageOutput.println("source line number and line", new Object[]{new Integer(var20), var11});
                        }
                     }
                  }
               } catch (AbsentInformationException var14) {
                  MessageOutput.println("No source information available for:", var4.toString());
               } catch (FileNotFoundException var15) {
                  MessageOutput.println("Source file not found:", var5);
               } catch (IOException var16) {
                  MessageOutput.println("I/O exception occurred:", var16.toString());
               }

            }
         }
      }
   }

   void commandLines(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Specify class and method");
      } else {
         String var2 = var1.nextToken();
         String var3 = var1.hasMoreTokens() ? var1.nextToken() : null;

         try {
            ReferenceType var4 = Env.getReferenceTypeFromToken(var2);
            if (var4 != null) {
               List var5 = null;
               Iterator var6;
               if (var3 == null) {
                  var5 = var4.allLineLocations();
               } else {
                  var6 = var4.allMethods().iterator();

                  while(var6.hasNext()) {
                     Method var7 = (Method)var6.next();
                     if (var7.name().equals(var3)) {
                        var5 = var7.allLineLocations();
                     }
                  }

                  if (var5 == null) {
                     MessageOutput.println("is not a valid method name", var3);
                  }
               }

               var6 = var5.iterator();

               while(var6.hasNext()) {
                  Location var9 = (Location)var6.next();
                  MessageOutput.printDirectln(var9.toString());
               }
            } else {
               MessageOutput.println("is not a valid id or class name", var2);
            }
         } catch (AbsentInformationException var8) {
            MessageOutput.println("Line number information not available for", var2);
         }
      }

   }

   void commandClasspath(StringTokenizer var1) {
      if (Env.vm() instanceof PathSearchingVirtualMachine) {
         PathSearchingVirtualMachine var2 = (PathSearchingVirtualMachine)Env.vm();
         MessageOutput.println("base directory:", var2.baseDirectory());
         MessageOutput.println("classpath:", var2.classPath().toString());
         MessageOutput.println("bootclasspath:", var2.bootClassPath().toString());
      } else {
         MessageOutput.println("The VM does not use paths");
      }

   }

   void commandUse(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.printDirectln(Env.getSourcePath());
      } else {
         Env.setSourcePath(var1.nextToken("").trim());
      }

   }

   private void printVar(LocalVariable var1, Value var2) {
      MessageOutput.println("expr is value", new Object[]{var1.name(), var2 == null ? "null" : var2.toString()});
   }

   void commandLocals() {
      ThreadInfo var2 = ThreadInfo.getCurrentThreadInfo();
      if (var2 == null) {
         MessageOutput.println("No default thread specified:");
      } else {
         try {
            StackFrame var1 = var2.getCurrentFrame();
            if (var1 == null) {
               throw new AbsentInformationException();
            }

            List var3 = var1.visibleVariables();
            if (var3.size() == 0) {
               MessageOutput.println("No local variables");
               return;
            }

            Map var4 = var1.getValues(var3);
            MessageOutput.println("Method arguments:");
            Iterator var5 = var3.iterator();

            LocalVariable var6;
            Value var7;
            while(var5.hasNext()) {
               var6 = (LocalVariable)var5.next();
               if (var6.isArgument()) {
                  var7 = (Value)var4.get(var6);
                  this.printVar(var6, var7);
               }
            }

            MessageOutput.println("Local variables:");
            var5 = var3.iterator();

            while(var5.hasNext()) {
               var6 = (LocalVariable)var5.next();
               if (!var6.isArgument()) {
                  var7 = (Value)var4.get(var6);
                  this.printVar(var6, var7);
               }
            }
         } catch (AbsentInformationException var8) {
            MessageOutput.println("Local variable information not available.");
         } catch (IncompatibleThreadStateException var9) {
            MessageOutput.println("Current thread isnt suspended.");
         }

      }
   }

   private void dump(ObjectReference var1, ReferenceType var2, ReferenceType var3) {
      Iterator var4 = var2.fields().iterator();

      while(var4.hasNext()) {
         Field var5 = (Field)var4.next();
         StringBuffer var6 = new StringBuffer();
         var6.append("    ");
         if (!var2.equals(var3)) {
            var6.append(var2.name());
            var6.append(".");
         }

         var6.append(var5.name());
         var6.append(MessageOutput.format("colon space"));
         var6.append(var1.getValue(var5));
         MessageOutput.printDirectln(var6.toString());
      }

      if (var2 instanceof ClassType) {
         ClassType var7 = ((ClassType)var2).superclass();
         if (var7 != null) {
            this.dump(var1, var7, var3);
         }
      } else if (var2 instanceof InterfaceType) {
         var4 = ((InterfaceType)var2).superinterfaces().iterator();

         while(var4.hasNext()) {
            InterfaceType var8 = (InterfaceType)var4.next();
            this.dump(var1, var8, var3);
         }
      } else if (var1 instanceof ArrayReference) {
         var4 = ((ArrayReference)var1).getValues().iterator();

         while(var4.hasNext()) {
            MessageOutput.printDirect(((Value)var4.next()).toString());
            if (var4.hasNext()) {
               MessageOutput.printDirect(", ");
            }
         }

         MessageOutput.println();
      }

   }

   void doPrint(StringTokenizer var1, boolean var2) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No objects specified.");
      } else {
         while(true) {
            while(var1.hasMoreTokens()) {
               String var3 = var1.nextToken("");
               Value var4 = this.evaluate(var3);
               if (var4 == null) {
                  MessageOutput.println("expr is null", var3.toString());
               } else if (var2 && var4 instanceof ObjectReference && !(var4 instanceof StringReference)) {
                  ObjectReference var7 = (ObjectReference)var4;
                  ReferenceType var6 = var7.referenceType();
                  MessageOutput.println("expr is value", new Object[]{var3.toString(), MessageOutput.format("grouping begin character")});
                  this.dump(var7, var6, var6);
                  MessageOutput.println("grouping end character");
               } else {
                  String var5 = this.getStringValue();
                  if (var5 != null) {
                     MessageOutput.println("expr is value", new Object[]{var3.toString(), var5});
                  }
               }
            }

            return;
         }
      }
   }

   void commandPrint(final StringTokenizer var1, final boolean var2) {
      AsyncExecution var10001 = new AsyncExecution() {
         void action() {
            Commands.this.doPrint(var1, var2);
         }
      };
   }

   void commandSet(StringTokenizer var1) {
      String var2 = var1.nextToken("");
      if (var2.indexOf(61) == -1) {
         MessageOutput.println("Invalid assignment syntax");
         MessageOutput.printPrompt();
      } else {
         this.commandPrint(new StringTokenizer(var2), false);
      }
   }

   void doLock(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No object specified.");
      } else {
         String var2 = var1.nextToken("");
         Value var3 = this.evaluate(var2);

         try {
            if (var3 != null && var3 instanceof ObjectReference) {
               ObjectReference var4 = (ObjectReference)var3;
               String var5 = this.getStringValue();
               if (var5 != null) {
                  MessageOutput.println("Monitor information for expr", new Object[]{var2.trim(), var5});
               }

               ThreadReference var6 = var4.owningThread();
               if (var6 == null) {
                  MessageOutput.println("Not owned");
               } else {
                  MessageOutput.println("Owned by:", new Object[]{var6.name(), new Integer(var4.entryCount())});
               }

               List var7 = var4.waitingThreads();
               if (var7.size() == 0) {
                  MessageOutput.println("No waiters");
               } else {
                  Iterator var8 = var7.iterator();

                  while(var8.hasNext()) {
                     ThreadReference var9 = (ThreadReference)var8.next();
                     MessageOutput.println("Waiting thread:", var9.name());
                  }
               }
            } else {
               MessageOutput.println("Expression must evaluate to an object");
            }
         } catch (IncompatibleThreadStateException var10) {
            MessageOutput.println("Threads must be suspended");
         }

      }
   }

   void commandLock(final StringTokenizer var1) {
      AsyncExecution var10001 = new AsyncExecution() {
         void action() {
            Commands.this.doLock(var1);
         }
      };
   }

   private void printThreadLockInfo(ThreadInfo var1) {
      ThreadReference var2 = var1.getThread();

      try {
         MessageOutput.println("Monitor information for thread", var2.name());
         List var3 = var2.ownedMonitors();
         if (var3.size() == 0) {
            MessageOutput.println("No monitors owned");
         } else {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               ObjectReference var5 = (ObjectReference)var4.next();
               MessageOutput.println("Owned monitor:", var5.toString());
            }
         }

         ObjectReference var7 = var2.currentContendedMonitor();
         if (var7 == null) {
            MessageOutput.println("Not waiting for a monitor");
         } else {
            MessageOutput.println("Waiting for monitor:", var7.toString());
         }
      } catch (IncompatibleThreadStateException var6) {
         MessageOutput.println("Threads must be suspended");
      }

   }

   void commandThreadlocks(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         ThreadInfo var5 = ThreadInfo.getCurrentThreadInfo();
         if (var5 == null) {
            MessageOutput.println("Current thread not set.");
         } else {
            this.printThreadLockInfo(var5);
         }

      } else {
         String var2 = var1.nextToken();
         if (var2.toLowerCase().equals("all")) {
            Iterator var3 = ThreadInfo.threads().iterator();

            while(var3.hasNext()) {
               ThreadInfo var4 = (ThreadInfo)var3.next();
               this.printThreadLockInfo(var4);
            }
         } else {
            ThreadInfo var6 = this.doGetThread(var2);
            if (var6 != null) {
               ThreadInfo.setCurrentThreadInfo(var6);
               this.printThreadLockInfo(var6);
            }
         }

      }
   }

   void doDisableGC(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No object specified.");
      } else {
         String var2 = var1.nextToken("");
         Value var3 = this.evaluate(var2);
         if (var3 != null && var3 instanceof ObjectReference) {
            ObjectReference var4 = (ObjectReference)var3;
            var4.disableCollection();
            String var5 = this.getStringValue();
            if (var5 != null) {
               MessageOutput.println("GC Disabled for", var5);
            }
         } else {
            MessageOutput.println("Expression must evaluate to an object");
         }

      }
   }

   void commandDisableGC(final StringTokenizer var1) {
      AsyncExecution var10001 = new AsyncExecution() {
         void action() {
            Commands.this.doDisableGC(var1);
         }
      };
   }

   void doEnableGC(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No object specified.");
      } else {
         String var2 = var1.nextToken("");
         Value var3 = this.evaluate(var2);
         if (var3 != null && var3 instanceof ObjectReference) {
            ObjectReference var4 = (ObjectReference)var3;
            var4.enableCollection();
            String var5 = this.getStringValue();
            if (var5 != null) {
               MessageOutput.println("GC Enabled for", var5);
            }
         } else {
            MessageOutput.println("Expression must evaluate to an object");
         }

      }
   }

   void commandEnableGC(final StringTokenizer var1) {
      AsyncExecution var10001 = new AsyncExecution() {
         void action() {
            Commands.this.doEnableGC(var1);
         }
      };
   }

   void doSave(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No save index specified.");
      } else {
         String var2 = var1.nextToken();
         if (!var1.hasMoreTokens()) {
            MessageOutput.println("No expression specified.");
         } else {
            String var3 = var1.nextToken("");
            Value var4 = this.evaluate(var3);
            if (var4 != null) {
               Env.setSavedValue(var2, var4);
               String var5 = this.getStringValue();
               if (var5 != null) {
                  MessageOutput.println("saved", var5);
               }
            } else {
               MessageOutput.println("Expression cannot be void");
            }

         }
      }
   }

   void commandSave(final StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         Set var2 = Env.getSaveKeys();
         if (var2.isEmpty()) {
            MessageOutput.println("No saved values");
         } else {
            Iterator var3 = var2.iterator();

            while(true) {
               while(var3.hasNext()) {
                  String var4 = (String)var3.next();
                  Value var5 = Env.getSavedValue(var4);
                  if (var5 instanceof ObjectReference && ((ObjectReference)var5).isCollected()) {
                     MessageOutput.println("expr is value <collected>", new Object[]{var4, var5.toString()});
                  } else if (var5 == null) {
                     MessageOutput.println("expr is null", var4);
                  } else {
                     MessageOutput.println("expr is value", new Object[]{var4, var5.toString()});
                  }
               }

               return;
            }
         }
      } else {
         AsyncExecution var10001 = new AsyncExecution() {
            void action() {
               Commands.this.doSave(var1);
            }
         };
      }
   }

   void commandBytecodes(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No class specified.");
      } else {
         String var2 = var1.nextToken();
         if (!var1.hasMoreTokens()) {
            MessageOutput.println("No method specified.");
         } else {
            String var3 = var1.nextToken();
            List var4 = Env.vm().classesByName(var2);
            if (var4.size() == 0) {
               if (var2.indexOf(46) < 0) {
                  MessageOutput.println("not found (try the full name)", var2);
               } else {
                  MessageOutput.println("not found", var2);
               }

            } else {
               ReferenceType var5 = (ReferenceType)var4.get(0);
               if (!(var5 instanceof ClassType)) {
                  MessageOutput.println("not a class", var2);
               } else {
                  byte[] var6 = null;
                  Iterator var7 = var5.methodsByName(var3).iterator();

                  while(var7.hasNext()) {
                     Method var8 = (Method)var7.next();
                     if (!var8.isAbstract()) {
                        var6 = var8.bytecodes();
                        break;
                     }
                  }

                  StringBuffer var11 = new StringBuffer(80);
                  var11.append("0000: ");

                  for(int var12 = 0; var12 < var6.length; ++var12) {
                     int var9;
                     if (var12 > 0 && var12 % 16 == 0) {
                        MessageOutput.printDirectln(var11.toString());
                        var11.setLength(0);
                        var11.append(String.valueOf(var12));
                        var11.append(": ");
                        var9 = var11.length();

                        for(int var10 = 0; var10 < 6 - var9; ++var10) {
                           var11.insert(0, '0');
                        }
                     }

                     var9 = 255 & var6[var12];
                     String var13 = Integer.toHexString(var9);
                     if (var13.length() == 1) {
                        var11.append('0');
                     }

                     var11.append(var13);
                     var11.append(' ');
                  }

                  if (var11.length() > 6) {
                     MessageOutput.printDirectln(var11.toString());
                  }

               }
            }
         }
      }
   }

   void commandExclude(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.printDirectln(Env.excludesString());
      } else {
         String var2 = var1.nextToken("");
         if (var2.equals("none")) {
            var2 = "";
         }

         Env.setExcludes(var2);
      }

   }

   void commandRedefine(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("Specify classes to redefine");
      } else {
         String var2 = var1.nextToken();
         List var3 = Env.vm().classesByName(var2);
         if (var3.size() == 0) {
            MessageOutput.println("No class named", var2);
            return;
         }

         if (var3.size() > 1) {
            MessageOutput.println("More than one class named", var2);
            return;
         }

         Env.setSourcePath(Env.getSourcePath());
         ReferenceType var4 = (ReferenceType)var3.get(0);
         if (!var1.hasMoreTokens()) {
            MessageOutput.println("Specify file name for class", var2);
            return;
         }

         String var5 = var1.nextToken();
         File var6 = new File(var5);
         byte[] var7 = new byte[(int)var6.length()];

         try {
            FileInputStream var8 = new FileInputStream(var6);
            var8.read(var7);
            var8.close();
         } catch (Exception var11) {
            MessageOutput.println("Error reading file", new Object[]{var5, var11.toString()});
            return;
         }

         HashMap var12 = new HashMap();
         var12.put(var4, var7);

         try {
            Env.vm().redefineClasses(var12);
         } catch (Throwable var10) {
            MessageOutput.println("Error redefining class to file", new Object[]{var2, var5, var10});
         }
      }

   }

   void commandPopFrames(StringTokenizer var1, boolean var2) {
      ThreadInfo var3;
      if (var1.hasMoreTokens()) {
         String var4 = var1.nextToken();
         var3 = this.doGetThread(var4);
         if (var3 == null) {
            return;
         }
      } else {
         var3 = ThreadInfo.getCurrentThreadInfo();
         if (var3 == null) {
            MessageOutput.println("No thread specified.");
            return;
         }
      }

      try {
         StackFrame var6 = var3.getCurrentFrame();
         var3.getThread().popFrames(var6);
         var3 = ThreadInfo.getCurrentThreadInfo();
         ThreadInfo.setCurrentThreadInfo(var3);
         if (var2) {
            this.commandStepi();
         }
      } catch (Throwable var5) {
         MessageOutput.println("Error popping frame", var5.toString());
      }

   }

   void commandExtension(StringTokenizer var1) {
      if (!var1.hasMoreTokens()) {
         MessageOutput.println("No class specified.");
      } else {
         String var2 = var1.nextToken();
         ReferenceType var3 = Env.getReferenceTypeFromToken(var2);
         String var4 = null;
         if (var3 != null) {
            try {
               var4 = var3.sourceDebugExtension();
               MessageOutput.println("sourcedebugextension", var4);
            } catch (AbsentInformationException var6) {
               MessageOutput.println("No sourcedebugextension specified");
            }
         } else {
            MessageOutput.println("is not a valid id or class name", var2);
         }

      }
   }

   void commandVersion(String var1, VirtualMachineManager var2) {
      MessageOutput.println("minus version", new Object[]{var1, new Integer(var2.majorInterfaceVersion()), new Integer(var2.minorInterfaceVersion()), System.getProperty("java.version")});
      if (Env.connection() != null) {
         try {
            MessageOutput.printDirectln(Env.vm().description());
         } catch (VMNotConnectedException var4) {
            MessageOutput.println("No VM connected");
         }
      }

   }

   abstract class AsyncExecution {
      abstract void action();

      AsyncExecution() {
         this.execute();
      }

      void execute() {
         final ThreadInfo var1 = ThreadInfo.getCurrentThreadInfo();
         final int var2 = var1 == null ? 0 : var1.getCurrentFrameIndex();
         Thread var3 = new Thread("asynchronous jdb command") {
            public void run() {
               try {
                  AsyncExecution.this.action();
               } catch (UnsupportedOperationException var17) {
                  MessageOutput.println("Operation is not supported on the target VM");
               } catch (Exception var18) {
                  MessageOutput.println("Internal exception during operation:", var18.getMessage());
               } finally {
                  if (var1 != null) {
                     ThreadInfo.setCurrentThreadInfo(var1);

                     try {
                        var1.setCurrentFrameIndex(var2);
                     } catch (IncompatibleThreadStateException var15) {
                        MessageOutput.println("Current thread isnt suspended.");
                     } catch (ArrayIndexOutOfBoundsException var16) {
                        MessageOutput.println("Requested stack frame is no longer active:", new Object[]{new Integer(var2)});
                     }
                  }

                  MessageOutput.printPrompt();
               }

            }
         };
         var3.start();
      }
   }
}
