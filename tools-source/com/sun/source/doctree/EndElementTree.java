package com.sun.source.doctree;

import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface EndElementTree extends DocTree {
   Name getName();
}
