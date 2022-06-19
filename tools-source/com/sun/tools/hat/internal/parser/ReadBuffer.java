package com.sun.tools.hat.internal.parser;

import java.io.IOException;

public interface ReadBuffer {
   void get(long var1, byte[] var3) throws IOException;

   char getChar(long var1) throws IOException;

   byte getByte(long var1) throws IOException;

   short getShort(long var1) throws IOException;

   int getInt(long var1) throws IOException;

   long getLong(long var1) throws IOException;
}
