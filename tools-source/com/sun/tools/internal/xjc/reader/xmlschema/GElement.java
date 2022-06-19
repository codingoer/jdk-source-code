package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.reader.gbind.Element;
import java.util.HashSet;
import java.util.Set;

abstract class GElement extends Element {
   final Set particles = new HashSet();

   abstract String getPropertyNameSeed();
}
