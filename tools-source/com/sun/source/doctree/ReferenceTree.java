package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface ReferenceTree extends DocTree {
   String getSignature();
}
