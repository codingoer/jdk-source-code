package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class JCommentPart extends ArrayList {
   private static final long serialVersionUID = 1L;

   public JCommentPart append(Object o) {
      this.add(o);
      return this;
   }

   public boolean add(Object o) {
      this.flattenAppend(o);
      return true;
   }

   private void flattenAppend(Object value) {
      if (value != null) {
         if (value instanceof Object[]) {
            Object[] var2 = (Object[])((Object[])value);
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Object o = var2[var4];
               this.flattenAppend(o);
            }
         } else if (value instanceof Collection) {
            Iterator var6 = ((Collection)value).iterator();

            while(var6.hasNext()) {
               Object o = var6.next();
               this.flattenAppend(o);
            }
         } else {
            super.add(value);
         }

      }
   }

   protected void format(JFormatter f, String indent) {
      Iterator itr;
      Object o;
      if (!f.isPrinting()) {
         itr = this.iterator();

         while(itr.hasNext()) {
            o = itr.next();
            if (o instanceof JClass) {
               f.g((JGenerable)((JClass)o));
            }
         }

      } else {
         if (!this.isEmpty()) {
            f.p(indent);
         }

         itr = this.iterator();

         while(true) {
            while(itr.hasNext()) {
               o = itr.next();
               if (o instanceof String) {
                  String s = (String)o;

                  int idx;
                  while((idx = s.indexOf(10)) != -1) {
                     String line = s.substring(0, idx);
                     if (line.length() > 0) {
                        f.p(this.escape(line));
                     }

                     s = s.substring(idx + 1);
                     f.nl().p(indent);
                  }

                  if (s.length() != 0) {
                     f.p(this.escape(s));
                  }
               } else if (o instanceof JClass) {
                  ((JClass)o).printLink(f);
               } else {
                  if (!(o instanceof JType)) {
                     throw new IllegalStateException();
                  }

                  f.g((JGenerable)((JType)o));
               }
            }

            if (!this.isEmpty()) {
               f.nl();
            }

            return;
         }
      }
   }

   private String escape(String s) {
      while(true) {
         int idx = s.indexOf("*/");
         if (idx < 0) {
            return s;
         }

         s = s.substring(0, idx + 1) + "<!---->" + s.substring(idx + 1);
      }
   }
}
