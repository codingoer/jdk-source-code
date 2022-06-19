package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Location;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import java.util.ArrayList;
import java.util.List;

public class ObsoleteMethodImpl extends NonConcreteMethodImpl {
   private Location location = null;

   ObsoleteMethodImpl(VirtualMachine var1, ReferenceTypeImpl var2) {
      super(var1, var2, 0L, "<obsolete>", "", (String)null, 0);
   }

   public boolean isObsolete() {
      return true;
   }

   public String returnTypeName() {
      return "<unknown>";
   }

   public Type returnType() throws ClassNotLoadedException {
      throw new ClassNotLoadedException("type unknown");
   }

   public List argumentTypeNames() {
      return new ArrayList();
   }

   public List argumentSignatures() {
      return new ArrayList();
   }

   Type argumentType(int var1) throws ClassNotLoadedException {
      throw new ClassNotLoadedException("type unknown");
   }

   public List argumentTypes() throws ClassNotLoadedException {
      return new ArrayList();
   }
}
