package com.sun.xml.internal.xsom.impl.scd;

import com.sun.xml.internal.xsom.SCD;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.Iterator;

public final class SCDImpl extends SCD {
   private final Step[] steps;
   private final String text;

   public SCDImpl(String text, Step[] steps) {
      this.text = text;
      this.steps = steps;
   }

   public Iterator select(Iterator contextNode) {
      Iterator nodeSet = contextNode;
      int len = this.steps.length;

      for(int i = 0; i < len; ++i) {
         if (i != 0 && i != len - 1 && !this.steps[i - 1].axis.isModelGroup() && this.steps[i].axis.isModelGroup()) {
            nodeSet = new Iterators.Unique(new Iterators.Map((Iterator)nodeSet) {
               protected Iterator apply(XSComponent u) {
                  return new Iterators.Union(Iterators.singleton(u), Axis.INTERMEDIATE_SKIP.iterator(u));
               }
            });
         }

         nodeSet = this.steps[i].evaluate((Iterator)nodeSet);
      }

      return (Iterator)nodeSet;
   }

   public String toString() {
      return this.text;
   }
}
