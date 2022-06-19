package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface SerialTree extends BlockTagTree {
   List getDescription();
}
