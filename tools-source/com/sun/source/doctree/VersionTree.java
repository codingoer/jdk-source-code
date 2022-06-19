package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface VersionTree extends BlockTagTree {
   List getBody();
}
