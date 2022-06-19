package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface TypeParameterTree extends Tree {
   Name getName();

   List getBounds();

   List getAnnotations();
}
