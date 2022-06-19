package sun.tools.attach;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;

public class BsdAttachProvider extends HotSpotAttachProvider {
   private static final String JVM_VERSION = "java.property.java.vm.version";

   public String name() {
      return "sun";
   }

   public String type() {
      return "socket";
   }

   public VirtualMachine attachVirtualMachine(String var1) throws AttachNotSupportedException, IOException {
      this.checkAttachPermission();
      this.testAttachable(var1);
      return new BsdVirtualMachine(this, var1);
   }

   public VirtualMachine attachVirtualMachine(VirtualMachineDescriptor var1) throws AttachNotSupportedException, IOException {
      if (var1.provider() != this) {
         throw new AttachNotSupportedException("provider mismatch");
      } else if (var1 instanceof HotSpotAttachProvider.HotSpotVirtualMachineDescriptor) {
         assert ((HotSpotAttachProvider.HotSpotVirtualMachineDescriptor)var1).isAttachable();

         this.checkAttachPermission();
         return new BsdVirtualMachine(this, var1.id());
      } else {
         return this.attachVirtualMachine(var1.id());
      }
   }
}
