package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.xml.internal.rngom.digested.DAttributePattern;
import com.sun.xml.internal.rngom.digested.DChoicePattern;
import com.sun.xml.internal.rngom.digested.DListPattern;
import com.sun.xml.internal.rngom.digested.DMixedPattern;
import com.sun.xml.internal.rngom.digested.DOneOrMorePattern;
import com.sun.xml.internal.rngom.digested.DOptionalPattern;
import com.sun.xml.internal.rngom.digested.DPatternWalker;
import com.sun.xml.internal.rngom.digested.DRefPattern;
import com.sun.xml.internal.rngom.digested.DZeroOrMorePattern;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

final class TypePatternBinder extends DPatternWalker {
   private boolean canInherit;
   private final Stack stack = new Stack();
   private final Set cannotBeInherited = new HashSet();

   void reset() {
      this.canInherit = true;
      this.stack.clear();
   }

   public Void onRef(DRefPattern p) {
      if (!this.canInherit) {
         this.cannotBeInherited.add(p.getTarget());
      } else {
         this.canInherit = false;
      }

      return null;
   }

   public Void onChoice(DChoicePattern p) {
      this.push(false);
      super.onChoice(p);
      this.pop();
      return null;
   }

   public Void onAttribute(DAttributePattern p) {
      this.push(false);
      super.onAttribute(p);
      this.pop();
      return null;
   }

   public Void onList(DListPattern p) {
      this.push(false);
      super.onList(p);
      this.pop();
      return null;
   }

   public Void onMixed(DMixedPattern p) {
      this.push(false);
      super.onMixed(p);
      this.pop();
      return null;
   }

   public Void onOneOrMore(DOneOrMorePattern p) {
      this.push(false);
      super.onOneOrMore(p);
      this.pop();
      return null;
   }

   public Void onZeroOrMore(DZeroOrMorePattern p) {
      this.push(false);
      super.onZeroOrMore(p);
      this.pop();
      return null;
   }

   public Void onOptional(DOptionalPattern p) {
      this.push(false);
      super.onOptional(p);
      this.pop();
      return null;
   }

   private void push(boolean v) {
      this.stack.push(this.canInherit);
      this.canInherit = v;
   }

   private void pop() {
      this.canInherit = (Boolean)this.stack.pop();
   }
}
