package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ReturnTree extends BlockTagTree {
   List getDescription();
}
