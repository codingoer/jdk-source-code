package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ThrowsTree extends BlockTagTree {
   ReferenceTree getExceptionName();

   List getDescription();
}
