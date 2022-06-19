package com.sun.tools.internal.ws.wsdl.framework;

public interface GloballyKnown extends Elemental {
   String getName();

   Kind getKind();

   Defining getDefining();
}
