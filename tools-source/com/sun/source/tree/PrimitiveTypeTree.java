package com.sun.source.tree;

import javax.lang.model.type.TypeKind;
import jdk.Exported;

@Exported
public interface PrimitiveTypeTree extends Tree {
   TypeKind getPrimitiveTypeKind();
}
