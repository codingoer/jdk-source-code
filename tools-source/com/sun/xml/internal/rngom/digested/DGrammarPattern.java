package com.sun.xml.internal.rngom.digested;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DGrammarPattern extends DPattern implements Iterable {
   private final Map patterns = new HashMap();
   DPattern start;

   public DPattern getStart() {
      return this.start;
   }

   public DDefine get(String name) {
      return (DDefine)this.patterns.get(name);
   }

   DDefine getOrAdd(String name) {
      if (this.patterns.containsKey(name)) {
         return this.get(name);
      } else {
         DDefine d = new DDefine(name);
         this.patterns.put(name, d);
         return d;
      }
   }

   public Iterator iterator() {
      return this.patterns.values().iterator();
   }

   public boolean isNullable() {
      return this.start.isNullable();
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onGrammar(this);
   }
}
