package com.sun.tools.javap;

import java.util.HashMap;
import java.util.Map;

public class Context {
   Map map = new HashMap();

   public Object get(Class var1) {
      return this.map.get(var1);
   }

   public Object put(Class var1, Object var2) {
      return this.map.put(var1, var2);
   }
}
