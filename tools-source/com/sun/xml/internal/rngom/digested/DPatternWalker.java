package com.sun.xml.internal.rngom.digested;

public class DPatternWalker implements DPatternVisitor {
   public Void onAttribute(DAttributePattern p) {
      return this.onXmlToken(p);
   }

   protected Void onXmlToken(DXmlTokenPattern p) {
      return this.onUnary(p);
   }

   public Void onChoice(DChoicePattern p) {
      return this.onContainer(p);
   }

   protected Void onContainer(DContainerPattern p) {
      for(DPattern c = p.firstChild(); c != null; c = c.next) {
         c.accept(this);
      }

      return null;
   }

   public Void onData(DDataPattern p) {
      return null;
   }

   public Void onElement(DElementPattern p) {
      return this.onXmlToken(p);
   }

   public Void onEmpty(DEmptyPattern p) {
      return null;
   }

   public Void onGrammar(DGrammarPattern p) {
      return (Void)p.getStart().accept(this);
   }

   public Void onGroup(DGroupPattern p) {
      return this.onContainer(p);
   }

   public Void onInterleave(DInterleavePattern p) {
      return this.onContainer(p);
   }

   public Void onList(DListPattern p) {
      return this.onUnary(p);
   }

   public Void onMixed(DMixedPattern p) {
      return this.onUnary(p);
   }

   public Void onNotAllowed(DNotAllowedPattern p) {
      return null;
   }

   public Void onOneOrMore(DOneOrMorePattern p) {
      return this.onUnary(p);
   }

   public Void onOptional(DOptionalPattern p) {
      return this.onUnary(p);
   }

   public Void onRef(DRefPattern p) {
      return (Void)p.getTarget().getPattern().accept(this);
   }

   public Void onText(DTextPattern p) {
      return null;
   }

   public Void onValue(DValuePattern p) {
      return null;
   }

   public Void onZeroOrMore(DZeroOrMorePattern p) {
      return this.onUnary(p);
   }

   protected Void onUnary(DUnaryPattern p) {
      return (Void)p.getChild().accept(this);
   }
}
