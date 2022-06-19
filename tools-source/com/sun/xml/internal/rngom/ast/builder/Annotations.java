package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;

public interface Annotations {
   void addAttribute(String var1, String var2, String var3, String var4, Location var5) throws BuildException;

   void addElement(ParsedElementAnnotation var1) throws BuildException;

   void addComment(CommentList var1) throws BuildException;

   void addLeadingComment(CommentList var1) throws BuildException;
}
