package com.sun.source.doctree;

import java.util.List;
import jdk.Exported;

@Exported
public interface DocCommentTree extends DocTree {
   List getFirstSentence();

   List getBody();

   List getBlockTags();
}
