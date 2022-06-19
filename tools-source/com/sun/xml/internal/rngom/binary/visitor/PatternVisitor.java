package com.sun.xml.internal.rngom.binary.visitor;

import com.sun.xml.internal.rngom.binary.Pattern;
import com.sun.xml.internal.rngom.nc.NameClass;
import org.relaxng.datatype.Datatype;

public interface PatternVisitor {
   void visitEmpty();

   void visitNotAllowed();

   void visitError();

   void visitAfter(Pattern var1, Pattern var2);

   void visitGroup(Pattern var1, Pattern var2);

   void visitInterleave(Pattern var1, Pattern var2);

   void visitChoice(Pattern var1, Pattern var2);

   void visitOneOrMore(Pattern var1);

   void visitElement(NameClass var1, Pattern var2);

   void visitAttribute(NameClass var1, Pattern var2);

   void visitData(Datatype var1);

   void visitDataExcept(Datatype var1, Pattern var2);

   void visitValue(Datatype var1, Object var2);

   void visitText();

   void visitList(Pattern var1);
}
