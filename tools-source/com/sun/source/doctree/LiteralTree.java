package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface LiteralTree extends InlineTagTree {
   TextTree getBody();
}
