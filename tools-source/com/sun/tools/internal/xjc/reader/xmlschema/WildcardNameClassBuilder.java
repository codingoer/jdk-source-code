package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.xml.internal.rngom.nc.AnyNameExceptNameClass;
import com.sun.xml.internal.rngom.nc.ChoiceNameClass;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.rngom.nc.NsNameClass;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSWildcardFunction;
import java.util.Iterator;

public final class WildcardNameClassBuilder implements XSWildcardFunction {
   private static final XSWildcardFunction theInstance = new WildcardNameClassBuilder();

   private WildcardNameClassBuilder() {
   }

   public static NameClass build(XSWildcard wc) {
      return (NameClass)wc.apply(theInstance);
   }

   public NameClass any(XSWildcard.Any wc) {
      return NameClass.ANY;
   }

   public NameClass other(XSWildcard.Other wc) {
      return new AnyNameExceptNameClass(new ChoiceNameClass(new NsNameClass(""), new NsNameClass(wc.getOtherNamespace())));
   }

   public NameClass union(XSWildcard.Union wc) {
      NameClass nc = null;
      Iterator itr = wc.iterateNamespaces();

      while(itr.hasNext()) {
         String ns = (String)itr.next();
         if (nc == null) {
            nc = new NsNameClass(ns);
         } else {
            nc = new ChoiceNameClass((NameClass)nc, new NsNameClass(ns));
         }
      }

      assert nc != null;

      return (NameClass)nc;
   }
}
