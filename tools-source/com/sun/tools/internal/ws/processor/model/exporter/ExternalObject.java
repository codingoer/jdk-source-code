package com.sun.tools.internal.ws.processor.model.exporter;

import org.xml.sax.ContentHandler;

public interface ExternalObject {
   String getType();

   void saveTo(ContentHandler var1);
}
