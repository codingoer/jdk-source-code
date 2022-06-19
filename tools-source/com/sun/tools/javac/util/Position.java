package com.sun.tools.javac.util;

import java.util.BitSet;

public class Position {
   public static final int NOPOS = -1;
   public static final int FIRSTPOS = 0;
   public static final int FIRSTLINE = 1;
   public static final int FIRSTCOLUMN = 1;
   public static final int LINESHIFT = 10;
   public static final int MAXCOLUMN = 1023;
   public static final int MAXLINE = 4194303;
   public static final int MAXPOS = Integer.MAX_VALUE;

   private Position() {
   }

   public static LineMap makeLineMap(char[] var0, int var1, boolean var2) {
      Object var3 = var2 ? new LineTabMapImpl(var1) : new LineMapImpl();
      ((LineMapImpl)var3).build(var0, var1);
      return (LineMap)var3;
   }

   public static int encodePosition(int var0, int var1) {
      if (var0 < 1) {
         throw new IllegalArgumentException("line must be greater than 0");
      } else if (var1 < 1) {
         throw new IllegalArgumentException("column must be greater than 0");
      } else {
         return var0 <= 4194303 && var1 <= 1023 ? (var0 << 10) + var1 : -1;
      }
   }

   public static class LineTabMapImpl extends LineMapImpl {
      private BitSet tabMap;

      public LineTabMapImpl(int var1) {
         this.tabMap = new BitSet(var1);
      }

      protected void setTabPosition(int var1) {
         this.tabMap.set(var1);
      }

      public int getColumnNumber(int var1) {
         int var2 = this.startPosition[this.getLineNumber(var1) - 1];
         int var3 = 0;

         for(int var4 = var2; var4 < var1; ++var4) {
            if (this.tabMap.get(var4)) {
               var3 = var3 / 8 * 8 + 8;
            } else {
               ++var3;
            }
         }

         return var3 + 1;
      }

      public int getPosition(int var1, int var2) {
         int var3 = this.startPosition[var1 - 1];
         --var2;
         int var4 = 0;

         while(var4 < var2) {
            ++var3;
            if (this.tabMap.get(var3)) {
               var4 = var4 / 8 * 8 + 8;
            } else {
               ++var4;
            }
         }

         return var3;
      }
   }

   static class LineMapImpl implements LineMap {
      protected int[] startPosition;
      private int lastPosition = 0;
      private int lastLine = 1;

      protected LineMapImpl() {
      }

      protected void build(char[] var1, int var2) {
         int var3 = 0;
         int var4 = 0;
         int[] var5 = new int[var2];

         while(true) {
            while(true) {
               while(var4 < var2) {
                  var5[var3++] = var4;

                  while(true) {
                     char var6 = var1[var4];
                     if (var6 == '\r' || var6 == '\n') {
                        if (var6 == '\r' && var4 + 1 < var2 && var1[var4 + 1] == '\n') {
                           var4 += 2;
                           break;
                        }

                        ++var4;
                        break;
                     }

                     if (var6 == '\t') {
                        this.setTabPosition(var4);
                     }

                     ++var4;
                     if (var4 >= var2) {
                        break;
                     }
                  }
               }

               this.startPosition = new int[var3];
               System.arraycopy(var5, 0, this.startPosition, 0, var3);
               return;
            }
         }
      }

      public int getStartPosition(int var1) {
         return this.startPosition[var1 - 1];
      }

      public long getStartPosition(long var1) {
         return (long)this.getStartPosition(longToInt(var1));
      }

      public int getPosition(int var1, int var2) {
         return this.startPosition[var1 - 1] + var2 - 1;
      }

      public long getPosition(long var1, long var3) {
         return (long)this.getPosition(longToInt(var1), longToInt(var3));
      }

      public int getLineNumber(int var1) {
         if (var1 == this.lastPosition) {
            return this.lastLine;
         } else {
            this.lastPosition = var1;
            int var2 = 0;
            int var3 = this.startPosition.length - 1;

            while(var2 <= var3) {
               int var4 = var2 + var3 >> 1;
               int var5 = this.startPosition[var4];
               if (var5 < var1) {
                  var2 = var4 + 1;
               } else {
                  if (var5 <= var1) {
                     this.lastLine = var4 + 1;
                     return this.lastLine;
                  }

                  var3 = var4 - 1;
               }
            }

            this.lastLine = var2;
            return this.lastLine;
         }
      }

      public long getLineNumber(long var1) {
         return (long)this.getLineNumber(longToInt(var1));
      }

      public int getColumnNumber(int var1) {
         return var1 - this.startPosition[this.getLineNumber(var1) - 1] + 1;
      }

      public long getColumnNumber(long var1) {
         return (long)this.getColumnNumber(longToInt(var1));
      }

      private static int longToInt(long var0) {
         int var2 = (int)var0;
         if ((long)var2 != var0) {
            throw new IndexOutOfBoundsException();
         } else {
            return var2;
         }
      }

      protected void setTabPosition(int var1) {
      }
   }

   public interface LineMap extends com.sun.source.tree.LineMap {
      int getStartPosition(int var1);

      int getPosition(int var1, int var2);

      int getLineNumber(int var1);

      int getColumnNumber(int var1);
   }
}
