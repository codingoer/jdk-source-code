package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class JNarrowedClass extends JClass {
   final JClass basis;
   private final List args;

   JNarrowedClass(JClass basis, JClass arg) {
      this(basis, Collections.singletonList(arg));
   }

   JNarrowedClass(JClass basis, List args) {
      super(basis.owner());
      this.basis = basis;

      assert !(basis instanceof JNarrowedClass);

      this.args = args;
   }

   public JClass narrow(JClass clazz) {
      List newArgs = new ArrayList(this.args);
      newArgs.add(clazz);
      return new JNarrowedClass(this.basis, newArgs);
   }

   public JClass narrow(JClass... clazz) {
      List newArgs = new ArrayList(this.args);
      newArgs.addAll(Arrays.asList(clazz));
      return new JNarrowedClass(this.basis, newArgs);
   }

   public String name() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.basis.name());
      buf.append('<');
      boolean first = true;

      JClass c;
      for(Iterator var3 = this.args.iterator(); var3.hasNext(); buf.append(c.name())) {
         c = (JClass)var3.next();
         if (first) {
            first = false;
         } else {
            buf.append(',');
         }
      }

      buf.append('>');
      return buf.toString();
   }

   public String fullName() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.basis.fullName());
      buf.append('<');
      boolean first = true;

      JClass c;
      for(Iterator var3 = this.args.iterator(); var3.hasNext(); buf.append(c.fullName())) {
         c = (JClass)var3.next();
         if (first) {
            first = false;
         } else {
            buf.append(',');
         }
      }

      buf.append('>');
      return buf.toString();
   }

   public String binaryName() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.basis.binaryName());
      buf.append('<');
      boolean first = true;

      JClass c;
      for(Iterator var3 = this.args.iterator(); var3.hasNext(); buf.append(c.binaryName())) {
         c = (JClass)var3.next();
         if (first) {
            first = false;
         } else {
            buf.append(',');
         }
      }

      buf.append('>');
      return buf.toString();
   }

   public void generate(JFormatter f) {
      f.t(this.basis).p('<').g((Collection)this.args).p('\uffff');
   }

   void printLink(JFormatter f) {
      this.basis.printLink(f);
      f.p("{@code <}");
      boolean first = true;

      JClass c;
      for(Iterator var3 = this.args.iterator(); var3.hasNext(); c.printLink(f)) {
         c = (JClass)var3.next();
         if (first) {
            first = false;
         } else {
            f.p(',');
         }
      }

      f.p("{@code >}");
   }

   public JPackage _package() {
      return this.basis._package();
   }

   public JClass _extends() {
      JClass base = this.basis._extends();
      return base == null ? base : base.substituteParams(this.basis.typeParams(), this.args);
   }

   public Iterator _implements() {
      return new Iterator() {
         private final Iterator core;

         {
            this.core = JNarrowedClass.this.basis._implements();
         }

         public void remove() {
            this.core.remove();
         }

         public JClass next() {
            return ((JClass)this.core.next()).substituteParams(JNarrowedClass.this.basis.typeParams(), JNarrowedClass.this.args);
         }

         public boolean hasNext() {
            return this.core.hasNext();
         }
      };
   }

   public JClass erasure() {
      return this.basis;
   }

   public boolean isInterface() {
      return this.basis.isInterface();
   }

   public boolean isAbstract() {
      return this.basis.isAbstract();
   }

   public boolean isArray() {
      return false;
   }

   public boolean equals(Object obj) {
      return !(obj instanceof JNarrowedClass) ? false : this.fullName().equals(((JClass)obj).fullName());
   }

   public int hashCode() {
      return this.fullName().hashCode();
   }

   protected JClass substituteParams(JTypeVar[] variables, List bindings) {
      JClass b = this.basis.substituteParams(variables, bindings);
      boolean different = b != this.basis;
      List clazz = new ArrayList(this.args.size());

      for(int i = 0; i < clazz.size(); ++i) {
         JClass c = ((JClass)this.args.get(i)).substituteParams(variables, bindings);
         clazz.set(i, c);
         different |= c != this.args.get(i);
      }

      if (different) {
         return new JNarrowedClass(b, clazz);
      } else {
         return this;
      }
   }

   public List getTypeParameters() {
      return this.args;
   }
}
