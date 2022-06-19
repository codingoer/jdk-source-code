package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.util.Misc;
import java.util.Enumeration;
import java.util.HashMap;

public abstract class JavaHeapObject extends JavaThing {
   private JavaThing[] referers = null;
   private int referersLen = 0;

   public abstract JavaClass getClazz();

   public abstract int getSize();

   public abstract long getId();

   public void resolve(Snapshot var1) {
      StackTrace var2 = var1.getSiteTrace(this);
      if (var2 != null) {
         var2.resolve(var1);
      }

   }

   void setupReferers() {
      if (this.referersLen > 1) {
         HashMap var1 = new HashMap();

         for(int var2 = 0; var2 < this.referersLen; ++var2) {
            if (var1.get(this.referers[var2]) == null) {
               var1.put(this.referers[var2], this.referers[var2]);
            }
         }

         this.referers = new JavaThing[var1.size()];
         var1.keySet().toArray(this.referers);
      }

      this.referersLen = -1;
   }

   public String getIdString() {
      return Misc.toHex(this.getId());
   }

   public String toString() {
      return this.getClazz().getName() + "@" + this.getIdString();
   }

   public StackTrace getAllocatedFrom() {
      return this.getClazz().getSiteTrace(this);
   }

   public boolean isNew() {
      return this.getClazz().isNew(this);
   }

   void setNew(boolean var1) {
      this.getClazz().setNew(this, var1);
   }

   public void visitReferencedObjects(JavaHeapObjectVisitor var1) {
      var1.visit(this.getClazz());
   }

   void addReferenceFrom(JavaHeapObject var1) {
      if (this.referersLen == 0) {
         this.referers = new JavaThing[1];
      } else if (this.referersLen == this.referers.length) {
         JavaThing[] var2 = new JavaThing[3 * (this.referersLen + 1) / 2];
         System.arraycopy(this.referers, 0, var2, 0, this.referersLen);
         this.referers = var2;
      }

      this.referers[this.referersLen++] = var1;
   }

   void addReferenceFromRoot(Root var1) {
      this.getClazz().addReferenceFromRoot(var1, this);
   }

   public Root getRoot() {
      return this.getClazz().getRoot(this);
   }

   public Enumeration getReferers() {
      if (this.referersLen != -1) {
         throw new RuntimeException("not resolved: " + this.getIdString());
      } else {
         return new Enumeration() {
            private int num = 0;

            public boolean hasMoreElements() {
               return JavaHeapObject.this.referers != null && this.num < JavaHeapObject.this.referers.length;
            }

            public Object nextElement() {
               return JavaHeapObject.this.referers[this.num++];
            }
         };
      }
   }

   public boolean refersOnlyWeaklyTo(Snapshot var1, JavaThing var2) {
      return false;
   }

   public String describeReferenceTo(JavaThing var1, Snapshot var2) {
      return "??";
   }

   public boolean isHeapAllocated() {
      return true;
   }
}
