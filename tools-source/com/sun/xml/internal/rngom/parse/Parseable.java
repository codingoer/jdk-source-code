package com.sun.xml.internal.rngom.parse;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public interface Parseable {
   ParsedPattern parse(SchemaBuilder var1) throws BuildException, IllegalSchemaException;

   ParsedPattern parseInclude(String var1, SchemaBuilder var2, IncludedGrammar var3, String var4) throws BuildException, IllegalSchemaException;

   ParsedPattern parseExternal(String var1, SchemaBuilder var2, Scope var3, String var4) throws BuildException, IllegalSchemaException;
}
