package sun.tools.java;

import sun.tools.tree.Node;

public interface ParserActions {
   void packageDeclaration(long var1, IdentifierToken var3);

   void importClass(long var1, IdentifierToken var3);

   void importPackage(long var1, IdentifierToken var3);

   ClassDefinition beginClass(long var1, String var3, int var4, IdentifierToken var5, IdentifierToken var6, IdentifierToken[] var7);

   void endClass(long var1, ClassDefinition var3);

   void defineField(long var1, ClassDefinition var3, String var4, int var5, Type var6, IdentifierToken var7, IdentifierToken[] var8, IdentifierToken[] var9, Node var10);
}
