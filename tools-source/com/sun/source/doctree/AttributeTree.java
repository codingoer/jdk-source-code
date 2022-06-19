package com.sun.source.doctree;

import java.util.List;
import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface AttributeTree extends DocTree {
   Name getName();

   ValueKind getValueKind();

   List getValue();

   @Exported
   public static enum ValueKind {
      EMPTY,
      UNQUOTED,
      SINGLE,
      DOUBLE;
   }
}
