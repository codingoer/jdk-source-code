package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ParamTree extends BlockTagTree {
   boolean isTypeParameter();

   IdentifierTree getName();

   List getDescription();
}
