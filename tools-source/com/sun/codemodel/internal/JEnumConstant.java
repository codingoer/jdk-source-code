package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class JEnumConstant extends JExpressionImpl implements JDeclaration, JAnnotatable, JDocCommentable {
   private final String name;
   private final JDefinedClass type;
   private JDocComment jdoc = null;
   private List annotations = null;
   private List args = null;

   JEnumConstant(JDefinedClass type, String name) {
      this.name = name;
      this.type = type;
   }

   public JEnumConstant arg(JExpression arg) {
      if (arg == null) {
         throw new IllegalArgumentException();
      } else {
         if (this.args == null) {
            this.args = new ArrayList();
         }

         this.args.add(arg);
         return this;
      }
   }

   public String getName() {
      return this.type.fullName().concat(".").concat(this.name);
   }

   public JDocComment javadoc() {
      if (this.jdoc == null) {
         this.jdoc = new JDocComment(this.type.owner());
      }

      return this.jdoc;
   }

   public JAnnotationUse annotate(JClass clazz) {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      JAnnotationUse a = new JAnnotationUse(clazz);
      this.annotations.add(a);
      return a;
   }

   public JAnnotationUse annotate(Class clazz) {
      return this.annotate(this.type.owner().ref(clazz));
   }

   public JAnnotationWriter annotate2(Class clazz) {
      return TypedAnnotationWriter.create(clazz, this);
   }

   public Collection annotations() {
      if (this.annotations == null) {
         this.annotations = new ArrayList();
      }

      return Collections.unmodifiableList(this.annotations);
   }

   public void declare(JFormatter f) {
      if (this.jdoc != null) {
         f.nl().g((JGenerable)this.jdoc);
      }

      if (this.annotations != null) {
         for(int i = 0; i < this.annotations.size(); ++i) {
            f.g((JGenerable)this.annotations.get(i)).nl();
         }
      }

      f.id(this.name);
      if (this.args != null) {
         f.p('(').g((Collection)this.args).p(')');
      }

   }

   public void generate(JFormatter f) {
      f.t((JClass)this.type).p('.').p(this.name);
   }
}
