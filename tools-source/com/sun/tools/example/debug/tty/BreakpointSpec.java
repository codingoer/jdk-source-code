package com.sun.tools.example.debug.tty;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class BreakpointSpec extends EventRequestSpec {
   String methodId;
   List methodArgs;
   int lineNumber;

   BreakpointSpec(ReferenceTypeSpec var1, int var2) {
      super(var1);
      this.methodId = null;
      this.methodArgs = null;
      this.lineNumber = var2;
   }

   BreakpointSpec(ReferenceTypeSpec var1, String var2, List var3) throws MalformedMemberNameException {
      super(var1);
      this.methodId = var2;
      this.methodArgs = var3;
      this.lineNumber = 0;
      if (!this.isValidMethodName(var2)) {
         throw new MalformedMemberNameException(var2);
      }
   }

   EventRequest resolveEventRequest(ReferenceType var1) throws AmbiguousMethodException, AbsentInformationException, InvalidTypeException, NoSuchMethodException, LineNotFoundException {
      Location var2 = this.location(var1);
      if (var2 == null) {
         throw new InvalidTypeException();
      } else {
         EventRequestManager var3 = var1.virtualMachine().eventRequestManager();
         BreakpointRequest var4 = var3.createBreakpointRequest(var2);
         var4.setSuspendPolicy(this.suspendPolicy);
         var4.enable();
         return var4;
      }
   }

   String methodName() {
      return this.methodId;
   }

   int lineNumber() {
      return this.lineNumber;
   }

   List methodArgs() {
      return this.methodArgs;
   }

   boolean isMethodBreakpoint() {
      return this.methodId != null;
   }

   public int hashCode() {
      return this.refSpec.hashCode() + this.lineNumber + (this.methodId != null ? this.methodId.hashCode() : 0) + (this.methodArgs != null ? this.methodArgs.hashCode() : 0);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BreakpointSpec)) {
         return false;
      } else {
         boolean var10000;
         label37: {
            BreakpointSpec var2 = (BreakpointSpec)var1;
            if (this.methodId != null) {
               if (!this.methodId.equals(var2.methodId)) {
                  break label37;
               }
            } else if (this.methodId != var2.methodId) {
               break label37;
            }

            if (this.methodArgs != null) {
               if (!this.methodArgs.equals(var2.methodArgs)) {
                  break label37;
               }
            } else if (this.methodArgs != var2.methodArgs) {
               break label37;
            }

            if (this.refSpec.equals(var2.refSpec) && this.lineNumber == var2.lineNumber) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   String errorMessageFor(Exception var1) {
      if (var1 instanceof AmbiguousMethodException) {
         return MessageOutput.format("Method is overloaded; specify arguments", this.methodName());
      } else if (var1 instanceof NoSuchMethodException) {
         return MessageOutput.format("No method in", new Object[]{this.methodName(), this.refSpec.toString()});
      } else if (var1 instanceof AbsentInformationException) {
         return MessageOutput.format("No linenumber information for", this.refSpec.toString());
      } else if (var1 instanceof LineNotFoundException) {
         return MessageOutput.format("No code at line", new Object[]{new Long((long)this.lineNumber()), this.refSpec.toString()});
      } else {
         return var1 instanceof InvalidTypeException ? MessageOutput.format("Breakpoints can be located only in classes.", this.refSpec.toString()) : super.errorMessageFor(var1);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(this.refSpec.toString());
      if (this.isMethodBreakpoint()) {
         var1.append('.');
         var1.append(this.methodId);
         if (this.methodArgs != null) {
            boolean var2 = true;
            var1.append('(');

            for(Iterator var3 = this.methodArgs.iterator(); var3.hasNext(); var2 = false) {
               String var4 = (String)var3.next();
               if (!var2) {
                  var1.append(',');
               }

               var1.append(var4);
            }

            var1.append(")");
         }
      } else {
         var1.append(':');
         var1.append(this.lineNumber);
      }

      return MessageOutput.format("breakpoint", var1.toString());
   }

   private Location location(ReferenceType var1) throws AmbiguousMethodException, AbsentInformationException, NoSuchMethodException, LineNotFoundException {
      Location var2 = null;
      if (this.isMethodBreakpoint()) {
         Method var3 = this.findMatchingMethod(var1);
         var2 = var3.location();
      } else {
         List var4 = var1.locationsOfLine(this.lineNumber());
         if (var4.size() == 0) {
            throw new LineNotFoundException();
         }

         var2 = (Location)var4.get(0);
         if (var2.method() == null) {
            throw new LineNotFoundException();
         }
      }

      return var2;
   }

   private boolean isValidMethodName(String var1) {
      return this.isJavaIdentifier(var1) || var1.equals("<init>") || var1.equals("<clinit>");
   }

   private boolean compareArgTypes(Method var1, List var2) {
      List var3 = var1.argumentTypeNames();
      if (var3.size() != var2.size()) {
         return false;
      } else {
         int var4 = var3.size();

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = (String)var3.get(var5);
            String var7 = (String)var2.get(var5);
            if (!var6.equals(var7)) {
               if (var5 == var4 - 1 && var1.isVarArgs() && var7.endsWith("...")) {
                  int var8 = var6.length();
                  if (var8 + 1 != var7.length()) {
                     return false;
                  }

                  if (!var6.regionMatches(0, var7, 0, var8 - 2)) {
                     return false;
                  }

                  return true;
               }

               return false;
            }
         }

         return true;
      }
   }

   private String normalizeArgTypeName(String var1) {
      int var2 = 0;
      StringBuffer var3 = new StringBuffer();
      StringBuffer var4 = new StringBuffer();
      var1 = var1.trim();
      int var5 = var1.length();
      boolean var6 = var1.endsWith("...");
      if (var6) {
         var5 -= 3;
      }

      char var7;
      while(var2 < var5) {
         var7 = var1.charAt(var2);
         if (Character.isWhitespace(var7) || var7 == '[') {
            break;
         }

         var3.append(var7);
         ++var2;
      }

      for(; var2 < var5; ++var2) {
         var7 = var1.charAt(var2);
         if (var7 != '[' && var7 != ']') {
            if (!Character.isWhitespace(var7)) {
               throw new IllegalArgumentException(MessageOutput.format("Invalid argument type name"));
            }
         } else {
            var4.append(var7);
         }
      }

      var1 = var3.toString();
      if (var1.indexOf(46) == -1 || var1.startsWith("*.")) {
         try {
            ReferenceType var9 = Env.getReferenceTypeFromToken(var1);
            if (var9 != null) {
               var1 = var9.name();
            }
         } catch (IllegalArgumentException var8) {
         }
      }

      var1 = var1 + var4.toString();
      if (var6) {
         var1 = var1 + "...";
      }

      return var1;
   }

   private Method findMatchingMethod(ReferenceType var1) throws AmbiguousMethodException, NoSuchMethodException {
      ArrayList var2 = null;
      if (this.methodArgs() != null) {
         var2 = new ArrayList(this.methodArgs().size());
         Iterator var3 = this.methodArgs().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var4 = this.normalizeArgTypeName(var4);
            var2.add(var4);
         }
      }

      Method var8 = null;
      Method var9 = null;
      int var5 = 0;
      Iterator var6 = var1.methods().iterator();

      while(var6.hasNext()) {
         Method var7 = (Method)var6.next();
         if (var7.name().equals(this.methodName())) {
            ++var5;
            if (var5 == 1) {
               var8 = var7;
            }

            if (var2 != null && this.compareArgTypes(var7, var2)) {
               var9 = var7;
               break;
            }
         }
      }

      var6 = null;
      Method var10;
      if (var9 != null) {
         var10 = var9;
      } else {
         if (var2 != null || var5 <= 0) {
            throw new NoSuchMethodException(this.methodName());
         }

         if (var5 != 1) {
            throw new AmbiguousMethodException();
         }

         var10 = var8;
      }

      return var10;
   }
}
