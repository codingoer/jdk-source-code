package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface InlineTagTree extends DocTree {
   String getTagName();
}
