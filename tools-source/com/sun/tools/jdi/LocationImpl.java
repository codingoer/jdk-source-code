package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

public class LocationImpl extends MirrorImpl implements Location {
   private final ReferenceTypeImpl declaringType;
   private Method method;
   private long methodRef;
   private long codeIndex;
   private LineInfo baseLineInfo = null;
   private LineInfo otherLineInfo = null;

   LocationImpl(VirtualMachine var1, Method var2, long var3) {
      super(var1);
      this.method = var2;
      this.codeIndex = var2.isNative() ? -1L : var3;
      this.declaringType = (ReferenceTypeImpl)var2.declaringType();
   }

   LocationImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, long var5) {
      super(var1);
      this.method = null;
      this.codeIndex = var5;
      this.declaringType = var2;
      this.methodRef = var3;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Location) {
         Location var2 = (Location)var1;
         return this.method().equals(var2.method()) && this.codeIndex() == var2.codeIndex() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.method().hashCode() + (int)this.codeIndex();
   }

   public int compareTo(Location var1) {
      LocationImpl var2 = (LocationImpl)var1;
      int var3 = this.method().compareTo(var2.method());
      if (var3 == 0) {
         long var4 = this.codeIndex() - var2.codeIndex();
         if (var4 < 0L) {
            return -1;
         } else {
            return var4 > 0L ? 1 : 0;
         }
      } else {
         return var3;
      }
   }

   public ReferenceType declaringType() {
      return this.declaringType;
   }

   public Method method() {
      if (this.method == null) {
         this.method = this.declaringType.getMethodMirror(this.methodRef);
         if (this.method.isNative()) {
            this.codeIndex = -1L;
         }
      }

      return this.method;
   }

   public long codeIndex() {
      this.method();
      return this.codeIndex;
   }

   LineInfo getBaseLineInfo(SDE.Stratum var1) {
      if (this.baseLineInfo != null) {
         return this.baseLineInfo;
      } else {
         MethodImpl var3 = (MethodImpl)this.method();
         LineInfo var2 = var3.codeIndexToLineInfo(var1, this.codeIndex());
         this.addBaseLineInfo(var2);
         return var2;
      }
   }

   LineInfo getLineInfo(SDE.Stratum var1) {
      if (var1.isJava()) {
         return this.getBaseLineInfo(var1);
      } else {
         LineInfo var2 = this.otherLineInfo;
         if (var2 != null && var1.id().equals(var2.liStratum())) {
            return var2;
         } else {
            int var3 = this.lineNumber("Java");
            SDE.LineStratum var4 = var1.lineStratum(this.declaringType, var3);
            Object var6;
            if (var4 != null && var4.lineNumber() != -1) {
               var6 = new StratumLineInfo(var1.id(), var4.lineNumber(), var4.sourceName(), var4.sourcePath());
            } else {
               MethodImpl var5 = (MethodImpl)this.method();
               var6 = var5.codeIndexToLineInfo(var1, this.codeIndex());
            }

            this.addStratumLineInfo((LineInfo)var6);
            return (LineInfo)var6;
         }
      }
   }

   void addStratumLineInfo(LineInfo var1) {
      this.otherLineInfo = var1;
   }

   void addBaseLineInfo(LineInfo var1) {
      this.baseLineInfo = var1;
   }

   public String sourceName() throws AbsentInformationException {
      return this.sourceName(this.vm.getDefaultStratum());
   }

   public String sourceName(String var1) throws AbsentInformationException {
      return this.sourceName(this.declaringType.stratum(var1));
   }

   String sourceName(SDE.Stratum var1) throws AbsentInformationException {
      return this.getLineInfo(var1).liSourceName();
   }

   public String sourcePath() throws AbsentInformationException {
      return this.sourcePath(this.vm.getDefaultStratum());
   }

   public String sourcePath(String var1) throws AbsentInformationException {
      return this.sourcePath(this.declaringType.stratum(var1));
   }

   String sourcePath(SDE.Stratum var1) throws AbsentInformationException {
      return this.getLineInfo(var1).liSourcePath();
   }

   public int lineNumber() {
      return this.lineNumber(this.vm.getDefaultStratum());
   }

   public int lineNumber(String var1) {
      return this.lineNumber(this.declaringType.stratum(var1));
   }

   int lineNumber(SDE.Stratum var1) {
      return this.getLineInfo(var1).liLineNumber();
   }

   public String toString() {
      return this.lineNumber() == -1 ? this.method().toString() + "+" + this.codeIndex() : this.declaringType().name() + ":" + this.lineNumber();
   }
}
