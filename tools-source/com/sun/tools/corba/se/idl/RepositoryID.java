package com.sun.tools.corba.se.idl;

public class RepositoryID {
   private String _id;

   public RepositoryID() {
      this._id = "";
   }

   public RepositoryID(String var1) {
      this._id = var1;
   }

   public String ID() {
      return this._id;
   }

   public Object clone() {
      return new RepositoryID(this._id);
   }

   public String toString() {
      return this.ID();
   }

   public static boolean hasValidForm(String var0) {
      return var0 != null && var0.indexOf(58) > 0;
   }
}
