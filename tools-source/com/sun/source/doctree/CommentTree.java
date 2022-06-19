package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface CommentTree extends DocTree {
   String getBody();
}
