package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.Context;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import java.util.List;

public interface SchemaBuilder {
   NameClassBuilder getNameClassBuilder() throws BuildException;

   ParsedPattern makeChoice(List var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeInterleave(List var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeGroup(List var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeOneOrMore(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeZeroOrMore(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeOptional(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeList(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeMixed(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;

   ParsedPattern makeEmpty(Location var1, Annotations var2);

   ParsedPattern makeNotAllowed(Location var1, Annotations var2);

   ParsedPattern makeText(Location var1, Annotations var2);

   ParsedPattern makeAttribute(ParsedNameClass var1, ParsedPattern var2, Location var3, Annotations var4) throws BuildException;

   ParsedPattern makeElement(ParsedNameClass var1, ParsedPattern var2, Location var3, Annotations var4) throws BuildException;

   DataPatternBuilder makeDataPatternBuilder(String var1, String var2, Location var3) throws BuildException;

   ParsedPattern makeValue(String var1, String var2, String var3, Context var4, String var5, Location var6, Annotations var7) throws BuildException;

   Grammar makeGrammar(Scope var1);

   ParsedPattern annotate(ParsedPattern var1, Annotations var2) throws BuildException;

   ParsedPattern annotateAfter(ParsedPattern var1, ParsedElementAnnotation var2) throws BuildException;

   ParsedPattern commentAfter(ParsedPattern var1, CommentList var2) throws BuildException;

   ParsedPattern makeExternalRef(Parseable var1, String var2, String var3, Scope var4, Location var5, Annotations var6) throws BuildException, IllegalSchemaException;

   Location makeLocation(String var1, int var2, int var3);

   Annotations makeAnnotations(CommentList var1, Context var2);

   ElementAnnotationBuilder makeElementAnnotationBuilder(String var1, String var2, String var3, Location var4, CommentList var5, Context var6);

   CommentList makeCommentList();

   ParsedPattern makeErrorPattern();

   boolean usesComments();

   ParsedPattern expandPattern(ParsedPattern var1) throws BuildException, IllegalSchemaException;
}
