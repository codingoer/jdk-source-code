package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface MethodTree extends Tree {
   ModifiersTree getModifiers();

   Name getName();

   Tree getReturnType();

   List getTypeParameters();

   List getParameters();

   VariableTree getReceiverParameter();

   List getThrows();

   BlockTree getBody();

   Tree getDefaultValue();
}
