package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface ValueTree extends InlineTagTree {
   ReferenceTree getReference();
}
