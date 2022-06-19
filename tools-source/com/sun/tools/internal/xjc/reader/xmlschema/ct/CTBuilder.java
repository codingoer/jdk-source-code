package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.BindGreen;
import com.sun.tools.internal.xjc.reader.xmlschema.ClassSelector;
import com.sun.tools.internal.xjc.reader.xmlschema.SimpleTypeBuilder;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSSchemaSet;

abstract class CTBuilder {
   protected final ComplexTypeFieldBuilder builder = (ComplexTypeFieldBuilder)Ring.get(ComplexTypeFieldBuilder.class);
   protected final ClassSelector selector = (ClassSelector)Ring.get(ClassSelector.class);
   protected final SimpleTypeBuilder simpleTypeBuilder = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
   protected final ErrorReceiver errorReceiver = (ErrorReceiver)Ring.get(ErrorReceiver.class);
   protected final BindGreen green = (BindGreen)Ring.get(BindGreen.class);
   protected final XSSchemaSet schemas = (XSSchemaSet)Ring.get(XSSchemaSet.class);
   protected final BGMBuilder bgmBuilder = (BGMBuilder)Ring.get(BGMBuilder.class);

   abstract boolean isApplicable(XSComplexType var1);

   abstract void build(XSComplexType var1);
}
