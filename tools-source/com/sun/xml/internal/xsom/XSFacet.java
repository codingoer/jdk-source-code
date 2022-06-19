package com.sun.xml.internal.xsom;

public interface XSFacet extends XSComponent {
   String FACET_LENGTH = "length";
   String FACET_MINLENGTH = "minLength";
   String FACET_MAXLENGTH = "maxLength";
   String FACET_PATTERN = "pattern";
   String FACET_ENUMERATION = "enumeration";
   String FACET_TOTALDIGITS = "totalDigits";
   String FACET_FRACTIONDIGITS = "fractionDigits";
   String FACET_MININCLUSIVE = "minInclusive";
   String FACET_MAXINCLUSIVE = "maxInclusive";
   String FACET_MINEXCLUSIVE = "minExclusive";
   String FACET_MAXEXCLUSIVE = "maxExclusive";
   String FACET_WHITESPACE = "whiteSpace";

   String getName();

   XmlString getValue();

   boolean isFixed();
}
