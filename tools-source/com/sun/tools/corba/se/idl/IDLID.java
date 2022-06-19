package com.sun.tools.corba.se.idl;

public class IDLID extends RepositoryID {
   private String _prefix;
   private String _name;
   private String _version;

   public IDLID() {
      this._prefix = "";
      this._name = "";
      this._version = "1.0";
   }

   public IDLID(String var1, String var2, String var3) {
      this._prefix = var1;
      this._name = var2;
      this._version = var3;
   }

   public String ID() {
      return this._prefix.equals("") ? "IDL:" + this._name + ':' + this._version : "IDL:" + this._prefix + '/' + this._name + ':' + this._version;
   }

   public String prefix() {
      return this._prefix;
   }

   void prefix(String var1) {
      if (var1 == null) {
         this._prefix = "";
      } else {
         this._prefix = var1;
      }

   }

   public String name() {
      return this._name;
   }

   void name(String var1) {
      if (var1 == null) {
         this._name = "";
      } else {
         this._name = var1;
      }

   }

   public String version() {
      return this._version;
   }

   void version(String var1) {
      if (var1 == null) {
         this._version = "";
      } else {
         this._version = var1;
      }

   }

   void appendToName(String var1) {
      if (var1 != null) {
         if (this._name.equals("")) {
            this._name = var1;
         } else {
            this._name = this._name + '/' + var1;
         }
      }

   }

   void replaceName(String var1) {
      if (var1 == null) {
         this._name = "";
      } else {
         int var2 = this._name.lastIndexOf(47);
         if (var2 < 0) {
            this._name = var1;
         } else {
            this._name = this._name.substring(0, var2 + 1) + var1;
         }
      }

   }

   public Object clone() {
      return new IDLID(this._prefix, this._name, this._version);
   }
}
