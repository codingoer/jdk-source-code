package com.sun.tools.internal.xjc.reader.gbind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Element extends Expression implements ElementSet {
   final Set foreEdges = new LinkedHashSet();
   final Set backEdges = new LinkedHashSet();
   Element prevPostOrder;
   private ConnectedComponent cc;

   protected Element() {
   }

   ElementSet lastSet() {
      return this;
   }

   boolean isNullable() {
      return false;
   }

   boolean isSource() {
      return false;
   }

   boolean isSink() {
      return false;
   }

   void buildDAG(ElementSet incoming) {
      incoming.addNext(this);
   }

   public void addNext(Element element) {
      this.foreEdges.add(element);
      element.backEdges.add(this);
   }

   public boolean contains(ElementSet rhs) {
      return this == rhs || rhs == ElementSet.EMPTY_SET;
   }

   /** @deprecated */
   public Iterator iterator() {
      return Collections.singleton(this).iterator();
   }

   Element assignDfsPostOrder(Element prev) {
      if (this.prevPostOrder != null) {
         return prev;
      } else {
         this.prevPostOrder = this;

         Element next;
         for(Iterator var2 = this.foreEdges.iterator(); var2.hasNext(); prev = next.assignDfsPostOrder(prev)) {
            next = (Element)var2.next();
         }

         this.prevPostOrder = prev;
         return this;
      }
   }

   public void buildStronglyConnectedComponents(List ccs) {
      List visitedElements = new ArrayList();

      for(Element cur = this; cur != cur.prevPostOrder && !visitedElements.contains(cur); cur = cur.prevPostOrder) {
         visitedElements.add(cur);
         if (!cur.belongsToSCC()) {
            ConnectedComponent cc = new ConnectedComponent();
            ccs.add(cc);
            cur.formConnectedComponent(cc);
         }
      }

   }

   private boolean belongsToSCC() {
      return this.cc != null || this.isSource() || this.isSink();
   }

   private void formConnectedComponent(ConnectedComponent group) {
      if (!this.belongsToSCC()) {
         this.cc = group;
         group.add(this);
         Iterator var2 = this.backEdges.iterator();

         while(var2.hasNext()) {
            Element prev = (Element)var2.next();
            prev.formConnectedComponent(group);
         }

      }
   }

   public boolean hasSelfLoop() {
      assert this.foreEdges.contains(this) == this.backEdges.contains(this);

      return this.foreEdges.contains(this);
   }

   final boolean checkCutSet(ConnectedComponent cc, Set visited) {
      assert this.belongsToSCC();

      if (this.isSink()) {
         return false;
      } else if (!visited.add(this)) {
         return true;
      } else if (this.cc == cc) {
         return true;
      } else {
         Iterator var3 = this.foreEdges.iterator();

         Element next;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            next = (Element)var3.next();
         } while(next.checkCutSet(cc, visited));

         return false;
      }
   }
}
