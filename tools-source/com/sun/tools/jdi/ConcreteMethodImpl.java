package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.VirtualMachine;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConcreteMethodImpl extends MethodImpl {
   private Location location = null;
   private SoftReference softBaseLocationXRefsRef;
   private SoftReference softOtherLocationXRefsRef;
   private SoftReference variablesRef = null;
   private boolean absentVariableInformation = false;
   private long firstIndex = -1L;
   private long lastIndex = -1L;
   private SoftReference bytecodesRef = null;
   private int argSlotCount = -1;

   ConcreteMethodImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, String var5, String var6, String var7, int var8) {
      super(var1, var2, var3, var5, var6, var7, var8);
   }

   public Location location() {
      if (this.location == null) {
         this.getBaseLocations();
      }

      return this.location;
   }

   List sourceNameFilter(List var1, SDE.Stratum var2, String var3) throws AbsentInformationException {
      if (var3 == null) {
         return var1;
      } else {
         ArrayList var4 = new ArrayList();
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            Location var6 = (Location)var5.next();
            if (((LocationImpl)var6).sourceName(var2).equals(var3)) {
               var4.add(var6);
            }
         }

         return var4;
      }
   }

   List allLineLocations(SDE.Stratum var1, String var2) throws AbsentInformationException {
      List var3 = this.getLocations(var1).lineLocations;
      if (var3.size() == 0) {
         throw new AbsentInformationException();
      } else {
         return Collections.unmodifiableList(this.sourceNameFilter(var3, var1, var2));
      }
   }

   List locationsOfLine(SDE.Stratum var1, String var2, int var3) throws AbsentInformationException {
      SoftLocationXRefs var4 = this.getLocations(var1);
      if (var4.lineLocations.size() == 0) {
         throw new AbsentInformationException();
      } else {
         Object var5 = (List)var4.lineMapper.get(new Integer(var3));
         if (var5 == null) {
            var5 = new ArrayList(0);
         }

         return Collections.unmodifiableList(this.sourceNameFilter((List)var5, var1, var2));
      }
   }

   public Location locationOfCodeIndex(long var1) {
      if (this.firstIndex == -1L) {
         this.getBaseLocations();
      }

      return var1 >= this.firstIndex && var1 <= this.lastIndex ? new LocationImpl(this.virtualMachine(), this, var1) : null;
   }

   LineInfo codeIndexToLineInfo(SDE.Stratum var1, long var2) {
      if (this.firstIndex == -1L) {
         this.getBaseLocations();
      }

      if (var2 >= this.firstIndex && var2 <= this.lastIndex) {
         List var4 = this.getLocations(var1).lineLocations;
         if (var4.size() == 0) {
            return super.codeIndexToLineInfo(var1, var2);
         } else {
            Iterator var5 = var4.iterator();

            LocationImpl var6;
            LocationImpl var7;
            for(var6 = (LocationImpl)var5.next(); var5.hasNext(); var6 = var7) {
               var7 = (LocationImpl)var5.next();
               if (var7.codeIndex() > var2) {
                  break;
               }
            }

            return var6.getLineInfo(var1);
         }
      } else {
         throw new InternalError("Location with invalid code index");
      }
   }

   public List variables() throws AbsentInformationException {
      return this.getVariables();
   }

   public List variablesByName(String var1) throws AbsentInformationException {
      List var2 = this.getVariables();
      ArrayList var3 = new ArrayList(2);
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         LocalVariable var5 = (LocalVariable)var4.next();
         if (var5.name().equals(var1)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public List arguments() throws AbsentInformationException {
      List var1 = this.getVariables();
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         LocalVariable var4 = (LocalVariable)var3.next();
         if (var4.isArgument()) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public byte[] bytecodes() {
      byte[] var1 = this.bytecodesRef == null ? null : (byte[])this.bytecodesRef.get();
      if (var1 == null) {
         try {
            var1 = JDWP.Method.Bytecodes.process(this.vm, this.declaringType, this.ref).bytes;
         } catch (JDWPException var3) {
            throw var3.toJDIException();
         }

         this.bytecodesRef = new SoftReference(var1);
      }

      return (byte[])var1.clone();
   }

   int argSlotCount() throws AbsentInformationException {
      if (this.argSlotCount == -1) {
         this.getVariables();
      }

      return this.argSlotCount;
   }

   private SoftLocationXRefs getLocations(SDE.Stratum var1) {
      if (var1.isJava()) {
         return this.getBaseLocations();
      } else {
         String var2 = var1.id();
         SoftLocationXRefs var3 = this.softOtherLocationXRefsRef == null ? null : (SoftLocationXRefs)this.softOtherLocationXRefsRef.get();
         if (var3 != null && var3.stratumID.equals(var2)) {
            return var3;
         } else {
            ArrayList var4 = new ArrayList();
            HashMap var5 = new HashMap();
            int var6 = -1;
            int var7 = -1;
            SDE.LineStratum var8 = null;
            SDE.Stratum var9 = this.declaringType.stratum("Java");
            Iterator var10 = this.getBaseLocations().lineLocations.iterator();

            while(true) {
               LocationImpl var11;
               SDE.LineStratum var13;
               int var14;
               do {
                  do {
                     do {
                        if (!var10.hasNext()) {
                           var3 = new SoftLocationXRefs(var2, var5, var4, var6, var7);
                           this.softOtherLocationXRefsRef = new SoftReference(var3);
                           return var3;
                        }

                        var11 = (LocationImpl)var10.next();
                        int var12 = var11.lineNumber(var9);
                        var13 = var1.lineStratum(this.declaringType, var12);
                     } while(var13 == null);

                     var14 = var13.lineNumber();
                  } while(var14 == -1);
               } while(var13.equals(var8));

               var8 = var13;
               if (var14 > var7) {
                  var7 = var14;
               }

               if (var14 < var6 || var6 == -1) {
                  var6 = var14;
               }

               var11.addStratumLineInfo(new StratumLineInfo(var2, var14, var13.sourceName(), var13.sourcePath()));
               var4.add(var11);
               Integer var15 = new Integer(var14);
               Object var16 = (List)var5.get(var15);
               if (var16 == null) {
                  var16 = new ArrayList(1);
                  var5.put(var15, var16);
               }

               ((List)var16).add(var11);
            }
         }
      }
   }

   private SoftLocationXRefs getBaseLocations() {
      SoftLocationXRefs var1 = this.softBaseLocationXRefsRef == null ? null : (SoftLocationXRefs)this.softBaseLocationXRefsRef.get();
      if (var1 != null) {
         return var1;
      } else {
         JDWP.Method.LineTable var2 = null;

         try {
            var2 = JDWP.Method.LineTable.process(this.vm, this.declaringType, this.ref);
         } catch (JDWPException var15) {
            throw var15.toJDIException();
         }

         int var3 = var2.lines.length;
         ArrayList var4 = new ArrayList(var3);
         HashMap var5 = new HashMap();
         int var6 = -1;
         int var7 = -1;

         for(int var8 = 0; var8 < var3; ++var8) {
            long var9 = var2.lines[var8].lineCodeIndex;
            int var11 = var2.lines[var8].lineNumber;
            if (var8 + 1 == var3 || var9 != var2.lines[var8 + 1].lineCodeIndex) {
               if (var11 > var7) {
                  var7 = var11;
               }

               if (var11 < var6 || var6 == -1) {
                  var6 = var11;
               }

               LocationImpl var12 = new LocationImpl(this.virtualMachine(), this, var9);
               var12.addBaseLineInfo(new BaseLineInfo(var11, this.declaringType));
               var4.add(var12);
               Integer var13 = new Integer(var11);
               Object var14 = (List)var5.get(var13);
               if (var14 == null) {
                  var14 = new ArrayList(1);
                  var5.put(var13, var14);
               }

               ((List)var14).add(var12);
            }
         }

         if (this.location == null) {
            this.firstIndex = var2.start;
            this.lastIndex = var2.end;
            if (var3 > 0) {
               this.location = (Location)var4.get(0);
            } else {
               this.location = new LocationImpl(this.virtualMachine(), this, this.firstIndex);
            }
         }

         var1 = new SoftLocationXRefs("Java", var5, var4, var6, var7);
         this.softBaseLocationXRefsRef = new SoftReference(var1);
         return var1;
      }
   }

   private List getVariables1_4() throws AbsentInformationException {
      JDWP.Method.VariableTable var1 = null;

      try {
         var1 = JDWP.Method.VariableTable.process(this.vm, this.declaringType, this.ref);
      } catch (JDWPException var9) {
         if (var9.errorCode() == 101) {
            this.absentVariableInformation = true;
            throw new AbsentInformationException();
         }

         throw var9.toJDIException();
      }

      this.argSlotCount = var1.argCnt;
      int var2 = var1.slots.length;
      ArrayList var3 = new ArrayList(var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         JDWP.Method.VariableTable.SlotInfo var5 = var1.slots[var4];
         if (!var5.name.startsWith("this$") && !var5.name.equals("this")) {
            LocationImpl var6 = new LocationImpl(this.virtualMachine(), this, var5.codeIndex);
            LocationImpl var7 = new LocationImpl(this.virtualMachine(), this, var5.codeIndex + (long)var5.length - 1L);
            LocalVariableImpl var8 = new LocalVariableImpl(this.virtualMachine(), this, var5.slot, var6, var7, var5.name, var5.signature, (String)null);
            var3.add(var8);
         }
      }

      return var3;
   }

   private List getVariables1() throws AbsentInformationException {
      if (!this.vm.canGet1_5LanguageFeatures()) {
         return this.getVariables1_4();
      } else {
         JDWP.Method.VariableTableWithGeneric var1 = null;

         try {
            var1 = JDWP.Method.VariableTableWithGeneric.process(this.vm, this.declaringType, this.ref);
         } catch (JDWPException var9) {
            if (var9.errorCode() == 101) {
               this.absentVariableInformation = true;
               throw new AbsentInformationException();
            }

            throw var9.toJDIException();
         }

         this.argSlotCount = var1.argCnt;
         int var2 = var1.slots.length;
         ArrayList var3 = new ArrayList(var2);

         for(int var4 = 0; var4 < var2; ++var4) {
            JDWP.Method.VariableTableWithGeneric.SlotInfo var5 = var1.slots[var4];
            if (!var5.name.startsWith("this$") && !var5.name.equals("this")) {
               LocationImpl var6 = new LocationImpl(this.virtualMachine(), this, var5.codeIndex);
               LocationImpl var7 = new LocationImpl(this.virtualMachine(), this, var5.codeIndex + (long)var5.length - 1L);
               LocalVariableImpl var8 = new LocalVariableImpl(this.virtualMachine(), this, var5.slot, var6, var7, var5.name, var5.signature, var5.genericSignature);
               var3.add(var8);
            }
         }

         return var3;
      }
   }

   private List getVariables() throws AbsentInformationException {
      if (this.absentVariableInformation) {
         throw new AbsentInformationException();
      } else {
         List var1 = this.variablesRef == null ? null : (List)this.variablesRef.get();
         if (var1 != null) {
            return var1;
         } else {
            var1 = this.getVariables1();
            var1 = Collections.unmodifiableList(var1);
            this.variablesRef = new SoftReference(var1);
            return var1;
         }
      }
   }

   private static class SoftLocationXRefs {
      final String stratumID;
      final Map lineMapper;
      final List lineLocations;
      final int lowestLine;
      final int highestLine;

      SoftLocationXRefs(String var1, Map var2, List var3, int var4, int var5) {
         this.stratumID = var1;
         this.lineMapper = Collections.unmodifiableMap(var2);
         this.lineLocations = Collections.unmodifiableList(var3);
         this.lowestLine = var4;
         this.highestLine = var5;
      }
   }
}
