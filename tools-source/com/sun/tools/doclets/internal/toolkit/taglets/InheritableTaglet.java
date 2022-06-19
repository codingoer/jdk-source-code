package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder;

public interface InheritableTaglet extends Taglet {
   void inherit(DocFinder.Input var1, DocFinder.Output var2);
}
