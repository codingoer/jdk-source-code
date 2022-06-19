package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public interface IncludedGrammar extends GrammarSection, Scope {
   ParsedPattern endIncludedGrammar(Location var1, Annotations var2) throws BuildException;
}
