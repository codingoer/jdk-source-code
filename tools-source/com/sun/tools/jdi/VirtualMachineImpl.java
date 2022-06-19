package com.sun.tools.jdi;

import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteType;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharType;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.DoubleType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatType;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InternalException;
import com.sun.jdi.LongType;
import com.sun.jdi.LongValue;
import com.sun.jdi.PathSearchingVirtualMachine;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.VoidType;
import com.sun.jdi.VoidValue;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequestManager;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

class VirtualMachineImpl extends MirrorImpl implements PathSearchingVirtualMachine, ThreadListener {
   public final int sizeofFieldRef;
   public final int sizeofMethodRef;
   public final int sizeofObjectRef;
   public final int sizeofClassRef;
   public final int sizeofFrameRef;
   final int sequenceNumber;
   private final TargetVM target;
   private final EventQueueImpl eventQueue;
   private final EventRequestManagerImpl internalEventRequestManager;
   private final EventRequestManagerImpl eventRequestManager;
   final VirtualMachineManagerImpl vmManager;
   private final ThreadGroup threadGroupForJDI;
   int traceFlags = 0;
   static int TRACE_RAW_SENDS = 16777216;
   static int TRACE_RAW_RECEIVES = 33554432;
   boolean traceReceives = false;
   private Map typesByID;
   private TreeSet typesBySignature;
   private boolean retrievedAllTypes = false;
   private String defaultStratum = null;
   private final Map objectsByID = new HashMap();
   private final ReferenceQueue referenceQueue = new ReferenceQueue();
   private static final int DISPOSE_THRESHOLD = 50;
   private final List batchedDisposeRequests = Collections.synchronizedList(new ArrayList(60));
   private JDWP.VirtualMachine.Version versionInfo;
   private JDWP.VirtualMachine.ClassPaths pathInfo;
   private JDWP.VirtualMachine.Capabilities capabilities = null;
   private JDWP.VirtualMachine.CapabilitiesNew capabilitiesNew = null;
   private BooleanType theBooleanType;
   private ByteType theByteType;
   private CharType theCharType;
   private ShortType theShortType;
   private IntegerType theIntegerType;
   private LongType theLongType;
   private FloatType theFloatType;
   private DoubleType theDoubleType;
   private VoidType theVoidType;
   private VoidValue voidVal;
   private Process process;
   private VMState state = new VMState(this);
   private Object initMonitor = new Object();
   private boolean initComplete = false;
   private boolean shutdown = false;

   private void notifyInitCompletion() {
      synchronized(this.initMonitor) {
         this.initComplete = true;
         this.initMonitor.notifyAll();
      }
   }

   void waitInitCompletion() {
      synchronized(this.initMonitor) {
         while(!this.initComplete) {
            try {
               this.initMonitor.wait();
            } catch (InterruptedException var4) {
            }
         }

      }
   }

   VMState state() {
      return this.state;
   }

   public boolean threadResumable(ThreadAction var1) {
      this.state.thaw(var1.thread());
      return true;
   }

   VirtualMachineImpl(VirtualMachineManager var1, Connection var2, Process var3, int var4) {
      super((VirtualMachine)null);
      this.vm = this;
      this.vmManager = (VirtualMachineManagerImpl)var1;
      this.process = var3;
      this.sequenceNumber = var4;
      this.threadGroupForJDI = new ThreadGroup(this.vmManager.mainGroupForJDI(), "JDI [" + this.hashCode() + "]");
      this.target = new TargetVM(this, var2);
      EventQueueImpl var5 = new EventQueueImpl(this, this.target);
      new InternalEventHandler(this, var5);
      this.eventQueue = new EventQueueImpl(this, this.target);
      this.eventRequestManager = new EventRequestManagerImpl(this);
      this.target.start();

      JDWP.VirtualMachine.IDSizes var6;
      try {
         var6 = JDWP.VirtualMachine.IDSizes.process(this.vm);
      } catch (JDWPException var8) {
         throw var8.toJDIException();
      }

      this.sizeofFieldRef = var6.fieldIDSize;
      this.sizeofMethodRef = var6.methodIDSize;
      this.sizeofObjectRef = var6.objectIDSize;
      this.sizeofClassRef = var6.referenceTypeIDSize;
      this.sizeofFrameRef = var6.frameIDSize;
      this.internalEventRequestManager = new EventRequestManagerImpl(this);
      ClassPrepareRequest var7 = this.internalEventRequestManager.createClassPrepareRequest();
      var7.setSuspendPolicy(0);
      var7.enable();
      ClassUnloadRequest var9 = this.internalEventRequestManager.createClassUnloadRequest();
      var9.setSuspendPolicy(0);
      var9.enable();
      this.notifyInitCompletion();
   }

