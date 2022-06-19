package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface DeprecatedTree extends BlockTagTree {
   List getBody();
}
