package com.sun.tools.attach;

import com.sun.tools.attach.spi.AttachProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import jdk.Exported;

@Exported
public abstract class VirtualMachine {
   private AttachProvider provider;
   private String id;
   private volatile int hash;

   protected VirtualMachine(AttachProvider var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("provider cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("id cannot be null");
      } else {
         this.provider = var1;
         this.id = var2;
      }
   }

   public static List list() {
      ArrayList var0 = new ArrayList();
      List var1 = AttachProvider.providers();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         AttachProvider var3 = (AttachProvider)var2.next();
         var0.addAll(var3.listVirtualMachines());
      }

      return var0;
   }

   public static VirtualMachine attach(String var0) throws AttachNotSupportedException, IOException {
      if (var0 == null) {
         throw new NullPointerException("id cannot be null");
      } else {
         List var1 = AttachProvider.providers();
         if (var1.size() == 0) {
            throw new AttachNotSupportedException("no providers installed");
         } else {
            AttachNotSupportedException var2 = null;
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               AttachProvider var4 = (AttachProvider)var3.next();

               try {
                  return var4.attachVirtualMachine(var0);
               } catch (AttachNotSupportedException var6) {
                  var2 = var6;
               }
            }

            throw var2;
         }
      }
   }

   public static VirtualMachine attach(VirtualMachineDescriptor var0) throws AttachNotSupportedException, IOException {
      return var0.provider().attachVirtualMachine(var0);
   }

   public abstract void detach() throws IOException;

   public final AttachProvider provider() {
      return this.provider;
   }

   public final String id() {
      return this.id;
   }

   public abstract void loadAgentLibrary(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException;

   public void loadAgentLibrary(String var1) throws AgentLoadException, AgentInitializationException, IOException {
      this.loadAgentLibrary(var1, (String)null);
   }

   public abstract void loadAgentPath(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException;

   public void loadAgentPath(String var1) throws AgentLoadException, AgentInitializationException, IOException {
      this.loadAgentPath(var1, (String)null);
   }

   public abstract void loadAgent(String var1, String var2) throws AgentLoadException, AgentInitializationException, IOException;

   public void loadAgent(String var1) throws AgentLoadException, AgentInitializationException, IOException {
      this.loadAgent(var1, (String)null);
   }

   public abstract Properties getSystemProperties() throws IOException;

   public abstract Properties getAgentProperties() throws IOException;

   public abstract void startManagementAgent(Properties var1) throws IOException;

   public abstract String startLocalManagementAgent() throws IOException;

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         this.hash = this.provider.hashCode() * 127 + this.id.hashCode();
         return this.hash;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof VirtualMachine)) {
         return false;
      } else {
         VirtualMachine var2 = (VirtualMachine)var1;
         if (var2.provider() != this.provider()) {
            return false;
         } else {
            return var2.id().equals(this.id());
         }
      }
   }

   public String toString() {
      return this.provider.toString() + ": " + this.id;
   }
}
