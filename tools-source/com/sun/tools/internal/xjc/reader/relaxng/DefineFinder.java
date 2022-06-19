package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.xml.internal.rngom.digested.DDefine;
import com.sun.xml.internal.rngom.digested.DGrammarPattern;
import com.sun.xml.internal.rngom.digested.DPatternWalker;
import com.sun.xml.internal.rngom.digested.DRefPattern;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class DefineFinder extends DPatternWalker {
   public final Set defs = new HashSet();

   public Void onGrammar(DGrammarPattern p) {
      Iterator var2 = p.iterator();

      while(var2.hasNext()) {
         DDefine def = (DDefine)var2.next();
         this.defs.add(def);
         def.getPattern().accept(this);
      }

      return (Void)p.getStart().accept(this);
   }

   public Void onRef(DRefPattern p) {
      return null;
   }
}
