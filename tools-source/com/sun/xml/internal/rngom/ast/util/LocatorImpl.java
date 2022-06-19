package com.sun.xml.internal.rngom.ast.util;

import com.sun.xml.internal.rngom.ast.om.Location;
import org.xml.sax.Locator;

public class LocatorImpl implements Locator, Location {
   private final String systemId;
   private final int lineNumber;
   private final int columnNumber;

   public LocatorImpl(String systemId, int lineNumber, int columnNumber) {
      this.systemId = systemId;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
   }

   public String getPublicId() {
      return null;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }
}
