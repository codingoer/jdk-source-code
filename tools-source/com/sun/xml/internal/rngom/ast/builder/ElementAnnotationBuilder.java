package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;

public interface ElementAnnotationBuilder extends Annotations {
   void addText(String var1, Location var2, CommentList var3) throws BuildException;

   ParsedElementAnnotation makeElementAnnotation() throws BuildException;
}
