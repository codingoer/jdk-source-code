package com.sun.tools.corba.se.idl;

import java.io.IOException;

class NoPragma extends PragmaHandler {
   public boolean process(String var1, String var2) throws IOException {
      this.parseException(Util.getMessage("Preprocessor.unknownPragma", var1));
      this.skipToEOL();
      return true;
   }
}
