package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public interface Scope extends GrammarSection {
   ParsedPattern makeParentRef(String var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeRef(String var1, Location var2, Annotations var3) throws BuildException;
}
