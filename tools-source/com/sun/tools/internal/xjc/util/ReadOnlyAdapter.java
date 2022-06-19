package com.sun.tools.internal.xjc.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class ReadOnlyAdapter extends XmlAdapter {
   public final Object marshal(Object onTheWire) {
      return null;
   }
}
