package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface ClassTree extends StatementTree {
   ModifiersTree getModifiers();

   Name getSimpleName();

   List getTypeParameters();

   Tree getExtendsClause();

   List getImplementsClause();

   List getMembers();
}
