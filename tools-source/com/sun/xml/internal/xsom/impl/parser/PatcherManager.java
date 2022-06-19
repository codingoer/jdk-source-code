package com.sun.xml.internal.xsom.impl.parser;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public interface PatcherManager {
   void addPatcher(Patch var1);

   void addErrorChecker(Patch var1);

   void reportError(String var1, Locator var2) throws SAXException;

   public interface Patcher {
      void run() throws SAXException;
   }
}
