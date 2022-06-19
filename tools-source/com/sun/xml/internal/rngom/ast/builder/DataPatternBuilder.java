package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.Context;

public interface DataPatternBuilder {
   void addParam(String var1, String var2, Context var3, String var4, Location var5, Annotations var6) throws BuildException;

   void annotation(ParsedElementAnnotation var1);

   ParsedPattern makePattern(Location var1, Annotations var2) throws BuildException;

   ParsedPattern makePattern(ParsedPattern var1, Location var2, Annotations var3) throws BuildException;
}
