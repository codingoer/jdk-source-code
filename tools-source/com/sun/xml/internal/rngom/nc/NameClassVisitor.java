package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

public interface NameClassVisitor {
   Object visitChoice(NameClass var1, NameClass var2);

   Object visitNsName(String var1);

   Object visitNsNameExcept(String var1, NameClass var2);

   Object visitAnyName();

   Object visitAnyNameExcept(NameClass var1);

   Object visitName(QName var1);

   Object visitNull();
}
