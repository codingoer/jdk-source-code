package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.InternalException;
import com.sun.jdi.Location;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.List;

public class NonConcreteMethodImpl extends MethodImpl {
   private Location location = null;

   NonConcreteMethodImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, String var5, String var6, String var7, int var8) {
      super(var1, var2, var3, var5, var6, var7, var8);
   }

   public Location location() {
      if (this.isAbstract()) {
         return null;
      } else {
         if (this.location == null) {
            this.location = new LocationImpl(this.vm, this, -1L);
         }

         return this.location;
      }
   }

   public List allLineLocations(String var1, String var2) {
      return new ArrayList(0);
   }

   public List allLineLocations(SDE.Stratum var1, String var2) {
      return new ArrayList(0);
   }

   public List locationsOfLine(String var1, String var2, int var3) {
      return new ArrayList(0);
   }

   public List locationsOfLine(SDE.Stratum var1, String var2, int var3) {
      return new ArrayList(0);
   }

   public Location locationOfCodeIndex(long var1) {
      return null;
   }

   public List variables() throws AbsentInformationException {
      throw new AbsentInformationException();
   }

   public List variablesByName(String var1) throws AbsentInformationException {
      throw new AbsentInformationException();
   }

   public List arguments() throws AbsentInformationException {
      throw new AbsentInformationException();
   }

   public byte[] bytecodes() {
      return new byte[0];
   }

   int argSlotCount() throws AbsentInformationException {
      throw new InternalException("should not get here");
   }
}
