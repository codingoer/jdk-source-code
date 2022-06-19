package com.sun.source.doctree;

import javax.tools.Diagnostic;
import jdk.Exported;

@Exported
public interface ErroneousTree extends TextTree {
   Diagnostic getDiagnostic();
}
