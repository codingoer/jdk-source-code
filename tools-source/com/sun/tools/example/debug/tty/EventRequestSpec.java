package com.sun.tools.example.debug.tty;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.ExceptionRequest;
import java.util.ArrayList;
import java.util.Iterator;

abstract class EventRequestSpec {
   final ReferenceTypeSpec refSpec;
   int suspendPolicy = 2;
   EventRequest resolved = null;
   ClassPrepareRequest prepareRequest = null;

   EventRequestSpec(ReferenceTypeSpec var1) {
      this.refSpec = var1;
   }

   abstract EventRequest resolveEventRequest(ReferenceType var1) throws Exception;

   synchronized EventRequest resolve(ClassPrepareEvent var1) throws Exception {
      if (this.resolved == null && this.prepareRequest != null && this.prepareRequest.equals(var1.request())) {
         this.resolved = this.resolveEventRequest(var1.referenceType());
         this.prepareRequest.disable();
         Env.vm().eventRequestManager().deleteEventRequest(this.prepareRequest);
         this.prepareRequest = null;
         if (this.refSpec instanceof PatternReferenceTypeSpec) {
            PatternReferenceTypeSpec var2 = (PatternReferenceTypeSpec)this.refSpec;
            if (!var2.isUnique()) {
               this.resolved = null;
               this.prepareRequest = this.refSpec.createPrepareRequest();
               this.prepareRequest.enable();
            }
         }
      }

      return this.resolved;
   }

   synchronized void remove() {
      if (this.isResolved()) {
         Env.vm().eventRequestManager().deleteEventRequest(this.resolved());
      }

      if (this.refSpec instanceof PatternReferenceTypeSpec) {
         PatternReferenceTypeSpec var1 = (PatternReferenceTypeSpec)this.refSpec;
         if (!var1.isUnique()) {
            ArrayList var2 = new ArrayList();
            Iterator var3 = Env.vm().eventRequestManager().exceptionRequests().iterator();

            while(var3.hasNext()) {
               ExceptionRequest var4 = (ExceptionRequest)var3.next();
               if (var1.matches(var4.exception())) {
                  var2.add(var4);
               }
            }

            Env.vm().eventRequestManager().deleteEventRequests(var2);
         }
      }

   }

   private EventRequest resolveAgainstPreparedClasses() throws Exception {
      Iterator var1 = Env.vm().allClasses().iterator();

      while(var1.hasNext()) {
         ReferenceType var2 = (ReferenceType)var1.next();
         if (var2.isPrepared() && this.refSpec.matches(var2)) {
            this.resolved = this.resolveEventRequest(var2);
         }
      }

      return this.resolved;
   }

   synchronized EventRequest resolveEagerly() throws Exception {
      try {
         if (this.resolved == null) {
            this.prepareRequest = this.refSpec.createPrepareRequest();
            this.prepareRequest.enable();
            this.resolveAgainstPreparedClasses();
            if (this.resolved != null) {
               this.prepareRequest.disable();
               Env.vm().eventRequestManager().deleteEventRequest(this.prepareRequest);
               this.prepareRequest = null;
            }
         }

         if (this.refSpec instanceof PatternReferenceTypeSpec) {
            PatternReferenceTypeSpec var1 = (PatternReferenceTypeSpec)this.refSpec;
            if (!var1.isUnique()) {
               this.resolved = null;
               if (this.prepareRequest == null) {
                  this.prepareRequest = this.refSpec.createPrepareRequest();
                  this.prepareRequest.enable();
               }
            }
         }
      } catch (VMNotConnectedException var2) {
      }

      return this.resolved;
   }

   EventRequest resolved() {
      return this.resolved;
   }

   boolean isResolved() {
      return this.resolved != null;
   }

   protected boolean isJavaIdentifier(String var1) {
      if (var1.length() == 0) {
         return false;
      } else {
         int var2 = var1.codePointAt(0);
         if (!Character.isJavaIdentifierStart(var2)) {
            return false;
         } else {
            for(int var3 = Character.charCount(var2); var3 < var1.length(); var3 += Character.charCount(var2)) {
               var2 = var1.codePointAt(var3);
               if (!Character.isJavaIdentifierPart(var2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   String errorMessageFor(Exception var1) {
      if (var1 instanceof IllegalArgumentException) {
         return MessageOutput.format("Invalid command syntax");
      } else if (var1 instanceof RuntimeException) {
         throw (RuntimeException)var1;
      } else {
         return MessageOutput.format("Internal error; unable to set", this.refSpec.toString());
      }
   }
}