   EventRequestManagerImpl getInternalEventRequestManager() {
      return this.internalEventRequestManager;
   }

   void validateVM() {
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   public List classesByName(String var1) {
      this.validateVM();
      String var2 = JNITypeParser.typeNameToSignature(var1);
      List var3;
      if (this.retrievedAllTypes) {
         var3 = this.findReferenceTypes(var2);
      } else {
         var3 = this.retrieveClassesBySignature(var2);
      }

      return Collections.unmodifiableList(var3);
   }

   public List allClasses() {
      this.validateVM();
      if (!this.retrievedAllTypes) {
         this.retrieveAllClasses();
      }

      ArrayList var1;
      synchronized(this) {
         var1 = new ArrayList(this.typesBySignature);
      }

      return Collections.unmodifiableList(var1);
   }

   public void redefineClasses(Map var1) {
      int var2 = var1.size();
      JDWP.VirtualMachine.RedefineClasses.ClassDef[] var3 = new JDWP.VirtualMachine.RedefineClasses.ClassDef[var2];
      this.validateVM();
      if (!this.canRedefineClasses()) {
         throw new UnsupportedOperationException();
      } else {
         Iterator var4 = var1.entrySet().iterator();

         ReferenceTypeImpl var7;
         for(int var5 = 0; var4.hasNext(); ++var5) {
            Map.Entry var6 = (Map.Entry)var4.next();
            var7 = (ReferenceTypeImpl)var6.getKey();
            this.validateMirror(var7);
            var3[var5] = new JDWP.VirtualMachine.RedefineClasses.ClassDef(var7, (byte[])((byte[])var6.getValue()));
         }

         this.vm.state().thaw();

         try {
            JDWP.VirtualMachine.RedefineClasses.process(this.vm, var3);
         } catch (JDWPException var8) {
            switch (var8.errorCode()) {
               case 60:
                  throw new ClassFormatError("class not in class file format");
               case 61:
                  throw new ClassCircularityError("circularity has been detected while initializing a class");
               case 62:
                  throw new VerifyError("verifier detected internal inconsistency or security problem");
               case 63:
                  throw new UnsupportedOperationException("add method not implemented");
               case 64:
                  throw new UnsupportedOperationException("schema change not implemented");
               case 65:
               default:
                  throw var8.toJDIException();
               case 66:
                  throw new UnsupportedOperationException("hierarchy change not implemented");
               case 67:
                  throw new UnsupportedOperationException("delete method not implemented");
               case 68:
                  throw new UnsupportedClassVersionError("version numbers of class are not supported");
               case 69:
                  throw new NoClassDefFoundError("class names do not match");
               case 70:
                  throw new UnsupportedOperationException("changes to class modifiers not implemented");
               case 71:
                  throw new UnsupportedOperationException("changes to method modifiers not implemented");
            }
         }

         ArrayList var9 = new ArrayList();
         EventRequestManager var10 = this.eventRequestManager();
         var4 = var10.breakpointRequests().iterator();

         while(var4.hasNext()) {
            BreakpointRequest var11 = (BreakpointRequest)var4.next();
            if (var1.containsKey(var11.location().declaringType())) {
               var9.add(var11);
            }
         }

         var10.deleteEventRequests(var9);
         var4 = var1.keySet().iterator();

         while(var4.hasNext()) {
            var7 = (ReferenceTypeImpl)var4.next();
            var7.noticeRedefineClass();
         }

      }
   }

   public List allThreads() {
      this.validateVM();
      return this.state.allThreads();
   }

   public List topLevelThreadGroups() {
      this.validateVM();
      return this.state.topLevelThreadGroups();
   }

   PacketStream sendResumingCommand(CommandSender var1) {
      return this.state.thawCommand(var1);
   }

   void notifySuspend() {
      this.state.freeze();
   }

   public void suspend() {
      this.validateVM();

      try {
         JDWP.VirtualMachine.Suspend.process(this.vm);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }

      this.notifySuspend();
   }

   public void resume() {
      this.validateVM();
      CommandSender var1 = new CommandSender() {
         public PacketStream send() {
            return JDWP.VirtualMachine.Resume.enqueueCommand(VirtualMachineImpl.this.vm);
         }
      };

      try {
         PacketStream var2 = this.state.thawCommand(var1);
         JDWP.VirtualMachine.Resume.waitForReply(this.vm, var2);
      } catch (VMDisconnectedException var3) {
      } catch (JDWPException var4) {
         switch (var4.errorCode()) {
            case 112:
               return;
            default:
               throw var4.toJDIException();
         }
      }

   }

   public EventQueue eventQueue() {
      return this.eventQueue;
   }

   public EventRequestManager eventRequestManager() {
      this.validateVM();
      return this.eventRequestManager;
   }

   EventRequestManagerImpl eventRequestManagerImpl() {
      return this.eventRequestManager;
   }

   public BooleanValue mirrorOf(boolean var1) {
      this.validateVM();
      return new BooleanValueImpl(this, var1);
   }

   public ByteValue mirrorOf(byte var1) {
      this.validateVM();
      return new ByteValueImpl(this, var1);
   }

   public CharValue mirrorOf(char var1) {
      this.validateVM();
      return new CharValueImpl(this, var1);
   }

   public ShortValue mirrorOf(short var1) {
      this.validateVM();
      return new ShortValueImpl(this, var1);
   }

   public IntegerValue mirrorOf(int var1) {
      this.validateVM();
      return new IntegerValueImpl(this, var1);
   }

   public LongValue mirrorOf(long var1) {
      this.validateVM();
      return new LongValueImpl(this, var1);
   }

   public FloatValue mirrorOf(float var1) {
      this.validateVM();
      return new FloatValueImpl(this, var1);
   }

   public DoubleValue mirrorOf(double var1) {
      this.validateVM();
      return new DoubleValueImpl(this, var1);
   }

   public StringReference mirrorOf(String var1) {
      this.validateVM();

      try {
         return JDWP.VirtualMachine.CreateString.process(this.vm, var1).stringObject;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   public VoidValue mirrorOfVoid() {
      if (this.voidVal == null) {
         this.voidVal = new VoidValueImpl(this);
      }

      return this.voidVal;
   }

   public long[] instanceCounts(List var1) {
      if (!this.canGetInstanceInfo()) {
         throw new UnsupportedOperationException("target does not support getting instances");
      } else {
         ReferenceTypeImpl[] var3 = new ReferenceTypeImpl[var1.size()];
         int var4 = 0;

         ReferenceType var6;
         for(Iterator var5 = var1.iterator(); var5.hasNext(); var3[var4++] = (ReferenceTypeImpl)var6) {
            var6 = (ReferenceType)var5.next();
            this.validateMirror(var6);
         }

         try {
            long[] var2 = JDWP.VirtualMachine.InstanceCounts.process(this.vm, var3).counts;
            return var2;
         } catch (JDWPException var7) {
            throw var7.toJDIException();
         }
      }
   }

   public void dispose() {
      this.validateVM();
      this.shutdown = true;

      try {
         JDWP.VirtualMachine.Dispose.process(this.vm);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }

      this.target.stopListening();
   }

   public void exit(int var1) {
      this.validateVM();
      this.shutdown = true;

      try {
         JDWP.VirtualMachine.Exit.process(this.vm, var1);
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }

      this.target.stopListening();
   }

   public Process process() {
      this.validateVM();
      return this.process;
   }

   private JDWP.VirtualMachine.Version versionInfo() {
      try {
         if (this.versionInfo == null) {
            this.versionInfo = JDWP.VirtualMachine.Version.process(this.vm);
         }

         return this.versionInfo;
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   public String description() {
      this.validateVM();
      return MessageFormat.format(this.vmManager.getString("version_format"), "" + this.vmManager.majorInterfaceVersion(), "" + this.vmManager.minorInterfaceVersion(), this.versionInfo().description);
   }

   public String version() {
      this.validateVM();
      return this.versionInfo().vmVersion;
   }

   public String name() {
      this.validateVM();
      return this.versionInfo().vmName;
   }

   public boolean canWatchFieldModification() {
      this.validateVM();
      return this.capabilities().canWatchFieldModification;
   }

   public boolean canWatchFieldAccess() {
      this.validateVM();
      return this.capabilities().canWatchFieldAccess;
   }

   public boolean canGetBytecodes() {
      this.validateVM();
      return this.capabilities().canGetBytecodes;
   }

   public boolean canGetSyntheticAttribute() {
      this.validateVM();
      return this.capabilities().canGetSyntheticAttribute;
   }

   public boolean canGetOwnedMonitorInfo() {
      this.validateVM();
      return this.capabilities().canGetOwnedMonitorInfo;
   }

   public boolean canGetCurrentContendedMonitor() {
      this.validateVM();
      return this.capabilities().canGetCurrentContendedMonitor;
   }

   public boolean canGetMonitorInfo() {
      this.validateVM();
      return this.capabilities().canGetMonitorInfo;
   }

   private boolean hasNewCapabilities() {
      return this.versionInfo().jdwpMajor > 1 || this.versionInfo().jdwpMinor >= 4;
   }

   boolean canGet1_5LanguageFeatures() {
      return this.versionInfo().jdwpMajor > 1 || this.versionInfo().jdwpMinor >= 5;
   }

   public boolean canUseInstanceFilters() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canUseInstanceFilters;
   }

   public boolean canRedefineClasses() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canRedefineClasses;
   }

   public boolean canAddMethod() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canAddMethod;
   }

   public boolean canUnrestrictedlyRedefineClasses() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canUnrestrictedlyRedefineClasses;
   }

   public boolean canPopFrames() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canPopFrames;
   }

