package com.sun.tools.corba.se.idl;

class TokenBuffer {
   private final int DEFAULT_SIZE = 10;
   private int _size = 0;
   private Token[] _buffer = null;
   private int _currPos = -1;

   TokenBuffer() {
      this._size = 10;
      this._buffer = new Token[this._size];
      this._currPos = -1;
   }

   TokenBuffer(int var1) throws Exception {
      this._size = var1;
      this._buffer = new Token[this._size];
      this._currPos = -1;
   }

   void insert(Token var1) {
      this._currPos = ++this._currPos % this._size;
      this._buffer[this._currPos] = var1;
   }

   Token lookBack(int var1) {
      return this._buffer[this._currPos - var1 >= 0 ? this._currPos - var1 : this._currPos - var1 + this._size];
   }

   Token current() {
      return this._buffer[this._currPos];
   }
}
