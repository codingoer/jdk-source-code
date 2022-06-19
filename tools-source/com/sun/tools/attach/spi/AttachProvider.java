package com.sun.tools.attach.spi;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AttachPermission;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import jdk.Exported;

@Exported
public abstract class AttachProvider {
   private static final Object lock = new Object();
   private static List providers = null;

   protected AttachProvider() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AttachPermission("createAttachProvider"));
      }

   }

   public abstract String name();

   public abstract String type();

   public abstract VirtualMachine attachVirtualMachine(String var1) throws AttachNotSupportedException, IOException;

   public VirtualMachine attachVirtualMachine(VirtualMachineDescriptor var1) throws AttachNotSupportedException, IOException {
      if (var1.provider() != this) {
         throw new AttachNotSupportedException("provider mismatch");
      } else {
         return this.attachVirtualMachine(var1.id());
      }
   }

   public abstract List listVirtualMachines();

   public static List providers() {
      synchronized(lock) {
         if (providers == null) {
            providers = new ArrayList();
            ServiceLoader var1 = ServiceLoader.load(AttachProvider.class, AttachProvider.class.getClassLoader());
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               try {
                  providers.add(var2.next());
               } catch (Throwable var6) {
                  if (var6 instanceof ThreadDeath) {
                     ThreadDeath var4 = (ThreadDeath)var6;
                     throw var4;
                  }

                  System.err.println(var6);
               }
            }
         }

         return Collections.unmodifiableList(providers);
      }
   }
}
