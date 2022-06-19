package com.sun.tools.internal.xjc.writer;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassContainer;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SignatureWriter {
   private final Collection classes;
   private final Map classSet = new HashMap();
   private final Writer out;
   private int indent = 0;

   public static void write(Outline model, Writer out) throws IOException {
      (new SignatureWriter(model, out)).dump();
   }

   private SignatureWriter(Outline model, Writer out) {
      this.out = out;
      this.classes = model.getClasses();
      Iterator var3 = this.classes.iterator();

      while(var3.hasNext()) {
         ClassOutline ci = (ClassOutline)var3.next();
         this.classSet.put(ci.ref, ci);
      }

   }

   private void printIndent() throws IOException {
      for(int i = 0; i < this.indent; ++i) {
         this.out.write("  ");
      }

   }

   private void println(String s) throws IOException {
      this.printIndent();
      this.out.write(s);
      this.out.write(10);
   }

   private void dump() throws IOException {
      Set packages = new TreeSet(new Comparator() {
         public int compare(JPackage lhs, JPackage rhs) {
            return lhs.name().compareTo(rhs.name());
         }
      });
      Iterator var2 = this.classes.iterator();

      while(var2.hasNext()) {
         ClassOutline ci = (ClassOutline)var2.next();
         packages.add(ci._package()._package());
      }

      var2 = packages.iterator();

      while(var2.hasNext()) {
         JPackage pkg = (JPackage)var2.next();
         this.dump(pkg);
      }

      this.out.flush();
   }

   private void dump(JPackage pkg) throws IOException {
      this.println("package " + pkg.name() + " {");
      ++this.indent;
      this.dumpChildren(pkg);
      --this.indent;
      this.println("}");
   }

   private void dumpChildren(JClassContainer cont) throws IOException {
      Iterator itr = cont.classes();

      while(itr.hasNext()) {
         JDefinedClass cls = (JDefinedClass)itr.next();
         ClassOutline ci = (ClassOutline)this.classSet.get(cls);
         if (ci != null) {
            this.dump(ci);
         }
      }

   }

   private void dump(ClassOutline ci) throws IOException {
      JDefinedClass cls = ci.implClass;
      StringBuilder buf = new StringBuilder();
      buf.append("interface ");
      buf.append(cls.name());
      boolean first = true;

      for(Iterator itr = cls._implements(); itr.hasNext(); buf.append(this.printName((JClass)itr.next()))) {
         if (first) {
            buf.append(" extends ");
            first = false;
         } else {
            buf.append(", ");
         }
      }

      buf.append(" {");
      this.println(buf.toString());
      ++this.indent;
      FieldOutline[] var6 = ci.getDeclaredFields();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         FieldOutline fo = var6[var8];
         String type = this.printName(fo.getRawType());
         this.println(type + ' ' + fo.getPropertyInfo().getName(true) + ';');
      }

      this.dumpChildren(cls);
      --this.indent;
      this.println("}");
   }

   private String printName(JType t) {
      String name = t.fullName();
      if (name.startsWith("java.lang.")) {
         name = name.substring(10);
      }

      return name;
   }
}
