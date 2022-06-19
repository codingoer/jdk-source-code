package com.sun.tools.internal.xjc.reader;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.ErrorReceiver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class TypeUtil {
   private static final Comparator typeComparator = new Comparator() {
      public int compare(JType t1, JType t2) {
         return t1.fullName().compareTo(t2.fullName());
      }
   };

   public static JType getCommonBaseType(JCodeModel codeModel, Collection types) {
      return getCommonBaseType(codeModel, (JType[])types.toArray(new JType[types.size()]));
   }

   public static JType getCommonBaseType(JCodeModel codeModel, JType... t) {
      Set uniqueTypes = new TreeSet(typeComparator);
      JType[] var3 = t;
      int var4 = t.length;

      int i;
      for(i = 0; i < var4; ++i) {
         JType type = var3[i];
         uniqueTypes.add(type);
      }

      if (uniqueTypes.size() == 1) {
         return (JType)uniqueTypes.iterator().next();
      } else {
         assert !uniqueTypes.isEmpty();

         uniqueTypes.remove(codeModel.NULL);
         Set s = null;
         Iterator var16 = uniqueTypes.iterator();

         while(var16.hasNext()) {
            JType type = (JType)var16.next();
            JClass cls = type.boxify();
            if (s == null) {
               s = getAssignableTypes(cls);
            } else {
               s.retainAll(getAssignableTypes(cls));
            }
         }

         s.add(codeModel.ref(Object.class));
         JClass[] raw = (JClass[])s.toArray(new JClass[s.size()]);
         s.clear();

         for(i = 0; i < raw.length; ++i) {
            int j;
            for(j = 0; j < raw.length && (i == j || !raw[i].isAssignableFrom(raw[j])); ++j) {
            }

            if (j == raw.length) {
               s.add(raw[i]);
            }
         }

         assert !s.isEmpty();

         JClass result = pickOne(s);
         if (result.isParameterized()) {
            return result;
         } else {
            List parameters = new ArrayList(uniqueTypes.size());
            int paramLen = -1;

            JClass bound;
            List tp;
            for(Iterator var8 = uniqueTypes.iterator(); var8.hasNext(); paramLen = tp.size()) {
               JType type = (JType)var8.next();
               JClass cls = type.boxify();
               bound = cls.getBaseClass(result);
               if (bound.equals(result)) {
                  return result;
               }

               assert bound.isParameterized();

               tp = bound.getTypeParameters();
               parameters.add(tp);

               assert paramLen == -1 || paramLen == tp.size();
            }

            List paramResult = new ArrayList();
            List argList = new ArrayList(parameters.size());

            for(int i = 0; i < paramLen; ++i) {
               argList.clear();
               Iterator var26 = parameters.iterator();

               while(var26.hasNext()) {
                  tp = (List)var26.next();
                  argList.add(tp.get(i));
               }

               bound = (JClass)getCommonBaseType(codeModel, (Collection)argList);
               boolean allSame = true;

               JClass a;
               for(Iterator var13 = argList.iterator(); var13.hasNext(); allSame &= a.equals(bound)) {
                  a = (JClass)var13.next();
               }

               if (!allSame) {
                  bound = bound.wildcard();
               }

               paramResult.add(bound);
            }

            return result.narrow((List)paramResult);
         }
      }
   }

   private static JClass pickOne(Set s) {
      Iterator var1 = s.iterator();

      JClass c;
      do {
         if (!var1.hasNext()) {
            return (JClass)s.iterator().next();
         }

         c = (JClass)var1.next();
      } while(!(c instanceof JDefinedClass));

      return c;
   }

   private static Set getAssignableTypes(JClass t) {
      Set r = new TreeSet(typeComparator);
      getAssignableTypes(t, r);
      return r;
   }

   private static void getAssignableTypes(JClass t, Set s) {
      if (s.add(t)) {
         s.add(t.erasure());
         JClass _super = t._extends();
         if (_super != null) {
            getAssignableTypes(_super, s);
         }

         Iterator itr = t._implements();

         while(itr.hasNext()) {
            getAssignableTypes((JClass)itr.next(), s);
         }

      }
   }

   public static JType getType(JCodeModel codeModel, String typeName, ErrorReceiver errorHandler, Locator errorSource) {
      try {
         return codeModel.parseType(typeName);
      } catch (ClassNotFoundException var5) {
         errorHandler.warning(new SAXParseException(Messages.ERR_CLASS_NOT_FOUND.format(typeName), errorSource));
         return codeModel.directClass(typeName);
      }
   }
}
