package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionEntry;
import java.util.Enumeration;
import java.util.Hashtable;

public class TCOffsets {
   private Hashtable tcs = new Hashtable();
   private int offset = 0;

   public int offset(String var1) {
      Integer var2 = (Integer)this.tcs.get(var1);
      return var2 == null ? -1 : var2;
   }

   public void set(SymtabEntry var1) {
      if (var1 == null) {
         this.offset += 8;
      } else {
         this.tcs.put(var1.fullName(), new Integer(this.offset));
         this.offset += 4;
         String var2 = Util.stripLeadingUnderscoresFromID(var1.repositoryID().ID());
         if (var1 instanceof InterfaceEntry) {
            this.offset += this.alignStrLen(var2) + this.alignStrLen(var1.name());
         } else if (var1 instanceof StructEntry) {
            this.offset += this.alignStrLen(var2) + this.alignStrLen(var1.name()) + 4;
         } else if (var1 instanceof UnionEntry) {
            this.offset += this.alignStrLen(var2) + this.alignStrLen(var1.name()) + 12;
         } else if (var1 instanceof EnumEntry) {
            this.offset += this.alignStrLen(var2) + this.alignStrLen(var1.name()) + 4;

            for(Enumeration var3 = ((EnumEntry)var1).elements().elements(); var3.hasMoreElements(); this.offset += this.alignStrLen((String)var3.nextElement())) {
            }
         } else if (var1 instanceof StringEntry) {
            this.offset += 4;
         } else if (var1 instanceof TypedefEntry) {
            this.offset += this.alignStrLen(var2) + this.alignStrLen(var1.name());
            if (((TypedefEntry)var1).arrayInfo().size() != 0) {
               this.offset += 8;
            }
         }
      }

   }

   public int alignStrLen(String var1) {
      int var2 = var1.length() + 1;
      int var3 = 4 - var2 % 4;
      if (var3 == 4) {
         var3 = 0;
      }

      return var2 + var3 + 4;
   }

   public void setMember(SymtabEntry var1) {
      this.offset += this.alignStrLen(var1.name());
      if (((TypedefEntry)var1).arrayInfo().size() != 0) {
         this.offset += 4;
      }

   }

   public int currentOffset() {
      return this.offset;
   }

   public void bumpCurrentOffset(int var1) {
      this.offset += var1;
   }
}
