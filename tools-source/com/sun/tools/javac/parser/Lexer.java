package com.sun.tools.javac.parser;

import com.sun.tools.javac.util.Position;

public interface Lexer {
   void nextToken();

   Tokens.Token token();

   Tokens.Token token(int var1);

   Tokens.Token prevToken();

   Tokens.Token split();

   int errPos();

   void errPos(int var1);

   Position.LineMap getLineMap();
}
