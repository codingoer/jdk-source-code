package com.sun.tools.internal.xjc.reader.internalizer;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public final class LocatorTable {
   private final Map startLocations = new HashMap();
   private final Map endLocations = new HashMap();

   public void storeStartLocation(Element e, Locator loc) {
      this.startLocations.put(e, new LocatorImpl(loc));
   }

   public void storeEndLocation(Element e, Locator loc) {
      this.endLocations.put(e, new LocatorImpl(loc));
   }

   public Locator getStartLocation(Element e) {
      return (Locator)this.startLocations.get(e);
   }

   public Locator getEndLocation(Element e) {
      return (Locator)this.endLocations.get(e);
   }
}
