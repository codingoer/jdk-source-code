package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ConstEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.ModuleEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class ConstGen implements com.sun.tools.corba.se.idl.ConstGen {
   protected Hashtable symbolTable = null;
   protected ConstEntry c = null;
   protected PrintWriter stream = null;

   public void generate(Hashtable var1, ConstEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.c = var2;
      this.stream = var3;
      this.init();
      if (var2.container() instanceof ModuleEntry) {
         this.generateConst();
      } else if (this.stream != null) {
         this.writeConstExpr();
      }

   }

   protected void init() {
   }

   protected void generateConst() {
      this.openStream();
      if (this.stream != null) {
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void openStream() {
      this.stream = Util.stream(this.c, ".java");
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.c);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      this.stream.println("public interface " + this.c.name());
      this.stream.println("{");
   }

   protected void writeBody() {
      this.writeConstExpr();
   }

   protected void writeConstExpr() {
      if (this.c.comment() != null) {
         this.c.comment().generate("  ", this.stream);
      }

      if (this.c.container() instanceof ModuleEntry) {
         this.stream.print("  public static final " + Util.javaName(this.c.type()) + " value = ");
      } else {
         this.stream.print("  public static final " + Util.javaName(this.c.type()) + ' ' + this.c.name() + " = ");
      }

      this.writeConstValue(this.c.type());
   }

   private void writeConstValue(SymtabEntry var1) {
      if (var1 instanceof PrimitiveEntry) {
         this.stream.println('(' + Util.javaName(var1) + ")(" + Util.parseExpression(this.c.value()) + ");");
      } else if (var1 instanceof StringEntry) {
         this.stream.println(Util.parseExpression(this.c.value()) + ';');
      } else if (!(var1 instanceof TypedefEntry)) {
         this.stream.println(Util.parseExpression(this.c.value()) + ';');
      } else {
         while(var1 instanceof TypedefEntry) {
            var1 = var1.type();
         }

         this.writeConstValue(var1);
      }

   }

   protected void writeClosing() {
      this.stream.println("}");
   }

   protected void closeStream() {
      this.stream.close();
   }
}
