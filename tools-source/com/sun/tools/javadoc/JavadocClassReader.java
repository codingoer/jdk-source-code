package com.sun.tools.javadoc;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.util.Context;
import java.util.EnumSet;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class JavadocClassReader extends ClassReader {
   private DocEnv docenv;
   private EnumSet all;
   private EnumSet noSource;

   public static JavadocClassReader instance0(Context var0) {
      Object var1 = (ClassReader)var0.get(classReaderKey);
      if (var1 == null) {
         var1 = new JavadocClassReader(var0);
      }

      return (JavadocClassReader)var1;
   }

   public static void preRegister(Context var0) {
      var0.put(classReaderKey, new Context.Factory() {
         public ClassReader make(Context var1) {
            return new JavadocClassReader(var1);
         }
      });
   }

   public JavadocClassReader(Context var1) {
      super(var1, true);
      this.all = EnumSet.of(Kind.CLASS, Kind.SOURCE, Kind.HTML);
      this.noSource = EnumSet.of(Kind.CLASS, Kind.HTML);
      this.docenv = DocEnv.instance(var1);
      this.preferSource = true;
   }

   protected EnumSet getPackageFileKinds() {
      return this.docenv.docClasses ? this.noSource : this.all;
   }

   protected void extraFileActions(Symbol.PackageSymbol var1, JavaFileObject var2) {
      if (var2.isNameCompatible("package", Kind.HTML)) {
         this.docenv.getPackageDoc(var1).setDocPath(var2);
      }

   }
}
