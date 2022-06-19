package com.sun.xml.internal.rngom.binary.visitor;

import com.sun.xml.internal.rngom.binary.AfterPattern;
import com.sun.xml.internal.rngom.binary.AttributePattern;
import com.sun.xml.internal.rngom.binary.ChoicePattern;
import com.sun.xml.internal.rngom.binary.DataExceptPattern;
import com.sun.xml.internal.rngom.binary.DataPattern;
import com.sun.xml.internal.rngom.binary.ElementPattern;
import com.sun.xml.internal.rngom.binary.EmptyPattern;
import com.sun.xml.internal.rngom.binary.ErrorPattern;
import com.sun.xml.internal.rngom.binary.GroupPattern;
import com.sun.xml.internal.rngom.binary.InterleavePattern;
import com.sun.xml.internal.rngom.binary.ListPattern;
import com.sun.xml.internal.rngom.binary.NotAllowedPattern;
import com.sun.xml.internal.rngom.binary.OneOrMorePattern;
import com.sun.xml.internal.rngom.binary.RefPattern;
import com.sun.xml.internal.rngom.binary.TextPattern;
import com.sun.xml.internal.rngom.binary.ValuePattern;

public interface PatternFunction {
   Object caseEmpty(EmptyPattern var1);

   Object caseNotAllowed(NotAllowedPattern var1);

   Object caseError(ErrorPattern var1);

   Object caseGroup(GroupPattern var1);

   Object caseInterleave(InterleavePattern var1);

   Object caseChoice(ChoicePattern var1);

   Object caseOneOrMore(OneOrMorePattern var1);

   Object caseElement(ElementPattern var1);

   Object caseAttribute(AttributePattern var1);

   Object caseData(DataPattern var1);

   Object caseDataExcept(DataExceptPattern var1);

   Object caseValue(ValuePattern var1);

   Object caseText(TextPattern var1);

   Object caseList(ListPattern var1);

   Object caseRef(RefPattern var1);

   Object caseAfter(AfterPattern var1);
}
