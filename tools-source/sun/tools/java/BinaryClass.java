package sun.tools.java;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import sun.tools.javac.Main;

public final class BinaryClass extends ClassDefinition implements Constants {
   BinaryConstantPool cpool;
   BinaryAttribute atts;
   Vector dependencies;
   private boolean haveLoadedNested = false;
   private boolean basicCheckDone = false;
   private boolean basicChecking = false;

   public BinaryClass(Object var1, ClassDeclaration var2, int var3, ClassDeclaration var4, ClassDeclaration[] var5, Vector var6) {
      super(var1, 0L, var2, var3, (IdentifierToken)null, (IdentifierToken[])null);
      this.dependencies = var6;
      this.superClass = var4;
      this.interfaces = var5;
   }

   protected void basicCheck(Environment var1) throws ClassNotFound {
      var1.dtEnter("BinaryClass.basicCheck: " + this.getName());
      if (!this.basicChecking && !this.basicCheckDone) {
         var1.dtEvent("BinaryClass.basicCheck: CHECKING " + this.getName());
         this.basicChecking = true;
         super.basicCheck(var1);
         if (doInheritanceChecks) {
            this.collectInheritedMethods(var1);
         }

         this.basicCheckDone = true;
         this.basicChecking = false;
         var1.dtExit("BinaryClass.basicCheck: " + this.getName());
      } else {
         var1.dtExit("BinaryClass.basicCheck: OK " + this.getName());
      }
   }

   public static BinaryClass load(Environment var0, DataInputStream var1) throws IOException {
      return load(var0, var1, -7);
   }

   public static BinaryClass load(Environment var0, DataInputStream var1, int var2) throws IOException {
      int var3 = var1.readInt();
      if (var3 != -889275714) {
         throw new ClassFormatError("wrong magic: " + var3 + ", expected " + -889275714);
      } else {
         int var4 = var1.readUnsignedShort();
         int var5 = var1.readUnsignedShort();
         if (var5 < 45) {
            throw new ClassFormatError(Main.getText("javac.err.version.too.old", String.valueOf(var5)));
         } else if (var5 <= 52 && (var5 != 52 || var4 <= 0)) {
            BinaryConstantPool var6 = new BinaryConstantPool(var1);
            Vector var7 = var6.getDependencies(var0);
            int var8 = var1.readUnsignedShort() & 3633;
            ClassDeclaration var9 = var6.getDeclaration(var0, var1.readUnsignedShort());
            ClassDeclaration var10 = var6.getDeclaration(var0, var1.readUnsignedShort());
            ClassDeclaration[] var11 = new ClassDeclaration[var1.readUnsignedShort()];

            for(int var12 = 0; var12 < var11.length; ++var12) {
               var11[var12] = var6.getDeclaration(var0, var1.readUnsignedShort());
            }

            BinaryClass var20 = new BinaryClass((Object)null, var9, var8, var10, var11, var7);
            var20.cpool = var6;
            var20.addDependency(var10);
            int var13 = var1.readUnsignedShort();

            int var14;
            int var15;
            for(var14 = 0; var14 < var13; ++var14) {
               var15 = var1.readUnsignedShort() & 223;
               Identifier var16 = var6.getIdentifier(var1.readUnsignedShort());
               Type var17 = var6.getType(var1.readUnsignedShort());
               BinaryAttribute var18 = BinaryAttribute.load(var1, var6, var2);
               var20.addMember(new BinaryMember(var20, var15, var17, var16, var18));
            }

            var14 = var1.readUnsignedShort();

            for(var15 = 0; var15 < var14; ++var15) {
               int var22 = var1.readUnsignedShort() & 3391;
               Identifier var24 = var6.getIdentifier(var1.readUnsignedShort());
               Type var25 = var6.getType(var1.readUnsignedShort());
               BinaryAttribute var19 = BinaryAttribute.load(var1, var6, var2);
               var20.addMember(new BinaryMember(var20, var22, var25, var24, var19));
            }

            var20.atts = BinaryAttribute.load(var1, var6, var2);
            byte[] var21 = var20.getAttribute(idSourceFile);
            if (var21 != null) {
               DataInputStream var23 = new DataInputStream(new ByteArrayInputStream(var21));
               var20.source = var6.getString(var23.readUnsignedShort());
            }

            var21 = var20.getAttribute(idDocumentation);
            if (var21 != null) {
               var20.documentation = (new DataInputStream(new ByteArrayInputStream(var21))).readUTF();
            }

            if (var20.getAttribute(idDeprecated) != null) {
               var20.modifiers |= 262144;
            }

            if (var20.getAttribute(idSynthetic) != null) {
               var20.modifiers |= 524288;
            }

            return var20;
         } else {
            throw new ClassFormatError(Main.getText("javac.err.version.too.recent", var5 + "." + var4));
         }
      }
   }

   public void loadNested(Environment var1) {
      this.loadNested(var1, 0);
   }

   public void loadNested(Environment var1, int var2) {
      if (this.haveLoadedNested) {
         var1.dtEvent("loadNested: DUPLICATE CALL SKIPPED");
      } else {
         this.haveLoadedNested = true;

         try {
            byte[] var3 = this.getAttribute(idInnerClasses);
            if (var3 != null) {
               this.initInnerClasses(var1, var3, var2);
            }
         } catch (IOException var4) {
            var1.error(0L, "malformed.attribute", this.getClassDeclaration(), idInnerClasses);
            var1.dtEvent("loadNested: MALFORMED ATTRIBUTE (InnerClasses)");
         }

      }
   }

