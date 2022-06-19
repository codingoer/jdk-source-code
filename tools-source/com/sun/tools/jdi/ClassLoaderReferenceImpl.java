package com.sun.tools.jdi;

import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ClassLoaderReferenceImpl extends ObjectReferenceImpl implements ClassLoaderReference, VMListener {
   protected ObjectReferenceImpl.Cache newCache() {
      return new Cache();
   }

   ClassLoaderReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
      this.vm.state().addListener(this);
   }

   protected String description() {
      return "ClassLoaderReference " + this.uniqueID();
   }

   public List definedClasses() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.vm.allClasses().iterator();

      while(var2.hasNext()) {
         ReferenceType var3 = (ReferenceType)var2.next();
         if (var3.isPrepared() && this.equals(var3.classLoader())) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public List visibleClasses() {
      List var1 = null;

      try {
         Cache var2 = (Cache)this.getCache();
         if (var2 != null) {
            var1 = var2.visibleClasses;
         }

         if (var1 == null) {
            JDWP.ClassLoaderReference.VisibleClasses.ClassInfo[] var3 = JDWP.ClassLoaderReference.VisibleClasses.process(this.vm, this).classes;
            ArrayList var6 = new ArrayList(var3.length);

            for(int var4 = 0; var4 < var3.length; ++var4) {
               var6.add(this.vm.referenceType(var3[var4].typeID, var3[var4].refTypeTag));
            }

            var1 = Collections.unmodifiableList(var6);
            if (var2 != null) {
               var2.visibleClasses = var1;
               if ((this.vm.traceFlags & 16) != 0) {
                  this.vm.printTrace(this.description() + " temporarily caching visible classes (count = " + var1.size() + ")");
               }
            }
         }

         return var1;
      } catch (JDWPException var5) {
         throw var5.toJDIException();
      }
   }

   Type findType(String var1) throws ClassNotLoadedException {
      List var2 = this.visibleClasses();
      Iterator var3 = var2.iterator();

      ReferenceType var4;
      do {
         if (!var3.hasNext()) {
            JNITypeParser var5 = new JNITypeParser(var1);
            throw new ClassNotLoadedException(var5.typeName(), "Class " + var5.typeName() + " not loaded");
         }

         var4 = (ReferenceType)var3.next();
      } while(!var4.signature().equals(var1));

      return var4;
   }

   byte typeValueKey() {
      return 108;
   }

   private static class Cache extends ObjectReferenceImpl.Cache {
      List visibleClasses;

      private Cache() {
         this.visibleClasses = null;
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }
}
