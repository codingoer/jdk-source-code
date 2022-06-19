package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import com.sun.xml.internal.xsom.impl.scd.SCDImpl;
import com.sun.xml.internal.xsom.impl.scd.SCDParser;
import com.sun.xml.internal.xsom.impl.scd.Step;
import com.sun.xml.internal.xsom.impl.scd.TokenMgrError;
import com.sun.xml.internal.xsom.util.DeferedCollection;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;

public abstract class SCD {
   public static SCD create(String path, NamespaceContext nsContext) throws ParseException {
      try {
         SCDParser p = new SCDParser(path, nsContext);
         List list = p.RelativeSchemaComponentPath();
         return new SCDImpl(path, (Step[])list.toArray(new Step[list.size()]));
      } catch (TokenMgrError var4) {
         throw setCause(new ParseException(var4.getMessage(), -1), var4);
      } catch (com.sun.xml.internal.xsom.impl.scd.ParseException var5) {
         throw setCause(new ParseException(var5.getMessage(), var5.currentToken.beginColumn), var5);
      }
   }

   private static ParseException setCause(ParseException e, Throwable x) {
      e.initCause(x);
      return e;
   }

   public final Collection select(XSComponent contextNode) {
      return new DeferedCollection(this.select(Iterators.singleton(contextNode)));
   }

   public final Collection select(XSSchemaSet contextNode) {
      return this.select(contextNode.getSchemas());
   }

   public final XSComponent selectSingle(XSComponent contextNode) {
      Iterator r = this.select(Iterators.singleton(contextNode));
      return r.hasNext() ? (XSComponent)r.next() : null;
   }

   public final XSComponent selectSingle(XSSchemaSet contextNode) {
      Iterator r = this.select(contextNode.iterateSchema());
      return r.hasNext() ? (XSComponent)r.next() : null;
   }

   public abstract Iterator select(Iterator var1);

   public final Collection select(Collection contextNodes) {
      return new DeferedCollection(this.select(contextNodes.iterator()));
   }

   public abstract String toString();
}
