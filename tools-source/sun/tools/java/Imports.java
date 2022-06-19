package sun.tools.java;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Imports implements Constants {
   Identifier currentPackage;
   long currentPackageWhere;
   Hashtable classes;
   Vector packages;
   Vector singles;
   protected int checked;

   public Imports(Environment var1) {
      this.currentPackage = idNull;
      this.currentPackageWhere = 0L;
      this.classes = new Hashtable();
      this.packages = new Vector();
      this.singles = new Vector();
      this.addPackage(idJavaLang);
   }

   public synchronized void resolve(Environment var1) {
      if (this.checked == 0) {
         this.checked = -1;
         Vector var2 = new Vector();
         Enumeration var3 = this.packages.elements();

         IdentifierToken var4;
         Identifier var5;
         long var6;
         Identifier var8;
         while(var3.hasMoreElements()) {
            var4 = (IdentifierToken)var3.nextElement();
            var5 = var4.getName();
            var6 = var4.getWhere();
            if (var1.isExemptPackage(var5)) {
               var2.addElement(var4);
            } else {
               try {
                  var8 = var1.resolvePackageQualifiedName(var5);
                  if (importable(var8, var1)) {
                     if (var1.getPackage(var8.getTopName()).exists()) {
                        var1.error(var6, "class.and.package", var8.getTopName());
                     }

                     if (!var8.isInner()) {
                        var8 = Identifier.lookupInner(var8, idNull);
                     }

                     var5 = var8;
                  } else if (!var1.getPackage(var5).exists()) {
                     var1.error(var6, "package.not.found", var5, "import");
                  } else if (var8.isInner()) {
                     var1.error(var6, "class.and.package", var8.getTopName());
                  }

                  var2.addElement(new IdentifierToken(var6, var5));
               } catch (IOException var14) {
                  var1.error(var6, "io.exception", "import");
               }
            }
         }

         this.packages = var2;
         var3 = this.singles.elements();

         while(var3.hasMoreElements()) {
            var4 = (IdentifierToken)var3.nextElement();
            var5 = var4.getName();
            var6 = var4.getWhere();
            var8 = var5.getQualifier();
            var5 = var1.resolvePackageQualifiedName(var5);
            if (!var1.classExists(var5.getTopName())) {
               var1.error(var6, "class.not.found", var5, "import");
            }

            Identifier var9 = var5.getFlatName().getName();
            Identifier var10 = (Identifier)this.classes.get(var9);
            if (var10 != null) {
               Identifier var11 = Identifier.lookup(var10.getQualifier(), var10.getFlatName());
               Identifier var12 = Identifier.lookup(var5.getQualifier(), var5.getFlatName());
               if (!var11.equals(var12)) {
                  var1.error(var6, "ambig.class", var5, var10);
               }
            }

            this.classes.put(var9, var5);

            try {
               ClassDeclaration var17 = var1.getClassDeclaration(var5);
               ClassDefinition var18 = var17.getClassDefinitionNoCheck(var1);

               for(Identifier var13 = var18.getName().getQualifier(); var18 != null; var18 = var18.getOuterClass()) {
                  if (var18.isPrivate() || !var18.isPublic() && !var13.equals(this.currentPackage)) {
                     var1.error(var6, "cant.access.class", var18);
                     break;
                  }
               }
            } catch (AmbiguousClass var15) {
               var1.error(var6, "ambig.class", var15.name1, var15.name2);
            } catch (ClassNotFound var16) {
               var1.error(var6, "class.not.found", var16.name, "import");
            }
         }

         this.checked = 1;
      }
   }

   public synchronized Identifier resolve(Environment var1, Identifier var2) throws ClassNotFound {
      var1.dtEnter("Imports.resolve: " + var2);
      if (var2.hasAmbigPrefix()) {
         var2 = var2.removeAmbigPrefix();
      }

      if (var2.isQualified()) {
         var1.dtExit("Imports.resolve: QUALIFIED " + var2);
         return var2;
      } else {
         if (this.checked <= 0) {
            this.checked = 0;
            this.resolve(var1);
         }

         Identifier var3 = (Identifier)this.classes.get(var2);
         if (var3 != null) {
            var1.dtExit("Imports.resolve: PREVIOUSLY IMPORTED " + var2);
            return var3;
         } else {
            Identifier var4 = Identifier.lookup(this.currentPackage, var2);
            if (importable(var4, var1)) {
               var3 = var4;
            } else {
               Enumeration var5 = this.packages.elements();

               while(var5.hasMoreElements()) {
                  IdentifierToken var6 = (IdentifierToken)var5.nextElement();
                  var4 = Identifier.lookup(var6.getName(), var2);
                  if (importable(var4, var1)) {
                     if (var3 != null) {
                        var1.dtExit("Imports.resolve: AMBIGUOUS " + var2);
                        throw new AmbiguousClass(var3, var4);
                     }

                     var3 = var4;
                  }
               }
            }

            if (var3 == null) {
               var1.dtExit("Imports.resolve: NOT FOUND " + var2);
               throw new ClassNotFound(var2);
            } else {
               this.classes.put(var2, var3);
               var1.dtExit("Imports.resolve: FIRST IMPORT " + var2);
               return var3;
            }
         }
      }
   }

   public static boolean importable(Identifier var0, Environment var1) {
      if (!var0.isInner()) {
         return var1.classExists(var0);
      } else if (!var1.classExists(var0.getTopName())) {
         return false;
      } else {
         try {
            ClassDeclaration var2 = var1.getClassDeclaration(var0.getTopName());
            ClassDefinition var3 = var2.getClassDefinitionNoCheck(var1);
            return var3.innerClassExists(var0.getFlatName().getTail());
         } catch (ClassNotFound var4) {
            return false;
         }
      }
   }

   public synchronized Identifier forceResolve(Environment var1, Identifier var2) {
      if (var2.isQualified()) {
         return var2;
      } else {
         Identifier var3 = (Identifier)this.classes.get(var2);
         if (var3 != null) {
            return var3;
         } else {
            var3 = Identifier.lookup(this.currentPackage, var2);
            this.classes.put(var2, var3);
            return var3;
         }
      }
   }

   public synchronized void addClass(IdentifierToken var1) {
      this.singles.addElement(var1);
   }

   public void addClass(Identifier var1) throws AmbiguousClass {
      this.addClass(new IdentifierToken(var1));
   }

   public synchronized void addPackage(IdentifierToken var1) {
      Identifier var2 = var1.getName();
      if (var2 != this.currentPackage) {
         int var3 = this.packages.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            if (var2 == ((IdentifierToken)this.packages.elementAt(var4)).getName()) {
               return;
            }
         }

         this.packages.addElement(var1);
      }
   }

   public void addPackage(Identifier var1) {
      this.addPackage(new IdentifierToken(var1));
   }

   public synchronized void setCurrentPackage(IdentifierToken var1) {
      this.currentPackage = var1.getName();
      this.currentPackageWhere = var1.getWhere();
   }

   public synchronized void setCurrentPackage(Identifier var1) {
      this.currentPackage = var1;
   }

   public Identifier getCurrentPackage() {
      return this.currentPackage;
   }

   public List getImportedPackages() {
      return Collections.unmodifiableList(this.packages);
   }

   public List getImportedClasses() {
      return Collections.unmodifiableList(this.singles);
   }

   public Environment newEnvironment(Environment var1) {
      return new ImportEnvironment(var1, this);
   }
}