   private void initInnerClasses(Environment var1, byte[] var2, int var3) throws IOException {
      DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var2));
      int var5 = var4.readUnsignedShort();

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4.readUnsignedShort();
         ClassDeclaration var8 = this.cpool.getDeclaration(var1, var7);
         ClassDeclaration var9 = null;
         int var10 = var4.readUnsignedShort();
         if (var10 != 0) {
            var9 = this.cpool.getDeclaration(var1, var10);
         }

         Identifier var11 = idNull;
         int var12 = var4.readUnsignedShort();
         if (var12 != 0) {
            var11 = Identifier.lookup(this.cpool.getString(var12));
         }

         int var13 = var4.readUnsignedShort();
         boolean var14 = var9 != null && !var11.equals(idNull) && ((var13 & 2) == 0 || (var3 & 4) != 0);
         if (var14) {
            Identifier var15 = Identifier.lookupInner(var9.getName(), var11);
            Type.tClass(var15);
            ClassDefinition var16;
            if (var8.equals(this.getClassDeclaration())) {
               try {
                  var16 = var9.getClassDefinition(var1);
                  this.initInner(var16, var13);
               } catch (ClassNotFound var18) {
               }
            } else if (var9.equals(this.getClassDeclaration())) {
               try {
                  var16 = var8.getClassDefinition(var1);
                  this.initOuter(var16, var13);
               } catch (ClassNotFound var17) {
               }
            }
         }
      }

   }

   private void initInner(ClassDefinition var1, int var2) {
      if (this.getOuterClass() == null) {
         if ((var2 & 2) != 0) {
            var2 &= -6;
         } else if ((var2 & 4) != 0) {
            var2 &= -2;
         }

         if ((var2 & 512) != 0) {
            var2 |= 1032;
         }

         if (var1.isInterface()) {
            var2 |= 9;
            var2 &= -7;
         }

         this.modifiers = var2;
         this.setOuterClass(var1);

         for(MemberDefinition var3 = this.getFirstMember(); var3 != null; var3 = var3.getNextMember()) {
            if (var3.isUplevelValue() && var1.getType().equals(var3.getType()) && var3.getName().toString().startsWith("this$")) {
               this.setOuterMember(var3);
            }
         }

      }
   }

   private void initOuter(ClassDefinition var1, int var2) {
      if (var1 instanceof BinaryClass) {
         ((BinaryClass)var1).initInner(this, var2);
      }

      this.addMember(new BinaryMember(var1));
   }

   public void write(Environment var1, OutputStream var2) throws IOException {
      DataOutputStream var3 = new DataOutputStream(var2);
      var3.writeInt(-889275714);
      var3.writeShort(var1.getMinorVersion());
      var3.writeShort(var1.getMajorVersion());
      this.cpool.write(var3, var1);
      var3.writeShort(this.getModifiers() & 3633);
      var3.writeShort(this.cpool.indexObject(this.getClassDeclaration(), var1));
      var3.writeShort(this.getSuperClass() != null ? this.cpool.indexObject(this.getSuperClass(), var1) : 0);
      var3.writeShort(this.interfaces.length);

      int var4;
      for(var4 = 0; var4 < this.interfaces.length; ++var4) {
         var3.writeShort(this.cpool.indexObject(this.interfaces[var4], var1));
      }

      var4 = 0;
      int var5 = 0;

      MemberDefinition var6;
      for(var6 = this.firstMember; var6 != null; var6 = var6.getNextMember()) {
         if (var6.isMethod()) {
            ++var5;
         } else {
            ++var4;
         }
      }

      var3.writeShort(var4);

      String var7;
      String var8;
      for(var6 = this.firstMember; var6 != null; var6 = var6.getNextMember()) {
         if (!var6.isMethod()) {
            var3.writeShort(var6.getModifiers() & 223);
            var7 = var6.getName().toString();
            var8 = var6.getType().getTypeSignature();
            var3.writeShort(this.cpool.indexString(var7, var1));
            var3.writeShort(this.cpool.indexString(var8, var1));
            BinaryAttribute.write(((BinaryMember)var6).atts, var3, this.cpool, var1);
         }
      }

      var3.writeShort(var5);

      for(var6 = this.firstMember; var6 != null; var6 = var6.getNextMember()) {
         if (var6.isMethod()) {
            var3.writeShort(var6.getModifiers() & 3391);
            var7 = var6.getName().toString();
            var8 = var6.getType().getTypeSignature();
            var3.writeShort(this.cpool.indexString(var7, var1));
            var3.writeShort(this.cpool.indexString(var8, var1));
            BinaryAttribute.write(((BinaryMember)var6).atts, var3, this.cpool, var1);
         }
      }

      BinaryAttribute.write(this.atts, var3, this.cpool, var1);
      var3.flush();
   }

   public Enumeration getDependencies() {
      return this.dependencies.elements();
   }

   public void addDependency(ClassDeclaration var1) {
      if (var1 != null && !this.dependencies.contains(var1)) {
         this.dependencies.addElement(var1);
      }

   }

   public BinaryConstantPool getConstants() {
      return this.cpool;
   }

   public byte[] getAttribute(Identifier var1) {
      for(BinaryAttribute var2 = this.atts; var2 != null; var2 = var2.next) {
         if (var2.name.equals(var1)) {
            return var2.data;
         }
      }

      return null;
   }
}
