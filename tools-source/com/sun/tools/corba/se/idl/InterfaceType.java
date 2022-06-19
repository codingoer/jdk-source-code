package com.sun.tools.corba.se.idl;

public interface InterfaceType {
   int NORMAL = 0;
   int ABSTRACT = 1;
   int LOCAL = 2;
   int LOCALSERVANT = 3;
   int LOCAL_SIGNATURE_ONLY = 4;

   int getInterfaceType();

   void setInterfaceType(int var1);
}
