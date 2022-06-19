package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import java.util.List;

public interface NameClassBuilder {
   ParsedNameClass annotate(ParsedNameClass var1, Annotations var2) throws BuildException;

   ParsedNameClass annotateAfter(ParsedNameClass var1, ParsedElementAnnotation var2) throws BuildException;

   ParsedNameClass commentAfter(ParsedNameClass var1, CommentList var2) throws BuildException;

   ParsedNameClass makeChoice(List var1, Location var2, Annotations var3);

   ParsedNameClass makeName(String var1, String var2, String var3, Location var4, Annotations var5);

   ParsedNameClass makeNsName(String var1, Location var2, Annotations var3);

   ParsedNameClass makeNsName(String var1, ParsedNameClass var2, Location var3, Annotations var4);

   ParsedNameClass makeAnyName(Location var1, Annotations var2);

   ParsedNameClass makeAnyName(ParsedNameClass var1, Location var2, Annotations var3);

   ParsedNameClass makeErrorNameClass();
}
