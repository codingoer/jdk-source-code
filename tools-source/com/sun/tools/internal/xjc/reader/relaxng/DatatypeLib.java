package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.xmlschema.SimpleTypeBuilder;
import java.util.HashMap;
import java.util.Map;

final class DatatypeLib {
   public final String nsUri;
   private final Map types = new HashMap();
   public static final DatatypeLib BUILTIN = new DatatypeLib("");
   public static final DatatypeLib XMLSCHEMA = new DatatypeLib("http://www.w3.org/2001/XMLSchema-datatypes");

   public DatatypeLib(String nsUri) {
      this.nsUri = nsUri;
   }

   TypeUse get(String name) {
      return (TypeUse)this.types.get(name);
   }

   static {
      BUILTIN.types.put("token", CBuiltinLeafInfo.TOKEN);
      BUILTIN.types.put("string", CBuiltinLeafInfo.STRING);
      XMLSCHEMA.types.putAll(SimpleTypeBuilder.builtinConversions);
   }
}
