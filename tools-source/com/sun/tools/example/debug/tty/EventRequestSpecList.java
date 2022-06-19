package com.sun.tools.example.debug.tty;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.request.EventRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class EventRequestSpecList {
   private static final int statusResolved = 1;
   private static final int statusUnresolved = 2;
   private static final int statusError = 3;
   private List eventRequestSpecs = Collections.synchronizedList(new ArrayList());

   boolean resolve(ClassPrepareEvent var1) {
      boolean var2 = false;
      synchronized(this.eventRequestSpecs) {
         Iterator var4 = this.eventRequestSpecs.iterator();

         while(var4.hasNext()) {
            EventRequestSpec var5 = (EventRequestSpec)var4.next();
            if (!var5.isResolved()) {
               try {
                  EventRequest var6 = var5.resolve(var1);
                  if (var6 != null) {
                     MessageOutput.println("Set deferred", var5.toString());
                  }
               } catch (Exception var8) {
                  MessageOutput.println("Unable to set deferred", new Object[]{var5.toString(), var5.errorMessageFor(var8)});
                  var2 = true;
               }
            }
         }
      }

      return !var2;
   }

   void resolveAll() {
      Iterator var1 = this.eventRequestSpecs.iterator();

      while(var1.hasNext()) {
         EventRequestSpec var2 = (EventRequestSpec)var1.next();

         try {
            EventRequest var3 = var2.resolveEagerly();
            if (var3 != null) {
               MessageOutput.println("Set deferred", var2.toString());
            }
         } catch (Exception var4) {
         }
      }

   }

   boolean addEagerlyResolve(EventRequestSpec var1) {
      try {
         this.eventRequestSpecs.add(var1);
         EventRequest var2 = var1.resolveEagerly();
         if (var2 != null) {
            MessageOutput.println("Set", var1.toString());
         }

         return true;
      } catch (Exception var3) {
         MessageOutput.println("Unable to set", new Object[]{var1.toString(), var1.errorMessageFor(var3)});
         return false;
      }
   }

   BreakpointSpec createBreakpoint(String var1, int var2) throws ClassNotFoundException {
      PatternReferenceTypeSpec var3 = new PatternReferenceTypeSpec(var1);
      return new BreakpointSpec(var3, var2);
   }

   BreakpointSpec createBreakpoint(String var1, String var2, List var3) throws MalformedMemberNameException, ClassNotFoundException {
      PatternReferenceTypeSpec var4 = new PatternReferenceTypeSpec(var1);
      return new BreakpointSpec(var4, var2, var3);
   }

   EventRequestSpec createExceptionCatch(String var1, boolean var2, boolean var3) throws ClassNotFoundException {
      PatternReferenceTypeSpec var4 = new PatternReferenceTypeSpec(var1);
      return new ExceptionSpec(var4, var2, var3);
   }

   WatchpointSpec createAccessWatchpoint(String var1, String var2) throws MalformedMemberNameException, ClassNotFoundException {
      PatternReferenceTypeSpec var3 = new PatternReferenceTypeSpec(var1);
      return new AccessWatchpointSpec(var3, var2);
   }

   WatchpointSpec createModificationWatchpoint(String var1, String var2) throws MalformedMemberNameException, ClassNotFoundException {
      PatternReferenceTypeSpec var3 = new PatternReferenceTypeSpec(var1);
      return new ModificationWatchpointSpec(var3, var2);
   }

   boolean delete(EventRequestSpec var1) {
      synchronized(this.eventRequestSpecs) {
         int var3 = this.eventRequestSpecs.indexOf(var1);
         if (var3 != -1) {
            EventRequestSpec var4 = (EventRequestSpec)this.eventRequestSpecs.get(var3);
            var4.remove();
            this.eventRequestSpecs.remove(var3);
            return true;
         } else {
            return false;
         }
      }
   }

   List eventRequestSpecs() {
      synchronized(this.eventRequestSpecs) {
         return new ArrayList(this.eventRequestSpecs);
      }
   }
}
