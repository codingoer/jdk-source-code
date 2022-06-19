package sun.tools.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodSet {
   private final Map lookupMap = new HashMap();
   private int count = 0;
   private boolean frozen = false;

   public int size() {
      return this.count;
   }

   public void add(MemberDefinition var1) {
      if (this.frozen) {
         throw new CompilerError("add()");
      } else {
         Identifier var2 = var1.getName();
         Object var3 = (List)this.lookupMap.get(var2);
         if (var3 == null) {
            var3 = new ArrayList();
            this.lookupMap.put(var2, var3);
         }

         int var4 = ((List)var3).size();

         for(int var5 = 0; var5 < var4; ++var5) {
            if (((MemberDefinition)((List)var3).get(var5)).getType().equalArguments(var1.getType())) {
               throw new CompilerError("duplicate addition");
            }
         }

         ((List)var3).add(var1);
         ++this.count;
      }
   }

   public void replace(MemberDefinition var1) {
      if (this.frozen) {
         throw new CompilerError("replace()");
      } else {
         Identifier var2 = var1.getName();
         Object var3 = (List)this.lookupMap.get(var2);
         if (var3 == null) {
            var3 = new ArrayList();
            this.lookupMap.put(var2, var3);
         }

         int var4 = ((List)var3).size();

         for(int var5 = 0; var5 < var4; ++var5) {
            if (((MemberDefinition)((List)var3).get(var5)).getType().equalArguments(var1.getType())) {
               ((List)var3).set(var5, var1);
               return;
            }
         }

         ((List)var3).add(var1);
         ++this.count;
      }
   }

   public MemberDefinition lookupSig(Identifier var1, Type var2) {
      Iterator var3 = this.lookupName(var1);

      MemberDefinition var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (MemberDefinition)var3.next();
      } while(!var4.getType().equalArguments(var2));

      return var4;
   }

   public Iterator lookupName(Identifier var1) {
      List var2 = (List)this.lookupMap.get(var1);
      return var2 == null ? Collections.emptyIterator() : var2.iterator();
   }

   public Iterator iterator() {
      class MethodIterator implements Iterator {
         Iterator hashIter;
         Iterator listIter;

         MethodIterator() {
            this.hashIter = MethodSet.this.lookupMap.values().iterator();
            this.listIter = Collections.emptyIterator();
         }

         public boolean hasNext() {
            if (this.listIter.hasNext()) {
               return true;
            } else if (this.hashIter.hasNext()) {
               this.listIter = ((List)this.hashIter.next()).iterator();
               if (this.listIter.hasNext()) {
                  return true;
               } else {
                  throw new CompilerError("iterator() in MethodSet");
               }
            } else {
               return false;
            }
         }

         public Object next() {
            return this.listIter.next();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      }

      return new MethodIterator();
   }

   public void freeze() {
      this.frozen = true;
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public String toString() {
      int var1 = this.size();
      StringBuffer var2 = new StringBuffer();
      Iterator var3 = this.iterator();
      var2.append("{");

      while(var3.hasNext()) {
         var2.append(var3.next().toString());
         --var1;
         if (var1 > 0) {
            var2.append(", ");
         }
      }

      var2.append("}");
      return var2.toString();
   }
}
