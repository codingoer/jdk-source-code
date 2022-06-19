package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface SeeTree extends BlockTagTree {
   List getReference();
}
