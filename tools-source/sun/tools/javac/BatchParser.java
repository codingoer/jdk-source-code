package sun.tools.javac;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.Imports;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Parser;
import sun.tools.java.Type;
import sun.tools.tree.Node;

/** @deprecated */
@Deprecated
public class BatchParser extends Parser {
   protected Identifier pkg;
   protected Imports imports;
   protected Vector classes;
   protected SourceClass sourceClass;
   protected Environment toplevelEnv;

   public BatchParser(Environment var1, InputStream var2) throws IOException {
      super(var1, var2);
      this.imports = new Imports(var1);
      this.classes = new Vector();
      this.toplevelEnv = this.imports.newEnvironment(var1);
   }

   public void packageDeclaration(long var1, IdentifierToken var3) {
      Identifier var4 = var3.getName();
      if (this.pkg == null) {
         this.pkg = var3.getName();
         this.imports.setCurrentPackage(var3);
      } else {
         this.env.error(var1, "package.repeated");
      }

   }

   public void importClass(long var1, IdentifierToken var3) {
      this.imports.addClass(var3);
   }

   public void importPackage(long var1, IdentifierToken var3) {
      this.imports.addPackage(var3);
   }

   public ClassDefinition beginClass(long var1, String var3, int var4, IdentifierToken var5, IdentifierToken var6, IdentifierToken[] var7) {
      this.toplevelEnv.dtEnter("beginClass: " + this.sourceClass);
      SourceClass var8 = this.sourceClass;
      if (var8 == null && this.pkg != null) {
         var5 = new IdentifierToken(var5.getWhere(), Identifier.lookup(this.pkg, var5.getName()));
      }

      if ((var4 & 65536) != 0) {
         var4 |= 18;
      }

      if ((var4 & 131072) != 0) {
         var4 |= 2;
      }

      if ((var4 & 512) != 0) {
         var4 |= 1024;
         if (var8 != null) {
            var4 |= 8;
         }
      }

      if (var8 != null && var8.isInterface()) {
         if ((var4 & 6) == 0) {
            var4 |= 1;
         }

         var4 |= 8;
      }

      this.sourceClass = (SourceClass)this.toplevelEnv.makeClassDefinition(this.toplevelEnv, var1, var5, var3, var4, var6, var7, var8);
      this.sourceClass.getClassDeclaration().setDefinition(this.sourceClass, 4);
      this.env = new Environment(this.toplevelEnv, this.sourceClass);
      this.toplevelEnv.dtEvent("beginClass: SETTING UP DEPENDENCIES");
      this.toplevelEnv.dtEvent("beginClass: ADDING TO CLASS LIST");
      this.classes.addElement(this.sourceClass);
      this.toplevelEnv.dtExit("beginClass: " + this.sourceClass);
      return this.sourceClass;
   }

   public ClassDefinition getCurrentClass() {
      return this.sourceClass;
   }

   public void endClass(long var1, ClassDefinition var3) {
      this.toplevelEnv.dtEnter("endClass: " + this.sourceClass);
      this.sourceClass.setEndPosition(var1);
      SourceClass var4 = (SourceClass)this.sourceClass.getOuterClass();
      this.sourceClass = var4;
      this.env = this.toplevelEnv;
      if (this.sourceClass != null) {
         this.env = new Environment(this.env, this.sourceClass);
      }

      this.toplevelEnv.dtExit("endClass: " + this.sourceClass);
   }

   public void defineField(long var1, ClassDefinition var3, String var4, int var5, Type var6, IdentifierToken var7, IdentifierToken[] var8, IdentifierToken[] var9, Node var10) {
      Identifier var11 = var7.getName();
      if (this.sourceClass.isInterface()) {
         if ((var5 & 6) == 0) {
            var5 |= 1;
         }

         if (var6.isType(12)) {
            var5 |= 1024;
         } else {
            var5 |= 24;
         }
      }

      if (var11.equals(idInit)) {
         Type var12 = var6.getReturnType();
         Identifier var13 = !var12.isType(10) ? idStar : var12.getClassName();
         Identifier var14 = this.sourceClass.getLocalName();
         if (var14.equals(var13)) {
            var6 = Type.tMethod(Type.tVoid, var6.getArgumentTypes());
         } else {
            if (!var14.equals(var13.getFlatName().getName())) {
               if (!var13.isQualified() && !var13.equals(idStar)) {
                  this.env.error(var1, "invalid.method.decl");
                  return;
               }

               this.env.error(var1, "invalid.method.decl.name");
               return;
            }

            var6 = Type.tMethod(Type.tVoid, var6.getArgumentTypes());
            this.env.error(var1, "invalid.method.decl.qual");
         }
      }

      if (var8 == null && var6.isType(12)) {
         var8 = new IdentifierToken[0];
      }

      if (var9 == null && var6.isType(12)) {
         var9 = new IdentifierToken[0];
      }

      MemberDefinition var15 = this.env.makeMemberDefinition(this.env, var1, this.sourceClass, var4, var5, var6, var11, var8, var9, var10);
      if (this.env.dump()) {
         var15.print(System.out);
      }

   }
}
