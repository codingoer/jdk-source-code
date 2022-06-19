package com.sun.xml.internal.rngom.binary;

final class PatternInterner {
   private static final int INIT_SIZE = 256;
   private static final float LOAD_FACTOR = 0.3F;
   private Pattern[] table;
   private int used;
   private int usedLimit;

   PatternInterner() {
      this.table = null;
      this.used = 0;
      this.usedLimit = 0;
   }

   PatternInterner(PatternInterner parent) {
      this.table = parent.table;
      if (this.table != null) {
         this.table = (Pattern[])((Pattern[])this.table.clone());
      }

      this.used = parent.used;
      this.usedLimit = parent.usedLimit;
   }

   Pattern intern(Pattern p) {
      int h;
      if (this.table == null) {
         this.table = new Pattern[256];
         this.usedLimit = 76;
         h = this.firstIndex(p);
      } else {
         for(h = this.firstIndex(p); this.table[h] != null; h = this.nextIndex(h)) {
            if (p.samePattern(this.table[h])) {
               return this.table[h];
            }
         }
      }

      if (this.used >= this.usedLimit) {
         Pattern[] oldTable = this.table;
         this.table = new Pattern[this.table.length << 1];
         int i = oldTable.length;

         label45:
         while(true) {
            do {
               if (i <= 0) {
                  for(h = this.firstIndex(p); this.table[h] != null; h = this.nextIndex(h)) {
                  }

                  this.usedLimit = (int)((float)this.table.length * 0.3F);
                  break label45;
               }

               --i;
            } while(oldTable[i] == null);

            int j;
            for(j = this.firstIndex(oldTable[i]); this.table[j] != null; j = this.nextIndex(j)) {
            }

            this.table[j] = oldTable[i];
         }
      }

      ++this.used;
      this.table[h] = p;
      return p;
   }

   private int firstIndex(Pattern p) {
      return p.patternHashCode() & this.table.length - 1;
   }

   private int nextIndex(int i) {
      return i == 0 ? this.table.length - 1 : i - 1;
   }
}
