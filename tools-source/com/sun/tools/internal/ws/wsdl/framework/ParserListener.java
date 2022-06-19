package com.sun.tools.internal.ws.wsdl.framework;

import javax.xml.namespace.QName;

public interface ParserListener {
   void ignoringExtension(Entity var1, QName var2, QName var3);

   void doneParsingEntity(QName var1, Entity var2);
}
