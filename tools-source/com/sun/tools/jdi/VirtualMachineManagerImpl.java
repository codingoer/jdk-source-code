package com.sun.tools.jdi;

import com.sun.jdi.JDIPermission;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public class VirtualMachineManagerImpl implements VirtualMachineManagerService {
   private List connectors = new ArrayList();
   private LaunchingConnector defaultConnector = null;
   private List targets = new ArrayList();
   private final ThreadGroup mainGroupForJDI;
   private ResourceBundle messages = null;
   private int vmSequenceNumber = 0;
   private static final int majorVersion = 1;
   private static final int minorVersion = 8;
   private static final Object lock = new Object();
   private static VirtualMachineManagerImpl vmm;

   public static VirtualMachineManager virtualMachineManager() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         JDIPermission var1 = new JDIPermission("virtualMachineManager");
         var0.checkPermission(var1);
      }

      synchronized(lock) {
         if (vmm == null) {
            vmm = new VirtualMachineManagerImpl();
         }
      }

      return vmm;
   }

   protected VirtualMachineManagerImpl() {
      ThreadGroup var1 = Thread.currentThread().getThreadGroup();

      for(ThreadGroup var2 = null; (var2 = var1.getParent()) != null; var1 = var2) {
      }

      this.mainGroupForJDI = new ThreadGroup(var1, "JDI main");
      ServiceLoader var3 = ServiceLoader.load(Connector.class, Connector.class.getClassLoader());
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Connector var5;
         try {
            var5 = (Connector)var4.next();
         } catch (ThreadDeath var14) {
            throw var14;
         } catch (Exception var15) {
            System.err.println(var15);
            continue;
         } catch (Error var16) {
            System.err.println(var16);
            continue;
         }

         this.addConnector(var5);
      }

      ServiceLoader var17 = ServiceLoader.load(TransportService.class, TransportService.class.getClassLoader());
      Iterator var6 = var17.iterator();

      while(var6.hasNext()) {
         TransportService var7;
         try {
            var7 = (TransportService)var6.next();
         } catch (ThreadDeath var11) {
            throw var11;
         } catch (Exception var12) {
            System.err.println(var12);
            continue;
         } catch (Error var13) {
            System.err.println(var13);
            continue;
         }

         this.addConnector(GenericAttachingConnector.create(var7));
         this.addConnector(GenericListeningConnector.create(var7));
      }

      if (this.allConnectors().size() == 0) {
         throw new Error("no Connectors loaded");
      } else {
         boolean var18 = false;
         List var8 = this.launchingConnectors();
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            LaunchingConnector var10 = (LaunchingConnector)var9.next();
            if (var10.name().equals("com.sun.jdi.CommandLineLaunch")) {
               this.setDefaultConnector(var10);
               var18 = true;
               break;
            }
         }

         if (!var18 && var8.size() > 0) {
            this.setDefaultConnector((LaunchingConnector)var8.get(0));
         }

      }
   }

   public LaunchingConnector defaultConnector() {
      if (this.defaultConnector == null) {
         throw new Error("no default LaunchingConnector");
      } else {
         return this.defaultConnector;
      }
   }

   public void setDefaultConnector(LaunchingConnector var1) {
      this.defaultConnector = var1;
   }

   public List launchingConnectors() {
      ArrayList var1 = new ArrayList(this.connectors.size());
      Iterator var2 = this.connectors.iterator();

      while(var2.hasNext()) {
         Connector var3 = (Connector)var2.next();
         if (var3 instanceof LaunchingConnector) {
            var1.add((LaunchingConnector)var3);
         }
      }

      return Collections.unmodifiableList(var1);
   }

   public List attachingConnectors() {
      ArrayList var1 = new ArrayList(this.connectors.size());
      Iterator var2 = this.connectors.iterator();

      while(var2.hasNext()) {
         Connector var3 = (Connector)var2.next();
         if (var3 instanceof AttachingConnector) {
            var1.add((AttachingConnector)var3);
         }
      }

      return Collections.unmodifiableList(var1);
   }

   public List listeningConnectors() {
      ArrayList var1 = new ArrayList(this.connectors.size());
      Iterator var2 = this.connectors.iterator();

      while(var2.hasNext()) {
         Connector var3 = (Connector)var2.next();
         if (var3 instanceof ListeningConnector) {
            var1.add((ListeningConnector)var3);
         }
      }

      return Collections.unmodifiableList(var1);
   }

   public List allConnectors() {
      return Collections.unmodifiableList(this.connectors);
   }

   public List connectedVirtualMachines() {
      return Collections.unmodifiableList(this.targets);
   }

   public void addConnector(Connector var1) {
      this.connectors.add(var1);
   }

   public void removeConnector(Connector var1) {
      this.connectors.remove(var1);
   }

   public synchronized VirtualMachine createVirtualMachine(Connection var1, Process var2) throws IOException {
      if (!var1.isOpen()) {
         throw new IllegalStateException("connection is not open");
      } else {
         VirtualMachineImpl var3;
         try {
            var3 = new VirtualMachineImpl(this, var1, var2, ++this.vmSequenceNumber);
         } catch (VMDisconnectedException var5) {
            throw new IOException(var5.getMessage());
         }

         this.targets.add(var3);
         return var3;
      }
   }

   public VirtualMachine createVirtualMachine(Connection var1) throws IOException {
      return this.createVirtualMachine(var1, (Process)null);
   }

   public void addVirtualMachine(VirtualMachine var1) {
      this.targets.add(var1);
   }

   void disposeVirtualMachine(VirtualMachine var1) {
      this.targets.remove(var1);
   }

   public int majorInterfaceVersion() {
      return 1;
   }

   public int minorInterfaceVersion() {
      return 8;
   }

   ThreadGroup mainGroupForJDI() {
      return this.mainGroupForJDI;
   }

   String getString(String var1) {
      if (this.messages == null) {
         this.messages = ResourceBundle.getBundle("com.sun.tools.jdi.resources.jdi");
      }

      return this.messages.getString(var1);
   }
}
