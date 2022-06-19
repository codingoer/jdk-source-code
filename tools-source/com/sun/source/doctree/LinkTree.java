package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface LinkTree extends InlineTagTree {
   ReferenceTree getReference();

   List getLabel();
}
