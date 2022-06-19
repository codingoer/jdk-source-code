package com.sun.tools.internal.xjc.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class MimeTypeRange {
   public final String majorType;
   public final String subType;
   public final Map parameters;
   public final float q;
   public static final MimeTypeRange ALL = create("*/*");

   public static List parseRanges(String s) throws ParseException {
      StringCutter cutter = new StringCutter(s, true);
      List r = new ArrayList();

      while(cutter.length() > 0) {
         r.add(new MimeTypeRange(cutter));
      }

      return r;
   }

   public MimeTypeRange(String s) throws ParseException {
      this(new StringCutter(s, true));
   }

   private static MimeTypeRange create(String s) {
      try {
         return new MimeTypeRange(s);
      } catch (ParseException var2) {
         throw new Error(var2);
      }
   }

   private MimeTypeRange(StringCutter cutter) throws ParseException {
      this.parameters = new HashMap();
      this.majorType = cutter.until("/");
      cutter.next("/");
      this.subType = cutter.until("[;,]");
      float q = 1.0F;

      while(cutter.length() > 0) {
         String sep = cutter.next("[;,]");
         if (sep.equals(",")) {
            break;
         }

         String key = cutter.until("=");
         cutter.next("=");
         char ch = cutter.peek();
         String value;
         if (ch == '"') {
            cutter.next("\"");
            value = cutter.until("\"");
            cutter.next("\"");
         } else {
            value = cutter.until("[;,]");
         }

         if (key.equals("q")) {
            q = Float.parseFloat(value);
         } else {
            this.parameters.put(key, value);
         }
      }

      this.q = q;
   }

   public MimeType toMimeType() throws MimeTypeParseException {
      return new MimeType(this.toString());
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(this.majorType + '/' + this.subType);
      if (this.q != 1.0F) {
         sb.append("; q=").append(this.q);
      }

      Iterator var2 = this.parameters.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry p = (Map.Entry)var2.next();
         sb.append("; ").append((String)p.getKey()).append('=').append((String)p.getValue());
      }

      return sb.toString();
   }

   public static MimeTypeRange merge(Collection types) {
      if (types.size() == 0) {
         throw new IllegalArgumentException();
      } else if (types.size() == 1) {
         return (MimeTypeRange)types.iterator().next();
      } else {
         String majorType = null;
         Iterator var2 = types.iterator();

         MimeTypeRange mt;
         do {
            if (!var2.hasNext()) {
               return create(majorType + "/*");
            }

            mt = (MimeTypeRange)var2.next();
            if (majorType == null) {
               majorType = mt.majorType;
            }
         } while(majorType.equals(mt.majorType));

         return ALL;
      }
   }

   public static void main(String[] args) throws ParseException {
      Iterator var1 = parseRanges(args[0]).iterator();

      while(var1.hasNext()) {
         MimeTypeRange m = (MimeTypeRange)var1.next();
         System.out.println(m.toString());
      }

   }
}
