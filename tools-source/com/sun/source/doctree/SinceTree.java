package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface SinceTree extends BlockTagTree {
   List getBody();
}
