package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface TextTree extends DocTree {
   String getBody();
}
