package com.sun.codemodel.internal.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

abstract class SingleByteEncoder extends CharsetEncoder {
   private final short[] index1;
   private final String index2;
   private final int mask1;
   private final int mask2;
   private final int shift;
   private final sun.nio.cs.Surrogate.Parser sgp = new sun.nio.cs.Surrogate.Parser();

   protected SingleByteEncoder(Charset cs, short[] index1, String index2, int mask1, int mask2, int shift) {
      super(cs, 1.0F, 1.0F);
      this.index1 = index1;
      this.index2 = index2;
      this.mask1 = mask1;
      this.mask2 = mask2;
      this.shift = shift;
   }

   public boolean canEncode(char c) {
      char testEncode = this.index2.charAt(this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2));
      return testEncode != 0;
   }

   private CoderResult encodeArrayLoop(CharBuffer src, ByteBuffer dst) {
      char[] sa = src.array();
      int sp = src.arrayOffset() + src.position();
      int sl = src.arrayOffset() + src.limit();
      sp = sp <= sl ? sp : sl;
      byte[] da = dst.array();
      int dp = dst.arrayOffset() + dst.position();
      int dl = dst.arrayOffset() + dst.limit();
      dp = dp <= dl ? dp : dl;

      try {
         while(sp < sl) {
            char c = sa[sp];
            CoderResult var16;
            if (sun.nio.cs.Surrogate.is(c)) {
               if (this.sgp.parse(c, sa, sp, sl) < 0) {
                  var16 = this.sgp.error();
                  return var16;
               }

               var16 = this.sgp.unmappableResult();
               return var16;
            }

            if (c >= '\ufffe') {
               var16 = CoderResult.unmappableForLength(1);
               return var16;
            }

            if (dl - dp < 1) {
               var16 = CoderResult.OVERFLOW;
               return var16;
            }

            char e = this.index2.charAt(this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2));
            if (e == 0 && c != 0) {
               CoderResult var11 = CoderResult.unmappableForLength(1);
               return var11;
            }

            ++sp;
            da[dp++] = (byte)e;
         }

         CoderResult var15 = CoderResult.UNDERFLOW;
         return var15;
      } finally {
         src.position(sp - src.arrayOffset());
         dst.position(dp - dst.arrayOffset());
      }
   }

   private CoderResult encodeBufferLoop(CharBuffer src, ByteBuffer dst) {
      int mark = src.position();

      try {
         while(src.hasRemaining()) {
            char c = src.get();
            CoderResult var11;
            if (sun.nio.cs.Surrogate.is(c)) {
               if (this.sgp.parse(c, src) < 0) {
                  var11 = this.sgp.error();
                  return var11;
               }

               var11 = this.sgp.unmappableResult();
               return var11;
            }

            if (c >= '\ufffe') {
               var11 = CoderResult.unmappableForLength(1);
               return var11;
            }

            if (!dst.hasRemaining()) {
               var11 = CoderResult.OVERFLOW;
               return var11;
            }

            char e = this.index2.charAt(this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2));
            if (e == 0 && c != 0) {
               CoderResult var6 = CoderResult.unmappableForLength(1);
               return var6;
            }

            ++mark;
            dst.put((byte)e);
         }

         CoderResult var10 = CoderResult.UNDERFLOW;
         return var10;
      } finally {
         src.position(mark);
      }
   }

   protected CoderResult encodeLoop(CharBuffer src, ByteBuffer dst) {
      return src.hasArray() && dst.hasArray() ? this.encodeArrayLoop(src, dst) : this.encodeBufferLoop(src, dst);
   }

   public byte encode(char inputChar) {
      return (byte)this.index2.charAt(this.index1[(inputChar & this.mask1) >> this.shift] + (inputChar & this.mask2));
   }
}