   public boolean canGetMethodReturnValues() {
      return this.versionInfo().jdwpMajor > 1 || this.versionInfo().jdwpMinor >= 6;
   }

   public boolean canGetInstanceInfo() {
      if (this.versionInfo().jdwpMajor <= 1 && this.versionInfo().jdwpMinor < 6) {
         return false;
      } else {
         this.validateVM();
         return this.hasNewCapabilities() && this.capabilitiesNew().canGetInstanceInfo;
      }
   }

   public boolean canUseSourceNameFilters() {
      return this.versionInfo().jdwpMajor > 1 || this.versionInfo().jdwpMinor >= 6;
   }

   public boolean canForceEarlyReturn() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canForceEarlyReturn;
   }

   public boolean canBeModified() {
      return true;
   }

   public boolean canGetSourceDebugExtension() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canGetSourceDebugExtension;
   }

   public boolean canGetClassFileVersion() {
      return this.versionInfo().jdwpMajor > 1 || this.versionInfo().jdwpMinor >= 6;
   }

   public boolean canGetConstantPool() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canGetConstantPool;
   }

   public boolean canRequestVMDeathEvent() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canRequestVMDeathEvent;
   }

   public boolean canRequestMonitorEvents() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canRequestMonitorEvents;
   }

   public boolean canGetMonitorFrameInfo() {
      this.validateVM();
      return this.hasNewCapabilities() && this.capabilitiesNew().canGetMonitorFrameInfo;
   }

   public void setDebugTraceMode(int var1) {
      this.validateVM();
      this.traceFlags = var1;
      this.traceReceives = (var1 & 2) != 0;
   }

   void printTrace(String var1) {
      System.err.println("[JDI: " + var1 + "]");
   }

   void printReceiveTrace(int var1, String var2) {
      StringBuffer var3 = new StringBuffer("Receiving:");

      for(int var4 = var1; var4 > 0; --var4) {
         var3.append("    ");
      }

      var3.append(var2);
      this.printTrace(var3.toString());
   }

   private synchronized ReferenceTypeImpl addReferenceType(long var1, int var3, String var4) {
      if (this.typesByID == null) {
         this.initReferenceTypes();
      }

      Object var5 = null;
      switch (var3) {
         case 1:
            var5 = new ClassTypeImpl(this.vm, var1);
            break;
         case 2:
            var5 = new InterfaceTypeImpl(this.vm, var1);
            break;
         case 3:
            var5 = new ArrayTypeImpl(this.vm, var1);
            break;
         default:
            throw new InternalException("Invalid reference type tag");
      }

      if (var4 != null) {
         ((ReferenceTypeImpl)var5).setSignature(var4);
      }

      this.typesByID.put(new Long(var1), var5);
      this.typesBySignature.add(var5);
      if ((this.vm.traceFlags & 8) != 0) {
         this.vm.printTrace("Caching new ReferenceType, sig=" + var4 + ", id=" + var1);
      }

      return (ReferenceTypeImpl)var5;
   }

   synchronized void removeReferenceType(String var1) {
      if (this.typesByID != null) {
         Iterator var2 = this.typesBySignature.iterator();
         int var3 = 0;

         while(var2.hasNext()) {
            ReferenceTypeImpl var4 = (ReferenceTypeImpl)var2.next();
            int var5 = var1.compareTo(var4.signature());
            if (var5 == 0) {
               ++var3;
               var2.remove();
               this.typesByID.remove(new Long(var4.ref()));
               if ((this.vm.traceFlags & 8) != 0) {
                  this.vm.printTrace("Uncaching ReferenceType, sig=" + var1 + ", id=" + var4.ref());
               }
            }
         }

         if (var3 > 1) {
            this.retrieveClassesBySignature(var1);
         }

      }
   }

   private synchronized List findReferenceTypes(String var1) {
      if (this.typesByID == null) {
         return new ArrayList(0);
      } else {
         Iterator var2 = this.typesBySignature.iterator();
         ArrayList var3 = new ArrayList();

         while(var2.hasNext()) {
            ReferenceTypeImpl var4 = (ReferenceTypeImpl)var2.next();
            int var5 = var1.compareTo(var4.signature());
            if (var5 == 0) {
               var3.add(var4);
            }
         }

         return var3;
      }
   }

   private void initReferenceTypes() {
      this.typesByID = new HashMap(300);
      this.typesBySignature = new TreeSet();
   }

   ReferenceTypeImpl referenceType(long var1, byte var3) {
      return this.referenceType(var1, var3, (String)null);
   }

   ClassTypeImpl classType(long var1) {
      return (ClassTypeImpl)this.referenceType(var1, 1, (String)null);
   }

   InterfaceTypeImpl interfaceType(long var1) {
      return (InterfaceTypeImpl)this.referenceType(var1, 2, (String)null);
   }

   ArrayTypeImpl arrayType(long var1) {
      return (ArrayTypeImpl)this.referenceType(var1, 3, (String)null);
   }

   ReferenceTypeImpl referenceType(long var1, int var3, String var4) {
      if ((this.vm.traceFlags & 8) != 0) {
         StringBuffer var5 = new StringBuffer();
         var5.append("Looking up ");
         if (var3 == 1) {
            var5.append("Class");
         } else if (var3 == 2) {
            var5.append("Interface");
         } else if (var3 == 3) {
            var5.append("ArrayType");
         } else {
            var5.append("UNKNOWN TAG: " + var3);
         }

         if (var4 != null) {
            var5.append(", signature='" + var4 + "'");
         }

         var5.append(", id=" + var1);
         this.vm.printTrace(var5.toString());
      }

      if (var1 == 0L) {
         return null;
      } else {
         ReferenceTypeImpl var9 = null;
         synchronized(this) {
            if (this.typesByID != null) {
               var9 = (ReferenceTypeImpl)this.typesByID.get(new Long(var1));
            }

            if (var9 == null) {
               var9 = this.addReferenceType(var1, var3, var4);
            }

            return var9;
         }
      }
   }

   private JDWP.VirtualMachine.Capabilities capabilities() {
      if (this.capabilities == null) {
         try {
            this.capabilities = JDWP.VirtualMachine.Capabilities.process(this.vm);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.capabilities;
   }

   private JDWP.VirtualMachine.CapabilitiesNew capabilitiesNew() {
      if (this.capabilitiesNew == null) {
         try {
            this.capabilitiesNew = JDWP.VirtualMachine.CapabilitiesNew.process(this.vm);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.capabilitiesNew;
   }

   private List retrieveClassesBySignature(String var1) {
      if ((this.vm.traceFlags & 8) != 0) {
         this.vm.printTrace("Retrieving matching ReferenceTypes, sig=" + var1);
      }

      JDWP.VirtualMachine.ClassesBySignature.ClassInfo[] var2;
      try {
         var2 = JDWP.VirtualMachine.ClassesBySignature.process(this.vm, var1).classes;
      } catch (JDWPException var10) {
         throw var10.toJDIException();
      }

      int var3 = var2.length;
      ArrayList var4 = new ArrayList(var3);
      synchronized(this) {
         for(int var6 = 0; var6 < var3; ++var6) {
            JDWP.VirtualMachine.ClassesBySignature.ClassInfo var7 = var2[var6];
            ReferenceTypeImpl var8 = this.referenceType(var7.typeID, var7.refTypeTag, var1);
            var8.setStatus(var7.status);
            var4.add(var8);
         }

         return var4;
      }
   }

   private void retrieveAllClasses1_4() {
      JDWP.VirtualMachine.AllClasses.ClassInfo[] var1;
      try {
         var1 = JDWP.VirtualMachine.AllClasses.process(this.vm).classes;
      } catch (JDWPException var8) {
         throw var8.toJDIException();
      }

      synchronized(this) {
         if (!this.retrievedAllTypes) {
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               JDWP.VirtualMachine.AllClasses.ClassInfo var5 = var1[var4];
               ReferenceTypeImpl var6 = this.referenceType(var5.typeID, var5.refTypeTag, var5.signature);
               var6.setStatus(var5.status);
            }

            this.retrievedAllTypes = true;
         }

      }
   }

   private void retrieveAllClasses() {
      if ((this.vm.traceFlags & 8) != 0) {
         this.vm.printTrace("Retrieving all ReferenceTypes");
      }

      if (!this.vm.canGet1_5LanguageFeatures()) {
         this.retrieveAllClasses1_4();
      } else {
         JDWP.VirtualMachine.AllClassesWithGeneric.ClassInfo[] var1;
         try {
            var1 = JDWP.VirtualMachine.AllClassesWithGeneric.process(this.vm).classes;
         } catch (JDWPException var8) {
            throw var8.toJDIException();
         }

         synchronized(this) {
            if (!this.retrievedAllTypes) {
               int var3 = var1.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  JDWP.VirtualMachine.AllClassesWithGeneric.ClassInfo var5 = var1[var4];
                  ReferenceTypeImpl var6 = this.referenceType(var5.typeID, var5.refTypeTag, var5.signature);
                  var6.setGenericSignature(var5.genericSignature);
                  var6.setStatus(var5.status);
               }

               this.retrievedAllTypes = true;
            }

         }
      }
   }

   void sendToTarget(Packet var1) {
      this.target.send(var1);
   }

   void waitForTargetReply(Packet var1) {
      this.target.waitForReply(var1);
      this.processBatchedDisposes();
   }

   Type findBootType(String var1) throws ClassNotLoadedException {
      List var2 = this.retrieveClassesBySignature(var1);
      Iterator var3 = var2.iterator();

      ReferenceType var4;
      do {
         if (!var3.hasNext()) {
            JNITypeParser var5 = new JNITypeParser(var1);
            throw new ClassNotLoadedException(var5.typeName(), "Type " + var5.typeName() + " not loaded");
         }

         var4 = (ReferenceType)var3.next();
      } while(var4.classLoader() != null);

      return var4;
   }

   BooleanType theBooleanType() {
      if (this.theBooleanType == null) {
         synchronized(this) {
            if (this.theBooleanType == null) {
               this.theBooleanType = new BooleanTypeImpl(this);
            }
         }
      }

      return this.theBooleanType;
   }

   ByteType theByteType() {
      if (this.theByteType == null) {
         synchronized(this) {
            if (this.theByteType == null) {
               this.theByteType = new ByteTypeImpl(this);
            }
         }
      }

      return this.theByteType;
   }

   CharType theCharType() {
      if (this.theCharType == null) {
         synchronized(this) {
            if (this.theCharType == null) {
               this.theCharType = new CharTypeImpl(this);
            }
         }
      }

      return this.theCharType;
   }

   ShortType theShortType() {
      if (this.theShortType == null) {
         synchronized(this) {
            if (this.theShortType == null) {
               this.theShortType = new ShortTypeImpl(this);
            }
         }
      }

      return this.theShortType;
   }

   IntegerType theIntegerType() {
      if (this.theIntegerType == null) {
         synchronized(this) {
            if (this.theIntegerType == null) {
               this.theIntegerType = new IntegerTypeImpl(this);
            }
         }
      }

      return this.theIntegerType;
   }

   LongType theLongType() {
      if (this.theLongType == null) {
         synchronized(this) {
            if (this.theLongType == null) {
               this.theLongType = new LongTypeImpl(this);
            }
         }
      }

      return this.theLongType;
   }

   FloatType theFloatType() {
      if (this.theFloatType == null) {
         synchronized(this) {
            if (this.theFloatType == null) {
               this.theFloatType = new FloatTypeImpl(this);
            }
         }
      }

      return this.theFloatType;
   }

   DoubleType theDoubleType() {
      if (this.theDoubleType == null) {
         synchronized(this) {
            if (this.theDoubleType == null) {
               this.theDoubleType = new DoubleTypeImpl(this);
            }
         }
      }

      return this.theDoubleType;
   }

   VoidType theVoidType() {
      if (this.theVoidType == null) {
         synchronized(this) {
            if (this.theVoidType == null) {
               this.theVoidType = new VoidTypeImpl(this);
            }
         }
      }

      return this.theVoidType;
   }

   PrimitiveType primitiveTypeMirror(byte var1) {
      switch (var1) {
         case 66:
            return this.theByteType();
         case 67:
            return this.theCharType();
         case 68:
            return this.theDoubleType();
         case 69:
         case 71:
         case 72:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         default:
            throw new IllegalArgumentException("Unrecognized primitive tag " + var1);
         case 70:
            return this.theFloatType();
         case 73:
            return this.theIntegerType();
         case 74:
            return this.theLongType();
         case 83:
            return this.theShortType();
         case 90:
            return this.theBooleanType();
      }
   }

   private void processBatchedDisposes() {
      if (!this.shutdown) {
         JDWP.VirtualMachine.DisposeObjects.Request[] var1 = null;
         synchronized(this.batchedDisposeRequests) {
            int var3 = this.batchedDisposeRequests.size();
            if (var3 >= 50) {
               if ((this.traceFlags & 16) != 0) {
                  this.printTrace("Dispose threashold reached. Will dispose " + var3 + " object references...");
               }

               var1 = new JDWP.VirtualMachine.DisposeObjects.Request[var3];

               for(int var4 = 0; var4 < var1.length; ++var4) {
                  SoftObjectReference var5 = (SoftObjectReference)this.batchedDisposeRequests.get(var4);
                  if ((this.traceFlags & 16) != 0) {
                     this.printTrace("Disposing object " + var5.key() + " (ref count = " + var5.count() + ")");
                  }

                  var1[var4] = new JDWP.VirtualMachine.DisposeObjects.Request(new ObjectReferenceImpl(this, var5.key()), var5.count());
               }

               this.batchedDisposeRequests.clear();
            }
         }

         if (var1 != null) {
            try {
               JDWP.VirtualMachine.DisposeObjects.process(this.vm, var1);
            } catch (JDWPException var7) {
               throw var7.toJDIException();
            }
         }

      }
   }

   private void batchForDispose(SoftObjectReference var1) {
      if ((this.traceFlags & 16) != 0) {
         this.printTrace("Batching object " + var1.key() + " for dispose (ref count = " + var1.count() + ")");
      }

      this.batchedDisposeRequests.add(var1);
   }

   private void processQueue() {
      Reference var1;
      while((var1 = this.referenceQueue.poll()) != null) {
         SoftObjectReference var2 = (SoftObjectReference)var1;
         this.removeObjectMirror(var2);
         this.batchForDispose(var2);
      }

   }

   synchronized ObjectReferenceImpl objectMirror(long var1, int var3) {
      this.processQueue();
      if (var1 == 0L) {
         return null;
      } else {
         Object var4 = null;
         Long var5 = new Long(var1);
         SoftObjectReference var6 = (SoftObjectReference)this.objectsByID.get(var5);
         if (var6 != null) {
            var4 = var6.object();
         }

         if (var4 == null) {
            switch (var3) {
               case 76:
                  var4 = new ObjectReferenceImpl(this.vm, var1);
                  break;
               case 91:
                  var4 = new ArrayReferenceImpl(this.vm, var1);
                  break;
               case 99:
                  var4 = new ClassObjectReferenceImpl(this.vm, var1);
                  break;
               case 103:
                  var4 = new ThreadGroupReferenceImpl(this.vm, var1);
                  break;
               case 108:
                  var4 = new ClassLoaderReferenceImpl(this.vm, var1);
                  break;
               case 115:
                  var4 = new StringReferenceImpl(this.vm, var1);
                  break;
               case 116:
                  ThreadReferenceImpl var7 = new ThreadReferenceImpl(this.vm, var1);
                  var7.addListener(this);
                  var4 = var7;
                  break;
               default:
                  throw new IllegalArgumentException("Invalid object tag: " + var3);
            }

            var6 = new SoftObjectReference(var5, (ObjectReferenceImpl)var4, this.referenceQueue);
            this.objectsByID.put(var5, var6);
            if ((this.traceFlags & 16) != 0) {
               this.printTrace("Creating new " + var4.getClass().getName() + " (id = " + var1 + ")");
            }
         } else {
            var6.incrementCount();
         }

         return (ObjectReferenceImpl)var4;
      }
   }

   synchronized void removeObjectMirror(ObjectReferenceImpl var1) {
      this.processQueue();
      SoftObjectReference var2 = (SoftObjectReference)this.objectsByID.remove(new Long(var1.ref()));
      if (var2 != null) {
         this.batchForDispose(var2);
      } else {
         throw new InternalException("ObjectReference " + var1.ref() + " not found in object cache");
      }
   }

   synchronized void removeObjectMirror(SoftObjectReference var1) {
      this.objectsByID.remove(var1.key());
   }

   ObjectReferenceImpl objectMirror(long var1) {
      return this.objectMirror(var1, 76);
   }

   StringReferenceImpl stringMirror(long var1) {
      return (StringReferenceImpl)this.objectMirror(var1, 115);
   }

   ArrayReferenceImpl arrayMirror(long var1) {
      return (ArrayReferenceImpl)this.objectMirror(var1, 91);
   }

   ThreadReferenceImpl threadMirror(long var1) {
      return (ThreadReferenceImpl)this.objectMirror(var1, 116);
   }

   ThreadGroupReferenceImpl threadGroupMirror(long var1) {
      return (ThreadGroupReferenceImpl)this.objectMirror(var1, 103);
   }

   ClassLoaderReferenceImpl classLoaderMirror(long var1) {
      return (ClassLoaderReferenceImpl)this.objectMirror(var1, 108);
   }

   ClassObjectReferenceImpl classObjectMirror(long var1) {
      return (ClassObjectReferenceImpl)this.objectMirror(var1, 99);
   }

   private JDWP.VirtualMachine.ClassPaths getClasspath() {
      if (this.pathInfo == null) {
         try {
            this.pathInfo = JDWP.VirtualMachine.ClassPaths.process(this.vm);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.pathInfo;
   }

   public List classPath() {
      return Arrays.asList(this.getClasspath().classpaths);
   }

   public List bootClassPath() {
      return Arrays.asList(this.getClasspath().bootclasspaths);
   }

   public String baseDirectory() {
      return this.getClasspath().baseDir;
   }

   public void setDefaultStratum(String var1) {
      this.defaultStratum = var1;
      if (var1 == null) {
         var1 = "";
      }

      try {
         JDWP.VirtualMachine.SetDefaultStratum.process(this.vm, var1);
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   public String getDefaultStratum() {
      return this.defaultStratum;
   }

   ThreadGroup threadGroupForJDI() {
      return this.threadGroupForJDI;
   }

   private static class SoftObjectReference extends SoftReference {
      int count = 1;
      Long key;

      SoftObjectReference(Long var1, ObjectReferenceImpl var2, ReferenceQueue var3) {
         super(var2, var3);
         this.key = var1;
      }

      int count() {
         return this.count;
      }

      void incrementCount() {
         ++this.count;
      }

      Long key() {
         return this.key;
      }

      ObjectReferenceImpl object() {
         return (ObjectReferenceImpl)this.get();
      }
   }
}
