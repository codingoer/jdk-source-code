package com.sun.xml.internal.rngom.xml.sax;

import com.sun.xml.internal.rngom.util.Uri;
import org.xml.sax.Locator;

public class XmlBaseHandler {
   private int depth = 0;
   private Locator loc;
   private Entry stack = null;

   public void setLocator(Locator loc) {
      this.loc = loc;
   }

   public void startElement() {
      ++this.depth;
   }

   public void endElement() {
      if (this.stack != null && this.stack.depth == this.depth) {
         this.stack = this.stack.parent;
      }

      --this.depth;
   }

   public void xmlBaseAttribute(String value) {
      Entry entry = new Entry();
      entry.parent = this.stack;
      this.stack = entry;
      entry.attValue = Uri.escapeDisallowedChars(value);
      entry.systemId = this.getSystemId();
      entry.depth = this.depth;
   }

   private String getSystemId() {
      return this.loc == null ? null : this.loc.getSystemId();
   }

   public String getBaseUri() {
      return getBaseUri1(this.getSystemId(), this.stack);
   }

   private static String getBaseUri1(String baseUri, Entry stack) {
      if (stack != null && (baseUri == null || baseUri.equals(stack.systemId))) {
         baseUri = stack.attValue;
         return Uri.isAbsolute(baseUri) ? baseUri : Uri.resolve(getBaseUri1(stack.systemId, stack.parent), baseUri);
      } else {
         return baseUri;
      }
   }

   private static class Entry {
      private Entry parent;
      private String attValue;
      private String systemId;
      private int depth;

      private Entry() {
      }

      // $FF: synthetic method
      Entry(Object x0) {
         this();
      }
   }
}
