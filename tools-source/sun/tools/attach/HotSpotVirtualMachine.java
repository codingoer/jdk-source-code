package sun.tools.attach;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.spi.AttachProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class HotSpotVirtualMachine extends VirtualMachine {
   private static final int JNI_ENOMEM = -4;
   private static final int ATTACH_ERROR_BADJAR = 100;
   private static final int ATTACH_ERROR_NOTONCP = 101;
   private static final int ATTACH_ERROR_STARTFAIL = 102;
   private static final String MANAGMENT_PREFIX = "com.sun.management.";
   private static long defaultAttachTimeout = 5000L;
   private volatile long attachTimeout;

   HotSpotVirtualMachine(AttachProvider var1, String var2) {
      super(var1, var2);
   }

   private void loadAgentLibrary(String var1, boolean var2, String var3) throws AgentLoadException, AgentInitializationException, IOException {
      InputStream var4 = this.execute("load", var1, var2 ? "true" : "false", var3);

      try {
         int var5 = this.readInt(var4);
         if (var5 != 0) {
            throw new AgentInitializationException("Agent_OnAttach failed", var5);
         }
      } finally {
         var4.close();
      }

   }

   public void loadAgentLibrary(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException {
      this.loadAgentLibrary(var1, false, var2);
   }

   public void loadAgentPath(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException {
      this.loadAgentLibrary(var1, true, var2);
   }

   public void loadAgent(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException {
      String var3 = var1;
      if (var2 != null) {
         var3 = var1 + "=" + var2;
      }

      try {
         this.loadAgentLibrary("instrument", var3);
      } catch (AgentLoadException var6) {
         throw new InternalError("instrument library is missing in target VM", var6);
      } catch (AgentInitializationException var7) {
         int var5 = var7.returnValue();
         switch (var5) {
            case -4:
               throw new AgentLoadException("Insuffient memory");
            case 100:
               throw new AgentLoadException("Agent JAR not found or no Agent-Class attribute");
            case 101:
               throw new AgentLoadException("Unable to add JAR file to system class path");
            case 102:
               throw new AgentInitializationException("Agent JAR loaded but agent failed to initialize");
            default:
               throw new AgentLoadException("Failed to load agent - unknown reason: " + var5);
         }
      }
   }

   public Properties getSystemProperties() throws IOException {
      InputStream var1 = null;
      Properties var2 = new Properties();

      try {
         var1 = this.executeCommand("properties");
         var2.load(var1);
      } finally {
         if (var1 != null) {
            var1.close();
         }

      }

      return var2;
   }

   public Properties getAgentProperties() throws IOException {
      InputStream var1 = null;
      Properties var2 = new Properties();

      try {
         var1 = this.executeCommand("agentProperties");
         var2.load(var1);
      } finally {
         if (var1 != null) {
            var1.close();
         }

      }

      return var2;
   }

   private static boolean checkedKeyName(Object var0) {
      if (!(var0 instanceof String)) {
         throw new IllegalArgumentException("Invalid option (not a String): " + var0);
      } else if (!((String)var0).startsWith("com.sun.management.")) {
         throw new IllegalArgumentException("Invalid option: " + var0);
      } else {
         return true;
      }
   }

   private static String stripKeyName(Object var0) {
      return ((String)var0).substring("com.sun.management.".length());
   }

   public void startManagementAgent(Properties var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("agentProperties cannot be null");
      } else {
         String var2 = (String)var1.entrySet().stream().filter((var0) -> {
            return checkedKeyName(var0.getKey());
         }).map((var1x) -> {
            return stripKeyName(var1x.getKey()) + "=" + this.escape(var1x.getValue());
         }).collect(Collectors.joining(" "));
         this.executeJCmd("ManagementAgent.start " + var2);
      }
   }

   private String escape(Object var1) {
      String var2 = var1.toString();
      return var2.contains(" ") ? "'" + var2 + "'" : var2;
   }

   public String startLocalManagementAgent() throws IOException {
      this.executeJCmd("ManagementAgent.start_local");
      return this.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
   }

   public void localDataDump() throws IOException {
      this.executeCommand("datadump").close();
   }

   public InputStream remoteDataDump(Object... var1) throws IOException {
      return this.executeCommand("threaddump", var1);
   }

   public InputStream dumpHeap(Object... var1) throws IOException {
      return this.executeCommand("dumpheap", var1);
   }

   public InputStream heapHisto(Object... var1) throws IOException {
      return this.executeCommand("inspectheap", var1);
   }

   public InputStream setFlag(String var1, String var2) throws IOException {
      return this.executeCommand("setflag", var1, var2);
   }

   public InputStream printFlag(String var1) throws IOException {
      return this.executeCommand("printflag", var1);
   }

   public InputStream executeJCmd(String var1) throws IOException {
      return this.executeCommand("jcmd", var1);
   }

   abstract InputStream execute(String var1, Object... var2) throws AgentLoadException, IOException;

   private InputStream executeCommand(String var1, Object... var2) throws IOException {
      try {
         return this.execute(var1, var2);
      } catch (AgentLoadException var4) {
         throw new InternalError("Should not get here", var4);
      }
   }

   int readInt(InputStream var1) throws IOException {
      StringBuilder var2 = new StringBuilder();
      byte[] var4 = new byte[1];

      int var3;
      int var5;
      do {
         var3 = var1.read(var4, 0, 1);
         if (var3 > 0) {
            var5 = (char)var4[0];
            if (var5 == 10) {
               break;
            }

            var2.append((char)var5);
         }
      } while(var3 > 0);

      if (var2.length() == 0) {
         throw new IOException("Premature EOF");
      } else {
         try {
            var5 = Integer.parseInt(var2.toString());
            return var5;
         } catch (NumberFormatException var7) {
            throw new IOException("Non-numeric value found - int expected");
         }
      }
   }

   String readErrorMessage(InputStream var1) throws IOException {
      byte[] var2 = new byte[1024];
      StringBuffer var4 = new StringBuffer();

      int var3;
      while((var3 = var1.read(var2)) != -1) {
         var4.append(new String(var2, 0, var3, "UTF-8"));
      }

      return var4.toString();
   }

   long attachTimeout() {
      if (this.attachTimeout == 0L) {
         synchronized(this) {
            if (this.attachTimeout == 0L) {
               try {
                  String var2 = System.getProperty("sun.tools.attach.attachTimeout");
                  this.attachTimeout = Long.parseLong(var2);
               } catch (SecurityException var4) {
               } catch (NumberFormatException var5) {
               }

               if (this.attachTimeout <= 0L) {
                  this.attachTimeout = defaultAttachTimeout;
               }
            }
         }
      }

      return this.attachTimeout;
   }
}
