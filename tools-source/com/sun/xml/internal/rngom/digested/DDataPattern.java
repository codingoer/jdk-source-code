package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.parse.Context;
import java.util.ArrayList;
import java.util.List;

public class DDataPattern extends DPattern {
   DPattern except;
   String datatypeLibrary;
   String type;
   final List params = new ArrayList();

   public String getDatatypeLibrary() {
      return this.datatypeLibrary;
   }

   public String getType() {
      return this.type;
   }

   public List getParams() {
      return this.params;
   }

   public DPattern getExcept() {
      return this.except;
   }

   public boolean isNullable() {
      return false;
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onData(this);
   }

   public final class Param {
      String name;
      String value;
      Context context;
      String ns;
      Location loc;
      Annotation anno;

      public Param(String name, String value, Context context, String ns, Location loc, Annotation anno) {
         this.name = name;
         this.value = value;
         this.context = context;
         this.ns = ns;
         this.loc = loc;
         this.anno = anno;
      }

      public String getName() {
         return this.name;
      }

      public String getValue() {
         return this.value;
      }

      public Context getContext() {
         return this.context;
      }

      public String getNs() {
         return this.ns;
      }

      public Location getLoc() {
         return this.loc;
      }

      public Annotation getAnno() {
         return this.anno;
      }
   }
}
