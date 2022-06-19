package com.sun.tools.javac.parser;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.tree.DocTreeMaker;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.Locale;

public class ParserFactory {
   protected static final Context.Key parserFactoryKey = new Context.Key();
   final TreeMaker F;
   final DocTreeMaker docTreeMaker;
   final Log log;
   final Tokens tokens;
   final Source source;
   final Names names;
   final Options options;
   final ScannerFactory scannerFactory;
   final Locale locale;

   public static ParserFactory instance(Context var0) {
      ParserFactory var1 = (ParserFactory)var0.get(parserFactoryKey);
      if (var1 == null) {
         var1 = new ParserFactory(var0);
      }

      return var1;
   }

   protected ParserFactory(Context var1) {
      var1.put((Context.Key)parserFactoryKey, (Object)this);
      this.F = TreeMaker.instance(var1);
      this.docTreeMaker = DocTreeMaker.instance(var1);
      this.log = Log.instance(var1);
      this.names = Names.instance(var1);
      this.tokens = Tokens.instance(var1);
      this.source = Source.instance(var1);
      this.options = Options.instance(var1);
      this.scannerFactory = ScannerFactory.instance(var1);
      this.locale = (Locale)var1.get(Locale.class);
   }

   public JavacParser newParser(CharSequence var1, boolean var2, boolean var3, boolean var4) {
      Scanner var5 = this.scannerFactory.newScanner(var1, var2);
      return new JavacParser(this, var5, var2, var4, var3);
   }
}
