package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSType;

public abstract class Ref {
   public interface IdentityConstraint {
      XSIdentityConstraint get();
   }

   public interface Element extends Term {
      XSElementDecl get();
   }

   public interface AttGroup {
      XSAttGroupDecl get();
   }

   public interface Attribute {
      XSAttributeDecl getAttribute();
   }

   public interface ComplexType extends Type {
      XSComplexType getType();
   }

   public interface SimpleType extends Type {
      XSSimpleType getType();
   }

   public interface ContentType {
      XSContentType getContentType();
   }

   public interface Type {
      XSType getType();
   }

   public interface Term {
      XSTerm getTerm();
   }
}
