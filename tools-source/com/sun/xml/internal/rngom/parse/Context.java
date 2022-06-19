package com.sun.xml.internal.rngom.parse;

import java.util.Enumeration;
import org.relaxng.datatype.ValidationContext;

public interface Context extends ValidationContext {
   Enumeration prefixes();

   Context copy();
}
