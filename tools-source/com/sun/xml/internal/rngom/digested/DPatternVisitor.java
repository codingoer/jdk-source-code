package com.sun.xml.internal.rngom.digested;

public interface DPatternVisitor {
   Object onAttribute(DAttributePattern var1);

   Object onChoice(DChoicePattern var1);

   Object onData(DDataPattern var1);

   Object onElement(DElementPattern var1);

   Object onEmpty(DEmptyPattern var1);

   Object onGrammar(DGrammarPattern var1);

   Object onGroup(DGroupPattern var1);

   Object onInterleave(DInterleavePattern var1);

   Object onList(DListPattern var1);

   Object onMixed(DMixedPattern var1);

   Object onNotAllowed(DNotAllowedPattern var1);

   Object onOneOrMore(DOneOrMorePattern var1);

   Object onOptional(DOptionalPattern var1);

   Object onRef(DRefPattern var1);

   Object onText(DTextPattern var1);

   Object onValue(DValuePattern var1);

   Object onZeroOrMore(DZeroOrMorePattern var1);
}
