package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface SerialFieldTree extends BlockTagTree {
   IdentifierTree getName();

   ReferenceTree getType();

   List getDescription();
}
