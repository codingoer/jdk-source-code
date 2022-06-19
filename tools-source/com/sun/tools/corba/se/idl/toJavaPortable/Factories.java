package com.sun.tools.corba.se.idl.toJavaPortable;

public class Factories extends com.sun.tools.corba.se.idl.Factories {
   static String[] keywords = new String[]{"abstract", "break", "byte", "catch", "class", "continue", "do", "else", "extends", "false", "final", "finally", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "native", "new", "null", "operator", "outer", "package", "private", "protected", "public", "return", "static", "super", "synchronized", "this", "throw", "throws", "transient", "true", "try", "volatile", "while", "+Helper", "+Holder", "+Package", "clone", "equals", "finalize", "getClass", "hashCode", "notify", "notifyAll", "toString", "wait"};
   private Helper _helper = null;
   private ValueFactory _valueFactory = null;
   private DefaultFactory _defaultFactory = null;
   private Holder _holder = new Holder();
   private Skeleton _skeleton = new Skeleton();
   private Stub _stub = new Stub();

   public com.sun.tools.corba.se.idl.GenFactory genFactory() {
      return new GenFactory();
   }

   public com.sun.tools.corba.se.idl.Arguments arguments() {
      return new Arguments();
   }

   public String[] languageKeywords() {
      return keywords;
   }

   public Helper helper() {
      if (this._helper == null) {
         if (Util.corbaLevel(2.4F, 99.0F)) {
            this._helper = new Helper24();
         } else {
            this._helper = new Helper();
         }
      }

      return this._helper;
   }

   public ValueFactory valueFactory() {
      if (this._valueFactory == null && Util.corbaLevel(2.4F, 99.0F)) {
         this._valueFactory = new ValueFactory();
      }

      return this._valueFactory;
   }

   public DefaultFactory defaultFactory() {
      if (this._defaultFactory == null && Util.corbaLevel(2.4F, 99.0F)) {
         this._defaultFactory = new DefaultFactory();
      }

      return this._defaultFactory;
   }

   public Holder holder() {
      return this._holder;
   }

   public Skeleton skeleton() {
      return this._skeleton;
   }

   public Stub stub() {
      return this._stub;
   }
}
