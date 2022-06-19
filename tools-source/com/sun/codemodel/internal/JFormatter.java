package com.sun.codemodel.internal;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class JFormatter {
   private HashMap collectedReferences;
   private HashSet importedClasses;
   private Mode mode;
   private int indentLevel;
   private final String indentSpace;
   private final PrintWriter pw;
   private char lastChar;
   private boolean atBeginningOfLine;
   private JPackage javaLang;
   static final char CLOSE_TYPE_ARGS = '\uffff';

   public JFormatter(PrintWriter s, String space) {
      this.mode = JFormatter.Mode.PRINTING;
      this.lastChar = 0;
      this.atBeginningOfLine = true;
      this.pw = s;
      this.indentSpace = space;
      this.collectedReferences = new HashMap();
      this.importedClasses = new HashSet();
   }

   public JFormatter(PrintWriter s) {
      this(s, "    ");
   }

   public JFormatter(Writer w) {
      this(new PrintWriter(w));
   }

   public void close() {
      this.pw.close();
   }

   public boolean isPrinting() {
      return this.mode == JFormatter.Mode.PRINTING;
   }

   public JFormatter o() {
      --this.indentLevel;
      return this;
   }

   public JFormatter i() {
      ++this.indentLevel;
      return this;
   }

   private boolean needSpace(char c1, char c2) {
      if (c1 == ']' && c2 == '{') {
         return true;
      } else if (c1 == ';') {
         return true;
      } else if (c1 == '\uffff') {
         return c2 != '(';
      } else if (c1 == ')' && c2 == '{') {
         return true;
      } else if (c1 != ',' && c1 != '=') {
         if (c2 == '=') {
            return true;
         } else if (Character.isDigit(c1)) {
            return c2 != '(' && c2 != ')' && c2 != ';' && c2 != ',';
         } else if (Character.isJavaIdentifierPart(c1)) {
            switch (c2) {
               case '+':
               case '>':
               case '@':
               case '{':
               case '}':
                  return true;
               default:
                  return Character.isJavaIdentifierStart(c2);
            }
         } else if (Character.isJavaIdentifierStart(c2)) {
            switch (c1) {
               case ')':
               case '+':
               case ']':
               case '}':
                  return true;
               default:
                  return false;
            }
         } else if (Character.isDigit(c2)) {
            return c1 != '(';
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private void spaceIfNeeded(char c) {
      if (this.atBeginningOfLine) {
         for(int i = 0; i < this.indentLevel; ++i) {
            this.pw.print(this.indentSpace);
         }

         this.atBeginningOfLine = false;
      } else if (this.lastChar != 0 && this.needSpace(this.lastChar, c)) {
         this.pw.print(' ');
      }

   }

   public JFormatter p(char c) {
      if (this.mode == JFormatter.Mode.PRINTING) {
         if (c == '\uffff') {
            this.pw.print('>');
         } else {
            this.spaceIfNeeded(c);
            this.pw.print(c);
         }

         this.lastChar = c;
      }

      return this;
   }

   public JFormatter p(String s) {
      if (this.mode == JFormatter.Mode.PRINTING) {
         this.spaceIfNeeded(s.charAt(0));
         this.pw.print(s);
         this.lastChar = s.charAt(s.length() - 1);
      }

      return this;
   }

   public JFormatter t(JType type) {
      return type.isReference() ? this.t((JClass)type) : this.g((JGenerable)type);
   }

   public JFormatter t(JClass type) {
      switch (this.mode) {
         case PRINTING:
            if (this.importedClasses.contains(type)) {
               this.p(type.name());
            } else if (type.outer() != null) {
               this.t(type.outer()).p('.').p(type.name());
            } else {
               this.p(type.fullName());
            }
            break;
         case COLLECTING:
            String shortName = type.name();
            if (this.collectedReferences.containsKey(shortName)) {
               ((ReferenceList)this.collectedReferences.get(shortName)).add(type);
            } else {
               ReferenceList tl = new ReferenceList();
               tl.add(type);
               this.collectedReferences.put(shortName, tl);
            }
      }

      return this;
   }

   public JFormatter id(String id) {
      switch (this.mode) {
         case PRINTING:
            this.p(id);
            break;
         case COLLECTING:
            if (this.collectedReferences.containsKey(id)) {
               if (!((ReferenceList)this.collectedReferences.get(id)).getClasses().isEmpty()) {
                  Iterator var2 = ((ReferenceList)this.collectedReferences.get(id)).getClasses().iterator();

                  while(var2.hasNext()) {
                     JClass type = (JClass)var2.next();
                     if (type.outer() != null) {
                        ((ReferenceList)this.collectedReferences.get(id)).setId(false);
                        return this;
                     }
                  }
               }

               ((ReferenceList)this.collectedReferences.get(id)).setId(true);
            } else {
               ReferenceList tl = new ReferenceList();
               tl.setId(true);
               this.collectedReferences.put(id, tl);
            }
      }

      return this;
   }

   public JFormatter nl() {
      if (this.mode == JFormatter.Mode.PRINTING) {
         this.pw.println();
         this.lastChar = 0;
         this.atBeginningOfLine = true;
      }

      return this;
   }

   public JFormatter g(JGenerable g) {
      g.generate(this);
      return this;
   }

   public JFormatter g(Collection list) {
      boolean first = true;
      if (!list.isEmpty()) {
         for(Iterator var3 = list.iterator(); var3.hasNext(); first = false) {
            JGenerable item = (JGenerable)var3.next();
            if (!first) {
               this.p(',');
            }

            this.g(item);
         }
      }

      return this;
   }

   public JFormatter d(JDeclaration d) {
      d.declare(this);
      return this;
   }

   public JFormatter s(JStatement s) {
      s.state(this);
      return this;
   }

   public JFormatter b(JVar v) {
      v.bind(this);
      return this;
   }

   void write(JDefinedClass c) {
      this.mode = JFormatter.Mode.COLLECTING;
      this.d(c);
      this.javaLang = c.owner()._package("java.lang");
      Iterator var2 = this.collectedReferences.values().iterator();

      while(var2.hasNext()) {
         ReferenceList tl = (ReferenceList)var2.next();
         if (!tl.collisions(c) && !tl.isId()) {
            assert tl.getClasses().size() == 1;

            this.importedClasses.add(tl.getClasses().get(0));
         }
      }

      this.importedClasses.add(c);
      this.mode = JFormatter.Mode.PRINTING;

      assert c.parentContainer().isPackage() : "this method is only for a pacakge-level class";

      JPackage pkg = (JPackage)c.parentContainer();
      if (!pkg.isUnnamed()) {
         this.nl().d(pkg);
         this.nl();
      }

      JClass[] imports = (JClass[])this.importedClasses.toArray(new JClass[this.importedClasses.size()]);
      Arrays.sort(imports);
      JClass[] var4 = imports;
      int var5 = imports.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         JClass clazz = var4[var6];
         if (!this.supressImport(clazz, c)) {
            if (clazz instanceof JNarrowedClass) {
               clazz = clazz.erasure();
            }

            this.p("import").p(clazz.fullName()).p(';').nl();
         }
      }

      this.nl();
      this.d(c);
   }

   private boolean supressImport(JClass clazz, JClass c) {
      if (clazz instanceof JNarrowedClass) {
         clazz = clazz.erasure();
      }

      if (clazz instanceof JAnonymousClass) {
         clazz = clazz._extends();
      }

      if (clazz._package().isUnnamed()) {
         return true;
      } else {
         String packageName = clazz._package().name();
         if (packageName.equals("java.lang")) {
            return true;
         } else {
            return clazz._package() == c._package() && clazz.outer() == null;
         }
      }
   }

   final class ReferenceList {
      private final ArrayList classes = new ArrayList();
      private boolean id;

      public boolean collisions(JDefinedClass enclosingClass) {
         if (this.classes.size() > 1) {
            return true;
         } else if (this.id && this.classes.size() != 0) {
            return true;
         } else {
            Iterator var2 = this.classes.iterator();

            JClass c;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               c = (JClass)var2.next();
               if (c instanceof JAnonymousClass) {
                  c = c._extends();
               }

               if (c._package() == JFormatter.this.javaLang) {
                  Iterator itr = enclosingClass._package().classes();

                  while(itr.hasNext()) {
                     JDefinedClass n = (JDefinedClass)itr.next();
                     if (n.name().equals(c.name())) {
                        return true;
                     }
                  }
               }
            } while(c.outer() == null);

            return true;
         }
      }

      public void add(JClass clazz) {
         if (!this.classes.contains(clazz)) {
            this.classes.add(clazz);
         }

      }

      public List getClasses() {
         return this.classes;
      }

      public void setId(boolean value) {
         this.id = value;
      }

      public boolean isId() {
         return this.id && this.classes.size() == 0;
      }
   }

   private static enum Mode {
      COLLECTING,
      PRINTING;
   }
}
