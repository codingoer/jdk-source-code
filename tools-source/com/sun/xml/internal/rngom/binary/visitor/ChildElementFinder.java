package com.sun.xml.internal.rngom.binary.visitor;

import com.sun.xml.internal.rngom.binary.Pattern;
import com.sun.xml.internal.rngom.nc.NameClass;
import java.util.HashSet;
import java.util.Set;

public class ChildElementFinder extends PatternWalker {
   private final Set children = new HashSet();

   public Set getChildren() {
      return this.children;
   }

   public void visitElement(NameClass nc, Pattern content) {
      this.children.add(new Element(nc, content));
   }

   public void visitAttribute(NameClass ns, Pattern value) {
   }

   public void visitList(Pattern p) {
   }

   public static class Element {
      public final NameClass nc;
      public final Pattern content;

      public Element(NameClass nc, Pattern content) {
         this.nc = nc;
         this.content = content;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof Element)) {
            return false;
         } else {
            Element element = (Element)o;
            if (this.content != null) {
               if (!this.content.equals(element.content)) {
                  return false;
               }
            } else if (element.content != null) {
               return false;
            }

            if (this.nc != null) {
               if (!this.nc.equals(element.nc)) {
                  return false;
               }
            } else if (element.nc != null) {
               return false;
            }

            return true;
         }
      }

      public int hashCode() {
         int result = this.nc != null ? this.nc.hashCode() : 0;
         result = 29 * result + (this.content != null ? this.content.hashCode() : 0);
         return result;
      }
   }
}
