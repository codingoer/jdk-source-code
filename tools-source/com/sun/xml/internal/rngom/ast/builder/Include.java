package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;

public interface Include extends GrammarSection {
   void endInclude(Parseable var1, String var2, String var3, Location var4, Annotations var5) throws BuildException, IllegalSchemaException;
}
