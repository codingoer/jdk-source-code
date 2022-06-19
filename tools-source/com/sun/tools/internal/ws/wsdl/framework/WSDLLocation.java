package com.sun.tools.internal.ws.wsdl.framework;

public class WSDLLocation {
   private LocationContext[] contexts;
   private int idPos;
   private LocationContext currentContext;

   WSDLLocation() {
      this.reset();
   }

   public void push() {
      int max = this.contexts.length;
      ++this.idPos;
      if (this.idPos >= max) {
         LocationContext[] newContexts = new LocationContext[max * 2];
         System.arraycopy(this.contexts, 0, newContexts, 0, max);
         this.contexts = newContexts;
      }

      this.currentContext = this.contexts[this.idPos];
      if (this.currentContext == null) {
         this.contexts[this.idPos] = this.currentContext = new LocationContext();
      }

   }

   public void pop() {
      --this.idPos;
      if (this.idPos >= 0) {
         this.currentContext = this.contexts[this.idPos];
      }

   }

   public final void reset() {
      this.contexts = new LocationContext[32];
      this.idPos = 0;
      this.contexts[this.idPos] = this.currentContext = new LocationContext();
   }

   public String getLocation() {
      return this.currentContext.getLocation();
   }

   public void setLocation(String loc) {
      this.currentContext.setLocation(loc);
   }

   private static class LocationContext {
      private String location;

      private LocationContext() {
      }

      void setLocation(String loc) {
         this.location = loc;
      }

      String getLocation() {
         return this.location;
      }

      // $FF: synthetic method
      LocationContext(Object x0) {
         this();
      }
   }
}
