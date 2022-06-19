package com.sun.xml.internal.xsom.parser;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public abstract class AnnotationParser {
   public abstract ContentHandler getContentHandler(AnnotationContext var1, String var2, ErrorHandler var3, EntityResolver var4);

   public abstract Object getResult(Object var1);
}
