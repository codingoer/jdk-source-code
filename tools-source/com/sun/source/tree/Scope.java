package com.sun.source.tree;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import jdk.Exported;

@Exported
public interface Scope {
   Scope getEnclosingScope();

   TypeElement getEnclosingClass();

   ExecutableElement getEnclosingMethod();

   Iterable getLocalElements();
}
