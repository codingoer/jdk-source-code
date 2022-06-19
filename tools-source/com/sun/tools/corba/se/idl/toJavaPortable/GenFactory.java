package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ForwardGen;
import com.sun.tools.corba.se.idl.IncludeGen;
import com.sun.tools.corba.se.idl.ParameterGen;
import com.sun.tools.corba.se.idl.PragmaGen;

public class GenFactory implements com.sun.tools.corba.se.idl.GenFactory {
   public com.sun.tools.corba.se.idl.AttributeGen createAttributeGen() {
      return (com.sun.tools.corba.se.idl.AttributeGen)(Util.corbaLevel(2.4F, 99.0F) ? new AttributeGen24() : new AttributeGen());
   }

   public com.sun.tools.corba.se.idl.ConstGen createConstGen() {
      return new ConstGen();
   }

   public com.sun.tools.corba.se.idl.NativeGen createNativeGen() {
      return new NativeGen();
   }

   public com.sun.tools.corba.se.idl.EnumGen createEnumGen() {
      return new EnumGen();
   }

   public com.sun.tools.corba.se.idl.ExceptionGen createExceptionGen() {
      return new ExceptionGen();
   }

   public ForwardGen createForwardGen() {
      return null;
   }

   public com.sun.tools.corba.se.idl.ForwardValueGen createForwardValueGen() {
      return null;
   }

   public IncludeGen createIncludeGen() {
      return null;
   }

   public com.sun.tools.corba.se.idl.InterfaceGen createInterfaceGen() {
      return new InterfaceGen();
   }

   public com.sun.tools.corba.se.idl.ValueGen createValueGen() {
      return (com.sun.tools.corba.se.idl.ValueGen)(Util.corbaLevel(2.4F, 99.0F) ? new ValueGen24() : new ValueGen());
   }

   public com.sun.tools.corba.se.idl.ValueBoxGen createValueBoxGen() {
      return (com.sun.tools.corba.se.idl.ValueBoxGen)(Util.corbaLevel(2.4F, 99.0F) ? new ValueBoxGen24() : new ValueBoxGen());
   }

   public com.sun.tools.corba.se.idl.MethodGen createMethodGen() {
      return (com.sun.tools.corba.se.idl.MethodGen)(Util.corbaLevel(2.4F, 99.0F) ? new MethodGen24() : new MethodGen());
   }

   public com.sun.tools.corba.se.idl.ModuleGen createModuleGen() {
      return new ModuleGen();
   }

   public ParameterGen createParameterGen() {
      return null;
   }

   public PragmaGen createPragmaGen() {
      return null;
   }

   public com.sun.tools.corba.se.idl.PrimitiveGen createPrimitiveGen() {
      return new PrimitiveGen();
   }

   public com.sun.tools.corba.se.idl.SequenceGen createSequenceGen() {
      return new SequenceGen();
   }

   public com.sun.tools.corba.se.idl.StringGen createStringGen() {
      return new StringGen();
   }

   public com.sun.tools.corba.se.idl.StructGen createStructGen() {
      return new StructGen();
   }

   public com.sun.tools.corba.se.idl.TypedefGen createTypedefGen() {
      return new TypedefGen();
   }

   public com.sun.tools.corba.se.idl.UnionGen createUnionGen() {
      return new UnionGen();
   }
}
