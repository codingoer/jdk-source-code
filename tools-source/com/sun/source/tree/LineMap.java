package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface LineMap {
   long getStartPosition(long var1);

   long getPosition(long var1, long var3);

   long getLineNumber(long var1);

   long getColumnNumber(long var1);
}
