package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassContainer;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlAccessorTypeWriter;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(Boolean.class)
public enum ImplStructureStrategy {
   @XmlEnumValue("true")
   BEAN_ONLY {
      protected Result createClasses(Outline outline, CClassInfo bean) {
         JClassContainer parent = outline.getContainer(bean.parent(), Aspect.EXPOSED);
         JDefinedClass impl = outline.getClassFactory().createClass(parent, 1 | (parent.isPackage() ? 0 : 16) | (bean.isAbstract() ? 32 : 0), bean.shortName, bean.getLocator());
         ((XmlAccessorTypeWriter)impl.annotate2(XmlAccessorTypeWriter.class)).value(XmlAccessType.FIELD);
         return new Result(impl, impl);
      }

      protected JPackage getPackage(JPackage pkg, Aspect a) {
         return pkg;
      }

      protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
         assert target.ref == target.implClass;

         return new MethodWriter(target) {
            private final JDefinedClass impl;
            private JMethod implMethod;

            {
               this.impl = target.implClass;
            }

            public JVar addParameter(JType type, String name) {
               return this.implMethod.param(type, name);
            }

            public JMethod declareMethod(JType returnType, String methodName) {
               this.implMethod = this.impl.method(1, (JType)returnType, methodName);
               return this.implMethod;
            }

            public JDocComment javadoc() {
               return this.implMethod.javadoc();
            }
         };
      }

      protected void _extends(ClassOutlineImpl derived, ClassOutlineImpl base) {
         derived.implClass._extends(base.implRef);
      }
   },
   @XmlEnumValue("false")
   INTF_AND_IMPL {
      protected Result createClasses(Outline outline, CClassInfo bean) {
         JClassContainer parent = outline.getContainer(bean.parent(), Aspect.EXPOSED);
         JDefinedClass intf = outline.getClassFactory().createInterface(parent, bean.shortName, bean.getLocator());
         parent = outline.getContainer(bean.parent(), Aspect.IMPLEMENTATION);
         JDefinedClass impl = outline.getClassFactory().createClass(parent, 1 | (parent.isPackage() ? 0 : 16) | (bean.isAbstract() ? 32 : 0), bean.shortName + "Impl", bean.getLocator());
         ((XmlAccessorTypeWriter)impl.annotate2(XmlAccessorTypeWriter.class)).value(XmlAccessType.FIELD);
         impl._implements((JClass)intf);
         return new Result(intf, impl);
      }

      protected JPackage getPackage(JPackage pkg, Aspect a) {
         switch (a) {
            case EXPOSED:
               return pkg;
            case IMPLEMENTATION:
               return pkg.subPackage("impl");
            default:
               assert false;

               throw new IllegalStateException();
         }
      }

      protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
         return new MethodWriter(target) {
            private final JDefinedClass intf;
            private final JDefinedClass impl;
            private JMethod intfMethod;
            private JMethod implMethod;

            {
               this.intf = target.ref;
               this.impl = target.implClass;
            }

            public JVar addParameter(JType type, String name) {
               if (this.intf != null) {
                  this.intfMethod.param(type, name);
               }

               return this.implMethod.param(type, name);
            }

            public JMethod declareMethod(JType returnType, String methodName) {
               if (this.intf != null) {
                  this.intfMethod = this.intf.method(0, (JType)returnType, methodName);
               }

               this.implMethod = this.impl.method(1, (JType)returnType, methodName);
               return this.implMethod;
            }

            public JDocComment javadoc() {
               return this.intf != null ? this.intfMethod.javadoc() : this.implMethod.javadoc();
            }
         };
      }

      protected void _extends(ClassOutlineImpl derived, ClassOutlineImpl base) {
         derived.implClass._extends(base.implRef);
         derived.ref._implements((JClass)base.ref);
      }
   };

   private ImplStructureStrategy() {
   }

   protected abstract Result createClasses(Outline var1, CClassInfo var2);

   protected abstract JPackage getPackage(JPackage var1, Aspect var2);

   protected abstract MethodWriter createMethodWriter(ClassOutlineImpl var1);

   protected abstract void _extends(ClassOutlineImpl var1, ClassOutlineImpl var2);

   // $FF: synthetic method
   ImplStructureStrategy(Object x2) {
      this();
   }

   public static final class Result {
      public final JDefinedClass exposed;
      public final JDefinedClass implementation;

      public Result(JDefinedClass exposed, JDefinedClass implementation) {
         this.exposed = exposed;
         this.implementation = implementation;
      }
   }
}
