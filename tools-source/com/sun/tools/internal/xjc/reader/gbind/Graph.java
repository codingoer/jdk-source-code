package com.sun.tools.internal.xjc.reader.gbind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class Graph implements Iterable {
   private final Element source = new SourceNode();
   private final Element sink = new SinkNode();
   private final List ccs = new ArrayList();

   public Graph(Expression body) {
      Expression whole = new Sequence(new Sequence(this.source, body), this.sink);
      whole.buildDAG(ElementSet.EMPTY_SET);
      this.source.assignDfsPostOrder(this.sink);
      this.source.buildStronglyConnectedComponents(this.ccs);
      Set visited = new HashSet();
      Iterator var4 = this.ccs.iterator();

      while(var4.hasNext()) {
         ConnectedComponent cc = (ConnectedComponent)var4.next();
         visited.clear();
         if (this.source.checkCutSet(cc, visited)) {
            cc.isRequired = true;
         }
      }

   }

   public Iterator iterator() {
      return this.ccs.iterator();
   }

   public String toString() {
      return this.ccs.toString();
   }
}
