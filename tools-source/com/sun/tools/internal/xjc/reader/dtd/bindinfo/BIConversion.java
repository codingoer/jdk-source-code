package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.tools.internal.xjc.model.TypeUse;

public interface BIConversion {
   String name();

   TypeUse getTransducer();
}
