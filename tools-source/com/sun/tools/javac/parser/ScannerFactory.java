package com.sun.tools.javac.parser;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import java.nio.CharBuffer;

public class ScannerFactory {
   public static final Context.Key scannerFactoryKey = new Context.Key();
   final Log log;
   final Names names;
   final Source source;
   final Tokens tokens;

   public static ScannerFactory instance(Context var0) {
      ScannerFactory var1 = (ScannerFactory)var0.get(scannerFactoryKey);
      if (var1 == null) {
         var1 = new ScannerFactory(var0);
      }

      return var1;
   }

   protected ScannerFactory(Context var1) {
      var1.put((Context.Key)scannerFactoryKey, (Object)this);
      this.log = Log.instance(var1);
      this.names = Names.instance(var1);
      this.source = Source.instance(var1);
      this.tokens = Tokens.instance(var1);
   }

   public Scanner newScanner(CharSequence var1, boolean var2) {
      if (var1 instanceof CharBuffer) {
         CharBuffer var4 = (CharBuffer)var1;
         return var2 ? new Scanner(this, new JavadocTokenizer(this, var4)) : new Scanner(this, var4);
      } else {
         char[] var3 = var1.toString().toCharArray();
         return this.newScanner(var3, var3.length, var2);
      }
   }

   public Scanner newScanner(char[] var1, int var2, boolean var3) {
      return var3 ? new Scanner(this, new JavadocTokenizer(this, var1, var2)) : new Scanner(this, var1, var2);
   }
}
